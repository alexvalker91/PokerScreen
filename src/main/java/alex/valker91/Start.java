package alex.valker91;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Start {

    static {
        // 1. Загружаем библиотеку OpenCV
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }

//    private static final String REF_RANKS_PATH = "src/main/resources/hero_cards_numbers/";
//    private static final String REF_SUITS_PATH = "src/main/resources/hero_cards_suits/";
//    private static Map<String, Mat> refRanks = new HashMap<>();
//    private static Map<String, Mat> refSuits = new HashMap<>();
//
//    public static void main(String[] args) {
//        // Загрузка эталонных изображений
//        loadReferences(REF_RANKS_PATH, refRanks);
//        loadReferences(REF_SUITS_PATH, refSuits);
//
//        File screenshotFile = new File("src/main/resources/screenshot/screenshot3.png");
//        // Обработка скриншота
//        Mat image = Imgcodecs.imread(screenshotFile.getAbsolutePath());
//        processImage(image);
//    }

    private static final String NUMBERS_PATH = "src/main/resources/hero_cards_numbers/";
    private static final String SUITS_PATH = "src/main/resources/hero_cards_suits/";
    private static final Map<String, Mat> numberTemplates = new HashMap<>();
    private static final Map<String, Mat> suitTemplates = new HashMap<>();

    // Область карт игрока [x, y, width, height]
    private static final Rect HERO_CARDS_ROI = new Rect(862, 648, 200, 75);

    public static void main(String[] args) {
        // Загрузка шаблонов
        loadTemplates(NUMBERS_PATH, numberTemplates);
        loadTemplates(SUITS_PATH, suitTemplates);

        File screenshotFile = new File("src/main/resources/screenshot/screenshot3.png");
//        // Обработка скриншота
        Mat tableImage = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        // Вырезаем область с картами игрока
        Mat heroCardsArea = tableImage.submat(HERO_CARDS_ROI);

        // Распознаем карты
        List<String> heroCards = processHeroCards(heroCardsArea);

        // Вывод результата
        System.out.println("Карты игрока: " + String.join(" | ", heroCards));
    }

    private static void loadTemplates(String path, Map<String, Mat> templates) {
        try {
            File dir = new File(path);
            for (File file : dir.listFiles()) {
                Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
                Imgproc.cvtColor(template, template, Imgproc.COLOR_BGR2GRAY);
                templates.put(file.getName().replace(".png", ""), template);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки шаблонов: " + e.getMessage());
        }
    }

    private static List<String> processHeroCards(Mat heroCardsArea) {
        List<String> result = new ArrayList<>();

        // 1. Предобработка
        Mat gray = new Mat();
        Imgproc.cvtColor(heroCardsArea, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, gray, 130, 255, Imgproc.THRESH_BINARY_INV);

        // 2. Поиск контуров
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // 3. Фильтрация и сортировка
        contours.sort(Comparator.comparingDouble(c ->
                Imgproc.boundingRect(c).x));

        // 4. Обработка карт с проверкой границ
        for (int i = 0; i < Math.min(2, contours.size()); i++) {
            Rect cardRect = Imgproc.boundingRect(contours.get(i));

            // Корректируем координаты с учетом границ изображения
            int pad = 2;
            int x = Math.max(0, cardRect.x - pad);
            int y = Math.max(0, cardRect.y - pad);
            int width = Math.min(cardRect.width + 2*pad, heroCardsArea.cols() - x);
            int height = Math.min(cardRect.height + 2*pad, heroCardsArea.rows() - y);

            if (width <= 0 || height <= 0) continue;

            try {
                Mat card = heroCardsArea.submat(new Rect(x, y, width, height));
                result.add(recognizeCard(card));
            } catch (Exception e) {
                System.err.println("Ошибка вырезания карты: " + e.getMessage());
            }
        }

        return result;
    }

    private static String recognizeCard(Mat card) {
        // Размеры ваших шаблонов
        int numberWidth = 21; // Ширина шаблона числа
        int numberHeight = 22; // Высота шаблона числа
        int suitWidth = 15;
        int suitHeight = 16;

        // Автоматическое определение областей
        int cardWidth = card.cols();
        int cardHeight = card.rows();

        // Значение (верхний левый угол)
        Rect numberROI = new Rect(
                (int)(cardWidth * 0.15),
                (int)(cardHeight * 0.1),
                Math.min(numberWidth, cardWidth - (int)(cardWidth * 0.15)),
                Math.min(numberHeight, cardHeight - (int)(cardHeight * 0.1))
        );

        // Масть (нижний левый угол)
        Rect suitROI = new Rect(
                (int)(cardWidth * 0.15),
                (int)(cardHeight * 0.7),
                Math.min(suitWidth, cardWidth - (int)(cardWidth * 0.15)),
                Math.min(suitHeight, cardHeight - (int)(cardHeight * 0.7))
        );

        // Проверка и коррекция ROI
        numberROI = validateROI(card, numberROI);
        suitROI = validateROI(card, suitROI);

        return matchTemplate(card.submat(numberROI), numberTemplates) +
                matchTemplate(card.submat(suitROI), suitTemplates);
    }

    private static Rect validateROI(Mat image, Rect roi) {
        int x = Math.max(0, roi.x);
        int y = Math.max(0, roi.y);
        int width = Math.min(roi.width, image.cols() - x);
        int height = Math.min(roi.height, image.rows() - y);
        return new Rect(x, y, width, height);
    }

    private static String matchTemplate(Mat query, Map<String, Mat> templates) {
        double maxVal = -1;
        String bestMatch = "?";

        Mat queryResized = new Mat();
        Imgproc.resize(query, queryResized, new Size(30, 33)); // Подгон под размер шаблонов

        for (Map.Entry<String, Mat> entry : templates.entrySet()) {
            Mat result = new Mat();
            Imgproc.matchTemplate(queryResized, entry.getValue(), result, Imgproc.TM_CCOEFF_NORMED);

            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            if (mmr.maxVal > maxVal) {
                maxVal = mmr.maxVal;
                bestMatch = entry.getKey();
            }
        }
        return (maxVal > 0.7) ? bestMatch : "?";
    }

//    private static void loadTemplates(String path, Map<String, Mat> templates) {
//        File dir = new File(path);
//        for (File file : Objects.requireNonNull(dir.listFiles())) {
//            Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
//            String name = file.getName().replace(".png", "");
//            templates.put(name, template);
//        }
//    }

    private static List<String> processImage(Mat image) {
        List<String> result = new ArrayList<>();

        // Предобработка изображения
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(gray, gray, 150, 255, Imgproc.THRESH_BINARY);

        // Поиск контуров
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Обработка найденных контуров
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            // Фильтр по размеру (настройте под ваш случай)
            if (rect.width < 70 || rect.height < 90) continue;

            // Вырезаем карту
            Mat card = new Mat(image, rect);

            // Распознаем карту
            String cardInfo = recognizeCard(card);
            if (cardInfo != null) result.add(cardInfo);
        }
        return result;
    }

//    private static String recognizeCard(Mat card) {
//        try {
//            // Вычисляем относительные координаты для ROI
//            int w = card.cols();
//            int h = card.rows();
//
//            // Область значения (верхний левый угол)
//            Rect numberROI = new Rect(
//                    (int)(w * 0.1),
//                    (int)(h * 0.1),
//                    (int)(w * 0.3),
//                    (int)(h * 0.3)
//            );
//
//            // Область масти (нижний левый угол)
//            Rect suitROI = new Rect(
//                    (int)(w * 0.1),
//                    (int)(h * 0.6),
//                    (int)(w * 0.3),
//                    (int)(h * 0.3)
//            );
//
//            // Распознавание компонентов
//            String number = matchComponent(card.submat(numberROI), numberTemplates);
//            String suit = matchComponent(card.submat(suitROI), suitTemplates);
//
//            return number + " " + suit;
//        } catch (Exception e) {
//            System.err.println("Ошибка обработки карты: " + e.getMessage());
//            return null;
//        }
//    }

    private static String matchComponent(Mat region, Map<String, Mat> templates) {
        double minScore = Double.MAX_VALUE;
        String bestMatch = "UNKNOWN";

        for (Map.Entry<String, Mat> entry : templates.entrySet()) {
            Mat result = new Mat();
            Imgproc.matchTemplate(region, entry.getValue(), result, Imgproc.TM_SQDIFF_NORMED);

            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            if (mmr.minVal < minScore) {
                minScore = mmr.minVal;
                bestMatch = entry.getKey();
            }
        }
        return bestMatch;
    }

//    public static void main(String[] args) {
//
//        File screenshotFile = new File("src/main/resources/screenshot/screenshot3.png");
//        // 2. Загрузка изображения в OpenCV
//        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());
//
//        // 3. Ручное выделение ROI (замените координаты!)
//        Rect heroCardsRect = new Rect(862, 648, 200, 75);
//        Mat roi = new Mat(img, heroCardsRect);
//
//        // Сохранение изображения с визуализацией
//        Imgproc.rectangle(img, heroCardsRect.tl(), heroCardsRect.br(), new Scalar(255, 0, 0), 3);
//        Imgcodecs.imwrite("annotated_screenshot.png", img);
//
//        // 4. Конвертация в градации серого
//        Mat gray = new Mat();
//        Imgproc.cvtColor(roi, gray, Imgproc.COLOR_BGR2GRAY);
//
//        // 5. Бинаризация
//        Mat thresh = new Mat();
//        Imgproc.threshold(gray, thresh, 120, 255, Imgproc.THRESH_BINARY_INV);
//
//        // 6. Поиск контуров
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(
//                thresh,
//                contours,
//                hierarchy,
//                Imgproc.RETR_EXTERNAL,
//                Imgproc.CHAIN_APPROX_SIMPLE
//        );
//
//        // 7. Фильтрация контуров по площади
//        List<MatOfPoint> cardContours = new ArrayList<>();
//        double minArea = 500; // Настройте под ваше изображение
//        for (MatOfPoint contour : contours) {
//            double area = Imgproc.contourArea(contour);
//            if (area > minArea) {
//                cardContours.add(contour);
//            }
//        }
//
//        // 8. Отрисовка контуров
//        Mat result = roi.clone();
//        Imgproc.drawContours(
//                result,
//                cardContours,
//                -1,
//                new Scalar(0, 255, 0), // Зеленый цвет
//                2 // Толщина линии
//        );
//
//        // 9. Сохранение результатов
//        Imgcodecs.imwrite("roi.png", roi);
//        Imgcodecs.imwrite("thresh.png", thresh);
//        Imgcodecs.imwrite("result.png", result);
//
//        System.out.println("Готово! Проверьте файлы roi.png, thresh.png и result.png");
//    }
}

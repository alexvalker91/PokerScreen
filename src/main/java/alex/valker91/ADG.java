package alex.valker91;

import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ADG {
    private static Map<String, Mat> cardTemplates = new HashMap<>();
    private static final String TEMPLATES_PATH = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\card_numbers";
    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
        loadCardTemplates();
    }

//    private static void loadCardTemplates() {
//        File templatesDir = new File(TEMPLATES_PATH);
//        for (File file : templatesDir.listFiles()) {
//            if (file.getName().endsWith(".png")) {
//                Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
//                cardTemplates.put(file.getName().replace(".png", ""), template);
//            }
//        }
//    }

    private static void loadCardTemplates() {
        File templatesDir = new File(TEMPLATES_PATH);

        // Проверка существования директории
        if (!templatesDir.exists() || !templatesDir.isDirectory()) {
            System.err.println("Директория шаблонов не найдена: " + TEMPLATES_PATH);
            return;
        }

        File[] files = templatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        // Проверка наличия PNG-файлов
        if (files == null || files.length == 0) {
            System.err.println("В директории нет PNG-файлов: " + TEMPLATES_PATH);
            return;
        }

        for (File file : files) {
            // Загрузка в градациях серого
            Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

            if (template.empty()) {
                System.err.println("Не удалось загрузить шаблон: " + file.getName());
                continue;
            }

            // Принудительная конвертация в CV_8U (если нужно)
            if (template.depth() != CvType.CV_8U) {
                Mat converted = new Mat();
                template.convertTo(converted, CvType.CV_8U);
                template = converted;
            }

            // Убеждаемся, что это одноканальное изображение
            if (template.channels() != 1) {
                Mat gray = new Mat();
                Imgproc.cvtColor(template, gray, Imgproc.COLOR_BGR2GRAY);
                template = gray;
            }

            String templateName = file.getName().replace(".png", "");
            cardTemplates.put(templateName, template);

            // Отладочный вывод
            System.out.printf(
                    "Загружен шаблон: %-15s | Каналы: %d | Тип: %s | Размер: %dx%d\n",
                    templateName,
                    template.channels(),
                    CvType.typeToString(template.type()),
                    template.cols(),
                    template.rows()
            );
        }
    }

//    private static String recognizeSingleCard(Mat cardImage) {
//        String bestMatch = "";
//        double bestMatchValue = Double.MIN_VALUE; // Для TM_CCOEFF_NORMED лучшее значение ближе к 1
//
//        // Убедимся, что входное изображение карты бинарное
//        cardImage = preprocessImage(cardImage);
//
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            Mat template = entry.getValue();
//
//            // Убедимся, что шаблон бинарный
//            template = preprocessImage(template);
//
//            // Проверяем размеры
//            if (cardImage.rows() < template.rows() || cardImage.cols() < template.cols()) {
//                System.err.println("Template is larger than the input image! Skipping...");
//                continue;
//            }
//
//            // Нормализация размеров
//            Mat resizedTemplate = new Mat();
//            Imgproc.resize(template, resizedTemplate, new Size(cardImage.cols(), cardImage.rows()));
//
//            // Сравнение с шаблоном
//            Mat result = new Mat();
//            Imgproc.matchTemplate(cardImage, resizedTemplate, result, Imgproc.TM_CCOEFF_NORMED);
//
//            // Извлечение максимального значения совпадения
//            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//            double matchValue = mmr.maxVal; // Для TM_CCOEFF_NORMED используем maxVal
//
//            System.out.println("Сравнение с шаблоном: " + entry.getKey() + " - Значение совпадения: " + matchValue);
//
//            if (matchValue > bestMatchValue) {
//                bestMatchValue = matchValue;
//                bestMatch = entry.getKey();
//            }
//
//            result.release();
//        }
//
//        System.out.println("Лучшее совпадение: " + bestMatch + " с значением: " + bestMatchValue);
//        return (bestMatchValue > 0.8) ? bestMatch : ""; // Установите порог для TM_CCOEFF_NORMED
//    }


//    private static Mat preprocessImage(Mat img) {
//        Mat grayImg = new Mat();
//
//        // Проверяем количество каналов
//        if (img.channels() > 1) {
//            // Если изображение цветное, преобразуем в оттенки серого
//            Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
//        } else {
//            // Если изображение уже в оттенках серого, используем его как есть
//            grayImg = img.clone();
//        }
//
//        // Преобразование в бинарное изображение
//        Mat binaryImg = new Mat();
//        Imgproc.threshold(grayImg, binaryImg, 200, 255, Imgproc.THRESH_BINARY);
//
//        return binaryImg;
//    }

    public static void main(String[] args) throws Exception {
        File screenshotFile = new File("src/main/resources/screenshot/screenshot11.png");
//        // Обработка скриншота
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        Rect card1 = new Rect(862, 648, 100, 56);
        Imgproc.rectangle(img, card1.tl(), card1.br(), new Scalar(255, 0, 0), 3);

        Rect card2 = new Rect(962, 648, 100, 56);
        Imgproc.rectangle(img, card2.tl(), card2.br(), new Scalar(255, 0, 0), 3);

        Mat roiImagecard1 = img.submat(card1);
        Mat binaryImagecard1 = thresholding(roiImagecard1, 200, 67);

        Mat roiImagecard2 = img.submat(card2);
        Mat binaryImagecard2 = thresholding(roiImagecard2, 200, 67);

        // Сохранение изображения с визуализацией
        Imgcodecs.imwrite("K.png", binaryImagecard1);
        Imgcodecs.imwrite("6.png", binaryImagecard2);

//        String myCard1 = recognizeSingleCard(binaryImagecard1);
//        String myCard2 = recognizeSingleCard(binaryImagecard2);
//        System.out.println(myCard1);
//        System.out.println(myCard2);

        String recognizedCard1 = matchCard(binaryImagecard1);
        String recognizedCard2 = matchCard(binaryImagecard2);

        System.out.println("Распознана карта 1: " + recognizedCard1);
        System.out.println("Распознана карта 2: " + recognizedCard2);
    }

    public static Mat thresholding(Mat img, int value1, int value2) {
        // Convert to grayscale
        Mat grayImg = new Mat();
        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);

        // Apply thresholding
        Mat binaryImg = new Mat();
        Imgproc.threshold(grayImg, binaryImg, value1, value2, Imgproc.THRESH_BINARY);

        return binaryImg;
    }

    public static String matchCard(Mat cardImage) {
        if (cardImage.empty()) {
            System.err.println("Передано пустое изображение!");
            return null;
        }

        // Принудительно конвертируем cardImage в одноканальное изображение (градации серого)
        if (cardImage.channels() != 1) {
            Mat gray = new Mat();
            Imgproc.cvtColor(cardImage, gray, Imgproc.COLOR_BGR2GRAY);
            cardImage = gray;
        }

        // Проверка типа данных (должен быть CV_8U)
        if (cardImage.depth() != CvType.CV_8U) {
            Mat converted = new Mat();
            cardImage.convertTo(converted, CvType.CV_8U);
            cardImage = converted;
        }

        double threshold = 0.8;
        String bestMatch = null;
        double maxVal = 0;

        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
            Mat template = entry.getValue();

            // Проверка каналов шаблона (должен быть 1 канал)
            if (template.channels() != 1) {
                System.err.println("Шаблон " + entry.getKey() + " не в градациях серого!");
                continue;
            }

            // Проверка типа данных шаблона (должен быть CV_8U)
            if (template.depth() != CvType.CV_8U) {
                Mat convertedTemplate = new Mat();
                template.convertTo(convertedTemplate, CvType.CV_8U);
                template = convertedTemplate;
            }

            // Проверка размеров
            if (cardImage.rows() < template.rows() || cardImage.cols() < template.cols()) {
                System.err.println("Шаблон " + entry.getKey() + " больше изображения");
                continue;
            }

            try {
                Mat result = new Mat();
                Imgproc.matchTemplate(cardImage, template, result, Imgproc.TM_CCOEFF_NORMED);

                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

                double matchPercent = mmr.maxVal * 100; // Конвертируем в проценты
                // Выводим результат для каждого шаблона
                System.out.printf("Шаблон: %-10s | Совпадение: %.2f%%\n", entry.getKey(), matchPercent);

                if (mmr.maxVal > maxVal && mmr.maxVal >= threshold) {
                    maxVal = mmr.maxVal;
                    bestMatch = entry.getKey();
                }
            } catch (Exception e) {
                System.err.println("Ошибка сравнения с шаблоном " + entry.getKey() + ": " + e.getMessage());
            }
        }
        return bestMatch;
    }

//    private static MatOfPoint extractContour(Mat binaryImage) {
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//
//        // Поиск контуров
//        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Выбираем самый крупный контур (например, карту)
//        double maxArea = 0;
//        MatOfPoint largestContour = null;
//
//        for (MatOfPoint contour : contours) {
//            double area = Imgproc.contourArea(contour);
//            if (area > maxArea) {
//                maxArea = area;
//                largestContour = contour;
//            }
//        }
//
//        return largestContour;
//    }

    private static Mat preprocessImage(Mat img) {
        Mat grayImg = new Mat();

        if (img.channels() > 1) {
            Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
        } else {
            grayImg = img.clone();
        }

        Mat binaryImg = new Mat();
        Imgproc.threshold(grayImg, binaryImg, 128, 255, Imgproc.THRESH_BINARY); // Попробуйте другие значения порога

        return binaryImg;
    }

    private static MatOfPoint extractContour(Mat binaryImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.isEmpty()) {
            System.err.println("Контуры не найдены!");
            return null;
        }

        System.out.println("Найдено контуров: " + contours.size());

        double maxArea = 0;
        MatOfPoint largestContour = null;

        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            System.out.println("Площадь контура: " + area);

            if (area > 10) { // Уменьшите минимальную площадь, если контуры слишком малы
                if (area > maxArea) {
                    maxArea = area;
                    largestContour = contour;
                }
            }
        }

        if (largestContour == null) {
            System.err.println("Самый крупный контур не найден!");
        }

        return largestContour;
    }

    private static String recognizeCardByContours(Mat cardImage) {
        String bestMatch = "";
        double bestMatchValue = Double.MAX_VALUE; // Для matchShapes лучшее значение ближе к 0

        Mat binaryCardImage = cardImage.clone(); // Если изображение уже чёрно-белое, используем его напрямую

        MatOfPoint cardContour = extractContour(binaryCardImage);
        if (cardContour == null) {
            System.err.println("Контур карты не найден!");
            return "";
        }

        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
            String templateName = entry.getKey();
            Mat templateImage = preprocessImage(entry.getValue());

            MatOfPoint templateContour = extractContour(templateImage);
            if (templateContour == null) {
                System.err.println("Контур шаблона " + templateName + " не найден!");
                continue;
            }

            double matchValue = Imgproc.matchShapes(cardContour, templateContour, Imgproc.CONTOURS_MATCH_I1, 0);

            System.out.println("Сравнение с шаблоном: " + templateName + " - Значение совпадения: " + matchValue);

            if (matchValue < bestMatchValue) {
                bestMatchValue = matchValue;
                bestMatch = templateName;
            }
        }

        if (bestMatchValue == Double.MAX_VALUE) {
            System.err.println("Не удалось найти совпадение!");
            return "";
        }

        System.out.println("Лучшее совпадение: " + bestMatch + " с значением: " + bestMatchValue);
        return bestMatch;
    }


}

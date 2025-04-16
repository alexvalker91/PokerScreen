package alex.valker91;

import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static Map<String, Mat> cardTemplates = new HashMap<>();
    private static final String TEMPLATES_PATH = "C:\\\\Users\\\\Aliaksandr_Kreyer\\\\Desktop\\\\my\\\\PokerScreen\\\\src\\\\main\\\\resources\\\\cards";
    private static final double MATCH_THRESHOLD = 0.91;
    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
        loadCardTemplates();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> pokerData = scanPokerTable();
        System.out.println(pokerData);
    }

    public static Map<String, Object> scanPokerTable() throws Exception {
        Map<String, Object> data = new HashMap<>();

        // 1. Сделать скриншот
//        BufferedImage screenshot = new Robot().createScreenCapture(
//                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
//        );
        File screenshotFile = new File("screenshot_3_cards.png");
//        ImageIO.write(screenshot, "png", screenshotFile);

        // 2. Загрузка изображения в OpenCV
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        // 3. Области интереса (настройте координаты)
//        Rect myCardsROI = new Rect(860, 645, 200, 70);  // x, y, width, height
        Rect communityCardsROI = new Rect(710, 350, 500, 105);
        Rect potROI = new Rect(870, 325, 190, 25);

        // Сохранение вырезанного кусочка
        String outputPath = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\PokerScreen\\src\\main\\resources\\saved.png";
        saveTemplateFromROI(img, communityCardsROI, outputPath);
//
////        Imgproc.rectangle(img, myCardsROI.tl(), myCardsROI.br(), new Scalar(0, 0, 255), 3);
//        Imgproc.rectangle(img, communityCardsROI.tl(), communityCardsROI.br(), new Scalar(0, 255, 0), 3);
//        Imgproc.rectangle(img, potROI.tl(), potROI.br(), new Scalar(255, 0, 0), 3);
//
//        // Сохранение изображения с визуализацией
//        Imgcodecs.imwrite("annotated_screenshot.png", img);
//
//        // 4. Распознавание карт (шаблонный метод)
////        data.put("my_cards", recognizeCards(img.submat(myCardsROI)));
//
//        Mat communityCardsImage = img.submat(communityCardsROI);
//        int cardWidth = 32; // Пример ширины одной карты
//        int cardHeight = 135; // Пример высоты одной карты
//        List<String> communityCards = recognizeMultipleCards(communityCardsImage, cardWidth, cardHeight, 20);
//        System.out.println("communityCards.size(): " + communityCards.size());
//        data.put("community_cards", recognizeCards(img.submat(communityCardsROI)));

        // 5. Распознавание текста
        data.put("pot", recognizeText(img.submat(potROI)));



        // 4. Распознать карты
        List<String> communityCards = recognizeCommunityCards(img.submat(communityCardsROI));

        // 5. Вывести результат
        System.out.println("Карты на столе: " + communityCards);
        return data;
    }


    private static void loadCardTemplates() {
        File templatesDir = new File(TEMPLATES_PATH);
        for (File file : templatesDir.listFiles()) {
            if (file.getName().endsWith(".png")) {
                Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
                cardTemplates.put(file.getName().replace(".png", ""), template);
            }
        }
    }

    private static List<String> recognizeCommunityCards(Mat communityCardsImage) {
        List<String> cards = new ArrayList<>();
        int maxCards = 5;
        int cardWidth = communityCardsImage.width() / maxCards;

        for (int i = 0; i < maxCards; i++) {
            Rect cardROI = new Rect(i * cardWidth, 0, cardWidth, communityCardsImage.height());
            Mat singleCard = new Mat(communityCardsImage, cardROI);
            String cardName = recognizeSingleCard(singleCard);
            if (!cardName.isEmpty()) cards.add(cardName);
            singleCard.release();
        }
        return cards;
    }

    private static List<String> recognizeCards(Mat cardArea) {
        List<String> cards = new ArrayList<>();
        int cardCount = 2;
        int cardWidth = cardArea.width() / cardCount;

        for (int i = 0; i < cardCount; i++) {
            Rect cardROI = new Rect(i * cardWidth, 0, cardWidth, cardArea.height());
            Mat singleCard = new Mat(cardArea, cardROI);
            String cardName = recognizeSingleCard(singleCard);
            if (!cardName.isEmpty()) cards.add(cardName);
            singleCard.release();
        }
        return cards;
    }

    private static String recognizeSingleCard(Mat cardImage) {
        String bestMatch = "";
        double maxVal = 0;

        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
            Mat result = new Mat();
            Imgproc.matchTemplate(cardImage, entry.getValue(), result, Imgproc.TM_CCOEFF_NORMED);
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            if (mmr.maxVal > maxVal) {
                maxVal = mmr.maxVal;
                bestMatch = entry.getKey();
            }
            result.release();
        }
        System.out.println("% Распознания: "+maxVal);
        return (maxVal > MATCH_THRESHOLD) ? bestMatch : "";
    }


//    private static List<String> recognizeCommunityCards(Mat communityCardsImage) {
//        List<String> cards = new ArrayList<>();
//
//        // 1. Предполагаем, что карты расположены горизонтально с равными промежутками
//        int cardCount = 5; // Максимальное возможное количество
//        int cardWidth = communityCardsImage.width() / cardCount;
//        int cardHeight = communityCardsImage.height();
//
//        // 2. Перебор всех возможных позиций
//        for (int i = 0; i < cardCount; i++) {
//            // Вырезаем область для i-й карты
//            Rect cardROI = new Rect(
//                    i * cardWidth,
//                    0,
//                    cardWidth,
//                    cardHeight
//            );
//            Mat singleCard = new Mat(communityCardsImage, cardROI);
//
//            // 3. Распознаем карту
//            String cardName = recognizeSingleCard(singleCard);
//            if (!cardName.isEmpty()) {
//                cards.add(cardName);
//            }
//            singleCard.release();
//        }
//
//        return cards;
//    }

//    private static void loadCardTemplates(String templatesPath) {
//        cardTemplates = new HashMap<>();
//        File templatesDir = new File(templatesPath);
//        for (File file : templatesDir.listFiles()) {
//            if (file.getName().endsWith(".png")) {
//                Mat template = Imgcodecs.imread(file.getAbsolutePath());
//                cardTemplates.put(file.getName().replace(".png", ""), template);
//            }
//        }
//    }

//    private static List<String> recognizeCards(Mat cardImage) {
//        List<String> cards = new ArrayList<>();
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            Mat result = new Mat();
//            Imgproc.matchTemplate(cardImage, entry.getValue(), result, Imgproc.TM_CCOEFF_NORMED);
//            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//            if (mmr.maxVal > 0.85) {
//                cards.add(entry.getKey());
//            }
//            result.release();
//        }
//        return cards;
//    }

//    private static String recognizeSingleCard(Mat cardImage) {
//        String matchedCard = "";
//        double maxVal = 0.0;
//
//        // 4. Сравнение со всеми шаблонами
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            Mat result = new Mat();
//            Imgproc.matchTemplate(
//                    cardImage,
//                    entry.getValue(),
//                    result,
//                    Imgproc.TM_CCOEFF_NORMED
//            );
//
//            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//            if (mmr.maxVal > maxVal) {
//                maxVal = mmr.maxVal;
//                matchedCard = entry.getKey();
//            }
//            result.release();
//        }
//
//        return (maxVal > 0.85) ? matchedCard : "";
//    }

//    private static List<String> recognizeCards(Mat cardImage) {
//        List<String> recognizedCards = new ArrayList<>();
//
//        // Получение пути к папке с шаблонами
//        String templatesPath = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\PokerScreen\\src\\main\\resources\\cards"; // Папка с шаблонами
//
//        File templatesDir = new File(templatesPath);
//
//        // 1. Загрузка всех шаблонов
//        Map<String, Mat> cardTemplates = new HashMap<>();
//        for (File templateFile : templatesDir.listFiles()) {
//            if (templateFile.getName().endsWith(".png")) {
//                Mat template = Imgcodecs.imread(templateFile.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
//                if (template.empty()) {
//                    System.err.println("Ошибка загрузки шаблона: " + templateFile.getAbsolutePath());
//                    continue;
//                }
//                String cardName = templateFile.getName().replace(".png", "");
//                cardTemplates.put(cardName, template);
//            }
//        }
//
//        // Проверка размеров входного изображения
//        System.out.println("Размер входного изображения: " + cardImage.size());
//
//        // Проверка размеров шаблонов
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            System.out.println("Размер шаблона " + entry.getKey() + ": " + entry.getValue().size());
//        }
//
//        // Если размеры не совпадают, измените размер шаблонов до размера входного изображения
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            Mat resizedTemplate = new Mat();
//            Imgproc.resize(entry.getValue(), resizedTemplate, cardImage.size());
//            cardTemplates.put(entry.getKey(), resizedTemplate);
//        }
//
//        // Проверка размеров шаблонов
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            System.out.println("Размер шаблона " + entry.getKey() + ": " + entry.getValue().size());
//        }
//
//        // 2. Поиск совпадений для каждой карты
//        for (Map.Entry<String, Mat> entry : cardTemplates.entrySet()) {
//            Mat result = new Mat();
//            Imgproc.matchTemplate(
//                    cardImage,
//                    entry.getValue(),
//                    result,
//                    Imgproc.TM_CCOEFF_NORMED
//            );
//
//            // 3. Поиск лучшего совпадения
//            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//            double maxVal = mmr.maxVal;
//            System.out.println("maxVal: " + maxVal);
//
//            // 4. Порог совпадения (настраивается)
//            if (maxVal > 0.8) {
//                recognizedCards.add(entry.getKey());
//            }
//        }
//
//        // 5. Освобождение ресурсов
//        for (Mat template : cardTemplates.values()) {
//            template.release();
//        }
//
//        return recognizedCards;
//    }
//
//    private static List<String> recognizeMultipleCards(Mat communityCardsImage, int cardWidth, int cardHeight, int gapWidth) {
//        List<String> recognizedCards = new ArrayList<>();
//
//        // Определяем количество карт в области (с учетом промежутков)
//        int numCards = (communityCardsImage.width() + gapWidth) / (cardWidth + gapWidth);
//
//        System.out.println("Количество карт в области: " + numCards);
//
//        // Применяем распознавание для каждой карты
//        for (int i = 0; i < numCards; i++) {
//            // Вычисляем координаты текущей карты
//            int x = i * (cardWidth + gapWidth);
//            if (x + cardWidth > communityCardsImage.width()) {
//                break; // Если карта выходит за пределы области, прекращаем
//            }
//
//            Rect cardROI = new Rect(x, 0, cardWidth, cardHeight);
//
//            // Вырезаем область карты
//            Mat cardImage = communityCardsImage.submat(cardROI);
//
//            // Распознаём карту с помощью шаблонов
//            List<String> recognizedCard = recognizeCards(cardImage);
//            recognizedCards.addAll(recognizedCard);
//
//            // Визуализация (опционально, для отладки)
//            Imgproc.rectangle(communityCardsImage, cardROI.tl(), cardROI.br(), new Scalar(0, 255, 0), 2);
//        }
//
//        // (Опционально) Сохранение изображения с выделенными картами
//        Imgcodecs.imwrite("detected_cards.png", communityCardsImage);
//
//        return recognizedCards;
//    }




    private static String recognizeText(Mat roi) throws Exception {
        // Конвертация Mat в BufferedImage
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", roi, mob);
        byte[] byteArray = mob.toArray();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));

        // Распознавание текста через Tesseract
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\Tesseract\\tessdata"); // Укажите путь к tessdata
        tesseract.setLanguage("rus");
        return tesseract.doOCR(bufferedImage).trim();
    }

    public static void saveTemplateFromROI(Mat img, Rect roi, String outputPath) {
        // Вырезаем область карты из изображения
        Mat cardROI = img.submat(roi);

        // Сохраняем область как изображение
        boolean success = Imgcodecs.imwrite(outputPath, cardROI);
        if (success) {
            System.out.println("Шаблон успешно сохранён: " + outputPath);
        } else {
            System.err.println("Ошибка сохранения шаблона: " + outputPath);
        }
    }
}
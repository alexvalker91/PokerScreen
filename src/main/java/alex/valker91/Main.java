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
    private static final double MATCH_THRESHOLD = 0.985;
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
        File screenshotFile = new File("screenshot_4_cards.png");
//        ImageIO.write(screenshot, "png", screenshotFile);

        // 2. Загрузка изображения в OpenCV
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        // 3. Области интереса (настройте координаты)
        Rect myCards1ROI = new Rect(862, 648, 200, 67);  // x, y, width, height
//        Rect myCards2ROI = new Rect(960, 648, 98, 67);  // x, y, width, height

        Rect communityCardsROI = new Rect(710, 350, 500, 105);
        Rect potROI = new Rect(870, 325, 190, 25);

        // Сохранение вырезанного кусочка
        String outputPath = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\PokerScreen\\src\\main\\resources\\saved.png";
        saveTemplateFromROI(img, communityCardsROI, outputPath);

        Imgproc.rectangle(img, myCards1ROI.tl(), myCards1ROI.br(), new Scalar(0, 0, 255), 3);
//        Imgproc.rectangle(img, myCards2ROI.tl(), myCards2ROI.br(), new Scalar(0, 0, 255), 3);

        Imgproc.rectangle(img, communityCardsROI.tl(), communityCardsROI.br(), new Scalar(0, 255, 0), 3);
        Imgproc.rectangle(img, potROI.tl(), potROI.br(), new Scalar(255, 0, 0), 3);

        // Сохранение изображения с визуализацией
        Imgcodecs.imwrite("annotated_screenshot.png", img);

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
        List<String> myCard1 = recognizeCommunityCardsMy(img.submat(myCards1ROI));
//        String myCard2 = recognizeSingleCard(img.submat(myCards2ROI));

        // 5. Вывести результат
        System.out.println("Карты на столе: " + communityCards);
        System.out.println("Мои карты: " + myCard1);
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

    private static List<String> recognizeCommunityCardsMy(Mat communityCardsImage) {
        List<String> cards = new ArrayList<>();
        int maxCards = 2;
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
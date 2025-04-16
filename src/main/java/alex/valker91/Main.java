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
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Main {
//    public static void main(String[] args) {
//        System.out.println("Hello world!");
//    }

    static {
        System.load("G:\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Загрузка OpenCV
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> pokerData = scanPokerTable();
        System.out.println(pokerData);
    }

    public static Map<String, Object> scanPokerTable() throws Exception {
        Map<String, Object> data = new HashMap<>();

        // 1. Сделать скриншот
        BufferedImage screenshot = new Robot().createScreenCapture(
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
        );
        File screenshotFile = new File("screenshot.png");
        ImageIO.write(screenshot, "png", screenshotFile);

        // 2. Загрузка изображения в OpenCV
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        // 3. Области интереса (настройте координаты)
        Rect myCardsROI = new Rect(100, 400, 200, 100);  // x, y, width, height
        Rect communityCardsROI = new Rect(300, 200, 500, 100);
        Rect potROI = new Rect(850, 150, 150, 50);

        Imgproc.rectangle(img, myCardsROI.tl(), myCardsROI.br(), new Scalar(0, 0, 255), 3);
        Imgproc.rectangle(img, communityCardsROI.tl(), communityCardsROI.br(), new Scalar(0, 255, 0), 3);
        Imgproc.rectangle(img, potROI.tl(), potROI.br(), new Scalar(255, 0, 0), 3);

        // Сохранение изображения с визуализацией
        Imgcodecs.imwrite("annotated_screenshot.png", img);

        // 4. Распознавание карт (шаблонный метод)
        data.put("my_cards", recognizeCards(img.submat(myCardsROI)));
        data.put("community_cards", recognizeCards(img.submat(communityCardsROI)));

        // 5. Распознавание текста
        data.put("pot", recognizeText(img.submat(potROI)));

        return data;
    }

    private static List<String> recognizeCards(Mat cardImage) {
        // Здесь можно реализовать сравнение с шаблонами
        List<String> cards = new ArrayList<String>();
        // Пример: cards.add("Ah");
        return cards;
    }

    private static String recognizeText(Mat roi) throws Exception {
        // Конвертация Mat в BufferedImage
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", roi, mob);
        byte[] byteArray = mob.toArray();
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));

        // Распознавание текста через Tesseract
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("G:\\tesseract\\tessdata"); // Укажите путь к tessdata
        tesseract.setLanguage("eng");
        return tesseract.doOCR(bufferedImage).trim();
    }
}
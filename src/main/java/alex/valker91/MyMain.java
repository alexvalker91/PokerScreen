package alex.valker91;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMain {

    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }

    public static void main(String[] args) {

        File screenshotFile = new File("src/main/resources/screenshot/screenshot3.png");
        // 2. Загрузка изображения в OpenCV
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());





        Map<String, String> paths = new HashMap<>();
        paths.put("hero_cards_suits", "src/main/resources/hero_cards_suits/");
        paths.put("hero_cards_numbers", "src/main/resources/hero_cards_numbers/");

        Map<String, Rect> coordinates = new HashMap<>();
        Rect heroCardsRect = new Rect(862, 648, 200, 75); // x, y, w, h
        coordinates.put("hero_cards", heroCardsRect);



        // Сохранение изображения с визуализацией
        Imgproc.rectangle(img, heroCardsRect.tl(), heroCardsRect.br(), new Scalar(255, 0, 0), 3);
        Imgcodecs.imwrite("annotated_screenshot.png", img);


        Map<String, Integer> heroSeparators = new HashMap<>();
        heroSeparators.put("separator_1", 67);
        heroSeparators.put("separator_2", 137);

        Config config = new Config(paths, coordinates, heroSeparators);

//        // Capture screen or load image
//        Mat img = Imgcodecs.imread("path_to_image.png");

        // Create recognizer
//        PokerStarsTableRecognizer recognizer = new PokerStarsTableRecognizer(img, config);
//
//        // Detect hero cards
//        List<String> heroCards = recognizer.detectHeroCards();
//        System.out.println("Hero's cards: " + heroCards);
    }
}

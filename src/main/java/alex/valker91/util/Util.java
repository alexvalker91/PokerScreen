package alex.valker91.util;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Util {
//    private static Map<String, Mat> cardTemplates = new HashMap<>();
//    private static final String TEMPLATES_PATH = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\card_numbers";
//
//    static {
//        loadCardTemplates();
//    }
//    private static void loadCardTemplates() {
//        File templatesDir = new File(TEMPLATES_PATH);
//        for (File file : templatesDir.listFiles()) {
//            if (file.getName().endsWith(".png")) {
//                Mat template = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
//                cardTemplates.put(file.getName().replace(".png", ""), template);
//            }
//        }
//    }
//    public static String recognizeSingleCard(Mat cardImage) {
//        String bestMatch = "";
//        double maxVal = 0;
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
//            // Сравнение с шаблоном
//            Mat result = new Mat();
//            Imgproc.matchTemplate(cardImage, template, result, Imgproc.TM_CCOEFF_NORMED);
//
//            // Извлечение максимального значения совпадения
//            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
//            if (mmr.maxVal > maxVal) {
//                maxVal = mmr.maxVal;
//                bestMatch = entry.getKey();
//            }
//
//            result.release();
//        }
//
//        System.out.println("% Распознания: " + maxVal);
//        return (maxVal > 0.985) ? bestMatch : "";
//    }
//
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
}

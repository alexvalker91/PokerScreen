package alex.valker91;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private String tablePartRecognition(Mat img, String directory, int colorOfImg) {
        // Словарь для хранения ошибок
        Map<String, Double> errDict = new HashMap<>();

        // Получаем список файлов в директории
        File dir = new File(directory);
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Directory is empty or does not exist: " + directory);
        }

        // Перебираем все изображения в директории
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".png")) { // Проверяем, что это файл изображения
                String imageName = file.getName().split("\\.")[0]; // Получаем имя файла без расширения

                // Читаем шаблонное изображение
                Mat templateImg = Imgcodecs.imread(file.getAbsolutePath(), colorOfImg);

                // Сравниваем изображения
//                double err = imageComparison(img, templateImg);

                // Сохраняем ошибку в словарь
//                errDict.put(imageName, err);
            }
        }

        // Находим ключ с минимальной ошибкой
        return errDict.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new IllegalStateException("No valid template found"))
                .getKey();
    }
//    public static List<Rect> convertContoursToBboxes(List<MatOfPoint> contours, int minHeight, int minWidth) {
//        List<Rect> bboxes = new ArrayList<>();
//        for (MatOfPoint contour : contours) {
//            Rect rect = Imgproc.boundingRect(contour);
//            if (rect.height >= minHeight && rect.width >= minWidth) {
//                bboxes.add(rect);
//            }
//        }
//        return bboxes;
//    }
//
//    public static Mat thresholding(Mat img, int thresholdValue) {
//        Mat gray = new Mat();
//        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
//        Mat binaryImg = new Mat();
//        Imgproc.threshold(gray, binaryImg, thresholdValue, 255, Imgproc.THRESH_BINARY);
//        return binaryImg;
//    }
//
//    public static List<Rect> sortBboxes(List<Rect> boundingBoxes, String method) {
//        List<Rect> sorted = new ArrayList<>(boundingBoxes);
//        if (method.equals("left-to-right")) {
//            sorted.sort((r1, r2) -> Integer.compare(r1.x, r2.x));
//        } else if (method.equals("bottom-to-top")) {
//            sorted.sort((r1, r2) -> Integer.compare(r2.y, r1.y));
//        } else if (method.equals("top-to-bottom")) {
//            sorted.sort((r1, r2) -> Integer.compare(r1.y, r2.y));
//        } else {
//            throw new IllegalArgumentException("Invalid sorting method");
//        }
//        return sorted;
//    }
//
//    public static Map<Integer, List<Rect>> cardSeparator(List<Rect> bboxes, List<Integer> separators) {
//        Map<Integer, List<Rect>> dct = new HashMap<>();
//        for (Rect bbox : bboxes) {
//            for (int i = 0; i < separators.size(); i++) {
//                int separator = separators.get(i);
//                if (bbox.x + bbox.width < separator) {
//                    dct.computeIfAbsent(i, k -> new ArrayList<>()).add(bbox);
//                    break;
//                }
//            }
//        }
//        return dct;
//    }
//
//    public static String tablePartRecognition(Mat img, String directory, int colorFlag) {
//        File dir = new File(directory);
//        Map<String, Double> errors = new HashMap<>();
//
//        if (!dir.exists() || !dir.isDirectory()) {
//            System.err.println("Директория не найдена или не является папкой: " + dir.getAbsolutePath());
//            return "";
//        }
//
//        File[] files = dir.listFiles();
//        if (files == null) {
//            System.err.println("Не удалось получить список файлов в директории: " + dir.getAbsolutePath());
//            return "";
//        }
//
//        for (File file : files) {
//            if (file.isFile() && isImageFile(file)) {
//                String fileName = file.getName();
//                String templateName = fileName.split("\\.")[0];
//                Mat template = Imgcodecs.imread(file.getAbsolutePath(), colorFlag);
//
//                if (template.empty()) {
//                    System.err.println("Ошибка загрузки шаблона: " + file.getAbsolutePath());
//                    continue;
//                }
//
//                Mat result = new Mat();
//                Imgproc.matchTemplate(img, template, result, Imgproc.TM_SQDIFF_NORMED);
//
//                Core.MinMaxLocResult minMax = new Core.MinMaxLocResult();
////                Core.minMaxLoc(result, minMax);
//
//                errors.put(templateName, minMax.minVal);
//            }
//        }
//
//        if (errors.isEmpty()) {
//            System.err.println("Не найдено ни одного шаблона в директории: " + dir.getAbsolutePath());
//            return "";
//        }
//
//        return Collections.min(errors.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
//    }
//
//    private static boolean isImageFile(File file) {
//        String fileName = file.getName().toLowerCase();
//        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".bmp");
//    }
}

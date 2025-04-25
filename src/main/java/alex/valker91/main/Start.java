package alex.valker91.main;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Start {

    private static Map<String, Mat> cardTemplates = new HashMap<>();
    private static final String TEMPLATES_PATH = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\card_numbers";

    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
        loadCardTemplates();
    }

    public static void main(String[] args) {
        File screenshotFile = new File("src/main/resources/screenshot/screenshot4.png");
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        Mat allImage = thresholding(img, 200, 67);

        Rect card1 = new Rect(735, 405, 65, 70);
        Mat card1Cut = allImage.submat(card1);
        Imgcodecs.imwrite("7.png", card1Cut);
        String recognizedCard1 = matchCard(card1Cut);
        System.out.println("Распознана карта 1: " + recognizedCard1);

        Rect card2 = new Rect(835, 405, 65, 70);
        Mat card2Cut = allImage.submat(card2);
        Imgcodecs.imwrite("7_2.png", card2Cut);
        String recognizedCard2 = matchCard(card2Cut);
        System.out.println("Распознана карта 2: " + recognizedCard2);

        Rect card3 = new Rect(936, 405, 65, 70);
        Mat card3Cut = allImage.submat(card3);
        Imgcodecs.imwrite("7_3.png", card3Cut);
        String recognizedCard3 = matchCard(card3Cut);
        System.out.println("Распознана карта 3: " + recognizedCard3);

        Rect card4 = new Rect(1035, 405, 65, 70);
        Mat card4Cut = allImage.submat(card4);
//        Imgcodecs.imwrite("7_4.png", card4Cut);
        String recognizedCard4 = matchCard(card4Cut);
        System.out.println("Распознана карта 4: " + recognizedCard4);
    }

    private static String matchCard(Mat cardImage) {
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

    private static Mat thresholding(Mat img, int value1, int value2) {
        // Convert to grayscale
        Mat grayImg = new Mat();
        Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);

        // Apply thresholding
        Mat binaryImg = new Mat();
        Imgproc.threshold(grayImg, binaryImg, value1, value2, Imgproc.THRESH_BINARY);
        return binaryImg;
    }

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
}

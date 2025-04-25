package alex.valker91.main;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Start {

    private static Map<String, Mat> cardTemplates = new HashMap<>();
    private static Map<String, Mat> myCardTemplates = new HashMap<>();
    private static final String TEMPLATES_PATH = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\card_numbers";
    private static final String MY_TEMPLATES_PATH = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\my_card_numbers";

    private static final String TEMPLATES_CARD_SUITE = "C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\my\\PokerScreen\\src\\main\\resources\\card_suits";
    private static Map<String, Mat> templatesCardSuite = new HashMap<>();

    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
        loadCardTemplates();
    }

    public static void main(String[] args) {
        File screenshotFile = new File("src/main/resources/screenshot/screenshot14.png");
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());
        Imgcodecs.imwrite("img.png", img);

        Mat allImage = thresholding(img, 200, 67);
        Imgcodecs.imwrite("allImage.png", allImage);

        Rect card1 = new Rect(735, 405, 65, 66);
        Mat card1Cut = allImage.submat(card1);
//        Imgcodecs.imwrite("A.png", card1Cut);
        String recognizedCard1 = matchCard(card1Cut);
        System.out.println("Распознана карта 1: " + recognizedCard1);
        Rect card1Suite = new Rect(757, 362, 30, 31);
        Mat card1SuiteCut = img.submat(card1Suite);
        String recognizedCard1Suite = detectCardSuit(card1SuiteCut);
        System.out.println("Распознана карта 1 масть: " + recognizedCard1Suite);
//        Imgcodecs.imwrite("Hearts.png", card1SuiteCut);

        Rect card2 = new Rect(835, 405, 65, 66);
        Mat card2Cut = allImage.submat(card2);
//        Imgcodecs.imwrite("7_2.png", card2Cut);
        String recognizedCard2 = matchCard(card2Cut);
        System.out.println("Распознана карта 2: " + recognizedCard2);
        Rect card2Suite = new Rect(857, 362, 30, 31);
        Mat card2SuiteCut = img.submat(card2Suite);
        String recognizedCard2Suite = detectCardSuit(card2SuiteCut);
        System.out.println("Распознана карта 2 масть: " + recognizedCard2Suite);

        Rect card3 = new Rect(936, 405, 65, 66);
        Mat card3Cut = allImage.submat(card3);
//        Imgcodecs.imwrite("7_3.png", card3Cut);
        String recognizedCard3 = matchCard(card3Cut);
        System.out.println("Распознана карта 3: " + recognizedCard3);
        Rect card3Suite = new Rect(957, 362, 30, 31);
        Mat card3SuiteCut = img.submat(card3Suite);
//        Imgcodecs.imwrite("1234.png", card3SuiteCut);
        String recognizedCard3Suite = detectCardSuit(card3SuiteCut);
        System.out.println("Распознана карта 3 масть: " + recognizedCard3Suite);

        Rect card4 = new Rect(1036, 405, 65, 66);
        Mat card4Cut = allImage.submat(card4);
//        Imgcodecs.imwrite("7_4.png", card4Cut);
        String recognizedCard4 = matchCard(card4Cut);
        System.out.println("Распознана карта 4: " + recognizedCard4);
        Rect card4Suite = new Rect(1057, 362, 30, 31);
        Mat card4SuiteCut = img.submat(card4Suite);
//        Imgcodecs.imwrite("12345.png", card4SuiteCut);
        String recognizedCard4Suite = detectCardSuit(card4SuiteCut);
        System.out.println("Распознана карта 4 масть: " + recognizedCard4Suite);





        Rect card5 = new Rect(1136, 405, 65, 66);
        Mat card5Cut = allImage.submat(card5);
//        Imgcodecs.imwrite("7_4.png", card5Cut);
        String recognizedCard5 = matchCard(card5Cut);
        System.out.println("Распознана карта 5: " + recognizedCard5);
        Rect card5Suite = new Rect(1157, 362, 30, 31);
        Mat card5SuiteCut = img.submat(card5Suite);
//        Imgcodecs.imwrite("1234578.png", card5SuiteCut);
        String recognizedCard5Suite = detectCardSuit(card5SuiteCut);
        System.out.println("Распознана карта 5 масть: " + recognizedCard5Suite);







        Rect myCard1 = new Rect(870, 655, 40, 44);
        Mat myCard1Cut = allImage.submat(myCard1);
//        Imgcodecs.imwrite("my_A.png", myCard1Cut);
        String recognizedMyCard1 = myMatchCard(myCard1Cut);
        System.out.println("Распознана моя карта 1: " + recognizedMyCard1);

        Rect myCard2 = new Rect(965, 655, 40, 44);
        Mat myCard2Cut = allImage.submat(myCard2);
//        Imgcodecs.imwrite("my_8.png", myCard2Cut);
        String recognizedMyCard2 = myMatchCard(myCard2Cut);
        System.out.println("Распознана моя карта 2: " + recognizedMyCard2);
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
//                System.out.printf("Шаблон: %-10s | Совпадение: %.2f%%\n", entry.getKey(), matchPercent);

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
    private static String myMatchCard(Mat cardImage) {
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

        for (Map.Entry<String, Mat> entry : myCardTemplates.entrySet()) {
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
//                System.out.printf("Шаблон: %-10s | Совпадение: %.2f%%\n", entry.getKey(), matchPercent);

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

    private static String detectCardSuit(Mat cardRegion) {
        double maxScore = -1;
        String bestMatch = "Unknown";

        // Проходим по всем загруженным шаблонам
        for (Map.Entry<String, Mat> entry : templatesCardSuite.entrySet()) {
            Mat result = new Mat();
            Mat template = entry.getValue();

            // Проверяем размер шаблона
            if (cardRegion.width() < template.width() || cardRegion.height() < template.height()) {
                System.err.println("Размер шаблона " + entry.getKey() + " превышает размер карты");
                continue;
            }

            // Сравнение шаблонов
            Imgproc.matchTemplate(
                    cardRegion,
                    template,
                    result,
                    Imgproc.TM_CCOEFF_NORMED
            );

            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            if (mmr.maxVal > maxScore) {
                maxScore = mmr.maxVal;
                bestMatch = entry.getKey();
            }
        }
        return maxScore > 0.8 ? bestMatch : "Unknown";
    }

    private static void loadCardTemplates() {
        File templatesDir = new File(TEMPLATES_PATH);
        File myTemplatesDir = new File(MY_TEMPLATES_PATH);
        File templatesCardSuiteFile = new File(TEMPLATES_CARD_SUITE);

        // Проверка существования директории
        if (!templatesDir.exists() || !templatesDir.isDirectory() || !myTemplatesDir.exists() || !myTemplatesDir.isDirectory()) {
            System.err.println("Директория шаблонов не найдена: " + TEMPLATES_PATH);
            System.err.println("Директория шаблонов не найдена: " + MY_TEMPLATES_PATH);
            return;
        }

        File[] files = templatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        File[] myFiles = myTemplatesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        File[] myFilesSuits = templatesCardSuiteFile.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));


        // Проверка наличия PNG-файлов
        if (files == null || files.length == 0 || myFiles == null || myFiles.length == 0) {
            System.err.println("В директории нет PNG-файлов: " + TEMPLATES_PATH);
            System.err.println("В директории нет PNG-файлов: " + MY_TEMPLATES_PATH);
            return;
        }

        for (File file : myFilesSuits) {
            String name = file.getName().replace(".png", "");
            Mat template = Imgcodecs.imread(file.getAbsolutePath());
            if (template.empty()) {
                System.err.println("Ошибка загрузки: " + file.getName());
                continue;
            }
            templatesCardSuite.put(name, template);
//            System.out.println("Загружен шаблон: " + name);
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
//            System.out.printf(
//                    "Загружен шаблон: %-15s | Каналы: %d | Тип: %s | Размер: %dx%d\n",
//                    templateName,
//                    template.channels(),
//                    CvType.typeToString(template.type()),
//                    template.cols(),
//                    template.rows()
//            );
        }

        for (File file : myFiles) {
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
            myCardTemplates.put(templateName, template);

            // Отладочный вывод
//            System.out.printf(
//                    "Загружен шаблон: %-15s | Каналы: %d | Тип: %s | Размер: %dx%d\n",
//                    templateName,
//                    template.channels(),
//                    CvType.typeToString(template.type()),
//                    template.cols(),
//                    template.rows()
//            );
        }
    }
}

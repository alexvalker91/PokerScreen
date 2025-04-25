package alex.valker91.main;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class Generater {

    static {
        System.load("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4110.dll");
    }

    public static void main(String[] args) throws Exception {
        File screenshotFile = new File("src/main/resources/screenshot/screenshot1.png");
        Mat img = Imgcodecs.imread(screenshotFile.getAbsolutePath());

        Mat allImage = thresholding(img, 200, 67);
        Imgcodecs.imwrite("allImage.png", allImage);

        Rect card1 = new Rect(735, 400, 65, 80);
//        Imgproc.rectangle(allImage, card1.tl(), card1.br(), new Scalar(255, 0, 0), 3);
//        Imgcodecs.imwrite("1card.png", allImage);

        Mat roiImagecard1 = allImage.submat(card1);
        Imgcodecs.imwrite("7.png", roiImagecard1);
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
}

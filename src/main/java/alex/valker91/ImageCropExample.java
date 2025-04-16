package alex.valker91;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageCropExample {
    public static void main(String[] args) {
        try {
            // Загружаем исходное изображение
            File inputFile = new File("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\PokerScreen\\src\\main\\resources\\cards\\9.png");
            BufferedImage originalImage = ImageIO.read(inputFile);

            // Указываем параметры обрезки (например, обрезать 50 пикселей слева и справа)
//            int cropWidth = originalImage.getWidth() - 100; // Обрезаем 50 пикселей с каждой стороны
//            int cropHeight = originalImage.getHeight(); // Высота остается неизменной
            int cropStartX = 2; // Начало обрезки по X (50 пикселей от левого края)
            int cropStartY = 0; // Начало обрезки по Y (0 пикселей от верхнего края)

            // Выполняем обрезку
            BufferedImage croppedImage = originalImage.getSubimage(cropStartX, cropStartY+3, originalImage.getWidth()-cropStartX-403, originalImage.getHeight()-4);

            // Сохраняем обрезанное изображение
            File outputFile = new File("C:\\Users\\Aliaksandr_Kreyer\\Desktop\\my\\PokerScreen\\src\\main\\resources\\cards\\8_clubs.png");
            ImageIO.write(croppedImage, "png", outputFile);

            System.out.println("Изображение успешно обрезано и сохранено!");
        } catch (IOException e) {
            System.err.println("Ошибка при обработке изображения: " + e.getMessage());
        }
    }
}

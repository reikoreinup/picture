package ee.avalancheTest;

import ee.avalancheTest.exceptions.InvalidFileException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidFileException {
        Picture picture = new Picture("1.jpg");
        BufferedImage resize = picture.compress(30000);
        ImageIO.write(resize, "jpg", new File("newImg.jpg"));

    }
}

package ee.avalancheTest;

import ee.avalancheTest.exceptions.InvalidFileException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Picture {

    private BufferedImage image;
    private float imageSize;

    public Picture(String imagePath) throws InvalidFileException {

        ClassLoader classLoader = getClass().getClassLoader();

        try {
            File file = new File(classLoader.getResource(imagePath).getFile());
            this.imageSize = file.length();
            this.image = ImageIO.read(file);
        } catch (IOException | NullPointerException e) {
            throw new InvalidFileException();
        }
    }

    public BufferedImage resize (int endHeight) {



        int currentHeight = image.getHeight(null);
        float changePercent = changePercentage((float) endHeight, currentHeight);
        int newWidth = (int) (image.getWidth(null) * changePercent);


        Image newImage = image.getScaledInstance((int) newWidth, endHeight, Image.SCALE_DEFAULT);

        BufferedImage bufferedImage = new BufferedImage(newWidth, endHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(newImage, 0, 0, null);
        bGr.dispose();
        return bufferedImage;
    }

    public BufferedImage compress(float bytes) {

        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        ImageOutputStream outputStream = null;
        try {
            outputStream = ImageIO.createImageOutputStream(compressed);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgWriteParam.setCompressionQuality(1f);
        jpgWriter.setOutput(outputStream);

        try {
            jpgWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);
        } catch (IOException e) {
            e.printStackTrace();
        }


        jpgWriter.dispose();


        byte[] jpegData = compressed.toByteArray();
        System.out.println(jpegData.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(jpegData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float changePercentage(float smaller, float larger) {
        return smaller / larger;
    }
}

package tests;


import ee.avalanchetest.PictureTool;
import ee.avalanchetest.exceptions.InvalidRequestException;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PictureToolTest {

    private PictureTool pictureEditor = new PictureTool();
    private String b64 = " data:image/jpeg;base64,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";
    public static final int DESIRED_SIZE = 30000;
    public static final int DESIRED_HEIGHT = 600;


    @Test
    public void decodeEncodeBase64Test() throws IOException {

        assert b64.equals(pictureEditor.bufferedImageToBase64(pictureEditor.base64ToBufferedImage(b64)));

    }

    @Test
    public void resizeTest() throws InvalidRequestException, IOException {
        String newImageString = pictureEditor.resize(b64, DESIRED_HEIGHT);
        BufferedImage newImage = pictureEditor.base64ToBufferedImage(newImageString);
        assert DESIRED_HEIGHT == newImage.getHeight();
    }

    @Test
    public void compressTest() throws InvalidRequestException, IOException {

        BufferedImage originalImage = pictureEditor.base64ToBufferedImage(b64);
        BufferedImage compressedImage = pictureEditor.base64ToBufferedImage(pictureEditor.compress(b64, DESIRED_SIZE));
        int compressedSize = compressedImage.getData().getDataBuffer().getSize();
        int originalSize = originalImage.getData().getDataBuffer().getSize();


        assert compressedSize < originalSize;
        assert Math.abs(compressedSize - DESIRED_SIZE) < 0.1 * DESIRED_SIZE; // within 10% of desired size
    }

    @Test(expected = InvalidRequestException.class)
    public void negativeInputTest1() throws InvalidRequestException, IOException {
        pictureEditor.resize(b64, -1);
    }

    @Test(expected = InvalidRequestException.class)
    public void negativeInputTest2() throws InvalidRequestException, IOException {
        pictureEditor.compress(b64, -1);
    }


    @Test(expected = InvalidRequestException.class)
    public void compressLargerTest() throws InvalidRequestException, IOException {
        pictureEditor.compress(b64, Integer.MAX_VALUE);
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


}

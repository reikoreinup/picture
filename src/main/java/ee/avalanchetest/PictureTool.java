package ee.avalanchetest;

import ee.avalanchetest.exceptions.InvalidRequestException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class PictureTool {

    public static final int BITS_IN_ONE_DIGIT_OF_BASE64 = 6; // each digit of base64 has 6 bits
    public static final double BASE64_QUANTIFIER = 1.37; //when encoded to base 64, the file is approximately 1.37 times larger


    private float qualityCounter = 0.99f;


    public String resize(String base64String, int endHeight) throws InvalidRequestException, IOException {
        validateNegative(endHeight);
        BufferedImage bfff = base64ToBufferedImage(base64String);
        int currentHeight = bfff.getHeight(null);
        float changePercent = (float) endHeight / currentHeight;
        int newWidth = (int) (bfff.getWidth(null) * changePercent);

        Image newImage = bfff.getScaledInstance(newWidth, endHeight, Image.SCALE_DEFAULT);

        BufferedImage blankBufferedImage = new BufferedImage(newWidth, endHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D bGr = blankBufferedImage.createGraphics();
        bGr.drawImage(newImage, 0, 0, null); // draw on blank image
        bGr.dispose();
        return bufferedImageToBase64(blankBufferedImage);
    }


    public String compress(String base64String, float bytes) throws InvalidRequestException, IOException {
        BufferedImage bufferedImage = base64ToBufferedImage(base64String);
        float jpgOriginalSize = (float) (base64String.length() * BITS_IN_ONE_DIGIT_OF_BASE64 / BASE64_QUANTIFIER);
        validateCompressionInput(bytes, jpgOriginalSize);

        while (jpgOriginalSize > bytes) {

            ByteArrayOutputStream compressed = new ByteArrayOutputStream();
            ImageOutputStream outputStream;

            outputStream = ImageIO.createImageOutputStream(compressed);

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();

            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(qualityCounter);

            if (qualityCounter <= 0.05f) {
                return bufferedImageToBase64(bufferedImage);
            }
            qualityCounter -= 0.05f;

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, new IIOImage(bufferedImage, null, null), jpgWriteParam);
            jpgWriter.dispose();
            outputStream.close();

            byte[] jpegData = compressed.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(jpegData);
            bufferedImage = ImageIO.read(bais);

            jpgOriginalSize = (float) (bufferedImageToBase64(bufferedImage).length() * BITS_IN_ONE_DIGIT_OF_BASE64 / BASE64_QUANTIFIER);

        }
        return bufferedImageToBase64(bufferedImage);
    }


    private void validateCompressionInput(float bytes, float jpgOriginalSize) throws InvalidRequestException {
        if (bytes >= jpgOriginalSize) {
            throw new InvalidRequestException("Desired size has to be smaller than original size.");
        }
        
        validateNegative((int) bytes);
    }

    private void validateNegative(int i) throws InvalidRequestException {
        if (i < 0) {
            throw new InvalidRequestException("Input cannot be negative.");
        }
    }


    public BufferedImage base64ToBufferedImage(String b64) throws IOException {
        try {
            // Base64 might have header that is split by a comma.
            byte[] b64ImageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(b64.split(",")[1]);
            return ImageIO.read(new ByteArrayInputStream(b64ImageBytes));
        } catch (ArrayIndexOutOfBoundsException e) {
            byte[] b64ImageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(b64);
            return ImageIO.read(new ByteArrayInputStream(b64ImageBytes));
        }

    }

    public String bufferedImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        return Base64.getEncoder().encodeToString(os.toByteArray());

    }
}

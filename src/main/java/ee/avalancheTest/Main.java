package ee.avalancheTest;

import ee.avalancheTest.exceptions.InvalidFileException;
import ee.avalancheTest.exceptions.InvalidRequestException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException, InvalidFileException, InvalidRequestException {
        String b64 = readFile("C:\\Users\\Reiko\\Desktop\\saast\\projektialged\\niisamaprog\\picture\\src\\main\\resources\\base64img.txt", Charset.defaultCharset());
        Picture picture = new Picture();
        String resize = picture.compress(b64, 20000);
        System.out.println(resize);

    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}

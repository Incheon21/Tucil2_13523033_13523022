import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FileProcessor {
    
    // load iamge
    public static BufferedImage loadImage(String filePath) throws IOException {
        File file = new File(filePath);
        return ImageIO.read(file);
    }
    
    // save image
    public static void saveImage(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        ImageIO.write(image, extension, outputFile);
    }
    
    // cek file size
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.length();
        } else {
            return -1;
        }
    }

    //hapus directory
    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    // dapeting file extension dri input user
    public static String getFileExtension(String filePath) {
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i);
        }
        return extension.toLowerCase();
    }
}
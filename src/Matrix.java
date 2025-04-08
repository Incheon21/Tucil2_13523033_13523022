import java.awt.Color;
import java.awt.image.BufferedImage;

public class Matrix {
    private int width;
    private int height;
    private int[][] red;
    private int[][] green;
    private int[][] blue;

    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.red = new int[height][width];
        this.green = new int[height][width];
        this.blue = new int[height][width];
    }
    
    // image ke matrix
    public static Matrix fromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Matrix matrix = new Matrix(width, height);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                matrix.red[y][x] = color.getRed();
                matrix.green[y][x] = color.getGreen();
                matrix.blue[y][x] = color.getBlue();
            }
        }
        
        return matrix;
    }
    
    // matrix ke image
    public BufferedImage toImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(red[y][x], green[y][x], blue[y][x]);
                image.setRGB(x, y, color.getRGB());
            }
        }
        
        return image;
    }
    
    // warna average dari RGB dalam sebuah region/node
    public Color getAverageColor(int startX, int startY, int regionWidth, int regionHeight) {
        long sumR = 0, sumG = 0, sumB = 0;
        int count = 0;
        
        int endX = Math.min(startX + regionWidth, width);
        int endY = Math.min(startY + regionHeight, height);
        
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                sumR += red[y][x];
                sumG += green[y][x];
                sumB += blue[y][x];
                count++;
            }
        }
        
        if (count > 0) {
            int avgR = (int)(sumR / count);
            int avgG = (int)(sumG / count);
            int avgB = (int)(sumB / count);
            return new Color(avgR, avgG, avgB);
        } else {
            return Color.BLACK;
        }
    }
    
    // samain region/node dengan warna tertentu
    public void fillRegion(int startX, int startY, int regionWidth, int regionHeight, Color color) {
        int endX = Math.min(startX + regionWidth, width);
        int endY = Math.min(startY + regionHeight, height);
        
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                red[y][x] = color.getRed();
                green[y][x] = color.getGreen();
                blue[y][x] = color.getBlue();
            }
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
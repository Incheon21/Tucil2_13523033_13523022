import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime, endTime;
        
        try {
            System.out.print("Masukkan absolute path ke input image: ");
            String inputPath = scanner.nextLine();
            BufferedImage originalImage = ImageProcessor.loadImage(inputPath);
            
            System.out.println("Select error measurement method:");
            System.out.println("1. Variance");
            System.out.println("2. Mean Absolute Deviation (MAD)");
            
            int errorMethod = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Masukkan nilai threshold: ");
            double threshold = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Masukkan ukuran minimum block: ");
            int minBlockSize = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Masukkan absolute path ke output image: ");
            String outputPath = scanner.nextLine();
            
            startTime = System.currentTimeMillis();
            
            QuadTree quadTree = new QuadTree(originalImage, minBlockSize, threshold, errorMethod);
            
            BufferedImage compressedImage = quadTree.generateCompressedImage();
            
            endTime = System.currentTimeMillis();
            
            ImageProcessor.saveImage(compressedImage, outputPath);
            
            long originalSize = ImageProcessor.getFileSize(inputPath);
            long compressedSize = ImageProcessor.getFileSize(outputPath);
            double compressionPercentage = quadTree.calculateCompressionPercentage(originalSize, compressedSize);
            
            System.out.println("\nResults:");
            System.out.println("Waktu eksekusi: " + (endTime - startTime) + " ms");
            System.out.println("Ukuran asli image: " + originalSize + " bytes");
            System.out.println("Ukuran setelah dikompresi: " + compressedSize + " bytes");
            System.out.println("Persentase kompresi: " + String.format("%.2f", compressionPercentage) + "%");
            System.out.println("Kedalaman tree (depth): " + quadTree.getTreeDepth());
            System.out.println("jumlah nodes: " + quadTree.getNodeCount());
            System.out.println("Alamat output: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
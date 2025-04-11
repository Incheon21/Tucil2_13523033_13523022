import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long startTime, endTime;

        try {
            System.out.print("Masukkan absolute path ke input image: ");
            String inputPath = scanner.nextLine();
            BufferedImage originalImage = FileProcessor.loadImage(inputPath);

            System.out.println("Select error measurement method:");
            System.out.println("1. Variance");
            System.out.println("2. Mean Absolute Deviation (MAD)");
            System.out.println("3. Max Pixel Difference");
            System.out.println("4. Entropy");
            System.out.println("5. Structural Similarity Index (SSIM)");
            System.out.print("Masukkan pilihan (1-5): ");

            int errorMethod = Integer.parseInt(scanner.nextLine());

            System.out.print("Masukkan nilai threshold: ");
            double threshold = Double.parseDouble(scanner.nextLine());

            System.out.print("Masukkan ukuran minimum block: ");
            int minBlockSize = Integer.parseInt(scanner.nextLine());

            System.out.print(
                    "Masukkan target compression rate (0-1, 0 untuk menggunakan threshold dan minimum block size yang telah ditentukan): ");
            double targetCompressionRate = Double.parseDouble(scanner.nextLine());

            System.out.print("Masukkan absolute path ke output image: ");
            String outputPath = scanner.nextLine();

            startTime = System.currentTimeMillis();

            QuadTree quadTree;

            if (targetCompressionRate > 0 && targetCompressionRate <= 1) {
                System.out.println("\nMencari parameter teroptimal untuk mendekati target: " +
                        String.format("%.2f", targetCompressionRate * 100) + "%");

                File tempDir = new File(new File(outputPath).getParent(), "temp");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                System.out.println("Debug images will be saved in: " + tempDir.getAbsolutePath());
                String fileExtension = FileProcessor.getFileExtension(inputPath);

                quadTree = Method.findOptimalParameters(originalImage, 1, targetCompressionRate, inputPath,
                        outputPath, tempDir.getAbsolutePath(), fileExtension);

                FileProcessor.deleteDirectory(tempDir);
                System.out.println("Temporary debug images have been deleted.");
            } else {
                quadTree = new QuadTree(originalImage, minBlockSize, threshold, errorMethod);
            }

            BufferedImage compressedImage = quadTree.generateCompressedImage();

            endTime = System.currentTimeMillis();

            FileProcessor.saveImage(compressedImage, outputPath);

            long originalSize = FileProcessor.getFileSize(inputPath);
            long compressedSize = FileProcessor.getFileSize(outputPath);
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
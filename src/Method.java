import java.io.IOException;
import java.awt.image.BufferedImage;

public class Method {

    // metode variance
    public static double calculateVariance(QuadTree quadTree, int x, int y, int width, int height) {
        java.awt.Color avgColor = quadTree.getAverageColor(x, y, width, height);
        double meanR = avgColor.getRed();
        double meanG = avgColor.getGreen();
        double meanB = avgColor.getBlue();

        double varR = 0, varG = 0, varB = 0;
        int count = 0;

        int endX = Math.min(x + width, quadTree.getWidth());
        int endY = Math.min(y + height, quadTree.getHeight());

        for (int cy = y; cy < endY; cy++) {
            for (int cx = x; cx < endX; cx++) {
                java.awt.Color pixelColor = quadTree.getAverageColor(cx, cy, 1, 1); // Get single pixel color
                varR += Math.pow(pixelColor.getRed() - meanR, 2);
                varG += Math.pow(pixelColor.getGreen() - meanG, 2);
                varB += Math.pow(pixelColor.getBlue() - meanB, 2);
                count++;
            }
        }

        if (count > 0) {
            varR /= count;
            varG /= count;
            varB /= count;
        }

        return (varR + varG + varB) / 3.0;
    }

    // metode mean absolute deviation
    public static double calculateMAD(QuadTree quadTree, int x, int y, int width, int height) {
        java.awt.Color avgColor = quadTree.getAverageColor(x, y, width, height);
        double meanR = avgColor.getRed();
        double meanG = avgColor.getGreen();
        double meanB = avgColor.getBlue();

        double madR = 0, madG = 0, madB = 0;
        int count = 0;

        int endX = Math.min(x + width, quadTree.getWidth());
        int endY = Math.min(y + height, quadTree.getHeight());

        for (int cy = y; cy < endY; cy++) {
            for (int cx = x; cx < endX; cx++) {
                java.awt.Color pixelColor = quadTree.getAverageColor(cx, cy, 1, 1); // Get single pixel color
                madR += Math.abs(pixelColor.getRed() - meanR);
                madG += Math.abs(pixelColor.getGreen() - meanG);
                madB += Math.abs(pixelColor.getBlue() - meanB);
                count++;
            }
        }

        if (count > 0) {
            madR /= count;
            madG /= count;
            madB /= count;
        }

        return (madR + madG + madB) / 3.0;
    }

    // metode max pixel difference
    public static double calculateMaxPixelDifference(QuadTree quadTree, int x, int y, int width, int height) {

        java.awt.Color avgColor = quadTree.getAverageColor(x, y, width, height);
        int meanR = avgColor.getRed();
        int meanG = avgColor.getGreen();
        int meanB = avgColor.getBlue();

        int maxDifference = 0;

        int endX = Math.min(x + width, quadTree.getWidth());
        int endY = Math.min(y + height, quadTree.getHeight());

        for (int cy = y; cy < endY; cy++) {
            for (int cx = x; cx < endX; cx++) {
                java.awt.Color pixelColor = quadTree.getAverageColor(cx, cy, 1, 1);
                int diffR = Math.abs(pixelColor.getRed() - meanR);
                int diffG = Math.abs(pixelColor.getGreen() - meanG);
                int diffB = Math.abs(pixelColor.getBlue() - meanB);

                int pixelMax = Math.max(diffR, Math.max(diffG, diffB));
                maxDifference = Math.max(maxDifference, pixelMax);
            }
        }

        return maxDifference;
    }

    // metode entropy
    public static double calculateEntropy(QuadTree quadTree, int x, int y, int width, int height) {
        int[] histogram = new int[256];
        int total = 0;

        int endX = Math.min(x + width, quadTree.getWidth());
        int endY = Math.min(y + height, quadTree.getHeight());

        for (int cy = y; cy < endY; cy++) {
            for (int cx = x; cx < endX; cx++) {
                java.awt.Color color = quadTree.getAverageColor(cx, cy, 1, 1);
                int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                histogram[gray]++;
                total++;
            }
        }

        double entropy = 0.0;
        for (int i = 0; i < 256; i++) {
            if (histogram[i] > 0) {
                double p = (double) histogram[i] / total;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return entropy;
    }

    // metode structural similarity index (SSIM)
    public static double calculateSSIM(QuadTree quadTree, int x, int y, int width, int height) {
        int endX = Math.min(x + width, quadTree.getWidth());
        int endY = Math.min(y + height, quadTree.getHeight());
        int N = (endX - x) * (endY - y);

        if (N == 0) {
            return 1.0; // pixel 0
        }

        double[] origL = new double[N]; // original luminance
        double[] compL = new double[N]; // compressed luminance

        java.awt.Color avg = quadTree.getAverageColor(x, y, width, height);
        double avgLum = 0.299 * avg.getRed() + 0.587 * avg.getGreen() + 0.114 * avg.getBlue();
        // L = 0.299 * R + 0.587 * G + 0.114 * B

        int i = 0;
        for (int cy = y; cy < endY; cy++) {
            for (int cx = x; cx < endX; cx++) {
                java.awt.Color pix = quadTree.getAverageColor(cx, cy, 1, 1);
                double lum = 0.299 * pix.getRed() + 0.587 * pix.getGreen() + 0.114 * pix.getBlue();
                origL[i] = lum;
                compL[i] = avgLum;
                i++;
            }
        }

        return computeSSIM(origL, compL);
    }

    private static double computeSSIM(double[] x, double[] y) {
        int N = x.length;
        double C1 = 6.5025, C2 = 58.5225;
        // C1 = (K1*L)^2, C2 = (K2*L)^2
        // K1 = 0.01, K2 = 0.03, L = 255

        double meanX = 0, meanY = 0;
        for (int i = 0; i < N; i++) {
            meanX += x[i];
            meanY += y[i];
        }
        meanX /= N;
        meanY /= N;

        double varX = 0, varY = 0, covXY = 0;
        for (int i = 0; i < N; i++) {
            varX += Math.pow(x[i] - meanX, 2);
            varY += Math.pow(y[i] - meanY, 2);
            covXY += (x[i] - meanX) * (y[i] - meanY);
        }
        varX /= N;
        varY /= N;
        covXY /= N;

        double numerator = (2 * meanX * meanY + C1) * (2 * covXY + C2);
        double denominator = (meanX * meanX + meanY * meanY + C1) * (varX + varY + C2);

        return numerator / denominator;
    }

    public static QuadTree findOptimalParameters(BufferedImage originalImage, int errorMethod,
            double targetCompressionRate, String inputPath, String outputPath, String tempDirPath,
            String fileExtension) throws IOException {
        if (targetCompressionRate > 0 && targetCompressionRate <= 1) {
            errorMethod = 1;
        }

        double minThreshold = 0.0;
        double maxThreshold = 255 * 255; 

        int minBlockSizeStart = 1; 
        int maxBlockSize = 64; 

        double optimalThreshold = 0.0;
        int optimalMinBlockSize = minBlockSizeStart;
        double bestDifference = Double.MAX_VALUE;
        double epsilon = 0.001;

        int iterations = 0;

        while ((maxThreshold - minThreshold) > epsilon && iterations < 20) { 
            iterations++;
            double midThreshold = (minThreshold + maxThreshold) / 2;

            String debugImgPath = String.format("%s/temp_image%02d_t%.4f_bs%d%s",
                    tempDirPath, iterations, midThreshold, minBlockSizeStart, fileExtension);

            double actualCompressionRate = testCompressionRate(originalImage, errorMethod, midThreshold,
                    4, inputPath, debugImgPath);

            double difference = Math.abs(actualCompressionRate - targetCompressionRate);

            // System.out.println("  Iteration " + iterations + ": Threshold=" + String.format("%.4f", midThreshold) +
            //         ", BlockSize=" + minBlockSizeStart +
            //         ", Compression=" + String.format("%.2f", actualCompressionRate * 100) + "%, " +
            //         "Diff=" + String.format("%.4f", difference * 100) + "%" +
            //         ", Debug image: " + new File(debugImgPath).getName());

            if (difference < bestDifference) {
                bestDifference = difference;
                optimalThreshold = midThreshold;
            }

            if (difference < 0.01) {
                break;
            }

            if (actualCompressionRate > targetCompressionRate) {
                maxThreshold = midThreshold;
            } else {
                minThreshold = midThreshold;
            }
        }

        // System.out.println("Optimal threshold found: " + String.format("%.4f", optimalThreshold));

        // Reset best difference for block size search
        bestDifference = Double.MAX_VALUE;


        int minBlockSizeMin = minBlockSizeStart;
        int minBlockSizeMax = maxBlockSize;
        iterations = 0;

        while (minBlockSizeMin <= minBlockSizeMax && iterations < 10) { 
            iterations++;
            int midBlockSize = (minBlockSizeMin + minBlockSizeMax) / 2;

            String debugImgPath = String.format("%s/temp_imageB%02d_t%.4f_bs%d%s",
                    tempDirPath, iterations, optimalThreshold, midBlockSize, fileExtension);

            double actualCompressionRate = testCompressionRate(originalImage, errorMethod, optimalThreshold,
                    midBlockSize, inputPath, debugImgPath);

            double difference = Math.abs(actualCompressionRate - targetCompressionRate);

            // System.out.println("  Iteration " + iterations + ": BlockSize=" + midBlockSize +
            //         ", Threshold=" + String.format("%.4f", optimalThreshold) +
            //         ", Compression=" + String.format("%.2f", actualCompressionRate * 100) + "%, " +
            //         "Diff=" + String.format("%.4f", difference * 100) + "%" +
            //         ", Debug image: " + new File(debugImgPath).getName());

            if (difference < bestDifference) {
                bestDifference = difference;
                optimalMinBlockSize = midBlockSize;
            }

            if (difference < 0.01) {
                break;
            }

            if (actualCompressionRate > targetCompressionRate) {
                minBlockSizeMax = midBlockSize - 1;
            } else {
                minBlockSizeMin = midBlockSize + 1;
            }
        }

        return new QuadTree(originalImage, optimalMinBlockSize, optimalThreshold, errorMethod);
    }

    // Test rate dengan target
    private static double testCompressionRate(BufferedImage originalImage, int errorMethod, double threshold,
            int minBlockSize, String inputPath, String debugImgPath) throws IOException {
        QuadTree testTree = new QuadTree(originalImage, minBlockSize, threshold, errorMethod);
        BufferedImage compressedImage = testTree.generateCompressedImage();
        FileProcessor.saveImage(compressedImage, debugImgPath);

        long originalSize = FileProcessor.getFileSize(inputPath);
        long compressedSize = FileProcessor.getFileSize(debugImgPath);
        double compressionRate = 1.0 - ((double) compressedSize / originalSize);

        // int treeDepth = testTree.getTreeDepth();
        // int nodeCount = testTree.getNodeCount();

        // System.out.println("    Details: Depth=" + treeDepth +
        //         ", Nodes=" + nodeCount +
        //         ", Original=" + originalSize + "B" +
        //         ", Compressed=" + compressedSize + "B");

        return compressionRate;
    }
}
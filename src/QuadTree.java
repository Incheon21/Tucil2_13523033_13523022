import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.IOException;
import java.io.File;

public class QuadTree {
    BufferedImage originalImage;
    private QuadTreeNode root;
    private int minBlockSize;
    private double threshold;
    private int treeDepth;
    private int nodeCount;
    private int errorMethod;

    private int[][] red;
    private int[][] green;
    private int[][] blue;

    public QuadTree(BufferedImage image, int minBlockSize, double threshold, int errorMethod) {
        originalImage = image;
        this.minBlockSize = minBlockSize;
        this.threshold = threshold;
        this.treeDepth = 0;
        this.nodeCount = 1;
        this.errorMethod = errorMethod;
        root = new QuadTreeNode(0, 0, image.getWidth(), image.getHeight());

        this.red = new int[root.getHeight()][root.getWidth()];
        this.green = new int[root.getHeight()][root.getWidth()];
        this.blue = new int[root.getHeight()][root.getWidth()];

        for (int y = 0; y < root.getHeight(); y++) {
            for (int x = 0; x < root.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                red[y][x] = color.getRed();
                green[y][x] = color.getGreen();
                blue[y][x] = color.getBlue();
            }
        }

        root.setAvgColor(getAverageColor(0, 0, root.getWidth(), root.getHeight()));;

        buildTree(root, 0);
    }

    private void buildTree(QuadTreeNode node, int depth) {
        this.treeDepth = Math.max(treeDepth, depth);

        if (shouldSplit(node)) {
            splitNode(node, minBlockSize);

            for (QuadTreeNode child : node.getChildren()) {
                child.setAvgColor(getAverageColor(child.getX(), child.getY(), child.getWidth(), child.getHeight()));
                nodeCount++;
                buildTree(child, depth + 1);
            }
        }
    }

    // hitung warna rata-rata dri matrix red green blue
    public Color getAverageColor(int startX, int startY, int regionWidth, int regionHeight) {
        long sumR = 0, sumG = 0, sumB = 0;
        int count = 0;

        int endX = Math.min(startX + regionWidth, root.getWidth());
        int endY = Math.min(startY + regionHeight, root.getHeight());

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                sumR += red[y][x];
                sumG += green[y][x];
                sumB += blue[y][x];
                count++;
            }
        }

        if (count > 0) {
            int avgR = (int) (sumR / count);
            int avgG = (int) (sumG / count);
            int avgB = (int) (sumB / count);
            return new Color(avgR, avgG, avgB);
        } else {
            return Color.BLACK;
        }
    }

    public void splitNode(QuadTreeNode node, int minBlockSize) {
        QuadTreeNode[] children = new QuadTreeNode[4];

        int minDimension = (int) Math.ceil(Math.sqrt(minBlockSize));

        int newWidth1 = calculateSplitSize(node.getWidth(), minDimension);
        int newHeight1 = calculateSplitSize(node.getHeight(), minDimension);

        int newWidth2 = node.getWidth() - newWidth1;
        int newHeight2 = node.getHeight() - newHeight1;
        
        int x = node.getX();
        int y = node.getY();

        children[0] = new QuadTreeNode(x, y, newWidth1, newHeight1);
        children[1] = new QuadTreeNode(x + newWidth1, y, newWidth2, newHeight1);
        children[2] = new QuadTreeNode(x, y + newHeight1, newWidth1, newHeight2);
        children[3] = new QuadTreeNode(x + newWidth1, y + newHeight1, newWidth2, newHeight2);

        node.setChildren(children);
        node.setLeaf(false);
    }

    // Moved from QuadTreeNode
    private int calculateSplitSize(int totalSize, int minBlockSize) {
        int halfSize = totalSize / 2;

        if (totalSize <= minBlockSize) {
            return totalSize;
        }

        return halfSize;
    }

    // samain region/node dengan warna tertentu
    public void fillRegion(int startX, int startY, int regionWidth, int regionHeight, Color color) {
        int endX = Math.min(startX + regionWidth, root.getWidth());
        int endY = Math.min(startY + regionHeight, root.getHeight());

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                red[y][x] = color.getRed();
                green[y][x] = color.getGreen();
                blue[y][x] = color.getBlue();
            }
        }
    }

    // untuk cek apakah bisa dipecah?
    private boolean shouldSplit(QuadTreeNode node) {
        if ((node.getWidth() / 2 * node.getHeight() / 2) < minBlockSize) {
            return false;
        }

        double error = calculateError(node);

        return error > threshold;
    }

    // menghitung error untuk dibandingkan dengan threshold
    private double calculateError(QuadTreeNode node) {
        int x = node.getX();
        int y = node.getY();
        int width = node.getWidth();
        int height = node.getHeight();
        
        switch (errorMethod) {
            case 1:
                return Method.calculateVariance(this, x, y, width, height);
            case 2:
                return Method.calculateMAD(this, x, y, width, height);
            case 3:
                return Method.calculateMaxPixelDifference(this, x, y, width, height);
            case 4:
                return Method.calculateEntropy(this, x, y, width, height);
            case 5:
                double ssim = Method.calculateSSIM(this, x, y, width, height);
                return 1.0 - ssim; //1 - SSIM untuk mendapat error
            default:
                return Method.calculateVariance(this, x, y, width, height);
        }
    }

    // reconstruct hasil kompresi dengan matrix
    public BufferedImage generateCompressedImageAtDepth(int depth) {
        BufferedImage outputImage = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_RGB);

        this.red = new int[root.getHeight()][root.getWidth()];
        this.green = new int[root.getHeight()][root.getWidth()];
        this.blue = new int[root.getHeight()][root.getWidth()];

        drawQuadTreeAtDepth(root, 0, depth);

        for (int y = 0; y < root.getHeight(); y++) {
            for (int x = 0; x < root.getWidth(); x++) {
                Color color = new Color(red[y][x], green[y][x], blue[y][x]);
                outputImage.setRGB(x, y, color.getRGB());
            }
        }

        return outputImage;
    }
    // generate final compressed image
    public BufferedImage generateCompressedImage() {
        return generateCompressedImageAtDepth(treeDepth);
    }

    // mengisi region dengan warna average
    private void drawNode(QuadTreeNode node) {
        fillRegion(node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getAvgColor());
    }

    // memindahkan warna quadtree dengan kedalaman tertentu pada matrix 
    private void drawQuadTreeAtDepth(QuadTreeNode node, int currentDepth, int maxDepth) {
        if (currentDepth >= maxDepth || node.isLeaf()) {
            drawNode(node);
        } else {
            for (QuadTreeNode child : node.getChildren()) {
                drawQuadTreeAtDepth(child, currentDepth + 1, maxDepth);
            }
        }
    }


    public static void generateCompressionGif(QuadTree quadTree, String gifPath) throws IOException {
        int treeDepth = quadTree.getTreeDepth();

        BufferedImage[] frames = new BufferedImage[treeDepth + 1];
        for (int depth = 0; depth <= treeDepth; depth++) {
            frames[depth] = quadTree.generateCompressedImageAtDepth(depth);
        }
        
        createGif(frames, gifPath, 800); 
    }
    
    private static void createGif(BufferedImage[] frames, String outputPath, int delayMs) throws IOException {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("No frames provided for GIF creation");
        }
        
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (ImageOutputStream output = new FileImageOutputStream(outputFile)) {
            GifSequenceWriter writer = new GifSequenceWriter(output, frames[0].getType(), delayMs, true);
            
            for (BufferedImage frame : frames) {
                writer.writeToSequence(frame);
            }
            
            writer.close();
        }
    }

    // Menghitung persentase kompresi
    public double calculateCompressionPercentage(long originalSize, long compressedSize) {
        return (1.0 - ((double) compressedSize / originalSize)) * 100.0;
    }
    
    public int getTreeDepth() {
        return treeDepth;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getWidth() {
        return root.getWidth();
    }

    public int getHeight() {
        return root.getHeight();
    }

}
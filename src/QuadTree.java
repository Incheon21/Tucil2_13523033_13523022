import java.awt.image.BufferedImage;

public class QuadTree {
    private QuadTreeNode root;
    private int minBlockSize;
    private double threshold;
    private int treeDepth;
    private int nodeCount;
    private Matrix imageMatrix;

    public QuadTree(BufferedImage image, int minBlockSize, double threshold, int errorMethod) {
        this.minBlockSize = minBlockSize;
        this.threshold = threshold;
        this.treeDepth = 0;
        this.nodeCount = 1;
        
        this.imageMatrix = Matrix.fromImage(image);

        root = new QuadTreeNode(0, 0, imageMatrix.getWidth(), imageMatrix.getHeight());
        root.calculateAvgColor(imageMatrix);

        buildTree(root, 0);
    }

    private void buildTree(QuadTreeNode node, int depth) {
        this.treeDepth = Math.max(treeDepth, depth);
    
        if (shouldSplit(node)) {
            node.split(minBlockSize);
    
            for (QuadTreeNode child : node.getChildren()) {
                child.calculateAvgColor(imageMatrix);
                nodeCount++;
                buildTree(child, depth + 1);
            }
        }
    }

    // untuk cek apakah bisa dipecah?
    private boolean shouldSplit(QuadTreeNode node) {
        if ((node.getWidth()/2  *  node.getHeight()/2) < minBlockSize) {
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
        
        return Method.calculateVariance(imageMatrix, x, y, width, height);
    }

    // reconstruct hasil kompresi dengan matrix
    public BufferedImage generateCompressedImage() {
        Matrix outputMatrix = new Matrix(imageMatrix.getWidth(), imageMatrix.getHeight());
        
        drawQuadTree(root, outputMatrix);
        
        return outputMatrix.toImage();
    }

    // mengisi region dengan warna average
    private void drawNode(QuadTreeNode node, Matrix matrix) {
        matrix.fillRegion(node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getAvgColor());
    }

    // memindahkan warna quadtree pada matrix
    private void drawQuadTree(QuadTreeNode node, Matrix matrix) {
        if (node.isLeaf()) {
            drawNode(node, matrix);
        } else {
            for (QuadTreeNode child : node.getChildren()) {
                drawQuadTree(child, matrix);
            }
        }
    }

    public int getTreeDepth() {
        return treeDepth;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    // Menghitung persentase kompresi
    public double calculateCompressionPercentage(long originalSize, long compressedSize) {
        return (1.0 - ((double) compressedSize / originalSize)) * 100.0;
    }
}
import java.awt.Color;

public class QuadTreeNode {
    private int x, y;
    private int width, height;
    private Color avgColor; 
    private QuadTreeNode[] children; 
    private boolean isLeaf;

    public QuadTreeNode(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isLeaf = true;
        this.children = null;
        this.avgColor = Color.BLACK;
    }

    // membagi node menjadi 4 bagian
    public void split(int minBlockSize) {
        children = new QuadTreeNode[4];

        System.out.println("--- Split Debug Info ---");
        System.out.println("Total width: " + width + ", Total height: " + height);
        System.out.println("Minimum block size (area): " + minBlockSize);

        int minDimension = (int) Math.ceil(Math.sqrt(minBlockSize));

        int newWidth1 = calculateSplitSize(width, minDimension);
        int newHeight1 = calculateSplitSize(height, minDimension);

        System.out.println("Width split result: " + newWidth1 + " and " + (width - newWidth1));
        System.out.println("Height split result: " + newHeight1 + " and " + (height - newHeight1));

        int newWidth2 = width - newWidth1;
        int newHeight2 = height - newHeight1;

        children[0] = new QuadTreeNode(x, y, newWidth1, newHeight1);
        children[1] = new QuadTreeNode(x + newWidth1, y, newWidth2, newHeight1);
        children[2] = new QuadTreeNode(x, y + newHeight1, newWidth1, newHeight2);
        children[3] = new QuadTreeNode(x + newWidth1, y + newHeight1, newWidth2, newHeight2);

        isLeaf = false;
    }

    // menghitung ukuran empat bagian untuk pembagian
    private int calculateSplitSize(int totalSize, int minBlockSize) {
        int halfSize = totalSize / 2;

        if (totalSize <= minBlockSize) {
            return totalSize;
        }

        return halfSize;
    }

    // menghhitung warna average untuk sebuah region/node
    public void calculateAvgColor(Matrix matrix) {
        this.avgColor = matrix.getAverageColor(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getAvgColor() {
        return avgColor;
    }

    public void setAvgColor(Color avgColor) {
        this.avgColor = avgColor != null ? avgColor : Color.BLACK;
    }

    public QuadTreeNode[] getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
}
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

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public void setChildren(QuadTreeNode[] children) {
        this.children = children;
    }
}
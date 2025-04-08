public class Method {
  
  // metode variance
  public static double calculateVariance(Matrix matrix, int x, int y, int width, int height) {
    java.awt.Color avgColor = matrix.getAverageColor(x, y, width, height);
    double meanR = avgColor.getRed();
    double meanG = avgColor.getGreen();
    double meanB = avgColor.getBlue();
    
    double varR = 0, varG = 0, varB = 0;
    int count = 0;
    
    int endX = Math.min(x + width, matrix.getWidth());
    int endY = Math.min(y + height, matrix.getHeight());
    
    for (int cy = y; cy < endY; cy++) {
      for (int cx = x; cx < endX; cx++) {
        java.awt.Color pixelColor = matrix.getAverageColor(cx, cy, 1, 1); // Get single pixel color
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
}
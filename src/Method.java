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
  public static double calculateMAD(QuadTree quadTree, int x, int y, int width, int height){
    java.awt.Color avgColor = quadTree.getAverageColor(x, y, width, height);
    double meanR = avgColor.getRed();
    double meanG = avgColor.getGreen();
    double meanB = avgColor.getBlue();

    double madR = 0, madG = 0, madB = 0;
    int count = 0;

    int endX = Math.min(x + width, quadTree.getWidth());
    int endY = Math.min(y + height, quadTree.getHeight());

    for (int cy =y; cy < endY; cy++) {
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
  public static double calculateMaxPixelDifference(QuadTree quadTree, int x, int y, int width, int height){
    
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
  
  
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitmap.demo;

/**
 *
 * @author kmhasan
 */
public class BitmapDemo {

    /*
    HW:
    1. Write methods to decompose a bitmap into 3 channels and write them into separate files
    2. Write a method that keeps the red parts red and everything else gray
    3. Write methods to flip (horizontally/vertically) and rotate
    4. Write methods to increase/decrease the brightness and contrast
    */
    
    /*
    HW: read about PDF (probability density functions), conditional probability & variance
    */
    public BitmapDemo() {
        Bitmap bitmap = new Bitmap("fly.bmp");
        /*
        int histogram[] = bitmap.grayscaleHistogram();
        for (int i = 0; i < histogram.length; i++)
            System.out.printf("%3d: %d\n", i, histogram[i]);
        */
        //bitmap.setToGray(0, 0, (int) (bitmap.getHeight() / 2 - 1), (int) (bitmap.getWidth() - 1));
        //bitmap.rotate90Clockwise();
//        bitmap.scale(2);
        //bitmap.add(100);
//        bitmap.write("copy.bmp");
        int t = bitmap.getThresholdValue();
        long startTime = System.currentTimeMillis();
        bitmap.applyThreshold(t);
        long stopTime = System.currentTimeMillis();
        System.out.println("Time spent: " + (stopTime - startTime) / 1000.0);
        bitmap.write("copy.bmp");
        System.out.println("Threshold: " + t);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new BitmapDemo();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floodfill.demo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author kmhasan
 */
public class FloodFillDemo {

    private Mat grayscaleMat;
    private final static double EPS = 1E-5;
    private int label[][];
    private int currentLabel = 0;
    
    public FloodFillDemo() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat frame1 = Imgcodecs.imread("frame1.png");
        Mat frame2 = Imgcodecs.imread("frame2.png");
        // apply gaussian filter on both the frames to cancel out noise
        grayscaleMat = frame1.clone();
        System.out.println("Height " + frame1.height());
        System.out.println("Width " + frame1.width());
        calculateDifference(frame1, frame2, grayscaleMat);
        Imgcodecs.imwrite("difference.png", grayscaleMat);
        process(0);
        System.out.println("Different labels: " + currentLabel);
    }
    
    private void calculateDifference(Mat m1, Mat m2, Mat dest) {
        for (int r = 0; r < m1.height(); r++)
            for (int c = 0; c < m2.width(); c++) {
                double color1[] = m1.get(r, c);
                double color2[] = m2.get(r, c);
                double color[] = new double[color1.length];
                for (int k = 0; k < color1.length; k++) {
                    color[k] = Math.abs(color1[k] - color2[k]);
                }
                dest.put(r, c, color);
            }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FloodFillDemo();
    }

    private void process(int channel) {
        label = new int[grayscaleMat.height()][grayscaleMat.width()];
        for (int r = 0; r < grayscaleMat.height(); r++)
            for (int c = 0; c < grayscaleMat.width(); c++) {
                double color[] = grayscaleMat.get(r, c);
                if (Math.abs(color[channel]) > EPS && label[r][c] == 0) {
                    currentLabel++;
                    floodFill(r, c, channel, currentLabel);
                }
            }
    }

    private void floodFill(int r, int c, int channel, int currentLabel) {
        if (r < 0 || c < 0 || r >= grayscaleMat.height() || c >= grayscaleMat.width())
            return;
        
        double color[] = grayscaleMat.get(r, c);
        if (Math.abs(color[channel]) > EPS && label[r][c] == 0) {
            label[r][c] = currentLabel;
            floodFill(r, c + 1, channel, currentLabel);
            floodFill(r, c - 1, channel, currentLabel);
            floodFill(r - 1, c, channel, currentLabel);
            floodFill(r + 1, c, channel, currentLabel);
        } else {
            return;
        }
    }
    
}

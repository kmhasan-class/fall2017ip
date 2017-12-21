/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floodfill.demo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javafx.util.Pair;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author kmhasan
 */
public class FloodFillDemo {

    private Mat grayscaleMat;
    private Mat frame1;
    private Mat frame2;
    private Mat originalMat;
    private final static double EPS = 10;
    private int label[][];
    private int currentLabel = 0;
    private int counts[];
    
    public FloodFillDemo() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        frame1 = Imgcodecs.imread("frame1.png");
        frame2 = Imgcodecs.imread("frame2.png");

        grayscaleMat = frame1.clone();
        originalMat = frame1.clone();
        
        // apply gaussian filter on both the frames to cancel out noise
        Imgproc.blur(frame1, frame1, new Size(5.0, 5.0));
        Imgproc.blur(frame2, frame2, new Size(5.0, 5.0));
        
        System.out.println("Height " + frame1.height());
        System.out.println("Width " + frame1.width());
        calculateDifference(frame1, frame2, grayscaleMat);
        Imgcodecs.imwrite("difference.png", grayscaleMat);
        
        // since we use the result from one process in another
        // at the end of the last process (here process(2))
        // we will have the final output (including all three channels)
        // written in output2.png
        process(0);
        process(1);
        process(2);
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
        // In the worst possible scenario we can have a checkerboard pattern where no two
        // pixels are connected. There can be half the total number of pixels in such an image
        counts = new int[(grayscaleMat.height() * grayscaleMat.width()) / 2];
        
        // the label 2d array will hold the individual labels for each of the pixels
        label = new int[grayscaleMat.height()][grayscaleMat.width()];
        
        // for each pixel in the "difference" image
        for (int r = 0; r < grayscaleMat.height(); r++)
            for (int c = 0; c < grayscaleMat.width(); c++) {
                // get the current color of the pixel
                double color[] = grayscaleMat.get(r, c);
                // if the difference is significant and the pixel is unvisited
                if (Math.abs(color[channel]) > EPS && label[r][c] == 0) {
                    // increase the count of different components
                    currentLabel++;
                    // start a floodfill algorithm from that pixel
                    floodFill(r, c, channel, currentLabel);
                }
            }
        
        // bestLabel will contain the label for the component with the highest number of pixels
        int bestLabel = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > counts[bestLabel])
                bestLabel = i;
        }

        // for each pixel in the original image
        for (int r = 0; r < originalMat.height(); r++)
            for (int c = 0; c < originalMat.width(); c++) {
                double color[] = originalMat.get(r, c);
                // if it belongs to the largest component
                if (label[r][c] == bestLabel) {
                    // scale up the intensity of that channel by a factor of 2
                    // but cap it to 255 (meaning it will not exceed 255
                    color[channel] = Math.min(255, 2 * color[channel]);
                } else {
                    // otherwise scale down the intensity of that channel by a factor of 0.5
                    color[channel] *= 0.5;
                }
                originalMat.put(r, c, color);
            }

        // write an output for this channel
        Imgcodecs.imwrite("output" + channel + ".png", originalMat);
    }

    /**
     * BFS implementation of flood fill algorithm.
     * @param r row to start from
     * @param c column to start from
     * @param channel the color channel
     * @param currentLabel the label that should be set to this pixel
     */
    private void floodFill(int r, int c, int channel, int currentLabel) {
        // Pair (r, c) represents the starting pixel
        Pair<Integer, Integer> source = new Pair(r, c);

        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        // we add the starting pixel to the queue
        queue.add(source);
        
        // while there are still unvisited pixels
        while (!queue.isEmpty()) {
            // pop from the head of the queue
            Pair<Integer, Integer> u = queue.poll();
            r = u.getKey();
            c = u.getValue();
            
            // if this pixel is outside the image boundary, or if it is already visited, ignore it
            if (r < 0 || c < 0 || r >= grayscaleMat.height() || c >= grayscaleMat.width() || label[r][c] > 0)
                continue;
            double color[] = grayscaleMat.get(r, c);
            // if the color difference of this pixel is insignificant, ignore it
            if (color[channel] < EPS)
                continue;
            
            // mark the pixel with the current label and increase the total count of such pixels
            label[r][c] = currentLabel;
            counts[currentLabel]++;
            
            // push all the neighboring pixels into the queue
            queue.add(new Pair(r, c + 1));
            queue.add(new Pair(r, c - 1));
            queue.add(new Pair(r - 1, c));
            queue.add(new Pair(r + 1, c));
        }
    }
    
}

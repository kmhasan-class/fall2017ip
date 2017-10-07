/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixel.demo;

import java.util.Arrays;

/**
 *
 * @author kmhasan
 */
public class PixelDemo {

    int[] decode(int pixel) {
        int components[] = new int[3];
        components[0] = pixel & 0xFF;
        components[1] = (pixel & 0xFF00) >> 8;
        components[2] = (pixel & 0xFF0000) >> 16;
        // returning as BGR
        return components;
    }
    
    int encode(int components[]) {
        /*
        HW1: Write the encode method that takes B8G8R8 and converts that to a 
        single integer
        */
        return 0;
    }
    
    public PixelDemo() {
        int components[] = decode(16737430);
        System.out.println(Arrays.toString(components));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new PixelDemo();
    }
    
    /*
    HW2: access the pixel data from a bitmap file
    Reference: https://en.wikipedia.org/wiki/BMP_file_format
    */
}

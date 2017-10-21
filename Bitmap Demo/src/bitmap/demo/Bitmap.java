/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitmap.demo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kmhasan
 */
public class Bitmap {

    private String filename;
    private byte buffer[];
    private long fileSize;
    private long width;
    private long height;
    private long imageDataOffset;

    private long byteArrayToLong(byte byteArray[]) {
        long value = 0;
        for (int i = byteArray.length - 1; i >= 0; i--) {
            int x = byteArray[i] & 0xFF;
            System.out.printf("index %d value %d\n", i, x);
            value = value << 8;
            value = value | x;
        }
        return value;
    }

    public void read() {
        try {
            RandomAccessFile input = new RandomAccessFile(filename, "r");

            byte byteArray[];

            input.seek(0);
            buffer = new byte[(int) input.length()];
            input.read(buffer);

            // reading the signature (offset 0, size 2)
            input.seek(0);
            byteArray = new byte[2];
            System.out.printf("Read %d bytes\n", input.read(byteArray));
            System.out.printf("Contents %s\n", new String(byteArray));

            // reading the filesize (offset 2, size 4)
            input.seek(2);
            byteArray = new byte[4];
            System.out.printf("Read %d bytes\n", input.read(byteArray));
            System.out.printf("Contents %s\n", Arrays.toString(byteArray));
            fileSize = byteArrayToLong(byteArray);
            System.out.printf("Filesize: %d\n", fileSize);

            // read the width (offset 18, size 2)
            input.seek(18);
            byteArray = new byte[2];
            System.out.printf("Read %d bytes\n", input.read(byteArray));
            System.out.printf("Contents %s\n", Arrays.toString(byteArray));
            width = byteArrayToLong(byteArray);
            System.out.printf("Width: %d\n", width);

            // read the width (offset 18, size 2)
            input.seek(22);
            byteArray = new byte[2];
            System.out.printf("Read %d bytes\n", input.read(byteArray));
            System.out.printf("Contents %s\n", Arrays.toString(byteArray));
            height = byteArrayToLong(byteArray);
            System.out.printf("Width: %d\n", height);

            // read the pixel data offset (offset 10, size 4)
            input.seek(10);
            byteArray = new byte[4];
            System.out.printf("Read %d bytes\n", input.read(byteArray));
            System.out.printf("Contents %s\n", Arrays.toString(byteArray));
            imageDataOffset = byteArrayToLong(byteArray);
            System.out.printf("Image data offset: %d\n", imageDataOffset);

            input.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Bitmap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Bitmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(String filename) {
        try (RandomAccessFile output = new RandomAccessFile(filename, "rw")) {
            output.setLength(0);
            output.write(buffer);
            output.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Bitmap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Bitmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setToBlack(int startRow, int startCol, int endRow, int endCol) {
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
//                int b = buffer[index + 0] & 0xFF;
//                int g = buffer[index + 1] & 0xFF;
//                int r = buffer[index + 2] & 0xFF;
                buffer[index + 0] = 0;
                buffer[index + 1] = 0;
                buffer[index + 2] = 0;
            }
        }
    }

    public void setToGray(int startRow, int startCol, int endRow, int endCol) {
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
                int b = buffer[index + 0] & 0xFF;
                int g = buffer[index + 1] & 0xFF;
                int r = buffer[index + 2] & 0xFF;
                //int gray = (r + g + b) / 3; // average method
                int gray = (int) (0.21 * r + 0.72 * g + 0.07 * b); // luminosity method
                buffer[index + 0] = (byte) gray;
                buffer[index + 1] = (byte) gray;
                buffer[index + 2] = (byte) gray;
            }
        }
    }

    public String getFilename() {
        return filename;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    // amount must be in the range [0, 255]
    public void add(int amount) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);
                for (int i = 0; i < 3; i++) {
                    int color = buffer[inputIndex + i] & 0xFF;
                    int newColor = color + amount;
                    if (newColor < 0) {
                        newColor = 0;
                    }
                    if (newColor > 255) {
                        newColor = 255;
                    }
                    buffer[inputIndex + i] = (byte) newColor;
                }
            }
        }
    }

    // final output must be in the range [0, 255]
    public void scale(double amount) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);
                for (int i = 0; i < 3; i++) {
                    int color = buffer[inputIndex + i] & 0xFF;
                    int newColor = (int) (color * amount);
                    if (newColor < 0) {
                        newColor = 0;
                    }
                    if (newColor > 255) {
                        newColor = 255;
                    }
                    buffer[inputIndex + i] = (byte) newColor;
                }
            }
        }
    }

    /*
    Need to fix this rotate method
    Gives some slanted output, not a proper 90 degress rotation
     */
    public void rotate90Clockwise() {
        long outputWidth = height;
        long outputHeight = width;

        byte outputBuffer[] = new byte[buffer.length];
        System.arraycopy(buffer, 0, outputBuffer, 0, buffer.length);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);

                int outputCol = row;
                int outputRow = (int) width - 1 - col;
                int outputIndex = (int) (imageDataOffset + (outputRow * outputWidth + outputCol) * 3);

                for (int i = 0; i < 3; i++) {
                    outputBuffer[outputIndex + i] = buffer[inputIndex + i];
                }
            }
        }

        System.arraycopy(outputBuffer, 0, buffer, 0, buffer.length);
        updateWidth(outputWidth);
        updateHeight(outputHeight);
    }

    public int[] grayscaleHistogram() {
        int counter[] = new int[256];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
                int b = buffer[index + 0] & 0xFF;
                int g = buffer[index + 1] & 0xFF;
                int r = buffer[index + 2] & 0xFF;
                //int gray = (r + g + b) / 3;
                int gray = (int) (0.21 * r + 0.72 * g + 0.07 * b); // luminosity method
                counter[gray]++;
            }
        }
        return counter;
    }

    public void updateWidth(long newWidth) {
        width = newWidth;
        byte lastByte = (byte) (width & 0xFF);
        byte firstByte = (byte) ((width & 0xFF00) >> 8);
        buffer[18] = lastByte;
        buffer[19] = firstByte;
    }

    public void updateHeight(long newHeight) {
        height = newHeight;
        byte lastByte = (byte) (height & 0xFF);
        byte firstByte = (byte) ((height & 0xFF00) >> 8);
        buffer[22] = lastByte;
        buffer[23] = firstByte;
    }

    // Homework: modify the method to do the thresholding on a particular component (R, G, B)
    // or the grayscale. Then somehow combine the output to produce a good result for
    // colored images
    public int getThresholdValue() {
        // Algorithm: Basic Global Thresholding
        int histogram[] = grayscaleHistogram();
        //setToGray(0, 0, (int) height, (int) width);
        int diff;
        int t1 = 127; // initial guess for threshold

        do {
            int m1 = 0; // average intensity of all the pixels in group 1
            int m2 = 0; // average intensity of all the pixels in group 2

            long g1sum = 0; // the average intensity of all the pixels in group 1
            long g1count = 0; // the number of pixels in group 1

            long g2sum = 0; // the average intensity of all the pixels in group 2
            long g2count = 0; // the number of pixels in group 2
            
            // Homework: see if you can improve the run time of this algorithm
            // by using two additional 256 element array
            
            for (int i = 0; i <= t1; i++) {
                g1sum = g1sum + i * histogram[i];
                g1count = g1count + histogram[i];
            }
            for (int i = t1 + 1; i < histogram.length; i++) {
                g2sum = g2sum + i * histogram[i];
                g2count = g2count + histogram[i];
            }
/*
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int index = (int) (imageDataOffset + (row * width + col) * 3);
                    int color = buffer[index + 0] & 0xFF;

                    if (color > t1) {
                        // this pixel belongs to group 2
                        g2sum = g2sum + color;
                        g2count = g2count + 1;
                    } else {
                        // this pixel belongs to group 1
                        g1sum = g1sum + color;
                        g1count = g1count + 1;
                    }
                }
            }
*/
            m1 = (int) (g1sum / g1count);
            m2 = (int) (g2sum / g2count);

            int t2 = (m1 + m2) / 2;
            diff = Math.abs(t1 - t2);
            System.out.printf("t1: %d, m1: %d, m2: %d, t2: %d\n", t1, m1, m2, t2);
            t1 = t2;
        } while (diff > 1);
        return t1;
    }

    public void applyThreshold(int t) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
                int color = buffer[index + 0] & 0xFF;
                for (int i = 0; i < 3; i++) {
                    if (color > t) {
                        buffer[index + i] = (byte) 255;
                    } else {
                        buffer[index + i] = 0;
                    }
                }
            }
        }
    }

    public Bitmap(String filename) {
        this.filename = filename;
        read();
    }

}

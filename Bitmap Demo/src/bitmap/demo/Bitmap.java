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
        for (int row = startRow; row <= endRow; row++)
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
    
    public void setToGray(int startRow, int startCol, int endRow, int endCol) {
        for (int row = startRow; row <= endRow; row++)
            for (int col = startCol; col <= endCol; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
                int b = buffer[index + 0] & 0xFF;
                int g = buffer[index + 1] & 0xFF;
                int r = buffer[index + 2] & 0xFF;
                int gray = (r + g + b) / 3;
                buffer[index + 0] = (byte) gray;
                buffer[index + 1] = (byte) gray;
                buffer[index + 2] = (byte) gray;
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
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);
                for (int i = 0; i < 3; i++) {
                    int color = buffer[inputIndex + i] & 0xFF;
                    int newColor = color + amount;
                    if (newColor < 0)
                        newColor = 0;
                    if (newColor > 255)
                        newColor = 255;
                    buffer[inputIndex + i] = (byte) newColor;
                }
            }
    }

    // final output must be in the range [0, 255]
    public void scale(double amount) {
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);
                for (int i = 0; i < 3; i++) {
                    int color = buffer[inputIndex + i] & 0xFF;
                    int newColor = (int) (color * amount);
                    if (newColor < 0)
                        newColor = 0;
                    if (newColor > 255)
                        newColor = 255;
                    buffer[inputIndex + i] = (byte) newColor;
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
        
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int inputIndex = (int) (imageDataOffset + (row * width + col) * 3);
                
                int outputCol = row;
                int outputRow = (int) width - 1 - col;
                int outputIndex = (int) (imageDataOffset + (outputRow * outputWidth + outputCol) * 3);
                
                for (int i = 0; i < 3; i++)
                    outputBuffer[outputIndex + i] = buffer[inputIndex + i];
            }
        
        System.arraycopy(outputBuffer, 0, buffer, 0, buffer.length);
        updateWidth(outputWidth);
        updateHeight(outputHeight);
    }
    
    public int[] grayscaleHistogram() {
        int counter[] = new int[256];
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int index = (int) (imageDataOffset + (row * width + col) * 3);
                int b = buffer[index + 0] & 0xFF;
                int g = buffer[index + 1] & 0xFF;
                int r = buffer[index + 2] & 0xFF;
                int gray = (r + g + b) / 3;
                counter[gray]++;
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
    
    public Bitmap(String filename) {
        this.filename = filename;
        read();
    }

}

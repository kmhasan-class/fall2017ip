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
    
    
    
    public Bitmap(String filename) {
        this.filename = filename;
        read();
    }

}

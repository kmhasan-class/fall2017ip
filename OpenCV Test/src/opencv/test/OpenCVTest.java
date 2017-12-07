/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv.test;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author kmhasan
 */
public class OpenCVTest {

    private void processStillImage() {
        Mat mat = Imgcodecs.imread("fly.bmp");
        for (int c = 0; c < mat.width() / 2; c++) {
            for (int r = 0; r < mat.height() / 2; r++) {
                double color[] = mat.get(r, c);
                color[0] = 255;
                mat.put(r, c, color);
                //System.out.printf("(%d, %d) = %s\n", r, c, Arrays.toString(color));
            }
        }
        Imgcodecs.imwrite("fly_new.bmp", mat);

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite("fly_gray.bmp", gray);
    }

    private void processLiveImageFromWebcam() {
        VideoCapture videoCapture = new VideoCapture();
        System.out.println("Capture device open: " + videoCapture.open(0));
        Mat frame = new Mat();

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        if (videoCapture.isOpened()) {
            System.out.println("Camera is on");
            service.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    videoCapture.read(frame);
                    String suffix = LocalTime.now().toString();
                    Imgcodecs.imwrite("frame" + suffix + ".png", frame);
                }
            }, 0, 33, TimeUnit.MILLISECONDS);
            try {
                service.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(OpenCVTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("Camera is off");
        }
        service.shutdown();
        videoCapture.release();
    }

    public OpenCVTest() {
        //processStillImage();
        processLiveImageFromWebcam();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new OpenCVTest();
    }
}

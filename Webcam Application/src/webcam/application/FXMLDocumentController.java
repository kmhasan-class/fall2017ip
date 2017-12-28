/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webcam.application;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author kmhasan
 */
public class FXMLDocumentController implements Initializable {

    public static VideoCapture getVideoCapture() {
        return videoCapture;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
    
    @FXML
    private Label statusLabel;
    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView modifiedImageView;
    private static VideoCapture videoCapture;
    
    private int blurSize = 3;
    private double tl = 10;
    private double th = 30;
    
    private static ScheduledExecutorService scheduledExecutorService;
    
    private Mat currentFrame;
    private Mat modifiedFrame;
    private Image image;
    private MatOfByte matOfByte;
    private double gxKernel[][] = 
        {
            {-1, 0, +1},
            {-2, 0, +2},
            {-1, 0, +1}
        };
    @FXML
    private TextField blurField;
    @FXML
    private TextField tlField;
    @FXML
    private TextField thField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        startCamera();
    }    

    private void startCamera() {
        currentFrame = new Mat();
        modifiedFrame = new Mat();
        matOfByte = new MatOfByte();

        videoCapture = new VideoCapture();
        videoCapture.open(0);
        
        startFrameGrabbing();
    }

    private void convolute(Mat mat, double[][] kernel) {
        
    }
    
    private void grabFrame() {
        videoCapture.read(currentFrame);
        Imgcodecs.imencode(".png", currentFrame, matOfByte);
        image = new Image(new ByteArrayInputStream(matOfByte.toArray()));
        Platform.runLater(() -> originalImageView.setImage(image));
        // Convert the current RGB frame to Grayscale
        // and show it on the modified frame
        
        Imgproc.cvtColor(currentFrame, modifiedFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(modifiedFrame, modifiedFrame, new Size(blurSize, blurSize));
        Imgproc.Canny(modifiedFrame, modifiedFrame, tl, th, 3, true);
        //convolute(modifiedFrame, gxKernel);
        //Imgproc.threshold(modifiedFrame, modifiedFrame, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        Imgcodecs.imencode(".png", modifiedFrame, matOfByte);
        image = new Image(new ByteArrayInputStream(matOfByte.toArray()));
        Platform.runLater(() -> modifiedImageView.setImage(image));
        
    }

    private void startFrameGrabbing() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> grabFrame(), 0, 33, TimeUnit.MILLISECONDS);
    }

    @FXML
    private void handleBlurSizeChangeAction(ActionEvent event) {
        blurSize = Integer.parseInt(blurField.getText());
    }

    @FXML
    private void handleTLChangeAction(ActionEvent event) {
        tl = Double.parseDouble(tlField.getText());
    }

    @FXML
    private void handleTHChangeAction(ActionEvent event) {
        th = Double.parseDouble(thField.getText());
    }
    
}

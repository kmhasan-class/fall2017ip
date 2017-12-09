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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
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
    
    private static ScheduledExecutorService scheduledExecutorService;
    
    private Mat currentFrame;
    private Mat modifiedFrame;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        startCamera();
    }    

    private void startCamera() {
        currentFrame = new Mat();
        modifiedFrame = new Mat();
        videoCapture = new VideoCapture();
        videoCapture.open(0);
        
        startFrameGrabbing();
    }

    private void grabFrame() {
        videoCapture.read(currentFrame);
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", currentFrame, matOfByte);
        Image image = new Image(new ByteArrayInputStream(matOfByte.toArray()));
        Platform.runLater(() -> originalImageView.setImage(image));
        // Convert the current RGB frame to Grayscale
        // and show it on the modified frame
    }

    private void startFrameGrabbing() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> grabFrame(), 0, 33, TimeUnit.MILLISECONDS);
    }
    
}

package com.lungesoft.cvtask;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 * Created by Vadim on 2017-03-16.
 */
public class HaarFaceDetector {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat frame = Imgcodecs.imread("faceInput.jpg");

        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        int height = grayFrame.rows();
        int absoluteFaceSize = Math.round(height * 0.2f);

        CascadeClassifier faceCascade  = new CascadeClassifier();
        faceCascade.load("haarcascade_frontalface_alt.xml");
        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
        }

        Imgcodecs.imwrite("face.jpg", frame);


    }
}

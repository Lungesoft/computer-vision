package com.lungesoft.cvtask;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class GaussianThirdTask {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat rgbInput = Imgcodecs.imread("input.jpg");
        Mat labProcessing = new Mat();
        Imgproc.GaussianBlur(rgbInput, labProcessing, new Size(3,3), 3);
        Mat mask = new Mat();
        Core.subtract(rgbInput, labProcessing, mask);
        Mat output = new Mat();
        Core.add(rgbInput, mask, output);
        Imgcodecs.imwrite("Output2.jpg", output);
    }
}
package com.lungesoft.cvtask;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Vadim on 2017-03-16.
 */
public class CannyFilter {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat imgSource = Imgcodecs.imread("input.jpg");

        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        Imgproc.cvtColor(imgSource, grayImage, Imgproc.COLOR_RGB2GRAY);

        // reduce noise with a 3x3 kernel
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        // canny detector, with ratio of lower:upper threshold of 3:1
        Imgproc.Canny(detectedEdges, detectedEdges, 10, 100);

        Mat dest = new Mat();
        dest.copyTo(dest, detectedEdges);
        Imgcodecs.imwrite("Canny.jpg", dest);

    }
}

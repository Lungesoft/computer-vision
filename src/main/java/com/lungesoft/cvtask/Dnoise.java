package com.lungesoft.cvtask;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

public class Dnoise {

    public static void main(String[] args) {
        {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Mat source = Imgcodecs.imread("oldPicture.jpg");
            Photo.fastNlMeansDenoisingColored(source,source, 10, 10, 7, 21);
            Imgcodecs.imwrite("Output2.jpg", source);
        }
    }

}
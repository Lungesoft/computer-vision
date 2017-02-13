package com.lungesoft.cvtask;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

import static org.opencv.imgproc.Imgproc.COLOR_Lab2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2Lab;

/**
 * Created by Vadim on 2017-01-26.
 */
public class ContrastFirstTask {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat rgbInput = Imgcodecs.imread("input.jpg");
        Mat labProcessing = new Mat();

        Imgproc.cvtColor(rgbInput, labProcessing, COLOR_RGB2Lab );
        int rows = labProcessing.rows();
        int cols = labProcessing.cols();
        //int ch = labProcessing.channels();
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                double[] data = labProcessing.get(i, j);
               // System.out.println(Arrays.toString(data));
                data[2] = (data[2]-128) * 1.25 + 128;
                data[1] = (data[1]-128) * 1.25 + 128;
                labProcessing.put(i, j, data);
            }
        }

        Mat rgbOutpet = new Mat();
        Imgproc.cvtColor(labProcessing, rgbOutpet, COLOR_Lab2RGB );
        Imgcodecs.imwrite("Output.jpg", rgbOutpet);
    }
}

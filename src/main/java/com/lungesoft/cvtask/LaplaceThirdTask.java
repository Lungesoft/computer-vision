package com.lungesoft.cvtask;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.COLOR_Lab2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2Lab;

public class LaplaceThirdTask {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat rgbInput = Imgcodecs.imread("input.jpg");
        Mat labProcessing = new Mat();
        Imgproc.Laplacian(rgbInput, labProcessing, 5, 3,  1, 0);
        Imgcodecs.imwrite("Output.jpg", labProcessing);

        //strokeEffect
        //not need for the task
//        labProcessing = Imgcodecs.imread("Output.jpg");
//        Mat mask = contractChange(2, labProcessing);
//        blackWhiteTranslate(mask);
//        colorInvert(mask);
//        overlayMask(mask, rgbInput);
//        Imgcodecs.imwrite("Output.jpg", rgbInput);
    }


    private static Mat contractChange(int factor, Mat input) {
        Mat temp1 = new Mat();
        Mat temp = new Mat();
        Imgproc.cvtColor(input, temp, COLOR_RGB2Lab );
        for (int i=0; i<temp.rows(); i++) {
            for (int j=0; j<temp.cols(); j++) {
                double[] data = temp.get(i, j);
                data[2] = (data[2]-128) * factor + 128;
                data[1] = (data[1]-128) * factor + 128;
                temp.put(i, j, data);
            }
        }
        Imgproc.cvtColor(temp, temp1, COLOR_Lab2RGB );
        return temp1;
    }

    private static void blackWhiteTranslate(Mat input) {
        //warning
        //hardcode
        for (int i=0; i<input.rows(); i++) {
            for (int j=0; j<input.cols(); j++) {
                double[] data = input.get(i, j);
                if (data[2] < 128)
                    data[2] = 0;
                if (data[1] < 128)
                    data[1] = 0;
                if (data[0] < 128)
                    data[0] = 0;

                if (data[2] >= 128)
                    data[2] = 255;
                if (data[1] >= 128)
                    data[1] = 255;
                if (data[0] >= 128)
                    data[0] = 255;

                int h = 0;
                for (int i1 = 0; i1 < data.length; i1++) {
                    h+= data[i1];
                }

                if (h == 255) {
                    data[0] = 0;
                    data[1] = 0;
                    data[2] = 0;
                }

                if (h == 510) {
                    data[0] = 255;
                    data[1] = 255;
                    data[2] = 255;
                }
                input.put(i, j, data);
            }
        }
    }

    private static void colorInvert(Mat input){
        for (int i=0; i < input.rows(); i++) {
            for (int j=0; j<input.cols(); j++) {
                double[] data = input.get(i, j);
                data[2] = Math.abs(data[2] - 255.0);
                data[1] = Math.abs(data[1] - 255.0);
                data[0] = Math.abs(data[0] - 255.0);
                input.put(i, j, data);
            }
        }
    }

    private static void overlayMask(Mat mask, Mat input) {
        for (int i=0; i < input.rows(); i++) {
            for (int j=0; j< input.cols(); j++) {
                double[] dataInput = input.get(i, j);
                double[] dataTemp = mask.get(i, j);
                if (dataTemp[0] == 0 && dataTemp[1] == 0 && dataTemp[2] == 0 ) {
                    dataInput[2] = dataTemp[2];
                    dataInput[1] = dataTemp[1];
                    dataInput[0] = dataTemp[0];
                }
                input.put(i, j, dataInput);
            }
        }
    }
}
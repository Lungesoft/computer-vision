package com.lungesoft.cvtask;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.photo.Photo.NORMAL_CLONE;

/**
 * Created by Vadim on 2017-03-02.
 */
public class BlendingImagesFourthTask {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat inputOne = Imgcodecs.imread("inputOne.jpg");
        Mat inputTwo = Imgcodecs.imread("inputThree.jpg");
        Mat resizeimage = new Mat();

        double sizeFactor = 2.3;
        Size sz = inputTwo.size();
        sz.width = sz.width / sizeFactor;
        sz.height = sz.height / sizeFactor;
        Imgproc.resize( inputTwo, resizeimage, sz );

        Mat src_mask = Mat.zeros(resizeimage.rows(), resizeimage.cols(), resizeimage.depth());
        Point poly[][] = new Point[1][7];
        poly[0][0] = new Point(15 / sizeFactor, 147 / sizeFactor);
        poly[0][1] = new Point(105 / sizeFactor, 51 / sizeFactor);
        poly[0][2] = new Point(273 / sizeFactor, 18 / sizeFactor);
        poly[0][3] = new Point(447 / sizeFactor, 90 / sizeFactor);
        poly[0][4] = new Point(374 / sizeFactor, 250 / sizeFactor);
        poly[0][5] = new Point(227 / sizeFactor, 292 / sizeFactor);
        poly[0][6] = new Point(8 / sizeFactor, 209 / sizeFactor);

        MatOfPoint point = new MatOfPoint(poly[0]);
        List<MatOfPoint> list = new ArrayList<>();
        list.add(point);

        Imgproc.fillPoly(src_mask, list, new Scalar(255,255,255));

        Mat result = new Mat();
        Photo.seamlessClone(resizeimage, inputOne, src_mask, new Point(650, 300), result, NORMAL_CLONE);
        Imgcodecs.imwrite("Output.jpg", result);



    }
}

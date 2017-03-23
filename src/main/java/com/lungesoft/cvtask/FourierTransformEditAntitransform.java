package com.lungesoft.cvtask;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class FourierTransformEditAntitransform {

    private List<Mat> planes = new ArrayList<>();

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("oldPicture.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        FourierTransformEditAntitransform task = new FourierTransformEditAntitransform();

        Mat dft = task.doDFT(image);

//        task.deleteWhiteSpots(dft,
//                new Spot(100, 300, 200),
//                new Spot(100, 1300, 200),
//                new Spot(100, 290, 1000),
//                new Spot(100, 1300, 1000),
//
//                new Spot(50, 1050, 400),
//                new Spot(50, 1075, 800),
//                new Spot(50, 550, 800),
//                new Spot(50, 550, 400),
//
//                new Spot(50, 1600, 800),
//                new Spot(50, 1600, 400),
//                new Spot(50, 0, 800),
//                new Spot(50, 0, 400),
//                new Spot(50, 550, 50),
//                new Spot(50, 1050, 50));

        Mat noramalize = task.doRGBNormalize(dft);
        Imgcodecs.imwrite("Output2.jpg", noramalize);

        Mat restoredImage = task.doIDFT(dft);
        Imgcodecs.imwrite("Out.jpg", restoredImage);

    }

    private void deleteWhiteSpots(Mat dft, Spot ... spots) {
        for (Spot spot : spots) {
            int radius = spot.getRadius();
            int x = spot.getX();
            int y = spot.getY();
            for (int i = y - radius; i < y + radius; i++) {
                for (int j = x - radius; j < x + radius; j++) {
                    dft.put(i, j, 0, 0);
                }
            }
        }
    }

    private Mat doDFT(Mat image) {
        Mat complexImage = new Mat();
        Mat padded = this.optimizeImageDim(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        Core.dft(complexImage, complexImage);
        return complexImage;
    }


    private Mat doIDFT(Mat dft) {
        Core.idft(dft, dft);
        Mat restoredImage = new Mat();
        Core.split(dft, this.planes);
        Core.normalize(this.planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX);
        return restoredImage;
    }

    private Mat optimizeImageDim(Mat image) {
        Mat padded = new Mat();
        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        // apply the optimal cols and rows size to the image
        Core.copyMakeBorder(
                image, padded, 0,
                addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                Core.BORDER_CONSTANT, Scalar.all(0));
        return padded;
    }



    private Mat doRGBNormalize(Mat complexImage) {
        List<Mat> newPlanes = new ArrayList<>();
        Mat mag = new Mat();
        // split the comples image in two planes
        Core.split(complexImage, newPlanes);
        System.out.println(newPlanes);
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);
        // move to a logarithmic scale
        Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
        Core.log(mag, mag);
        // optionally reorder the 4 quadrants of the magnitude image
        this.shiftDFT(mag);

        // normalize the magnitude image for the visualization
        mag.convertTo(mag, CvType.CV_8UC1);
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        return mag;
    }

    private void shiftDFT(Mat image) {
        image = image.submat(new Rect(0, 0, image.cols() & -2, image.rows() & -2));
        int cx = image.cols() / 2;
        int cy = image.rows() / 2;

        Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }

    static class Spot{
        private int radius;
        private int x;
        private int y;

        public Spot(int radius, int x, int y) {
            this.radius = radius;
            this.x = x;
            this.y = y;
        }

        public int getRadius() {
            return radius;
        }

        public int getX() {
            return x;
        }


        public int getY() {
            return y;
        }

    }


}
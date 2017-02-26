package com.lungesoft.cvtask;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FourierThirdTask {

    List<Mat> planes = new ArrayList<>();
    Mat complexImage = new Mat();

    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread("input.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        FourierThirdTask task = new FourierThirdTask();
        task.transformImage(image);
        //not working
        task.antitransformImage();

    }


    public Mat elaborate(Mat input) {

//        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY);
        // init
        Mat padded = new Mat();
        // get the optimal rows size for dft
        int addPixelRows = Core.getOptimalDFTSize(input.rows());
        // get the optimal cols size for dft
        int addPixelCols = Core.getOptimalDFTSize(input.cols());
        // apply the optimal cols and rows size to the image
        Core.copyMakeBorder(input, padded, 0, addPixelRows - input.rows(),
                0, addPixelCols - input.cols(), Core.BORDER_CONSTANT,
                Scalar.all(0));

        padded.convertTo(padded, CvType.CV_32F);
        this.planes.add(padded);
        this.planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        // prepare a complex image for performing the dft
        Core.merge(this.planes, this.complexImage);
        // dft
        // complexImage.convertTo(complexImage, CvType.CV_64FC2);
        Core.dft(this.complexImage, this.complexImage);

        List<Mat> newPlanes = new ArrayList<>();
        Mat mag = new Mat();
        // split the comples image in two planes
        Core.split(complexImage, newPlanes);
        // compute the magnitude
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);

        // move to a logarithmic scale
        Core.add(mag, Scalar.all(1), mag);
        Core.log(mag, mag);

        mag = mag.submat(new Rect(0, 0, mag.cols() & -2, mag.rows() & -2));
        int cx = mag.cols() / 2;
        int cy = mag.rows() / 2;

        Mat q0 = new Mat(mag, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);

        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX);
        planes = new ArrayList<>();
        return mag;
    }

    public void transformImage(Mat image) {
        // optimize the dimension of the loaded image
        Mat padded = this.optimizeImageDim(image);
        padded.convertTo(padded, CvType.CV_32F);
        // prepare the image planes to obtain the complex image
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        // prepare a complex image for performing the dft

        Core.merge(planes, complexImage);
        System.out.println(planes);
        System.out.println(Arrays.toString(complexImage.get(2, 2)));

        // dft
        Core.dft(complexImage, complexImage);

        // optimize the image resulting from the dft operation
        Mat magnitude = this.createOptimizedMagnitude(complexImage);
        Imgcodecs.imwrite("Output2.jpg", magnitude);

    }


    protected void antitransformImage() {
        Core.idft(this.complexImage, this.complexImage);
//        Mat image = Imgcodecs.imread("Output2.jpg");
//        image.convertTo(image, CvType.CV_32FC1);
//        Core.idft(image, image);

        Mat restoredImage = new Mat();
        Core.split(this.complexImage, this.planes);
        Core.normalize(this.planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX);
        Imgcodecs.imwrite("Out.jpg", restoredImage);

    }

    private Mat optimizeImageDim(Mat image) {
        // init
        Mat padded = new Mat();
        // get the optimal rows size for dft
        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        // get the optimal cols size for dft
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        // apply the optimal cols and rows size to the image
        Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                Core.BORDER_CONSTANT, Scalar.all(0));
        return padded;
    }

    private Mat createOptimizedMagnitude(Mat complexImage) {
        // init
        List<Mat> newPlanes = new ArrayList<>();
        Mat mag = new Mat();
        // split the comples image in two planes
        Core.split(complexImage, newPlanes);
        // compute the magnitude
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);

        // move to a logarithmic scale
        Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
        Core.log(mag, mag);
        // optionally reorder the 4 quadrants of the magnitude image
        this.shiftDFT(mag);
        // normalize the magnitude image for the visualization since both JavaFX
        // and OpenCV need images with value between 0 and 255
        // convert back to CV_8UC1
        mag.convertTo(mag, CvType.CV_8UC1);
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        // you can also write on disk the resulting image...
        // Imgcodecs.imwrite("../magnitude.png", mag);
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







}
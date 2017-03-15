package com.lungesoft.cvtask;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vadim on 2017-03-08.
 */
public class FindHomography {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("run searching");

        Mat imgObject = Imgcodecs.imread("object.jpg", 0);
        Mat imgScene = Imgcodecs.imread("scene.jpg", 0);

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);

        MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
        MatOfKeyPoint keypointsScene = new MatOfKeyPoint();

        detector.detect(imgObject, keypointsObject);
        detector.detect(imgScene, keypointsScene);

        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        Mat descriptorObject = new Mat();
        Mat descriptorScene = new Mat();

        extractor.compute(imgObject, keypointsObject, descriptorObject);
        extractor.compute(imgScene, keypointsScene, descriptorScene);

        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        MatOfDMatch matches = new MatOfDMatch();

        matcher.match(descriptorObject, descriptorScene, matches);
        List<DMatch> matchesList = matches.toList();

        Double maxDist = 0.0;
        Double minDist = 100.0;

        for (int i = 0; i < descriptorObject.rows(); i++) {
            Double dist = (double) matchesList.get(i).distance;
            if (dist < minDist) minDist = dist;
            if (dist > maxDist) maxDist = dist;
        }

        System.out.println("- max dist : " + maxDist);
        System.out.println("- min dist : " + minDist);

        LinkedList<DMatch> goodMatches = new LinkedList<>();
        MatOfDMatch gm = new MatOfDMatch();

        for (int i = 0; i < descriptorObject.rows(); i++) {
            if (matchesList.get(i).distance < minDist * 4) {
                goodMatches.addLast(matchesList.get(i));
            }
        }

        gm.fromList(goodMatches);

        Mat imgMatches = new Mat();
        Features2d.drawMatches(
                imgObject,
                keypointsObject,
                imgScene,
                keypointsScene,
                gm,
                imgMatches,
                new Scalar(255, 0, 0),
                new Scalar(0, 0, 255),
                new MatOfByte(),
                2);

        LinkedList<Point> objList = new LinkedList<>();
        LinkedList<Point> sceneList = new LinkedList<>();

        List<KeyPoint> keypointsObjectList = keypointsObject.toList();
        List<KeyPoint> keypointsSceneList = keypointsScene.toList();

        for (DMatch goodMatcher : goodMatches) {
            objList.addLast(keypointsObjectList.get(goodMatcher.queryIdx).pt);
            sceneList.addLast(keypointsSceneList.get(goodMatcher.trainIdx).pt);
        }

        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);

        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(sceneList);

        Mat h = Calib3d.findHomography(obj, scene, 8, 10);

        Mat objCorners = new Mat(4, 1, CvType.CV_32FC2);
        Mat sceneCorners = new Mat(4, 1, CvType.CV_32FC2);

        objCorners.put(0, 0, new double[] {0,0});
        objCorners.put(1, 0, new double[] {imgObject.cols(),0});
        objCorners.put(2, 0, new double[] {imgObject.cols(),imgObject.rows()});
        objCorners.put(3, 0, new double[] {0,imgObject.rows()});

        Core.perspectiveTransform(objCorners, sceneCorners, h);

//        Imgproc.line(imgMatches, new Point(), new Point(), new Scalar(0,255,0), 4);
//        Imgproc.line(imgMatches, new Point(), new Point(), new Scalar(0,255,0), 4);
//        Imgproc.line(imgMatches, new Point(), new Point(), new Scalar(0,255,0), 4);
//        Imgproc.line(imgMatches, new Point(), new Point(), new Scalar(0,255,0), 4);

        Mat img = Imgcodecs.imread("scene.jpg");

        Imgproc.line(img, new Point(sceneCorners.get(0,0)), new Point(sceneCorners.get(1,0)), new Scalar(0, 255, 0),4);
        Imgproc.line(img, new Point(sceneCorners.get(1,0)), new Point(sceneCorners.get(2,0)), new Scalar(0, 255, 0),4);
        Imgproc.line(img, new Point(sceneCorners.get(2,0)), new Point(sceneCorners.get(3,0)), new Scalar(0, 255, 0),4);
        Imgproc.line(img, new Point(sceneCorners.get(3,0)), new Point(sceneCorners.get(0,0)), new Scalar(0, 255, 0),4);

        System.out.println("writing to file");
        Imgcodecs.imwrite("searchingResult.png", img);
        Imgcodecs.imwrite("searchingResultPoints.png", imgMatches);
    }



}

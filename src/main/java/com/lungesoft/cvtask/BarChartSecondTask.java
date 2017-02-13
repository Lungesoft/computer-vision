package com.lungesoft.cvtask;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2Lab;

public class BarChartSecondTask extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private Map<Double, Integer>[] getChartData(Mat image) {
        Map<Double, Integer>[] channelsChartData = IntStream.range(0, image.channels()).mapToObj(v -> new TreeMap<>()).toArray(Map[]::new);
        for (int i=0; i < image.rows(); i++) {
            for (int j=0; j < image.cols(); j++) {
                double[] data = image.get(i, j);
                for (int k = 0; k < image.channels(); k++) {
                    channelsChartData[k].put(data[k], channelsChartData[k].getOrDefault(data[k], 0) + 1);
                }
            }
        }
        return channelsChartData;
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat input = Imgcodecs.imread("input.jpg");
        Scanner scanner = new Scanner(System.in);
        System.out.println("RGB - 1, LAB - 2, CMYK - 3");
        System.out.println("Enter type: ");
        int type = scanner.nextInt();
        Mat image;
        switch (type) {
            case 1 : {
                image = input;
                break;
            }
            case 2 : {
                image = translateTo(input, COLOR_RGB2Lab);
                break;
            }
            case 3 : {
                image = rgb2cmyk(input);;
                break;
            }
            default: {
                throw new Exception("Not valid type");
            }
        }
        showChart(stage, image);
    }

    private void showChart(Stage stage, Mat mat) {
        Map<Double, Integer>[] channelsChartData = getChartData(mat);
        stage.setTitle("Channels chart");
        LineChart<Number,Number> lineChart = new LineChart<>(new NumberAxis(0, 255, 15), new NumberAxis());
        lineChart.setCreateSymbols(false);
        lineChart.setTitle("Channels chart");
        for (int i = 0; i < channelsChartData.length; i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Channel " + (i + 1));
            channelsChartData[i].forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
            lineChart.getData().add(series);
        }
        stage.setScene(new Scene(lineChart, 800, 600));
        stage.show();
    }


    private Mat translateTo(Mat input, int formatCode) {
        Mat labProcessing = new Mat();
        Imgproc.cvtColor(input, labProcessing, formatCode );
        return labProcessing;
    }

    private Mat rgb2cmyk(Mat img) {
        Mat cmyk = new Mat(img.rows(), img.cols(), CV_32SC4);
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                double[] data = img.get(i, j);
                int r = (int) data[2];
                int g = (int) data[1];
                int b = (int) data[0];
                cmyk.put(i,j, getCMYK(r, g, b));
            }
        }

        return cmyk;
    }

    private static double[] getCMYK(int red, int green, int blue) {
        double[] list = new double[4];
        double highestValue;
        double r = red / 255.0;
        double g = green / 255.0;
        double b = blue / 255.0;
        highestValue = Math.max(r, g);
        highestValue = Math.max(highestValue, b);
        list[0] = (1 - r - list[3]) / (1 - list[3]) * 255;
        list[1] = (1 - g - list[3]) / (1 - list[3]) * 255;
        list[2] = (1 - b - list[3]) / (1 - list[3]) * 255;
        list[3] = (1 - highestValue) * 255;
        return list;
    }
}
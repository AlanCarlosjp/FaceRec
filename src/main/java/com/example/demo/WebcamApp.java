package com.example.demo;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WebcamApp extends JFrame {
    private JPanel canvas;
    private CascadeClassifier faceDetector;
    private CascadeClassifier eyeDetector;
    private boolean wasEyeClosed = false; // Indica se o olho estava fechado no frame anterior

    public WebcamApp() {
        super("Webcam com Reconhecimento de Rosto e Olhos - JavaCV");
        canvas = new JPanel();
        add(canvas, BorderLayout.CENTER);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        faceDetector = new CascadeClassifier(
                "C:\\Users\\noteb\\Desktop\\projetos\\face rec\\demo\\src\\main\\java\\com\\example\\demo\\haarcascade_frontalface_alt.xml");
        eyeDetector = new CascadeClassifier(
                "C:\\Users\\noteb\\Desktop\\projetos\\face rec\\demo\\src\\main\\java\\com\\example\\demo\\haarcascade_eye.xml");
    }

    private void startCapturing() {
        Thread thread = new Thread(() -> {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            FrameConverter<Mat> converter = new OpenCVFrameConverter.ToMat();
            try {
                grabber.start();
                while (true) {
                    Frame frame = grabber.grab();
                    Mat capturedFrame = converter.convert(frame);
                    detectAndDrawFaces(capturedFrame);
                    BufferedImage image = new Java2DFrameConverter().convert(converter.convert(capturedFrame));
                    updateCanvas(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void detectAndDrawFaces(Mat frame) {
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(frame, faces);

        for (int i = 0; i < faces.size(); i++) {
            Rect face = faces.get(i);
            rectangle(frame, face, new Scalar(0, 255, 0, 0));

            Mat faceROI = new Mat(frame, face);
            RectVector eyes = new RectVector();
            eyeDetector.detectMultiScale(faceROI, eyes);

            boolean isEyeClosed = eyes.size() == 0;
            if (wasEyeClosed && !isEyeClosed) {
                // Piscada detectada
                System.out.println("Piscada detectada!");
            }
            wasEyeClosed = isEyeClosed;

            for (int j = 0; j < eyes.size(); j++) {
                Rect eye = eyes.get(j);
                rectangle(faceROI, eye, new Scalar(255, 0, 0, 0));
            }
        }
    }

    private void updateCanvas(BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
            Graphics g = canvas.getGraphics();
            g.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        });
    }

    public static void main(String[] args) {
        WebcamApp app = new WebcamApp();
        app.setVisible(true);
        app.startCapturing();
    }
}


/*

        // Inicializa os classificadores
        faceDetector = new CascadeClassifier(
                "C:\\Users\\noteb\\Desktop\\projetos\\face rec\\demo\\src\\main\\java\\com\\example\\demo\\haarcascade_frontalface_alt.xml");
        eyeDetector = new CascadeClassifier(
                "C:\\Users\\noteb\\Desktop\\projetos\\face rec\\demo\\src\\main\\java\\com\\example\\demo\\haarcascade_eye.xml");
 */
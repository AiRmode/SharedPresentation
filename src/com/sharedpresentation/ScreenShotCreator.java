package com.sharedpresentation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;

/**
 * Created by Admin on 16.06.14.
 */
public class ScreenShotCreator extends Application {
    private static String path_to_image = "D:\\Java\\TomCats\\apache-tomcat-8.0.8\\res\\";
    private int sX = -1, fX = -1;
    private int sY = -1, fY = -1;
    protected boolean firstData = true;
    protected boolean secondData = false;
    private int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    private int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    private Timer timer2 = null;
    private TimerTask task = null;
    private static int imgCounter = 0;

    public static void main(String[] args) {
//		System.out.println(java.awt.MouseInfo.getPointerInfo().getLocation().x);

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.TRANSPARENT);

        Group root = new Group();

        final Button b = new Button("set start coordinat");
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (sX == -1 || fX == -1) {
                    b.setDisable(true);
                }
                if (sX != -1 || fX != -1) {
                    b.setDisable(false);
                }
            }

        });

        final Button b1 = new Button("start getting jpg...");
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                // start timer
                timer2 = new java.util.Timer();
                task = new TimerTask() {
                    public void run() {
                        getScreen(sX, sY, fX, fY);
                    }
                };
                timer2.schedule(task, 0, 500);
            }

        });

        final Button b2 = new Button("stop getting jpg...");
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                //stop timer
                timer2.cancel();
            }

        });

        HBox h = new HBox();
        h.getChildren().addAll(b, b1, b2);

        javafx.scene.shape.Rectangle rect2 = RectangleBuilder.create()
                .arcWidth(30).arcHeight(30).fill(Color.color(0, 1, 1, 0.01)).x(10).y(160)
                .strokeWidth(3).build();
        rect2.setWidth(width);
        rect2.setHeight(height - 25);

        VBox v = new VBox();
        v.getChildren().add(rect2);

        GridPane t = new GridPane();
        t.add(h, 1, 1);
        t.add(v, 1, 2);

        root.getChildren().add(t);

        Scene scene = new Scene(root, 800, 600);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {
                if (firstData) {
                    sX = (int) m.getScreenX();
                    sY = (int) m.getScreenY();
                    System.out.println("click1!" + sX + " , " + sY);
                    firstData = false;
                    secondData = true;
                    return;
                }

                if (secondData) {
                    fX = (int) m.getScreenX();
                    fY = (int) m.getScreenY();
                    System.out.println("click2!" + fX + " , " + fY);
                    firstData = true;
                    secondData = false;
                }
            }
        });
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void getScreen(double xs, double ys, double xf, double yf) {
        Rectangle screenRect = new Rectangle();
        screenRect.setBounds((int) xs, (int) ys, ((int) (xf - xs)), ((int) (yf - ys)));
        try {
            BufferedImage bufferedImage = new Robot().createScreenCapture(screenRect);
//            ImageIO.write(bufferedImage, "jpg", new File("screens/image" + ".jpg"));
            ImageIO.write(bufferedImage, "jpg", new File(path_to_image + "image" + ".jpg"));
            Thread.sleep(1500);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            imgCounter++;
        }

    }

    public <T, K, M> T sum(T item1, K item2) {

        return item1;

    }
}

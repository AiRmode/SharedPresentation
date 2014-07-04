package com.sharedpresentation;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Admin on 16.06.14.
 */
public class ScreenShotCreator extends Application {
    //    private static String path_to_image = "D:\\Java\\wildfly-8.1.0.Final\\wildfly-8.1.0.Final\\res\\";
//    private static String path_to_image = "D:\\Java\\TomCats\\apache-tomcat-8.0.8\\res\\";
//    private static String path_to_image = "D:\\java-data\\apache-tomcat-8.0.8\\apache-tomcat-8.0.8\\res\\";
    private static String path_to_image = "server/res/";
    private int start_X = -1, final_X = -1;
    private int start_Y = -1, final_Y = -1;
    protected boolean firstCoordinats = true;
    protected boolean secondCoordinats = false;
    private int height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
    private int width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
    private volatile boolean isTimerStopped = true;
    private volatile boolean isDoCapture = true;
    private final long capturingDelay = 500;
    private final long idleCheckDelay = 500;
    private final String runTomCatCmdPath = RunInCmd.getPath()+"server/bin";
    private final String runTomCatCmdName = "startup.bat";
    private final String stopTomCatCmd = RunInCmd.getPath()+"server/bin";
    private final String stopTomCatCmdName = "shutdown.bat";
    private final String presentationStageTitle = "Shared Presentation";
    private final String controlStageTitle = "Control Presentation";
    private String fullServerLink = "http://" + getLocalIP() + ":8080" + "/sp\n\n";

    public static void main(String[] args) {
        //TODO: make file transfer via socket
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final Stage control = new Stage();
        control.setTitle(controlStageTitle);
        stage.setTitle(presentationStageTitle);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOpacity(0.1f);

        Group rootGroup = new Group();

        EventHandler<ActionEvent> setFullScreen = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    stage.toFront();
                    control.toFront();
                } else {
                    stage.setFullScreen(true);
                    stage.toBack();
                    control.toFront();
                }
            }

        };
        final Button fullScreen = createButton("Full screen", setFullScreen);

        EventHandler<ActionEvent> setExitEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                new RunInCmd().runBatFileInCmd(stopTomCatCmdName,stopTomCatCmd);
                System.exit(0);
            }

        };
        final Button exit = createButton("Exit", setExitEvent);

        EventHandler<ActionEvent> setCoordinatEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                firstCoordinats = true;
                secondCoordinats = true;
            }

        };
        final Button setCoordinat = createButton("set start coordinat", setCoordinatEvent);

        EventHandler<ActionEvent> startGettingPictureEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                //enable do capture
                isDoCapture = true;
                // start timer
                if (isTimerStopped) {
                    isTimerStopped = false;
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new RunInCmd().runBatFileInCmd(runTomCatCmdName,runTomCatCmdPath);
                            while (true) {
                                while (isDoCapture) {
                                    getScreen(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                                    sleep(Thread.currentThread(), capturingDelay);
                                }
                                sleep(Thread.currentThread(), idleCheckDelay);
                            }
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
        };

        final Button startGettingPicture = createButton("start getting jpg...", startGettingPictureEvent);

        EventHandler<ActionEvent> stopGettingPictureEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                isDoCapture = false;
            }
        };
        final Button stopGettingPicture = createButton("stop getting jpg...", stopGettingPictureEvent);


        TextField ipAdress = new TextField();
        ipAdress.setStyle("-fx-font: 22px \"Arial\";");
        ipAdress.setText(fullServerLink);

        VBox vBox = createVBox();
        vBox.getChildren().addAll(ipAdress, startGettingPicture, stopGettingPicture, fullScreen, exit);


        javafx.scene.shape.Rectangle rect2 = RectangleBuilder.create()
                .arcWidth(30).arcHeight(30).fill(Color.color(0, 1, 1, 0.01)).x(10).y(160)
                .strokeWidth(3).build();
        rect2.setWidth(width);
        rect2.setHeight(height - 25);

        VBox v = new VBox();
        v.getChildren().add(rect2);

        GridPane t = new GridPane();
        t.add(vBox, 1, 1);
        t.add(v, 1, 2);

        rootGroup.getChildren().add(t);

        Scene scene = new Scene(new Group(), 800, 600);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {
                if (firstCoordinats) {
                    start_X = (int) m.getScreenX();
                    start_Y = (int) m.getScreenY();
                    System.out.println("click1!" + start_X + " , " + start_Y);
                    firstCoordinats = false;
                    secondCoordinats = true;
                    return;
                }

                if (secondCoordinats) {
                    final_X = (int) m.getScreenX();
                    final_Y = (int) m.getScreenY();
                    System.out.println("click2!" + final_X + " , " + final_Y);
                    firstCoordinats = false;
                    secondCoordinats = false;
                }
            }
        });
        scene.setFill(Color.TRANSPARENT);
        control.setScene(new Scene(rootGroup, 300, 150));
        control.show();
        control.setX(scene.getX() / 2);
        control.setY(scene.getY());
        control.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                new RunInCmd().runBatFileInCmd(stopTomCatCmdName,stopTomCatCmd);
                System.exit(0);
            }
        });

        ResizeListener listener = new ResizeListener(stage, scene);
        scene.setOnMouseMoved(listener);
        scene.setOnMousePressed(listener);
        scene.setOnMouseDragged(listener);

        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        stage.show();
    }

    public boolean sleep(Thread t, long time) {
        try {
            t.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public VBox createVBox() {
        return new VBox();
    }

    public Button createButton(String name, EventHandler<ActionEvent> f) {
        Button b = new Button(name);
        b.setOnAction(f);
        return b;
    }

    public static void getScreen(double xs, double ys, double xf, double yf) {
        Rectangle screenRect = new Rectangle();
        screenRect.setBounds((int) xs, (int) ys, ((int) (xf)), ((int) (yf)));
        try {
            BufferedImage bufferedImage = new Robot().createScreenCapture(screenRect);
            ImageIO.write(bufferedImage, "jpg", new File(path_to_image + "image" + ".jpg"));
            Thread.sleep(500);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getLocalIP() {
        String hostname = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostname;
    }
}

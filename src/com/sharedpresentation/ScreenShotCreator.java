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
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(ScreenShotCreator.class);
    private static String path_to_image = "server/res";
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
    private final String runTomCatCmdPath = RunInCmd.getPath() + "server/bin";
    private final String runTomCatCmdName = "startup.bat";
    private final String stopTomCatCmd = RunInCmd.getPath() + "server/bin";
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
        stage.setOpacity(0.2f);

        Group controlRootGroup = new Group();

        Group presentationRootGroup = new Group();

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
        final Button fullScreen = createButton("Full screen/Window screen", setFullScreen);

        EventHandler<ActionEvent> setExitEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                new RunInCmd().runBatFileInCmd(stopTomCatCmdName, stopTomCatCmd);
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
                            new RunInCmd().runBatFileInCmd(runTomCatCmdName, runTomCatCmdPath);
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

        final Button startGettingPicture = createButton("Start presentation", startGettingPictureEvent);

        EventHandler<ActionEvent> stopGettingPictureEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                isDoCapture = false;
            }
        };
        final Button stopGettingPicture = createButton("Stop presentation", stopGettingPictureEvent);


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

        GridPane gridPane = new GridPane();
        gridPane.add(vBox, 1, 1);
        gridPane.add(v, 1, 2);


        javafx.scene.shape.Rectangle captureSceneRect = new javafx.scene.shape.Rectangle();
        captureSceneRect.setId("captureSceneRect");

        VBox pVBox = new VBox();
        pVBox.getChildren().add(captureSceneRect);

        GridPane pPane = new GridPane();
        pPane.add(pVBox, 1, 1);

        presentationRootGroup.getChildren().add(pPane);


        controlRootGroup.getChildren().add(gridPane);

        Scene scene = new Scene(presentationRootGroup, 800, 600);
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {
                if (firstCoordinats) {
                    start_X = (int) m.getScreenX();
                    start_Y = (int) m.getScreenY();
                    logger.info("click1!" + start_X + " , " + start_Y);
                    firstCoordinats = false;
                    secondCoordinats = true;
                    return;
                }

                if (secondCoordinats) {
                    final_X = (int) m.getScreenX();
                    final_Y = (int) m.getScreenY();
                    logger.info("click2!" + final_X + " , " + final_Y);
                    firstCoordinats = false;
                    secondCoordinats = false;
                }
            }
        });

//        scene.setFill(Color.TRANSPARENT);

        control.setScene(new Scene(controlRootGroup, 300, 150));
        control.show();
        control.setX(scene.getX() / 2);
        control.setY(scene.getY());
        control.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                new RunInCmd().runBatFileInCmd(stopTomCatCmdName, stopTomCatCmd);
                System.exit(0);
            }
        });

        ResizeListener listener = new ResizeListener(stage, scene, captureSceneRect);
        scene.setOnMouseMoved(listener);
        scene.setOnMousePressed(listener);
        scene.setOnMouseDragged(listener);

        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                new RunInCmd().runBatFileInCmd(stopTomCatCmdName, stopTomCatCmd);
                System.exit(0);
            }
        });
        stage.show();
    }

    public boolean sleep(Thread t, long time) {
        try {
            t.sleep(time);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
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
            createImageFolderIfNotExist();
            File capturedImage = new File(path_to_image + "/" + "temp_image" + ".jpg");
            ImageIO.write(bufferedImage, "jpg", capturedImage);
            createCorrectImgFileIfNeeded(capturedImage);
            Thread.sleep(500);
        } catch (AWTException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    private static void createCorrectImgFileIfNeeded(File capturedImage) {
        File prevImage = new File(path_to_image + "/" + "image" + ".jpg");
        if (prevImage == null || capturedImage == null) {
            return;
        }
        if (!prevImage.exists()) {
            capturedImage.renameTo(prevImage);
        } else if (prevImage.equals(capturedImage)) {
            return;
        } else {
            boolean isDeleted = prevImage.delete();
            if (isDeleted) {
                capturedImage.renameTo(prevImage);
            }
        }
    }

    private static void createImageFolderIfNotExist() {
        try {
            File imgFolder = new File(path_to_image);
            if (!imgFolder.exists()) {
                imgFolder.createNewFile();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static String getLocalIP() {
        String hostname = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostname = addr.getHostAddress();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return hostname;
    }
}

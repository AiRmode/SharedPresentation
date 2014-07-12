package com.sharedpresentation;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Created by alshevchuk on 02.07.2014.
 */
public class ResizeListener implements EventHandler<MouseEvent> {
    private double dx;
    private double dy;
    private double deltaX;
    private double deltaY;
    private static double border = 10;
    private boolean moveH;
    private boolean moveV;
    private boolean resizeH = false;
    private boolean resizeV = false;
    private Scene scene;
    private Stage stage;
    private Rectangle captureSceneRect;

    double minSizeWidth = 100;
    double minSizeHeight = 100;

    private static double xOffset = 0;
    private static double yOffset = 0;

    public ResizeListener(Stage stage, Scene scene,Rectangle captureSceneRect) {
        this.stage = stage;
        this.scene = scene;
        this.captureSceneRect = captureSceneRect;
    }

    @Override
    public void handle(MouseEvent t) {
        if (MouseEvent.MOUSE_MOVED.equals(t.getEventType())) {
            if (t.getX() < border && t.getY() < border) {
                scene.setCursor(Cursor.NW_RESIZE);
                resizeH = true;
                resizeV = true;
                moveH = true;
                moveV = true;
            } else if (t.getX() < border && t.getY() > scene.getHeight() - border) {
                scene.setCursor(Cursor.SW_RESIZE);
                resizeH = true;
                resizeV = true;
                moveH = true;
                moveV = false;
            } else if (t.getX() > scene.getWidth() - border && t.getY() < border) {
                scene.setCursor(Cursor.NE_RESIZE);
                resizeH = true;
                resizeV = true;
                moveH = false;
                moveV = true;
            } else if (t.getX() > scene.getWidth() - border && t.getY() > scene.getHeight() - border) {
                scene.setCursor(Cursor.SE_RESIZE);
                resizeH = true;
                resizeV = true;
                moveH = false;
                moveV = false;
            } else if (t.getX() < border || t.getX() > scene.getWidth() - border) {
                scene.setCursor(Cursor.E_RESIZE);
                resizeH = true;
                resizeV = false;
                moveH = (t.getX() < border);
                moveV = false;
            } else if (t.getY() < border || t.getY() > scene.getHeight() - border) {
                scene.setCursor(Cursor.N_RESIZE);
                resizeH = false;
                resizeV = true;
                moveH = false;
                moveV = (t.getY() < border);
            } else {
                scene.setCursor(Cursor.DEFAULT);
                resizeH = false;
                resizeV = false;
                moveH = false;
                moveV = false;
            }
        } else if (MouseEvent.MOUSE_PRESSED.equals(t.getEventType())) {
            dx = stage.getWidth() - t.getX();
            dy = stage.getHeight() - t.getY();

            xOffset = stage.getX() - t.getScreenX();
            yOffset = stage.getY() - t.getScreenY();

        } else if (MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())) {
            if (resizeH) {
                if (stage.getWidth() <= minSizeWidth) {//minSize.width
                    if (moveH) {
                        deltaX = stage.getX() - t.getScreenX();
                        if (t.getX() < 0) {// if new > old, it's permitted
                            stage.setWidth(deltaX + stage.getWidth());
                            stage.setX(t.getScreenX());
                        }
                    } else {
                        if (t.getX() + dx - stage.getWidth() > 0) {
                            stage.setWidth(t.getX() + dx);
                        }
                    }
                } else if (stage.getWidth() > minSizeWidth) {//minSize.width
                    if (moveH) {
                        deltaX = stage.getX() - t.getScreenX();
                        stage.setWidth(deltaX + stage.getWidth());
                        stage.setX(t.getScreenX());
                    } else {
                        stage.setWidth(t.getX() + dx);
                    }
                }
            }

           else if (resizeV) {
                if (stage.getHeight() <= minSizeHeight) {//minSize.height
                    if (moveV) {
                        deltaY = stage.getY() - t.getScreenY();
                        if (t.getY() < 0) {// if new > old, it's permitted
                            stage.setHeight(deltaY + stage.getHeight());
                            stage.setY(t.getScreenY());
                        }
                    } else {
                        if (t.getY() + dy - stage.getHeight() > 0) {
                            stage.setHeight(t.getY() + dy);
                        }
                    }
                } else if (stage.getHeight() > minSizeHeight) {//minSize.height
                    if (moveV) {
                        deltaY = stage.getY() - t.getScreenY();
                        stage.setHeight(deltaY + stage.getHeight());
                        stage.setY(t.getScreenY());
                    } else {
                        stage.setHeight(t.getY() + dy);
                    }
                }
            }

            else {
                stage.setX(t.getScreenX() + xOffset);
                stage.setY(t.getScreenY() + yOffset);
            }
        }


        updateSceneProperties();
    }

    private void updateSceneProperties() {
        captureSceneRect.setWidth(stage.getWidth()-4);
        captureSceneRect.setHeight(stage.getHeight()-4);
        captureSceneRect.setX(stage.getX());
        captureSceneRect.setY(stage.getY());
        captureSceneRect.setStroke(Color.RED);
        captureSceneRect.setStrokeDashOffset(10);
        captureSceneRect.setStrokeWidth(4);
        captureSceneRect.setArcWidth(20);
        captureSceneRect.setArcHeight(20);
    }

    public static double getBorder() {
        return border;
    }
}

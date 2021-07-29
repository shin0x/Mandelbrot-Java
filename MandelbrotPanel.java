package Mandelbrot.Self;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class MandelbrotPanel extends JPanel implements MouseListener{

    //declare variables
    private final int IMAGE_WIDTH, IMAGE_HEIGHT;
    private int maxIterations = 100;
    private double zoomFactor = 0.8;
    private final double OFFSET = 0.1;
    public boolean stateChanged = true;
    private final BufferedImage mandelbrotImage;

    private double middleR = -0.75;
    private double middleI = 0;

    private double rangeR = 3.5;
    private double rangeI = 2;

    private boolean recording = false;
    private int recordCount = 0;

    private int red, green, blue;

    //declare actions for keybindings
    Action upAction;
    Action downAction;
    Action leftAction;
    Action rightAction;

    public MandelbrotPanel(int width, int height){
        IMAGE_WIDTH = width;
        IMAGE_HEIGHT = height;

        mandelbrotImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        //init actions for keybindings
        upAction = new GoUp();
        downAction = new GoDown();
        leftAction = new GoLeft();
        rightAction = new GoRight();

        //set keybindings
        //Up
        this.getInputMap().put(KeyStroke.getKeyStroke('w'), "upAction");
        this.getActionMap().put("upAction", upAction);
        //Down
        this.getInputMap().put(KeyStroke.getKeyStroke('s'), "downAction");
        this.getActionMap().put("downAction", downAction);
        //Left
        this.getInputMap().put(KeyStroke.getKeyStroke('a'), "leftAction");
        this.getActionMap().put("leftAction", leftAction);
        //Right
        this.getInputMap().put(KeyStroke.getKeyStroke('d'), "rightAction");
        this.getActionMap().put("rightAction", rightAction);
        addMouseListener(this);
    }

    private void compute(){
        for(int x = 0; x < IMAGE_WIDTH; x++){
            for(int y = 0; y < IMAGE_HEIGHT; y++){
                //calculate c
                double cReal = (double)x / (double)IMAGE_WIDTH;
                double cImaginary = (double)y / (double)IMAGE_HEIGHT;

                cReal = cReal * rangeR + middleR - rangeR / 2;
                cImaginary = cImaginary * rangeI + middleI - rangeI / 2;

                //set values needed for calc
                double zReal = 0;
                double zImaginary = 0;

                int iteration = 0;
                while(iteration < maxIterations){
                    double zReal2 = zReal * zReal;
                    double zImaginary2 = zImaginary * zImaginary;

                    //check if numbers are already too high and thus further calc is not needed
                    if(zReal2 + zImaginary2 > 4.0){
                        break;
                    }

                    double newZReal = zReal*zReal-zImaginary*zImaginary+cReal;
                    zImaginary = 2*zReal*zImaginary+cImaginary;
                    zReal = newZReal;
                    iteration++;
                }

                //check which color is needed
                this.mandelbrotImage.setRGB(x, y, computeColor(iteration).getRGB());
            }
        }
    }

    private Color computeColor(int iteration){
        double t = (double)iteration / (double) maxIterations;

        int red = (int)(9 * (1 - t)*t*t*t * this.red);
        int green = (int)(15 * (1 - t)*(1 - t)*t*t * this.green);
        int blue = (int)(8.5*(1 - t)*(1 - t)*(1 - t)*t * this.blue);

        return new Color(red, green, blue);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(stateChanged){
            compute();
            stateChanged = false;
        }
        g2d.drawImage(mandelbrotImage, 0, 0, null);
        if(recording){
            File outputFile = new File("pictures/" + recordCount + ".png");
            try {
                ImageIO.write(mandelbrotImage, "png", outputFile);
                recordCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reset(){
        middleR = -0.75;
        middleI = 0;
        rangeR = 3.5;
        rangeI = 2;

        stateChanged = true;
        repaint();
    }


    private void zoom() {
        rangeR *= zoomFactor;
        rangeI *= zoomFactor;
        stateChanged = true;
        repaint();
    }


    private void zoomOut() {
        rangeR /= zoomFactor;
        rangeI /= zoomFactor;
        stateChanged = true;
        repaint();
    }

    public void setRecording(boolean selected) {
        if(selected){
            if(!Files.isDirectory(Path.of("pictures"))){
                try {
                    Files.createDirectory(Path.of("pictures"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        recording = selected;
        recordCount = 0;
    }

    public class GoUp extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            middleI -= OFFSET * rangeI;
            stateChanged = true;
            repaint();
        }
    }

    public class GoDown extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            middleI += OFFSET * rangeI;
            stateChanged = true;
            repaint();
        }
    }

    public class GoLeft extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            middleR -= OFFSET * rangeI;
            stateChanged = true;
            repaint();
        }
    }

    public class GoRight extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            middleR += OFFSET * rangeI;
            stateChanged = true;
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //left click -> zoom
        if(e.getButton() == MouseEvent.BUTTON1)
            zoom();
        //right click -> zoom out
        else if(e.getButton() == MouseEvent.BUTTON3)
            zoomOut();
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    //SETTER
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    
    //SETTER FOR THE RGB VALUES


    public void setR(int r) {
        this.red = r;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }
}

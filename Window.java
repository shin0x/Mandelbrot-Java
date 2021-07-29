package Mandelbrot.Self;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class Window extends JFrame{

    //declare variables
    private final int WINDOW_WIDTH = 1080, WINDOW_HEIGHT = 700;
    private SliderFrame sliderFrame;
    private MandelbrotPanel mandelbrotPanel = new MandelbrotPanel(WINDOW_WIDTH, WINDOW_HEIGHT-100);

    public Window(){
        super("Mandelbrot");
        this.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                mandelbrotPanel.requestFocusInWindow();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });

        //controlling the zoom factor
        JLabel zoomLabel = new JLabel("Zoom:");
        JTextField zoomFactorTextField = new JTextField(String.valueOf(mandelbrotPanel.getZoomFactor()), 3);
        zoomFactorTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                try{
                    mandelbrotPanel.setZoomFactor(Double.parseDouble(zoomFactorTextField.getText()));
                    mandelbrotPanel.stateChanged = true;
                    repaint();
                }
                catch (NumberFormatException ne){
                    JOptionPane.showMessageDialog(null, "Please insert real number", "Number error" ,JOptionPane.ERROR_MESSAGE);
                    zoomFactorTextField.setText(zoomFactorTextField.getText().replaceAll("[^\\d.]", ""));
                }
                mandelbrotPanel.requestFocusInWindow();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        //controlling the maximal iterations
        JLabel maxIterationsLabel = new JLabel("Maximal Iterations:");
        JTextField maxIterationsField = new JTextField(String.valueOf(mandelbrotPanel.getMaxIterations()), 5);
        maxIterationsField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
               try{
                    mandelbrotPanel.setMaxIterations(Integer.parseInt(maxIterationsField.getText()));
                    mandelbrotPanel.stateChanged = true;
                    repaint();
                }
                catch (NumberFormatException ne){
                    JOptionPane.showMessageDialog(null, "Please insert real number", "Number error" ,JOptionPane.ERROR_MESSAGE);
                    maxIterationsField.setText(maxIterationsField.getText().replaceAll("[^\\d.]", ""));
                }
               mandelbrotPanel.requestFocusInWindow();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        //stuff to change the color of the mandelbrot
        JPanel controlPanel = new JPanel(new FlowLayout()); //Panel for stuff on the bottom
        sliderFrame = new SliderFrame();
        JButton changeColorButton = new JButton("Set new color");
        changeColorButton.addActionListener(e -> {
            sliderFrame.setVisible(!sliderFrame.isVisible());
        });

        JCheckBox recordCheckBox = new JCheckBox("Record zoom", false);
        recordCheckBox.addActionListener(e -> {
            mandelbrotPanel.setRecording(recordCheckBox.isSelected());
            mandelbrotPanel.requestFocusInWindow();
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            mandelbrotPanel.reset();
            mandelbrotPanel.requestFocusInWindow();
        });

        JButton openPlayerButton = new JButton("Open Player for recorded images");
        openPlayerButton.addActionListener(e -> {
            Player player = new Player();
        });

        controlPanel.add(zoomLabel);
        controlPanel.add(zoomFactorTextField);
        controlPanel.add(maxIterationsLabel);
        controlPanel.add(maxIterationsField);
        controlPanel.add(changeColorButton);
        controlPanel.add(recordCheckBox);
        controlPanel.add(resetButton);
        controlPanel.add(openPlayerButton);

        this.getContentPane().add(mandelbrotPanel, BorderLayout.CENTER);
        mandelbrotPanel.requestFocusInWindow();
        this.add(controlPanel, BorderLayout.SOUTH);
        sliderChanged();

        repaint();
        this.setVisible(true);
    }

    //adjust the color when one slider is changed
    public void sliderChanged(){
        mandelbrotPanel.setR(sliderFrame.getRedValue());
        mandelbrotPanel.setGreen(sliderFrame.getGreenValue());
        mandelbrotPanel.setBlue(sliderFrame.getBlueValue());
        mandelbrotPanel.stateChanged = true;
        repaint();
    }

    private class SliderFrame extends JFrame {
        JSlider redSlider = new JSlider(10, 255, 200);
        JSlider greenSlider = new JSlider(10, 255, 200);
        JSlider blueSlider = new JSlider(10, 255,200);

        public SliderFrame(){
            this.setSize(new Dimension(270, 100));
            this.setResizable(false);
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JLabel redLabel = new JLabel("Red: ");
            JLabel greenLabel = new JLabel("Green: ");
            JLabel blueLabel = new JLabel("Blue: ");

            this.add(redLabel);
            this.add(redSlider);

            this.add(greenLabel);
            this.add(greenSlider);

            this.add(blueLabel);
            this.add(blueSlider);
            redSlider.addChangeListener(e -> sliderChanged());
            greenSlider.addChangeListener(e -> sliderChanged());
            blueSlider.addChangeListener(e -> sliderChanged());

            this.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {

                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    mandelbrotPanel.requestFocusInWindow();
                    dispose();
                }
            });
        }

        public int getRedValue(){
            return redSlider.getValue();
        }
        public int getGreenValue(){
            return greenSlider.getValue();
        }
        public int getBlueValue(){
            return blueSlider.getValue();
        }

    }

    //main method
    public static void main(String[] args) {
        new Window();
    }
}

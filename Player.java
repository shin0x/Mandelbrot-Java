package Mandelbrot.Self;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends JFrame {

    private int delayTime = 25, delayMaximum = 500;
    private final int WIDTH = 600, HEIGHT = 600;
    PicturePanel picturePanel = new PicturePanel();
    Thread thread = new Thread(picturePanel);
    String directoryPath = "";

    public Player() {
        super("Mandelbrot - Player");
        initWindow();
    }

    private void initWindow() {
        //set stuff for the window
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //add control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        //Buttons to start and stop the zooming
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        //action listeners for the buttons
        startButton.addActionListener(e -> {
            showPictures();
            startButton.setEnabled(!startButton.isEnabled());
            stopButton.setEnabled(!stopButton.isEnabled());
        });
        stopButton.addActionListener(e -> {
            stopThread();
            startButton.setEnabled(!startButton.isEnabled());
            stopButton.setEnabled(!stopButton.isEnabled());
        });

        JButton openFolderButton = new JButton("Open Folder");
        openFolderButton.addActionListener(e -> {
            JFileChooser openFolderChooser = new JFileChooser();
            openFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            openFolderChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            openFolderChooser.setAcceptAllFileFilterUsed(false);
            if (openFolderChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                directoryPath = openFolderChooser.getSelectedFile().getAbsolutePath();
            }
        });

        //setup slider for zoom delay
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, delayTime, delayMaximum, delayTime);
        speedSlider.addChangeListener(e -> changeSpeed(speedSlider.getValue()));

        //add elements to the control panel
        controlPanel.add(openFolderButton);
        controlPanel.add(startButton);
        controlPanel.add(speedSlider);
        controlPanel.add(stopButton);

        //add elements to the frame
        this.add(picturePanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void changeSpeed(int value) {
        delayTime = value;
    }

    private void showPictures() {

        //get files from directory
        File directory = new File(directoryPath);
        File[] images = directory.listFiles();
        ArrayList<BufferedImage> imageArrayList = new ArrayList<>();
        try{
            assert images != null;
            for (File image : images) {
                //if file is a file and an image then add to ArrayList
                if (image.isFile() && (image.getName().endsWith("jpg") || image.getName().endsWith("png"))) {
                    try {
                        imageArrayList.add(ImageIO.read(image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception ignored) { }

        //restart thread when it got stopped before
        if (thread.getState() != Thread.State.NEW) {
            thread = new Thread(picturePanel);
            picturePanel.running = true;
        }
        picturePanel.setImages(imageArrayList);

        thread.start();
    }

    private void stopThread() {
        picturePanel.terminate();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Player();
    }



    private class PicturePanel extends JPanel implements Runnable {
        private ArrayList<BufferedImage> images;
        private BufferedImage currentImage;
        private boolean running;
        private int i = 0;
        //mode to restart at the same moment than stopped
        //true -> zoom in; false -> zoom out
        private boolean mode = true;

        public PicturePanel() {
            this.setSize(new Dimension(WIDTH, HEIGHT - 100));
            running = true;
        }

        public void terminate() {
            running = false;
        }

        @Override
        public void run() {
            while(true) {
                if(mode) {
                    while (i < images.size()) {
                        if (!running) {
                            break;
                        }
                        currentImage = images.get(i);
                        repaint();
                        try {
                            Thread.sleep(delayTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        i++;
                    }
                    if (!running) {
                        break;
                    }
                    mode = !mode;
                }
                i--;

                while (i > 0) {
                    if (!running) {
                        break;
                    }
                    currentImage = images.get(i);
                    repaint();
                    try {
                        Thread.sleep(delayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }
                if (!running) {
                    break;
                }
                mode = !mode;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(currentImage, 0, 0, null);
            this.setVisible(true);
        }

        //SETTER
        public void setImages(ArrayList<BufferedImage> images) {
            this.images = images;
        }
    }

}

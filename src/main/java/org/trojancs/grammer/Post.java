package org.trojancs.grammer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Post {

    final public static int PREVIEW_DIM = 640;
    final public static int PUBSIZE_SQUARE = 1080;
    final public static int PUBSIZE_PORTRAIT = 1350;
    final public static int PUBSIZE_LANDSCAPE = 608;

    // width to height
    final public static double RATIO_SQUARE = 1;
    final public static double RATIO_PORTRAIT = 0.8;
    final public static double RATIO_LANDSCAPE = 1.777777777;

    final static int CENTER = 0;

    private int panelsN;
    private Color bgColor;
    private double percentBorder;
    private double aspectRatio;

    private int position;
    private ArrayList<BufferedImage> panels;
    private BufferedImage sourceImg;

    public Post(File sourceFile, int panelsN, Color c, double percentBorder, int position) {
        sourceImg = null;

        this.panelsN = panelsN;
        this.percentBorder = percentBorder;
        this.position = position;
        this.bgColor = c;
        aspectRatio = RATIO_SQUARE;

        panels = new ArrayList<>();

        try {
            sourceImg = ImageIO.read(new File(sourceFile.getAbsolutePath()));
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        generatePanels(PREVIEW_DIM);

    }

    public void generatePanels(int size) {
        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();
        Image scaled = null;
        if (width > height) {
            double ratio = (double) width / height;
            if (aspectRatio == Post.RATIO_LANDSCAPE) {
                height = (int) Math.min((double) (height) / width * size * panelsN * aspectRatio, size);
                width = (int) (ratio * height);
            } else {
                height = (int) Math.min((double) (height) / width * size * panelsN, size);
                width = (int) (ratio * height / aspectRatio);
            }
            scaled = sourceImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        } else {
            double ratio = (double) width / height;
            width = (int) ((double) (width) / height * size);
            if (aspectRatio == Post.RATIO_PORTRAIT) {
                height = (int) (size/aspectRatio);
                width = (int) (ratio * size / aspectRatio);
            } else {
                height = size;
            }
            scaled = sourceImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }

        panels.clear();
        panels.addAll(squarify(convertImageToBufferedImage(scaled), size));
    }

    public ArrayList<BufferedImage> squarify(BufferedImage src, int size) {
        BufferedImage target;
        ArrayList<BufferedImage> imagepanels = new ArrayList<>();
        System.out.println(this);

        int width = 0, height = 0;
        if (aspectRatio == Post.RATIO_SQUARE) {
            width = size;
            height = size;
        } else if (aspectRatio == Post.RATIO_LANDSCAPE) {
            height = size;
            width = (int) (size * Post.RATIO_LANDSCAPE);
        } else if (aspectRatio == Post.RATIO_PORTRAIT) {
            height = (int) (size / Post.RATIO_PORTRAIT);
            width = size;
        }

        if (src.getWidth() > src.getHeight()) { // wide image
            target = new BufferedImage(panelsN * width, height, BufferedImage.TYPE_INT_ARGB);
            if (percentBorder != 0) {
                src = convertImageToBufferedImage(
                        src.getScaledInstance((int) (src.getWidth() * (1 - 2 * percentBorder)),
                                (int) (src.getHeight() * (1 - 2 * percentBorder)), Image.SCALE_SMOOTH));
            }
            int imgstart_width = (int) ((panelsN * width - src.getWidth()) / 2);
            int imgstart_height = (height - src.getHeight()) / 2;

            for (int i = 0; i < panelsN * width; i++) {
                for (int j = 0; j < height; j++) {
                    if (position == CENTER) {
                        if (j > imgstart_height && j < height - imgstart_height && i > imgstart_width
                                && i < panelsN * width - imgstart_width) {
                            try {
                                target.setRGB(i, j, src.getRGB(i - imgstart_width, j - imgstart_height));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                target.setRGB(i, j, bgColor.getRGB());
                            }
                        } else {
                            target.setRGB(i, j, bgColor.getRGB());
                        }
                    }

                }
                if (i % (width / 10) == 0) {
                    System.out.print(i / (width / 10) + " ");
                }
            }
        } else { // tall image
            target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            if (percentBorder != 0) {
                src = convertImageToBufferedImage(
                        src.getScaledInstance((int) (src.getWidth() * (1 - 2 * percentBorder)),
                                (int) (src.getHeight() * (1 - 2 * percentBorder)), Image.SCALE_SMOOTH));
            }
            int imgstart_width = (width - src.getWidth()) / 2;
            int imgstart_height = (int) (height * percentBorder);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (position == CENTER) {
                        if (j > imgstart_height && j < height - imgstart_height && i > imgstart_width
                                && i < width - imgstart_width) {
                            try {
                                target.setRGB(i, j, src.getRGB(i - imgstart_width, j - imgstart_height));
                            } catch (ArrayIndexOutOfBoundsException e) {
                                target.setRGB(i, j, bgColor.getRGB());
                            }
                        } else {
                            target.setRGB(i, j, bgColor.getRGB());
                        }
                    }

                }
                if (i % (width / 10) == 0) {
                    System.out.print(i / (width / 10) + " ");
                }
            }
        }

        System.out.println("Finished.");

        // separate into panels
        for (int p = 0; p < panelsN; p++) {
            BufferedImage panel = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    panel.setRGB(i, j, target.getRGB(width * p + i, j));
                }
            }
            imagepanels.add(panel);
        }

        return imagepanels;
    }

    public static BufferedImage convertImageToBufferedImage(Image img) {
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public BufferedImage getPanel(int n) {
        try {
            return panels.get(n);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error, panel not found: " + e);
        }
        return null;
    }

    public int getPanelsN() {
        return panelsN;
    }

    public void setPanelsN(int panelsN) {
        this.panelsN = panelsN;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    public double getPercentBorder() {
        return percentBorder;
    }

    public void setPercentBorder(double percentBorder) {
        this.percentBorder = percentBorder;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String toString() {
        return panelsN + " " + percentBorder + " " + bgColor;
    }

    public void generateFullResolution() {
        this.generatePanels(PUBSIZE_SQUARE);
    }

    public static void savePanel(BufferedImage panel, File file) {
        try {
            System.out.println(ImageIO.write(panel, "png", file));
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Wrote to file: " + file.getAbsolutePath());
    }

}

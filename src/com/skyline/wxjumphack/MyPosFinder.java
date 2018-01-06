package com.skyline.wxjumphack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by chenliang on 2017/12/31.
 */
public class MyPosFinder {

    public static final int R_TARGET = 40;

    public static final int G_TARGET = 43;

    public static final int B_TARGET = 86;

    public int[] find(BufferedImage image) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        int[] ret = {0, 0};
        int maxX = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = height / 4; j < height * 3 / 4; j++) {
                int pixel = image.getRGB(i, j);
                int r = (pixel & 0xff0000) >> 16;
                int g = (pixel & 0xff00) >> 8;
                int b = (pixel & 0xff);
                if (ToleranceHelper.match(r, g, b, R_TARGET, G_TARGET, B_TARGET, 16)) {
                    maxX = Integer.max(maxX, i);
                    minX = Integer.min(minX, i);
                    maxY = Integer.max(maxY, j);
                    minY = Integer.min(minY, j);
                }
            }
        }
        ret[0] = (maxX + minX) / 2 +3;
        ret[1] = maxY;
        System.out.println(maxX + ", " + minX);
        System.out.println("pos, x: " + ret[0] + ", y: " + ret[1]);
        return ret;
    }

    public static void main(String... strings) throws IOException {
        MyPosFinder t = new MyPosFinder();
        String root = t.getClass().getResource("/").getPath();
        System.out.println("root: " + root);
        String imgsSrc = root + "imgs/src";
        String imgsDesc = root + "imgs/my_pos";
        File srcDir = new File(imgsSrc);
        System.out.println(srcDir);
        long cost = 0;
        for (File file : srcDir.listFiles()) {
            if (!file.getName().endsWith(".png")) {
                continue;
            }
            System.out.println(file);
            BufferedImage img = ImgLoader.load(file.getAbsolutePath());
            long t1 = System.nanoTime();
            int[] pos = t.find(img);
            long t2 = System.nanoTime();
            cost += (t2 - t1);
            BufferedImage desc = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            desc.getGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null); // 绘制缩小后的图
            desc.getGraphics().drawRect(pos[0] - 5, pos[1] - 5, 10, 10);
            File descFile = new File(imgsDesc, file.getName());
            if (!descFile.exists()) {
                descFile.mkdirs();
                descFile.createNewFile();
            }
            ImageIO.write(desc, "png", descFile);
        }
        System.out.println("avg time cost: " + (cost / srcDir.listFiles().length / 1_000_000));

    }
}

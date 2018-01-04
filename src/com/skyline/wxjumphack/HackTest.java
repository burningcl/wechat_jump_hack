package com.skyline.wxjumphack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by chenliang on 2018/1/1.
 */
public class HackTest {


    public static void main(String... strings) throws IOException {
        HackTest t = new HackTest();
        String root = t.getClass().getResource("/").getPath();
        System.out.println("root: " + root);
        String imgsSrc = root + "imgs/input";
        String imgsDesc = root + "imgs/output";
        File srcDir = new File(imgsSrc);
        System.out.println(srcDir);
        MyPosFinder myPosFinder = new MyPosFinder();
        NextCenterFinder nextCenterFinder = new NextCenterFinder();
        WhitePointFinder whitePointFinder = new WhitePointFinder();

        long cost = 0;
        for (File file : srcDir.listFiles()) {
            System.out.println(file);
            BufferedImage img = ImgLoader.load(file.getAbsolutePath());
            int[] myPos = myPosFinder.find(img);
            int[] nextCenter = nextCenterFinder.find(img, myPos);
            if (nextCenter == null || nextCenter[0] == 0) {
                System.err.println("find nextCenter, fail");
                continue;
            } else {
                int centerX, centerY;
                int[] whitePoint = whitePointFinder.find(img, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180);
                if (whitePoint != null) {
                    centerX = whitePoint[0];
                    centerY = whitePoint[1];
                    System.out.println("find whitePoint, succ, (" + centerX + ", " + centerY + ")");
                } else {
                    if (nextCenter[2] != Integer.MAX_VALUE && nextCenter[4] != Integer.MIN_VALUE) {
                        centerX = (nextCenter[2] + nextCenter[4]) / 2;
                        centerY = (nextCenter[3] + nextCenter[5]) / 2;
                    } else {
                        centerX = nextCenter[0];
                        centerY = nextCenter[1] + 48;
                    }
                }
                System.out.println("find nextCenter, succ, (" + centerX + ", " + centerY + ")");
                BufferedImage desc = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics g = desc.getGraphics();
                g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
                g.setColor(Color.RED);
                g.fillRect(myPos[0] - 5, myPos[1] - 5, 10, 10);
                g.setColor(Color.GREEN);
                g.fillRect(nextCenter[0] - 5, nextCenter[1] - 5, 10, 10);
                g.fillRect(nextCenter[2] - 5, nextCenter[3] - 5, 10, 10);
                g.fillRect(nextCenter[4] - 5, nextCenter[5] - 5, 10, 10);
                g.setColor(Color.BLUE);
                g.fillRect(centerX - 5, centerY - 5, 10, 10);
                File descFile = new File(imgsDesc, file.getName());
                if (!descFile.exists()) {
                    descFile.mkdirs();
                    descFile.createNewFile();
                }
                ImageIO.write(desc, "png", descFile);
            }

        }
        System.out.println("avg time cost: " + (cost / srcDir.listFiles().length / 1_000_000));

    }

}

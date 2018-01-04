package com.skyline.wxjumphack;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 瓶子的下一步位置计算
 * Created by chenliang on 2017/12/31.
 */
public class BottleFinder {

    public static final int TARGET = 255;

    public int[] find(BufferedImage image, int i, int j) {
        if (image == null) {
            return null;
        }

        int[] ret = new int[6];
        ret[0] = i;
        ret[1] = j;
        ret[2] = Integer.MAX_VALUE;
        ret[3] = Integer.MAX_VALUE;
        ret[4] = Integer.MIN_VALUE;
        ret[5] = Integer.MAX_VALUE;

        int width = image.getWidth();
        int height = image.getHeight();

        boolean[][] vMap = new boolean[width][height];
        Queue<int[]> queue = new ArrayDeque<>();
        int[] pos = {i, j};
        queue.add(pos);

        while (!queue.isEmpty()) {
            pos = queue.poll();
             i = pos[0];
             j = pos[1];
            if (i < 0 || i >= width || j < 0 || j > height || vMap[i][j]) {
                continue;
            }
            vMap[i][j] = true;
            int pixel = image.getRGB(i, j);
            int r = (pixel & 0xff0000) >> 16;
            int g = (pixel & 0xff00) >> 8;
            int b = (pixel & 0xff);
            if (r == TARGET && g == TARGET && b == TARGET) {
                //System.out.println("("+i+", "+j+")");
                if (i < ret[2]) {
                    ret[2] = i;
                    ret[3] = j;
                } else if (i == ret[2] && j < ret[3]) {
                    ret[2] = i;
                    ret[3] = j;
                }
                if (i > ret[4]) {
                    ret[4] = i;
                    ret[5] = j;
                } else if (i == ret[4] && j < ret[5]) {
                    ret[4] = i;
                    ret[5] = j;
                }
                if (j < ret[1]) {
                    ret[0] = i;
                    ret[1] = j;
                }
                queue.add(buildArray(i - 1, j));
                queue.add(buildArray(i + 1, j));
                queue.add(buildArray(i, j - 1));
                queue.add(buildArray(i, j + 1));
            }
        }

        return ret;
    }

    public static int[] buildArray(int i, int j) {
        int[] ret = {i, j};
        return ret;
    }


}

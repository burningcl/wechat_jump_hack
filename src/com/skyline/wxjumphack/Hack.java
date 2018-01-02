package com.skyline.wxjumphack;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by chenliang on 2018/1/1.
 */
public class Hack {


    static final String ADB_PATH = "/Users/chenliang/Library/Android/sdk/platform-tools/adb";

    public static void main(String... strings) {
        //System.out.print(Math.sqrt(4));
        String root = Hack.class.getResource("/").getPath();
        System.out.println("root: " + root);
        File srcDir = new File(root, "imgs/input");
        srcDir.mkdirs();
        System.out.println("srcDir: " + srcDir.getAbsolutePath());
        MyPosFinder myPosFinder = new MyPosFinder();
        NextCenterFinder nextCenterFinder = new NextCenterFinder();
        WhitePointFinder whitePointFinder = new WhitePointFinder();
        for (int i = 0; i < 2048; i++) {
            try {
                File file = new File(srcDir, i + ".png");
                if (file.exists()) {
                    file.deleteOnExit();
                }
                Runtime.getRuntime().exec(ADB_PATH + " shell /system/bin/screencap -p /sdcard/screenshot.png");
                Thread.sleep(800);
                Runtime.getRuntime().exec(ADB_PATH + " pull /sdcard/screenshot.png " + file.getAbsolutePath());
                Thread.sleep(800);

                System.out.println("screenshot, file: " + file.getAbsolutePath());
                BufferedImage image = ImgLoader.load(file.getAbsolutePath());
                int[] myPos = myPosFinder.find(image);
                if (myPos != null) {
                    System.out.println("find myPos, succ, (" + myPos[0] + ", " + myPos[1] + ")");
                    int[] excepted = {myPos[0] - 30, myPos[0] + 30};
                    int[] nextCenter = nextCenterFinder.find(image, excepted, myPos[1]);
                    if (nextCenter == null || nextCenter[0] == 0) {
                        System.err.println("find nextCenter, fail");
                        break;
                    } else {
                        int centerX, centerY;
                        int[] whitePoint = whitePointFinder.find(image, nextCenter[0] - 120, nextCenter[1], nextCenter[0] + 120, nextCenter[1] + 180);
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
                        int distance = (int) (Math.sqrt((centerX - myPos[0]) * (centerX - myPos[0]) + (centerY - myPos[1]) * (centerY - myPos[1])) * 1.392);
                        System.out.println("distance: " + distance);
                        System.out.println(ADB_PATH + " shell input swipe 100 100 100 100 " + distance);
                        Runtime.getRuntime().exec(ADB_PATH + " shell input swipe 300 300 400 400 " + distance);

                    }
                } else {
                    System.err.println("find myPos, fail");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}

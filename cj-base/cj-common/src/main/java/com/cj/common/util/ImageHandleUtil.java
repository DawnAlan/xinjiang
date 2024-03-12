package com.cj.common.util;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;

public class ImageHandleUtil {

    @SneakyThrows
    public static String transferAlpha(MultipartFile file, String userName) {
        // 加载图片
        BufferedImage bufferedImage = ImageIO.read(toFile(file));
        int black = 6200000;
        int alpha;
        for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {
            // 遍历X轴的像素
            boolean flag = false;
            for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
                int rgb = convertRgbInt(bufferedImage.getRGB(x, y));
                if (!flag && rgb <= black) {
                    flag = true;
                }
                if (flag && rgb > black) {
                    flag = false;
                }
                alpha = flag ? 255 : 0;
                rgb = (alpha << 24) | (rgb & 0x00ffffff);
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        // 保存修改后的图像
        String property = System.getProperty("java.io.tmpdir");
        String path = property+"//"+userName+".png";
        ImageIO.write(bufferedImage, "PNG", new File(path));
        return path;
    }

    @SneakyThrows
    public static String removeBaseImage(MultipartFile file, String userName){
        BufferedImage bufferedImage = ImageIO.read(toFile(file));

        Image image = (Image) bufferedImage;
        //将原图片的二进制转化为ImageIcon
        ImageIcon imageIcon = new ImageIcon(image);
        int width = imageIcon.getIconWidth();
        int height = imageIcon.getIconHeight();
        //这个背景底色的选择，我这里选择的是比较偏的位置，可以修改位置。
        int RGB=bufferedImage.getRGB(width-1, height-1);

        int alpha = 255;
        // 遍历Y轴的像素
        for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {
            // 遍历X轴的像素
            for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
                int rgb = bufferedImage.getRGB(x, y);
                int r = (rgb & 0xff0000) >>16;
                int g = (rgb & 0xff00) >> 8;
                int b = (rgb & 0xff);
                int R = (RGB & 0xff0000) >>16;
                int G = (RGB & 0xff00) >> 8;
                int B = (RGB & 0xff);
                //a为色差范围值，渐变色边缘处理，数值需要具体测试，50左右的效果比较可以
                int a = 15;
                if(Math.abs(R-r) < a && Math.abs(G-g) < a && Math.abs(B-b) < a ) {
                    alpha = 0;
                } else {
                    alpha = 255;
                }
                rgb = (alpha << 24) | (rgb & 0x00ffffff);
                bufferedImage.setRGB(x, y, rgb);
            }
        }
        String property = System.getProperty("java.io.tmpdir");
        String path = property+"//"+userName+".png";
        ImageIO.write(bufferedImage, "png", new File(path));
        return path;
    }
    public static String convertRgbStr(int color) {
        int red = (color & 0xff0000) >> 16;// 获取color(RGB)中R位
        int green = (color & 0x00ff00) >> 8;// 获取color(RGB)中G位
        int blue = (color & 0x0000ff);// 获取color(RGB)中B位
        return red + "," + green + "," + blue;
    }

    public static int convertRgbInt(int color) {
        return color & 0xffffff;
    }

    public static File toFile(MultipartFile multipartFile) throws IOException {
        // 获取文件的输入流
        InputStream inputStream = multipartFile.getInputStream();
        // 使用Files库的createTempFile方法创建临时文件
        File tempFile = Files.createTempFile("prefix", "." + multipartFile.getOriginalFilename()).toFile();
        // 将输入流的内容写入临时文件
        Files.copy(inputStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        // 关闭输入流
        inputStream.close();
        // 返回创建的临时文件
        return tempFile;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sauceda_joseph_project02;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 *
 * @author Family
 */
public class Sauceda_Joseph_Project02 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        byte[][] byteData = new byte[256][256];
        BufferedImage inImage = ImageIoFX.readImage("Key1.jpg");
        BufferedImage grayImage = ImageIoFX.toGray(inImage);
        //ImageIoFX.writeImage(grayImage, "jpg", "graykey.jpg");
        Operations ops = new Operations(grayImage);
        
        
        
        System.out.println("total:" + ops.getTotal());
        System.out.println("min:" + ops.getMin());
        System.out.println("max:" + ops.getMax());
        System.out.println("mean:" + ops.getMean());
        System.out.println("variance:" + ops.getVariance());
        System.out.println("This is an " + ops.getNumberOfBits()+ "-bit image");
        System.out.println("dynamicRange:" + ops.getDynamicRange() + "\n\n");
        
        //image,x,y,length,width
        
        //ops.Blur(inImage, 70, 50, 140, 16);
        //System.out.println((int) ops.CreateThreshold(0.5f));
        int t1 = ops.CreateThreshold(0.0032f);
        System.out.println(t1);
        ops.ThresholdingOp(t1,"thresholdkey1");
        
        int t2 = ops.CreateGlobalThreshold(0.003f);
        System.out.println(t2);
        ops.ThresholdingOp(t2,"thresholdkey2");
        
        int t3 = ops.CreateThresholdByBlock(100,100,"block");
        
        ops.ThresholdByBlock(t3,100,100,"block");
        
        
        
        
        
    }
    
}

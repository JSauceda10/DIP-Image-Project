/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sauceda_joseph_project02;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 *
 * @author Family
 */
public class Operations {
    BufferedImage _image;
    byte[][] byteData = new byte[256][256];
    int[] histogram = new int[256];
    int[] cumHistogram = new int[256];
    int total = 0;
    int mean = 0;
    int variance = 0;
    int min = 256, max = 0,dynamicRange = 0;
    int NumberOfBits;

    public Operations(BufferedImage image) {
        this._image = image;
        total = 0;
        mean = 0;
        variance = 0;
        min = 256;
        max = 0;
        dynamicRange = 0;
        
        
        byteData = ImageIoFX.getGrayByteImageArray2DFromBufferedImage(_image);
        
        //initialize histograms
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
            cumHistogram[i] = 0;
            
        }
        //assigning histogram count
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++)
            {
                
                histogram[(byteData[i][j] & 0xFF)]++;
            }
            
        }
        int count = histogram[0];
        cumHistogram[0] = histogram[0];
        //assigning cumulitive histogram count
        for (int i = 1; i < histogram.length; i++) {
            count += histogram[i];
            cumHistogram[i] = count;
            //System.out.println(cumHistogram[i]);
            
            
        }
        
        //getting the total
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++) {
                total += byteData[i][j] & 0xff;

            }
            
        }
        //getting the variance
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++) {
                variance += (((byteData[i][j] & 0xff) - mean) * ((byteData[i][j] & 0xff) - mean));
                
            }
            
        }
        
        //getting the min and max
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++) {
                if((byteData[i][j] & 0xff) > max){
                    max = (byteData[i][j] & 0xff);
                }
                if((byteData[i][j] & 0xff) < min && (byteData[i][j] & 0xff) != 0){
                    min = (byteData[i][j] & 0xff);
                }
                
            }
            
        }
        
        mean = total/(_image.getHeight()* _image.getWidth());
        NumberOfBits = (int)Math.ceil(Math.log(Math.abs(max-min))/Math.log(2));
        dynamicRange = Math.abs(max-min);
        
    }
    
    public void Blur(BufferedImage image, int J, int I, int BlockSize, int subBlockSize){
        byte[][] byteImage = new byte[256][256];
        
        BufferedImage newGray = ImageIoFX.toGray(image);
        byteImage = ImageIoFX.getGrayByteImageArray2DFromBufferedImage(newGray);
        int avg = 0,sum = 0;
        for (int i = I; i < I+BlockSize; i+= subBlockSize) {
            for (int j = J; j < J+BlockSize; j+= subBlockSize) {
                
                
                //System.out.println(i + "," + j);
                for (int k = 0; k < subBlockSize; k++) {
                    for (int l = 0; l < subBlockSize; l++) {
                        //System.out.println((k+i) + "," + (l+j));
                        sum += byteImage[k+i][l+j] & 0xff;
                        
                    }
                    
                }
                
                avg = sum/(subBlockSize * subBlockSize);
                
                for (int k = 0; k < subBlockSize; k++) {
                    for (int l = 0; l < subBlockSize; l++) {
                        
                        byteImage[k+i][l+j] = (byte)avg;
                        
                    }
                    
                }
                
                
            }
            
        }
        
        BufferedImage newImage = ImageIoFX.setGrayByteImageArray2DToBufferedImage(byteImage);
        ImageIoFX.writeImage(newImage, "jpg", "BlurredImage.jpg");
    }
    
    public int CreateThreshold(float percentile){
        //Set T=0 and Current_Percentil =Hc[0]/MN 
        int T = 0;
        float Current_Percentil = (float)(cumHistogram[0]/(float)(256*256));
        //for i from 0 to 2k-1
        for (int i = 0; i < cumHistogram.length; i++) {
            //If Hc[i]/MN >= P1 break with T= i or i-1whichevere gives Hc[i]/MN closer to P1
            
            //System.out.println(String.format("%.017f",(float)(cumHistogram[i]/(float)(256*256))));
            
            if((float)cumHistogram[i]/(float)(256*256) >= percentile){
                
                //System.out.println((float)cumHistogram[i]/(float)(256*256));
                //System.out.println(" is > = ");
                //System.out.println(percentile);
                
                if(Math.abs(percentile - (float)(cumHistogram[i]/(float)(256*256))) < Math.abs(percentile - Current_Percentil)){
                    T = i;
                    return T;
                }else{
                    T = i - 1;
                    return T;
                }
                //return T;
            }
            else{
                T = i;
                Current_Percentil = (float)(cumHistogram[i]/(float)(256*256));
            }
            
        }
        return T;
        
    }
    
    public void ThresholdingOp(int threshold, String name){
        //System.out.println((float)cumHistogram[threshold]/(float)(256*256));
        byte[][] temp = byteData;
        int count = 0;
        for (int i = 0; i < byteData.length; i++) {
            for (int j = 0; j < byteData[0].length; j++) {
                int first = i,second = j;
                //System.out.println("count : " + count++);
                if((float)cumHistogram[threshold]/(float)(256*256) <= (float)(byteData[first][second] & 0xff)/(float)(256*256)){
                    //System.out.println((float)cumHistogram[threshold]/(float)(256*256));
                    //System.out.println("<=");
                    //System.out.println((float)(byteData[first][second] & 0xff)/(float)(256*256));
                    temp[i][j] = (byte)255;
                }
                else{
                    //System.out.println((float)cumHistogram[threshold]/(float)(256*256));
                    //System.out.println(">=");
                    //System.out.println((float)(byteData[first][second] & 0xff)/(float)(256*256));
                    temp[i][j] = (byte)0;
                }
                
            }
            
        }
        BufferedImage newImage = ImageIoFX.setGrayByteImageArray2DToBufferedImage(temp);
        ImageIoFX.writeImage(newImage, "jpg", name + ".jpg");
    }

    public int CreateGlobalThreshold(float To){
        
        float u1 = 0,u2 = 0;
        int initT = 64;
        
        for (int i = 0; i < histogram.length; i++) {
            if(histogram[i] <= initT){
                u1 += histogram[i];
            }else{
                u2 += histogram[i];
            }

        }
        u1 /= (float)(256*256);
        u2 /= (float)(256*256);
        initT = (int) ((u1+u2) * 64);
        
        //System.out.println(initT);
        return initT;
    }
    
    public int CreateThresholdByBlock(int y, int x,String name){
        float mean = 0,stdDev = 0;
        int t = 0, sum = 0;
        for (int i = y; i < byteData.length; i++) {
            for (int j = x; j < byteData[0].length; j++) {
                sum += byteData[i][j] & 0xff;
                
            }
            
        }
        mean = sum/(256*256);
        stdDev = ((sum * sum)/(256*256)) - (mean *mean);
        t = (int) (mean * ((1- 0.3f) * (1 - (stdDev/128))));
        return t;
        
    }
    
    public void ThresholdByBlock(int t,int y, int x, String name){
        byte[][] temp = byteData;
        int count = 0;
        for (int i = y; i < byteData.length; i++) {
            for (int j = x; j < byteData[0].length; j++) {
                int first = i,second = j;
                //System.out.println("count : " + count++);
                if((byteData[i][j] & 0xff) <= t){
                    //System.out.println((float)cumHistogram[threshold]/(float)(256*256));
                    //System.out.println("<=");
                    //System.out.println((float)(byteData[first][second] & 0xff)/(float)(256*256));
                    temp[i][j] = (byte)0;
                }
                else{
                    //System.out.println((float)cumHistogram[threshold]/(float)(256*256));
                    //System.out.println(">=");
                    //System.out.println((float)(byteData[first][second] & 0xff)/(float)(256*256));
                    temp[i][j] = (byte)255;
                }
                
            }
            
        }
        BufferedImage newImage = ImageIoFX.setGrayByteImageArray2DToBufferedImage(temp);
        ImageIoFX.writeImage(newImage, "jpg", name + ".jpg");
    }

    public byte[][] getByteData() {
        return byteData;
    }

    

    public int getTotal() {
        return total;
    }

    

    public int getMean() {
        return mean;
    }

    

    public int getVariance() {
        return variance;
    }

    

    public int getMin() {
        return min;
    }

    

    public int getMax() {
        return max;
    }

    

    public int getDynamicRange() {
        return dynamicRange;
    }

    

    public int getNumberOfBits() {
        return NumberOfBits;
    }

    public int[] getHistogram() {
        return histogram;
    }

    
    
    
    
}

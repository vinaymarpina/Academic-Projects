/*** Author :Vibhav Gogate
The University of Texas at Dallas

Updated By: Venkata Vinay Kumar Marpina
*****/


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

 

public class KMeans {
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
		System.out.println("***********K-Means Algorithm*********");
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    /*
	     * To repeat the Experiment multiple times with different initializations to find the mean and variance
	     */
	    File original = new File(args[0]);
	    long originalSize = original.length();
	    double [] a = new double[10];
	    double totalCR = 0.0;
	    for(int i=0;i<10;i++){
	    	BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    	ImageIO.write(kmeansJpg, "jpg", new File(args[2]));
	    	File finalImage = new File(args[2]);
	    	double finalSize = finalImage.length();
	    	double compressedRatio = (finalSize/originalSize)*100;
	    	System.out.println("Compressed Ratio :"+compressedRatio+" for run number :"+(i+1));
	    	totalCR = totalCR+compressedRatio;
	    	a[i]= compressedRatio;
	    } 
	    double mean = totalCR/a.length;
	    double numSum = 0;
	    for(int j=0;j<a.length;j++){
	    	numSum = numSum+((a[j]-mean)*(a[j]-mean));
	    }
	    double variance = numSum/(a.length-1);
	    System.out.println("*********************************");
	    System.out.println("K-Value :"+k);
	    System.out.println("Average Compressed Ratio :"+mean);
	    System.out.println("Variance :"+variance);
	    System.out.println("*********************************");
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	
	kmeans(rgb,k);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){
    	int[] kArray = new int[k];
    	Random rand = new Random();
    	for(int j=0;j<k;j++){
    		kArray[j] =  rgb[rand.nextInt(rgb.length)];
		}
    	List<ArrayList<Integer>> rnkList = new ArrayList<ArrayList<Integer>>();
    	int loopCount = 30;
    	/*
    	 * Loop to make the algorithm get converge. Given a HardCount.
    	 */
    	for(int a=0;a<loopCount;a++){
    		/*if(a%10 ==0)
    		System.out.println("Printing the Loop Count: "+a);
    		*/
    		for(int i=0;i<rgb.length;i++){
    			double minFinder =0.0;
    			int minIndex= 0;
    			ArrayList<Integer> individualmuKList = new ArrayList<Integer>();
    			for(int j=0;j<kArray.length;j++){
    				double rnkFinder = 0.0;
    				rnkFinder = getDistance(rgb[i], kArray[j]);;
    				if(j!=0){
    					if(minFinder>rnkFinder){
    						if(!individualmuKList.isEmpty()){
    							if(individualmuKList.get(minIndex)==1){
    								individualmuKList.remove(minIndex);
    								individualmuKList.add(minIndex,0);
    							}
    						}
    						minFinder = rnkFinder;
    						minIndex = j;
    						individualmuKList.add(minIndex,1);
    					}else{
    						individualmuKList.add(j,0);
    						
    					}
    				}else{
    					minFinder = rnkFinder;
    					individualmuKList.add(minIndex,1);
    				}
    			}
    			rnkList.add(i,individualmuKList);
    		}
    		for(int i=0;i<kArray.length;i++){
    			kArray[i] = newMean(rnkList, i, rgb);
    		}
    		if(a!=loopCount-1){
    			rnkList.clear();
    		}
    	}
    	for(int i=0;i<rgb.length;i++){
			for(int j=0;j<kArray.length;j++){
				if(rnkList.get(i).get(j)==1){
					rgb[i]=(int) kArray[j];
				}
			}
		}
    
    }
    /*
     * Finding the distance of the each point after converting the 
     * data point into RGB values and 
     */
    public static double getDistance(int rgb,int kA){
    	
    	int red = rgb >> 16 & 0x000000FF;
		int green = rgb >> 8 & 0x000000FF;
		int blue = rgb >> 0 & 0x000000FF;
    	
    	int kred = kA >> 16 & 0x000000FF;
		int kgreen = kA >> 8 & 0x000000FF;
		int kblue = kA >> 0 & 0x000000FF;
	
		double rnkFinder = ((red-kred)*(red-kred))+((green-kgreen)*(green-kgreen))+((blue-kblue)*(blue-kblue));
    	return rnkFinder;
    }
    /*
     * Finding the New mean after assigning the data points to the respective clusters
     * converting each data point into respective RGB values once we
     * find the individual means converting that value to original
     * rgb data point.
     * 
     */
    public static int newMean(List<ArrayList<Integer>> rnkList,int index, int[] rgb){
    	int numeratorRedSum = 0;
    	int denominatorSum = 0;
    	int meanRed =0;
    	int numeratorGreenSum = 0;
    	int meanGreen =0;
    	int numeratorBlueSum = 0;
    	int meanBlue =0;
    	int finalMean =0;
    	
    	for(int i=0;i<rgb.length;i++){
    		int red = rgb[i] >> 16 & 0x000000FF;
    		int green = rgb[i] >> 8 & 0x000000FF;
    		int blue = rgb[i] >> 0 & 0x000000FF;
    		numeratorRedSum = numeratorRedSum + red*rnkList.get(i).get(index);
    		numeratorGreenSum = numeratorGreenSum + green*rnkList.get(i).get(index);
    		numeratorBlueSum = numeratorBlueSum + blue*rnkList.get(i).get(index);
    		denominatorSum = denominatorSum + rnkList.get(i).get(index);
    	}
    	if(denominatorSum != 0.0){
    		meanRed = numeratorRedSum/denominatorSum;
    		meanGreen = numeratorGreenSum/denominatorSum;
    		meanBlue = numeratorBlueSum/denominatorSum;
    	}
    	finalMean = (meanRed << 16)|(meanGreen << 8)|(meanBlue);
    	return finalMean;
    }
   
}
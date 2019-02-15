import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import org.cheapgsean.serial.SerRotatedRect;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

public class T_RectFinder extends Thread {
	
	private boolean threadRunning = true;
	
	private LinkedBlockingDeque<ArrayList<MatOfPoint>> queuedData;
	
	public T_RectFinder() {
		 queuedData = new LinkedBlockingDeque<ArrayList<MatOfPoint>>();
		 //queuedData.add(new ArrayList<MatOfPoint>()); // Test item
	}
	
	public void run() { 
		
		System.out.println("T_RectFinder Thread started!");
		
		while (threadRunning) {
			//System.out.println("RectFinderThread Running");
        try { 
        	if (queuedData.size() > 100) {
        		System.out.println("T_RectFinder Queue overflow! Clearing queue...");
        		queuedData.clear();
        	}
			if (queuedData.size() > 0) { // Data is available
				 //System.out.println("T_RectFinder: Working with data!");
				 // Do stuff with data that's available!
				 ArrayList<MatOfPoint> workingInstance = queuedData.remove(); // Get ArrayList from head of queue
				 
				 ArrayList<SerRotatedRect> tempRectList = new ArrayList<SerRotatedRect>(); // List to serialize
			  
				 for (MatOfPoint pMat : workingInstance) {
					  MatOfPoint2f pMat2f = new MatOfPoint2f();
					  pMat.convertTo(pMat2f, CvType.CV_32F);
					  
					  tempRectList.add(RectConvertToSer(Imgproc.minAreaRect(pMat2f))); // Find and add RotatedRects
				  }
				 
				 Main.RectSerializeSend.addRectData(tempRectList);
			}
		}
		catch (Exception e) { 
		    e.printStackTrace();
		} 

        }
		
		System.out.println("T_RectFinder Thread ending!");
        
    }
	
	public void addMatData(ArrayList<MatOfPoint> _listInput) {
		queuedData.addLast(_listInput);
	}
	
	public static SerRotatedRect RectConvertToSer(RotatedRect rotRect) {
		  SerRotatedRect tempRect = new SerRotatedRect();
		  
		  tempRect.angle = rotRect.angle;
		  tempRect.center = new Point();
		  tempRect.center.setLocation(rotRect.center.x, rotRect.center.y);
		  tempRect.size = new Dimension();
		  tempRect.size.setSize(rotRect.size.width, rotRect.size.height);
		  
		  return tempRect;
	  }
}

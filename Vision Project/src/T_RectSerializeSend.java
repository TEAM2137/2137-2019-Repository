import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.lang3.SerializationUtils;
import org.cheapgsean.serial.SerRotatedRect;
import org.nustaq.serialization.FSTConfiguration;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.networktables.NetworkTableEntry;

public class T_RectSerializeSend extends Thread {
	
	private boolean threadRunning = true;
	
	private LinkedBlockingDeque<ArrayList<SerRotatedRect>> queuedData;
	
	private NetworkTableEntry ntDestination;
	
	private static FSTConfiguration FSTConf = FSTConfiguration.createDefaultConfiguration();
	
	public T_RectSerializeSend(NetworkTableEntry _ntDestination) {
		ntDestination = _ntDestination;
		queuedData = new LinkedBlockingDeque<ArrayList<SerRotatedRect>>();
	}
	
	public void run() { 
		
		System.out.println("T_RectSerializeSend Thread started!");
		
		while (threadRunning) {
		
        try { 
        	if (queuedData.size() > 100) {
        		System.out.println("T_RectSerializeSend Queue overflow! Clearing queue...");
        		queuedData.clear();
        	}
            if (queuedData.size() > 0) { // When data available
            	//System.out.println("T_RectSerializeSend: Working with data!");
            	byte[] objStream = FSTConf.asByteArray(queuedData.remove());
            	//SerializationUtils.serialize(queuedData.remove());
          	  
          	  	ntDestination.setRaw(objStream); // Write bytestream of RotaredRect list to NetworkTable
            }
        } 
        catch (Exception e) { 
            e.printStackTrace();
        } 
		}
		
		System.out.println("T_RectSerializeSend Thread ending!");
    }
	
	public synchronized void addRectData(ArrayList<SerRotatedRect> _listInput) {
		//System.out.println("Rect Data added!");
		queuedData.add(_listInput);
	}
	
}

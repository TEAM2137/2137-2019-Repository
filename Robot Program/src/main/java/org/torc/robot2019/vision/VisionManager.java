package org.torc.robot2019.vision;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;

import org.cheapgsean.serial.SerRotatedRect;
import org.nustaq.serialization.FSTConfiguration;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionManager {

    private static FSTConfiguration FSTConf = FSTConfiguration.createDefaultConfiguration();

    static final byte[] DEFAULT_STREAM_VAL = {0x00}; // Default value when there's not a valid byte array from the N.T.
    static final int TARGET_ANGLE_THRESHOLD = 160;

    private byte[] byteArr; // Buffer for keeping data from NetworkTable

    private NetworkTableInstance tableInst;
    private NetworkTable dataTable;
    private NetworkTableEntry byteEntry;
    private NetworkTableEntry visionResolution;

    private ArrayList<HatchTarget> foundHatchTargets;

    long previousTime;

    public VisionManager(NetworkTableInstance _tableInst) {
        tableInst = _tableInst;

        dataTable = tableInst.getTable("dataTable");
        byteEntry = dataTable.getEntry("RectList");
        visionResolution = dataTable.getEntry("VisionResolution");

        previousTime = System.nanoTime();

        foundHatchTargets = new ArrayList<HatchTarget>(); // Prevent any errors from trying to read list too early

        byteEntry.addListener(entry -> {

            //System.out.println("Listener Triggered!");

            ArrayList<SerRotatedRect> tempList;// = new ArrayList<SerRotatedRect>();

            byteArr = entry.value.getRaw();//byteEntry.getRaw(DEFAULT_STREAM_VAL); // Gets data from NetworkTable
            //byteEntry.addListener(listener, flags);

            if (byteArr.length > 0) { // If the recieved bytes actually consist of bytes
                try {
                    tempList = (ArrayList<SerRotatedRect>)FSTConf.asObject(byteArr);//SerializationUtils.deserialize(byteArr);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (tempList.size() > 0) {
                    if (!(tempList.size() < 2)) { // Only sort if nessicary
                        SortRectHoriz(tempList);
                    }
                    RectAngleCorrection(tempList);

                    SerRotatedRect leftmostRect = tempList.get(0);
                    SerRotatedRect rightmostRect = tempList.get(tempList.size() - 1);

                    SmartDashboard.putNumberArray("LeftmostCenter", new Double[]
                    {leftmostRect.center.getX(), leftmostRect.center.getY(), leftmostRect.angle});

                    SmartDashboard.putNumberArray("RightmostCenter", new Double[]
                    {rightmostRect.center.getX(), rightmostRect.center.getY(), rightmostRect.angle});

                    foundHatchTargets = FindRectPairs(tempList);

                    SmartDashboard.putNumber("TargAmt", foundHatchTargets.size());

                    if (tempList.size() > 1) {
                        if (foundHatchTargets.size() > 0) {
                            HatchTarget target = foundHatchTargets.get(0);

                            SmartDashboard.putNumberArray("FirstTarg", new Double[]
                            {target.center.getX(), target.center.getY()});
                        }
                    }
                }

                //System.out.printf("T.B.E. : %dms\n", (System.nanoTime() - previousTime) / 1000000);
                SmartDashboard.putNumber("VisionExecutionTime", (System.nanoTime() - previousTime) / 1000000);
                previousTime = System.nanoTime();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    public static void RectAngleCorrection(SerRotatedRect _origRect) {
        if (_origRect.size.width < _origRect.size.height) {
            //System.out.printf("Angle along longer side: %7.2f\n", _origRect.angle+180);
            _origRect.angle += 180;
        }
        else {
            //System.out.printf("Angle along longer side: %7.2f\n", _origRect.angle+90);
            _origRect.angle += 90;
        }
    }

    public static void RectAngleCorrection(ArrayList<SerRotatedRect> _rectList) {
        for (SerRotatedRect r : _rectList) {
            RectAngleCorrection(r);
        }
    }
    
    public static void SortRectHoriz(ArrayList<SerRotatedRect> _origList) {

        if (_origList.size() < 2) { // Make sure sorting can happen
            System.out.println("SortRectHoriz: Cannot sort list with less than 2 objects!");
            return;
        }

        boolean sortNeeded = true;

        while (sortNeeded) { // Bubble sort through 
            sortNeeded = false;

            // Ensure that we are looping through 1 less than the list size.
            for (int i = 0; i < _origList.size() - 1; i++) {
                if (_origList.get(i).center.x > _origList.get(i+1).center.x) {
                    Collections.swap(_origList, i, i+1);
                    sortNeeded = true;
                }
            }
        }
    }

    private static ArrayList<HatchTarget> FindRectPairs (ArrayList<SerRotatedRect> _rectList) {
        ArrayList<HatchTarget> retTargList = new ArrayList<HatchTarget>();

        if (_rectList.size() < 2) {
            //System.out.println("FindRectPairs: Cannot find with less than 2 Rects!");

            return retTargList;
        }

        for (int i = 0; i < _rectList.size() - 1; i++) {
            SerRotatedRect rectLeft = _rectList.get(i);
            SerRotatedRect rectRight = _rectList.get(i+1);

            double angleDiff = rectRight.angle - rectLeft.angle;
            //System.out.println("AngleDiff: " + angleDiff);
            if (0 < angleDiff && angleDiff < TARGET_ANGLE_THRESHOLD) { // If pair found
                HatchTarget tempTarget = new HatchTarget();

                tempTarget.center.x = ((rectRight.center.x - rectLeft.center.x) / 2) + rectLeft.center.x;
                tempTarget.center.y = ((rectRight.center.y - rectLeft.center.y) / 2) + rectLeft.center.y;

                retTargList.add(tempTarget);
            }
        }

        return retTargList;
    }

    public Dimension getVisionResolution() {
        double[] vRes = visionResolution.getDoubleArray(new double[]{0, 0});
        return new Dimension((int)vRes[0], (int)vRes[1]);
    }

    public ArrayList<HatchTarget> getVisionTargets() {
        return foundHatchTargets;
    }
}
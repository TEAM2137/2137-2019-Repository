package org.torc.robot2019.functions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class FileLogger {

    public int debugLevel = 3;
    public int numFilesToSave = 15;
    private int index = 0;
    private final static int BUFFER_SIZE = 10;

    public boolean logRoboRio = true;
    public boolean isOpen = false;

    public String rootDirectory = "/home/lvuser/";
    private String filenamePrefix = "roboLog_";
    public String logFileDir = rootDirectory + "Logs/";
    public String filename = "";
    private String[] buffer = new String[BUFFER_SIZE];
    public String eventTag = "";

    private FileWriter writer;

    public FileLogger(){
        openFile();
        setDebug(1);
        setLogRoboRio(true);
    }

    public FileLogger(int debugLevel){
        openFile();
        setDebug(debugLevel);
        setLogRoboRio(true);
    }

    public FileLogger(int debugLevel, boolean logRoboRio){
        openFile();
        setDebug(debugLevel);
        setLogRoboRio(logRoboRio);
    }

    /**
     * Debug is so that all of the file logging does not take down the system
     * @param debug
     */
    public void setDebug(int debug){
        this.debugLevel = debug;
    }
    public int getDebug(){
        return this.debugLevel;
    }

    /**
     * This is so if you log some thing it goes to the system.out panel
     * @param logRoboRio
     */
    public void setLogRoboRio(boolean logRoboRio){
        this.logRoboRio = logRoboRio;
    }
    public boolean getLogRoboRio() {
        return this.logRoboRio;
    }

    /**
     * This comes before what the event is or what you are writing
     * @param eventTag
     */
    public void setEventTag(String eventTag){
        this.eventTag = eventTag;
    }
    public String getEventTag(){
        return this.eventTag;
    }

    public void openFile(){
        String outFile = "";
        if (!isOpen) {
            try {
                long tm = System.currentTimeMillis();
                String tmpFileName = this.filenamePrefix + tm + ".txt";
                deleteOldLogs();
                File out = getFileLoggerStorageFullPath(tmpFileName);
                writer = new FileWriter(tmpFileName, true);
                if (out != null) {
                    this.filename = out.toString();
                    System.out.println("Opened file: " + out.toString());
                    isOpen = true;
                } else {
                    System.out.println("Directory " + this.logFileDir + " is not writable...");
                }
            } catch (Exception ex) {
                System.out.println("Err" + "Caught Exception opening file: " + outFile + ", ex: " + ex);
            }
        }
    }
    private String fileLoggerFormatter(String event){
        return System.currentTimeMillis() + "," + Thread.currentThread().getId() + "," + this.eventTag + "," + event;
    }

    public void saveBitmap(BufferedImage bm, String filename) {
        String fileName = this.filenamePrefix + filename;

        try {
            File out = getFileLoggerStorageFullPath(fileName);
            FileOutputStream stream = new FileOutputStream(out);
            ImageIO.write(bm, "jpg", stream);

            stream.flush();
            stream.close();

        } catch( Exception ex) {
            System.out.println("Err " + "Caught Exception opening file: " + fileName + ", ex: " + ex);
        }
    }

    public void close() {
        try {
            writeBuffer();
            writer.flush();
            writer.close();
        } catch( Exception ex) {
            System.out.println("Caught Exception closing file: "+ex);
        }
        isOpen = false;
    }
    public synchronized void writeEvent(String desc) {
        if (this.logRoboRio) {
            System.out.println(this.eventTag.toUpperCase() + "," + desc);
        }
        if (isOpen) {
            this.write(fileLoggerFormatter(desc));
        }
    }
    public synchronized void writeEvent(String event, String desc) {
        if (this.logRoboRio) {
            if (event.length() > 23) {
                this.eventTag = event.substring(0, 22);
            } else {
                this.eventTag = event;
            }
            System.out.println(this.eventTag.toUpperCase() + "," + desc);
        }
        if (isOpen)
            this.write(fileLoggerFormatter(desc));
    }
    public synchronized void writeEvent(int debug, String event, String desc) {
        if ( this.debugLevel >= debug ) {
            if (this.logRoboRio) {
                if (event.length() > 23) {
                    this.eventTag = event.substring(0, 22);
                } else {
                    this.eventTag = event;
                }
                System.out.println(this.eventTag.toUpperCase() + "," + desc);
            }
            if (isOpen)
                this.write(fileLoggerFormatter(desc));
        }
    }
    public synchronized void writeEvent(int debug, String desc) {
        if ( this.debugLevel >= debug ) {
            if (this.logRoboRio) {
                System.out.println(this.eventTag.toUpperCase() + "," + desc);
            }
            if (isOpen)
                this.write(fileLoggerFormatter(desc));
        }
    }
    public synchronized void write(String line) {
        if (isOpen) {
            if (index == BUFFER_SIZE) {
                writeBuffer();
            }
            buffer[index] = line;
            index++;
        }
    }
    private void writeBuffer() {
        for (int i = 0; i < buffer.length; i++) {
            try {
                if (writer != null) {
                    writer.flush();
                    if (buffer[i] != null)
                        writer.write(buffer[i]);
                    writer.write('\r');
                    writer.write('\n');
                }
            } catch( Exception ex) {
                System.out.println("Caught Exception writing buffer["+i+"] value: "+ex);
            }
        }
        index = 0;
    }

    public String getFileLoggerStoragePath() {
        return this.logFileDir;
    }
    public File getFileLoggerStorageFullPath(String fileName) {
        // Get the directory for the user's public docs directory.
        File file = new File(getFileLoggerStoragePath(), fileName);
        return file;
    }

    private void deleteOldLogs() {
        String path = getFileLoggerStoragePath();
        File directory = new File(path);
        File[] files = directory.listFiles();
        //Log.d("Files", "Size: "+ files.length);

        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {

                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                    return -1;
                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < files.length; i++) {
            if (i <= numFilesToSave) {
                System.out.println("Keeping FileName: " + i + " , " + files[i].getName() + " Modified " + files[i].lastModified());
            } else {
                System.out.println("Deleting FileName: " + i + " , " + files[i].getName() + " Modified " + files[i].lastModified());
                files[i].delete();
            }
        }
    }
}
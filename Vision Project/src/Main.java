/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.vision.VisionThread;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.Videoio;

/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ]
           }
       ]
   }
 */

public final class Main {
  private static String configFile = "/boot/frc.json";

  @SuppressWarnings("MemberName")
  public static class CameraConfig {
    public String name;
    public String path;
    public JsonObject config;
  }

  public static int team;
  public static boolean server;
  public static List<CameraConfig> cameraConfigs = new ArrayList<>();
  
  public static T_RectFinder RectFinder;
  public static T_RectSerializeSend RectSerializeSend;
  
  private static boolean devMode = false;

  private Main() {
  }

  /**
   * Report parse error.
   */
  public static void parseError(String str) {
    System.err.println("config error in '" + configFile + "': " + str);
  }

  /**
   * Read single camera configuration.
   */
  public static boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      parseError("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    cam.config = config;
    
    cameraConfigs.add(cam);
    return true;
  }

  /**
   * Read configuration file.
   */
  public static boolean readConfig() {
    // parse file
    JsonElement top;
    try {
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
    } catch (IOException ex) {
      System.err.println("could not open '" + configFile + "': " + ex);
      return false;
    }

    // top level must be an object
    if (!top.isJsonObject()) {
      parseError("must be JSON object");
      return false;
    }
    JsonObject obj = top.getAsJsonObject();

    // team number
    JsonElement teamElement = obj.get("team");
    if (teamElement == null) {
      parseError("could not read team number");
      return false;
    }
    team = teamElement.getAsInt();

    // ntmode (optional)
    if (obj.has("ntmode")) {
      String str = obj.get("ntmode").getAsString();
      if ("client".equalsIgnoreCase(str)) {
        server = false;
      } else if ("server".equalsIgnoreCase(str)) {
        server = true;
      } else {
        parseError("could not understand ntmode value '" + str + "'");
      }
    }

    // cameras
    JsonElement camerasElement = obj.get("cameras");
    if (camerasElement == null) {
      parseError("could not read cameras");
      return false;
    }
    JsonArray cameras = camerasElement.getAsJsonArray();
    for (JsonElement camera : cameras) {
      if (!readCameraConfig(camera.getAsJsonObject())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    System.out.println("Starting camera '" + config.name + "' on " + config.path);
    /*
    VideoSource camera = CameraServer.getInstance().startAutomaticCapture(
        config.name, 0);//config.path);
        */
    VideoSource camera = CameraServer.getInstance().startAutomaticCapture();

    Gson gson = new GsonBuilder().create();

    if (!devMode) {
    	camera.setConfigJson(gson.toJson(config.config));
    }

    return camera;
  }

  /**
   * Example pipeline.
   */
  public static class MyPipeline implements VisionPipeline {
    public int val;

    @Override
    public void process(Mat mat) {
      val += 1;
    }
  }
  
  /**
   * Main.
 * @throws IOException 
   */
  public static void main(String... args) throws IOException {
    if (args.length > 0) {
    	if (!args[0].startsWith("-")) {
    		configFile = args[0];
    	}
    	
    	for (String s : args) {
    		// If dev mode specified
    		if (s.toLowerCase().equals("-d")) {
    			devMode = true;
    			System.out.println("Devmode Specified");
    		}
    	}
    }
    
    System.out.printf("OS Name: %s\n", System.getProperty("os.name"));

    // read configuration
    if (!readConfig()) {
      return;
    }
    
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    // start NetworkTables
    
    NetworkTableInstance ntinst;
    
    if (!devMode) {
    	ntinst = NetworkTableInstance.getDefault();
    }
    else {
	    ntinst = NetworkTableInstance.create();
	    ntinst.startClient("localhost");
    }
    
    
    /*
    NetworkTableInstance ntinst = NetworkTableInstance.create();
    ntinst.startClient("Gabe-Thinkpad.local");
    */
    if (!devMode) {
	    if (server) { // If server defined in config json
	    	System.out.println("Setting up NetworkTables server");
	    	ntinst.startServer();
	    } else { // If client defined in config json
	      System.out.println("Setting up NetworkTables client for team " + team);
	      ntinst.startClientTeam(team);
	    }
    }
    
    
    // Assign networkTable
    NetworkTable table = ntinst.getTable("dataTable");
    NetworkTableEntry selectedCamera = table.getEntry("SelectedCamera");
    
    System.out.println("WHAT IS UP PEEEEEEEEEEIIIIIIMMMMMMPPPPPSSSSS");
    
    CameraCapture[] cameraArray = new CameraCapture[2];
    
    CvSource outputStream = CameraServer.getInstance().putVideo("RobotCamera", 640, 360);
    
    for (CameraConfig cameraConfig : cameraConfigs) {
    
	    CameraCapture cap = new CameraCapture(cameraConfig.path);
	    
	    int fps = cameraConfig.config.get("fps").getAsInt();
	    
	    cap.setSetupParametersEvent((capture) -> {
	    	capture.set(Videoio.CAP_PROP_FPS, fps);
	    	capture.set(Videoio.CAP_PROP_FRAME_WIDTH, cameraConfig.config.get("width").getAsInt());
	    	capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, cameraConfig.config.get("height").getAsInt());
	    });
	    
	    cap.setPipelineEvent((frame) -> {
			// Flip along vertical axis
			//Core.flip(frame, frame, 1);
			// Convert frame to greyscale
			//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2GRAY);
			// Force resize frame to lower resolution
			//Imgproc.resize(frame, frame, new Size(160, 120));
	    });		
		
		cap.startCamera();
		
		//cap.setCapturingFrames(false);
		
		cap.setFPS(fps);
		
		// Send captured frames to CameraServer
		cap.addNewFrameEvent((e) -> {
			if (!e.empty()) {
				//Mat square = GetSquareImage(e, 156);
				outputStream.putFrame(e);
				//square.release();
			}
		});
		
		if (cameraConfig.name.toLowerCase().contains("front")) {
			cameraArray[0] = cap;
		}
		else {
			cameraArray[1] = cap;
		}
    }
    // On value change
    selectedCamera.addListener(event -> {
    	
    	String ntValue = event.value.getString();
    	
    	System.out.println("updatedNTValue!: " + ntValue);
    	
    	if (ntValue.toLowerCase().contains("rear")) {
    		cameraArray[1].setCapturingFrames(true);
    		cameraArray[0].setCapturingFrames(false);
    	}
    	else {
    		cameraArray[0].setCapturingFrames(true);
    		cameraArray[1].setCapturingFrames(false);
    	}
    }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    selectedCamera.setString("front");
    cameraArray[0].setCapturingFrames(true);
	cameraArray[1].setCapturingFrames(false);

    // loop forever
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }
}

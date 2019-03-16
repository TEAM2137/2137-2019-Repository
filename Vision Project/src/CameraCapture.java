import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CameraCapture {
	
	private Runnable FrameGrabber = new Runnable() {
		
		@Override
		public void run() {
			// effectively grab and process a single frame
			if (currentFrame != null) {
				currentFrame.release();
			}
			currentFrame = grabFrame();
			updateEventList(CameraCaptureEvent.CameraCaptureEvents.NewFrame);
		}
	};
	
	List<CameraCaptureEvent> ccEventList;
	
	private VideoCapture capture;
	private ScheduledExecutorService timer;
	
	private Mat currentFrame;
	
	private final String cameraDir;
	
	private int FPS = 30;
	
	private boolean isWindows = false;
	
	public CameraCapture(String _cameraDir) {
		ccEventList = new ArrayList<CameraCaptureEvent>();
		
		isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
		
		cameraDir = _cameraDir;
		
		if (isWindows) {
			System.out.println("System is Windows; Parsing cameraDir as int...");
			try {
				capture = new VideoCapture(Integer.parseInt(cameraDir));
			}
			catch (Exception e) {
				System.err.println("CameraCapture: Cannot parse int! Attempting directory connection...");
				capture = new VideoCapture(cameraDir);
			}
		}
		else {
			capture = new VideoCapture(cameraDir);
		}
	}
	
	public void startCamera() {
		
		if (capture == null) {
			System.err.println("Cannot start camera, capture is null!!");
			return;
		}
		
		// start the video capture
		System.out.println("Opening cameraID: " + cameraDir);
		//this.capture.open(cameraDir);
		if (isWindows) {
			try {
				this.capture.open(Integer.parseInt(cameraDir));
			}
			catch (Exception e) {
				System.err.println("CameraCapture: Cannot parse int! Attempting directory connection...");
				capture = new VideoCapture(cameraDir);
			}
		}
		else {
			this.capture.open(cameraDir);
		}
		
		// is the video stream available?
		if (this.capture.isOpened()) {
			
			System.out.println("Video stream available");
			//capture.set(Videoio.CAP_PROP_FPS, 10);
			System.out.println("FPS: " + capture.get(Videoio.CAP_PROP_FPS));
			startTimer();
		}
		else {
			System.out.println("Loading connectionerror.png");
		}
			
	}
	
	public void addEvent(CameraCaptureEvent _event) {
		ccEventList.add(_event);
	}
	
	public boolean removeEvent(CameraCaptureEvent _event) {
		return ccEventList.remove(_event);
	}
	
	public void setFPS(int _fps) {
		if (_fps < 1) {
			System.out.println("FPS Cannot be less than 1! Not changing...");
			return;
		}
		FPS = _fps;
		startTimer();
	}
	
	public Mat getCurrentFrame() {
		return currentFrame;
	}
	
	private void startTimer() {
		// If timer exists and is still running
		if (this.timer != null && !this.timer.isShutdown()) {
			this.timer.shutdown();
		}
		this.timer = Executors.newSingleThreadScheduledExecutor();
		// Default FPS to 30
		this.timer.scheduleAtFixedRate(FrameGrabber, 0, (long)(1000/FPS), TimeUnit.MILLISECONDS);
	}
	
	private void updateEventList(CameraCaptureEvent.CameraCaptureEvents _eventType) {
		for (CameraCaptureEvent e : ccEventList) {
			e.onEvent(_eventType);
		}
	}
	
	private boolean checkIfCapturing() {
		return (capture != null && capture.isOpened());
	}
	
	private Mat grabFrame() {
		// init everything
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty()) {
					//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					
					// Flip along vertical axis
					Core.flip(frame, frame, 1);
					Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2GRAY);
					Imgproc.resize(frame, frame, new Size(160, 120));
				}
				
			}
			catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	public void stopAcquisition() {
		if (this.timer!=null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
			System.out.println("Camera released");
		}
	}
}

@FunctionalInterface
interface CameraCaptureEvent {
	enum CameraCaptureEvents {
		NewFrame
	}
	void onEvent(CameraCaptureEvents e);
}

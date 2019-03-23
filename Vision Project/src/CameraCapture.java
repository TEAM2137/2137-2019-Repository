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
			if (!captureFrames) {
				return;
			}
			Mat frame = grabFrame();
			updateNewFrameEventList(frame);
			frame.release();
		}
	};
	
	List<CCNewFrameEvent> ccNewFrameEventList = new ArrayList<CCNewFrameEvent>();
	CCPipelineEvent ccPipelineEvent;
	CCSetupParametersEvent ccSetupParametersEvent;
	
	
	private VideoCapture capture;
	private ScheduledExecutorService timer;
	
	private final String cameraDir;
	
	private int FPS = 30;
	
	private boolean isWindows = false;
	
	private boolean captureFrames = false;
	
	public CameraCapture(String _cameraDir) {
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
			// Set setupParameters
			updateSetupParametersEvent(capture);
			captureFrames = true;
			startTimer();
		}
		else {
			System.out.println("Error with camera connection");
		}
		
	}
	
	public void setFPS(int _fps) {
		if (_fps < 1) {
			System.out.println("FPS Cannot be less than 1! Not changing...");
			return;
		}
		FPS = _fps;
		startTimer();
	}
	
	public void setCapturingFrames(boolean _value) {
		captureFrames = _value;
	}
	
	public boolean getCapturingFrames() {
		return captureFrames;
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
					// Run camera processing pipeline.
					updatePipelineEvent(frame);
					//System.out.println("Target FPS: " + capture.get(Videoio.FRAM));
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
	
	/** Event Functions **/
	
	public void addNewFrameEvent(CCNewFrameEvent _event) {
		ccNewFrameEventList.add(_event);
	}
	
	public boolean removeNewFrameEvent(CCNewFrameEvent _event) {
		return ccNewFrameEventList.remove(_event);
	}
	
	private void updateNewFrameEventList(Mat _frame) {
		for (CCNewFrameEvent e : ccNewFrameEventList) {
			e.onEvent(_frame);
		}
	}
	
	public void setPipelineEvent(CCPipelineEvent _event) {
		ccPipelineEvent = _event;
	}
	
	private void updatePipelineEvent(Mat _frame) {
		if (ccPipelineEvent == null) {
			return;
		}
		ccPipelineEvent.onEvent(_frame);
	}
	
	public void setSetupParametersEvent(CCSetupParametersEvent _event) {
		ccSetupParametersEvent = _event;
	}
	
	private void updateSetupParametersEvent(VideoCapture _capture) {
		if (ccSetupParametersEvent == null) {
			return;
		}
		ccSetupParametersEvent.onEvent(_capture);
	}
}

@FunctionalInterface
interface CCNewFrameEvent {
	void onEvent(Mat _newFrame);
}

@FunctionalInterface
interface CCPipelineEvent {
	void onEvent(Mat _unprocessedFrame);
}

@FunctionalInterface
interface CCSetupParametersEvent {
	void onEvent(VideoCapture _capture);
}


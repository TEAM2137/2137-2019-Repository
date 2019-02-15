import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.wpi.first.vision.VisionPipeline;

public class GripPipelinePredecessor extends GripPipeline {
	
	/*
	private GripPipeline pipeline;
	
	public GripPipelinePredecessor(GripPipeline _pipeline) {
		pipeline = _pipeline;
	}

	@Override
	public void process(Mat _source) {
		Mat flipTemp = new Mat();
		Core.flip(_source, flipTemp, 0);
		Core.flip(flipTemp, _source, 1);
		
		pipeline.process(_source);
	}
	*/
	
	@Override
	public void process(Mat _source) {
		//System.out.println("GripPipelinePredecessor process!!");
		Mat flipTemp = new Mat();
		Core.flip(_source, flipTemp, 0);
		Core.flip(flipTemp, _source, 1);
		
		super.process(_source);
	}
	
}

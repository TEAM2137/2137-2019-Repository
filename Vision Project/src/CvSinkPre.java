import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.wpi.cscore.CvSink;

public class CvSinkPre extends CvSink{

	public CvSinkPre(String arg0) {
		super(arg0);
	}
	
	@Override
	public long grabFrame(Mat _source, double _timeout) {
		System.out.println("Grabbing frame!");
		long retVal = super.grabFrame(_source, _timeout);
		
		Mat flipTemp = new Mat();
		Core.flip(_source, flipTemp, 0);
		Core.flip(flipTemp, _source, 1);
		
		return retVal;
	}
	
	@Override
	public long grabFrame(Mat _source) {
	    return grabFrame(_source, 0.225);
	}

}

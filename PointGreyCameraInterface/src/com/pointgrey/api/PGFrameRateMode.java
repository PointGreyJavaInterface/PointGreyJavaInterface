package com.pointgrey.api;

/**
 * This enumeration contains the framerate modes that we know Point Grey cameras to support.
 * It may be necessary to add any missing modes.
 * @author Matt
 */
public enum PGFrameRateMode {
	FC2_FRAMERATE_1_875(1.875), //1.875 fps 
	FC2_FRAMERATE_3_75(3.75), //3.75 fps
	FC2_FRAMERATE_7_5(7.5), //7.5 fps
	FC2_FRAMERATE_15(15), //15fps
	FC2_FRAMERATE_30(30), //30fps
	FC2_FRAMERATE_60(60), //60fps
	FC2_FRAMERATE_120(120), //120fps
	FC2_FRAMERATE_240(240); //240fps

	private final double frameRate;

	private PGFrameRateMode(double frameRate){
		this.frameRate = frameRate;
	}

	public double getFrameRate(){
		return frameRate;
	}
}
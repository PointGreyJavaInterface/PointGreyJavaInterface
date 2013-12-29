package com.pointgrey.api;

/**
 * This enumeration represents the different camera properties that a Point Grey Camera may support
 * @author Matt
 */
public enum PGPropertyType {
	FC2_BRIGHTNESS("Brightness"),
	FC2_AUTO_EXPOSURE("Exposure"),
	FC2_SHARPNESS("Sharpness"),
	FC2_WHITE_BALANCE("White Balance"),
	FC2_HUE("Hue"),
	FC2_SATURATION("Saturation"),
	FC2_GAMMA("Gamma"),
	FC2_IRIS("Iris"),
	FC2_FOCUS("Focus"),
	FC2_ZOOM("Zoom"),
	FC2_PAN("Pan"),
	FC2_TILT("Tilt"),
	FC2_SHUTTER("Shutter"),
	FC2_GAIN("Gain"),
	FC2_TRIGGER_MODE("Trigger Mode"), //You probably should not use this one.
	FC2_TRIGGER_DELAY("Trigger Delay"), //Or this one
	FC2_FRAME_RATE("Frame Rate"),
	FC2_TEMPERATURE("Temperature");

	private final String name;
	PGPropertyType(String name){
		this.name = name;
	}

	@Override
	public String toString(){
		return name;
	}
}
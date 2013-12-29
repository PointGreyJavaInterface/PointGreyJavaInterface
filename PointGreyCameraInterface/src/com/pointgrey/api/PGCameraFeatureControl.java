package com.pointgrey.api;

/**
 * This class represents a feature on a Point Grey camera. It keeps track of the
 * minimum and maximum values a feature can be set to and prevents you from
 * exceeding them
 *
 * @author Matt
 */
public class PGCameraFeatureControl {

	PGPropertyType type;
	PGPropertyInfo info;
	private double minVal, maxVal;

	public PGCameraFeatureControl(PGPropertyType type) {
		this.type = type;

		updateMinMaxValues();
	}

	public void updateMinMaxValues() {
		info = PointGreyCameraInterface.getPropertyInfo(type);
		if (info.absValSupported) {
			minVal = info.absMin;
			maxVal = info.absMax;
		} else {
			minVal = info.min;
			maxVal = info.max;
		}
	}

	public double read() {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		if (info.absValSupported) {
			return (int) prop.absValue;
		} else {
			return prop.valueA;
		}
	}

	public void write(double value) {
		if (minVal > value || value > maxVal) {
			throw new Error("You keep on using this value... I do not think the range on it is what you think the range on it is");
		}

		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		prop.absControl = info.absValSupported;
		prop.absValue = (float) value;
		prop.valueA = (int) value;
		prop.valueB = (int) value;
		PointGreyCameraInterface.setProperty(prop);
	}

	public double getMaximum() {
		return maxVal;
	}

	public double getMinimum() {
		return minVal;
	}

	public double getCurrentValue() {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		if (info.absValSupported) {
			return prop.absValue;
		} else {
			return prop.valueA;
		}
	}

	public boolean isFeaturePresent() {
		return info.present;
	}

	public boolean isAbsConstrol() {
		return info.absValSupported;
	}

	public boolean canOnePush() {
		return info.onePushSupported;
	}

	public boolean isOn() {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		return prop.onOff;
	}

	public boolean isAutoMode() {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		return prop.autoManualMode;
	}

	public void setOnOff(boolean isOn) {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		prop.onOff = isOn;
		PointGreyCameraInterface.setProperty(prop);
	}

	public void setOnePush(boolean onePush) {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		prop.onePush = onePush;
		PointGreyCameraInterface.setProperty(prop);
	}

	public void setAutoMode(boolean isAuto) {
		PGProperty prop = PointGreyCameraInterface.getProperty(type);
		prop.autoManualMode = isAuto;
		PointGreyCameraInterface.setProperty(prop);
	}
}
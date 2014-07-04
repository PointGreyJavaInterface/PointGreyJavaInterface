package com.pointgrey.api;

import java.util.LinkedList;

public class PointGreyCameraInterface {

	private static long currentCameraSerial;
	private static PGCameraMode currentCameraMode;
	
	static {
		if (System.getProperty("sun.arch.data.model").equals("64")) {
			System.loadLibrary("flyCapture2JNI_Interface64");
		} else {
			System.loadLibrary("flyCapture2JNI_Interface");
		}
	}
	
	private static synchronized native void createContext(); // must be called before everything else. inits the subsystem
	private static synchronized native void destroyContext(); // releases the subsystem. called when program no longer needs access to the camera system
	private static synchronized native int getNumOfCameras(); // returns number of PointGrey USB cameras connected to the computer.
	private static synchronized native void connectToDefaultCamera(); // connects to the first camera found on the computer
	private static synchronized native void startCapture(); // starts capturing from the currently connected camera
	private static synchronized native void stopCapture(); // stops capturing from the currently connected camera
	private static synchronized native byte[] getImage(); // returns a byte array containing the image data from the current frame of the camera. Note: a camera must be connected and capturing to call this function
	private static synchronized native void storeImage(byte[] buffer);
	private static synchronized native String getCameraName();
	private static synchronized native long getSerialFromIndex(long index);
	private static synchronized native void connectCameraWithSerial(long serial);
	private static synchronized native int[] getSupportedModes(); //Returns an array containing supported camera modes in this format {(VideoMode, FrameRateMode)} where VideoMode and FrameRateMode are enumeration values from C.
	private static synchronized native void setCameraMode(int VideoMode, int FrameRateMode);
	private static synchronized native PGPropertyInfo getPropertyInfo(int PropertyType);
	private static synchronized native PGProperty getProperty(int PropertyType);
	private static synchronized native void setProperty(int PropertyType, PGProperty property);

	public static synchronized void createPGContext() {
		createContext();
	}
	
	public static synchronized void destroyPGContext() {
		destroyContext();
	}
	
	public static synchronized int getNumCameras() {
		return getNumOfCameras();
	}
	
	public static synchronized void connectCamera() {
		currentCameraSerial = getSerialFromIndex(0);
		connectToDefaultCamera();
	}
	
	public static synchronized void connectCamera(long serial) {
		currentCameraSerial = serial;
		connectCameraWithSerial(serial);
	}
	
	public static synchronized void startCapturing() {
		startCapture();
	}
	
	public static synchronized void stopCapturing() {
		stopCapture();
	}
	
	public static synchronized byte[] getImageByteArray() {
		byte[] imageByteArray = null;
		try {
			imageByteArray = getImage();
		} catch(Exception e) {
			reconnect();
			imageByteArray = getImage();
		}
		
		return imageByteArray;
	}
	
	public static synchronized void storeImageInBuffer(byte[] buffer) {
		try {
			storeImage(buffer);
		} catch(Exception e) {
			reconnect();
			storeImage(buffer);
		}
	}
	
	public static synchronized String getConnectedCameraName() {
		try {
			return getCameraName();
		} catch(Exception e) {
			reconnect();
		} finally {
			return getCameraName();
		}
	}
	
	public static synchronized long getCameraSerialFromIndex(long index) {
		try {
			return getSerialFromIndex(index);
		} catch(Exception e) {
			reconnect();
			return getSerialFromIndex(index);
		}
	}
	
	public static synchronized PGCameraMode[] getSupportedCameraModes() {
		LinkedList<PGCameraMode> retList = new LinkedList<>();
		int[] supportedModes = null;
		
		try {
			supportedModes = getSupportedModes();
		} catch(Exception e) {
			reconnect();
			supportedModes = getSupportedModes();
		}

		for (int i = 0; i < supportedModes.length; i += 2) {
			if (!PGVideoMode.values()[supportedModes[i]].toString().contains("Y16")) { //Remove Y16 formats. Just because nobody supports it doesn't mean that we should.
				retList.add(new PGCameraMode(PGVideoMode.values()[supportedModes[i]], PGFrameRateMode.values()[supportedModes[i + 1]]));
			}
		}

		return retList.toArray(new PGCameraMode[retList.size()]);
	}

	public static synchronized PGPropertyInfo getPropertyInfo(PGPropertyType propertyType) {
		PGPropertyInfo info;
		try {
			info = getPropertyInfo(propertyType.ordinal());
		} catch(Exception e) {
			reconnect();
			info = getPropertyInfo(propertyType.ordinal());
		}

		info.type = propertyType; //Well isn't that convenient?

		return info;
	}

	public static synchronized PGProperty getProperty(PGPropertyType propertyType) {
		PGProperty info;
		try {
			info = getProperty(propertyType.ordinal());
		} catch(Exception e) {
			reconnect();
			info = getProperty(propertyType.ordinal());
		}

		info.type = propertyType; //Well isn't that convenient?

		return info;
	}

	public static synchronized void setProperty(PGProperty property) {
		setProperty(property.type.ordinal(), property);
	}

	public static synchronized void setCameraMode(PGCameraMode mode) {
		try {
			setCameraMode(mode.getVideoMode().ordinal(), mode.getFrameRateMode().ordinal());
		} catch(Exception e) {
			reconnect();
			setCameraMode(mode.getVideoMode().ordinal(), mode.getFrameRateMode().ordinal());
		}
		
		currentCameraMode = mode;
	}
	
	private static void reconnect() {
		destroyContext(); // make sure he's dead http://www.youtube.com/watch?v=Wpsf-EbyBhI
		
		long reconnectStartTime = System.nanoTime();
		
		boolean reconnectSuccessful = false;
		while(!reconnectSuccessful && System.nanoTime() - reconnectStartTime < 30000000000l) {
			try {
				createContext();
				connectCameraWithSerial(currentCameraSerial);
				setCameraMode(currentCameraMode);
				startCapture();
				reconnectSuccessful = true;
				System.out.println("reconnected!");
			} catch(Exception e) {
				destroyContext();
			}
		}
		
		if(!reconnectSuccessful) {
			System.out.println("failed to reconnect after 30 seconds.");
		}
	}
}
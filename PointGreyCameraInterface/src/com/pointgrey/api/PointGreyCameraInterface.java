package com.pointgrey.api;

import com.pointgrey.util.Color;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class PointGreyCameraInterface {
    static {
        System.loadLibrary("flyCapture2JNI_Interface");
    }
    
    public static synchronized native void createContext(); // must be called before everything else. inits the subsystem
    public static synchronized native void destroyContext(); // releases the subsystem. called when program no longer needs access to the camera system
    public static synchronized native int getNumOfCameras(); // returns number of PointGrey USB cameras connected to the computer.
    public static synchronized native void connectToDefaultCamera(); // connects to the first camera found on the computer
    public static synchronized native void startCapture(); // starts capturing from the currently connected camera
    public static synchronized native void stopCapture(); // stops capturing from the currently connected camera
    public static synchronized native byte[] getImage(); // returns a byte array containing the image data from the current frame of the camera. Note: a camera must be connected and capturing to call this function
    public static synchronized native void storeImage(byte[] buffer);
    public static synchronized native String getCameraName();
    public static synchronized native long getSerialFromIndex(long index);
    public static synchronized native void connectCameraWithSerial(long serial);
    
    private static synchronized native int[] getSupportedModes(); //Returns an array containing supported camera modes in this format {(VideoMode, FrameRateMode)} where VideoMode and FrameRateMode are enumeration values from C.
    private static synchronized native void setCameraMode(int VideoMode, int FrameRateMode);
    private static synchronized native PGPropertyInfo getPropertyInfo(int PropertyType);
    private static synchronized native PGProperty getProperty(int PropertyType);
    private static synchronized native void setProperty(int PropertyType, PGProperty property);
    
    public static synchronized PGCameraMode[] getSupportedCameraModes(){
        LinkedList<PGCameraMode> retList = new LinkedList();
        int[] supportedModes = getSupportedModes();

        for(int i = 0; i < supportedModes.length; i+=2){
            if(!PGVideoMode.values()[supportedModes[i]].toString().contains("Y16")){ //Remove Y16 formats. Just because nobody supports it doesn't mean that we should.
                retList.add(new PGCameraMode(PGVideoMode.values()[supportedModes[i]], PGFrameRateMode.values()[supportedModes[i+1]]));
            }
        }
        
        return retList.toArray(new PGCameraMode[retList.size()]);
    }
    
    public static synchronized PGPropertyInfo getPropertyInfo(PGPropertyType propertyType){
        PGPropertyInfo info = getPropertyInfo(propertyType.ordinal());
        
        info.type = propertyType; //Well isn't that convenient?
        
        return info;
    }
    
    public static synchronized PGProperty getProperty(PGPropertyType propertyType){
        PGProperty info = getProperty(propertyType.ordinal());
        
        info.type = propertyType; //Well isn't that convenient?
        
        return info;
    }
    
    public static synchronized void setProperty(PGProperty property){
        setProperty(property.type.ordinal(), property);
    }
    
    public static synchronized void setCameraMode(PGCameraMode mode){
        setCameraMode(mode.getVideoMode().ordinal(), mode.getFrameRateMode().ordinal());
    }
    
    public static void main(String[] args) {
        System.out.println("about to call native function...");
        
        createContext();
        
        System.out.println("There are " + getNumOfCameras() + " cameras connected to the system");
        
        connectToDefaultCamera();
        
        startCapture();
        
        System.out.println("camera name: " + getCameraName());
        
        for(PGPropertyType type : PGPropertyType.values()){
            PGPropertyInfo info = getPropertyInfo(type);
            if(info.present)
                System.out.println(info);
            
            PGPropertyInfo property = getPropertyInfo(type);
            if(property.present)
                System.out.println(property);
        }
        
        byte[] bigBytha = new byte[1280*960*3];
        //byte[] image = getImage();
        
        int imgCount = 0;
        for(PGCameraMode mode : getSupportedCameraModes()){
            System.out.println(mode);
            setCameraMode(mode);
            byte[] buff = new byte[mode.getVideoMode().getWidth() * mode.getVideoMode().getHeight() * 3];
            storeImage(buff);
            BufferedImage img = new BufferedImage(mode.getVideoMode().getWidth(), mode.getVideoMode().getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        try{
            for(int i = 0; i < buff.length; i+=3)
                img.setRGB((i/3)%mode.getVideoMode().getWidth(), (i/3)/mode.getVideoMode().getWidth(), Color.getIntColor(buff[i+2], buff[i+1], buff[i]));
            ImageIO.write(img, "png", new File(""+mode.getVideoMode() + "" + mode.getFrameRateMode() + ".png"));
        } catch(IOException e) {
            System.out.println("can't write image file");
        } catch(Exception e) {
            System.out.println("other image writing failure");
            e.printStackTrace();
        }
        }
        
        storeImage(bigBytha);
        
        System.out.println("Testing getting supported image modes..");
        int[] supportedCameraModes = getSupportedModes();
        for(int i = 0; i < supportedCameraModes.length; i+=2){
            System.out.println("(" + supportedCameraModes[i] + ", " + supportedCameraModes[i+1] + ")");

        }
        
        stopCapture();
        
        destroyContext();
        
        BufferedImage image3 = new BufferedImage(1280, 960, BufferedImage.TYPE_3BYTE_BGR);
        
        try{
            for(int i = 0; i < bigBytha.length; i+=3)
                image3.setRGB((i/3)%1280, (i/3)/1280, Color.getIntColor(bigBytha[i+2], bigBytha[i+1], bigBytha[i]));
            ImageIO.write(image3, "png", new File("testoutimage.png"));
        } catch(IOException e) {
            System.out.println("can't write image file");
        } catch(Exception e) {
            System.out.println("other image writing failure");
            e.printStackTrace();
        }
        
        System.out.println("native function is done!");
    }
}
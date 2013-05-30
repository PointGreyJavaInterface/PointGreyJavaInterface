package com.pointgrey.api;

/**
 *
 * @author Matt
 */
public class PGCameraFeatureControl {
    
    PGPropertyType type;
    PGPropertyInfo info;
    private float minVal, maxVal;
    
    public PGCameraFeatureControl(PGPropertyType type){
        this.type = type;
        
        updateMinMaxValues();
    }
    
    public void updateMinMaxValues(){
        info = PointGreyCameraInterface.getPropertyInfo(type);
        if (info.absValSupported){
            minVal = info.absMin;
            maxVal = info.absMax;
        } else {
            minVal = info.min;
            maxVal = info.max;
        }
    }
    
    public float read(){
        PGProperty prop = PointGreyCameraInterface.getProperty(type);
        if(info.absValSupported)
            return (int)prop.absValue;
        else
            return prop.valueA;
    }
    
    public void write(float value) {
        if(minVal > value || value > maxVal)
            throw new Error("You keep on using this value... I dont think the range on it is what you think the range on it is");
        
        PGProperty prop = PointGreyCameraInterface.getProperty(type);
        prop.absControl = info.absValSupported;
        prop.absValue = value;
        prop.autoManualMode = false;
        prop.valueA = (int)value;
        prop.valueB = (int)value;
        PointGreyCameraInterface.setProperty(prop);
    }
    
    public float getMaximum(){
        return (int)Math.floor(maxVal);
    }
    
    public float getMinimum(){
        return (int)Math.ceil(minVal);
    }
    
    public float getCurrentValue(){
        PGProperty prop = PointGreyCameraInterface.getProperty(type);
        if(info.absValSupported){
            return prop.absValue;
        } else {
            return prop.valueA;
        }
    }
    
    public boolean isFeaturePresent(){
        return info.present;
    }
}
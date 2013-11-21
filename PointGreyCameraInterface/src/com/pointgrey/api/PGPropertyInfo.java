package com.pointgrey.api;

/**
 * Contains all information about a camera property that we are able to grab
 * @author Matt
 */
public class PGPropertyInfo {
    public PGPropertyType type;
    public boolean present;
    public boolean autoSupported;
    public boolean manualSupported;
    public boolean onOffSupported;
    public boolean onePushSupported;
    public boolean absValSupported;
    public boolean readOutSupported;
    public int min;
    public int max;
    public float absMin;
    public float absMax;
    
    @Override
    public String toString(){
        String ret = "";
        ret += "TYPE: " + type + "\n";
        ret += "present: " + present + "\n";
        ret += "autoSupported: " + autoSupported + "\n";
        ret += "manualSupported: " + manualSupported + "\n";
        ret += "onOffSupported: " + onOffSupported + "\n";
        ret += "onePushSupported: " + onePushSupported+ "\n";
        ret += "absValSupported: " + absValSupported + "\n";
        ret += "readOutSupported: " + readOutSupported + "\n";
        ret += "min: " + min + "\n";
        ret += "max: " + max + "\n";
        ret += "absMin: " + absMin + "\n";
        ret += "absMax: " + absMax + "\n";
        return ret;
    }
}
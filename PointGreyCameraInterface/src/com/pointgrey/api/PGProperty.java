package com.pointgrey.api;

/**
 * This class is used to change a camera's property.
 *
 * @author Matt
 */
public class PGProperty {

	PGPropertyType type;
	boolean present;
	boolean absControl; //This cleverly named variable actually just determines whether or not the floating point value or the integer value is written. Why name this variable absControl and not leave a comment with  the structure? Why indeed.
	boolean onePush; //One more variable like this and I wont need a push. Also, let me know if you figure out what this means.
	boolean onOff;
	boolean autoManualMode;
	int valueA;
	int valueB; //Applies only to the white balance blue value. Use
	//Value A for the red value.
	float absValue;

	@Override
	public String toString() {
		String ret = "";
		ret += "TYPE: " + type + "\n";
		ret += "present: " + present + "\n";
		ret += "absControl: " + absControl + "\n";
		ret += "onePush: " + onePush + "\n";
		ret += "onOff: " + onOff + "\n";
		ret += "autoManualMode: " + autoManualMode + "\n";
		ret += "valueA: " + valueA + "\n";
		ret += "valueB: " + valueB + "\n";
		ret += "absValue: " + absValue + "\n";
		return ret;
	}
}
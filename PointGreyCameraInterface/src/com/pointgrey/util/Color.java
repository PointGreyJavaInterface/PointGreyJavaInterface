package com.pointgrey.util;

/**
 *
 * @author Zebulun Barnett
 */

public class Color {
	private int redValue, greenValue, blueValue, alphaValue;

	public static final Color red = new Color(255, 0, 0);
	public static final Color green = new Color(0, 255, 0);
	public static final Color blue = new Color(0, 0, 255);
	public static final Color white = new Color(255, 255, 255);
	public static final Color black = new Color(0, 0, 0);
	public static final Color transPurple = new Color(255, 0, 255, 127);

	Color(int red, int green, int blue){
		this(red, green, blue, 255);
	}
	
	Color(int red, int green, int blue, int alpha){
		if(red < 0 || green < 0 || blue < 0 || alpha < 0){
			System.out.println("bad color parameter(s) (below zero!)");
			return;
		}

		if(red > 255 || green > 255 || blue > 255 || alpha > 255){
			System.out.println("bad color parameter(s) (over 255!)");
			return;
		}

		this.redValue = red;
		this.greenValue = green;
		this.blueValue = blue;
		this.alphaValue = alpha;
	}

	Color(byte red, byte green, byte blue){
		this(red, green, blue, (byte)255);
	}
	
	Color(byte red, byte green, byte blue, byte alpha){
		if(red < -128 || green < -128 || blue < -128 || alpha < -128){
			System.out.println("bad color parameter(s) (below -128!)");
			return;
		}

		if(red > 127 || green > 127 || blue > 127 || alpha > 127){
			System.out.println("bad color parameter(s) (over 127!)");
			return;
		}

		this.redValue = (red < 0) ? red + 256 : red;
		this.greenValue = (green < 0) ? green + 256 : green;
		this.blueValue = (blue < 0) ? blue + 256 : blue;
		this.alphaValue = (alpha < 0) ? alpha + 256 : alpha;
	}

	Color(int color){
		blueValue = color & 0x000000FF;
		greenValue = (color >> 8) & 0x000000FF;
		redValue = (color >> 16) & 0x000000FF;
		alphaValue = (color >> 24) & 0x000000FF;
	}

	public boolean equals(Color otherColor){
		return (this.getRed() == otherColor.getRed() && this.getGreen() == otherColor.getGreen() && this.getBlue() == otherColor.getBlue() && this.getAlpha() == otherColor.getAlpha());
	}

	public int getRed(){
		return redValue;
	}

	public int getGreen(){
		return greenValue;
	}

	public int getBlue(){
		return blueValue;
	}
	
	public int getAlpha(){
		return alphaValue;
	}

	public byte getRedByte(){
		return (byte)((redValue > 127)? redValue - 256 : redValue);
	}

	public byte getGreenByte(){
		return (byte)((greenValue > 127)? greenValue - 256 : greenValue);
	}

	public byte getBlueByte(){
		return (byte)((blueValue > 127)? blueValue - 256 : blueValue);
	}
	
	public byte getAlphaByte(){
		return (byte)((alphaValue > 127)? alphaValue - 256 : alphaValue);
	}

	public short getBrightness(){
		return (short)(redValue + blueValue + greenValue);
                // real formula is Math.sqrt(red * red * .241 + green * green * .691 + blue * blue * .068)
                // but we aren't interested in the human perception in this case
	}

	public int getIntColor(){
		// doesn't include alpha at the moment. May want to change that later
		return (((alphaValue << 8) | redValue) << 8 | greenValue) << 8 | blueValue;
	}

	public static int getIntColor(byte red, byte green, byte blue){
		// doesn't include alpha at the moment. May want to change that later
		return (((0xFF << 8) | ((red < 0) ? red + 256 : red)) << 8 | ((green < 0) ? green + 256 : green)) << 8 | ((blue < 0) ? blue + 256 : blue);
	}

	@Override
	public String toString(){
		return "ColorToString ("+redValue+", "+greenValue+", "+blueValue+", "+alphaValue+")";
	}
}
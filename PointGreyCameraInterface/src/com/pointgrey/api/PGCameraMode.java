/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pointgrey.api;

/**
 *
 * @author Matt
 */
public class PGCameraMode {
	private final PGFrameRateMode frameRateMode;
	private final PGVideoMode videoMode;
	
	public PGCameraMode(PGVideoMode videoMode, PGFrameRateMode frameRateMode){
		this.frameRateMode = frameRateMode;
		this.videoMode = videoMode;
	}
	
	public PGFrameRateMode getFrameRateMode(){
		return frameRateMode;
	}
	
	public PGVideoMode getVideoMode(){
		return videoMode;
	}
	
	public int getNeededByteBufferSize(){ //The camera currently always returns in BGR format, so it's always 3 bytes per pixels until this is changed.
		return videoMode.getWidth() * videoMode.getHeight() * 3;
	}
        
        @Override
        public String toString(){
            return "Video Mode: " + videoMode + " FrameRate Mode: " + frameRateMode + " Resolution (" + videoMode.getWidth() + ", " + videoMode.getHeight() + ")";
        }
}
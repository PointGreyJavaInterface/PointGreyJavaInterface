/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pointgrey.api;

/**
 * This class is an enumeration of the video modes that we know Point Grey to support.
 * There may exist more video modes which are not yet added here.
 * @author Matt
 */
    public enum PGVideoMode //23 video modes....
    {        
        VIDEOMODE_160x120YUV444(160, 120, 0), /**< 160x120 YUV444. */
        VIDEOMODE_320x240YUV422(320, 240, 0), /**< 320x240 YUV422. */
        VIDEOMODE_640x480YUV411(640, 480, 0), /**< 640x480 YUV411. */
        VIDEOMODE_640x480YUV422(640, 480, 0), /**< 640x480 YUV422. */
        VIDEOMODE_640x480RGB(640, 480, 0), /**< 640x480 24-bit RGB. */
        VIDEOMODE_640x480Y8(640, 480, 0), /**< 640x480 8-bit. */
        VIDEOMODE_640x480Y16(640, 480, 0), /**< 640x480 16-bit. */
        VIDEOMODE_800x600YUV422(800, 600, 0), /**< 800x600 YUV422. */
        VIDEOMODE_800x600RGB(800, 600, 0), /**< 800x600 RGB. */
        VIDEOMODE_800x600Y8(800, 600, 0), /**< 800x600 8-bit. */
        VIDEOMODE_800x600Y16(800, 600, 0), /**< 800x600 16-bit. */
        VIDEOMODE_1024x768YUV422(1024, 768, 0), /**< 1024x768 YUV422. */
        VIDEOMODE_1024x768RGB(1024, 768, 0), /**< 1024x768 RGB. */
        VIDEOMODE_1024x768Y8(1024, 768, 0), /**< 1024x768 8-bit. */
        VIDEOMODE_1024x768Y16(1024, 768, 0), /**< 1024x768 16-bit. */
        VIDEOMODE_1280x960YUV422(1280, 960, 0), /**< 1280x960 YUV422. */
        VIDEOMODE_1280x960RGB(1280, 960, 0), /**< 1280x960 RGB. */
        VIDEOMODE_1280x960Y8(1280, 960, 0), /**< 1280x960 8-bit. */
        VIDEOMODE_1280x960Y16(1280, 960, 0), /**< 1280x960 16-bit. */
        VIDEOMODE_1600x1200YUV422(1600, 1200, 0), /**< 1600x1200 YUV422. */
        VIDEOMODE_1600x1200RGB(1600, 1200, 0), /**< 1600x1200 RGB. */
        VIDEOMODE_1600x1200Y8(1600, 1200, 0), /**< 1600x1200 8-bit. */
        VIDEOMODE_1600x1200Y16(1600, 1200, 0), /**< 1600x1200 16-bit. */
        VIDEOMODE_FORMAT7(0, 0, 0), /**< Custom video mode for Format7 functionality. */
        NUM_VIDEOMODES(0, 0, 0), /**< Number of possible video modes. */
        VIDEOMODE_FORCE_32BITS(0, 0, 0); //Value is currently unknown
	
	private final int width, height, bitsPerPixel; // bytes per pixel
	
	private PGVideoMode(int width, int height, int bitsPerPixel){
            this.width = width;
            this.height = height;
            this.bitsPerPixel = bitsPerPixel;
	}
	
	public int getWidth(){
            return width;
	}
	
	public int getHeight(){
            return height;
	}
        
        public int getBitsPerPixel(){
            return bitsPerPixel;
        }
    };
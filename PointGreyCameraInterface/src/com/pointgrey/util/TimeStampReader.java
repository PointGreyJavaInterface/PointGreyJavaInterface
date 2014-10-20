package com.pointgrey.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * @author Matt
 * 
 * FROM THE POINTGREY DOCS: http://www.ptgrey.com/support/downloads/downloads_admin/Dlds/Flea3%20USB3%20Technical%20Reference.pdf
 * Interpreting Timestamp information
 * The Timestamp format is as follows (some cameras replace the bottom 4 bits of the cycle offset with a 4-bit version of the Frame Counter):
 * Cycle_offset increments from 0 to 3071, which equals one cycle_count.
 * Cycle_count increments from 0 to 7999, which equals one second.
 * Second_count increments from 0 to 127. All counters reset to 0 at the end of each cycle.
 * 
 * Note: on USB 3.0 devices, the four least significant bits of the timestamp do not
 * accurately reflect the cycle_offset and should be discounted.
 */
public class TimeStampReader {

    public static double readTimeStamp(BufferedImage image) throws InvalidFrameTimestampException {
        try {
            byte[] imageData = ((DataBufferByte) (image.getRaster().getDataBuffer())).getData();

            return getTimeStamp(imageData[0], imageData[1], imageData[2], imageData[3]);
        } catch (Exception e) {
            throw new InvalidFrameTimestampException("unable to decode timestamp");
        }
    }

    private static double getTimeStamp(byte byte0, byte byte1, byte byte2, byte byte3) {
        long nSeconds, nCycleCount;
        long rawTime = 0;

        // Put the bytes in little endian format
        // We have to use the ternary stuff because Java is dumb and doesn't support unsigned types
        rawTime += byte0 < 0 ? (int) byte0 + 256 : byte0;
        rawTime = rawTime << 8;
        rawTime += byte1 < 0 ? (int) byte1 + 256 : byte1;
        rawTime = rawTime << 8;
        rawTime += byte2 < 0 ? (int) byte2 + 256 : byte2;
        rawTime = rawTime << 8;
        rawTime += byte3 < 0 ? (int) byte3 + 256 : byte3;

        // Read the timestamp
        nSeconds = (rawTime >> 25);
        nCycleCount = (rawTime >> 12) & 0x1FFF;

        return (double) nSeconds + nCycleCount / 8000.0d;
    }
}
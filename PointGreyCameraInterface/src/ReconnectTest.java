
import com.pointgrey.api.PGCameraMode;
import static com.pointgrey.api.PointGreyCameraInterface.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ReconnectTest {

	public static void main(String[] args) {
		System.out.println("Creating a context for the camera.");
		createPGContext();

		System.out.println("\nThere are " + getNumCameras() + " camera(s) connected to the system");
		System.out.println("Connecting to the default camera.");
		connectCamera();
		System.out.println("Connected to: " + getConnectedCameraName());

		System.out.println("\nStarting capture.");
		startCapturing();

		// set camera to first mode
		PGCameraMode[] modes = getSupportedCameraModes();
		PGCameraMode mode = modes[0];
		setCameraMode(mode); // setting camera mode stops capture
		startCapturing();
		
		//Currently an image in 3 byte BGR format will always be returned by the camera. Therefore we need width * height * 3 bytes to store an image.
		BufferedImage img = new BufferedImage(mode.getVideoMode().getWidth(), mode.getVideoMode().getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		byte[] outImageByteArrayReference = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < 50; i++) {
			try {
				System.out.println("capturing image " + i);
				storeImageInBuffer(outImageByteArrayReference); //storeImage stores an image inside of the byte buffer passed to it.
				ImageIO.write(img, "png", new File("" + mode.getVideoMode() + "" + mode.getFrameRateMode() + ".png"));
			} catch (IOException e) {
				System.out.println("can't write image file");
			} catch (Exception e) {
				System.out.println("other image writing failure");
			}
		}

		System.out.println("\nStopping capture.");
		stopCapturing();
		System.out.println("Destroying camera context.");
		destroyPGContext();
	}
}
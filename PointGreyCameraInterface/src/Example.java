
import com.pointgrey.api.PGCameraFeatureControl;
import com.pointgrey.api.PGCameraMode;
import com.pointgrey.api.PGPropertyInfo;
import com.pointgrey.api.PGPropertyType;
import static com.pointgrey.api.PointGreyCameraInterface.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Example {

	public static void main(String[] args) {
		System.out.println("Creating a context for the camera.");
		createPGContext();

		System.out.println("\nThere are " + getNumCameras() + " camera(s) connected to the system");
		System.out.println("Connecting to the default camera.");
		connectCamera();
		System.out.println("Connected to: " + getConnectedCameraName());

		System.out.println("\nStarting capture.");
		startCapturing();

		//Check each property type that may be available.
		System.out.println("\nListing available properties.");
		for (PGPropertyType type : PGPropertyType.values()) {
			PGPropertyInfo info = getPropertyInfo(type);
			//If the property is supported by the camera, print out the property's information.
			if (info.present) {
				System.out.println(info);
			}
		}

		//PGCameraFeatureControls can be used to control properties on the camera. Features that would normally be controlled automatically are put in manual mode if changed.
		PGCameraFeatureControl brightnessControl = new PGCameraFeatureControl(PGPropertyType.FC2_BRIGHTNESS);

		//Save an image using each of the camera's supported modes with different brightnesses.
		int imageCount = 0;
		System.out.println("Saving an image for each supported mode with different brightness values.");
		for (PGCameraMode mode : getSupportedCameraModes()) {
			//If the brightness feature is present, change it.
			if (brightnessControl.isFeaturePresent()) {
				brightnessControl.write(brightnessControl.getMaximum() / (imageCount + 1));
			}
			System.out.println("Capturing an image with mode: " + mode + " and brightness " + (int) brightnessControl.getCurrentValue());

			//Changing the camera mode will cause the camera to stop capturing, it must be started again.
			setCameraMode(mode);
			startCapturing();

			//Currently an image in 3 byte BGR format will always be returned by the camera. Therefore we need width * height * 3 bytes to store an image.
			BufferedImage img = new BufferedImage(mode.getVideoMode().getWidth(), mode.getVideoMode().getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			byte[] outImageByteArrayReference = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
			storeImageInBuffer(outImageByteArrayReference); //storeImage stores an image inside of the byte buffer passed to it.

			try {
				ImageIO.write(img, "png", new File("" + mode.getVideoMode() + "" + mode.getFrameRateMode() + ".png"));
			} catch (IOException e) {
				System.out.println("can't write image file");
			} catch (Exception e) {
				System.out.println("other image writing failure");
				e.printStackTrace();
			}

			imageCount++;
		}

		System.out.println("\nStopping capture.");
		stopCapturing();
		System.out.println("Destroying camera context.");
		destroyPGContext();

		System.out.println("\nFinished! " + imageCount + " images were saved!");
	}
}
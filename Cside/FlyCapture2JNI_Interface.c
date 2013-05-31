#if defined(WIN32) || defined(WIN64)
#define _CRT_SECURE_NO_WARNINGS		
#endif

#include "C/FlyCapture2_C.h"
#include <stdio.h>
#include <string.h>

#include "PointGreyCameraInterface.h"

fc2Context context;
fc2Image latestImage;
fc2Image latestConvertedImage;
unsigned char * C_convertedImagePData;
int C_convertedImageDataSize;
int VideoModes[] = {FC2_VIDEOMODE_160x120YUV444, FC2_VIDEOMODE_320x240YUV422, FC2_VIDEOMODE_640x480YUV411, FC2_VIDEOMODE_640x480YUV422, FC2_VIDEOMODE_640x480RGB,
					FC2_VIDEOMODE_640x480Y8, FC2_VIDEOMODE_640x480Y16, FC2_VIDEOMODE_800x600YUV422, FC2_VIDEOMODE_800x600RGB, FC2_VIDEOMODE_800x600Y8, FC2_VIDEOMODE_800x600Y16,
					FC2_VIDEOMODE_1024x768YUV422, FC2_VIDEOMODE_1024x768RGB, FC2_VIDEOMODE_1024x768Y8, FC2_VIDEOMODE_1024x768Y16, FC2_VIDEOMODE_1280x960YUV422,
					FC2_VIDEOMODE_1280x960RGB, FC2_VIDEOMODE_1280x960Y8, FC2_VIDEOMODE_1280x960Y16, FC2_VIDEOMODE_1600x1200YUV422, FC2_VIDEOMODE_1600x1200RGB,
					FC2_VIDEOMODE_1600x1200Y8, FC2_VIDEOMODE_1600x1200Y16};//, FC2_VIDEOMODE_FORMAT7, FC2_NUM_VIDEOMODES, FC2_VIDEOMODE_FORCE_32BITS};
int FramerateModes[] = {FC2_FRAMERATE_1_875, FC2_FRAMERATE_3_75, FC2_FRAMERATE_7_5, FC2_FRAMERATE_15, FC2_FRAMERATE_30, FC2_FRAMERATE_60, FC2_FRAMERATE_120, FC2_FRAMERATE_240}; 
						//FC2_FRAMERATE_FORMAT7, FC2_NUM_FRAMERATES, FC2_FRAMERATE_FORCE_32BITS};

char* getError(int error){
	char* output;

	switch(error){
	case FC2_ERROR_UNDEFINED: output = "Undefined"; break;
	case FC2_ERROR_OK: output = "Function returned with no errors."; break;
	case FC2_ERROR_FAILED: output = "General failure."; break;
	case FC2_ERROR_NOT_IMPLEMENTED: output = "Function has not been implemented."; break;
	case FC2_ERROR_FAILED_BUS_MASTER_CONNECTION: output = "Could not connect to Bus Master."; break;
	case FC2_ERROR_NOT_CONNECTED: output = "Camera has not been connected."; break;
	case FC2_ERROR_INIT_FAILED: output = "Initialization failed."; break; 
	case FC2_ERROR_NOT_INTITIALIZED: output = "Camera has not been initialized."; break;
	case FC2_ERROR_INVALID_PARAMETER: output = "Invalid parameter passed to function."; break;
	case FC2_ERROR_INVALID_SETTINGS: output = "Setting set to camera is invalid."; break;         
	case FC2_ERROR_INVALID_BUS_MANAGER: output = "Invalid Bus Manager object."; break;
	case FC2_ERROR_MEMORY_ALLOCATION_FAILED: output = "Could not allocate memory."; break; 
	case FC2_ERROR_LOW_LEVEL_FAILURE: output = "Low level error."; break;
	case FC2_ERROR_NOT_FOUND: output = "Device not found."; break;
	case FC2_ERROR_FAILED_GUID: output = "GUID failure."; break;
	case FC2_ERROR_INVALID_PACKET_SIZE: output = "Packet size set to camera is invalid."; break;
	case FC2_ERROR_INVALID_MODE: output = "Invalid mode has been passed to function."; break;
	case FC2_ERROR_NOT_IN_FORMAT7: output = "Error due to not being in Format7."; break;
	case FC2_ERROR_NOT_SUPPORTED: output = "This feature is unsupported."; break;
	case FC2_ERROR_TIMEOUT: output = "Timeout error."; break;
	case FC2_ERROR_BUS_MASTER_FAILED: output = "Bus Master Failure."; break;
	case FC2_ERROR_INVALID_GENERATION: output = "Generation Count Mismatch."; break;
	case FC2_ERROR_LUT_FAILED: output = "Look Up Table failure."; break;
	case FC2_ERROR_IIDC_FAILED: output = "IIDC failure."; break;
	case FC2_ERROR_STROBE_FAILED: output = "Strobe failure."; break;
	case FC2_ERROR_TRIGGER_FAILED: output = "Trigger failure."; break;
	case FC2_ERROR_PROPERTY_FAILED: output = "Property failure."; break;
	case FC2_ERROR_PROPERTY_NOT_PRESENT: output = "Property is not present."; break;
	case FC2_ERROR_REGISTER_FAILED: output = "Register access failed."; break;
	case FC2_ERROR_READ_REGISTER_FAILED: output = "Register read failed."; break;
	case FC2_ERROR_WRITE_REGISTER_FAILED: output = "Register write failed."; break;
	case FC2_ERROR_ISOCH_FAILED: output = "Isochronous failure."; break;
	case FC2_ERROR_ISOCH_ALREADY_STARTED: output = "Isochronous transfer has already been started."; break;
	case FC2_ERROR_ISOCH_NOT_STARTED: output = "Isochronous transfer has not been started."; break;
	case FC2_ERROR_ISOCH_START_FAILED: output = "Isochronous start failed."; break;
	case FC2_ERROR_ISOCH_RETRIEVE_BUFFER_FAILED: output = "Isochronous retrieve buffer failed."; break;
	case FC2_ERROR_ISOCH_STOP_FAILED: output = "Isochronous stop failed."; break;
	case FC2_ERROR_ISOCH_SYNC_FAILED: output = "Isochronous image synchronization failed."; break;
	case FC2_ERROR_ISOCH_BANDWIDTH_EXCEEDED: output = "Isochronous bandwidth exceeded."; break;
	case FC2_ERROR_IMAGE_CONVERSION_FAILED: output = "Image conversion failed."; break;
	case FC2_ERROR_IMAGE_LIBRARY_FAILURE: output = "Image library failure."; break;
	case FC2_ERROR_BUFFER_TOO_SMALL: output = "Buffer is too small."; break;
	case FC2_ERROR_IMAGE_CONSISTENCY_ERROR: output = "There is an image consistency error."; break;
	case FC2_ERROR_FORCE_32BITS: output = "Force 32 bits";
	}
	return output;
}

void PrintBuildInfo() {
    fc2Version version;
    char versionStr[512];
    char timeStamp[512];

    fc2GetLibraryVersion( &version );

    sprintf(versionStr, "FlyCapture2 library version: %d.%d.%d.%d\n", version.major, version.minor, version.type, version.build);

    printf("%s", versionStr);

    sprintf(timeStamp, "Application build date: %s %s\n\n", __DATE__, __TIME__);

    printf("%s", timeStamp);
}

void PrintCameraInfo() {
    fc2CameraInfo camInfo;
    fc2GetCameraInfo(context, &camInfo);

    printf(
        "\n*** CAMERA INFORMATION ***\n"
        "Serial number - %u\n"
        "Camera model - %s\n"
        "Camera vendor - %s\n"
        "Sensor - %s\n"
        "Resolution - %s\n"
        "Firmware version - %s\n"
        "Firmware build time - %s\n\n",
        camInfo.serialNumber,
        camInfo.modelName,
        camInfo.vendorName,
        camInfo.sensorInfo,
        camInfo.sensorResolution,
        camInfo.firmwareVersion,
        camInfo.firmwareBuildTime);
}

JNIEXPORT jintArray JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getSupportedModes(JNIEnv *env, jobject thisClass){
	fc2VideoMode testMode;
	fc2FrameRate testFramerate;
	int i;
	int j;
	int supportedModes = FC2_NUM_VIDEOMODES-2; //Not messing with format7 right now.
	int supportedFramerates = FC2_NUM_FRAMERATES -2; //Again not messing with format7 right now.
	int retSupportedIndex = 0;
	int* retSupported;
	jintArray retJava;
	jint* bufferPtr;
	BOOL isSupported;
	int error;
	char exBuffer[128];

	retSupported = (int*)malloc(sizeof(int)*supportedModes * supportedFramerates);

	for(i = 0; i < supportedModes; i++){
		for(j = 0; j < supportedFramerates; j++){
			testMode = (fc2VideoMode)VideoModes[i];
			testFramerate = (fc2FrameRate)FramerateModes[j];
			error = fc2GetVideoModeAndFrameRateInfo(context, testMode, testFramerate, &isSupported);

			if(error != FC2_ERROR_OK){
				sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetVideoModeAndFrameRateInfo returned error", getError(error));
				(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
				return NULL;
			}

			if(isSupported){
				retSupported[retSupportedIndex] = i;
				retSupported[retSupportedIndex + 1] = j;
				retSupportedIndex += 2;
				//printf("Mode %d Framerate %d (i = %d, j = %d) is supported!!\n", testMode, testFramerate, i, j);
			}
		}
	}

	retJava = (*env)->NewIntArray(env, retSupportedIndex); // works even though visual studio 2012 underlines as red

	bufferPtr = (*env)->GetIntArrayElements(env, retJava, NULL); // works even though visual studio 2012 underlines as red

	for(i = 0; i < retSupportedIndex; i++){ // this is horribly inefficient...must find a better way
		bufferPtr[i] = retSupported[i];
	}
	
	(*env)->ReleaseIntArrayElements(env, retJava, bufferPtr, 0); // a bit of cleanup

	free(retSupported);

	return retJava;
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_setCameraMode(JNIEnv *env, jclass thisClass, jint vidMode, jint frameMode){
	char exBuffer[128];
	int error;

	fc2StopCapture(context); // if camera is currently capturing it should be stopped.

	error = fc2SetVideoModeAndFrameRate(context, (fc2VideoMode)vidMode, (fc2FrameRate)frameMode);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2SetVideoModeAndFrameRate returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
}
 
void SetTimeStamping(BOOL enableTimeStamp) {
    fc2EmbeddedImageInfo embeddedInfo;

    fc2GetEmbeddedImageInfo(context, &embeddedInfo);

    if (embeddedInfo.timestamp.available != 0) {       
        embeddedInfo.timestamp.onOff = enableTimeStamp;
    }    

    fc2SetEmbeddedImageInfo(context, &embeddedInfo);
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_createContext(JNIEnv *env, jclass thisClass){
	int error = fc2CreateContext(&context);
	char exBuffer[128];

	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2CreateContext returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
	}
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_destroyContext(JNIEnv *env, jclass thisClass){
	int error = fc2DestroyContext(context);
	char exBuffer[128];

	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2DestroyContext returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
	}
}

JNIEXPORT jint JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getNumOfCameras(JNIEnv *env, jclass thisClass){
	unsigned int numOfCameras;
	int error = fc2GetNumOfCameras(context, &numOfCameras);
	char exBuffer[128];

	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetNumOfCameras returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return -1;
	}

	return numOfCameras;
}

JNIEXPORT jlong JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getSerialFromIndex(JNIEnv *env, jclass thisClass, jlong index){
	int error;
	char exBuffer[128];
	unsigned int serialNumber;

	error = fc2GetCameraSerialNumberFromIndex(context, (unsigned int)index, &serialNumber);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetCameraFromIndex returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return -1;
	}

	return (jlong)serialNumber;
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_connectToDefaultCamera(JNIEnv *env, jclass thisClass){
	fc2PGRGuid guid;
	int error;
	char exBuffer[128];

	 // Get the 0th camera
    error = fc2GetCameraFromIndex(context, 0, &guid);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetCameraFromIndex returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

    error = fc2Connect(context, &guid);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2Connect returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_connectCameraWithSerial(JNIEnv *env, jclass thisClass, jlong serial){
	fc2PGRGuid guid;
	int error;
	char exBuffer[128];

	error = fc2GetCameraFromSerialNumber(context, (unsigned int)serial, &guid);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetCameraFromSerial returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

    error = fc2Connect(context, &guid);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2Connect returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_startCapture(JNIEnv *env, jclass thisClass){
	int error;
	char exBuffer[128];  
	
	error = fc2StartCapture(context);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2StartCapture returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	SetTimeStamping(TRUE); // this puts the timestamp in the first few pixels (upper left-hand corner) of the image

	error = fc2CreateImage(&latestImage);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2CreateImage returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2CreateImage(&latestConvertedImage);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2CreateImage returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_stopCapture(JNIEnv *env, jclass thisClass){
	int error;
	char exBuffer[128];

	error = fc2SetImageData(&latestConvertedImage, NULL, 0);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2SetImageData returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2StopCapture(context);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2StopCapture returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2SetImageData(&latestConvertedImage, C_convertedImagePData, C_convertedImageDataSize);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2SetImageData returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2DestroyImage(&latestImage); //This might cause an issue down the road, but probably not.
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2DestroyImage returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2DestroyImage(&latestConvertedImage);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2DestroyImage returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
}

void printPropertyValues(fc2PropertyType propertyNo){
	fc2PropertyInfo prop;
	prop.type = propertyNo;

	fc2GetPropertyInfo(context, &prop);

	printf("Property #%d: \n\tpresent(%d) \n\tautoSupported(%d) \n\tmanualSupported(%d) \n\tonOffSupported(%d) \n\tonePushSupported(%d) \n\tabsValSupported(%d) \n\treadOutSupported(%d) \n\tmin(%d) \n\tmax(%d) \n\tabsMin(%f) \n\tabsMax(%f) \n",
			prop.type,	 prop.present,	prop.autoSupported,		prop.manualSupported,	prop.onOffSupported,  prop.onePushSupported,  prop.absValSupported,	  prop.readOutSupported,	prop.min,   prop.max,	 prop.absMin,	prop.absMax);
}

JNIEXPORT jobject JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getPropertyInfo(JNIEnv *env, jobject thisClass, jint propertyNo){
	  jclass retClass = (*env)->FindClass(env, "Lcom/pointgrey/api/PGPropertyInfo;");
	  jmethodID retConstructor = (*env)->GetMethodID(env, retClass, "<init>", "()V");
	  jobject retJava = (*env)->NewObject(env, retClass, retConstructor);
	  jfieldID currField;
	  fc2PropertyInfo prop;
	  int error;
	  char exBuffer[128];

	  prop.type = propertyNo;
	  error = fc2GetPropertyInfo(context, &prop);
		if(error != FC2_ERROR_OK){
			sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetPropertyInfo returned error", getError(error));
			(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
			return NULL;
		}

	  currField = (*env)->GetFieldID(env, retClass, "present", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.present);
	  currField = (*env)->GetFieldID(env, retClass, "autoSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.autoSupported);
	  currField = (*env)->GetFieldID(env, retClass, "manualSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.manualSupported);
	  currField = (*env)->GetFieldID(env, retClass, "onOffSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.onOffSupported);
	  currField = (*env)->GetFieldID(env, retClass, "onePushSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.onePushSupported);
	  currField = (*env)->GetFieldID(env, retClass, "absValSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.absValSupported);
	  currField = (*env)->GetFieldID(env, retClass, "readOutSupported", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.readOutSupported);
	  currField = (*env)->GetFieldID(env, retClass, "min", "I");
	  (*env)->SetIntField(env, retJava, currField, (jint)prop.min);
	  currField = (*env)->GetFieldID(env, retClass, "max", "I");
	  (*env)->SetIntField(env, retJava, currField, (jint)prop.max);
	  currField = (*env)->GetFieldID(env, retClass, "absMin", "F");
	  (*env)->SetFloatField(env, retJava, currField, (jfloat)prop.absMin);
	  currField = (*env)->GetFieldID(env, retClass, "absMax", "F");
	  (*env)->SetFloatField(env, retJava, currField, (jfloat)prop.absMax);

	return retJava;
}

JNIEXPORT jobject JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getProperty(JNIEnv *env, jobject thisClass, jint propertyNo){
	  jclass retClass = (*env)->FindClass(env, "Lcom/pointgrey/api/PGProperty;");
	  jmethodID retConstructor = (*env)->GetMethodID(env, retClass, "<init>", "()V");
	  jobject retJava = (*env)->NewObject(env, retClass, retConstructor);
	  jfieldID currField;
	  fc2Property prop;
	  int error;
	  char exBuffer[128];

	  prop.type = propertyNo;
	  error = fc2GetProperty(context, &prop);
		if(error != FC2_ERROR_OK){
			sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetProperty returned error", getError(error));
			(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
			return NULL;
		}

	  currField = (*env)->GetFieldID(env, retClass, "present", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.present);
	  currField = (*env)->GetFieldID(env, retClass, "absControl", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, prop.absControl);
	  currField = (*env)->GetFieldID(env, retClass, "onePush", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.onePush);
	  currField = (*env)->GetFieldID(env, retClass, "onOff", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.onOff);
	  currField = (*env)->GetFieldID(env, retClass, "autoManualMode", "Z");
	  (*env)->SetBooleanField(env, retJava, currField, (jboolean)prop.autoManualMode);
	  currField = (*env)->GetFieldID(env, retClass, "valueA", "I");
	  (*env)->SetIntField(env, retJava, currField, (jint)prop.valueA);
	  currField = (*env)->GetFieldID(env, retClass, "valueB", "I");
	  (*env)->SetIntField(env, retJava, currField, (jint)prop.valueB);
	  currField = (*env)->GetFieldID(env, retClass, "absValue", "F");
	  (*env)->SetFloatField(env, retJava, currField, (jfloat)prop.absValue);

	return retJava;
}

void main(){
	  fc2PGRGuid guid;
	  int i;
	  printf("Create context %d \n", fc2CreateContext(&context));
	  printf("Get camera from index %d \n", fc2GetCameraFromIndex(context, 0, &guid));
      printf("connect %d \n", fc2Connect(context, &guid));
	  //SetTimeStamping(TRUE);
	  //printf("start capture %d \n",fc2StartCapture(context));
	  //fc2CreateImage(&latestImage);
	  //fc2CreateImage(&latestConvertedImage);

	  //printf("Retrieve buffer %d\n", fc2RetrieveBuffer(context, &latestImage));
	  //fc2ConvertImageTo(FC2_PIXEL_FORMAT_BGR, &latestImage, &latestConvertedImage);
	  //fc2SaveImage(&latestConvertedImage, "testfc2", FC2_PNG);
	  //fc2RetrieveBuffer(context, &latestImage);

	  for(i = (int)FC2_BRIGHTNESS; i <= (int)FC2_TEMPERATURE; i++)
		printPropertyValues(i);
	  // Save it to PNG for comparison with the image output through java. should output to the project directory root
   //printf("Saving the last image to fc2outimage.png %d\n", fc2SaveImage(&latestConvertedImage, "fc2TestImage.png", FC2_PNG));
	  //fc2SaveImage(&latestConvertedImage, "fc2mage.png", FC2_PNG);
	  //fc2StopCapture(context);
	  fc2DestroyContext(context);
}



JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_storeImage(JNIEnv *env, jclass thisClass, jbyteArray byteArray){
	jbyte* bufferPtr;
	int error;
	char exBuffer[128];

	error = fc2RetrieveBuffer(context, &latestImage);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2RetrieveBuffer returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}
	
	// Here be smaller-than-usual dragons that breathe inefficient JNI code.
	bufferPtr = (*env)->GetByteArrayElements(env, byteArray, NULL);

	//Save a reference to the old pData and size before switching to Java's (Java don't take too kindly to freeing up its memory when you don't possess it)
	C_convertedImagePData = latestConvertedImage.pData;
	C_convertedImageDataSize = latestConvertedImage.dataSize;

	error = fc2SetImageData(&latestConvertedImage, (unsigned char*)bufferPtr, (*env)->GetArrayLength(env, byteArray));
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2ImageData returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	error = fc2ConvertImageTo(FC2_PIXEL_FORMAT_BGR, &latestImage, &latestConvertedImage);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2ConvertImageTo returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	//Restore latestConvertedImageData to its un-javafied state.
	error = fc2SetImageData(&latestConvertedImage, NULL, 0);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2SetImageData returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return;
	}

	//And give Java back its garbage so that it can collect it
	(*env)->ReleaseByteArrayElements(env, byteArray, bufferPtr, 0); // a bit of cleanup
	// end smaller-than-usual dragons that breathe inefficient JNI code.
}

JNIEXPORT jstring JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_getCameraName(JNIEnv *env, jclass thisClass){
	char nameBuffer[256];
	fc2CameraInfo camInfo;
	jstring retJava;
	int error;
	char exBuffer[128];

	error = fc2GetCameraInfo(context, &camInfo);
	if(error != FC2_ERROR_OK){
		sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2GetCameraInfo returned error", getError(error));
		(*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
		return NULL;
	}

	sprintf(nameBuffer, "%s - %u", camInfo.modelName, camInfo.serialNumber);

	//retJava = (*env)->NewString(env, (jchar*)nameBuffer, (jsize)strlen(nameBuffer));
	retJava = (*env)->NewStringUTF(env, nameBuffer);

	return retJava;
}

JNIEXPORT void JNICALL Java_com_pointgrey_api_PointGreyCameraInterface_setProperty(JNIEnv *env, jclass thisClass, jint propertyNo, jobject PGPropertyRef){
	  jclass retClass = (*env)->FindClass(env, "Lcom/pointgrey/api/PGProperty;");
	  jfieldID currField;
	  fc2Property prop;
	  int error;
	  char exBuffer[128];

	  currField = (*env)->GetFieldID(env, retClass, "present", "Z");
	  prop.present = (*env)->GetBooleanField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "absControl", "Z");
	  prop.absControl = (*env)->GetBooleanField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "onePush", "Z");
	  prop.onePush = (*env)->GetBooleanField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "onOff", "Z");
	  prop.onOff = (*env)->GetBooleanField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "autoManualMode", "Z");
	  prop.autoManualMode = (*env)->GetBooleanField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "valueA", "I");
	  prop.valueA = (*env)->GetIntField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "valueB", "I");
	  prop.valueB = (*env)->GetIntField(env, PGPropertyRef, currField);
	  currField = (*env)->GetFieldID(env, retClass, "absValue", "F");
	  prop.absValue = (*env)->GetFloatField(env, PGPropertyRef, currField);
	  
	  prop.type = propertyNo;
	  error = fc2SetProperty(context, &prop);
	  if(error != FC2_ERROR_OK){
		  sprintf(exBuffer, "JNI Exception in PointGrey Interface: %s \"%s\"", "fc2SetProperty returned error", getError(error));
		  (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), exBuffer);
	  }
}
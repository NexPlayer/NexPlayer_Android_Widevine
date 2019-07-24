## How to use Hardware/Software Widevine DRM for Android

There are two options to configure Widevine DRM. One is using Hardware and the other is using Software.
This document explains how to use not only Hardware Widevine but also Software Widevine and assumes that you are aware of how to set NexPlayer for Android.
Please refer to [NexPlayer for Android](https://github.com/NexPlayerSDK/NexPlayer_Android) if you are not sure about how to implement NexPlayer on Android.

***
### 1. Hardware Widevine with NexPlayer
Using Hardware Widevine with NexPlayer means using MediaDrm. MediaDrm supports both 32-bit CPU architecture and 64-bit CPU architecture. Here is sample code for how to set Hardware Widevine with NexPlayer below. For more information about MediaDrm, please refer to https://developer.android.com/reference/android/media/MediaDrm

### _MainActivity.java_
```java
...

private void startPlay() {
	String contentUrl = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd";
	String keyServerUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test";

	int drmType = 0;

	// HARDWARE WIDEVINE CONFIGURATION
	mNexPlayer.setNexMediaDrmKeyServerUri(keyServerUrl);
	drmType |= 1;

	mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);

	int result = mNexPlayer.open(contentUrl, null, null, NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING, NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
	if( result != 0 ) {
		Log.e(LOG_TAG, "mNexPlayer open failed");
	}
}
```

Hardware Widevine configuration should be done before _open_ method of NexPlayer object is called.
When Hardware Widevine is used, keyServerUrl should be passed to _setNexMediaDrmKeyServerUri_ method of NexPlayer object and drmType should be passed to _setProperties_ method. In case of Hardware Widevine, drmType should be set to 1.

***
### 2. Software Widevine with NexPlayer

Software Widevine only supports 32-bit CPU architecture since Google does not support 64-bit Software Widevine at this point. By the time 64-bit Software Widevine is supported, this document will be updated as well. To use Software Widevine, these three settings below should be confirmed.
1. _~/YourAndroidProject/app/libs/YourCPUArchitecture/libnexwvdrm.so_ should exist.
2. In _YourPlayer.java_, NexWVDRM object should be initialized in order to set Software Widevine.
3. _setProperties_ should be set with drmType.

In YourAndroidProject, _libnexwvdrm.so_ should exist in _~/YourAndroidProject/app/libs/YourCPUArchitecture/libnexwvdrm.so_ and here is sample code for Software Widevine usage in _MainActivity.java_ below.
#### _MainActivity.java_
```java
...

private void startPlay() {
	String contentUrl = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd";
	String keyServerUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test";

	int drmType = 0;

	// SOFTWARE WIDEVINE CONFIGURATION
        mNexWVDRM = new NexWVDRM();
        File fileDir = this.getFilesDir();
        String strCertPath = fileDir.getAbsolutePath() + "/wvcert";

        int offlineMode = 0;
        if(mNexWVDRM.initDRMManager(getEnginePath(this), strCertPath, keyServerUrl, offlineMode) == 0) {
            drmType |= 2;
        }

	mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);

	int result = mNexPlayer.open(contentUrl, null, null, NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING, NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
	if( result != 0 ) {
		Log.e(LOG_TAG, "mNexPlayer open failed");
	}
}

...

public static String getEnginePath(Context context) {
        String engine = "libnexplayerengine.so";
        String ret = context.getApplicationInfo().dataDir + "/lib/" + engine;
        return ret;
}

...

```
Software Widevine configuration should be done before _open_ method of NexPlayer object is called as well.
keyServerUrl should be passed to _initDRMManager_ method of NexWVDRM object. Lastly, drmType should be passed to _setProperties_ and for Software Widevine usage, drmType should be set to 2.
***

If you want to support only for 32-bit CPU architecture Android application with Widevine, Hardware Widevine should be configured, which means NexWVDRM object should be removed.

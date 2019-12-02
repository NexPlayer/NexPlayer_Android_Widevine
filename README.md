## How to use Widevine DRM for Android

This guide provides a simple guide about how to use Nexplayer with Widevine DRM.

Nexplayer will try to go for MediaDRM, if it is not available Nexplayer will go for SW Widevine.

Please refer to [NexPlayer for Android](https://github.com/NexPlayerSDK/NexPlayer_Android) if you are not sure about how to implement NexPlayer on Android.

***
### 1. Simple Widevine with NexPlayer


### _NexplayerSample.java_
```java
int drmType = 0;
UUID WIDEVINE_UUID= new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID))
{
	//Configure Media DRM
	mNexPlayer.setNexMediaDrmKeyServerUri(strKeyServerURL);
	drmType = 1;
} else {
	//Instantiate and configure NexWVDRM
	mNexWVDRM = new NexWVDRM();
	mNexWVDRM.initDRMManager(strEnginePath, strFilePath, strKeyServerURL, offlineMode);
	drmType = 2;
}else{
	drmType = 0;
}
mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);

mNexPlayer.open(path, smiPath, externalPDPath, sourceType, transportType);

```

Widevine configuration should be done before _open_ method of NexPlayer object is called.

For advance usage check the Widevine documentation provided with the SDK.

***
### 2. Solving compatibility problems

In order to use SW Widevine in an aparently MediaDRM compatible device it is necessary to create a blacklist, a simple approach could be like this:

```java
if (Build.MODEL !=  "Your problematic device" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) {
```






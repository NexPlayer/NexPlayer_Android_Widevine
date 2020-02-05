# How to use Widevine DRM on Android

This is a simple guide to teach you how to integrate Widevine DRM into NexPlayer's Android SDK.

By default, NexPlayer will use MediaDRM. If it is not available, NexPlayer will utilize software Widevine.

Please refer to the [NexPlayer for Android](https://github.com/NexPlayer/NexPlayer_Android) documentation if you need assistance integrating NexPlayer on Android.

***
## Widevine Integration Sample Code


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
}
mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);

mNexPlayer.open(path, smiPath, externalPDPath, sourceType, transportType);

```

The Widevine configuration should be executed before the _open_ method of the NexPlayer object is called.

For advanced usage, please check the Widevine documentation provided with the SDK.

***
## Solving compatibility problems

In order to use software Widevine in MediaDRM compatible devices, you must create a blacklist. 

Here's an example of how to achieve this:

```java
if (<Your problematic device is in the blacklist>)
{
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) {
		//Use MediaDRM
	} else {
		//Use SW Widevine DRM
	}
} else {
	//Use SW Widevine DRM
}
```






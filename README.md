# How to use Widevine DRM on Android

Widevine allows to distribute and protect both content and its playback. WV uses MediaDRM, which is a module provided by google, and used for obtaining keys to decrypt content.

There are two types of Widevine: <strong>MediaDRM</strong> (From Android 4.0.3) and <strong>Software Widevine</strong>

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
	drmType |= 1;
} else {
	//Instantiate and configure NexWVDRM
	mNexWVDRM = new NexWVDRM();
	
	//This method initializes the minimum necessary information for playing Widevine DRM contents and registers it in Widevine DRM module.
	mNexWVDRM.initDRMManager(strEnginePath, strFilePath, strKeyServerURL, offlineMode);
	drmType |= 2;
}

//  Specifies the decryption module to use. Sets a value for NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM:
//          -drmType = 1: Using HW decryption module.
//          -drmType = 2: Using SW decryption module.
//          -drmType = 3: Using SW and HW decryption modules.
//  Despite of this, this is only a setter of the property and appropriate type of Widevine must be initialized. 

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






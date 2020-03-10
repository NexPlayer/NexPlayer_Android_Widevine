package com.example.widevineintegrationguideforandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.nexstreaming.nexplayerengine.INexDRMLicenseListener;
import com.nexstreaming.nexplayerengine.NexALFactory;
import com.nexstreaming.nexplayerengine.NexEventReceiver;
import com.nexstreaming.nexplayerengine.NexPlayer;
import com.nexstreaming.nexplayerengine.NexVideoRenderer;
import com.nexstreaming.nexplayerengine.NexVideoViewFactory;
import com.nexstreaming.nexplayerengine.NexWVDRM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.freewheel.renderers.vast.model.Util;

import static com.nexstreaming.nexplayerengine.gles.NexGLUtil.LOG_TAG;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivity";
    public static final Handler mHandler = new Handler();

    private NexPlayer mNexPlayer;
    private NexALFactory mNexALFactory;
    private NexEventReceiver mEventReceiver;

    private NexVideoRenderer mVideoView = null;
    private int mVideoWidth = 0;
    private int mVideoHeight = 0;

    private Button mPlayPauseButton;
    private Button mStopButton;

    private enum PLAYER_FLOW_STATE {
        START_PLAY, BEGINNING_OF_COMPLETE, END_OF_COMPLETE, FINISH_ACTIVITY, BEGINNING_OF_ONERROR, END_OF_ONERROR, STATE_NONE
    };
    private PLAYER_FLOW_STATE mPlayerState = PLAYER_FLOW_STATE.STATE_NONE;

    private static final int SCALE_FIT_TO_SCREEN = 0;
    private static final int SCALE_ORIGINAL = 1;
    private static final int SCALE_STRETCH_TO_SCREEN = 2;

    private NexWVDRM mNexWVDRM;
    private static final int NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM = 215;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setVideoRendererView();

        setupPlayPauseButton();
        setupStopButton();

        if( setPlayer() < 0) {
            Log.e(LOG_TAG, "setPlayer() failed");
            return;
        }

        startPlay();
    }

    private void startPlay() {
        //Content's URL
        String contentUrl = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd";

        //Server access key
        String keyServerUrl = "https://proxy.uat.widevine.com/proxy?provider=widevine_test";

        int drmType = 0;

//        // HARDWARE WIDEVINE

          // Enter the DRM key server URL to play content using the HW decryption module.
//        mNexPlayer.setNexMediaDrmKeyServerUri(keyServerUrl);

          //MediaDRM---> drmType = 1
//        drmType |= 1;


//         // SOFTWARE WIDEVINE
//         mNexWVDRM = new NexWVDRM();

           // path to store the key and authentication information received from the License server when Store / Retrieve play is performed
//         File fileDir = this.getFilesDir();
//         String strCertPath = fileDir.getAbsolutePath() + "/wvcert";

//         int offlineMode = 0;

           //This method initializes the minimum necessary information for playing Widevine DRM contents and registers it in Widevine DRM module. 
//         if(mNexWVDRM.initDRMManager(getEnginePath(this), strCertPath, keyServerUrl, offlineMode) == 0) {
//             drmType |= 2;
//         }
        
//          Specifies the decryption module to use. Sets a value for NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM:
//                  -drmType = 1: Using HW decryption module.
//                  -drmType = 2: Using SW decryption module.
//                  -drmType = 3: Using SW and HW decryption modules.
//          Despite of this, this is only a setter of the property and appropriate type of Widevine must be initialized. 
        mNexPlayer.setProperties(NEXPLAYER_PROPERTY_ENABLE_MEDIA_DRM, drmType);

        //Open content
        int result = mNexPlayer.open(contentUrl, null, null, NexPlayer.NEXPLAYER_SOURCE_TYPE_STREAMING, NexPlayer.NEXPLAYER_TRANSPORT_TYPE_TCP);
        if( result != 0 ) {
            Log.e(LOG_TAG, "mNexPlayer open failed");
        }
    }

    private void setVideoRendererView() {
        mVideoView = (NexVideoRenderer)findViewById(R.id.videoview);
        mVideoView.setBackgroundColor(Color.BLACK);
        mVideoView.setVisibility(View.VISIBLE);

        mVideoView.setListener(new NexVideoRenderer.IListener() {
            @Override
            public void onDisplayedRectChanged() {

            }

            @Override
            public void onFirstVideoRenderCreate() {
                setPlayerOutputPosition(mVideoWidth, mVideoHeight, SCALE_FIT_TO_SCREEN);
            }

            @Override
            public void onSizeChanged() {
                setPlayerOutputPosition(mVideoWidth, mVideoHeight, SCALE_FIT_TO_SCREEN);
            }

            @Override
            public void onVideoSizeChanged() {
                Point videoSize = new Point();
                mVideoView.getVideoSize(videoSize);

                mVideoWidth = videoSize.x;
                mVideoHeight = videoSize.y;

                setPlayerOutputPosition(mVideoWidth, mVideoHeight, SCALE_FIT_TO_SCREEN);
            }
        });

        mVideoView.setPostNexPlayerVideoRendererListener(mEventReceiver);
    }

    void setPlayerOutputPosition(int videoWidth, int videoHeight, int scaleMode) {
        int width, height, top, left;
        width = height = top = left = 0;
        final int screenWidth = mVideoView.getWidth();
        final int screenHeight = mVideoView.getHeight();
        Log.d(LOG_TAG, "setPlayerOutputPosition screenWidth : " + screenWidth + " screenHeight : " + screenHeight);

        float scale = 1f;

        switch (scaleMode) {
            case SCALE_FIT_TO_SCREEN:
                scale = Math.min((float) screenWidth / (float) videoWidth, (float) screenHeight / (float) videoHeight);

                width = (int) (videoWidth * scale);
                height = (int) (videoHeight * scale);
                top = (screenHeight - height) / 2;
                left = (screenWidth - width) / 2;

                mVideoView.setOutputPos(left, top, width, height);
                break;
        }
    }

    private int setPlayer() {
        mNexPlayer = new NexPlayer();
        mNexALFactory = new NexALFactory();

        int debugLogLevel = 0;

        if( mNexALFactory.init(this, android.os.Build.MODEL, NexPlayer.NEX_DEVICE_USE_AUTO, debugLogLevel, 1 ) == false ) {
            Log.e(LOG_TAG, "ALFactory initialization failed");
            return -2;
        }

        mNexPlayer.setNexALFactory(mNexALFactory);
        NexPlayer.NexErrorCode result = mNexPlayer.init(this);
        if(NexPlayer.NexErrorCode.NONE != result) {
            Log.e(LOG_TAG, "NexPlayer initialization failed : " + result.getDesc());
            return -3;
        }

        addEventReceiver();

        mVideoView.init(mNexPlayer);
        mVideoView.setVisibility(View.VISIBLE);

        return 0;
    }

    protected void addEventReceiver() {
        mEventReceiver = new NexEventReceiver() {
            @Override
            public void onAsyncCmdComplete(NexPlayer mp, int command, int result, int param1, int param2) {
                Log.d(LOG_TAG, "onAsyncCmdComplete : mp : " + mp + " command : " + command + ", result : " + result);
                switch (command) {
                    case NexPlayer.NEXPLAYER_ASYNC_CMD_OPEN_STREAMING:
                        if(result == 0) {
                            Log.d(LOG_TAG, "onAsyncCmdComplete : OPEN");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    startPlayer();
                                    mPlayerState = PLAYER_FLOW_STATE.BEGINNING_OF_COMPLETE;
                                }
                            });
                        }
                        break;
                }
            }

            @Override
            public void onStatusReport(NexPlayer mp, int msg, int param1) {
                Log.e(LOG_TAG, "onStatusReport");
            }

            @Override
            public void onEndOfContent(NexPlayer mp) {
                Log.e(LOG_TAG, "onEndOfContent");
            }

            @Override
            public void onTime(NexPlayer mp, int millisec) {
                Log.e(LOG_TAG, "onTime");
            }

            @Override
            public void onError(NexPlayer mp, NexPlayer.NexErrorCode errorcode) {
                Log.e(LOG_TAG, "onError");
            }

            @Override
            public void onVideoRenderCreate(NexPlayer mp, int width, int height, Object rgbBuffer) {
                super.onVideoRenderCreate(mp, width, height, rgbBuffer);
                Log.e(LOG_TAG, "onVideoRenderCreate");
            }
        };

        mNexPlayer.addEventReceiver(mEventReceiver);
    }


    private void startPlayer() {
        if( mNexPlayer.getState() == NexPlayer.NEXPLAYER_STATE_STOP && mPlayerState != PLAYER_FLOW_STATE.BEGINNING_OF_ONERROR && mPlayerState != PLAYER_FLOW_STATE.END_OF_ONERROR ) {
            mNexPlayer.start(0);
        }
    }

    private  void setupStopButton() {
        mStopButton = (Button)findViewById(R.id.stop_button);
        mStopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void  onClick(View view) {
                if(mNexPlayer != null && mNexPlayer.isInitialized()) {
                    if ( mNexPlayer.getState() >= NexPlayer.NEXPLAYER_STATE_STOP ) {
                        mNexPlayer.stop();
                        mPlayPauseButton.setText("PLAY");
                    }
                }
            }
        });
    }

    private void setupPlayPauseButton() {
        mPlayPauseButton = (Button)findViewById(R.id.play_pause_button);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(mNexPlayer != null && mNexPlayer.isInitialized()) {
                    int state = mNexPlayer.getState();

                    switch (mNexPlayer.getState()) {
                        case NexPlayer.NEXPLAYER_STATE_PLAY:
                            int ret = mNexPlayer.pause();
                            mPlayPauseButton.setText("PLAY");
                            break;

                        case NexPlayer.NEXPLAYER_STATE_PAUSE:
                            ret = mNexPlayer.resume();
                            mPlayPauseButton.setText("PAUSE");
                            break;

                        case NexPlayer.NEXPLAYER_STATE_STOP:
                            startPlayer();
                            mPlayPauseButton.setText("PAUSE");
                            break;

                        case NexPlayer.NEXPLAYER_STATE_CLOSED:
                            startPlay();
                            break;
                    }

                }

            }
        });

    }

    public static String getEnginePath(Context context) {
        String engine = "libnexplayerengine.so";
        String ret = context.getApplicationInfo().dataDir + "/lib/" + engine;
        return ret;
    }

}

package com.eor.onechat.calls;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.eor.onechat.ExtendedApplication;
import com.eor.onechat.R;
import com.eor.onechat.net.Direct;
import com.eor.onechat.net.Proto;
import com.eor.onechat.net.ServerResponse;
import com.google.gson.JsonObject;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallActivity extends Activity implements /*AppRTCClient.SignalingEvents,*/
                                                      PeerConnectionClient.PeerConnectionEvents,
                                                      CallFragment.OnCallEvents, ServerResponse {
  private static final String TAG = "CallRTCClient";

  public static final String EXTRA_ROOMID = "com.eor.onechat.ROOMID";
  public static final String EXTRA_URLPARAMETERS = "com.eor.onechat.URLPARAMETERS";
  public static final String EXTRA_LOOPBACK = "com.eor.onechat.LOOPBACK";
  public static final String EXTRA_VIDEO_CALL = "com.eor.onechat.VIDEO_CALL";
  public static final String EXTRA_SCREENCAPTURE = "com.eor.onechat.SCREENCAPTURE";
  public static final String EXTRA_CAMERA2 = "com.eor.onechat.CAMERA2";
  public static final String EXTRA_VIDEO_WIDTH = "com.eor.onechat.VIDEO_WIDTH";
  public static final String EXTRA_VIDEO_HEIGHT = "com.eor.onechat.VIDEO_HEIGHT";
  public static final String EXTRA_VIDEO_FPS = "com.eor.onechat.VIDEO_FPS";
  public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
      "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
  public static final String EXTRA_VIDEO_BITRATE = "com.eor.onechat.VIDEO_BITRATE";
  public static final String EXTRA_VIDEOCODEC = "com.eor.onechat.VIDEOCODEC";
  public static final String EXTRA_HWCODEC_ENABLED = "com.eor.onechat.HWCODEC";
  public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "com.eor.onechat.CAPTURETOTEXTURE";
  public static final String EXTRA_FLEXFEC_ENABLED = "com.eor.onechat.FLEXFEC";
  public static final String EXTRA_AUDIO_BITRATE = "com.eor.onechat.AUDIO_BITRATE";
  public static final String EXTRA_AUDIOCODEC = "com.eor.onechat.AUDIOCODEC";
  public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
      "com.eor.onechat.NOAUDIOPROCESSING";
  public static final String EXTRA_AECDUMP_ENABLED = "com.eor.onechat.AECDUMP";
  public static final String EXTRA_OPENSLES_ENABLED = "com.eor.onechat.OPENSLES";
  public static final String EXTRA_DISABLE_BUILT_IN_AEC = "com.eor.onechat.DISABLE_BUILT_IN_AEC";
  public static final String EXTRA_DISABLE_BUILT_IN_AGC = "com.eor.onechat.DISABLE_BUILT_IN_AGC";
  public static final String EXTRA_DISABLE_BUILT_IN_NS = "com.eor.onechat.DISABLE_BUILT_IN_NS";
  public static final String EXTRA_ENABLE_LEVEL_CONTROL = "com.eor.onechat.ENABLE_LEVEL_CONTROL";
  public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
      "com.eor.onechat.DISABLE_WEBRTC_GAIN_CONTROL";
  public static final String EXTRA_DISPLAY_HUD = "com.eor.onechat.DISPLAY_HUD";
  public static final String EXTRA_TRACING = "com.eor.onechat.TRACING";
  public static final String EXTRA_CMDLINE = "com.eor.onechat.CMDLINE";
  public static final String EXTRA_RUNTIME = "com.eor.onechat.RUNTIME";
  public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "com.eor.onechat.VIDEO_FILE_AS_CAMERA";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
      "com.eor.onechat.SAVE_REMOTE_VIDEO_TO_FILE";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
      "com.eor.onechat.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
  public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
      "com.eor.onechat.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
  public static final String EXTRA_USE_VALUES_FROM_INTENT =
      "com.eor.onechat.USE_VALUES_FROM_INTENT";
  public static final String EXTRA_DATA_CHANNEL_ENABLED = "com.eor.onechat.DATA_CHANNEL_ENABLED";
  public static final String EXTRA_ORDERED = "com.eor.onechat.ORDERED";
  public static final String EXTRA_MAX_RETRANSMITS_MS = "com.eor.onechat.MAX_RETRANSMITS_MS";
  public static final String EXTRA_MAX_RETRANSMITS = "com.eor.onechat.MAX_RETRANSMITS";
  public static final String EXTRA_PROTOCOL = "com.eor.onechat.PROTOCOL";
  public static final String EXTRA_NEGOTIATED = "com.eor.onechat.NEGOTIATED";
  public static final String EXTRA_ID = "com.eor.onechat.ID";

  private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

  // List of mandatory application permissions.
  private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
      "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};

  // Peer connection statistics callback period in ms.
  private static final int STAT_CALLBACK_PERIOD = 1000;

    @Override
    public void onServerResponse(JsonObject jsonObject) {

    }

    private static class ProxyRenderer implements VideoRenderer.Callbacks {
    private VideoRenderer.Callbacks target;

    @Override
    synchronized public void renderFrame(VideoRenderer.I420Frame frame) {
      if (target == null) {
        Logging.d(TAG, "Dropping frame in proxy because target is null.");
        VideoRenderer.renderFrameDone(frame);
        return;
      }

      target.renderFrame(frame);
    }

    synchronized public void setTarget(VideoRenderer.Callbacks target) {
      this.target = target;
    }
  }

  private static class ProxyVideoSink implements VideoSink {
    private VideoSink target;

    @Override
    synchronized public void onFrame(VideoFrame frame) {
      if (target == null) {
        Logging.d(TAG, "Dropping frame in proxy because target is null.");
        return;
      }

      target.onFrame(frame);
    }

    synchronized public void setTarget(VideoSink target) {
      this.target = target;
    }
  }

  private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
  private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
  private PeerConnectionClient peerConnectionClient = null;
//  private AppRTCClient appRtcClient;
//  private SignalingParameters signalingParameters;
  private AudioManager audioManager = null;
  private SurfaceViewRenderer pipRenderer;
  private SurfaceViewRenderer fullscreenRenderer;
  private VideoFileRenderer videoFileRenderer;
  private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<>();
  private Toast logToast;
  private boolean commandLineRun;
  private boolean activityRunning;
//  private RoomConnectionParameters roomConnectionParameters;
  private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
  private boolean iceConnected;
  private boolean isError;
  private boolean callControlFragmentVisible = true;
  private long callStartedTimeMs = 0;
  private boolean micEnabled = true;
  private boolean screencaptureEnabled = false;
  private static Intent mediaProjectionPermissionResultData;
  private static int mediaProjectionPermissionResultCode;
  // True if local view is in the fullscreen renderer.
  private boolean isSwappedFeeds;
  private ExtendedApplication app;
  private Boolean initiator = false;
  private String peerId = null;

  // Controls
  private CallFragment callFragment;
//  private HudFragment hudFragment;
//  private CpuMonitor cpuMonitor;

  @Override
  // TODO(bugs.webrtc.org/8580): LayoutParams.FLAG_TURN_SCREEN_ON and
  // LayoutParams.FLAG_SHOW_WHEN_LOCKED are deprecated.
  @SuppressWarnings("deprecation")
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.d("onCreate");
    Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

    // Set window styles for fullscreen-window size. Needs to be done before
    // adding content.
//    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(/*LayoutParams.FLAG_FULLSCREEN |*/ LayoutParams.FLAG_KEEP_SCREEN_ON
        | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);
    getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
    setContentView(R.layout.activity_call);

    app = (ExtendedApplication) getApplication();

    iceConnected = false;
//    signalingParameters = null;

    // Create UI controls.
    pipRenderer = findViewById(R.id.pip_video_view);
    fullscreenRenderer = findViewById(R.id.fullscreen_video_view);
    callFragment = new CallFragment();
//    hudFragment = new HudFragment();

    // Show/hide call control fragment on view click.
    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        toggleCallControlFragmentVisibility();
      }
    };

    // Swap feeds on pip view click.
    pipRenderer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setSwappedFeeds(!isSwappedFeeds);
      }
    });

    fullscreenRenderer.setOnClickListener(listener);
    remoteRenderers.add(remoteProxyRenderer);

    final Intent intent = getIntent();

    // Create peer connection client.
    peerConnectionClient = new PeerConnectionClient();

    // Create video renderers.
    pipRenderer.init(peerConnectionClient.getRenderContext(), null);
    pipRenderer.setScalingType(ScalingType.SCALE_ASPECT_FIT);
    String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

    // When saveRemoteVideoToFile is set we save the video from the remote to a file.
    if (saveRemoteVideoToFile != null) {
      int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
      int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
      try {
        videoFileRenderer = new VideoFileRenderer(saveRemoteVideoToFile, videoOutWidth,
            videoOutHeight, peerConnectionClient.getRenderContext());
        remoteRenderers.add(videoFileRenderer);
      } catch (IOException e) {
        throw new RuntimeException(
            "Failed to open video file for output: " + saveRemoteVideoToFile, e);
      }
    }
    fullscreenRenderer.init(peerConnectionClient.getRenderContext(), null);
    fullscreenRenderer.setScalingType(ScalingType.SCALE_ASPECT_FILL);

    pipRenderer.setZOrderMediaOverlay(true);
    pipRenderer.setEnableHardwareScaler(true /* enabled */);
    fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);
    // Start with local feed in fullscreen and swap it to the pip when the call is connected.
    setSwappedFeeds(true /* isSwappedFeeds */);

    // Check for mandatory permissions.
    for (String permission : MANDATORY_PERMISSIONS) {
      if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        logAndToast("Permission " + permission + " is not granted");
        setResult(RESULT_CANCELED);
        finish();
        return;
      }
    }

      peerId = "5A88D64D71ADD835DE00000C"; // caller - demo app device userId
    if (intent.getAction() != null && intent.getAction().equals("OFFER")) {
        initiator = true;
        peerId = "5A89105271ADD835DE000012"; // callee - support side userId
    }

    boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
    boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

    int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
    int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

    screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
    // If capturing format is not specified for screencapture, use screen resolution.
    if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
      DisplayMetrics displayMetrics = getDisplayMetrics();
      videoWidth = displayMetrics.widthPixels;
      videoHeight = displayMetrics.heightPixels;
    }
//    DataChannelParameters dataChannelParameters = null;
//    if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
//      dataChannelParameters = new DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
//          intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
//          intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
//          intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
//    }
    peerConnectionParameters =
        new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
            tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
            intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
            intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
            intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
            intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
            intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
            intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
            intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
            intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
            intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
            intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false)/*, dataChannelParameters*/);
    commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
    int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

    Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

    // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
    // standard WebSocketRTCClient.
//    if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
//      appRtcClient = new WebSocketRTCClient(this);
//    } else {
//      Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
//      appRtcClient = new DirectRTCClient(this);
//    }
    // Create connection parameters.
    String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
//    roomConnectionParameters =
//        new RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

    // Create CPU monitor
//    if (cpuMonitor.isSupported()) {
//      cpuMonitor = new CpuMonitor(this);
//      hudFragment.setCpuMonitor(cpuMonitor);
//    }

    // Send intent arguments to fragments.
    callFragment.setArguments(intent.getExtras());
//    hudFragment.setArguments(intent.getExtras());
    // Activate call and HUD fragments and start the call.
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.add(R.id.call_fragment_container, callFragment);
//    ft.add(R.id.hud_fragment_container, hudFragment);
    ft.commit();

    // For command line execution run connection for <runTimeMs> and exit.
    if (commandLineRun && runTimeMs > 0) {
      (new Handler()).postDelayed(new Runnable() {
        @Override
        public void run() {
          disconnect();
        }
      }, runTimeMs);
    }

    if (loopback) {
      PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
      options.networkIgnoreMask = 0;
      peerConnectionClient.setPeerConnectionFactoryOptions(options);
    }
    peerConnectionClient.createPeerConnectionFactory(
        getApplicationContext(), peerConnectionParameters, CallActivity.this);

    if (screencaptureEnabled) {
      startScreenCapture();
    } else {
      startCall();
    }
  }

  @TargetApi(17)
  private DisplayMetrics getDisplayMetrics() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    WindowManager windowManager =
        (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
    windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
    return displayMetrics;
  }

  @TargetApi(19)
  private static int getSystemUiVisibility() {
    int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }
    return flags;
  }

  @TargetApi(21)
  private void startScreenCapture() {
    MediaProjectionManager mediaProjectionManager =
        (MediaProjectionManager) getApplication().getSystemService(
            Context.MEDIA_PROJECTION_SERVICE);
    startActivityForResult(
        mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
      return;
    mediaProjectionPermissionResultCode = resultCode;
    mediaProjectionPermissionResultData = data;
    startCall();
  }

  private boolean useCamera2() {
    return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
  }

  private boolean captureToTexture() {
    return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
  }

  private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
    final String[] deviceNames = enumerator.getDeviceNames();

    // First, try to find front facing camera
    Logging.d(TAG, "Looking for front facing cameras.");
    for (String deviceName : deviceNames) {
      if (enumerator.isFrontFacing(deviceName)) {
        Logging.d(TAG, "Creating front facing camera capturer.");
        VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }

    // Front facing camera not found, try something else
    Logging.d(TAG, "Looking for other cameras.");
    for (String deviceName : deviceNames) {
      if (!enumerator.isFrontFacing(deviceName)) {
        Logging.d(TAG, "Creating other camera capturer.");
        VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

        if (videoCapturer != null) {
          return videoCapturer;
        }
      }
    }

    return null;
  }

  @TargetApi(21)
  private VideoCapturer createScreenCapturer() {
    if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
      reportError("User didn't give permission to capture the screen.");
      return null;
    }
    return new ScreenCapturerAndroid(
        mediaProjectionPermissionResultData, new MediaProjection.Callback() {
      @Override
      public void onStop() {
        reportError("User revoked permission to capture the screen.");
      }
    });
  }

  // Activity interfaces
  @Override
  public void onStop() {
    super.onStop();
    activityRunning = false;
    // Don't stop the video when using screencapture to allow user to show other apps to the remote
    // end.
    if (peerConnectionClient != null && !screencaptureEnabled) {
      peerConnectionClient.stopVideoSource();
    }
//    if (cpuMonitor != null) {
//      cpuMonitor.pause();
//    }
  }

  @Override
  public void onStart() {
    super.onStart();
    activityRunning = true;
    // Video is not paused for screencapture. See onPause.
    if (peerConnectionClient != null && !screencaptureEnabled) {
      peerConnectionClient.startVideoSource();
    }
//    if (cpuMonitor != null) {
//      cpuMonitor.resume();
//    }
  }

  @Override
  protected void onDestroy() {
    Thread.setDefaultUncaughtExceptionHandler(null);
    disconnect();
    if (logToast != null) {
      logToast.cancel();
    }
    activityRunning = false;
    super.onDestroy();
  }

  // CallFragment.OnCallEvents interface implementation.
  @Override
  public void onCallHangUp() {
    disconnect();
  }

  @Override
  public void onCameraSwitch() {
    if (peerConnectionClient != null) {
      peerConnectionClient.switchCamera();
    }
  }

  @Override
  public void onVideoScalingSwitch(ScalingType scalingType) {
    fullscreenRenderer.setScalingType(scalingType);
  }

  @Override
  public void onCaptureFormatChange(int width, int height, int framerate) {
    if (peerConnectionClient != null) {
      peerConnectionClient.changeCaptureFormat(width, height, framerate);
    }
  }

  @Override
  public boolean onToggleMic() {
    if (peerConnectionClient != null) {
      micEnabled = !micEnabled;
      peerConnectionClient.setAudioEnabled(micEnabled);
    }
    return micEnabled;
  }

  // Helper functions.
  private void toggleCallControlFragmentVisibility() {
    if (!iceConnected || !callFragment.isAdded()) {
      return;
    }
    // Show/hide call control fragment
    callControlFragmentVisible = !callControlFragmentVisible;
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    if (callControlFragmentVisible) {
      ft.show(callFragment);
//      ft.show(hudFragment);
    } else {
      ft.hide(callFragment);
//      ft.hide(hudFragment);
    }
    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    ft.commit();
  }

  private void startCall() {
//    if (appRtcClient == null) {
//      Log.e(TAG, "AppRTC client is not allocated for a call.");
//      return;
//    }
    callStartedTimeMs = System.currentTimeMillis();

    // Start room connection.
//    logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
//    appRtcClient.connectToRoom(roomConnectionParameters);

    // Create and audio manager that will take care of audio routing,
    // audio modes, audio device enumeration etc.
    audioManager = AudioManager.create(getApplicationContext());
    // Store existing audio settings and change audio mode to
    // MODE_IN_COMMUNICATION for best possible VoIP performance.
    Log.d(TAG, "Starting the audio manager...");
    audioManager.start(new AudioManager.AudioManagerEvents() {
      // This method will be called each time the number of available audio
      // devices has changed.
      @Override
      public void onAudioDeviceChanged(
              AudioManager.AudioDevice audioDevice, Set<AudioManager.AudioDevice> availableAudioDevices) {
        onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
      }
    });
    onConnectedToRoomInternal();
  }

  // Should be called from UI thread
  private void callConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    Log.i(TAG, "Call connected: delay=" + delta + "ms");
    if (peerConnectionClient == null || isError) {
      Log.w(TAG, "Call is connected in closed or error state");
      return;
    }
    // Enable statistics callback.
    peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    setSwappedFeeds(false /* isSwappedFeeds */);
  }

  // This method is called when the audio manager reports audio device change,
  // e.g. from wired headset to speakerphone.
  private void onAudioManagerDevicesChanged(
          final AudioManager.AudioDevice device, final Set<AudioManager.AudioDevice> availableDevices) {
    Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
            + "selected: " + device);
    // TODO(henrika): add callback handler.
  }

  // Disconnect from remote resources, dispose of local resources, and exit.
  private void disconnect() {
    activityRunning = false;
    remoteProxyRenderer.setTarget(null);
    localProxyVideoSink.setTarget(null);
//    if (appRtcClient != null) {
//      appRtcClient.disconnectFromRoom();
//      appRtcClient = null;
//    }
    if (pipRenderer != null) {
      pipRenderer.release();
      pipRenderer = null;
    }
    if (videoFileRenderer != null) {
      videoFileRenderer.release();
      videoFileRenderer = null;
    }
    if (fullscreenRenderer != null) {
      fullscreenRenderer.release();
      fullscreenRenderer = null;
    }
    if (peerConnectionClient != null) {
      peerConnectionClient.close();
      peerConnectionClient = null;
    }
    if (audioManager != null) {
      audioManager.stop();
      audioManager = null;
    }
    if (iceConnected && !isError) {
      setResult(RESULT_OK);
    } else {
      setResult(RESULT_CANCELED);
    }
    finish();
  }

  private void disconnectWithErrorMessage(final String errorMessage) {
//    if (commandLineRun || !activityRunning) {
//      Log.e(TAG, "Critical error: " + errorMessage);
//      disconnect();
//    } else {
//      new AlertDialog.Builder(this)
//          .setTitle(getText(R.string.channel_error_title))
//          .setMessage(errorMessage)
//          .setCancelable(false)
//          .setNeutralButton(R.string.ok,
//              new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int id) {
//                  dialog.cancel();
//                  disconnect();
//                }
//              })
//          .create()
//          .show();
//    }
  }

  // Log |msg| and Toast about it.
  private void logAndToast(String msg) {
//    Log.d(TAG, msg);
//    if (logToast != null) {
//      logToast.cancel();
//    }
//    logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
//    logToast.show();
  }

  private void reportError(final String description) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError) {
          isError = true;
          disconnectWithErrorMessage(description);
        }
      }
    });
  }

  private VideoCapturer createVideoCapturer() {
    final VideoCapturer videoCapturer;
    String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
    if (videoFileAsCamera != null) {
      try {
        videoCapturer = new FileVideoCapturer(videoFileAsCamera);
      } catch (IOException e) {
        reportError("Failed to open video file for emulated camera");
        return null;
      }
    } else if (screencaptureEnabled) {
      return createScreenCapturer();
    } else if (useCamera2()) {
      if (!captureToTexture()) {
//        reportError(getString(R.string.camera2_texture_only_error));
        return null;
      }

      Logging.d(TAG, "Creating capturer using camera2 API.");
      videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
    } else {
      Logging.d(TAG, "Creating capturer using camera1 API.");
      videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
    }
    if (videoCapturer == null) {
      reportError("Failed to open camera");
      return null;
    }
    return videoCapturer;
  }

  private void setSwappedFeeds(boolean isSwappedFeeds) {
    Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
    this.isSwappedFeeds = isSwappedFeeds;
    localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
    remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
    fullscreenRenderer.setMirror(isSwappedFeeds);
    pipRenderer.setMirror(!isSwappedFeeds);
  }

  // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
  // All callbacks are invoked from websocket signaling looper thread and
  // are routed to UI thread.
  private void onConnectedToRoomInternal(/*final SignalingParameters params*/) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    List<PeerConnection.IceServer> iceServers = new ArrayList<>();

//    signalingParameters = params;
    logAndToast("Creating peer connection, delay=" + delta + "ms");
    VideoCapturer videoCapturer = null;
    if (peerConnectionParameters.videoCallEnabled) {
      videoCapturer = createVideoCapturer();
    }

      PeerConnection.IceServer iceServer =
              PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                      .setUsername("")
                      .setPassword("")
                      .createIceServer();
      iceServers.add(iceServer);
      iceServer = PeerConnection.IceServer.builder("turn:52.57.247.209:3478")
              .setUsername("eik6aGhoap")
              .setPassword("UaV8gi3aix")
              .createIceServer();
      iceServers.add(iceServer);
      peerConnectionClient.createPeerConnection(
        localProxyVideoSink, remoteRenderers, videoCapturer, iceServers);

    if (initiator) {
        logAndToast("Creating OFFER...");
        // Create offer. Offer SDP will be sent to answering client in
        // PeerConnectionEvents.onLocalDescription event.
        peerConnectionClient.createOffer();
        app.setPc(peerConnectionClient);
    } else {
        peerConnectionClient.setRemoteDescription(app.getRemoteSdp());
        peerConnectionClient.createAnswer();
        for (IceCandidate candidate : app.getRemoteIceCandidates()) {
            peerConnectionClient.addRemoteIceCandidate(candidate);
        }
        app.getRemoteIceCandidates().clear();
    }
  }

//  @Override
//  public void onConnectedToRoom(final SignalingParameters params) {
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        onConnectedToRoomInternal(params);
//      }
//    });
//  }

//  @Override
//  public void onRemoteDescription(final SessionDescription remoteSdp) {
//    final long delta = System.currentTimeMillis() - callStartedTimeMs;
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        if (peerConnectionClient == null) {
//          Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
//          return;
//        }
//        logAndToast("Received remote " + remoteSdp.type + ", delay=" + delta + "ms");
//        peerConnectionClient.setRemoteDescription(remoteSdp);
//        if (!signalingParameters.initiator) {
//          logAndToast("Creating ANSWER...");
//          // Create answer. Answer SDP will be sent to offering client in
//          // PeerConnectionEvents.onLocalDescription event.
//          peerConnectionClient.createAnswer();
//        }
//      }
//    });
//  }

//  @Override
//  public void onRemoteIceCandidate(final IceCandidate candidate) {
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        if (peerConnectionClient == null) {
//          Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
//          return;
//        }
//        peerConnectionClient.addRemoteIceCandidate(candidate);
//      }
//    });
//  }

//  @Override
//  public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        if (peerConnectionClient == null) {
//          Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
//          return;
//        }
//        peerConnectionClient.removeRemoteIceCandidates(candidates);
//      }
//    });
//  }

//  @Override
//  public void onChannelClose() {
//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        logAndToast("Remote end hung up; dropping PeerConnection");
//        disconnect();
//      }
//    });
//  }

//  @Override
//  public void onChannelError(final String description) {
//    reportError(description);
//  }

  // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
  // Send local peer connection SDP and ICE candidates to remote party.
  // All callbacks are invoked from peer connection client looper thread and
  // are routed to UI thread.
  @Override
  public void onLocalDescription(final SessionDescription sdp) {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
      app.getWebSocketClient().send(Proto.Method.DIRECT, new Direct(peerId, Direct.Type.SDP,sdp), this);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
//        if (app.getWebSocketClient() != null) {
//          logAndToast("Sending " + remoteSdp.type + ", delay=" + delta + "ms");
//          if (initiator) {
////            app.getWebSocketClient().send(Proto.Method.DIRECT, remoteSdp, this);
//          } else {
////            app.getWebSocketClient().send(Proto.Method.DIRECT, remoteSdp, this);
//          }
//        }
        if (peerConnectionParameters.videoMaxBitrate > 0) {
          Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
          peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
        }
      }
    });
    Timber.d("onLocalDescription %s", sdp.type.toString());
  }

  @Override
  public void onIceCandidate(final IceCandidate candidate) {
      Timber.d("onIceCandidate");
      app.getWebSocketClient().send(Proto.Method.DIRECT, new Direct(peerId, Direct.Type.CANDY,candidate), this);

//    runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
////        if (appRtcClient != null) {
////          appRtcClient.sendLocalIceCandidate(candidate);
////        }
//      }
//    });
  }

  @Override
  public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
//        if (appRtcClient != null) {
//          appRtcClient.sendLocalIceCandidateRemovals(candidates);
//        }
      }
    });
  }

  @Override
  public void onIceConnected() {
    final long delta = System.currentTimeMillis() - callStartedTimeMs;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        logAndToast("ICE connected, delay=" + delta + "ms");
        iceConnected = true;
        callConnected();
      }
    });
  }

  @Override
  public void onIceDisconnected() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        logAndToast("ICE disconnected");
        iceConnected = false;
        disconnect();
      }
    });
  }

  @Override
  public void onPeerConnectionClosed() {}

  @Override
  public void onPeerConnectionStatsReady(final StatsReport[] reports) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (!isError && iceConnected) {
//          hudFragment.updateEncoderStatistics(reports);
        }
      }
    });
  }

  @Override
  public void onPeerConnectionError(final String description) {
      Timber.d("onPeerConnectionError %s", description);
    reportError(description);
  }
}

package com.example.drone_interactor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.thirdparty.afinal.core.AsyncTask;

/**
 * The main activity of the app, starts everything else and handles the UI.
 */
public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataListener mReceivedVideoDataListener = null;
    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private BaseProduct mProduct;
    private Handler mHandler;
    private static MainActivity instance = null;

    // Codec for video live view
    protected DJICodecManager mCodecManager = null;
    private VideoFeeder.VideoDataListener videoDataListener = null;

    //
    private TextureView mVideoSurface = null;

    private ViewListeners viewListeners;

    /**
     * During startup the class will store it's own instance. This method will always be
     * called first during startup of app.
     */
    public MainActivity() {
        MainActivity.instance = this;
    }

    /**
     * Returns the instance of this class
     * This is done because we can only change objects on screen from the 
     * main class with it's main thread.
     */
    public static MainActivity getInstance() {
        return MainActivity.instance;
    }

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };
    private List<String> missingPermission = new ArrayList<>();
    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);
    private static final int REQUEST_PERMISSION_CODE = 12345;

    /**
     * starts of the application with overriding the AppCompatActivity onCreate method 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        }

        setContentView(R.layout.activity_main);

        //Initialize DJI SDK Manager
        mHandler = new Handler(Looper.getMainLooper());

        mVideoSurface = (TextureView) findViewById(R.id.video_previewer_surface);

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        // Check for permissions
        for (String eachPermission : REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(this, eachPermission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(eachPermission);
            }
        }
        // Request for missing permissions
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showToast("Need to grant the permissions!");
            ActivityCompat.requestPermissions(this,
                    missingPermission.toArray(new String[missingPermission.size()]),
                    REQUEST_PERMISSION_CODE);
        }

    }

    /**
     * Result of runtime permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    missingPermission.remove(permissions[i]);
                }
            }
        }
        // If there is enough permission, we will start the registration
        if (missingPermission.isEmpty()) {
            startSDKRegistration();
        } else {
            showToast("Missing permissions!!!");
        }
    }

    /**
     * Start the registration of the application with DJI with the APP_KEY stored in the Manifest
     * Code given by DJI mobile SDK
     */
    private void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    showToast("registering, pls wait...");
                    DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                        @Override
                        public void onRegister(DJIError djiError) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.setDisplayContent(null);
                                }
                            });

                            if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                                showToast("Register Success");
                                Log.e(TAG, "WHILE REGISTERING " + String.valueOf(DJISDKManager.getInstance().startConnectionToProduct()));
                            } else {
                                showToast("Register sdk fails, please check the bundle id and network connection!");
                            }
                            Log.v(TAG, djiError.getDescription());

                        }

                        @Override
                        public void onProductDisconnect() {
                            Log.d(TAG, "onProductDisconnect");
                            showToast("Product Disconnected");
                            notifyStatusChange();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.setDisplayContent(null);
                                }
                            });

                        }
                        @Override
                        public void onProductConnect(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct));
                            showToast("Product Connected");
                            notifyStatusChange();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.setDisplayContent(baseProduct);
                                }
                            });
                            // call our own startup class
                            startupClasses();

                            MainActivity.this.mProduct = baseProduct;
                        }

                        @Override
                        public void onProductChanged(BaseProduct baseProduct) {
                            Log.d(TAG, String.format("onProductChanged newProduct:%s", baseProduct));
                            MainActivity.this.mProduct = baseProduct;
                            startupClasses();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.this.setDisplayContent(baseProduct);
                                }
                            });
                        }

                        @Override
                        public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                      BaseComponent newComponent) {
                            if (newComponent != null) {
                                newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                                    @Override
                                    public void onConnectivityChange(boolean isConnected) {
                                        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
                                        notifyStatusChange();
                                    }
                                });
                            }
                            Log.d(TAG,
                                    String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                            componentKey,
                                            oldComponent,
                                            newComponent));

                        }
                        @Override
                        public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {
                            Log.d(TAG, "init process: " + djisdkInitEvent);
                        }

                        @Override
                        public void onDatabaseDownloadProgress(long l, long l1) {
                            Log.d(TAG, "download: " + l + ", " + l1);

                        }
                    });
                }
            });
        }
    }

    /** 
     * Creates a TextViews instance and start every other classes which are required
     * for the application to work. The textViews are used to control the objects 
     * on the screen to show information to specific objects.
     */
    private void startupClasses() {
        TextViews textViews = new TextViews(
                findViewById(R.id.droneConnectionStatus),
                findViewById(R.id.serverConnectionStatus),
                findViewById(R.id.currentAngle),
                findViewById(R.id.forwardDistance),
                findViewById(R.id.backwardDistance),
                findViewById(R.id.upwardDistance));

        try {
            // fetch the Aircraft instance from the DJISDKManager
            Aircraft aircraft = (Aircraft)DJISDKManager.getInstance().getProduct();

            //this.viewListeners = new ViewListeners((Button) findViewById(R.id.startButton), textViews, aircraft);
            DroneDataProcessing droneDataProcessing = DroneDataProcessing.getInstance();

            droneDataProcessing.setup(textViews, aircraft);

            // Initialize video preview
            initPreviewer();

            initSDKCallback();

            //this.viewListeners.init();



            showToast("Started all classes with parameters");

        } catch (Exception e) {
            showToast(String.format("Class init error: %s", e));
        }

    }



    private void initPreviewer() {

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);

            /*
             * onSurfaceTextureAvailable does not get called if it is already available.
             * This took longer to figure out than we would like to admit
             */
            if (mVideoSurface.isAvailable()) {
                onSurfaceTextureAvailable(mVideoSurface.getSurfaceTexture(), mVideoSurface.getWidth(), mVideoSurface.getHeight());
            }

            videoDataListener = new VideoFeeder.VideoDataListener() {
                @Override
                public void onReceive(byte[] bytes, int size) {
                    if (mCodecManager != null) {
                        mCodecManager.sendDataToDecoder(bytes, size);
                    }
                }
            };
        } else {
            showToast("Video surface null!");
        }
    }

    private void initSDKCallback() {
        try {
            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(videoDataListener);
        } catch (Exception ignored) {
        }
    }

    /**
     * Sets the text connected to or disconnected to the drone
     */
    @SuppressLint("SetTextI18n")
    private void setDisplayContent(BaseProduct baseProduct) {
        if (baseProduct == null) {
            ((TextView) findViewById(R.id.droneConnectionStatus)).setText("Disconnected");
        } else {
            ((TextView) findViewById(R.id.droneConnectionStatus)).setText("Connected to " + baseProduct.getModel().getDisplayName());
        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            sendBroadcast(intent);
        }
    };

    /** 
     * Shows a toast message
     */
    private void showToast(final String toastMsg) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Sets a text message to a textView object
     * @param textView the textView object which should be set
     * @param text the text which should be set
     */
    public void setText(TextView textView, String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(MainActivity.getInstance(), surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(MainActivity.getInstance(), surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
package com.example.drone_interactor;

import static java.lang.Thread.sleep;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * A class which contains all the UI listeners which is necessary for the app.
 */
public class CameraHandler extends AppCompatActivity {

    private static final String TAG = CameraHandler.class.getName();

    private Button startButton;
    private int[] droneDataSnapshot;

    private final Handler handler;
    private MediaManager mediaManager;
    private List<MediaFile> mediaFileList;
    private MediaManager.FileListState currentFileListSlate = MediaManager.FileListState.UNKNOWN;
    private final File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightPolDemo/");

    protected String connectionString = "";

    /**
     * Constructor for a ViewListeners object, setting all the objects to the given parameters.
     * @param startButton start button
     */
    public CameraHandler(Button startButton) {
        this.handler = new Handler();
        this.startButton = startButton;

        // start the listener for the button start. When the button is pressed, the listener will
        // start the DroneDataProcessing, and adds a connectionString to the ConnectionToServer

        this.startButton.setOnClickListener(v -> {
            Log.i(TAG, "Clicked on Measure, " + CameraHandler.this.connectionString);
            MainActivity.getInstance().showToast("Clicked on Measure, path:" + destDir.getPath());
            //TODO: Grab sensor data

            try{
                captureAction();
            } catch (Exception e){
                MainActivity.getInstance().showToast("Error: " + e);
            }

        });


    }

    private void captureAction(){
        final Camera camera = DJISDKManager.getInstance().getProduct().getCamera();

        if(camera != null){
            camera.setPhotoFileFormat(
                    SettingsDefinitions.PhotoFileFormat.JPEG, djiError -> {
                        if (djiError != null){
                            MainActivity.getInstance().showToast("Error setting img type: " + djiError.getDescription());
                            return;
                        }
                    }
            );


            camera.setFlatMode(SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE, djiError ->  {
                    if(djiError == null) {
                        handler.postDelayed(
                                () -> camera.startShootPhoto(
                                        djiError1 ->
                                        {
                                            if(djiError1 == null){
                                                this.droneDataSnapshot = DroneDataProcessing.getInstance().getSensorData();
                                                initializeFileList(camera);
                                            } else {
                                                //TODO: Error!
                                                MainActivity.getInstance().showToast("StartShootphoto error: "+ djiError1.getDescription());
                                            }
                                        }
                                ), 2000
                        );
                    } else {
                        //TODO: Error!
                        MainActivity.getInstance().showToast("setFlatMode error:" + djiError.getDescription());
                    }
                }
            );
        }
    }

    private MediaManager.FileListStateListener updateFileListStateListener = state -> CameraHandler.this.currentFileListSlate = state;

    private void initializeFileList(Camera camera){
        this.mediaManager = camera.getMediaManager();

        if(mediaManager == null){
            MainActivity.getInstance().showToast("No media manager");
            DJILog.e("No Media Manager");
        }

        this.mediaManager.addUpdateFileListStateListener(updateFileListStateListener);

        if(this.currentFileListSlate == MediaManager.FileListState.SYNCING || this.currentFileListSlate == MediaManager.FileListState.DELETING){
            //TODO: Busy!
            MainActivity.getInstance().showToast("Invalid file list state: " + this.currentFileListSlate);

        } else {
            this.mediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, djiError -> {
                if(djiError == null){
                    if (this.currentFileListSlate != MediaManager.FileListState.INCOMPLETE && this.mediaFileList != null) {
                        this.mediaFileList.clear();
                    }

                    this.mediaFileList = this.mediaManager.getInternalStorageFileListSnapshot();

                    if(this.mediaFileList == null){
                        MainActivity.getInstance().showToast("Media file list null after snap");
                    } else {

                        Collections.sort(this.mediaFileList, (lhs, rhs) -> {
                            if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                return 1;
                            } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                return -1;
                            }
                            return 0;
                        });


                        MainActivity.getInstance().showToast("File list sorted");
                        camera.enterPlayback(
                                djiError1 -> {
                                    if (djiError1 == null) {
                                        uploadAndSendData(mediaFileList, camera);
                                    } else {
                                        MainActivity.getInstance().showToast("Error setting playback mode");
                                    }
                                }
                        );
                    }
                } else {
                    //TODO: Error!
                    MainActivity.getInstance().showToast("Error refreshing file list: " + djiError.getDescription());
                }
            });
        }
    }

    private void uploadAndSendData(List<MediaFile> mediaFileList, Camera camera){

        //TODO: MediaManager
        assert mediaFileList != null;


        mediaFileList.get(0).fetchFileData(destDir, "snap_img", new DownloadListener<String>() {
            @Override
            public void onStart() {
                MainActivity.getInstance().showToast("Snap download started...");
            }

            @Override
            public void onRateUpdate(long l, long l1, long l2) {

            }

            @Override
            public void onRealtimeDataUpdate(byte[] bytes, long l, boolean b) {

            }

            @Override
            public void onProgress(long l, long l1) {

            }

            @Override
            public void onSuccess(String filePath) {
                //TODO: SuccessToast
                //TODO: Upload file to server
                //client1.sendData(filePath, this.droneDataSnapshot);
                try {
                    ConnectionToServer.sendFile(filePath);
                } catch (Exception e) {
                    MainActivity.getInstance().showToast("Error sending file:" + e);
                }

                MainActivity.getInstance().showToast("Snap successful!");
            }

            @Override
            public void onFailure(DJIError djiError) {
                //TODO: ERROR!
                MainActivity.getInstance().showToast("File fetch error: " + djiError.getDescription());

            }
        });

        camera.exitPlayback(
                djiError -> {
                    if (djiError != null) {
                        MainActivity.getInstance().showToast("Error exiting playback: " + djiError.getDescription());
                    }
                });


    }
}

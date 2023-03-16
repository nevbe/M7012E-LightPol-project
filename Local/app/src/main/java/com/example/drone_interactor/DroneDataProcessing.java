package com.example.drone_interactor;

import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.flightassistant.FaceAwareState;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

@SuppressLint("SetTextI18n")

/**
 *
 * A singleton class which fetches and handles data from the drone.
 * Updates the position of the drone, and reads sensor data.
 *
 * It is a singleton class as there should only be one instance of
 * the class which handles data.
 */
public class DroneDataProcessing {
    private static final String TAG = DroneDataProcessing.class.getName();
    private TextViews textViews;
    private static DroneDataProcessing INSTANCE = null;
    private Aircraft aircraft = null;

    /**
     *
     * Returns the instance of the class. Creates a new one
     * if no instance exists.
     *
     * @return The instance of the ConnectionToServer class.
     */
    public static DroneDataProcessing getInstance() {
        if (DroneDataProcessing.INSTANCE == null) {
            DroneDataProcessing.INSTANCE = new DroneDataProcessing();
        }
        return DroneDataProcessing.INSTANCE;
    }

    /**
     *
     * Initializes data processing.
     *
     * @param textViews The text fields in the app.
     * @param aircraft The aircraft that we are connected to.
     */
    public void setup(TextViews textViews, Aircraft aircraft) {
        this.textViews = textViews;
        this.aircraft = aircraft;

        this.startListeningToSensors();
    }

    /**
     * private constructor to prevent initialization.
     */
    private DroneDataProcessing() {}

    private void startListeningToSensors(){
        if (this.aircraft == null){
            return;
        }


        this.aircraft.getFlightController().getFlightAssistant().setVisualPerceptionInformationCallback(
                new CommonCallbacks.CompletionCallbackWith<PerceptionInformation>() {
                    @Override
                    public void onSuccess(PerceptionInformation perceptionInformation) {
                        int forwardDistance = 60000;
                        int backwardDistance = 60000;
                        int upDistance = perceptionInformation.getUpwardObstacleDistance();
                        float angle = perceptionInformation.getAngleInterval();
                        int nDistances = perceptionInformation.getDistances().length;

                        if(nDistances >= 45){
                            backwardDistance = perceptionInformation.getDistances()[45];
                        }

                        if(nDistances > 0){
                            forwardDistance = perceptionInformation.getDistances()[0];
                        }

                        DroneDataProcessing.this.updateText(forwardDistance, backwardDistance, upDistance, angle);
                        Log.e(TAG, Arrays.toString(perceptionInformation.getDistances()));
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        MainActivity.getInstance().setText(DroneDataProcessing.this.textViews.debugText, djiError.getDescription());
                    }
                }
        );
    }

    private void updateText(int front, int back, int up, float currentAngle){

        if(front != 60000){
            MainActivity.getInstance().setText(this.textViews.forwardDistance, "Front: " + front);
        }
        if(back != 60000){
            MainActivity.getInstance().setText(this.textViews.backwardDistance, "Back: " + back);
        }
        if(up != 60000){
            MainActivity.getInstance().setText(this.textViews.upwardDistance, "Up: " + up);
        }
        if(currentAngle != 4.0F){
            MainActivity.getInstance().setText(this.textViews.currentAngle, "Angle: " + currentAngle);
        }

    }


}

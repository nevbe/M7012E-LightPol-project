package com.example.drone_interactor;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.products.Aircraft;

/**
 * A class which contains all the UI listeners which is necessary for the app.
 */
public class ViewListeners extends AppCompatActivity {

    private static final String TAG = ViewListeners.class.getName();

    private final Button startButton;
    private TextViews textViews;
    private Aircraft aircraft;
    private FlightAssistant assistant;

    protected String connectionString = "";

    /**
     * Constructor for a ViewListeners object, setting all the objects to the given parameters.
     * @param startButton start button
     */
    public ViewListeners(Button startButton, TextViews textViews, Aircraft aircraft) {
        this.startButton = startButton;
        this.textViews = textViews;

        // start the listener for the button start. When the button is pressed, the listener will
        // start the DroneDataProcessing, and adds a connectionString to the ConnectionToServer
        /*
        this.startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clicked on START, " + ViewListeners.this.connectionString);
                //TODO:
            }
        });
         */

    }
}

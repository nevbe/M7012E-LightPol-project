package com.example.drone_interactor;

import android.widget.TextView;

public class TextViews {
    public TextView droneText;
    public TextView serverStatus;
    public TextView currentAngle;
    public TextView forwardDistance;
    public TextView backwardDistance;
    public TextView upwardDistance;

    /**
     * Constructor for a TextViews object, setting all the objects to the given parameters.
     * @param droneText debug text
     * @param serverStatus motors text
     * @param currentAngle current angle text
     * @param forwardDistance forward distance text
     * @param backwardDistance backward distance text
     * @param upwardDistance upward distance text
     */
    public TextViews(
            TextView droneText,
            TextView serverStatus,
            TextView currentAngle,
            TextView forwardDistance,
            TextView backwardDistance,
            TextView upwardDistance) {
        this.droneText = droneText;
        this.serverStatus = serverStatus;
        this.currentAngle = currentAngle;
        this.forwardDistance = forwardDistance;
        this.backwardDistance = backwardDistance;
        this.upwardDistance = upwardDistance;
    }
}

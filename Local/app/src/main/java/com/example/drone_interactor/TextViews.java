package com.example.drone_interactor;

import android.view.TextureView;
import android.widget.TextView;

public class TextViews {
    public TextView debugText;
    public TextView motors;
    public TextView currentAngle;
    public TextView forwardDistance;
    public TextView backwardDistance;
    public TextView upwardDistance;

    /**
     * Constructor for a TextViews object, setting all the objects to the given parameters.
     * @param debugText debug text
     * @param motors motors text
     * @param currentAngle current angle text
     * @param forwardDistance forward distance text
     * @param backwardDistance backward distance text
     * @param upwardDistance upward distance text
     */
    public TextViews(
            TextView debugText,
            TextView motors,
            TextView currentAngle,
            TextView forwardDistance,
            TextView backwardDistance,
            TextView upwardDistance) {
        this.debugText = debugText;
        this.motors = motors;
        this.currentAngle = currentAngle;
        this.forwardDistance = forwardDistance;
        this.backwardDistance = backwardDistance;
        this.upwardDistance = upwardDistance;
    }
}

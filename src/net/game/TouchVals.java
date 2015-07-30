package net.game;

/**
 * Created by Simon on 15/06/2015.
 */
public class TouchVals {
    private String x;
    private String y;
    private String timeStampD;
    private String timeStampUp;


    public TouchVals(float x, float y, long timestampD, long timestampUp) {
        this.timeStampD = String.valueOf(timestampD);
        this.timeStampUp = String.valueOf(timestampUp);
        this.x = String.valueOf(x);
        this.y = String.valueOf(y);
    }

    @Override
    public String toString() {
        return timeStampD + ",  " + x +
                ",  " + y + ", " + timeStampUp;

    }
}
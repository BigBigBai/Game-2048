package lab4_203_36.uwaterloo.ca.lab4_203_36;

import android.content.Context;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;

public class AccelerationSensor extends TextView implements SensorEventListener {
    private final int SAMPLE = 20;
    private int counter = SAMPLE;
    private SignatureFSM LRFSM;
    private SignatureFSM UDFSM;
    GameLoopTask gameTask;

    private float[][] recordValues; //Where indexes are (0, 1, 2) = (x, y, z)

    //With Nexus 6P:
    // Noisy at C = 5, sharp signal clean
    // At C =10, noise is more smoothed out, max at 9 m/s^2
    // Secondary response disappears at c=15, sensor insensitive

    private final int C = 12;


    public AccelerationSensor (Context context, GameLoopTask gameTask){
        //Calls TextView constructor
        super(context);
        setTextColor(Color.BLACK);
        setTextSize (72);
        this.gameTask = gameTask;

        LRFSM = new SignatureFSM();
        UDFSM = new SignatureFSM();

        recordValues = new float[100][3];
        for(int i = 0; i < 100; i++){
            for (int j = 0; j < 3; j++){
                recordValues[i][j] = 0;
            }
        }
    }

    //Overriding abstract class onSensorChanged
    public void onSensorChanged (SensorEvent se){
        //Shifts readings down by one to make space for new reading
        for (int i = 99; i > 0; i--){
            for (int j = 0; j < 3; j++){
                recordValues[i][j] = recordValues[i-1][j];
            }
        }

        //Inputs filtered data at start of array
        for (int i = 0; i < 3; i++){
            recordValues[0][i] = recordValues[1][i] + (se.values[i]-recordValues[1][i])/C;
        }

        //Update FSMs: LR is x axis and UP is y axis
        LRFSM.update(recordValues[0][0], recordValues[1][0]);
        UDFSM.update(recordValues[0][1], recordValues[1][1]);

        update();
        counter--;

        if (counter <= 0) resetFSM();
    }

    public void onAccuracyChanged (Sensor s, int e){ }

    //Getter function for linkedList of data points
    public float[][] getRecordValues(){
        return recordValues;
    }

    //First update function
    private void update(){
        if (LRFSM.getDeterminedState() == SignatureState.TYPE_A){
            setText("RIGHT");
            gameTask.setDirection(Direction.RIGHT);
            counter = 0;
        }
        else if (LRFSM.getDeterminedState() == SignatureState.TYPE_B){
            setText("LEFT");
            gameTask.setDirection(Direction.LEFT);
            counter = 0;
        }
        else if (UDFSM.getDeterminedState() == SignatureState.TYPE_A){
            setText("UP");
            gameTask.setDirection(Direction.UP);
            counter = 0;
        }
        else if(UDFSM.getDeterminedState() == SignatureState.TYPE_B){
            setText("DOWN");
            gameTask.setDirection(Direction.DOWN);
            counter = 0;
        }
        else if (LRFSM.getDeterminedState() == SignatureState.TYPE_X && UDFSM.getDeterminedState() == SignatureState.TYPE_X){
            setText("UNDETERMINED");
        }
    }

    private void resetFSM(){
        counter = SAMPLE;
        LRFSM.resetState();
        UDFSM.resetState();
    }
}

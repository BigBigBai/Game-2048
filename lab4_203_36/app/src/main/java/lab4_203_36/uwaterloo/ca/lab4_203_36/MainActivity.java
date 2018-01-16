package lab4_203_36.uwaterloo.ca.lab4_203_36;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private AccelerationSensor acceleration;
    private RelativeLayout layout1;
    private final int GAMEBOARD_DIMENSIONS = 1080;
    Timer myGameLoop;
    GameLoopTask myGameLoopTask;
    private int period = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout1 = (RelativeLayout) findViewById(R.id.activity_main);
        layout1.getLayoutParams().width = GAMEBOARD_DIMENSIONS;
        layout1.getLayoutParams().height = GAMEBOARD_DIMENSIONS;
        layout1.setBackgroundResource(R.drawable.gameboard);

        myGameLoop = new Timer();
        myGameLoopTask = new GameLoopTask(this, getApplicationContext(), layout1);
        myGameLoop.schedule(myGameLoopTask, period, period);

        acceleration = new AccelerationSensor(getApplicationContext(), myGameLoopTask);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(acceleration, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        layout1.addView(acceleration);
    }


}

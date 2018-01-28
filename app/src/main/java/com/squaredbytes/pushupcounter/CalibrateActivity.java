package com.squaredbytes.pushupcounter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CalibrateActivity extends AppCompatActivity {

    float minLight=999;
    float maxLight=0;
    TextView textViewCalibrate;
    SensorManager sensorManager;
    Sensor mLight;
    SensorListener lightListener;
    private boolean ignoreSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        textViewCalibrate = (TextView) findViewById(R.id.textViewCalibrate);
        textViewCalibrate.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        textViewCalibrate.setText("Get on Position and Tap Here\n to Start");
        textViewCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CountDownTimer(3000, 100) {

                    public void onTick(long millisUntilFinished) {
                        textViewCalibrate.setText("Ready\n" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        textViewCalibrate.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
                        resetMinMax();
                        //attachListener
                        downCalibrate();
                    }
                }.start();

            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightListener=new SensorListener();


    }

    void downCalibrate()
    {
        ignoreSensor = false;
        new CountDownTimer(3000, 100) {

            public void onTick(long millisUntilFinished) {
                textViewCalibrate.setText("UP\n" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                upCalibrate();
            }
        }.start();
    }

    void upCalibrate()
    {
        new CountDownTimer(3000, 100) {

            public void onTick(long millisUntilFinished) {
                textViewCalibrate.setText("DOWN\n" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                computeResult();
            }
        }.start();
    }

    void computeResult(){
        ignoreSensor = true;
        textViewCalibrate.setText("Min : "+minLight+"\nMax : "+maxLight);
        if(minLight == 999 || maxLight == 0)
        {
            minLight=maxLight=-1f;
        }
        Intent intent=new Intent();
        intent.putExtra("min",minLight);
        intent.putExtra("max",maxLight);
        setResult(MainActivity.CALIBRATE_REQCODE,intent);
        finish();//finishing activity

    }
    void resetMinMax()
    {
        minLight=999;
        maxLight=0;
    }

    class SensorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(!ignoreSensor)
            {
                if(sensorEvent.values[0] >maxLight)
                    maxLight=sensorEvent.values[0];
                if (sensorEvent.values[0]<minLight)
                    minLight=sensorEvent.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(lightListener, mLight, SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(lightListener);
    }
    

}

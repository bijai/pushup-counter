package com.squaredbytes.pushupcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  {

    private SensorManager mSensorManager;
    private Sensor mProximity,mLight;
    private TextView textView,textView2,textViewCount,textViewState;
    private int state=0;
    int pushupCount,downThreshold=20,upThreshold=40;
    ProximityListener proximityListener;
    LightListener lightListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Reset Count ?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener(){

                            @Override
                            public void onClick(View view) {
                                pushupCount =0;
                                textViewCount.setText(String.valueOf(pushupCount));
                            }
                        }).show();
            }
        });
        pushupCount =0;
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textViewCount = (TextView) findViewById(R.id.textViewCount);
        textViewState = (TextView) findViewById(R.id.textViewState);

        proximityListener = new ProximityListener();
        lightListener = new LightListener();
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(proximityListener, mProximity, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(lightListener, mLight, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(proximityListener);
        mSensorManager.unregisterListener(lightListener);
    }

    class ProximityListener implements SensorEventListener{


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float distance = sensorEvent.values[0];
            textView.setText(String.valueOf(distance));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    class LightListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float distance = sensorEvent.values[0];
            textView2.setText(String.valueOf(distance));
            if(state ==0 && distance < downThreshold){
                textViewState.setText("DOWN");
                state =1;
            }
            else if(state ==1 && distance>upThreshold){
                textViewState.setText("UP");
                state =0;
                pushupCount ++;
                textViewCount.setText(String.valueOf(pushupCount));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}

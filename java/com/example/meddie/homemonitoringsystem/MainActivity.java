package com.example.meddie.homemonitoringsystem;

import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import java.net.Socket;
import android.os.AsyncTask;
import java.io.*;
import java.net.UnknownHostException;
import java.text.DecimalFormat;


public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener {


    private DecimalFormat df = new DecimalFormat("0.0");
    private SensorManager sensorManager;
    private Sensor sensRotation, sensLight, sensTemperature;


    private TextView thresholdDoor, thresholdLight, thresholdTemp, valueDoor, valueLight, valueTemp;
    private String valLight, valTemp, valDoor, lightstatus, tempstatus;
    private Button buttonOPEN, buttonCLOSE;


    private String values, rotateValues, lightValues, tempValues;
    private String rotaValues, doorStatus;
    private float valueOpen, valueClose;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, sensRotation, SensorManager.SENSOR_DELAY_FASTEST);


        sensTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorManager.registerListener(this, sensTemperature, SensorManager.SENSOR_DELAY_GAME);

        sensLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, sensLight,SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == (R.id.btnLight)) {
            values = "Light: " + lightValues;
            new SendSensorValues().execute();

        } else if (v.getId() == (R.id.btnMove)) {
            values = rotateValues;
            new SendSensorValues().execute();
        } else if (v.getId() == (R.id.btnTemp)) {
            values = "Temperature: " + tempValues;
            new SendSensorValues().execute();
        }
    }


    public void initializeViews() {


        valueLight = (TextView) findViewById(R.id.valueLight);
        valueDoor = (TextView) findViewById(R.id.valueDoor);
        valueTemp = (TextView) findViewById(R.id.valueTemp);
        Button btnLight = (Button) findViewById(R.id.btnLight);
        btnLight.setOnClickListener(this);
        Button btnMove = (Button) findViewById(R.id.btnMove);
        btnMove.setOnClickListener(this);
        Button btnTemp = (Button) findViewById(R.id.btnTemp);
        btnTemp.setOnClickListener(this);
        thresholdDoor = (TextView) findViewById(R.id.thresholdDoor);
        thresholdLight = (TextView) findViewById(R.id.thresholdLight);
        thresholdTemp = (TextView) findViewById(R.id.thresholdTemp);


    }



    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensRotation, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensLight, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensTemperature, SensorManager.SENSOR_DELAY_GAME);

    }


    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            final float readValues = event.values[1];
            float[] values = event.values;

            valDoor = ("x = " + df.format(values[0]) + " y = " + df.format(values[1]) + " z = " + df.format(values[2]));
            valueDoor.setText("X = " + (df.format(values[0])) + " m/s²" +
                    "\nY = " + (df.format(values[1])) + " m/s²" +
                    "\nZ = " + (df.format(values[2])) + " m/s²");

            buttonOPEN = (Button) findViewById(R.id.buttonOPEN);
            buttonCLOSE = (Button) findViewById(R.id.buttonCLOSE);

            buttonOPEN.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    valueOpen = readValues;
                }
            });

            buttonCLOSE.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    valueClose = readValues;
                }
            });

            if (valueClose < valueOpen) {
                if (event.values[1] < valueClose) {
                    rotaValues = "Y Value : " + (df.format(values[1]));
                    doorStatus = "Door Status: Closed";
                    thresholdDoor.setText(doorStatus);
                } else {
                    rotaValues = "Y Value : " + (df.format(values[1]));
                    doorStatus = "Door Status: Opened";
                    thresholdDoor.setText(doorStatus);
                }
            } else if (valueClose > valueOpen){
                if (event.values[1] > valueClose) {
                    rotaValues = "Y Value : " + (df.format(values[1]));
                    doorStatus = "Door Status: Closed";
                    thresholdDoor.setText(doorStatus);
                } else {
                    rotaValues = "Y Value : " + (df.format(values[1]));
                    doorStatus = "Door Status: Opened";
                    thresholdDoor.setText(doorStatus);
                }
                readRotate(event);
            }


        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            float[] values = event.values;
            valLight = (df.format(values[0]) + "lux");
            valueLight.setText(df.format(values[0]) + " lx");
            if (values[0] >= 70) {
                thresholdLight.setText("The room/area is bright");
                lightstatus = "Bright";
            } else {
                thresholdLight.setText("The room/area is dark");
                lightstatus = "Dark";
            }
            readLight(event);

        } else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float[] values = event.values;
            valTemp = (df.format(values[0]) + " °C");
            valueTemp.setText(df.format(values[0]) + " °C");
            valTemp = (values[0] + " °C");
            if (values[0] >= 33) {
                tempstatus = "The ambient temperature is high";
                thresholdTemp.setText(tempstatus);

            } else
                tempstatus = "The ambient temperature is low";
                thresholdTemp.setText(tempstatus);


        }
        readTemperature(event);
    }



    private void readRotate(SensorEvent event) {

        this.rotateValues = doorStatus + " , " + valDoor;
    }
    private void readLight(SensorEvent event) {
        this.lightValues = lightstatus + " , " + valueLight.getText();
    }
    private void readTemperature(SensorEvent event) {
        this.tempValues = tempstatus + " , " + valueTemp.getText();
    }



    private class SendSensorValues extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket client = new Socket("192.168.43.102", 8080, true);
                PrintWriter writerAcc = new PrintWriter(client.getOutputStream());
                writerAcc.write("Sensors's Current Reading :" + values);

                writerAcc.flush();
                writerAcc.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}








package com.student.portretov.lab4_sensor;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView aX, aY, aZ, mX, mY, mZ, proximity, light;
    ImageView imageViewBoll;
    SensorManager sensorManager;
    Sensor aSensor, mSensor, pSensor, lSensor;
    Display display;
    Point size;
    Integer brightnessValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        aX = (TextView)findViewById(R.id.tvAX);
        aY = (TextView)findViewById(R.id.tvAY);
        aZ = (TextView)findViewById(R.id.tvAZ);

        mX = (TextView)findViewById(R.id.tvMX);
        mY = (TextView)findViewById(R.id.tvMY);
        mZ = (TextView)findViewById(R.id.tvMZ);

        proximity = (TextView)findViewById(R.id.tvProximity);
        light = (TextView)findViewById(R.id.tvLight);

        imageViewBoll = (ImageView) findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    private float setXInBorder(float x, float deviation){
        final int MAX_WIDTH = size.x - 117;
        final int MIN_WIDTH = -25;
        float resultX = 0;

        resultX = imageViewBoll.getX() - ((x - deviation)/10);

        if (resultX < MIN_WIDTH){
            return MIN_WIDTH;
        }
        else if (resultX > MAX_WIDTH) {
            return MAX_WIDTH;
        }
        return resultX;
    }

    private float setYInBorder(float y, float deviation){
        final int MAX_HEIGHT = size.y - 260;
        final int MIN_HEIGHT = 0;
        float resultY = imageViewBoll.getY() + ((y - deviation)/10);

        if (resultY < MIN_HEIGHT){
            return MIN_HEIGHT;
        }
        else if (resultY > MAX_HEIGHT) {
            return MAX_HEIGHT;
        }
        return resultY;
    }

    private void setBrightness(int brightnessValue) {
        if (Settings.System.canWrite(getApplicationContext())) {
            Settings.System.putInt(
                    getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue);
            this.brightnessValue = brightnessValue;
        }
        else {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            aX.setText(Float.toString(sensorEvent.values[0]));
            aY.setText(Float.toString(sensorEvent.values[1]));
            aZ.setText(Float.toString(sensorEvent.values[2]));
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            Float x = sensorEvent.values[0],
                  y = sensorEvent.values[1],
                  z = sensorEvent.values[2];
            mX.setText(x.toString());
            mY.setText(y.toString());
            mZ.setText(z.toString());

            imageViewBoll.setX(setXInBorder(x, 22));// 22 - значение при котором телефон лежит ровно
            imageViewBoll.setY(setYInBorder(y, 5.9F)); //5.9F - значение при котором телефон лежит ровно

//            if ((x < 22 && x > 0) || (x < 0 && x > -45)) {
//                imageViewBoll.setX(setXInBorder("-", ((x - 22)/10))); // 22 - значение при котором телефон лежит ровно
//            }
//            else if (x < 49 && x > 22 ){
//                imageViewBoll.setX(setXInBorder("-", ((x - 22)/10)));
//            }

//            if (y > 0) {
//                imageViewBoll.setY(setYInBorder("+",(y - 5.9F) / 10));
//            }
//            else if (y < 0){
//                imageViewBoll.setY(setYInBorder("+",(y - 5.9F) / 10));
//            }

        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY){
            proximity.setText(Float.toString(sensorEvent.values[0]));}
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            float lightValue = sensorEvent.values[0];
            light.setText(Float.toString(lightValue));
            if (300 < lightValue ) {
                setBrightness(80);
            } else if (200 < lightValue && 300 >= lightValue) {
                setBrightness(150);
            }else if (100 < lightValue && 200 >= lightValue) {
                setBrightness(200);
            }else if (0 <= lightValue && 100 >= lightValue) {
                setBrightness(255);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,aSensor);
        sensorManager.unregisterListener(this,mSensor);
        sensorManager.unregisterListener(this,pSensor);
        sensorManager.unregisterListener(this,lSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, lSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }
}

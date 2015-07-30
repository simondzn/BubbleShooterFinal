package net.game;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Simon on 15/07/2015.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SensorService extends Service implements SensorEventListener{
    SensorManager sensorManager;
    private ArrayList sensorData;
    private ArrayList rotationData, gyroData, magnoData;
    private ArrayList linAccelData;
    String Id = BubbleShooterActivity.Id;
    private static final String TAG = BubbleShooterActivity.class.getSimpleName();
    Thread thread;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList();
        linAccelData = new ArrayList();
        rotationData = new ArrayList();
        gyroData = new ArrayList();
        magnoData = new ArrayList();
        Sensor accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor rotation = sensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor linAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linAccel,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor gyro = sensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyro,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor magno = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magno,
                SensorManager.SENSOR_DELAY_FASTEST);


        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Runnable runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "Start the thread..");
                try {
                    midWrite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread = new Thread(runnable);
        super.onCreate();

    }


    public void midWrite() throws IOException {
        Log.d(TAG,"start write!!");
//        Write the Accelerometer data
        String csv = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_Bubble.csv";
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int j=0;j<sensorData.size(); j += 1) {
            writer.writeNext(new String[]{sensorData.get(j).toString()});
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sensorData.clear();

//      Write the rotation data
        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Rotation_Bubble.csv";
        CSVWriter writer_rotation = null;
        try {
            writer_rotation = new CSVWriter(new FileWriter(csv2,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int j=0;j<rotationData.size(); j += 1) {
            writer_rotation.writeNext(new String[]{rotationData.get(j).toString()});
        }
        try {
            writer_rotation.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rotationData.clear();
        // Write the linear accelerometer
        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_lin_Bubble.csv";
        CSVWriter writer_lin = null;
        try {
            writer_lin = new CSVWriter(new FileWriter(csv3,true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int j=0;j<linAccelData.size(); j += 1) {
            writer_lin.writeNext(new String[]{linAccelData.get(j).toString()});
        }
        try {
            writer_lin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        linAccelData.clear();
        // Write the Gyroscope
        String csv4 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Gyro_Bubble.csv";
        CSVWriter writer_gyro = new CSVWriter(new FileWriter(csv4,true));
        for(int j=0;j<gyroData.size(); j += 1) {
            writer_gyro.writeNext(gyroData.get(j).toString());
        }
        writer_gyro.close();
        gyroData.clear();


//              Write the Magnometer
        String csv5 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Magno_Bubble.csv";
        CSVWriter writer_magno = new CSVWriter(new FileWriter(csv5,true));
        for(int j=0;j<magnoData.size(); j += 1) {
            writer_magno.writeNext(magnoData.get(j).toString());
        }
        writer_magno.close();
        magnoData.clear();

        Log.d(TAG, "Writing has finished!");
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        long timestamp = System.nanoTime();

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            sensorData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            rotationData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            linAccelData.add(acceVals);
        }else if(event.sensor.getType()== Sensor.TYPE_GYROSCOPE){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            gyroData.add(acceVals);
        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            magnoData.add(acceVals);
        }

        if(linAccelData.size()>2000||sensorData.size()>2000||rotationData.size()>2000)
            try {
                midWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        Log.d(TAG, "onDestroy");
        try {
            midWrite();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

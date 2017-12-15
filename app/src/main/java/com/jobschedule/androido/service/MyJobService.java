package com.jobschedule.androido.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSubscribe;

/**
 * Created by nguyenvanlinh on 12/15/17.
 */

public class MyJobService extends JobService implements MqttCallback,MqttCallbackExtended ,IMqttActionListener {
    private static final String TAG = MyJobService.class.getSimpleName();

    private MqttAndroidClient mqttAndroidClient;
    private String CLIENT_ID = "XXX";
    private String BASE_MQTT_URL = "wss://app.xxx.help:1884";  //wss://xxxxxx.help:8884
    private String TOPIC = "MYTOPIC";
    private boolean isConnected = false;
    @Override
    public void onCreate() {
        super.onCreate();
        ServiceUtils.startJobService(this ,1);
        Log.i(TAG, "Service created");
        connectMQTT();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private void connectMQTT() {
        Log.i(TAG, "connectMQTT");
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), BASE_MQTT_URL, CLIENT_ID);
        mqttAndroidClient.setCallback(this);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setKeepAliveInterval(60);
        try {
            mqttAndroidClient.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.i(TAG, "MqttException" + e.getMessage());
        }

    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "onStartJob: ");
        if (!isConnected) {
            connectMQTT();
        } else {
            ServiceUtils.startJobService(this ,3);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "onStopJob: ");
        return false;
    }


    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null && cause.getMessage() != null) {
            Log.d(TAG,"Connection Lost: " + cause.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.i(TAG, "connectComplete: " + serverURI);
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(TOPIC,2,null,this);
            mqttAndroidClient.subscribe(TOPIC, 2, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(TAG, "messageArrived: " + message.getPayload());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG, "onSuccess: Subscribe CallBack");
        isConnected = true;
        ServiceUtils.startJobService(this ,5);
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        isConnected = false;
        Log.i(TAG, "onFailure: Subscribe CallBack");
    }
}

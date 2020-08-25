package com.pushkar.packagecustomer.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.estimote.coresdk.common.requirements.DefaultRequirementsCheckerCallback;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.utils.Constants;
import com.pushkar.packagecustomer.view.DirectionsActivity;
import com.pushkar.packagecustomer.view.home.HomeActivity;

import java.util.List;
import java.util.UUID;

public class MyNotificationService extends FirebaseMessagingService {
    private final String TAG = MyNotificationService.class.getSimpleName();
    private BeaconManager beaconManager;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived hit");
        if(remoteMessage.getData()!=null){
            handleNotification(remoteMessage);
        }
    }

    private void handleNotification(RemoteMessage remoteMessage){
        createNotificationChannel();
        String notificationType = remoteMessage.getData().get("type");
        if(notificationType!= null){
            if(notificationType.equalsIgnoreCase("possession")){
                Log.d(TAG, "handleNotification: UUID: "+remoteMessage.getData().get("uuid"));
                startBeaconScanning(remoteMessage.getData().get("uuid"));
            }else if(notificationType.equalsIgnoreCase("delivered")){
                Log.d(TAG, "handleNotification: UUID: "+remoteMessage.getData().get("uuid"));
                stopBeaconScanning(remoteMessage.getData().get("uuid"));
            }
            showNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"),
                    getPendingIntentForDeliveryPoss());
        }else{
            showNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"),
                    getPendingIntentForUserAtStore(remoteMessage.getData().get("directions")));
        }

    }

    private PendingIntent getPendingIntentForUserAtStore(String direction){
        if(direction==null || direction.isEmpty()){
            direction = "N/A";
        }
        Intent notifyIntent = new Intent(this, DirectionsActivity.class);
        notifyIntent.putExtra(Constants.DIRECTIONS_INTENT_KEY,direction);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingIntentForDeliveryPoss(){
        Intent notifyIntent = new Intent(this, HomeActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startBeaconScanning(String uuid){
        beaconManager = MyBeaconApplication.getBeaconManager();
        Log.d(TAG, "startBeaconScanning beaconManager is: "+beaconManager);
        beaconManager.setBackgroundScanPeriod(2000,4000);
        beaconManager.setForegroundScanPeriod(2000,4000);
        beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
            @Override
            public void onEnteredRegion(BeaconRegion region, List<Beacon> beacons) {
                Log.d(TAG, "onEnteredRegion: I have entered the beacon we are looking for");
                UserAtStoreNotifier notifier = new UserAtStoreNotifier();
                notifier.notifyUserAtStore(
                        beacons,
                        MyBeaconApplication.getUserEmail(),
                        region.getProximityUUID().toString()
                );
            }
            @Override
            public void onExitedRegion(BeaconRegion region) {
                // could add an "exit" notification too if you want (-:
                Log.d(TAG,"Exit region");
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "onServiceReady called Manchester finally");
                beaconManager.startMonitoring(new BeaconRegion(
                        uuid,
                        UUID.fromString(uuid),
                        null, null));
            }
        });
    }

    private void stopBeaconScanning(String uuid){
        Log.d(TAG,"stopScanning beacon with uuid: "+uuid);
        beaconManager = MyBeaconApplication.getBeaconManager();
        beaconManager.stopMonitoring(uuid);
        Log.d(TAG,"beacon with uuid: "+uuid+" stopped scanning");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    public void showNotification(String title, String message, PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.tracking_128)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(message);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        int id = (int) System.currentTimeMillis();
        manager.notify(id,builder.build());
    }

    private void createNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if(manager != null){
            manager.createNotificationChannel(notificationChannel);
        }
    }
}

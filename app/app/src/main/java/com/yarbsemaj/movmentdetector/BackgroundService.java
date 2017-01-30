package com.yarbsemaj.movmentdetector;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarbs on 10/01/2017.
 */

public class BackgroundService extends Service {

    private static final String TAG = "BGService";

    private static boolean isRunning  = false;
    public Boolean run = true;
    public Boolean lastState=true;


    public String name;

    static boolean isMyServiceRunning() {
       return isRunning;
    }


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                name=intent.getExtras().getString("Name");
                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                do {
                    try {
                        new NotificationRequest(getApplicationContext()).execute();
                        Thread.sleep(10000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.i(TAG, "Service running");
                    }
                } while(run);

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }




    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    class NotificationRequest extends AsyncTask<Void, Void, Void> {

        private Context context;
        public NotificationRequest(Context context){
            this.context=context;
        }
        JSONParser jParser = new JSONParser();
        // url to get all products list

        // JSON Node names
        private static final String TAG_SUCCESS = "success";
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args){
            // getting JSON string from URL
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("Name", name));
            String url="http://bedmovement.yarbsemaj.com/get.php";
            Log.e("URL",url);
            JSONObject json = jParser.makeHttpRequest(url, "POST", params);
            Boolean success= false;
            long time = 0;
            String epoch ="";
            try {
                // Checking for SUCCESS TAG
                success = json.getBoolean("alert");
                epoch = new SimpleDateFormat("MM/dd/yyyy HH:m").format(Long.parseLong(json.getString("lastUpdate"))*1000);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Data",success + "flub");
            if (success&&lastState) {
                Notification.Builder mBuilder = new Notification.Builder(context);


                //mBuilder.setSmallIcon(R.drawable.notification_icon);
                mBuilder.setContentTitle("Alert");
                mBuilder.setContentText("No movement has been since ".concat(epoch));
                mBuilder.setSmallIcon(R.drawable.notification);
                mBuilder.setLargeIcon( BitmapFactory.decodeResource(context.getResources(), R.drawable.notification));
                mBuilder.setVibrate(new long[] { 100, 1000, 100, 1000, 100 });
                mBuilder.setStyle(new Notification.BigTextStyle()
                        .bigText("No movement has been detected since ".concat(epoch)));
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);

                Intent resultIntent = new Intent(context, Alert.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());
                //run=false;
            }
            lastState=!success;
            return null;
        }
    }

}


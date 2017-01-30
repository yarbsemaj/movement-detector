package com.yarbsemaj.movmentdetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView headerTextView=(TextView)findViewById(R.id.textView2);

        Button startStop=(Button)findViewById(R.id.ok);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        if(BackgroundService.isMyServiceRunning()){
            headerTextView.setText("Monitoring is active");
            startStop.setText("Stop monitoring");
            imageView.setBackgroundResource(R.drawable.info);
        } else {
            headerTextView.setText("Monitoring is not active");
            startStop.setText("Start monitoring");
            imageView.setBackgroundResource(R.drawable.infored);
        }


        startStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(BackgroundService.isMyServiceRunning()){
                    Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                    stopService(intent);

                    finish();
                    startActivity(getIntent());
                } else askName();
            }
        });
    }



    public void askName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter the three on word on the screen");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = input.getText().toString();
                new NotificationRequest(getApplicationContext()).execute();
            }
        });

        builder.show();

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
                success = json.getBoolean("success");
                epoch = new SimpleDateFormat("MM/dd/yyyy HH:m").format(Long.parseLong(json.getString("lastUpdate"))*1000);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Data",success + "flub");
            if (success) {
                Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                intent.putExtra("Name",name);
                startService(intent);
            } else{
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(MainActivity.this, "Make sure you copied the name down correctly", Toast.LENGTH_LONG).show();
                    }
                });


            }

            finish();
            startActivity(getIntent());
            return null;
        }
    }


}

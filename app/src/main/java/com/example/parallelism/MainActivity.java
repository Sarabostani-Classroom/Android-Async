package com.example.parallelism;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LoginDialog.LoginDialogListener {

    ProgressBar progressBar;
    TextView tv;
    Random random;
    ExecutorService executorService;
    DatePickerDialog datePicker;
    LoginDialog loginDialog;
    TimePickerDialog timePicker;
    Button downloadBtn;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        tv = findViewById(R.id.progress_text);
        downloadBtn = findViewById(R.id.download_btn);
        random = new Random();

        //Executor service
        executorService = Executors.newSingleThreadExecutor();

        datePicker = new DatePickerDialog(this);
        datePicker.setOnDateSetListener((view, year, month, dayOfMonth) -> Toast.makeText(
                getApplicationContext(),
                String.format("%02d/%02d/%d", month, dayOfMonth, year), Toast.LENGTH_LONG).show());

        timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> Toast.makeText(
                getApplicationContext(),
                String.format("%02d:%02d", hourOfDay, minute), Toast.LENGTH_LONG).show(), 0, 0, false);

        loginDialog = new LoginDialog();
        loginDialog.setCancelable(false);
    }

    public void basicCall(View view) {
        doFakeWork();
    }

    public void useThread(View view) {
        // Spawn a new thread
        new Thread(() -> {
            doFakeWork();
        }).start();
    }

    /*
    Dialogs
     */
    public void pickDate(View view) {
        datePicker.show();
    }

    public void pickTime(View view) {
        timePicker.show();
    }

    // Shows the custom dialog
    public void showDialog(View view) {
        loginDialog.show(this.getSupportFragmentManager(), "Login");
    }

    public void useHandlerThread(View view) {
        final int SOME_MESSAGE_ID = 0;
        HandlerThread thread = new HandlerThread("ProgressThread");
        thread.start();

        Looper looper = thread.getLooper();
        Handler handler = new Handler(looper) {
            // Created an anonymous class which extends Handler
            // Overriding handleMessage for handling incoming messages in a specific way
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case SOME_MESSAGE_ID:
                        // SOME_MESSAGE_ID is any int value
                        // do something
                        break;
                    // other cases
                }
            }
        };

        handler.post(() -> doFakeWork());

        // Handler.handleMessage() will be executed on the created thread
        // after the previous Runnable is finished
        handler.sendEmptyMessage(SOME_MESSAGE_ID);
    }

    public void useExecutorService(View view) {

        Log.i("INFO","Creating a Runnable...");
        Runnable runnable = () -> {
            String threadName = Thread.currentThread().getName();
            long currentMillis = System.currentTimeMillis();
            Log.i("INFO","Inside : " + threadName);
            doFakeWork();
            Log.i("INFO",String.format("Thread %s is done after %d milli seconds!", threadName, System.currentTimeMillis() - currentMillis));
        };

        Log.i("INFO","Submit the task specified by the runnable to the executor service.");
        executorService.submit(runnable);
    }


    @Override
    public void storeToken(String token) {
        Log.i("INFO",String.format("Token: %s", token));
    }

    /*
    USING ASYNCTASK -- THANKFULLY DEPRECATED/DISCOURAGED
     */

    public void useAsyncTask(View view) {
        new SingleTask().execute(new String[] {"First Task"});
    }

    private class SingleTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            doFakeWork();
            return String.format("Task \"%s\" is done!", strings[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("INFO", s);
        }
    }



    /// MOCKING FETCHERS
    // Simulating something time consuming
    private void doFakeWork() {
        while(fetchValue()) {
            Log.i("INFO", "Making Progress...");
        }
    }
    private boolean fetchValue() {
        SystemClock.sleep(1000 + random.nextInt(4000));
        setProgressBar(1 + random.nextInt(2));
        return progressBar.getProgress() < 10;
    }

    private void setProgressBar(int val) {
        int progress = val + progressBar.getProgress();
        progressBar.setProgress(progress >= 10 ? 10 : progress);
    }
}
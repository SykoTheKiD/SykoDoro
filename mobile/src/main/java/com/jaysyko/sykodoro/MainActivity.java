package com.jaysyko.sykodoro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String BREAK_TIME_TITLE = "Break Time";
    private static final String WORK_TIME_TITLE = "Work Time";
    private static final int MAX_POMODORO = 4;
    private static final String START_TITLE = "Start";
    private static final String PAUSE_TITLE = "Pause";
    private static final String WORK_TIME = "10";
    private static final String BREAK_TIME = "5";
    private static final String MAX_BREAK_TIME = "25";
    int numPomodoro = 1;
    int time, workTime, breakTime, maxBreakTime;
    boolean doneWork = false;
    int totalWorkTime, totalBreakTime = 0;
    private Button timeControlButton;
    private Handler handler = new Handler();
    private long initValue, timeMilli, timeSwapBuff, updatedTime = 0L;
    private Runnable updateStatusThread = new Runnable() {
        @Override
        public void run() {
            // Flip work done value
            doneWork ^= true;
            updateStatusText();
        }
    };
    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeMilli = SystemClock.uptimeMillis() - initValue;
            updatedTime = timeSwapBuff + timeMilli;
            int secs = (int) (updatedTime / 1000);
            int mins = secs/60;
            if(secs < time) {
                secs %= 60;
                int milli = (int)(updatedTime % 1000);
                final String seconds = String.format("%02d", secs);
                final String milliseconds = String.format("%03d", milli);
                final String time = String.format("%s:%s:%s", mins, seconds, milliseconds);
                setClock(time);
                handler.post(this);
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("SLEEP THREAD",e.getMessage());
                }
                initClock();
                displayToast();
                swapButtonText();
                handler.post(updateStatusThread);
                handler.removeCallbacksAndMessages(this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        workTime = Integer.valueOf(prefs.getString(getString(R.string.pref_work_key), WORK_TIME));
        breakTime = Integer.valueOf(prefs.getString(getString(R.string.pref_break_key), BREAK_TIME));
        maxBreakTime = Integer.valueOf(prefs.getString(getString(R.string.pref_large_break_key), MAX_BREAK_TIME));
        time = workTime;
        timeControlButton = (Button) findViewById(R.id.controlButton);
        timeControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapButtonText();
                initValue = SystemClock.uptimeMillis();
                handler.postDelayed(updateTimerThread, 0);
            }
        });
        setSupportActionBar(toolbar);
    }

    private void displayToast() {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        String toastMessage = (doneWork) ?  "Break Time Over": "Work Time Over";
        Toast toast = Toast.makeText(context, toastMessage, duration);
        toast.show();
    }

    private void updateStatusText() {
        // Update Number of cycles
        TextView countStat = (TextView) findViewById(R.id.pomoCount);
        countStat.setText(String.format("%d/4 Pomodoro Cycles Complete", numPomodoro));

        // Update total work done time
        TextView stats;
        String statsText;
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        if(doneWork){
            if(numPomodoro==MAX_POMODORO){
                numPomodoro = 0;
                time = maxBreakTime;
            }else{
                time = breakTime;
            }
            mode.setText(BREAK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.workStats);
            totalWorkTime += time;
            statsText = String.format("%d Total Minutes of Work Time", totalWorkTime);
        }else{
            numPomodoro ++;
            mode.setText(WORK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.breakStats);
            time = workTime;
            totalBreakTime += time;
            statsText = String.format("%d Total Minutes of Break Time", totalBreakTime);
        }
        stats.setText(statsText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_settings):
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case (R.id.about):
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Set value to clock
    private void setClock(String time) {
        TextView timerValue = (TextView) findViewById(R.id.timerValue);
        timerValue.setText(time);
    }

    // Flip between pause time and start time
    private void swapButtonText(){
        if(timeControlButton.getText().equals(START_TITLE)){
            timeControlButton.setText(PAUSE_TITLE);
            timeControlButton.setEnabled(false);
        }else{
            timeControlButton.setEnabled(true);
            timeControlButton.setText(START_TITLE);
        }
    }

    // Reset Clock to 0
    private void initClock(){
        initValue = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeMilli = 0L;
        setClock(getString(R.string.initClock));
    }
}
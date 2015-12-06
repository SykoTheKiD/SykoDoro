package com.jaysyko.sykodoro;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final String BREAK_TIME_TITLE = "Break Time";
    static final String WORK_TIME_TITLE = "Work Time";
    static final int MAX_POMODORO = 4;
    static final String START_TITLE = "Start";
    static final String PAUSE_TITLE = "Pause";
    static final int WORK_TIME = 1;
    static final int BREAK_TIME = 2;
    static final int MAX_BREAK_TIME = 5;

    private Button timeControlButton;
    private Handler handler = new Handler();
    private long initValue = 0L;
    long timeMilli = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int numPomodoro = 1;

    boolean doneWork = false;
    int time = WORK_TIME;
    int totalWorkTime = 0;
    int totalBreakTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        timeControlButton = (Button)findViewById(R.id.controlButton);
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
                initClock();
                swapButtonText();
                handler.post(updateStatusThread);
                handler.removeCallbacksAndMessages(this);
            }
        }
    };

    private Runnable updateStatusThread = new Runnable() {
        @Override
        public void run() {
            // Flip work done value
            doneWork ^= true;
            updateStatusText();
        }
    };

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
                time = MAX_BREAK_TIME;
            }else{
                time = BREAK_TIME;
            }
            mode.setText(BREAK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.workStats);
            totalWorkTime += WORK_TIME;
            statsText = String.format("%d Total Minutes of Work Time", totalWorkTime);
        }else{
            numPomodoro ++;
            mode.setText(WORK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.breakStats);
            time = WORK_TIME;
            totalBreakTime += BREAK_TIME;
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
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
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
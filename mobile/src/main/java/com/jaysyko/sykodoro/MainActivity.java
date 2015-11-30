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
    static final int MAX_BREAK_TIME = 25;

    private Button timeControlButton;
    private Handler handler = new Handler();
    private long initValue = 0L;
    long timeMilli = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int numCycles = 0;

    boolean doneWork = false;
    int pomodoroTime = 1;
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
            if(secs < pomodoroTime) {
                secs %= 60;
                int milli = (int)(updatedTime % 1000);
                final String seconds = String.format("%02d", secs);
                final String milliseconds = String.format("%03d", milli);
                final String time = String.format("%s:%s:%s", mins, seconds, milliseconds);
                setClock(time);
                handler.post(this);
            }else{
                handler.post(updateStatusThread);
                handler.removeCallbacksAndMessages(this);
            }
        }
    };

    private Runnable updateStatusThread = new Runnable() {
        @Override
        public void run() {
            doneWork ^= true;
            updateStatusText();
        }
    };

    private void updateStatusText() {
        TextView stats;
        String statsText;
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        if(doneWork){
            mode.setText(BREAK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.breakStats);
            totalWorkTime += WORK_TIME;
            statsText = String.format("%d Total Minutes of Work Time", totalWorkTime);
        }else{
            mode.setText(WORK_TIME_TITLE);
            stats = (TextView)findViewById(R.id.workStats);
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

    private void breakTime() {
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        mode.setText(BREAK_TIME_TITLE);
        numCycles +=1;
        if(numCycles == MAX_POMODORO){
            TextView pomoCount = (TextView)findViewById(R.id.pomoCount);
            pomoCount.setText(getString(R.string.break_text));
            numCycles = 0;
            totalBreakTime += MAX_BREAK_TIME;
        }else{
            TextView pomoCount = (TextView)findViewById(R.id.pomoCount);
            String pomoCountText = String.format("%d/4 Pomodoro Cycles Complete", numCycles);
            pomoCount.setText(pomoCountText);
            totalBreakTime += BREAK_TIME;
        }
        TextView stats = (TextView)findViewById(R.id.breakStats);
        String statsText = String.format("%d Total Minutes of Break Time",totalBreakTime);
        stats.setText(statsText);
        doneWork = true;
        initClock();
    }

    private void workTime(){
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        mode.setText(WORK_TIME_TITLE);
        totalWorkTime += WORK_TIME;
        TextView stats = (TextView)findViewById(R.id.workStats);
        String statsText = String.format("%d Total Minutes of Work Done", totalWorkTime);
        stats.setText(statsText);
        initClock();
        doneWork = false;
    }

    private void setClock(String time) {
        TextView timerValue = (TextView) findViewById(R.id.timerValue);
        timerValue.setText(time);
    }

    private void swapButtonText(){
        if(timeControlButton.getText().equals(START_TITLE)){
            timeControlButton.setText(PAUSE_TITLE);
        }else{
            timeControlButton.setText(START_TITLE);
        }
    }

    private void initClock(){
        initValue = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeMilli = 0L;
        setClock(getString(R.string.initClock));
    }
}
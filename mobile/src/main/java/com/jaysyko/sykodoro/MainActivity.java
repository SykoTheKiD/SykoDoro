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

    static final String BREAK_TIME = "Break Time";
    static final String WORK_TIME = "Work Time";
    public static final int MAX_POMODORO = 4;
    private Button timeControlButton;
    private long initValue = 0L;
    private Handler handler = new Handler();
    long timeMilli = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int numPomo = 0;
    int breakTime = 5;
    boolean work = false;
    int pomodoroTime = 1;

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
                handler.postDelayed(this, 0);
            }else{
                handler.postDelayed(updateStatusThread, 0);
                handler.removeCallbacksAndMessages(this);
            }
        }
    };
    private Runnable updateStatusThread = new Runnable() {
        @Override
        public void run() {
            if (work){
                workTime();
                work = false;
            }else{
                breakTime();
                work = true;
            }
            handler.removeCallbacksAndMessages(this);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void breakTime() {
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        mode.setText(BREAK_TIME);
        numPomo +=1;
        if(numPomo == MAX_POMODORO){
            TextView pomoCount = (TextView)findViewById(R.id.pomoCount);
            pomoCount.setText("Pomodoro Sequence Complete!\n\t\t\t\t\t\t\t\t\t\t\t\t25 Minute Break");
            numPomo = 0;
            pomodoroTime = 2;
        }else{
            TextView pomoCount = (TextView)findViewById(R.id.pomoCount);
            String pomoCountText = String.format("%d/4 Pomodoro Cycles Complete", numPomo);
            pomoCount.setText(pomoCountText);
            breakTime = 10;
            TextView stats = (TextView)findViewById(R.id.workStats);
            String statsText = String.format("%d Total Minutes of Work Done", numPomo * pomodoroTime);
            stats.setText(statsText);
        }
        initClock();
    }

    private void workTime(){
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        mode.setText(WORK_TIME);
        TextView stats = (TextView)findViewById(R.id.breakStats);
        String statsText = String.format("%d Total Minutes of Break Time", numPomo * pomodoroTime);
        stats.setText(statsText);
        initClock();
    }

    private void setClock(String time) {
        TextView timerValue = (TextView) findViewById(R.id.timerValue);
        timerValue.setText(time);
    }

    private void swapButtonText(){
        if(timeControlButton.getText().equals("Start")){
            timeControlButton.setText("Pause");
        }else{
            timeControlButton.setText("Start");
        }
    }

    private void initClock(){
        initValue = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeMilli = 0L;
        setClock("0:00:000");
    }
}
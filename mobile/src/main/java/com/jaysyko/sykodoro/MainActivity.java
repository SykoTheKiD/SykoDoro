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

    private Button timeControlButton;

    private long initValue = 0L;
    private Handler handler = new Handler();
    long timeMilli = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int numPomo = 0;
    static final int POMODORO_TIME = 5;
    static final int BREAK_TIME = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        timeControlButton = (Button)findViewById(R.id.controlButton);
        timeControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeControlButton.getText() == "Start") {
                    timeControlButton.setText("Pause");
                    initValue = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                } else {
                    timeControlButton.setText("Start");
                    timeSwapBuff += timeMilli;
                    handler.removeCallbacks(updateTimerThread);
                }
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
            if(secs < POMODORO_TIME) {
                secs %= 60;
                int milli = (int)(updatedTime % 1000);
                final String seconds = String.format("%02d", secs);
                final String milliseconds = String.format("%03d", milli);
                final String time = String.format("%s:%s:%s", mins, seconds, milliseconds);
                setClock(time);
                handler.postDelayed(this, 0);
            }else{
                startBreakTime();
                handler.removeCallbacksAndMessages(updateTimerThread);
            }
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

    private void startBreakTime() {
        TextView mode = (TextView)findViewById(R.id.mode_tv);
        mode.setText(" Break Time");
        numPomo +=1;
        TextView pomoCount = (TextView)findViewById(R.id.pomoCount);
        String pomoCountText = String.format("%d/4 Pomodoro Cycles Complete", numPomo);
        pomoCount.setText(pomoCountText);
        TextView stats = (TextView)findViewById(R.id.stats);
        String statsText = String.format("%d Minutes of Work Done\n0 minutes of Break Time", numPomo * POMODORO_TIME);
        stats.setText(statsText);
        initClock();
    }

    private void setClock(String time) {
        TextView timerValue = (TextView) findViewById(R.id.timerValue);
        timerValue.setText(time);
    }

    private void initClock(){
        initValue = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        timeMilli = 0L;
        setClock("0:00:000");
    }
}
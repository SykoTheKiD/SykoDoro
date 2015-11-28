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
    private TextView timerValue;
    private long initValue = 0L;
    private Handler handler = new Handler();
    long timeMilli = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        timerValue = (TextView) findViewById(R.id.timerValue);
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
            secs %= 60;
            int milli = (int)(updatedTime %1000);
            final String seconds = String.format("%02d", secs);
            final String milliseconds = String.format("%03d", milli);
            final String time = String.format("%s:%s:%s", mins, seconds, milliseconds);
            timerValue.setText(time);
            handler.postDelayed(this, 0);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

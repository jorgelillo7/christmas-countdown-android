package com.jorgelillo.christmascountdown;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final long TICK_MILLIS = 1000;

    private TextView txtTimerDay, txtTimerHour, txtTimerMinute, txtTimerSecond;
    private TextView tvEvent;
    private View countdownTop, counters, countdownBottom;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            updateCountdown();
            // Align the next tick with the start of the next wall-clock second.
            handler.postDelayed(this, TICK_MILLIS - System.currentTimeMillis() % TICK_MILLIS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View content = findViewById(R.id.content);
        int basePadding = content.getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(basePadding + bars.left, basePadding + bars.top,
                    basePadding + bars.right, basePadding + bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        txtTimerDay = findViewById(R.id.txtTimerDay);
        txtTimerHour = findViewById(R.id.txtTimerHour);
        txtTimerMinute = findViewById(R.id.txtTimerMinute);
        txtTimerSecond = findViewById(R.id.txtTimerSecond);
        tvEvent = findViewById(R.id.tvhappyevent);
        countdownTop = findViewById(R.id.countdownGroupTop);
        counters = findViewById(R.id.counters);
        countdownBottom = findViewById(R.id.countdownGroupBottom);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(tick);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(tick);
        super.onStop();
    }

    private void updateCountdown() {
        Calendar now = Calendar.getInstance();
        boolean christmas = ChristmasCountdown.isChristmasDay(now);

        int countdownVisibility = christmas ? View.GONE : View.VISIBLE;
        countdownTop.setVisibility(countdownVisibility);
        counters.setVisibility(countdownVisibility);
        countdownBottom.setVisibility(countdownVisibility);
        tvEvent.setVisibility(christmas ? View.VISIBLE : View.GONE);
        if (christmas) {
            return;
        }

        long[] parts = ChristmasCountdown.split(ChristmasCountdown.millisUntilChristmas(now));
        txtTimerDay.setText(format(parts[0]));
        txtTimerHour.setText(format(parts[1]));
        txtTimerMinute.setText(format(parts[2]));
        txtTimerSecond.setText(format(parts[3]));
    }

    private static String format(long value) {
        return String.format(Locale.ROOT, "%02d", value);
    }
}

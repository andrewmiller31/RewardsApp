package rewards.rewardsapp.views;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import rewards.rewardsapp.R;


/**
 * Created by Andrew Miller on 10/24/2017.
 */

public class OverlayHUD extends Service implements View.OnTouchListener {
    private TextView enabledText;
    public static boolean isRunning;
    private static boolean showAd;
    private AdView mAdView;

    @Override
    public void onCreate() {
        super.onCreate();
        enabledText = new TextView(this);

        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        GradientDrawable rect = new GradientDrawable();
        rect.setShape(GradientDrawable.RECTANGLE);
        rect.setColor(ContextCompat.getColor(this, R.color.op50Black));
        rect.setSize(650, 45);
        rect.setCornerRadii(new float[] { 12, 12, 12, 12, 12, 12, 12, 12});

        enabledText.setText("Surf & Earn Enabled");
        enabledText.setTextSize(16);
        enabledText.setBackground(rect);
        enabledText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        enabledText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
        params.verticalMargin = 0.01f;
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(enabledText, params);

        final Handler handler = new Handler();
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                if(showAd) {
                    params.verticalMargin = 0.01f;
                    wm.addView(enabledText, params);
                    wm.removeView(mAdView);
                    showAd = false;
                }
                else {
                    params.verticalMargin = 0;
                    wm.removeView(enabledText);
                    wm.addView(mAdView, params);
                    showAd = true;
                }
                if (isRunning) {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(task, 1000);
        isRunning = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(this,"Overlay button event", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(enabledText != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(enabledText);
            enabledText = null;
        }
    }
}

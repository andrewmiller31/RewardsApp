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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import rewards.rewardsapp.R;


/**
 * Created by Andrew Miller on 10/24/2017.
 */

public class OverlayHUD extends Service implements View.OnTouchListener, RewardedVideoAdListener {
    private TextView enabledText;
    public static boolean isRunning;
    private static boolean showAd;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private int clickCount;
    private Runnable task;

    @Override
    public void onCreate() {
        clickCount = 1;
        super.onCreate();
        enabledText = new TextView(this);
        enabledText.setOnTouchListener(this);
        mAdView = new AdView(this);
        mAdView.setOnTouchListener(this);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

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
        task = new Runnable() {
            @Override
            public void run() {
                if(showAd && isRunning) {
                    params.verticalMargin = 0.01f;
                    wm.addView(enabledText, params);
                    wm.removeView(mAdView);
                    showAd = false;
                    handler.postDelayed(this, 5000);
                }
                else if(isRunning){
                    params.verticalMargin = 0;
                    wm.removeView(enabledText);
                    wm.addView(mAdView, params);
                    showAd = true;
                    handler.postDelayed(this, 30000);
                }
            }
        };
        handler.postDelayed(task, 30000);
        isRunning = true;
        loadRewardedVideoAd();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        clickCount++;
        mAdView.performClick();
        if(clickCount%30 == 0) {
//            Toast.makeText(this, "VIDEO AD HERE", Toast.LENGTH_SHORT).show();
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
                loadRewardedVideoAd();
            }
            else loadRewardedVideoAd();
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if(enabledText != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(enabledText);
        }
        mAdView.destroy();
        enabledText = null;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "ERROR: problem loading video.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }
}

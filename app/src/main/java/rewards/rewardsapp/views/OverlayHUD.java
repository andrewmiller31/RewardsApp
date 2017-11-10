package rewards.rewardsapp.views;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.ads.mediation.OnContextChangedListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;


/**
 * Created by Andrew Miller on 10/24/2017.
 */

public class OverlayHUD extends Service implements View.OnTouchListener, RewardedVideoAdListener {
    private TextView enabledText;
    private static boolean isRunning;
    private boolean showAd;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private int clickCount;
    private Presenter presenter;
    private static boolean returningFromAd;

    //amount the user earns per video watched
    private static final int EARN_AMOUNT = 10;
    //amount of clicks between ads
    private static final int CLICKS = 30;
    //amount of time ad banner shows(1000 = 1 sec)
    private static final int BAN_TIME = 30000;
    //amount of time enabled text shows between ads
    private static final int TEXT_TIME = 5000;

    @Override
    public void onCreate() {
        super.onCreate();
        presenter = new Presenter();
        viewSetup();
        loadRewardedVideoAd();
        wmSetup();

        clickCount = 1;
        isRunning = true;
    }

//    public static void setContext(Context context){
//        this.context = context;
//    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void stopRunning(){
        isRunning = false;
    }

    public static boolean isReturningFromAd(){
        return returningFromAd;
    }

    public static void setReturningFromAd(boolean rfa){
        returningFromAd = rfa;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    private void viewSetup(){
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
    }

    private void wmSetup(){
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.BOTTOM;
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        params.verticalMargin = 0.01f;
        assert wm != null;
        wm.addView(enabledText, params);
        showAd = false;

        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (showAd && isRunning) {
                    params.verticalMargin = 0.01f;
                    wm.addView(enabledText, params);
                    wm.removeView(mAdView);
                    showAd = false;
                    handler.postDelayed(this, TEXT_TIME);
                } else if (isRunning) {
                    params.verticalMargin = 0;
                    wm.removeView(enabledText);
                    wm.addView(mAdView, params);
                    showAd = true;
                    handler.postDelayed(this, BAN_TIME);
                }
                if (!isRunning) {
                    handler.removeCallbacksAndMessages(this);
                }
            }
        };
        handler.postDelayed(task, TEXT_TIME);
    }

    private void runAd(int num){
        int count = num;
        if (mRewardedVideoAd.isLoaded()) {
            disableChildren();
            mRewardedVideoAd.show();
            returningFromAd = true;
        }
        else if(count <= 100){
            count++;
            runAd(count);
        }
        else Toast.makeText(this, "Failed to load ad.", Toast.LENGTH_SHORT).show();
    }

    private void disableChildren(){
        enabledText.setVisibility(View.GONE);
        enabledText.setClickable(false);
        mAdView.setVisibility(View.GONE);
        mAdView.setClickable(false);
    }

    private void enableChildren(){
        enabledText.setVisibility(View.VISIBLE);
        enabledText.setClickable(true);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.setClickable(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        clickCount++;
        mAdView.performClick();
        if(clickCount%CLICKS == 0) runAd(0);
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRewardedVideoAd.destroy(this);
        isRunning = false;
        try {
            assert ((WindowManager) getSystemService(WINDOW_SERVICE)) != null;
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(enabledText);
        }
        catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        mAdView.destroy();
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "+10 Points", Toast.LENGTH_SHORT).show();
        presenter.sendPoints(EARN_AMOUNT);
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
//        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        returningFromAd = true;
        enableChildren();
        loadRewardedVideoAd();
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




package rewards.rewardsapp.views;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.cooltechworks.views.ScratchImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.ScratchInformation;
import rewards.rewardsapp.models.ImageInfo;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private ScratchImageView scratch1;
    private ScratchImageView scratch2;
    private ScratchImageView scratch3;
    private ScratchImageView scratch4;
    private ScratchImageView scratch5;
    private ScratchImageView scratch6;
    private ScratchImageView tokenScratch;

    private boolean[] revealed;
    private boolean over;
    private RewardedVideoAd mRewardedVideoAd;
    private int winAmount;
    private int tokenWinAmount;
    private ImageView winner;
    private Presenter presenter;
    private boolean rewarded;
    private PopupWindow popUp;
    private ConstraintLayout cLayout;
    private ScratchInformation testScratch;
    private ImageInfo[] icons;
    private ImageInfo[] bonusIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        winAmount = 0;
        tokenWinAmount = 0;
        over = false;
        rewarded = true;
        presenter = new Presenter();

        revealed = new boolean[7];
        setContentView(R.layout.activity_scratch);
        cLayout = (ConstraintLayout) findViewById(R.id.scratch_layout);
        ImageView background = (ImageView) findViewById(R.id.card_background);
        winner = (ImageView) findViewById(R.id.match_symbol);
        winner.setImageResource(R.drawable.scratch_dog);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());

        try {
            String test = presenter.restGet("scratch", null);
            JSONObject testObject = new JSONObject(test);
            testScratch = new ScratchInformation(testObject);
            background.setImageBitmap(testScratch.getBackground());

            icons = testScratch.getIcons();
            bonusIcons = testScratch.getBonusIcons();
            presenter.setScratchModel( "scratchModel", icons);
            presenter.setScratchModel( "tokenModel", bonusIcons);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setCard();
    }

    private void endGame(){
        checkWin();

        if(winAmount != 0 || tokenWinAmount != 0){
            rewarded = false;
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View claimView = inflater.inflate(R.layout.claim_popup,null);
            popUp = new PopupWindow(
                    claimView,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );

            // requires API level 21
            if(Build.VERSION.SDK_INT>=21){
                popUp.setElevation(5.0f);
            }

            Button closeButton = (Button) claimView.findViewById(R.id.claim_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runAd();
                }
            });

            TextView prizeText = (TextView) claimView.findViewById(R.id.prize_text);

            if(tokenWinAmount != 0 && winAmount != 0) {
                prizeText.setText(tokenWinAmount + " tokens\n" + winAmount + " points");
            }
            else if(tokenWinAmount != 0){
                prizeText.setText(tokenWinAmount + " tokens");
            }
            else if(winAmount != 0){
                prizeText.setText(winAmount + " points");
            }
            popUp.showAtLocation(cLayout, Gravity.CENTER,0,0);
        }
        else{
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View claimView = inflater.inflate(R.layout.lose_popup,null);
            popUp = new PopupWindow(
                    claimView,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );

            // requires API level 21
            if(Build.VERSION.SDK_INT>=21){
                popUp.setElevation(5.0f);
            }

            Button closeButton = (Button) claimView.findViewById(R.id.okay_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popUp.dismiss();
                    ScratchActivity.this.onBackPressed();
                }
            });

            popUp.showAtLocation(cLayout, Gravity.CENTER,0,0);
        }
    }

    private void checkWin(){
        List<ImageInfo> iconsImageInfos = presenter.getFrequencies("scratchModel");
        List<ImageInfo> bonusIconsImageInfos = presenter.getFrequencies("tokenModel");

        for(int i = 0; i < iconsImageInfos.size(); i++) {
            if (iconsImageInfos.get(i).getType().equals("points")) {
                winAmount = iconsImageInfos.get(i).getReward();
            } else if (iconsImageInfos.get(i).getType().equals("tokens")) {
                tokenWinAmount += iconsImageInfos.get(i).getReward();
            }
        }

        for(int i = 0; i < bonusIconsImageInfos.size(); i++){
            if (bonusIconsImageInfos.get(i).getType().equals("points")) {
                winAmount += bonusIconsImageInfos.get(i).getReward();
            } else if (bonusIconsImageInfos.get(i).getType().equals("tokens")) {
                Log.d("Token id: ", Integer.toString(bonusIconsImageInfos.get(i).getImageID()));
                Log.d("Token reward: ", Integer.toString(bonusIconsImageInfos.get(i).getReward()));
                tokenWinAmount += bonusIconsImageInfos.get(i).getReward();
            }
        }

    }

    private void setCard(){
        scratch1 = (ScratchImageView) findViewById(R.id.scratch_view1);
        scratch2 = (ScratchImageView) findViewById(R.id.scratch_view2);
        scratch3 = (ScratchImageView) findViewById(R.id.scratch_view3);
        scratch4 = (ScratchImageView) findViewById(R.id.scratch_view4);
        scratch5 = (ScratchImageView) findViewById(R.id.scratch_view5);
        scratch6 = (ScratchImageView) findViewById(R.id.scratch_view6);

        scratch1.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());
        scratch2.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());
        scratch3.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());
        scratch4.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());
        scratch5.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());
        scratch6.setImageBitmap(icons[presenter.scratchNumGen("scratchModel")].getImage());

        scratchListenerSet(scratch1, 0);
        scratchListenerSet(scratch2, 1);
        scratchListenerSet(scratch3, 2);
        scratchListenerSet(scratch4, 3);
        scratchListenerSet(scratch5, 4);
        scratchListenerSet(scratch6, 5);

        tokenScratch = (ScratchImageView) findViewById(R.id.token_scratch);
        tokenScratch.setImageBitmap(bonusIcons[presenter.scratchNumGen("tokenModel")].getImage());
        scratchListenerSet(tokenScratch, 6);
    }

    private void scratchListenerSet(ScratchImageView siv, final int num) {
        siv.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                if(percent < 25) revealed[num] = true;
                if(presenter.checkAllRevealed(revealed) && !over){
                    over = true;
                    endGame();
                }
            }
        });
    }

    //Checks if ad is loaded until it is loaded and then runs ad
    private void runAd(){
        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    handler.removeCallbacksAndMessages(this);
                    return;
                }
                handler.postDelayed(this, 50);
            }
        };
        handler.post(task);
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

    @Override
    public void onRewardedVideoAdClosed() {
        if(rewarded) {
            this.onBackPressed();
        }
        else {
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
            Toast.makeText(this, "You must watch the video to receive points!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        presenter.sendPoints(winAmount, tokenWinAmount, 0, getIntent().getStringExtra("id"));
        Toast.makeText(this, "+" + winAmount + " Points, +" + tokenWinAmount + " Tokens", Toast.LENGTH_SHORT).show();
        rewarded = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRewardedVideoAd.destroy(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        mRewardedVideoAd.destroy(this);
    }

    @Override
    public void onBackPressed(){
        if(rewarded){
            finish();
        }
        else Toast.makeText(this, "Watch the video to claim your points!", Toast.LENGTH_SHORT).show();
    }
}

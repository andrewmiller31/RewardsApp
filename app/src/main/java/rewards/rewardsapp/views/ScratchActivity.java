package rewards.rewardsapp.views;

import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private ScratchImageView scratch1;
    private ScratchImageView scratch2;
    private ScratchImageView scratch3;
    private ScratchImageView scratch4;
    private ScratchImageView scratch5;
    private ScratchImageView scratch6;
    private ScratchImageView tokenScratch;

    private static int[] imageBank = {R.drawable.scratch_one_token, R.drawable.scratch_cow, R.drawable.scratch_dog, R.drawable.scratch_pig, R.drawable.scratch_sheep};
    private static int[] tokenBank = {R.drawable.scratch_one_token, R.drawable.scratch_one_token, R.drawable.scratch_one_token,
            R.drawable.scratch_token_pile, R.drawable.scratch_token_pile, R.drawable.scratch_token_pile, R.drawable.scratch_token_jackpot,
            R.drawable.scratch_lose, R.drawable.scratch_lose, R.drawable.scratch_lose};
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

    //constants
    private static final int SCRATCH_WIN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        winAmount = 0;
        tokenWinAmount = 0;
        over = false;
        rewarded = true;
        presenter = new Presenter();

        presenter.setScratchModel( "tokenModel",tokenBank);
        presenter.setScratchModel( "scratchModel",imageBank);

        revealed = new boolean[7];
        setContentView(R.layout.activity_scratch);
        cLayout = (ConstraintLayout) findViewById(R.id.scratch_layout);
        ImageView background = (ImageView) findViewById(R.id.card_background);
        background.setImageResource(R.drawable.background_field);
        winner = (ImageView) findViewById(R.id.match_symbol);
        winner.setImageResource(R.drawable.scratch_dog);
        setCard();


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    private void endGame(){

        if(presenter.checkScratchFrequency("scratchModel", R.drawable.scratch_dog) >= 3){
            winAmount = SCRATCH_WIN;
        }

        tokenWinAmount += presenter.checkScratchFrequency("scratchModel", R.drawable.scratch_one_token);
        tokenWinAmount += presenter.checkScratchFrequency("tokenModel", R.drawable.scratch_one_token);
        tokenWinAmount += (5 * presenter.checkScratchFrequency("tokenModel", R.drawable.scratch_token_pile));
        tokenWinAmount += (10 * presenter.checkScratchFrequency("tokenModel", R.drawable.scratch_token_jackpot));

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

    private void setCard(){
        scratch1 = (ScratchImageView) findViewById(R.id.scratch_view1);
        scratch2 = (ScratchImageView) findViewById(R.id.scratch_view2);
        scratch3 = (ScratchImageView) findViewById(R.id.scratch_view3);
        scratch4 = (ScratchImageView) findViewById(R.id.scratch_view4);
        scratch5 = (ScratchImageView) findViewById(R.id.scratch_view5);
        scratch6 = (ScratchImageView) findViewById(R.id.scratch_view6);

        scratch1.setImageResource(presenter.scratchNumGen("scratchModel"));
        scratch2.setImageResource(presenter.scratchNumGen("scratchModel"));
        scratch3.setImageResource(presenter.scratchNumGen("scratchModel"));
        scratch4.setImageResource(presenter.scratchNumGen("scratchModel"));
        scratch5.setImageResource(presenter.scratchNumGen("scratchModel"));
        scratch6.setImageResource(presenter.scratchNumGen("scratchModel"));

        scratchListenerSet(scratch1, 0);
        scratchListenerSet(scratch2, 1);
        scratchListenerSet(scratch3, 2);
        scratchListenerSet(scratch4, 3);
        scratchListenerSet(scratch5, 4);
        scratchListenerSet(scratch6, 5);

        tokenScratch = (ScratchImageView) findViewById(R.id.token_scratch);
        tokenScratch.setImageResource(presenter.scratchNumGen("tokenModel"));
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

package rewards.rewardsapp.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.ScratchImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.List;
import rewards.rewardsapp.R;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private ScratchImageView scratch1;
    private ScratchImageView scratch2;
    private ScratchImageView scratch3;
    private ScratchImageView scratch4;
    private ScratchImageView scratch5;
    private ScratchImageView scratch6;
    private static int[] imageBank = {R.drawable.scratch_cow, R.drawable.scratch_dog, R.drawable.scratch_pig, R.drawable.scratch_sheep}; //, R.drawable.scratch_cat
    private TextView resultMessage;
    private boolean[] revealed;
    private boolean over;
    private RewardedVideoAd mRewardedVideoAd;
    private int winAmount;
    private Button claim;
    private Presenter presenter;
    private boolean rewarded;

    //constants
    private static final int LITTLE_WIN = 10;
    private static final int SMALL_WIN = 100;
    private static final int MEDIUM_WIN = 1000;
    private static final int LARGE_WIN = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        over = false;
        rewarded = false;
        presenter = new Presenter();
        presenter.setScratchModel(imageBank);
        setContentView(R.layout.activity_scratch);
        revealed = new boolean[6];
        resultMessage = (TextView) findViewById(R.id.end_result);
        claim = (Button) findViewById(R.id.claim_button);
        setCard();
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    private void endGame(){

        int winNum = presenter.checkScratchWin();

        switch (winNum){
            case 3:
                resultMessage.setText("Little win. +" + LITTLE_WIN + " points");
                winAmount = LITTLE_WIN;
                break;
            case 4:
                resultMessage.setText("Small win. +" + SMALL_WIN + " points");
                winAmount = SMALL_WIN;
                break;
            case 5:
                resultMessage.setText("Medium win! +" + MEDIUM_WIN + " points");
                winAmount = MEDIUM_WIN;
                break;
            case 6:
                resultMessage.setText("MAJOR PRIZE!! +" + LARGE_WIN + " points");
                winAmount = LARGE_WIN;
                break;
            default:
                resultMessage.setText("Loss!");
                winAmount = 0;
                rewarded = true;
                return;
        }
        if(winAmount != 0){
            claim.setVisibility(View.VISIBLE);
            claim.setClickable(true);
        }
    }

    private void setCard(){
        scratch1 = (ScratchImageView) findViewById(R.id.scratch_view1);
        scratch2 = (ScratchImageView) findViewById(R.id.scratch_view2);
        scratch3 = (ScratchImageView) findViewById(R.id.scratch_view3);
        scratch4 = (ScratchImageView) findViewById(R.id.scratch_view4);
        scratch5 = (ScratchImageView) findViewById(R.id.scratch_view5);
        scratch6 = (ScratchImageView) findViewById(R.id.scratch_view6);

        scratch1.setImageResource(presenter.scratchNumGen());
        scratch2.setImageResource(presenter.scratchNumGen());
        scratch3.setImageResource(presenter.scratchNumGen());
        scratch4.setImageResource(presenter.scratchNumGen());
        scratch5.setImageResource(presenter.scratchNumGen());
        scratch6.setImageResource(presenter.scratchNumGen());

        scratchListenerSet(scratch1, 0);
        scratchListenerSet(scratch2, 1);
        scratchListenerSet(scratch3, 2);
        scratchListenerSet(scratch4, 3);
        scratchListenerSet(scratch5, 4);
        scratchListenerSet(scratch6, 5);
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

    public void claimPoints(View view){
        if(mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        else {
            Toast.makeText(this, "Ad not loaded. Must be watched to receive points.", Toast.LENGTH_SHORT).show();
        }

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
            onBackPressed();
        }
        else {
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
            Toast.makeText(this, "You must watch the video to receive points!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        presenter.sendPoints(winAmount);
        Toast.makeText(this, "+" + winAmount + " Points", Toast.LENGTH_SHORT).show();
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
    }
}

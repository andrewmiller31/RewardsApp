package rewards.rewardsapp.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.presenters.Presenter;

public class SlotsActivity extends AppCompatActivity implements RewardedVideoAdListener {

    //presenter
    private Presenter presenter;

    //arrays
    private SlotReel.ReelListener[] reelListeners;
    private ImageView[] slotImgs;
    private static int[] imageBank = {R.drawable.slots_cherry, R.drawable.slots_chili, R.drawable.slots_gold,
            R.drawable.slots_horseshoe, R.drawable.slots_moneybag}; //R.drawable.slots_bell

    //views
    private Button spin;
    private Button autoSpin;
    private Button claim;
    private TextView resultMsg;
    TextView spinsLeft;
    TextView pointsEarned;
    private boolean done;
    private boolean rewarded;


    //constants
    private static final int SMALL_WIN = 100;
    private static final int MEDIUM_WIN = 1000;
    private static final int LARGE_WIN = 10000;
    private static final long AUTO_CLICK_WAIT = 100;

    private int spinCount, totalPoints;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new Presenter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);
        slotImgs = new ImageView[5];
        reelListeners = new SlotReel.ReelListener[slotImgs.length];
        reelListenerSetup();
        presenter.setSlotsModel(imageBank, reelListeners);
        findViews();
        spinCount = 10;
        totalPoints = 0;
        spinsLeft.setText(Integer.toString(spinCount));
        pointsEarned.setText(Integer.toString(totalPoints));

        rewarded = true;
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    //spins the reels and waits for them to stop spinning before checking for win
    public void spin(View view){
        if(spinCount > 0) {
            resultMsg.setText("");
            done = false;
            spin.setText("Spinning...");
            autoSpin.setText("Spinning...");
            autoSpin.setClickable(false);
            spin.setClickable(false);
            spinCount--;
            spinsLeft.setText(Integer.toString(spinCount));

            presenter.spinReels();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkWinStatus();
                    if (spinCount != 0) {
                        spin.setClickable(true);
                        spin.setText("Spin");
                        autoSpin.setClickable(true);
                        autoSpin.setText("Auto Spin");
                    } else {
                        spin.setText("NO SPINS LEFT");
                        autoSpin.setText("NO SPINS LEFT");
                        try {
                            String jsonResponse = presenter.restGet("getPointsInfo", null);
                            JSONObject pointsInfo = new JSONObject(jsonResponse);
                            resultMsg.setText("Overall points earned: " + pointsInfo.get("totalEarned").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        done = true;
                    }
                }
            }, presenter.getSpinTime());
        }
    }

    //spins the slot machine until all spins are used
    public void autoSpin(View view){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                spin.performClick();
                if(!done) clickAgain();
            }
        });
    }

    //helper method for autoSpin
    private void clickAgain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spin.performClick();
                if(!done) clickAgain();
            }
        }, presenter.getSpinTime() + AUTO_CLICK_WAIT);
    }

    //sets the reel listeners that are sent to the SlotsModel through the presenter
    private void reelListenerSetup(){
        for(int i = 0; i < slotImgs.length; i++) {
            final int finalI = i;
            reelListeners[i] = new SlotReel.ReelListener() {
                @Override
                public void newIcon(final int img) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            slotImgs[finalI].setImageResource(img);
                        }
                    });
                }
            };
        }

    }

    //checks with the presenter for the number of matches and processes the result
    private void checkWinStatus(){
        int winNum = presenter.checkSlotsWin();
        int winAmount = 0;

        switch (winNum){
            case 3: resultMsg.setText("Small win. +" + SMALL_WIN + " points");
                winAmount = SMALL_WIN;
                totalPoints += SMALL_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 4: resultMsg.setText("Medium win! +" + MEDIUM_WIN + " points");
                winAmount = MEDIUM_WIN;
                totalPoints += MEDIUM_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 5:
                resultMsg.setText("MAJOR PRIZE!! +" + LARGE_WIN + " points");
                winAmount = LARGE_WIN;
                totalPoints += LARGE_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            default: resultMsg.setText("You lose :(");
                return;
        }
        if(totalPoints > 0){
            claim.setClickable(true);
            claim.setVisibility(View.VISIBLE);
            rewarded = false;
        }
    }

    //locates all the views used
    private void findViews(){
        slotImgs[0] = (ImageView) findViewById(R.id.slot_1);
        slotImgs[1] = (ImageView) findViewById(R.id.slot_2);
        slotImgs[2] = (ImageView) findViewById(R.id.slot_3);
        slotImgs[3] = (ImageView) findViewById(R.id.slot_4);
        slotImgs[4] = (ImageView) findViewById(R.id.slot_5);
        spin = (Button) findViewById(R.id.spin);
        autoSpin = (Button) findViewById(R.id.auto_spin);
        claim = (Button) findViewById(R.id.claim_slots);
        resultMsg = (TextView) findViewById(R.id.win_message);
        spinsLeft = (TextView) findViewById(R.id.remaining);
        pointsEarned = (TextView) findViewById(R.id.points_won);
        claim = (Button) findViewById(R.id.claim_slots);
    }

    public void claimPoints(View view){
        runAd();
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
    public void onRewarded(RewardItem rewardItem){
        presenter.sendPoints(totalPoints);
        Toast.makeText(this, "+" + totalPoints + " Points", Toast.LENGTH_SHORT).show();
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

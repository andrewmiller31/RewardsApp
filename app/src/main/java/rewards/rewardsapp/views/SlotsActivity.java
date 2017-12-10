package rewards.rewardsapp.views;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.ImageInfo;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.SlotsInformation;
import rewards.rewardsapp.presenters.Presenter;

public class SlotsActivity extends AppCompatActivity implements RewardedVideoAdListener {

    //presenter
    private Presenter presenter;

    //arrays
    private SlotReel.ReelListener[] reelListeners;
    private ImageView[] slotImgs;

    //views
    private Button spin;
    TextView tokensLeft;
    TextView totalEarned;
    TextView jackpotView;
    private boolean rewarded;
    private PopupWindow claimPopUp;
    private ConstraintLayout cLayout;


    //constants
    private static final int SMALL_WIN = 3;
    private static final int MEDIUM_WIN = 5;
    private static final int LARGE_WIN = 10;
    private static final long AUTO_CLICK_WAIT = 100;

    private int tokenCount, pointsEarned, currentPoints, cost, jackpot, jackpotID;

    private RewardedVideoAd mRewardedVideoAd;

    private SlotsInformation testSlots;
    private ImageInfo[]icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new Presenter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);
        slotImgs = new ImageView[5];
        reelListeners = new SlotReel.ReelListener[slotImgs.length];
        reelListenerSetup();
        pointsEarned = 0;

        rewarded = true;
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());

        cLayout = (ConstraintLayout) findViewById(R.id.slots_layout);

        try {
            String test = presenter.restGet("slots", null);
            JSONObject testObject = new JSONObject(test);
            testSlots = new SlotsInformation(testObject);
            icons = testSlots.getIcons();
            findViews();
            cLayout.setBackground(new BitmapDrawable(getResources(), testSlots.getBackground()));
            cost = testSlots.getCost();
            updateJackpot(testSlots.getJackpot());
            jackpotID = testSlots.getJackpotImageID();
            presenter.setSlotsModel(icons, reelListeners);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initTokensPointsCount();
    }


    //spins the reels and waits for them to stop spinning before checking for win
    public void spin(View view){
        try{
            String jsonResponse = presenter.sendPoints(0, 0, cost, getIntent().getStringExtra("id"));
            JSONObject updateResult = new JSONObject(jsonResponse);

            JSONObject slotCost = new JSONObject();
            slotCost.put("cost", cost);
            String newJackpot = presenter.restPut("slotsJackpot", slotCost.toString());
            JSONObject updateSlots = new JSONObject(newJackpot);
            updateJackpot(updateSlots.getInt("jackpot"));
            if(updateResult.get("status").toString().equals("negative")){
                Toast.makeText(this, "You don't have enough tokens!", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                spin.setText("Spinning...");
                spin.setClickable(false);
                tokenCount -= cost;
                tokensLeft.setText(Integer.toString(tokenCount));
                presenter.spinReels();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkWinStatus();
                        spin.setText("Spin to Win!");
                        spin.setClickable(true);
                    }
                }, presenter.getSpinTime());
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateJackpot(int newPot){
        jackpot = newPot;
        String viewString = Integer.toString(newPot);
        while(viewString.length() != 10){
            viewString = "0" + viewString;
        }
        jackpotView.setText(viewString);
    }


    //sets the reel listeners that are sent to the SlotsModel through the presenter
    private void reelListenerSetup(){
        for(int i = 0; i < slotImgs.length; i++) {
            final int finalI = i;
            reelListeners[i] = new SlotReel.ReelListener() {
                @Override
                public void newIcon(final Bitmap img) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            slotImgs[finalI].setImageBitmap(img);
                        }
                    });
                }
            };
        }

    }

    //checks with the presenter for the number of matches and processes the result
    private void checkWinStatus(){
        List winner = presenter.checkSlotsWin();

        switch ((Integer)winner.get(1)){
            case 3:
                pointsEarned = SMALL_WIN;
                break;
            case 4:
                pointsEarned = MEDIUM_WIN;
                break;
            case 5:
                ImageInfo winningImg = (ImageInfo)winner.get(0);
                Log.d("ID is", Integer.toString(winningImg.getImageID()));
                Log.d("ID is", Integer.toString(jackpotID));
                if(winningImg.getImageID() == jackpotID) pointsEarned = jackpot;
                else pointsEarned = LARGE_WIN;
                break;
            default:
                return;
        }

        if(pointsEarned > 0){
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View claimView = inflater.inflate(R.layout.claim_popup,null);
            claimPopUp = new PopupWindow(
                    claimView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );

            // requires API level 21
            if(Build.VERSION.SDK_INT>=21){
                claimPopUp.setElevation(5.0f);
            }

            Button closeButton = (Button) claimView.findViewById(R.id.claim_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runAd();
                }
            });

            TextView prizeText = (TextView) claimView.findViewById(R.id.prize_text);

            if(pointsEarned != 0){
                prizeText.setText(pointsEarned + " points");
            }
            claimPopUp.showAtLocation(cLayout, Gravity.CENTER,0,0);
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
        setSlotsImgs();
        spin = (Button) findViewById(R.id.spin);
        tokensLeft = (TextView) findViewById(R.id.tokens_available);
        totalEarned = (TextView) findViewById(R.id.current_points);
        jackpotView = findViewById(R.id.jackpot);
    }

    private void setSlotsImgs(){
        for(ImageView curView : slotImgs){
            curView.setImageBitmap(icons[1].getImage());
        }
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

    public void initTokensPointsCount() {
        try {
            String jsonResponse = presenter.restGet("getPointsInfo", getIntent().getStringExtra("id"));
            JSONObject userInfo = new JSONObject(jsonResponse);
            tokenCount = userInfo.getInt("currentTokens");
            currentPoints = userInfo.getInt("currentPoints");
            tokensLeft.setText(Integer.toString(tokenCount));
            totalEarned.setText(Integer.toString(currentPoints));

        } catch (JSONException e) {
            e.printStackTrace();
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
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        if(!rewarded) {
            Toast.makeText(this, "You must watch the video to receive points!", Toast.LENGTH_SHORT).show();
        } else {
            claimPopUp.dismiss();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem){
        presenter.sendPoints(pointsEarned, 0,0, getIntent().getStringExtra("id"));
        initTokensPointsCount();
        Toast.makeText(this, "+" + pointsEarned + " Points", Toast.LENGTH_SHORT).show();
        pointsEarned = 0;
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

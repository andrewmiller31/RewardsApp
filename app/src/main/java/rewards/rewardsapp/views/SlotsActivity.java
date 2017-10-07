package rewards.rewardsapp.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.presenters.Presenter;

public class SlotsActivity extends AppCompatActivity {

    private Presenter presenter;

    private ImageView[] slotImgs;
    private SlotReel[] reels;
    private Button spin;
    private Button autoSpin;
    private TextView resultMsg;
    private boolean done;

    private static int[] imageBank = {R.drawable.slots_cherry, R.drawable.slots_chili, R.drawable.slots_gold,
            R.drawable.slots_horseshoe, R.drawable.slots_moneybag}; //R.drawable.slots_bell

    private static final int FRAME_DURATION = 75;
    private static final int LOWER_BOUND = 150;
    private static final int UPPER_BOUND = 600;

    private static final int SMALL_WIN = 10;
    private static final int MEDIUM_WIN = 100;
    private static final int LARGE_WIN = 1000;

    TextView spinsLeft;
    TextView pointsEarned;
    private int spinCount = 100;
    private int totalPoints = 0;
    private static final int SPIN_TIME = 2200;
    private static final int AUTO_CLICK_WAIT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new Presenter(this);
        slotImgs = new ImageView[5];
        reels = Presenter.initializeReels(slotImgs);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);
        slotImgs[0] = (ImageView) findViewById(R.id.slot_1);
        slotImgs[1] = (ImageView) findViewById(R.id.slot_2);
        slotImgs[2] = (ImageView) findViewById(R.id.slot_3);
        slotImgs[3] = (ImageView) findViewById(R.id.slot_4);
        slotImgs[4] = (ImageView) findViewById(R.id.slot_5);
        spin = (Button) findViewById(R.id.spin);
        autoSpin = (Button) findViewById(R.id.auto_spin);
        resultMsg = (TextView) findViewById(R.id.win_message);
        spinsLeft = (TextView) findViewById(R.id.remaining);
        pointsEarned = (TextView) findViewById(R.id.points_won);
        spinsLeft.setText(Integer.toString(spinCount));
        pointsEarned.setText(Integer.toString(totalPoints));

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinCount > 0) {
                    resultMsg.setText("");
                    done = false;
                    spin.setText("Spinning...");
                    autoSpin.setText("Spinning...");
                    autoSpin.setClickable(false);
                    spin.setClickable(false);
                    spinCount--;
                    spinsLeft.setText(Integer.toString(spinCount));
                    reelSetup();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Presenter.stopReels(reels);
                            checkWin();
                            if(spinCount != 0){
                                spin.setClickable(true);
                                spin.setText("Spin");
                                autoSpin.setClickable(true);
                                autoSpin.setText("Auto Spin");
                            }
                            else{
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
                    }, SPIN_TIME);
                }
            }
        });

        autoSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        spin.performClick();
                        if(!done) clickAgain();
                    }
                });


            }
        });
    }

    private void clickAgain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spin.performClick();
                if(!done) clickAgain();
            }
        }, SPIN_TIME + AUTO_CLICK_WAIT);
    }


    private void reelSetup(){
        for(int i = 0; i < reels.length; i++){
            final int finalI = i;
            reels[i] = new SlotReel(new SlotReel.ReelListener() {
                @Override
                public void newIcon(final int img) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            slotImgs[finalI].setImageResource(img);
                        }
                    });
                }
            }, FRAME_DURATION, Presenter.randomLong(LOWER_BOUND, UPPER_BOUND), imageBank);

            reels[i].start();
        }
    }

    private void checkWin(){
        int winNum = Presenter.checkWin(reels, resultMsg);

        switch (winNum){
            case 0: resultMsg.setText("You lose :(");
                break;
            case 1: resultMsg.setText("Small win. +" + SMALL_WIN + " points");
                presenter.restPut("putPointsInfo", new UserInformation(SMALL_WIN, 0, false).jsonStringify());
                totalPoints += SMALL_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 2: resultMsg.setText("Medium win! +" + MEDIUM_WIN + " points");
                presenter.restPut("putPointsInfo", new UserInformation(MEDIUM_WIN, 0, false).jsonStringify());
                totalPoints += MEDIUM_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 3:
                resultMsg.setText("MAJOR PRIZE!! +" + LARGE_WIN + " points");
                presenter.restPut("putPointsInfo", new UserInformation(LARGE_WIN, 0, false).jsonStringify());
                totalPoints += LARGE_WIN;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            default: resultMsg.setText("ERROR");
                break;
        }
    }
}

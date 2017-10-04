package rewards.rewardsapp.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.presenters.Presenter;

public class SlotsActivity extends AppCompatActivity {

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

    TextView spinsLeft;
    TextView pointsEarned;
    private int spinCount = 1000;
    private int totalPoints = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                                done = true;
                            }
                        }
                    }, 2200);
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
        }, 2300);
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
            case 1: resultMsg.setText("Small win. +10 points");
                totalPoints += 10;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 2: resultMsg.setText("Medium win! +100 points");
                totalPoints += 100;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            case 3:
                resultMsg.setText("MAJOR PRIZE!! +1000 points");
                totalPoints += 1000;
                pointsEarned.setText(Integer.toString(totalPoints));
                break;
            default: resultMsg.setText("ERROR");
                break;
        }
    }
}

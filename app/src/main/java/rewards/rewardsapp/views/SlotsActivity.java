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
    private TextView resultMsg;

    private static int[] imageBank = {R.drawable.slots_cherry, R.drawable.slots_chili, R.drawable.slots_gold,
            R.drawable.slots_horseshoe, R.drawable.slots_moneybag}; //R.drawable.slots_bell

    private static final int FRAME_DURATION = 75;
    private static final int LOWER_BOUND = 150;
    private static final int UPPER_BOUND = 600;

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
        resultMsg = (TextView) findViewById(R.id.win_message);

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultMsg.setText("");
                spin.setText("Spinning...");
                spin.setClickable(false);

                reelSetup();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        Presenter.stopReels(reels);
                        Presenter.checkWin(reels, resultMsg);
                        spin.setClickable(true);
                        spin.setText("Spin");
                    }
                }, 2200);
            }
        });
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
}

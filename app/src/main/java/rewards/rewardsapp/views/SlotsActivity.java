package rewards.rewardsapp.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.SlotReel;

public class SlotsActivity extends AppCompatActivity {

    private ImageView[] slots;
    private SlotReel[] reels;
    private Button spin;
    private TextView resultMsg;

    private final int FRAME_DURATION = 75;
    private final int LOWER_BOUND = 150;
    private final int UPPER_BOUND = 600;

    public static final Random RANDOM = new Random();

    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reels = new SlotReel[5];
        slots = new ImageView[5];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots);
        slots[0] = (ImageView) findViewById(R.id.slot_1);
        slots[1] = (ImageView) findViewById(R.id.slot_2);
        slots[2] = (ImageView) findViewById(R.id.slot_3);
        slots[3] = (ImageView) findViewById(R.id.slot_4);
        slots[4] = (ImageView) findViewById(R.id.slot_5);
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
                        reels[0].stopReel();
                        reels[1].stopReel();
                        reels[2].stopReel();
                        reels[3].stopReel();
                        reels[4].stopReel();
                        List curIndexes = new ArrayList();
                        curIndexes.add(reels[0].curIndex);
                        curIndexes.add(reels[1].curIndex);
                        curIndexes.add(reels[2].curIndex);
                        curIndexes.add(reels[3].curIndex);
                        curIndexes.add(reels[4].curIndex);
                        checkWin(curIndexes);
                        spin.setClickable(true);
                        spin.setText("Spin");
                    }
                }, 2200);
            }
        });
    }

    public void checkWin(List<Integer> values){
        int matchCounter;
        int winNum = 0;
        for(int i = 0; i < values.size(); i++){
            matchCounter = 0;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))) matchCounter++;
            }
            if(matchCounter >= 2 && winNum < matchCounter - 1) winNum = matchCounter - 1;
        }

        switch (winNum){
            case 0: resultMsg.setText("You lose :(");
                break;
            case 1: resultMsg.setText("Small win.");
                break;
            case 2: resultMsg.setText("Medium win!");
                break;
            case 3:
                resultMsg.setText("MAJOR PRIZE!!");
                break;
            default: resultMsg.setText("ERROR");
                break;
        }
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
                            slots[finalI].setImageResource(img);
                        }
                    });
                }
            }, FRAME_DURATION, randomLong(LOWER_BOUND, UPPER_BOUND));

            reels[i].start();
        }
    }
}

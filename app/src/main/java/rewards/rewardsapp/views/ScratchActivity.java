package rewards.rewardsapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cooltechworks.views.ScratchImageView;
import java.util.ArrayList;
import java.util.List;
import rewards.rewardsapp.R;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity {

    private ScratchImageView scratch1;
    private ScratchImageView scratch2;
    private ScratchImageView scratch3;
    private ScratchImageView scratch4;
    private ScratchImageView scratch5;
    private ScratchImageView scratch6;
    private static int[] imageBank = {R.drawable.scratch_cow, R.drawable.scratch_dog, R.drawable.scratch_pig, R.drawable.scratch_sheep}; //, R.drawable.scratch_cat
    private TextView resultMessage;
    private boolean[] revealed;
    Presenter presenter;

    //constants
    private static final int LITTLE_WIN = 10;
    private static final int SMALL_WIN = 100;
    private static final int MEDIUM_WIN = 1000;
    private static final int LARGE_WIN = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new Presenter();
        presenter.setScratchModel(imageBank);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);
        revealed = new boolean[6];
        resultMessage = (TextView) findViewById(R.id.end_result);
        setCard();
    }

    private void endGame(){

        int winNum = presenter.checkScratchWin();

        switch (winNum){
            case 3: resultMessage.setText("Little win. +" + LITTLE_WIN + " points");
//                presenter.restPut("putPointsInfo", new UserInformation(LITTLE_WIN, 0, false).jsonStringify());
                break;
            case 4: resultMessage.setText("Small win. +" + SMALL_WIN + " points");
//                presenter.restPut("putPointsInfo", new UserInformation(SMALL_WIN, 0, false).jsonStringify());
                break;
            case 5: resultMessage.setText("Medium win! +" + MEDIUM_WIN + " points");
//                presenter.restPut("putPointsInfo", new UserInformation(MEDIUM_WIN, 0, false).jsonStringify());
                break;
            case 6:
                resultMessage.setText("MAJOR PRIZE!! +" + LARGE_WIN + " points");
//                presenter.restPut("putPointsInfo", new UserInformation(LARGE_WIN, 0, false).jsonStringify());
                break;
            default: resultMessage.setText("Loss!");
                break;
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
                if(presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });
    }
}

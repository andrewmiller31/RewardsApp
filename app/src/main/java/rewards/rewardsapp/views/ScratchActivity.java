package rewards.rewardsapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cooltechworks.views.ScratchImageView;
import java.util.ArrayList;
import java.util.List;
import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity {

    private ScratchImageView scratch1;
    private ScratchImageView scratch2;
    private ScratchImageView scratch3;
    private ScratchImageView scratch4;
    private ScratchImageView scratch5;
    private ScratchImageView scratch6;
    private TextView resultMessage;
    private List<Integer> values;
    private boolean[] revealed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);
        revealed = new boolean[6];
        values = new ArrayList<>();
        resultMessage = (TextView) findViewById(R.id.end_result);
        setCard();
    }

    private void endGame(){
        if(Presenter.win(values)) resultMessage.setText("Winner!");
        else resultMessage.setText("Better luck next time!");
    }

    private void scratchListenerSet(ScratchImageView siv, final int num) {
        siv.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                if(percent < 25) revealed[num] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });
    }

    private void setCard(){
        scratch1 = (ScratchImageView) findViewById(R.id.scratch_view1);
        scratch2 = (ScratchImageView) findViewById(R.id.scratch_view2);
        scratch3 = (ScratchImageView) findViewById(R.id.scratch_view3);
        scratch4 = (ScratchImageView) findViewById(R.id.scratch_view4);
        scratch5 = (ScratchImageView) findViewById(R.id.scratch_view5);
        scratch6 = (ScratchImageView) findViewById(R.id.scratch_view6);

        scratch1.setImageResource(Presenter.numGen(values));
        scratch2.setImageResource(Presenter.numGen(values));
        scratch3.setImageResource(Presenter.numGen(values));
        scratch4.setImageResource(Presenter.numGen(values));
        scratch5.setImageResource(Presenter.numGen(values));
        scratch6.setImageResource(Presenter.numGen(values));

        scratchListenerSet(scratch1, 0);
        scratchListenerSet(scratch2, 1);
        scratchListenerSet(scratch3, 2);
        scratchListenerSet(scratch4, 3);
        scratchListenerSet(scratch5, 4);
        scratchListenerSet(scratch6, 5);
    }
}

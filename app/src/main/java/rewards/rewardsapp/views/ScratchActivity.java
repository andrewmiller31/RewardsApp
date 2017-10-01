package rewards.rewardsapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cooltechworks.views.ScratchTextView;

import java.util.ArrayList;
import java.util.List;

import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class ScratchActivity extends AppCompatActivity {

    private ScratchTextView scratch1;
    private ScratchTextView scratch2;
    private ScratchTextView scratch3;
    private ScratchTextView scratch4;
    private ScratchTextView scratch5;
    private ScratchTextView scratch6;

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

//    private String numGen(){
//        int num = (int)(Math.random() * 4 + 1);
//        values.add(num);
//        return Integer.toString(num);
//    }
//
//    private boolean checkAllRevealed(){
//        int i = 0;
//        boolean allRevealed = true;
//        while(i < revealed.length && allRevealed == true){
//            allRevealed = revealed[i];
//            i++;
//        }
//        return allRevealed;
//    }

    private void revealAll(){
        scratch1.reveal();
        scratch2.reveal();
        scratch3.reveal();
        scratch4.reveal();
        scratch5.reveal();
        scratch6.reveal();
    }

    private void endGame(){
        revealAll();
        if(win()) resultMessage.setText("Winner!");
        else resultMessage.setText("Better luck next time!");
    }

    private boolean win(){
        boolean matchThree = false;
        int matchCounter;
        for(int i = 0; i < values.size(); i++){
            matchCounter = 0;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))) matchCounter++;
            }
            if(matchCounter >= 2) matchThree = true;
        }
        return matchThree;
    }

    private void setCard(){
        scratch1 = (ScratchTextView) findViewById(R.id.scratch_view1);
        scratch2 = (ScratchTextView) findViewById(R.id.scratch_view2);
        scratch3 = (ScratchTextView) findViewById(R.id.scratch_view3);
        scratch4 = (ScratchTextView) findViewById(R.id.scratch_view4);
        scratch5 = (ScratchTextView) findViewById(R.id.scratch_view5);
        scratch6 = (ScratchTextView) findViewById(R.id.scratch_view6);

        scratch1.setText(Presenter.numGen(values));
        scratch2.setText(Presenter.numGen(values));
        scratch3.setText(Presenter.numGen(values));
        scratch4.setText(Presenter.numGen(values));
        scratch5.setText(Presenter.numGen(values));
        scratch6.setText(Presenter.numGen(values));

        scratch1.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[0] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });

        scratch2.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[1] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });

        scratch3.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[2] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });

        scratch4.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[3] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });

        scratch5.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[4] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });

        scratch6.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView scratchTextView) {
            }

            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                if(percent < 25) revealed[5] = true;
                if(Presenter.checkAllRevealed(revealed)){
                    endGame();
                }
            }
        });
    }
}

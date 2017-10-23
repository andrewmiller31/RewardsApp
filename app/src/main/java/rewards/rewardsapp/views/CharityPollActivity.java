package rewards.rewardsapp.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import rewards.rewardsapp.R;

public class CharityPollActivity extends AppCompatActivity {
    Button sendButton;
    Toast toast;
    Boolean picked;
    RadioGroup radioGroup;
    RadioButton chosenOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toast = new Toast(this);
        picked = false;
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        sendButton = (Button) findViewById(R.id.send_vote);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId){
                picked = true;
                chosenOne = (RadioButton) findViewById(checkedId);
            }
        } );
    }

    public void sendVote(View view){
        if(!picked){
            showToast("Please choose a charity.");
        }
        else{
            showToast("Current winner: " + chosenOne.getText());
            radioGroup.setClickable(false);
            sendButton.setClickable(false);
            sendButton.setBackgroundResource(R.color.gray70);
            sendButton.setText("Thanks for voting!");
        }
    }

    public void showToast(String message){
        try{
            toast.getView().isShown();
            toast.setText(message);
        }
        catch (Exception e) {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}

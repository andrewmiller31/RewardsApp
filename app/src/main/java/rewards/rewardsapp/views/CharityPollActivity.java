package rewards.rewardsapp.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class CharityPollActivity extends AppCompatActivity {
    private Button sendButton;
    private Toast toast;
    private Boolean picked;
    private RadioGroup radioGroup;
    private RadioButton chosenOne;
    private Presenter presenter;
    private TextView voteStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        presenter = new Presenter();
        toast = new Toast(this);
        picked = false;
        voteStats = (TextView) findViewById(R.id.vote_stats);
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
            showToast("Current winner: " + vote((String)chosenOne.getText()));
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

    public String vote(String charity){
        JSONObject jsonString = null;
        try{
            jsonString = new JSONObject();
            jsonString.put("vote", charity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonResponse = presenter.restPut("putCharityVote", jsonString.toString());
        try {
            JSONObject responseInfo = new JSONObject(jsonResponse);
            String response = responseInfo.get("winner").toString();
            updateTotals();
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateTotals(){
        String jsonResponse = presenter.restGet("getVotesInfo", null);
        try {
            JSONObject responseInfo = new JSONObject(jsonResponse);
            String c1 = responseInfo.get("charity1").toString();
            String c2 = responseInfo.get("charity2").toString();
            String c3 = responseInfo.get("charity3").toString();
            String c4 = responseInfo.get("charity4").toString();
            String c5 = responseInfo.get("charity5").toString();
            String win = responseInfo.get("winning").toString();
            voteStats.setText("Votes for Charity 1: " + c1 + "\nVotes for Charity 2: " + c2 + "\nVotes for Charity 3: " + c3 + "\nVotes for Charity 4: "
            + c4 + "\nVotes for Charity 5: " + c5 + "\nCurrent winner is Charity " + win + "!");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

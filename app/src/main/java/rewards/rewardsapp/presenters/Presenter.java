package rewards.rewardsapp.presenters;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import rewards.rewardsapp.models.RedeemModel;
import rewards.rewardsapp.models.RestModel;
import rewards.rewardsapp.models.ScratchModel;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.SlotsModel;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.views.SlotsActivity;

/**
 * Created by Andrew Miller on 9/28/2017.
 */

public class Presenter {

    private RestModel restModel;
    private SlotsModel slotsModel;
    private ScratchModel scratchModel;
    private ScratchModel bonusScratchModel;
    private RedeemModel redeemModel;

    public Presenter(){
        restModel = new RestModel();
    }

    //
    // This section covers methods related to RestModel
    //
    public String restPost(String task, String data) { return restModel.restPost(task, data); }

    public String restPut(String task, String data) {
        return restModel.restPut(task, data);
    }

    public String restDelete(String task, String toDelete) { return restModel.restDelete(task, toDelete);}

    public String restGet(String task, String toGet) { return restModel.restGet(task, toGet); }

    //
    //This method is used to send earned points to the server;
    //
    public String sendPoints(int points, String id){ return restPut("putPointsInfo", new UserInformation(points, 0, false, id).jsonStringify());}

    //
    // This section covers the methods related to RedeemModel
    //
    public void setRedeemModel(int cost, RedeemModel.redeemType type, String id){redeemModel = new RedeemModel(cost, type, id);}

    public String redeemPoints(){return redeemModel.redeem();}

    //
    // This section covers methods related to ScratchModel
    //
    public void setScratchModel(String modelName, int[] imageBank, int[] winners){
        if(Objects.equals(modelName, "scratchModel")) scratchModel = new ScratchModel(imageBank, winners);
        if(modelName.equals("tokenModel")) bonusScratchModel = new ScratchModel(imageBank, winners);
    }

    public int scratchNumGen(String modelName){
        if(Objects.equals(modelName, "scratchModel")) return scratchModel.numGen();
        if(Objects.equals(modelName, "tokenModel")) return bonusScratchModel.numGen();
        return 0;
    }

    public int checkScratchWin(String modelName){
        if(Objects.equals(modelName, "scratchModel")) return scratchModel.win();
        if(modelName.equals("tokenModel")) return bonusScratchModel.win();
        return 0;
    }

    public int getScratchWinner(String modelName){
        if(Objects.equals(modelName, "scratchModel")) return scratchModel.getWinningImage();
        if(modelName.equals("tokenModel")) return bonusScratchModel.getWinningImage();
        return 0;
    }

    public boolean checkAllRevealed(boolean[] revealed){return scratchModel.checkAllRevealed(revealed);}


    //
    // This section covers methods related to SlotsModel
    //
    public void setSlotsModel(int[] imageBank, SlotReel.ReelListener[] reelListeners){ slotsModel = new SlotsModel(imageBank, reelListeners);}

    public int checkSlotsWin(){ return slotsModel.checkWin();}

    public void spinReels(){slotsModel.spinReels();}

    public void setFrameDuration(long frameDuration) {slotsModel.setFrameDuration(frameDuration);}

    public void setSpinTime(long spinTime){slotsModel.setSpinTime(spinTime);}

    public long getSpinTime(){return slotsModel.getSpinTime();}

    public void setLowerBound(long lowerBound) {slotsModel.setLowerBound(lowerBound);}

    public void setUpperBound(long upperBound) {slotsModel.setUpperBound(upperBound);}

    //
    // This section covers REST calls for verifying users
    //
    public String verifySignIn(String jsonToken) {
        return restPost("verifySignIn", jsonToken);
    }


}

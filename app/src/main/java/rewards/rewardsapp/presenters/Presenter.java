package rewards.rewardsapp.presenters;

import java.util.List;

import rewards.rewardsapp.models.RedeemModel;
import rewards.rewardsapp.models.RestModel;
import rewards.rewardsapp.models.ScratchModel;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.SlotsModel;
import rewards.rewardsapp.models.UserInformation;
import rewards.rewardsapp.models.ImageInfo;

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
    public String sendPoints(int points, int tokens, int tokensSpent, String id){ return restPut("putPointsInfo", new UserInformation(points, tokens, 0,tokensSpent, id).jsonStringify());}

    //
    // This section covers the methods related to RedeemModel
    //
    public void setRedeemModel(int cost, RedeemModel.redeemType type, String id){redeemModel = new RedeemModel(cost, type, id);}

    public String redeemPoints(){return redeemModel.redeem();}

    //
    // This section covers methods related to ScratchModel
    //
    public void setScratchModel(String modelName, ImageInfo[] imageBank){
        if(modelName.equals("scratchModel")) scratchModel = new ScratchModel(imageBank);
        if(modelName.equals("tokenModel")) bonusScratchModel = new ScratchModel(imageBank);
    }

    public int scratchNumGen(String modelName){
        if(modelName.equals("scratchModel")) return scratchModel.numGen();
        if(modelName.equals("tokenModel")) return bonusScratchModel.numGen();
        return 0;
    }

    public List<ImageInfo> getFrequencies(String modelName){
        if(modelName.equals("scratchModel")) return scratchModel.getWinners();
        if(modelName.equals("tokenModel")) return bonusScratchModel.getWinners();
        return null;
    }

    public boolean checkAllRevealed(boolean[] revealed){return scratchModel.checkAllRevealed(revealed);}


    //
    // This section covers methods related to SlotsModel
    //
    public void setSlotsModel(ImageInfo[] imageBank, SlotReel.ReelListener[] reelListeners){ slotsModel = new SlotsModel(imageBank, reelListeners);}

    public List checkSlotsWin(){ return slotsModel.checkWin();}

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

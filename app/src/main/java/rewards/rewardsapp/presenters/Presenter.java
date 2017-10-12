package rewards.rewardsapp.presenters;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rewards.rewardsapp.models.RestModel;
import rewards.rewardsapp.models.ScratchModel;
import rewards.rewardsapp.models.SlotReel;
import rewards.rewardsapp.models.SlotsModel;
import rewards.rewardsapp.views.SlotsActivity;

/**
 * Created by Andrew Miller on 9/28/2017.
 */

public class Presenter {

    private Activity activity;

    private RestModel restModel;
    private SlotsModel slotsModel;
    private ScratchModel scratchModel;

    public Presenter(){
        restModel = new RestModel();
    }

    public Presenter(Activity incomingActivity) {
        restModel = new RestModel();
        activity = incomingActivity;
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
    // This section covers methods related to ScratchModel
    //
    public void setScratchModel(int[] imageBank){scratchModel = new ScratchModel(imageBank);}

    public int scratchNumGen(){ return scratchModel.numGen();}

    public boolean checkAllRevealed(boolean[] revealed){return scratchModel.checkAllRevealed(revealed);}

    public int checkScratchWin(){ return scratchModel.win();}

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



}

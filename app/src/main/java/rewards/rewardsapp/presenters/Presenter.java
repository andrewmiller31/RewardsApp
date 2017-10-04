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

    public Presenter(Activity activity){
        this.activity = activity;
    }

    //
    // This section covers methods related to RestModel
    //
    public static String restPost(String task, String data) { return RestModel.restPost(task, data); }

    public static String restPut(String task, String data) {
        return RestModel.restPut(task, data);
    }

    public static String restDelete(String task, String toDelete) { return RestModel.restDelete(task, toDelete);}

    public static String restGet(String task, String toGet) { return RestModel.restGet(task, toGet); }

    //
    // This section covers methods related to ScratchModel
    //
    public static int numGen(List<Integer> values){ return ScratchModel.numGen(values);}

    public static boolean checkAllRevealed(boolean[] revealed){return ScratchModel.checkAllRevealed(revealed);}

    public static boolean win(List<Integer> values){ return ScratchModel.win(values);}

    //
    // This section covers methods related to SlotsModel
    //
    public static void checkWin(SlotReel[] reels, TextView resultMsg){ SlotsModel.checkWin(reels, resultMsg);}

    public static long randomLong(long lower, long upper){return SlotsModel.randomLong(lower, upper);}

    public static void stopReels(SlotReel[] reels){ SlotsModel.stopReels(reels);}

    public static SlotReel[] initializeReels(ImageView[] slotImgs){ return SlotsModel.initializeReels(slotImgs); }


}

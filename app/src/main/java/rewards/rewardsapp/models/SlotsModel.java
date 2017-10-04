package rewards.rewardsapp.models;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Andrew Miller on 10/3/2017.
 */

public class SlotsModel {

    private static Random RANDOM = new Random();

    //returns a random long
    public static long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    //counts the highest match number in a given array of reels
    public static int checkWin(SlotReel[] reels){
        List values = new ArrayList();
        for(int i = 0; i < reels.length; i++){
            values.add(reels[i].curIndex);
        }

        int matchCounter;
        int winNum = 0;
        for(int i = 0; i < values.size(); i++){
            matchCounter = 0;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))) matchCounter++;
            }
            if(matchCounter >= 2 && winNum < matchCounter - 1) winNum = matchCounter - 1;
        }
        return winNum;
    }

    //stops all reels in the given array
    public static void stopReels(SlotReel[] reels){
        for(int i = 0; i < reels.length; i++){
            reels[i].stopReel();
        }
    }

    //returns an array of reels that is the same length as the number of slots
    public static SlotReel[] initializeReels(ImageView[] slotImgs){
        return new SlotReel[slotImgs.length];
    }


}

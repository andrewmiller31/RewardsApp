package rewards.rewardsapp.models;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rewards.rewardsapp.presenters.Presenter;

/**
 * Created by Andrew Miller on 10/3/2017.
 */

public class SlotsModel {
    private static Random RANDOM = new Random();

    private long frameDuration, lowerBound, upperBound, spinTime;
    private SlotReel.ReelListener[] reelListeners;
    private static int[] imageBank;
    private SlotReel[] reels;

    public SlotsModel(int[] imageBank, SlotReel.ReelListener[] reelListeners){
        frameDuration = 75;
        lowerBound = 150;
        upperBound = 600;
        spinTime = 2200;
        this.reelListeners = reelListeners.clone();
        this.imageBank = imageBank.clone();
        reels = new SlotReel[reelListeners.length];
        reelSetup();
    }

    //getter for spinTime
    public long getSpinTime(){
        return spinTime;
    }

    //spinTime setter
    public void setSpinTime(long spinTime){
        this.spinTime = spinTime;
    }


    //counts the highest match number in a given array of reels
    public int checkWin(){
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
            if(matchCounter >= 2 && winNum < matchCounter) winNum = matchCounter + 1;
        }
        return winNum;
    }

    //spins the slot machine
    public void spinReels() {
        for (int i = 0; i < reels.length; i++) {
            reels[i].startReel();
            reels[i].start();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopReels();
            }
        }, spinTime);
    }

    //stops all reels
    private void stopReels(){
        for(int i = 0; i < reels.length; i++){
            reels[i].stopReel();
        }
    }

    //creates the reels
    private void reelSetup() {
        for (int i = 0; i < reels.length; i++) {
            reels[i] = new SlotReel(reelListeners[i], frameDuration, randomLong(lowerBound, upperBound), imageBank);
        }
    }

    //returns a random long between the bounds provided
    private long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }



}

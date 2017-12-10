package rewards.rewardsapp.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
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
    private static ImageInfo[] imageBank;
    private SlotReel[] reels;

    public SlotsModel(ImageInfo[] imageBank, SlotReel.ReelListener[] reelListeners){
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

    //setter for frame duration
    public void setFrameDuration(long frameDuration) {
        this.frameDuration = frameDuration;
    }

    //setter for lower bound of random number
    public void setLowerBound(long lowerBound) {
        this.lowerBound = lowerBound;
    }

    //setter for upper bound of random number
    public void setUpperBound(long upperBound) {
        this.upperBound = upperBound;
    }

    //counts the highest match number in a given array of reels
    public List checkWin(){
        List<ImageInfo> values = new ArrayList();
        for(int i = 0; i < reels.length; i++){
            values.add(imageBank[reels[i].getCurIndex()]);
        }

        int matchCounter;
        int winNum = 0;

        List winner = new ArrayList();
        winner.add(null);
        winner.add(null);

        for(int i = 0; i < values.size(); i++){
            matchCounter = 1;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).getImageID() == (values.get(j).getImageID())) matchCounter++;
            }
            if(winNum < matchCounter){
                winNum = matchCounter;
                winner.set(0, values.get(i));
                winner.set(1, winNum);
            }
        }
        return winner;
    }

    //spins the slot machine
    public void spinReels() {
        reelSetup();
        for (int i = 0; i < reels.length; i++) {
            reels[i].startReel(randomLong(lowerBound, upperBound));
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
            reels[i] = new SlotReel(reelListeners[i], frameDuration, imageBank);
        }
    }

    //returns a random long between the bounds provided
    private long randomLong(long lower, long upper) {
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }



}

package rewards.rewardsapp.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    private  ImageInfo[] imageBank;
    private List<ImageInfo> frequencies;
    private List<Integer> values;

    /**
     * Ctor for ScratchModel
     * @param imageBank the array of images to randomly place
     */
    public ScratchModel(ImageInfo[] imageBank){
        this.imageBank = imageBank.clone();
        frequencies = new LinkedList<>();
        values = new ArrayList<>();
    }

    //randomly chooses a picture and adds to values for comparing later
    //should only be used when setting images on the scratch card
    public int numGen(){
        int num = (int)(Math.random() * imageBank.length);
        values.add(num);
        if(imageBank[num].isWinner()) {
            frequencies.add(imageBank[num]);
        }
        return imageBank[num].getImageID();
    }


    public List<ImageInfo> getFrequencies(){
        return frequencies;
    }

    //checks if all of the array of boolean values are true
    public boolean checkAllRevealed(boolean[] revealed){
        int i = 0;
        boolean allRevealed = true;
        while(i < revealed.length && allRevealed){
            allRevealed = revealed[i];
            i++;
        }
        return allRevealed;
    }
}

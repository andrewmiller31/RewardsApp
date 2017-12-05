package rewards.rewardsapp.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    private  int[] imageBank;
    private int[] frequencies; //parallel to imageBank
    private List<Integer> values;

    /**
     * Ctor for ScratchModel
     * @param imageBank the array of images to randomly place
     */
    public ScratchModel(int[] imageBank){
        this.imageBank = imageBank.clone();
        frequenciesInit();
        values = new ArrayList<>();
    }

    //sets the frequency of each image to 0
    public void frequenciesInit(){
        frequencies = new int[imageBank.length];
        for(int i = 0; i < imageBank.length; i++){
            frequencies[i] = 0;
        }
    }

    //randomly chooses a picture and adds to values for comparing later
    //should only be used when setting images on the scratch card
    public int numGen(){
        int num = (int)(Math.random() * imageBank.length);
        values.add(num);
        frequencies[num]++;
        return imageBank[num];
    }

    //checks how many times a given image was chosen
    public int checkFrequency(int image){
        int freq = 0;
        for(int i = 0; i < imageBank.length; i++){
            if(imageBank[i] == image) freq += frequencies[i];
        }
        return freq;
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

package rewards.rewardsapp.models;

import java.util.ArrayList;
import java.util.List;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    private static int[] imageBank;
    private List<Integer> values;

    public ScratchModel(int[] imageBank){
        this.imageBank = imageBank.clone();
        values = new ArrayList<>();
    }

    //randomly chooses a picture and adds to values for comparing later
    //should only be used when setting images on the scratch card
    public int numGen(){
        int num = (int)(Math.random() * imageBank.length);
        values.add(num);
        return imageBank[num];
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

    //returns the number of matches there are
    public int win(){
        int matchCounter;
        int winNum = 0;
        for(int i = 0; i < values.size(); i++){
            matchCounter = 0;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))) matchCounter++;
            }
            if(matchCounter >= 1 && winNum < matchCounter + 1) winNum = matchCounter + 1;
        }
        return winNum;
    }
}

package rewards.rewardsapp.models;

import java.util.ArrayList;
import java.util.List;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    private  int[] imageBank;
    private int[] winners;
    private List<Integer> values;
    private int winningImage;

    /**
     * Ctor for ScratchModel
     * @param imageBank the array of images to randomly place
     * @param winners array of possible winners (usually will just contain one element)
     */
    public ScratchModel(int[] imageBank, int[] winners){
        this.imageBank = imageBank.clone();
        this.winners = winners.clone();
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
        int size = values.size();
        for(int i = 0; i < size; i++) {
            if (isWinner(values.get(i))) {
                matchCounter = 1;
                for (int j = i + 1; j < size; j++) {
                    if (values.get(i).equals(values.get(j))) matchCounter++;
                }
                if (winNum < matchCounter){
                    winNum = matchCounter;
                    winningImage = imageBank[i];
                }
            }
        }
        return winNum;
    }

    public int getWinningImage(){
        return winningImage;
    }

    private boolean isWinner(int checkValue){
        for (int winner : winners) {
            if (checkValue == winner) return true;
        }
        return false;
    }
}

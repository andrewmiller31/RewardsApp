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
    private int[] frequencies;
    private List<ImageInfo> winners;
    private List<Integer> potentialWinners;
    private List<Integer> indexBank;

    /**
     * Ctor for ScratchModel
     * @param imageBank the array of images to randomly place
     */
    public ScratchModel(ImageInfo[] imageBank){
        this.imageBank = imageBank.clone();
        winners = new LinkedList<>();
        potentialWinners = new LinkedList<>();
        frequencies = new int[imageBank.length];
        initIdBank();
    }

    //randomly chooses a picture and adds to values for comparing later
    //should only be used when setting images on the scratch card
    public int numGen(){
        int num = (int)(Math.random() * indexBank.size());
        ImageInfo chosenImageInfo = imageBank[indexBank.get(num)];
        if(chosenImageInfo.isWinner()) {
            int index = potentialWinners.indexOf(chosenImageInfo.getImageID());
            if(chosenImageInfo.getAmountNeeded() == 1) winners.add(chosenImageInfo);
            else if(index != -1){
                frequencies[index]++;
                if(frequencies[index] >= chosenImageInfo.getAmountNeeded()){
                    frequencies[index] = 0;
                    winners.add(chosenImageInfo);
                }
            }
            else {
                potentialWinners.add(chosenImageInfo.getImageID());
                frequencies[potentialWinners.indexOf(chosenImageInfo.getImageID())] = 1;
            }
        }
        return indexBank.get(num);
    }


    public List<ImageInfo> getWinners(){
        return winners;
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

    private void initIdBank(){
        indexBank = new ArrayList<>();
        for(int i = 0; i < imageBank.length; i++){
            int j = imageBank[i].getWeight();
            if(j > 1){
                while (j > 0){
                    indexBank.add(i);
                    j--;
                }
            }
            else indexBank.add(i);
        }
    }
}

package rewards.rewardsapp.models;

import java.util.List;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    public static int numGen(List<Integer> values){
        int image;
        int num = (int)(Math.random() * 5 + 1);
        values.add(num);
        switch (num){
            case 1: image = R.drawable.scratch_cow;
                break;
            case 2: image = R.drawable.scratch_dog;
                break;
            case 3: image = R.drawable.scratch_pig;
                break;
            case 4: image = R.drawable.scratch_sheep;
                break;
            case 5: image = R.drawable.scratch_cat;
                break;
            default: image = 0;
                break;
        }
        return image;
    }

    public static boolean checkAllRevealed(boolean[] revealed){
        int i = 0;
        boolean allRevealed = true;
        while(i < revealed.length && allRevealed){
            allRevealed = revealed[i];
            i++;
        }
        return allRevealed;
    }

    public static boolean win(List<Integer> values){
        boolean matchThree = false;
        int matchCounter;
        for(int i = 0; i < values.size(); i++){
            matchCounter = 0;
            for(int j = i + 1; j < values.size(); j++){
                if(values.get(i).equals(values.get(j))) matchCounter++;
            }
            if(matchCounter >= 2) matchThree = true;
        }
        return matchThree;
    }
}

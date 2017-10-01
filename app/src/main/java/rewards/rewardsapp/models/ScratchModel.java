package rewards.rewardsapp.models;

import java.util.List;

/**
 * Created by Andrew Miller on 10/1/2017.
 */

public class ScratchModel {

    public static String numGen(List<Integer> values){
        int num = (int)(Math.random() * 4 + 1);
        values.add(num);
        return Integer.toString(num);
    }

    public static boolean checkAllRevealed(boolean[] revealed){
        int i = 0;
        boolean allRevealed = true;
        while(i < revealed.length && allRevealed == true){
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
                if(values.get(i) == values.get(j)) matchCounter++;
            }
            if(matchCounter >= 2) matchThree = true;
        }
        return matchThree;
    }
}

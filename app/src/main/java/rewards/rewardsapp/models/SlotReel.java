package rewards.rewardsapp.models;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/3/2017.
 */

public class SlotReel extends Thread{

    public interface ReelListener {
        void newIcon(Bitmap img);
    }

    private static ImageInfo[] images;
    private List<Integer> imageIndex;
    private int curIndex;
    private int curIndexFind;
    private ReelListener reelListener;
    private long duration;
    private long startTime;
    private boolean isStarted;

    public SlotReel(ReelListener reelListener, long duration, ImageInfo[] imageInfos) {
        this.reelListener = reelListener;
        this.duration = duration;
        this.images = imageInfos.clone();
        curIndex = 0;
        curIndexFind = 0;
        isStarted = true;
        images = imageInfos;
        imageIndexSetUp();
    }

    private void imageIndexSetUp(){
        imageIndex = new LinkedList();
        for(int i = 0; i < images.length; i++){
            int weight = images[i].getWeight();
            if(weight > 1){
                while (weight > 0){
                    imageIndex.add(i);
                    weight--;
                }
            }
            else{
                imageIndex.add(i);
            }
        }
    }

    //keeps track of the index for the images array
    private void nextImg() {
        curIndex = imageIndex.get(curIndexFind++);
        if (curIndexFind == imageIndex.size()) {
            curIndexFind = 0;
        }
    }

    //method used when start() is called, spins the reel
    @Override
    public void run() {
        try {
            Thread.sleep(startTime);
        } catch (InterruptedException e) {
        }

        while (isStarted) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
            }

            nextImg();

            if (reelListener != null) {
                reelListener.newIcon(images[imageIndex.get(curIndex)].getImage());
            }
        }
    }

    //stops a spinning reel
    public void stopReel() {
        isStarted = false;
    }

    //starts the reel
    public void startReel(long startTime) {
        this.startTime = startTime;
        isStarted = true;
        start();
    }

    public int getCurIndex() {
        return curIndex;
    }

}
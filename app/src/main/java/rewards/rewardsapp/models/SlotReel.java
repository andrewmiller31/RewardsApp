package rewards.rewardsapp.models;

import rewards.rewardsapp.R;

/**
 * Created by Andrew Miller on 10/3/2017.
 */

public class SlotReel extends Thread{

    public interface ReelListener {
        void newIcon(int img);
    }

    private static int[] images;
    public int curIndex;
    private ReelListener reelListener;
    private long duration;
    private long startTime;
    private boolean isStarted;

    public SlotReel(ReelListener reelListener, long duration, int[] images) {
        this.reelListener = reelListener;
        this.duration = duration;
        this.images = images.clone();
        curIndex = 0;
        isStarted = true;
    }

    //keeps track of the index for the images array
    private void nextImg() {
        ++curIndex;
        if (curIndex == images.length) {
            curIndex = 0;
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
                reelListener.newIcon(images[curIndex]);
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

}
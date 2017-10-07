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

    public SlotReel(ReelListener reelListener, long duration, long startTime, int[] images) {
        this.reelListener = reelListener;
        this.duration = duration;
        this.startTime = startTime;
        this.images = images.clone();
        curIndex = 0;
        isStarted = true;
    }

    private void nextImg() {
        ++curIndex;

        if (curIndex == images.length) {
            curIndex = 0;
        }
    }

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

    public void stopReel() {
        isStarted = false;
    }

    public void setBank(int[] images){

    }

}
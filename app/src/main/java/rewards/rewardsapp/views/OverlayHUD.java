package rewards.rewardsapp.views;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Andrew Miller on 10/24/2017.
 */

public class OverlayHUD extends Service{
    private Button placeholderButton;
    public static boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        placeholderButton = new Button(this);
        placeholderButton.setText("Passive Earning");

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER | Gravity.TOP;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(placeholderButton, params);
        isRunning = true;
    }


    @Override
    public IBinder
    onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(placeholderButton != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(placeholderButton);
            placeholderButton = null;
        }
    }

}

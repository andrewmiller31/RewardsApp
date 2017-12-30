package rewards.rewardsapp.views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by Andrew Miller on 10/24/2017.
 */

public class OverlayEarnActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent overlay = new Intent(this, OverlayHUD.class);
        overlay.putExtra("id", getIntent().getStringExtra("id"));
        if(!OverlayHUD.isIsRunning()) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                } else {
                    Toast.makeText(this, "Surf & Earn enabled. You can now use your phone freely!", Toast.LENGTH_SHORT).show();
                    startService(overlay);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.google.com"));
                    startActivity(browserIntent);
                }
            }

            finish();
        }
        else{
            OverlayHUD.stopRunning();
            stopService(overlay);
            finish();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

package rewards.rewardsapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew Miller on 12/11/2017.
 */

public class GameInfo {
    private String type;
    private String title;
    private String winMessage;
    private Bitmap background;
    private String cost;
    private String id;

    public GameInfo(JSONObject gameObject){
        try{
            type = gameObject.getString("type");
            title = gameObject.getString("title");
            background = decodeImage(gameObject.getString("background"));
            cost = gameObject.getString("cost");
            winMessage = gameObject.getString("winMessage");
            id = gameObject.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeImage(String image){
        byte[] bitmapData = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getWinMessage() {
        return winMessage;
    }

    public Bitmap getBackground() {
        return background;
    }

    public String getCost() {
        return cost;
    }

    public String getId() {
        return id;
    }

}

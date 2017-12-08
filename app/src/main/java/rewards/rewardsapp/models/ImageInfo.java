package rewards.rewardsapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Andrew Miller on 12/7/2017.
 */

public class ImageInfo {

    boolean winner;
    String type;
    int imageID;
    int reward;
    Bitmap image;

    public ImageInfo(boolean winner, String type, int imageID, int reward, Bitmap image){
        this.winner = winner;
        this.type = type;
        this.imageID = imageID;
        this.reward = reward;
        this.image = image;
    }

    public ImageInfo(int imageID, Bitmap image){
        this.imageID = imageID;
        this.image = image;
        type = "n/a";
        reward = 0;
        winner = false;
    }

    public ImageInfo(JSONObject winnerInfo){
        String imageString;
        try {
            winner = winnerInfo.getBoolean("winner");
            type = winnerInfo.getString("rewardType");
            reward = winnerInfo.getInt("reward");
            imageString = winnerInfo.get("image").toString();
            byte[] bitmapData = Base64.decode(imageString, Base64.NO_WRAP);
            image = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
            imageID = winnerInfo.getInt("imageID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONObject(){
        String imageString;
        JSONObject jsonString = null;
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bitStream);
        imageString = Base64.encodeToString(bitStream.toByteArray(), Base64.NO_WRAP);
        try {
            jsonString = new JSONObject();
            jsonString.put("rewardType", type);
            jsonString.put("reward", reward);
            jsonString.put("winner", winner);
            jsonString.put("imageID", imageID);
            jsonString.put("image", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public boolean isWinner() {
        return winner;
    }

    public String getType() {
        return type;
    }

    public int getImageID() {
        return imageID;
    }

    public int getReward() {
        return reward;
    }

    public Bitmap getImage() {
        return image;
    }


}

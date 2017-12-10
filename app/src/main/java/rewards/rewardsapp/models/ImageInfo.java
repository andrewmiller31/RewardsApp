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

    private boolean winner;
    private String type;
    private int imageID;
    private int reward;
    private Bitmap image;
    private int weight;
    private int amountNeeded;

    public ImageInfo(boolean winner, String type, int imageID, int reward, Bitmap image, int amountNeeded){
        this.winner = winner;
        this.type = type;
        this.imageID = imageID;
        this.reward = reward;
        this.image = image;
        weight = 1;
        this.amountNeeded = amountNeeded;
    }

    public ImageInfo(int imageID, Bitmap image){
        this.imageID = imageID;
        this.image = image;
        type = "n/a";
        reward = 0;
        winner = false;
        amountNeeded = 1;
        weight = 1;
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
            amountNeeded = winnerInfo.getInt("amountNeeded");
            weight = winnerInfo.getInt("weight");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSONObject(){
        String imageString;
        JSONObject jsonString = null;
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bitStream);
        imageString = Base64.encodeToString(bitStream.toByteArray(), Base64.NO_WRAP);
        try {
            jsonString = new JSONObject();
            jsonString.put("rewardType", type);
            jsonString.put("reward", reward);
            jsonString.put("winner", winner);
            jsonString.put("imageID", imageID);
            jsonString.put("image", imageString);
            jsonString.put("weight", weight);
            jsonString.put("amountNeeded", amountNeeded);
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

    public int getWeight() {
        return weight;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


}

package rewards.rewardsapp.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Andrew Miller on 12/7/2017.
 */

public class SlotsInformation {
    private String title;
    private Bitmap background;
    private ImageInfo[] icons;
    private int jackpot;
    private int jackpotImageID;
    private int cost;

    public SlotsInformation(String title, Bitmap background, ImageInfo[] icons, int cost, int jackpot, int imageID){
        this.title = title;
        this.background = background;
        this.icons = icons.clone();
        this.cost = cost;
        this.jackpot = jackpot;
        this.jackpotImageID = imageID;
    }

    public SlotsInformation(JSONObject scratchInfo){
        try{
            title = scratchInfo.getString("title");
            background = decodeImage(scratchInfo.getString("background"));
            icons = jsonArrayToWinnerArray(scratchInfo.getJSONArray("icons"));
            cost = scratchInfo.getInt("cost");
            jackpot = scratchInfo.getInt("jackpot");
            jackpotImageID = scratchInfo.getInt("jackpotID");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getBackground() {
        return background;
    }

    public ImageInfo[] getIcons() {
        return icons;
    }

    public int getJackpot() {
        return jackpot;
    }

    public int getCost() {
        return cost;
    }

    public int getJackpotImageID() {
        return jackpotImageID;
    }

    public String jsonStringify(){
        JSONObject jsonString = null;
        try {
            jsonString = new JSONObject();
            jsonString.put("title", title);
            jsonString.put("background", encodeImage(background));
            jsonString.put("icons", jsonWinners(icons));
            jsonString.put("jackpot", jackpot);
            jsonString.put("cost", cost);
            jsonString.put("jackpotID", jackpotImageID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    private ImageInfo[] jsonArrayToWinnerArray(JSONArray jsonArray){
        ImageInfo[] imageInfoArray = new ImageInfo[jsonArray.length()];
        try {
            for (int i = 0; i < imageInfoArray.length; i++){
                imageInfoArray[i] = new ImageInfo(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageInfoArray;
    }

    private Bitmap decodeImage(String image){
        byte[] bitmapData = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
    }

    private String encodeImage(Bitmap image){
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bitStream);
        return Base64.encodeToString(bitStream.toByteArray(), Base64.NO_WRAP);
    }

    private JSONArray jsonWinners(ImageInfo[] imageInfoArray){
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < imageInfoArray.length; i++){
            jsonArray.put(imageInfoArray[i].getJSONObject());
        }
        return jsonArray;
    }


}

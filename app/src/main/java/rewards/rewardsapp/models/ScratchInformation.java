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

public class ScratchInformation {
    private String title;
    private String winMessage;
    private Bitmap background;
    private ImageInfo[] icons;
    private ImageInfo[] bonusIcons;

    public ScratchInformation(String title, Bitmap background, ImageInfo[] icons, ImageInfo[] bonusIcons){
        this.title = title;
        this.background = background;
        this.icons = icons.clone();
        this.bonusIcons = bonusIcons.clone();
        winMessage = "Win points/tokens!";
    }

    public ScratchInformation(JSONObject scratchInfo){
        try{
            title = scratchInfo.getString("title");
            background = decodeImage(scratchInfo.getString("background"));
            icons = jsonArrayToWinnerArray(scratchInfo.getJSONArray("icons"));
            bonusIcons = jsonArrayToWinnerArray(scratchInfo.getJSONArray("bonusIcons"));
            winMessage = scratchInfo.getString("winMessage");
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

    public ImageInfo[] getBonusIcons() {
        return bonusIcons;
    }

    public String getWinMessage() {
        return winMessage;
    }

    public void setWinMessage(String winMessage) {
        this.winMessage = winMessage;
    }

    public String jsonStringify(){
        JSONObject jsonString = null;
        try {
            jsonString = new JSONObject();
            jsonString.put("title", title);
            jsonString.put("background", encodeImage(background));
            jsonString.put("winMessage", winMessage);
            jsonString.put("icons", jsonWinners(icons));
            jsonString.put("bonusIcons", jsonWinners(bonusIcons));

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

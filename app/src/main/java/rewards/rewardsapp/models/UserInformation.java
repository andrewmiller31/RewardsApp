package rewards.rewardsapp.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew Miller on 9/28/2017.
 *
 * This class controls user related information.
 */

public class UserInformation {
    private int pointsSpent;
    private int pointsEarned;
    private boolean rankUp;
    private String id;

    public UserInformation(int pointsToAdd, int pointsSpent, boolean rankUp, String id){
        this.pointsEarned = pointsToAdd;
        this.pointsSpent = pointsSpent;
        this.rankUp = rankUp;
        this.id = id;
    }

    /**
     * Creates a JSON object containing the contents of this class and stringifies it.
     *
     * @return jsonString string of JSON object
     */
    public String jsonStringify(){
        JSONObject jsonString = null;
        try{
            jsonString = new JSONObject();
            jsonString.put("pointsEarned", pointsEarned);
            jsonString.put("pointsSpent", pointsSpent);
            jsonString.put("rankUp", rankUp);
            jsonString.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG:", jsonString.toString());
        return jsonString.toString();
    }
}

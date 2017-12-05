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
    private int tokensEarned;
    private int tokensSpent;
    private String id;

    public UserInformation(int pointsToAdd, int tokensToAdd, int pointsSpent, int tokensSpent, String id){
        this.pointsEarned = pointsToAdd;
        this.tokensEarned = tokensToAdd;
        this.pointsSpent = pointsSpent;
        this.tokensSpent = tokensSpent;
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
            jsonString.put("tokensEarned", tokensEarned);
            jsonString.put("pointsSpent", pointsSpent);
            jsonString.put("tokensSpent", tokensSpent);
            jsonString.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG:", jsonString.toString());
        return jsonString.toString();
    }
}

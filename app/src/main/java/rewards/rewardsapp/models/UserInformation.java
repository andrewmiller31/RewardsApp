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
    static private String id;
    static private String name;
    static private String email;
    static private String charity;
    private int points;
    private int totalPointsEarned;
    private int rank;

    /**
     * Constructor for UserInformation.
     * @param userInfo be sure to list in correct order.
     * @param pointsInfo be sure to list in correct order.
     */
    public UserInformation(String[] userInfo, int[] pointsInfo){
        id = userInfo[0];
        name = userInfo[1];
        email = userInfo[2];
        charity = userInfo[3];

        points = pointsInfo[0];
        totalPointsEarned = pointsInfo[1];
        rank = pointsInfo[2];
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
            jsonString.put("id", id);
            jsonString.put("name", name);
            jsonString.put("email", email);
            jsonString.put("charity", charity);
            jsonString.put("points", points);
            jsonString.put("totalPointsEarned", totalPointsEarned);
            jsonString.put("rank", rank);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("DEBUG:", jsonString.toString());
        return jsonString.toString();
    }
}

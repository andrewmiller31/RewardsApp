package rewards.rewardsapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.presenters.Presenter;

/**
 * Created by Andrew on 10/16/2017.
 */

public class RedeemModel {
    private Presenter presenter;
    private int cost;
    private redeemType type;
    private String id;

//    private String title;
//    private String info;
    public static enum redeemType{
        cash,
        card,
        sweepstakes
    }

    public RedeemModel(int cost, redeemType type, String id){
        presenter = new Presenter();
        this.cost = cost;
        this.type = type;
        this.id = id;
    }

    public String redeem(){
        String jsonResponse = presenter.restPut("putPointsInfo",  new UserInformation(0, 0, cost, 0, id).jsonStringify());
        try {
            JSONObject responseInfo = new JSONObject(jsonResponse);
            String response = responseInfo.get("id").toString();
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCost() {
        return cost;
    }

    public redeemType getType() {
        return type;
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public String getInfo() {
//        return info;
//    }

}

package rewards.rewardsapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.presenters.Presenter;

/**
 * Created by Andrew on 10/16/2017.
 */

public class RedeemInformation {
    private Presenter presenter;
    private int cost;
    private String id;
    private String type;
    private String spendType;
    private String title;
    private String info;

    public RedeemInformation(int cost, String type, String title, String id){
        presenter = new Presenter();
        this.cost = cost;
        this.type = type;
        this.title = title;
        this.id = id;
        spendType = "points";
        info = Integer.toString(cost) + " " + type;
    }

    public RedeemInformation(int cost, String type, String title, String info, String id){
        presenter = new Presenter();
        this.cost = cost;
        this.type = type;
        this.title = title;
        this.id = id;
        this.info = info;
        spendType = "points";
    }

    public RedeemInformation(int cost, String type, String spendType, String title, String info, String id){
        presenter = new Presenter();
        this.cost = cost;
        this.type = type;
        this.title = title;
        this.info = info;
        this.id = id;
        this.spendType = spendType;
    }

    public String redeem(){
        String jsonResponse = null;
        if(spendType.equals("points")) {
            jsonResponse = presenter.restPut("putPointsInfo", new UserInformation(0, 0, cost, 0, id).jsonStringify());
        } else if(spendType.equals("tokens")){
            jsonResponse = presenter.restPut("putPointsInfo", new UserInformation(0, 0, 0, cost, id).jsonStringify());
        }
        if(jsonResponse != null) {
            try {
                JSONObject responseInfo = new JSONObject(jsonResponse);
                String response = responseInfo.get("id").toString();
                return response;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public String getSpendType() {
        return spendType;
    }


}

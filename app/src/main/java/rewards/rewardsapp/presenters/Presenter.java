package rewards.rewardsapp.presenters;

import android.app.Activity;

import rewards.rewardsapp.models.RestModel;

/**
 * Created by Andrew Miller on 9/28/2017.
 */

public class Presenter {
    private Activity activity;
    private RestModel restModel;

    public Presenter(Activity activity){
        restModel = new RestModel();
        this.activity = activity;
    }

    /**
     * @param task to be performed
     * @param data data to be posted
     * @return response from server
     */
    public String restPost(String task, String data) {
        return restModel.restPost(task, data);
    }

    /**
     * @param task to be performed
     * @param data data to be put
     * @return response from server
     */
    public String restPut(String task, String data) {
        return restModel.restPut(task, data);
    }

    /**
     * @param task     to be performed
     * @param toDelete what will be deleted
     * @return response from server
     */
    public String restDelete(String task, String toDelete) { return restModel.restDelete(task, toDelete);}

    /**
     * @param task  to be performed
     * @param toGet data to get
     * @return response from server
     */
    public String restGet(String task, String toGet) {
        return restModel.restGet(task, toGet);
    }
}

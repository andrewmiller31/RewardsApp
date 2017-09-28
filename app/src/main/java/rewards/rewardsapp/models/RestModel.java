package rewards.rewardsapp.models;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Miller on 9/28/2017.
 *
 * This model controls server interactions using REST calls
 */

public class RestModel {

    public RestModel(){}

    /**
     * @param postString name of task
     * @param data JSON data to post
     */
    public String restPost(String postString, String data){
        switch (postString){
            case "": return null;
            default: return null;
        }
    }

    /**
     * @param postString name of task
     * @param data JSON data to put
     */
    public String restPut(String postString, String data){
        switch (postString){
            case "": return null;
            default: return null;
        }
    }

    /**
     * @param postString name of task
     * @param data JSON data to get
     */
    public String restGet(String postString, String data){
        switch (postString){
            case "": return null;
            default: return null;
        }
    }

    /**
     * @param postString name of task
     * @param data JSON data to delete
     */
    public String restDelete(String postString, String data){
        switch (postString){
            case "": return null;
            default: return null;
        }
    }

    private class HTTPAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection serverConnection = null;
            InputStream is;

            Log.d("Debug:", "Attempting to connect to: " + params[0]);

            try {
                URL url = new URL(params[0]);
                serverConnection = (HttpURLConnection) url.openConnection();
                serverConnection.setRequestMethod(params[1]);

                if (params[1].equals("PUT")) {
                    if(params[1].equals("PUT")) {
                        Log.d("DEBUG PUT:", "In put: params[0]=" + params[0] + ", params[1]=" + params[1] + ", params[2]=" + params[2]);
                        serverConnection.setDoOutput(true);
                        serverConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        serverConnection.setRequestProperty("Content-Length", "" +
                                Integer.toString(params[2].getBytes().length));
                        DataOutputStream out = new DataOutputStream(serverConnection.getOutputStream());
                        out.writeBytes(params[2]);
                        out.flush();
                        out.close();
                    }
                }

                int responseCode = serverConnection.getResponseCode();
                Log.d("Debug:", "\nSending " + params[1] + " request to URL : " + params[0]);
                Log.d("Debug: ", "Response Code : " + responseCode);
                is = serverConnection.getInputStream();

                if (params[1].equals("GET")) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    try {
                        JSONObject jsonData = new JSONObject(sb.toString());
                        return jsonData.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                assert serverConnection != null;
                serverConnection.disconnect();
            }
            return "Should not get to this line!";
        }
        protected void onPostExecute(String result) {
            Log.d("onPostExecute JSON: ", result);
        }
    }
}
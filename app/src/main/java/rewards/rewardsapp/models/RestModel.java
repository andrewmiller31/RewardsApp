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
import java.util.concurrent.ExecutionException;

/**
 * Created by Miller on 9/28/2017.
 *
 * This model controls server interactions using REST calls
 */

public class RestModel {
    private final String serverAddress = "http://10.0.2.2:5000"; //Emulator Tunnel
//    private final String serverAddress = "http://akka.d.umn.edu:5000";

    public RestModel(){}

    /**
     * @param postString name of task
     * @param data JSON data to post
     */
    public String restPost(String postString, String data){
        switch (postString){
            case "verifySignIn": return verifySignIn(data);
            default:
                Log.d("NO ROUTE FOR: ", postString);
                return null;
        }
    }

    /**
     * @param putString name of task
     * @param data JSON data to put
     */
    public String restPut(String putString, String data){
        switch (putString){
            case "putPointsInfo": return putPointsInfo(data);
            case "putCharityVote": return putCharityInfo(data);
            case "scratch": return putScratchInfo(data);
            case "slots": return putSlotsInfo(data);
            default:
                Log.d("NO ROUTE FOR: ", putString);
                return null;
        }
    }

    /**
     * @param getString name of task
     * @param data JSON data to get
     */
    public String restGet(String getString, String data){
        switch (getString){
            case "getPointsInfo": return getPointsInfo(data);
            case "getVotesInfo": return getVotesInfo();
            case "scratchIDs": return getScratchIDs();
            case "slotsIDs": return getSlotsIDs();
            case "scratchCard": return getScratchCardInfo(data);
            case "slotsCard": return getSlotsCardInfo(data);
            case "slotsInfo": return getSlotsGameInfo(data);
            case "scratchInfo": return getScratchGameInfo(data);
            case "slotsJackpot": return getSlotsJackpot(data);
            default:
                Log.d("NO ROUTE FOR: ", getString);
                return null;
        }
    }

    /**
     * @param deleteString name of task
     * @param data JSON data to delete
     */
    public String restDelete(String deleteString, String data){
        switch (deleteString){
            case "": return null;
            default:
                Log.d("NO ROUTE FOR: ", deleteString);
                return null;
        }
    }

    private String putScratchInfo(String data){
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/scratch", "PUT", data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String putSlotsInfo(String data){
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/slots", "PUT", data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSlotsJackpot(String data){
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/slotsJackpot/" + data, "GET").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getScratchIDs(){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/scratchGameIDs/", "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSlotsIDs(){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/slotsGameIDs/", "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSlotsCardInfo(String id){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/slots/card/" + id, "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getScratchCardInfo(String id){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/scratch/card/" + id, "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getScratchGameInfo(String id){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/scratch/" + id, "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getSlotsGameInfo(String id){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/slots/" + id, "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String putPointsInfo(String data) {
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/pointsInfo", "PUT", data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String putCharityInfo(String data) {
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/charityVotes", "PUT", data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPointsInfo(String id){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/users/" + id, "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getVotesInfo(){
        try{
            return new HTTPAsyncTask().execute(serverAddress + "/charityVotes", "GET").get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String verifySignIn(String data){
        try {
            return new HTTPAsyncTask().execute(serverAddress + "/verifySignIn", "POST", data).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
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


                if (params[1].equals("PUT") || params[1].equals("POST")) {
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

                int responseCode = serverConnection.getResponseCode();
                Log.d("Debug:", "\nSending " + params[1] + " request to URL : " + params[0]);
                Log.d("Debug: ", "Response Code : " + responseCode);
                is = serverConnection.getInputStream();

                if (params[1].equals("GET") || params[1].equals("PUT") || params[1].equals("POST")) {
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
            return "Should not reach this point!";
        }
        protected void onPostExecute(String result) {
            Log.d("onPostExecute JSON: ", result);
        }
    }
}

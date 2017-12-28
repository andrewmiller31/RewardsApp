package rewards.rewardsapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class SplashScreen extends AppCompatActivity {
    private Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new Presenter();
        final Intent intent = new Intent(this, LoginActivity.class);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject1 = new JSONObject(presenter.restGet("scratchIDs", null));
                    JSONObject jsonObject2 = new JSONObject(presenter.restGet("slotsIDs", null));
                    String[] scratchArray = jsonArrayToStringArray(jsonObject1.getJSONArray("idArray"));
                    String[] slotsArray = jsonArrayToStringArray(jsonObject2.getJSONArray("idArray"));
                    intent.putExtra("scratchArray", scratchArray);
                    intent.putExtra("slotsArray", slotsArray);
                    findGames("scratch", scratchArray);
                    findGames("slots", slotsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
            }
        });
    }

    private void findGames(String type, String[] array){
        for (String id : array) {
            String cardName = id + "CARD";
            String gameName = id;
            String fileData = readFile(cardName);
            String gameData = readFile(gameName);
            if (fileData.equals("")) {
                Log.d("FILE NOT FOUND", "Card file not found with ID: " + id);
                String gameString = presenter.restGet(type + "Card", id);
                writeFile(cardName, gameString);
            }
            if (gameData.equals("")) {
                Log.d("FILE NOT FOUND", "Game file not found with ID: " + id);
                String gameString = presenter.restGet(type + "Info", id);
                writeFile(cardName, gameString);
            }
        }
    }

    private String readFile(String fileName){
        try {
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private boolean writeFile(String fileName, String data){
        FileOutputStream outputStream;
        try{
            outputStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String[] jsonArrayToStringArray(JSONArray jsonArray){
        String[] stringArray = new String[jsonArray.length()];
        try {
            for (int i = 0; i < stringArray.length; i++){
                stringArray[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringArray;
    }

}

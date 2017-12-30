package rewards.rewardsapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import rewards.rewardsapp.models.ImageInfo;
import rewards.rewardsapp.models.ScratchInformation;
import rewards.rewardsapp.models.SlotsInformation;
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
            String fileData = readFile(cardName);
            if (fileData.equals("")) {
                Log.d("FILE NOT FOUND", "Card file not found with ID: " + id);
                String gameString = presenter.restGet(type + "Card", id);
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
            if(readFile(fileName).equals("")){
                Log.d("ERROR", "failed to write file." + fileName);
                return false;
            }
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

    private void setGameTests(){
        ImageInfo imageInfo1 = new ImageInfo(true, "points", 1001, 100, intToBM(R.drawable.scratch_dog), 3);
        ImageInfo imageInfo2 = new ImageInfo(true, "tokens", 1002, 1, intToBM(R.drawable.scratch_one_token), 1);
        ImageInfo[] icons = {imageInfo1, imageInfo2, new ImageInfo(1, intToBM(R.drawable.scratch_cow)),
                new ImageInfo(2, intToBM(R.drawable.scratch_pig)),new ImageInfo(3, intToBM(R.drawable.scratch_sheep))};

        ImageInfo imageInfo5 = new ImageInfo(true, "tokens", 1002, 1, intToBM(R.drawable.scratch_one_token), 1);
        ImageInfo imageInfo3 = new ImageInfo(true, "tokens", 1003, 5, intToBM(R.drawable.scratch_token_pile), 1);
        ImageInfo imageInfo4 = new ImageInfo(true, "tokens", 1004, 10, intToBM(R.drawable.scratch_token_jackpot), 1);
        ImageInfo loser = new ImageInfo(999, intToBM(R.drawable.scratch_lose));

        imageInfo5.setWeight(3);
        imageInfo3.setWeight(3);
        loser.setWeight(3);
        ImageInfo[] bonusIcons = {imageInfo5, imageInfo3, imageInfo4, loser};
        ScratchInformation si = new ScratchInformation("scratch1", "Farm Frenzy", BitmapFactory.decodeResource(getResources(), R.drawable.background_field), icons, bonusIcons);
        si.setWinMessage("Win 100 points!");
        presenter.restPut("scratch", si.jsonStringify());

        ImageInfo slots1 = new ImageInfo(true, "points", 1001, 5, intToBM(R.drawable.slots_cherry), 3);
        ImageInfo slots2 = new ImageInfo(true, "points", 1002, 10000, intToBM(R.drawable.slots_chili), 5);
        ImageInfo slots3 = new ImageInfo(true, "tokens", 1003, 1, intToBM(R.drawable.scratch_one_token), 1);
        ImageInfo slots4 = new ImageInfo(true, "points", 1004, 3, intToBM(R.drawable.slots_horseshoe), 3);
        ImageInfo slots5 = new ImageInfo(true, "points", 1005, 10, intToBM(R.drawable.slots_moneybag), 3);

        ImageInfo[] slotImages = {slots1, slots2, slots3, slots4, slots5};
        SlotsInformation si2 = new SlotsInformation("slots1", "Hot Jackpot", intToBM(R.drawable.background_peppers), slotImages, 2, 10000, 1002);
        si2.setWinMessage("Win 10,000+ points!");
        presenter.restPut("slots", si2.jsonStringify());

        ImageInfo pirate1 = new ImageInfo(true, "points", 1001, 3, intToBM(R.drawable.game_pirate_boat), 3);
        ImageInfo pirate2 = new ImageInfo(true, "points", 1002, 5, intToBM(R.drawable.game_pirate_jolly), 3);
        ImageInfo pirate3 = new ImageInfo(1003, intToBM(R.drawable.game_pirate_kraken));
        ImageInfo pirate4 = new ImageInfo(1004, intToBM(R.drawable.game_pirate_shark));
        ImageInfo pirate5 = new ImageInfo(true, "points", 1005, 3, intToBM(R.drawable.game_pirate_ship), 3);
        ImageInfo pirate6 = new ImageInfo(true, "points", 1006, 3, intToBM(R.drawable.game_pirate_wheel), 3);
        ImageInfo pirate7 = new ImageInfo(true, "points", 1007, 5000, intToBM(R.drawable.game_pirate_treasure), 5);
        ImageInfo pirate8 = new ImageInfo(true, "tokens", 1008, 5, intToBM(R.drawable.scratch_one_token), 1);

        ImageInfo[] pirateImages = {pirate1, pirate2, pirate3, pirate4, pirate5, pirate6, pirate7, pirate8};
        SlotsInformation pirateSlots = new SlotsInformation("pirateSlots", "Pirate Treasure", intToBM(R.drawable.game_background_pirate), pirateImages, 3, 5000, 1007);
        pirateSlots.setWinMessage("Win 5,000+ points!");
        presenter.restPut("slots", pirateSlots.jsonStringify());
    }

    private Bitmap intToBM(int images){
        return BitmapFactory.decodeResource(getResources(), images);
    }


}

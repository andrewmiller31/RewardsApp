package rewards.rewardsapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.GameInfo;
import rewards.rewardsapp.models.RedeemInformation;
import rewards.rewardsapp.presenters.Presenter;

/**
 * Created by Andrew Miller on 12/11/2017.
 */

public class RedeemInfoAdapter extends RecyclerView.Adapter<RedeemInfoAdapter.ViewHolder>{
    private ArrayList<RedeemInformation> redeemData;
    private RecyclerView recyclerView;
    private Context context;
    private String userId;

    /**
     *
     * @param redeemData ArrayList of posts
     * @param recyclerView this view
     */
    public RedeemInfoAdapter(ArrayList<RedeemInformation> redeemData, RecyclerView recyclerView, Context context, String userId) {
        this.redeemData = redeemData;
        this.recyclerView = recyclerView;
        this.context = context;
        this.userId = userId;
    }

    /**
     * Holder for each game card
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cView) {
            super(cView);
            cardView = cView;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.redeem_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(cView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RedeemInformation curRedeemInfo = redeemData.get(position);
        TextView title = holder.cardView.findViewById(R.id.title);
        TextView cost = holder.cardView.findViewById(R.id.cost_info);
        ImageView icon = holder.cardView.findViewById(R.id.type_icon);

        title.setText(curRedeemInfo.getTitle());
        cost.setText(curRedeemInfo.getInfo());
        if(curRedeemInfo.getType().equals("sweepstakes")){
            icon.setImageResource(R.drawable.redeem_sweepstakes);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String result = curRedeemInfo.redeem();
                if(result.equals("444")){
                    Toast.makeText(context, "Insufficient Points/Tokens.", Toast.LENGTH_SHORT).show();
                } else if(curRedeemInfo.getType().equals("sweepstakes")){
                    Toast.makeText(context, "Contest entered.", Toast.LENGTH_SHORT).show();
                    updateUI();
                } else if(curRedeemInfo.getType().equals("card")){
                    Toast.makeText(context, "Gift card purchased.", Toast.LENGTH_SHORT).show();
                    updateUI();
                }
            }
        });
    }

    private void updateUI(){
        TextView progressView = ((Activity) context).findViewById(R.id.progress);
        TextView rank = ((Activity) context).findViewById(R.id.rank);
        TextView totalPoints = ((Activity) context).findViewById(R.id.points_total);
        TextView totalSpent = ((Activity) context).findViewById(R.id.points_spent);
        TextView currentPoints = ((Activity) context).findViewById(R.id.points_current);
        TextView currentTokens = ((Activity) context).findViewById(R.id.tokens_current);
        TextView redeemCurPoints = ((Activity) context).findViewById(R.id.points_available);
        TextView redeemCurTokens = ((Activity) context).findViewById(R.id.tokens_available);

        if(progressView != null) {
            try {
                Presenter presenter = new Presenter();
                String jsonResponse = presenter.restGet("getPointsInfo", userId);
                JSONObject userInfo = new JSONObject(jsonResponse);
                redeemCurPoints.setText(userInfo.get("currentPoints").toString());
                redeemCurTokens.setText(userInfo.get("currentTokens").toString());
                currentTokens.setText(userInfo.get("currentTokens").toString());
                progressView.setText(userInfo.get("totalEarned").toString() + "/" + userInfo.get("newRank").toString());
                rank.setText(userInfo.get("rank").toString());
                currentPoints.setText(userInfo.get("currentPoints").toString());
                totalPoints.setText(userInfo.get("totalEarned").toString());
                totalSpent.setText(userInfo.get("totalSpent").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return redeemData.size();
    }
}

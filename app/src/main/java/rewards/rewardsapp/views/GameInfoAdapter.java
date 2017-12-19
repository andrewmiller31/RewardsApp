package rewards.rewardsapp.views;

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

import java.util.ArrayList;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.GameInfo;

/**
 * Created by Andrew Miller on 12/11/2017.
 */

public class GameInfoAdapter extends RecyclerView.Adapter<GameInfoAdapter.ViewHolder>{
    private ArrayList<GameInfo> gameData;
    private RecyclerView recyclerView;
    private Context context;
    private String userId;

    /**
     *
     * @param gameData ArrayList of posts
     * @param recyclerView this view
     */
    public GameInfoAdapter(ArrayList<GameInfo> gameData, RecyclerView recyclerView, Context context, String userId) {
        this.gameData = gameData;
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
        CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(cView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GameInfo curGameInfo = gameData.get(position);
        TextView title = holder.cardView.findViewById(R.id.title);
        ImageView background = holder.cardView.findViewById(R.id.background);
        TextView winMessage = holder.cardView.findViewById(R.id.win_message);
        TextView cost = holder.cardView.findViewById(R.id.cost_amount);

        title.setText(curGameInfo.getTitle());
        winMessage.setText(curGameInfo.getWinMessage());
        cost.setText(curGameInfo.getCost());
        background.setImageBitmap(curGameInfo.getBackground());

        final String gameType = curGameInfo.getType();

        if(gameType.equals("scratch")){
            TextView type = holder.cardView.findViewById(R.id.game_type2);
            TextView otherType = holder.cardView.findViewById(R.id.game_type1);
            type.setVisibility(View.VISIBLE);
            otherType.setVisibility(View.GONE);
        } else if(curGameInfo.getType().equals("slots")){
            TextView type = holder.cardView.findViewById(R.id.game_type1);
            TextView otherType = holder.cardView.findViewById(R.id.game_type2);
            type.setVisibility(View.VISIBLE);
            otherType.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (gameType.equals("scratch") && !OverlayHUD.isIsRunning()) {
                    Intent intent = new Intent(context, ScratchActivity.class);
                    intent.putExtra("id", userId);
                    //runAd();
                    context.startActivity(intent);
                } else if (gameType.equals("slots") && !OverlayHUD.isIsRunning()) {
                    Intent intent = new Intent(context, SlotsActivity.class);
                    intent.putExtra("id", userId);
                    //runAd();
                    context.startActivity(intent);
                } else Toast.makeText(context, "Cannot play games while using Surf & Earn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameData.size();
    }
}

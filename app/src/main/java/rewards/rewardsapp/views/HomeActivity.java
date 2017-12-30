package rewards.rewardsapp.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.RedeemInformation;
import rewards.rewardsapp.models.ScratchInformation;
import rewards.rewardsapp.models.ImageInfo;
import rewards.rewardsapp.models.SlotsInformation;
import rewards.rewardsapp.presenters.Presenter;

public class HomeActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private Presenter presenter;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toast toast;
    private Button closeAd;
    private PopupWindow charityPopUp;
    private AdView mAdView;
    private Intent earnIntent;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean adRewarded;
    private GoogleApiClient mGoogleApiClient;
    private String id;

    private RadioGroup radioGroup;
    private RadioButton chosenOne;
    private boolean picked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new Presenter();
        id = getIntent().getStringExtra("id");
        super.onCreate(savedInstanceState);
        toast = new Toast(this);
        viewSetup();
        adSetup();
    }

    //ad setup
    private void adSetup(){
        mAdView = findViewById(R.id.adView);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loadRewardedVideoAd();
    }

    //view setup
    private void viewSetup(){
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Rewards App");
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        closeAd = findViewById(R.id.close_ad);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    //options menu setup

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.log_out:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                                Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                                Intent loginIntent =new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        });
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    //onClick Methods

    public void redeemCash(View view){
        RedeemInformation cashRedeem = new RedeemInformation(100, "cash", "cash redeem", id);
        String result = cashRedeem.redeem();
        if(result.equals("444")){
            showToast("Insufficient Points.");
        }
        else {
            showToast("$1 redeemed.");
        }
    }

    public void charityPoll(View view){
        picked = false;
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View voteView = inflater.inflate(R.layout.vote_popup,(ViewGroup)findViewById(R.id.vote_popup));
        radioGroup = voteView.findViewById(R.id.radio_group);

        charityPopUp = new PopupWindow(
                voteView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            charityPopUp.setElevation(5.0f);
        }

        Typeface face1 = Typeface.createFromAsset(getAssets(),"fonts/bree_serif.ttf");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId){
                picked = true;
                chosenOne = voteView.findViewById(checkedId);
            }
        } );

        Button voteButton = voteView.findViewById(R.id.send_vote);
        voteButton.setTypeface(face1);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!picked){
                    showToast("Please choose a charity.");
                } else {
                    showToast("Current winner: " + vote((String) chosenOne.getText()));
                    charityPopUp.dismiss();
                }
            }
        });

        Button closeButton = voteView.findViewById(R.id.exit);
        closeButton.setTypeface(face1);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                charityPopUp.dismiss();
            }
        });

        TextView instructions = voteView.findViewById(R.id.instructions);
        instructions.setTypeface(face1);

        charityPopUp.showAtLocation(voteView.findViewById(R.id.vote_popup), Gravity.CENTER, 0, 0);
    }

    public String vote(String charity){
        JSONObject jsonString = null;
        try{
            jsonString = new JSONObject();
            jsonString.put("vote", charity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonResponse = presenter.restPut("putCharityVote", jsonString.toString());
        try {
            JSONObject responseInfo = new JSONObject(jsonResponse);
            return responseInfo.get("winner").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void passiveEarn(View view) {
        if(!OverlayHUD.isReturningFromAd()) {
            Intent newIntent = new Intent(this, OverlayEarnActivity.class);
            newIntent.putExtra("id", id);
            startActivity(newIntent);
            if(OverlayHUD.isIsRunning()) {
                OverlayHUD.setReturningFromAd(false);
                showToast("Surf & Earn disabled.");
            }
        }
    }

    //loads the rewarded video
    //NOTE: only call after previous ad has completely finished its life. Otherwise there will be an interference with the next ad loading properly.
    private void loadRewardedVideoAd() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    //closes ad banner
    public void closeAd(View view){
        mAdView.setClickable(false);
        mAdView.setVisibility(View.GONE);
        mAdView.pause();
        closeAd.setClickable(false);
        closeAd.setVisibility(View.GONE);
    }

    //opens ad banner
    public void showAd(){
        mAdView.setClickable(true);
        mAdView.setVisibility(View.VISIBLE);
        mAdView.resume();
        closeAd.setClickable(true);
        closeAd.setVisibility(View.VISIBLE);

    }

    //shows toast of message
    public void showToast(String message){
        try{
            toast.getView().isShown();
            toast.setText(message);
        }
        catch (Exception e) {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

//    refreshes account info
    public void refreshAccountInfo() {
        TextView progressView = findViewById(R.id.progress);
        TextView rank = findViewById(R.id.rank);
        TextView totalPoints = findViewById(R.id.points_total);
        TextView totalSpent = findViewById(R.id.points_spent);
        TextView currentPoints = findViewById(R.id.points_current);
        TextView currentTokens = findViewById(R.id.tokens_current);
        TextView redeemCurPoints = findViewById(R.id.points_available);
        TextView redeemCurTokens = findViewById(R.id.tokens_available);

        if(progressView != null) {
            try {
                String jsonResponse = presenter.restGet("getPointsInfo", getIntent().getStringExtra("id"));
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

    //Checks if ad is loaded until it is loaded and then runs ad
    private void runAd(){
        final Handler handler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    handler.removeCallbacksAndMessages(this);
                    return;
                }
                handler.postDelayed(this, 50);
            }
        };
        handler.post(task);
    }

    //find and set up fragments used
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Bundle extras;
        public PlaceholderFragment() {
        }

        @SuppressLint("ValidFragment")
        public PlaceholderFragment(Bundle extras) {
            this.extras = extras;
        }

        public static PlaceholderFragment newInstance(int sectionNumber, Bundle extras) {
            PlaceholderFragment fragment = new PlaceholderFragment(extras);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_earn, container, false);
                setEarnPage(rootView);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                rootView = inflater.inflate(R.layout.fragment_redeem, container, false);
                setRedeemPage(rootView);
            }
            else {
                rootView = inflater.inflate(R.layout.fragment_account, container, false);
                setAccountPage(rootView);
            }
            return rootView;
        }

        private void setEarnPage(View rootView) {
            RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(rootView.getContext(), 2);
            RecyclerView recyclerView1 = rootView.findViewById(R.id.recycler_one);
            recyclerView1.setLayoutManager(layoutManager1);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            width = width - dpToPx(300);
            width = width / 3;
            Log.d("Calculated Spacing:", Integer.toString(width));

            recyclerView1.addItemDecoration(new GridDecoration(2, width));
            recyclerView1.setAdapter(new GameInfoAdapter(extras.getStringArray("slotsArray"), extras.getStringArray("scratchArray"), rootView.getContext(), extras.getString("id")));
        }

        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        }

        private void setRedeemPage(View rootView){
            TextView currentPoints = rootView.findViewById(R.id.points_available);
            TextView currentTokens = rootView.findViewById(R.id.tokens_available);
            currentPoints.setText(extras.getString("currentPoints"));
            currentTokens.setText(extras.getString("currentTokens"));
            String id = extras.getString("id");
            ArrayList<RedeemInformation> redeemData = new ArrayList<>();
            redeemData.add(new RedeemInformation(1000, "card", "$10 Amazon Gift Card", "1,000 points", id));
            redeemData.add(new RedeemInformation(10, "sweepstakes", "tokens", "Chance to win $10,000!", "10 tokens", id));
            redeemData.add(new RedeemInformation(10000, "card", "$100 Amazon Gift Card", "10,000 points", id));
            redeemData.add(new RedeemInformation(1, "sweepstakes", "tokens", "Chance to win $10!", "1 tokens", id));
            redeemData.add(new RedeemInformation(3, "sweepstakes", "tokens", "Chance to win $1,000!", "3 tokens", id));
            redeemData.add(new RedeemInformation(10000, "card", "$100 Best Buy Gift Card", "10,000 points", id));
            redeemData.add(new RedeemInformation(10000, "card", "$100 Walmart Gift Card", "10,000 points", id));
            redeemData.add(new RedeemInformation(5, "sweepstakes", "tokens", "Chance to win a trip to Florida!", "5 tokens", id));

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(rootView.getContext(), 1);
            RecyclerView recyclerView = rootView.findViewById(R.id.redeem_recycler);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new GridDecoration(1, dpToPx(20)));

            recyclerView.setAdapter(new RedeemInfoAdapter(redeemData, recyclerView, rootView.getContext(), extras.getString("id")));
        }

        private void setAccountPage(View rootView) {
            TextView progressView = rootView.findViewById(R.id.progress);
            TextView rank = rootView.findViewById(R.id.rank);
            TextView currentPoints = rootView.findViewById(R.id.points_current);
            TextView totalPoints = rootView.findViewById(R.id.points_total);
            TextView totalSpent = rootView.findViewById(R.id.points_spent);
            TextView name = rootView.findViewById(R.id.name);
            TextView email =  rootView.findViewById(R.id.email);
            TextView tokens = rootView.findViewById(R.id.tokens_current);

            if (extras != null) {
                name.setText(extras.getString("name"));
                email.setText(extras.getString("email"));
                progressView.setText(extras.getString("totalEarned") + "/" + extras.getString("newRank"));
                rank.setText(extras.getString("rank"));
                currentPoints.setText(extras.getString("currentPoints"));
                totalPoints.setText(extras.getString("totalEarned"));
                totalSpent.setText(extras.getString("totalSpent"));
                tokens.setText(extras.getString("currentTokens"));
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle extras = getIntent().getExtras();
            Fragment test = PlaceholderFragment.newInstance(position + 1, extras);
            test.setUserVisibleHint(true);
            return test;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Earn";
                case 1:
                    return "Redeem";
                case 2:
                    return "Account";
            }
            return null;
        }
    }

    //life cycle methods

    @Override
    public void onResume()
    {
        super.onResume();
        if(OverlayHUD.isReturningFromAd()) {
            onBackPressed();
//            OverlayHUD.setReturningFromAd(false);
        }
        refreshAccountInfo();
        showAd();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAdView.destroy();
        mRewardedVideoAd.destroy(this);
//        if(!OverlayHUD.isIsRunning()) startActivity(new Intent(this, OverlayEarnActivity.class));
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    //RewardedAdListener methods

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        if(adRewarded) {
            startActivity(earnIntent);
            adRewarded = false;
        }
        else {
            loadRewardedVideoAd();
            showToast("You must watch the entire ad to proceed.");
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        adRewarded = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

}



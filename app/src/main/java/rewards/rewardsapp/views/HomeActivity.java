package rewards.rewardsapp.views;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.R;
import rewards.rewardsapp.models.RedeemModel;
import rewards.rewardsapp.presenters.Presenter;

public class HomeActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private Presenter presenter;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toast toast;
    private Button closeAd;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private Intent earnIntent;
    private boolean adRewarded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new Presenter();
        toast = new Toast(this);
        viewSetup();
        adSetup();
    }

    //ad setup
    private void adSetup(){
        mAdView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
    }

    //view setup
    private void viewSetup(){
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Rewards App");
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        closeAd = (Button) findViewById(R.id.close_ad);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
        return super.onOptionsItemSelected(item);
    }

    //onClick Methods

    public void cashClick(View view){
        presenter.setRedeemModel(1000, RedeemModel.redeemType.cash);
        String result = presenter.redeemPoints();
        if(result.equals("333")){
            showToast("Insufficient Points.");
        }
        else {
            showToast("Congrats! You earned $1.");
            refreshAccountInfo();
        }
    }

    public void sweepClick(View view){
        presenter.setRedeemModel(50, RedeemModel.redeemType.cash);
        String result = presenter.redeemPoints();
        if(result.equals("333")){
            showToast("Insufficient Points.");
        }
        else {
            showToast("Congrats! You have entered the contest.");
            refreshAccountInfo();
        }
    }

    public void giftClick(View view){
        presenter.setRedeemModel(10000, RedeemModel.redeemType.cash);
        String result = presenter.redeemPoints();
        if(result.equals("333")){
            showToast("Insufficient Points.");
        }
        else {
            showToast("Congrats! You earned a $10 card.");
            refreshAccountInfo();
        }
    }

    public void scratchOff(View view) {
        if(!OverlayHUD.isReturningFromAd()) {
            earnIntent = new Intent(this, ScratchActivity.class);
            if(mRewardedVideoAd.isLoaded()){
                mRewardedVideoAd.show();
            }
            else startActivity(earnIntent);
        }
    }

    public void slots(View view){
        if(!OverlayHUD.isReturningFromAd()) {
            earnIntent = new Intent(this, SlotsActivity.class);
            mRewardedVideoAd.show();
        }
    }

    public void charityPoll(View view){ startActivity(new Intent(this, CharityPollActivity.class));}

    public void passiveEarn(View view) {
        if(!OverlayHUD.isReturningFromAd()) {
            startActivity(new Intent(this, OverlayEarnActivity.class));
            if (!OverlayHUD.isIsRunning()) {
                showToast("Surf & Earn enabled. You can now use your phone freely!");
            } else if (OverlayHUD.isIsRunning()) {
                showToast("Surf & Earn disabled.");
            }
        }
    }

    //loads the rewarded video
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
    }

    //restarts ad upon resume
    private void restartAd(){
        mRewardedVideoAd.destroy(this);
        mRewardedVideoAd = null;
        RewardedVideoAd newAd;
        newAd = MobileAds.getRewardedVideoAdInstance(this);
        newAd.setRewardedVideoAdListener(this);
        newAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        mRewardedVideoAd = newAd;
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

    //refreshes account info
    public void refreshAccountInfo() {
        TextView progressView = (TextView) findViewById(R.id.progress);
        TextView rank = (TextView) findViewById(R.id.rank_view);
        TextView currentPoints = (TextView) findViewById(R.id.points_current);
        TextView totalPoints = (TextView) findViewById(R.id.points_total);
        TextView totalSpent = (TextView) findViewById(R.id.points_spent);
        TextView redeemCurPoints = (TextView) findViewById(R.id.redeem_cur_points);

        if(progressView != null) {
            try {
                String jsonResponse = presenter.restGet("getPointsInfo", null);
                JSONObject pointsInfo = new JSONObject(jsonResponse);
                redeemCurPoints.setText("Current points: " + pointsInfo.get("currentPoints").toString());
                progressView.setText(pointsInfo.get("totalEarned").toString() + "/" + pointsInfo.get("newRank").toString());
                rank.setText(pointsInfo.get("rank").toString());
                currentPoints.setText(pointsInfo.get("currentPoints").toString());
                totalPoints.setText(pointsInfo.get("totalEarned").toString());
                totalSpent.setText(pointsInfo.get("totalSpent").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //life cycle methods

    @Override
    public void onResume()
    {
        super.onResume();
        restartAd();
        if(OverlayHUD.isReturningFromAd()) {
            onBackPressed();
            OverlayHUD.setReturningFromAd(false);
        }
        refreshAccountInfo();
        showAd();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mAdView.destroy();
        mRewardedVideoAd.destroy(this);
        if(!OverlayHUD.isIsRunning()) startActivity(new Intent(this, OverlayEarnActivity.class));
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
        else showToast("You must watch the entire ad to proceed.");
        loadRewardedVideoAd();
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
        startActivity(earnIntent);
    }

    //find and set up fragments used

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private Presenter presenter;
        private String jsonResponse;

        public PlaceholderFragment() {
            presenter = new Presenter();
            jsonResponse = presenter.restGet("getPointsInfo", null);
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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

        public void setRedeemPage(View rootView){
            TextView currentPoints = (TextView) rootView.findViewById(R.id.redeem_cur_points);
            try {
                JSONObject pointsInfo = new JSONObject(jsonResponse);
                currentPoints.setText("Current points: " + pointsInfo.get("currentPoints").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void setAccountPage(View rootView) {
            TextView progressView = (TextView) rootView.findViewById(R.id.progress);
            TextView rank = (TextView) rootView.findViewById(R.id.rank_view);
            TextView currentPoints = (TextView) rootView.findViewById(R.id.points_current);
            TextView totalPoints = (TextView) rootView.findViewById(R.id.points_total);
            TextView totalSpent = (TextView) rootView.findViewById(R.id.points_spent);

            try {
                JSONObject pointsInfo = new JSONObject(jsonResponse);
                progressView.setText(pointsInfo.get("totalEarned").toString() + "/" + pointsInfo.get("newRank").toString());
                rank.setText(pointsInfo.get("rank").toString());
                currentPoints.setText(pointsInfo.get("currentPoints").toString());
                totalPoints.setText(pointsInfo.get("totalEarned").toString());
                totalSpent.setText(pointsInfo.get("totalSpent").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment test = PlaceholderFragment.newInstance(position + 1);
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
                    return "My Account";
            }
            return null;
        }
    }

}

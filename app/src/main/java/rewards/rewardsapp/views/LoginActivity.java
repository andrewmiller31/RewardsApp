package rewards.rewardsapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import rewards.rewardsapp.R;
import rewards.rewardsapp.presenters.Presenter;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    Presenter presenter;
    private String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new Presenter();
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        signInButton.setVisibility(View.GONE);
        signInInitialize();
    }

    private void signInInitialize(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 5000);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    private void updateUI(GoogleSignInAccount account){
        if(account != null){
            idToken = account.getIdToken();
            JSONObject jsonString;
            try {
                jsonString = new JSONObject();
                jsonString.put("token", idToken);
                String jsonResponse = presenter.verifySignIn(jsonString.toString());
                JSONObject userInfo = new JSONObject(jsonResponse);
                if(getIntent().getStringArrayExtra("scratchArray") != null && getIntent().getStringArrayExtra("slotsArray") != null) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("id", userInfo.get("id").toString());
                    intent.putExtra("currentPoints", userInfo.get("currentPoints").toString());
                    intent.putExtra("currentTokens", userInfo.get("currentTokens").toString());
                    intent.putExtra("totalEarned", userInfo.get("totalEarned").toString());
                    intent.putExtra("totalSpent", userInfo.get("totalSpent").toString());
                    intent.putExtra("rank", userInfo.get("rank").toString());
                    intent.putExtra("newRank", userInfo.get("newRank").toString());
                    intent.putExtra("email", account.getEmail());
                    intent.putExtra("name", account.getDisplayName());
                    startActivity(intent);
                    finish();
                } else{
                    startActivity(new Intent(this, SplashScreen.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("LoginActivity", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            updateUI(account);
        } else {
            Log.d("LoginActivity", "signInResult:failed");
            updateUI(null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("LoginActivity", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(getIntent().getBooleanExtra("signOff", false)){
            signOut();
        }
        else {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                Log.d("LoginActivity", "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                signInButton.setVisibility(View.VISIBLE);
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5000) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(OverlayHUD.isReturningFromAd()) {
            onBackPressed();
            OverlayHUD.setReturningFromAd(false);
        }
    }

}

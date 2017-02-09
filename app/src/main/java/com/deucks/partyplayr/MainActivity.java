package com.deucks.partyplayr;

import android.animation.ArgbEvaluator;

import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Class for the main Activity/MainScreen handles facebook login
 */
public class MainActivity extends AppCompatActivity {

    //Declare Variables
    private PreferenceManager mPrefManager;
    private RelativeLayout mMainLayout;
    private CallbackManager mCallbackManager;
    private AccessToken mAccessToken;
    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    private LoginButton mLoginButton;
    private Button mCreatePartyButton;
    private Button mJoinPartyButton;
    private TextView mActivTitle;

    private final ScheduledExecutorService changeBackgroundExec = Executors.newScheduledThreadPool(1);
    private int mBackgroundCount = 0;

    /**
     * Called when the MainActivity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_main);

        //Initilize variables
        mPrefManager = new PreferenceManager(this);

        mMainLayout = (RelativeLayout) findViewById(R.id.activ_main_content);
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mCreatePartyButton = (Button) findViewById(R.id.create_party_button);
        mJoinPartyButton = (Button) findViewById(R.id.main_join_party_button);
        mActivTitle = (TextView) findViewById(R.id.activ_main_party_started);
        changeStatusBarColor();

        //Check what state the user is currently in
        if (mPrefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

        //Start background color change
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeBackgroundColor(mBackgroundCount++);
                handler.postDelayed(this, 10000);
            }
        }, 7000);


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");

        //Track Facebooks tokens, check if logged in
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                //Track new/old tokens
                if (currentAccessToken == null) {
                    hidePartyButtons();
                } else {
                    showPartyButtons();
                }

            }
        };
        // If the access token is available already assign it.
        mAccessToken = AccessToken.getCurrentAccessToken();

        if (mAccessToken == null) {
            hidePartyButtons();
        } else {
            showPartyButtons();
        }

        mAccessTokenTracker.startTracking();

        //Do Login, check if logged in or not
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    private ProfileTracker mProfileTracker;

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (Profile.getCurrentProfile() == null) {
                            mProfileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    //Set the user as logged in with new token
                                    mPrefManager.setFBUserSessionVariables(true,
                                            profile2.getFirstName(),
                                            profile2.getLastName(),
                                            profile2.getName(),
                                            profile2.getId(),
                                            profile2.getProfilePictureUri(150, 150).toString()
                                    );
                                    mProfileTracker.stopTracking();
                                    showPartyButtons();
                                    Log.d("user id ", profile2.getId());
                                }
                            };

                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            mPrefManager.setFBUserSessionVariables(true,
                                    profile.getFirstName(),
                                    profile.getLastName(),
                                    profile.getName(),
                                    profile.getId(),
                                    profile.getProfilePictureUri(150, 150).toString()
                            );
                            showPartyButtons();
                            Log.v("facebook - profile", profile.getFirstName());
                        }
                    }

                    @Override
                    public void onCancel() {
                        // When canceled, log user out (just in case)
                        mPrefManager.setFBUserSessionVariables(false,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY
                        );
                        hidePartyButtons();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // When an error occurs, log user out
                        mPrefManager.setFBUserSessionVariables(false,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY,
                                UniversalConstants.EMPTY
                        );
                        hidePartyButtons();
                    }
                }
        );


    }


    /**
     * When the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        //stop the token tracking
        super.onDestroy();
        mAccessTokenTracker.stopTracking();
    }

    /**
     * When a result is given to the activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handler for create Party Button
     * @param view
     */
    public void createParty(View view) {
        startActivity(new Intent(this, CreateParty.class));
    }

    /**
     * Handler for join party button
     * @param view
     */
    public void joinParty(View view) {
        startActivity(new Intent(this, JoinParty.class));
    }

    /**
     * Method for hiding party Buttons
     */
    private void hidePartyButtons() {
        mActivTitle.setText(getString(R.string.get_the_party_started));
        mCreatePartyButton.setVisibility(View.GONE);
        mJoinPartyButton.setVisibility(View.GONE);
    }

    /**
     * Method for showing party buttons
     */
    private void showPartyButtons() {
        //Also set the title for main activity, appropriately
        mActivTitle.setText(
                String.format("%s%s%s",
                        mPrefManager.getGeneralString(UniversalConstants.PREF_FB_FIRST_NAME),
                        getString(R.string.comma_seperation),
                        getString(R.string.get_the_party_started))
        );
        mCreatePartyButton.setVisibility(View.VISIBLE);
        mJoinPartyButton.setVisibility(View.VISIBLE);
    }



    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }

    /**
     * Method for changing background color
     *
     * @param backgroundCount
     */
    private void changeBackgroundColor(int backgroundCount) {
        int defaultTransValue = 5000;
        //Count the background and update the color appropriately
        if (backgroundCount == 0) {
            animateBetweenColors(mMainLayout, getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), defaultTransValue);
            animateBetweenColors(mLoginButton, getResources().getColor(R.color.dot_light_screen1), getResources().getColor(R.color.dot_light_screen2), defaultTransValue);
            animateBetweenColors(mCreatePartyButton, getResources().getColor(R.color.dot_light_screen1), getResources().getColor(R.color.dot_light_screen2), defaultTransValue);
            animateBetweenColors(mJoinPartyButton, getResources().getColor(R.color.dot_light_screen1), getResources().getColor(R.color.dot_light_screen2), defaultTransValue);

        } else if (backgroundCount == 1) {
            animateBetweenColors(mMainLayout, getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3), defaultTransValue);
            animateBetweenColors(mLoginButton, getResources().getColor(R.color.dot_light_screen2), getResources().getColor(R.color.dot_light_screen3), defaultTransValue);
            animateBetweenColors(mCreatePartyButton, getResources().getColor(R.color.dot_light_screen2), getResources().getColor(R.color.dot_light_screen3), defaultTransValue);
            animateBetweenColors(mJoinPartyButton, getResources().getColor(R.color.dot_light_screen2), getResources().getColor(R.color.dot_light_screen3), defaultTransValue);

        } else if (backgroundCount == 2) {
            animateBetweenColors(mMainLayout, getResources().getColor(R.color.bg_screen3), getResources().getColor(R.color.bg_screen4), defaultTransValue);
            animateBetweenColors(mLoginButton, getResources().getColor(R.color.dot_light_screen3), getResources().getColor(R.color.dot_light_screen4), defaultTransValue);
            animateBetweenColors(mCreatePartyButton, getResources().getColor(R.color.dot_light_screen3), getResources().getColor(R.color.dot_light_screen4), defaultTransValue);
            animateBetweenColors(mJoinPartyButton, getResources().getColor(R.color.dot_light_screen3), getResources().getColor(R.color.dot_light_screen4), defaultTransValue);

        } else if (backgroundCount == 3) {
            animateBetweenColors(mMainLayout, getResources().getColor(R.color.bg_screen4), getResources().getColor(R.color.bg_screen1), defaultTransValue);
            animateBetweenColors(mLoginButton, getResources().getColor(R.color.dot_light_screen4), getResources().getColor(R.color.dot_light_screen1), defaultTransValue);
            animateBetweenColors(mCreatePartyButton, getResources().getColor(R.color.dot_light_screen4), getResources().getColor(R.color.dot_light_screen1), defaultTransValue);
            animateBetweenColors(mJoinPartyButton, getResources().getColor(R.color.dot_light_screen4), getResources().getColor(R.color.dot_light_screen1), defaultTransValue);

            mBackgroundCount = 0;
        }

    }


    /**
     * Method for changing color
     *
     * @param viewToAnimateItBackground
     * @param colorFrom
     * @param colorTo
     * @param durationInMs
     */
    private void animateBetweenColors(final View viewToAnimateItBackground, final int colorFrom, final int colorTo,
                                      final int durationInMs) {

        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ColorDrawable colorDrawable = new ColorDrawable(colorFrom);

            /**
             * Update the color background on anmation update
             * @param animator
             */
            @Override
            public void onAnimationUpdate(final ValueAnimator animator) {
                colorDrawable.setColor((Integer) animator.getAnimatedValue());
                viewToAnimateItBackground.setBackgroundDrawable(colorDrawable);
            }
        });
        if (durationInMs >= 0)
            colorAnimation.setDuration(durationInMs);
        colorAnimation.start();
    }
}

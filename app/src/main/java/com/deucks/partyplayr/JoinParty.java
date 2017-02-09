package com.deucks.partyplayr;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class for Joining a party,
 */
public class JoinParty extends AppCompatActivity {

    //Declare the variables
    private Toolbar mToolbar;
    private TextView mJoinPartyTitle;
    private RelativeLayout mJoinPartyContainer;
    private FloatingActionButton mFab;
    private PartyPlayrService mPartyPlayrService;
    private EditText mPartyNameInput;

    private String [] mUserVars;
    private PreferenceManager mPrefManager;
    private int mBackgroundCount = 0;


    /**
     * Called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        changeStatusBarColor();

        //Start background color change
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeBackgroundColor(mBackgroundCount++);
                handler.postDelayed(this, 10000);
            }
        }, 7000);


        setContentView(R.layout.activity_join_party);

        mToolbar = (Toolbar) findViewById(R.id.join_party_toolbar);
        mJoinPartyTitle = (TextView) findViewById(R.id.join_party_title);
        mJoinPartyContainer = (RelativeLayout) findViewById(R.id.join_party_title_layout);
        mPartyNameInput = (EditText) findViewById(R.id.join_party_input);

        mPrefManager = new PreferenceManager(this);
        mPartyPlayrService = new PartyPlayrService(getApplicationContext());
        loadUserVars();

        //setup toolbar buttons
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //back button click listner
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to main screen
                finish();
            }
        });

        //Click listner for FAB
        mFab = (FloatingActionButton) findViewById(R.id.join_party_button);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mPartyPlayrService.checkTokens();
                new JoinPartyAsyncTask(mPartyPlayrService, view, mPartyNameInput.getText().toString()).execute();
            }
        });
    }

    /**
     * Load user variables into class variables
     */
    private void loadUserVars()
    {
        mUserVars = mPrefManager.getFBUserSessionVariables();
        mJoinPartyTitle.setText(String.format("%s%s", mUserVars[0], getString(R.string.join_the_party)));
    }

    /**
     * Async Task Class for Single photo background download
     */
    private class JoinPartyAsyncTask extends AsyncTask<Void, Void, Boolean> {

        //Setup variables
        private ProgressDialog mRingProgressDialog;
        private PartyPlayrService mPartyPlayrService;
        private String mPartyNameInput;
        private View mView;
        /**
         * Called when class is initiated, set variables
         */
        public JoinPartyAsyncTask(PartyPlayrService partyPlayrService, View view, String partyName) {
            mPartyPlayrService = partyPlayrService;
            mView = view;
            mPartyNameInput = partyName;
        }

        /**
         * Background thread operations
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            //Sleep the worker for 3 seconds
            return mPartyPlayrService.joinParty(mPartyNameInput);
        }

        /**
         * Called before task is being run
         */
        @Override
        protected void onPreExecute() {
            //Initiate and show the progress dialog box
            mRingProgressDialog = ProgressDialog.show(JoinParty.this, getString(R.string.finding_party_string),
                    getString(R.string.just_a_sec_string), true);

        }

        /**
         * Called when the background task is done
         */
        @Override
        protected void onPostExecute(Boolean mIsPartyJoinedSuccess) {
            //Hide progressbar, check if the party was joined successfully
            mRingProgressDialog.dismiss();
            if (mIsPartyJoinedSuccess)
            {
                //Start a new QueActivity
                startActivity(new Intent(getApplicationContext(), QueActivity.class));
            }
            else
            {
                //Show an error
                Snackbar.make(mView, R.string.could_not_join_party_string, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_action_string, null).show();

            }
        }
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        //Check if lollipop SDK first
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
        if (backgroundCount == 0) {
            animateBetweenColors(mJoinPartyContainer, getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), defaultTransValue);
        } else if (backgroundCount == 1) {
            animateBetweenColors(mJoinPartyContainer, getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3), defaultTransValue);
        } else if (backgroundCount == 2) {
            animateBetweenColors(mJoinPartyContainer, getResources().getColor(R.color.bg_screen3), getResources().getColor(R.color.bg_screen4), defaultTransValue);

        } else if (backgroundCount == 3) {
            animateBetweenColors(mJoinPartyContainer, getResources().getColor(R.color.bg_screen4), getResources().getColor(R.color.bg_screen1), defaultTransValue);
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

        //Update the color from old to new
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ColorDrawable colorDrawable = new ColorDrawable(colorFrom);

            /**
             * Update the animation, set the color transition
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

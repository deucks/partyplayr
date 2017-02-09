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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class for CreateParty Activity, contains code for dealing with creating party etc.
 */
public class CreateParty extends AppCompatActivity {

    //Declare Variables
    private int mBackgroundCount = 0;
    private RelativeLayout mCreatePartyContainer;
    private String [] mUserVars;
    private PreferenceManager mPrefManager;

    private Toolbar mToolbar;
    private TextView mCreatePartyTitle;
    private FloatingActionButton mFab;
    private PartyPlayrService mPartyPlayrService;
    private EditText mPartyNameInput;

    /**
     * Called When Activity is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        changeStatusBarColor();

        setContentView(R.layout.activity_create_party);

        //Start background color change
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeBackgroundColor(mBackgroundCount++);
                handler.postDelayed(this, 10000);
            }
        }, 7000);

        //Setup variables with widgets
        mCreatePartyContainer = (RelativeLayout) findViewById(R.id.create_party_title_layout);
        mToolbar = (Toolbar) findViewById(R.id.create_party_toolbar);
        mCreatePartyTitle = (TextView) findViewById(R.id.create_party_title);
        mPartyNameInput = (EditText) findViewById(R.id.create_party_input);

        mPrefManager = new PreferenceManager(this);
        mPartyPlayrService = new PartyPlayrService(getApplicationContext());
        loadUserVars();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Listner for backbutton
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to main screen
                finish();
            }
        });

        //Listener for FAB
        mFab = (FloatingActionButton) findViewById(R.id.create_party_button);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create the new party in the background
                new CreatePartyAsyncTask(mPartyPlayrService, view, mPartyNameInput.getText().toString()).execute();
            }
        });

    }

    /**
     * Load user variables into class variables
     */
    private void loadUserVars()
    {
        mUserVars = mPrefManager.getFBUserSessionVariables();
        mCreatePartyTitle.setText(String.format("%s%s", mUserVars[0], getString(R.string.start_the_party)));
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
     * Async Task Class for Creating the party in the background
     */
    private class CreatePartyAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mRingProgressDialog;
        private PartyPlayrService mPartyPlayrService;
        private String mPartyNameInput;
        private View mView;
        /**
         * Called when class is initiated, set variables
         */
        public CreatePartyAsyncTask(PartyPlayrService partyPlayrService, View view, String partyName) {
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
            return mPartyPlayrService.createParty(mPartyNameInput);
        }

        /**
         * Called before task is being run
         */
        @Override
        protected void onPreExecute() {
            //Initiate and show the progress dialog box
            mRingProgressDialog = ProgressDialog.show(CreateParty.this, getString(R.string.progress_create_party_string),
                    getString(R.string.just_a_sec_string), true);

        }

        /**
         * Called when the background task is done
         */
        @Override
        protected void onPostExecute(Boolean mIsPartyCreatedSuccess) {
            //Figure out which photo to display, and display it
            mRingProgressDialog.dismiss();

            //Check if party was created successfully.
            if (mIsPartyCreatedSuccess)
            {
                //Start Que activity
                startActivity(new Intent(getApplicationContext(), QueActivity.class));
            }
            else
            {
                //Show error in a snackbar
                Snackbar.make(mView, R.string.could_not_create_party_string, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_action_string, null).show();

            }
        }

    }


    /**
     * Method for changing background color
     *
     * @param backgroundCount
     */
    private void changeBackgroundColor(int backgroundCount) {
        //Check what state the background color is in
        int defaultTransValue = 5000;
        if (backgroundCount == 0) {
            animateBetweenColors(mCreatePartyContainer, getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), defaultTransValue);
        } else if (backgroundCount == 1) {
            animateBetweenColors(mCreatePartyContainer, getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3), defaultTransValue);
        } else if (backgroundCount == 2) {
            animateBetweenColors(mCreatePartyContainer, getResources().getColor(R.color.bg_screen3), getResources().getColor(R.color.bg_screen4), defaultTransValue);
        } else if (backgroundCount == 3) {
            animateBetweenColors(mCreatePartyContainer, getResources().getColor(R.color.bg_screen4), getResources().getColor(R.color.bg_screen1), defaultTransValue);
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

        //Animate the colors transition
        final ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            ColorDrawable colorDrawable = new ColorDrawable(colorFrom);

            /**
             * When then onAnimation method is called, animate color change
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

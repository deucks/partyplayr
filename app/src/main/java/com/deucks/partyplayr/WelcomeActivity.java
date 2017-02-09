package com.deucks.partyplayr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class for First Time Welcome Screen
 */
public class WelcomeActivity extends AppCompatActivity {

    //Declare Variables
    private ViewPager mVviewPager;
    private MyViewPagerAdapter mMyViewPagerAdapter;
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    private int[] mLayouts;
    private Button mBtnSkip;
    private Button mBtnNext;
    private PreferenceManager mPrefManager;

    /**
     * Called When the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        mPrefManager = new PreferenceManager(this);
        if (!mPrefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        //Setup the widgets with the variables
        mVviewPager = (ViewPager) findViewById(R.id.view_pager);
        mDotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        mBtnSkip = (Button) findViewById(R.id.btn_skip);
        mBtnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        mLayouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        mMyViewPagerAdapter = new MyViewPagerAdapter();
        mVviewPager.setAdapter(mMyViewPagerAdapter);
        mVviewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        //Listners for button
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < mLayouts.length) {
                    // move to next screen
                    mVviewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    /**
     * Method for adding dots to bottom of screen
     * @param currentPage
     *
     */
    private void addBottomDots(int currentPage) {
        mDots = new TextView[mLayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        //Adds dots
        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive[currentPage]);
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * Gets the current item of viewpager
     * @param i
     * @return
     */
    private int getItem(int i) {
        return mVviewPager.getCurrentItem() + i;
    }

    /**
     * Launches the Main screen
     */
    private void launchHomeScreen() {
        mPrefManager.setFirstTimeLaunched(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        /**
         * When a different page is selected
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == mLayouts.length - 1) {
                // last page. make button text to GOT IT
                mBtnNext.setText(getString(R.string.start));
                mBtnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                mBtnNext.setText(getString(R.string.next));
                mBtnSkip.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Called when the page is scrolled
         * @param arg0
         * @param arg1
         * @param arg2
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        /**
         * Called when the page state is changed
         * @param arg0
         */
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

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
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        /**
         * Constructor for adapter
         */
        public MyViewPagerAdapter() {
        }

        /**
         * Create the item of from viewgroup
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(mLayouts[position], container, false);
            container.addView(view);

            return view;
        }

        /**
         * Returnds the count of layouts
         * @return
         */
        @Override
        public int getCount() {
            return mLayouts.length;
        }

        /**
         * Returns the view
         * @param view
         * @param obj
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        /**
         * Destroys the view item at a position
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
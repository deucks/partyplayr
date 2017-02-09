package com.deucks.partyplayr;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;


public class QueActivity extends YouTubeBaseActivity {

    //Declare variables

    private PartyPlayrService mPartyPlayerService;
    private PreferenceManager mPrefManager;
    private SlidingUpPanelLayout mSlidingLayout;
    private PartyDetailModel mPartyModel;
    private List<PartyMusicModel> mPartyMusicModel;
    private List<PartyPeopleModel> mPartyPeopleModel;
    private List<YoutubeSearchModel> mYoutubeSearchModel;
    private PartyMusicModel mPartyCurrentMusicModel;
    private MusicQueAdapter mMusicQueAdapter;
    private MusicQueSearchAdapter mMusicQueSearchAdapter;

    private YouTubePlayerView mYoutubePlayerView;
    private YouTubePlayer.OnInitializedListener mOnInitializedListner;
    private YouTubePlayer mYoutubePlayer;



    private Pusher mPusher;
    private Channel mChannel;

    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private Button btnHide;
    private LinearLayout mDraggerTitle;
    private LinearLayout mDraggerSongsInput;
    private EditText mYoutubeSearchEditText;
    private ImageView mNoHostImageView;
    private Button mPauseYoutubeButton;
    private Button mPlayYoutubeButton;
    private Button mDislikeMusicButton;

    private RecyclerView mQueMusicRecycler;
    private RecyclerView mQueSearchRecycler;

    private boolean isUserHost = false;
    private boolean hasDisliked = false;


    private int mBackgroundCount = 0;

    /**
     * Called when the QueActivity is created
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


        //Setup all the variables with widgets on activity
        setContentView(R.layout.activity_que);
        mToolbar= (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(UniversalConstants.EMPTY);

        mQueMusicRecycler = (RecyclerView)findViewById(R.id.music_que_recylcer);
        mQueMusicRecycler.setLayoutManager(new LinearLayoutManager(this));
        mQueMusicRecycler.setItemAnimator(new SlideInDownAnimator());
        mQueMusicRecycler.setHasFixedSize(true);

        mQueSearchRecycler = (RecyclerView)findViewById(R.id.music_que_search_recycler);
        mQueSearchRecycler.setLayoutManager(new LinearLayoutManager(this));
        mQueSearchRecycler.setItemAnimator(new SlideInDownAnimator());
        mQueSearchRecycler.setHasFixedSize(true);



        btnHide = (Button)findViewById(R.id.btn_hide);
        mDraggerTitle = (LinearLayout)findViewById(R.id.que_activ_dragger_title);
        mDraggerSongsInput = (LinearLayout)findViewById(R.id.que_activ_dragger_search_songs_layout);
        mYoutubeSearchEditText = (EditText)findViewById(R.id.que_active_youtube_search_field);
        mNoHostImageView = (ImageView)findViewById(R.id.player_no_host_image);
        mPauseYoutubeButton = (Button)findViewById(R.id.content_que_pause_music_button);
        mDislikeMusicButton = (Button)findViewById(R.id.content_que_dislikebutton);
        mPlayYoutubeButton = (Button)findViewById(R.id.content_que_play_music_button);

        mPartyPlayerService = new PartyPlayrService(this);
        mPrefManager = new PreferenceManager(this);
        mPartyModel = new PartyDetailModel();

        mPusher = new Pusher(UniversalConstants.PUSHER_API_KEY);

        //Connect to Pusher
        mPusher.connect(new ConnectionEventListener() {
            /**
             * Called when pusher is connected with app
             * @param change
             */
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.d("State changed to ", change.getCurrentState().toString());
            }

            /**
             * Called when there is an error connecting with pusher api
             * @param message
             * @param code
             * @param e
             */
            @Override
            public void onError(String message, String code, Exception e) {
                Log.d("There was a problem co ", message.toString() + code + e.toString());
            }
        }, ConnectionState.ALL);


        //set layout slide listener
        mSlidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);

        //Start background color change
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeBackgroundColor(mBackgroundCount++);
                handler.postDelayed(this, 10000);
            }
        }, 7000);

        mYoutubePlayerView = (YouTubePlayerView)findViewById(R.id.youtube_video);

        //Setup the listner for youtube
        mOnInitializedListner = new YouTubePlayer.OnInitializedListener(){

            /**
             * When initialized setup the variables
             * @param provider
             * @param youTubePlayer
             * @param b
             */
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYoutubePlayer = youTubePlayer;
                mYoutubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                mYoutubePlayer.setPlaybackEventListener(playerEventChangeListener);

            }

            /**
             * Called when there is an initilize failure
             * @param provider
             * @param youTubeInitializationResult
             */
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }

        };


        //The main panel UI listner
        mSlidingLayout.setPanelSlideListener(onSlideListener());
        mYoutubePlayerView.initialize(UniversalConstants.YOUTUBE_API_KEY, mOnInitializedListner);

        //Call to recieve details about the party
        new GetPartyDetailsAsyncTask(mPartyPlayerService, mPartyMusicModel, mPartyPeopleModel).execute();

        //Setup FAB event listner
        mFab = (FloatingActionButton)findViewById(R.id.que_activ_add_music_button);
        mFab.hide();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //When the Fab is clicked, show the add songs panel
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

            }
        });

        //Set the onkey listner for the textfield on add song
        mYoutubeSearchEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            //When enter is pressed, Search youtube with string
                            new SearchYoutubeAsyncTask(mYoutubeSearchEditText.getText().toString(), mYoutubeSearchModel).execute();
                            mYoutubeSearchEditText.clearFocus();
                            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    /**
     * Called when activity destroyed
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mYoutubePlayer = null;
    }

    /**
     * Method for listening when the orientation changes
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen, hide fab when landscape
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isUserHost)
            {
                //Hide the Image when user is not host
                mNoHostImageView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
                mNoHostImageView.setVisibility(View.GONE);
            }
            else
            {
                mFab.hide();
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if (!isUserHost)
            {
                //Show the Image when user is not host
                mNoHostImageView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                mNoHostImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                mFab.show();
            }
        }
    }

    /**
     * Method to listen for youtube player changes
     */
    private YouTubePlayer.PlaybackEventListener playerEventChangeListener = new YouTubePlayer.PlaybackEventListener(){

        /**
         * When the video starts playing
         */
        @Override
        public void onPlaying() {
            mPauseYoutubeButton.setVisibility(View.VISIBLE);
            mPlayYoutubeButton.setVisibility(View.INVISIBLE);
        }

        /**
         * Called when the youtubeplayr is paused
         */
        @Override
        public void onPaused() {
            mPauseYoutubeButton.setVisibility(View.INVISIBLE);
            mPlayYoutubeButton.setVisibility(View.VISIBLE);
        }

        /**
         * Called when the youtube payer is stopped
         */
        @Override
        public void onStopped() {

        }

        /**
         * Called when the Youtube player is currently buffering
         */
        @Override
        public void onBuffering(boolean b) {

        }

        /**
         * When the user seeks to a different time on a video
         * @param i
         */
        @Override
        public void onSeekTo(int i) {

        }
    };

    /**
     * Method listner for youtube, notifys if any event happens with youtube player
     */
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        /**
         * Called when ads are playing
         */
        @Override
        public void onAdStarted() {
        }

        /**
         * Called when an error occurs with youtube
         * @param arg0
         */
        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        /**
         * Called when the youtube player is loaded
         * @param arg0
         */
        @Override
        public void onLoaded(String arg0) {
        }

        /**
         * Called when the youtube player is loading
         */
        @Override
        public void onLoading() {
        }

        /**
         * Called when the video has ended
         */
        @Override
        public void onVideoEnded() {
            //goto next song
            nextSongUpdateQue(true);
            Log.d("video next", "success");
        }

        /**
         * Called when the video is started
         */
        @Override
        public void onVideoStarted() {
        }
    };


    /**
     * Async Task for adding new songs to song que
     */
    private class SearchYoutubeAsyncTask extends AsyncTask<Void, Void, List<YoutubeSearchModel>> {

        //Setup variables
        private String mSearchQuery;
        private ProgressDialog mRingProgressDialog;
        private List<YoutubeSearchModel> youtubeSearchModel;

        /**
         * Called before task is being run
         */
        @Override
        protected void onPreExecute() {
            //Initiate and show the progress dialog box
            mRingProgressDialog = ProgressDialog.show(QueActivity.this, getString(R.string.just_second_string),
                    getString(R.string.sweet_tracks_string), true);

        }

        /**
         * Contructor for Async task
         * @param search
         * @param youtubeSearchModel
         */
        SearchYoutubeAsyncTask(String search, List<YoutubeSearchModel> youtubeSearchModel) {

            mSearchQuery = search.replace(getString(R.string.empty_space_string), "%20");
            this.youtubeSearchModel = youtubeSearchModel;
        }

        /**
         * Method for doing things in the background
         * @param params
         * @return
         */
        @Override
        protected List<YoutubeSearchModel> doInBackground(Void... params) {
            return mPartyPlayerService.youtubeSearch(mSearchQuery, youtubeSearchModel);
        }

        /**
         * Method for displaying results when background task is completed
         * @param youtubeSearch
         */
        @Override
        protected void onPostExecute(List<YoutubeSearchModel> youtubeSearch)
        {
            //Display results
            mRingProgressDialog.dismiss();
            mYoutubeSearchModel = youtubeSearch;
            String currentId = PartyPlayrHelper.getCurrentUserId(mPartyPeopleModel, getApplicationContext());
            mMusicQueSearchAdapter = new MusicQueSearchAdapter(mYoutubeSearchModel, mPartyModel, currentId, getApplicationContext());
            mQueSearchRecycler.setAdapter(mMusicQueSearchAdapter);
        }
    }



    /**
     * Async Task Class for Single photo background download
     */
    private class GetPartyDetailsAsyncTask extends AsyncTask<Void, Void, PartyDetailModel> {

        private ProgressDialog mRingProgressDialog;
        private PartyPlayrService mPartyPlayrService;
        private String mPartyId;
        private PartyDetailModel mPartyDetailModel;
        private List<PartyMusicModel> mPartyMusicModel;
        private List<PartyPeopleModel> mPartyPeopleModel;
        /**
         * Called when class is initiated, set variables
         */
        public GetPartyDetailsAsyncTask(PartyPlayrService partyPlayrService, List<PartyMusicModel> partMusicModel, List<PartyPeopleModel> partyPeopleModel) {
            mPartyPlayrService = partyPlayrService;
            mPartyMusicModel = partMusicModel;
            mPartyPeopleModel = partyPeopleModel;
            mPartyId = mPrefManager.getGeneralString(UniversalConstants.PREF_CURRENT_PARTY_ID);
        }

        /**
         * Background thread operations
         */
        @Override
        protected PartyDetailModel doInBackground(Void... params) {
            //Set local variables with returned values
            mPartyDetailModel = mPartyPlayerService.partyDetails(mPartyDetailModel);
            mPartyPeopleModel = mPartyPlayrService.partyPeople(mPartyDetailModel.getmPartyId(), mPartyPeopleModel);
            mPartyMusicModel = mPartyPlayrService.partyMusic(mPartyDetailModel.getmPartyUrl(), mPartyMusicModel);

            return mPartyDetailModel;
        }

        /**
         * Called before task is being run
         */
        @Override
        protected void onPreExecute() {
            //Initiate and show the progress dialog box
            mRingProgressDialog = ProgressDialog.show(QueActivity.this, getString(R.string.syncing_text),
                    getString(R.string.loading_tracks_tracks), true);

        }

        /**
         * Called when the background task is done
         */
        @Override
        protected void onPostExecute(PartyDetailModel partyModel) {
            //Update The Ui based on new data
            mRingProgressDialog.dismiss();
            updatePartyDetailUI(partyModel, this.mPartyMusicModel, this.mPartyPeopleModel);
            setupChannel();
            Log.d("pusher party name", UniversalConstants.PUSHER_CHANNEL_DEFAULT + mPartyModel.getmPartyUrl());
        }


    }

    /**
     * Method for setting up pusher channel
     */
    private void setupChannel()
    {
        //Setup comms with pusher
        mChannel = mPusher.subscribe(UniversalConstants.PUSHER_CHANNEL_DEFAULT + mPartyModel.getmPartyUrl(), new ChannelEventListener() {
            /**
             * Called when an event happens with pusher
             * @param s
             * @param s1
             * @param s2
             */
            @Override
            public void onEvent(String s, String s1, String s2) {

            }

            /**
             * Called when the subscription is succeeded with pusher
             * @param channelName
             */
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("Subscribed with channel", channelName);
            }
        });

        //Bind pusher with the list-update event
        mChannel.bind("list-update", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, final String data) {
                runOnUiThread(new Runnable() {
                    /**
                     * Method for updating next song from the UI thread
                     */
                    public void run() {
                        if (data.equals("\"dislike-update\""))
                        {
                            if (isUserHost)
                            {
                                nextSongUpdateQue(true);
                            }
                            return;
                        }
                        else
                        {
                            nextSongUpdateQue(false);
                        }
                    }
                });
                Log.d("Received: list update", data);
            }
        });

        //Bind pushr with list-add-song update
        mChannel.bind("list-add-song", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, final String data) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Get the response, replace response chars to make json friendly
                        String dataJson = "[" + data.substring(1, data.length()-1) + "]";
                        dataJson = dataJson.replace("\\\"","\"");
                        Log.d("json add song", dataJson);
                        //Add song to que
                        new ListQueAddSongAsyncTask(dataJson).execute();

                    }
                });
            }
        });
    }

    /**
     * Async Task for adding new songs to song que
     */
    private class ListQueAddSongAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        //Declare variables
        private JSONArray mJsonDataArray;
        private JSONObject mJsonDataObject;
        private String mImageUrl;

        /**
         * Constructor class
         * @param jsonData
         */
        ListQueAddSongAsyncTask(String jsonData) {

            //try parsing the json
            try {
                mJsonDataArray = new JSONArray(jsonData);
                mJsonDataObject = mJsonDataArray.getJSONObject(0);
                mImageUrl = mJsonDataObject.getString("music_albumart");
                mImageUrl = mImageUrl.replace("\\","");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /**
         * Backgrounf task for adding new song to que
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Void... params) {
            //Download the cover art
            Bitmap imageDownloadStore = null;
            try {
                InputStream in = new java.net.URL(mImageUrl).openStream();
                imageDownloadStore = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return  imageDownloadStore;
        }

        /**
         * When downloading art is finished, this is called
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            //Update the Model class, add new item
            int partyMusicSize = mPartyMusicModel.size();
            try {
                mPartyMusicModel.add(partyMusicSize,
                        new PartyMusicModel(
                                mJsonDataObject.getString("party_music_id"),
                                mJsonDataObject.getString("party_id"),
                                mJsonDataObject.getString("music_id"),
                                mJsonDataObject.getString("music_played"),
                                mJsonDataObject.getString("user_id"),
                                mJsonDataObject.getString("music_name"),
                                mJsonDataObject.getString("music_url"),
                                mJsonDataObject.getString("music_artist"),
                                mJsonDataObject.getString("music_albumart"),
                                bitmap
                        )
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Notify that music has been added, insert with animation
            mMusicQueAdapter.notifyItemInserted(partyMusicSize);
            //If there is no music playing, load the new track
            if (mPartyCurrentMusicModel == null)
            {
                nextSongUpdateQue(true);
            }

        }
    }

    /**
     * Method for updating the song que
     * @param removeFromPartyPlayr
     */
    private void nextSongUpdateQue(boolean removeFromPartyPlayr)
    {
        //If the music list is ot empty, then udpate que to next track
        if (mPartyMusicModel.size() != 0)
        {
            hasDisliked = false;
            //End the song, tell partyplayr about it
            if (removeFromPartyPlayr && isUserHost && (mPartyCurrentMusicModel != null))
            {
                new ListQueFinishSongAsyncTask(mPartyModel.getmPartyUrl(), mPartyCurrentMusicModel.getPartyMusicId()).execute();
            }

            //Check where the method request came from called from code or pusher
            if (removeFromPartyPlayr == false && isUserHost == true )
            {
                //Prevent the method being called twice for the host
                return;
            }

            //Remove from the beginning of the que & load into player
            mPartyCurrentMusicModel = mPartyMusicModel.get(0);
            mPartyMusicModel.remove(0);
            mMusicQueAdapter.notifyItemRemoved(0);

            //Setup widgets with new data
            ImageView imgView = (ImageView)findViewById(R.id.music_que_upnext_title_image);
            imgView.setImageBitmap(mPartyCurrentMusicModel.getmCoverArtBitmap());
            mNoHostImageView.setImageBitmap(mPartyCurrentMusicModel.getmCoverArtBitmap());

            TextView musicName = (TextView)findViewById(R.id.music_que_upnext_title_text);
            musicName.setText(mPartyCurrentMusicModel.getMusicName());

            TextView musicArtits = (TextView)findViewById(R.id.music_que_upnext_title_artist);
            musicArtits.setText(mPartyCurrentMusicModel.getMusicArtist());

            ImageView submitterImage = (ImageView) findViewById(R.id.music_que_upnext_title_submitter_img);
            TextView submitterName = (TextView)findViewById(R.id.music_que_upnext_title_submitter_text);

            //Loop through the amount of peeople at a party
            for (PartyPeopleModel pplMdl:mPartyPeopleModel)
            {
                //Find who is the submitter
                if (pplMdl.getmProfileId().equals(mPartyCurrentMusicModel.getUserId()))
                {
                    //Set their details
                    submitterImage.setImageDrawable(PartyPlayrHelper.getRoundedBitmapImage(this, pplMdl.getmProfileBitmap()));
                    submitterName.setText(pplMdl.getmPersonFirstName());
                }
            }

            if (isUserHost)
            {
                //Only play the video is the user is the host of the party
                if (mYoutubePlayer != null)
                {
                    mYoutubePlayer.loadVideo(mPartyCurrentMusicModel.getMusicUrl());
                }
            }

        }
        else
        {
            //If the song que is empty, show the user to add songs to que
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
    }

    /**
     * Async Task for adding new songs to song que
     */
    private class ListQueFinishSongAsyncTask extends AsyncTask<Void, Void, Boolean> {

        //Declare variables
        private String mPlayerId;
        private String mPartyMusicId;

        /**
         * Constructor
         * @param playerId
         * @param partyMusicId
         */
        ListQueFinishSongAsyncTask(String playerId, String partyMusicId) {
            mPlayerId = playerId;
            mPartyMusicId = partyMusicId;
        }

        /**
         * Background method, get song and tell party playr is has ended
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            return mPartyPlayerService.endSongToQue(mPlayerId, mPartyMusicId);
        }

        /**
         * Called when background has ended
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Boolean bitmap)
        {
            //Check if the song updating was success full
            if (bitmap)
            {
                Log.d("update song ended", "success");
            }
            else
            {
                Log.d("update song ended", "error");
            }
        }
    }

    /**
     * Method for updating the player state
     */
    private void updatePlayerState()
    {
        //Check if the user is host or not, if host then update ui
        if (!isUserHost)
        {
            //Show Image since not party host
            mYoutubePlayerView.setVisibility(View.GONE);
            mNoHostImageView.setVisibility(View.VISIBLE);
            mPauseYoutubeButton.setVisibility(View.GONE);
            mPlayYoutubeButton.setVisibility(View.GONE);
        }

    }



    /**
     * Method for handling click event to pause music
     * @param view
     */
    public void pauseCurrentMusic(View view)
    {
        if (mYoutubePlayer != null)
        {
            mYoutubePlayer.pause();

        }
    }

    /**
     * Method for handling click event to play music
     * @param view
     */
    public void playCurrentMusic(View view)
    {
        if (mYoutubePlayer != null)
        {
            mYoutubePlayer.play();
        }
    }

    /**
     * Method for handling click event to dislike video
     * @param view
     */
    public void dislikeCurrentMusic(View view)
    {
        if (mPartyCurrentMusicModel != null)
        {
            if (hasDisliked)
            {
                Toast.makeText(this, R.string.already_disliked_string, Toast.LENGTH_LONG).show();
            }
            else
            {
                new DislikeSongAsyncTask(mPartyCurrentMusicModel.getPartyMusicId(), mPartyModel.getmPartyUrl()).execute();
                hasDisliked = true;
            }

        }
    }

    /**
     * Async Task for disliking current song
     */
    private class DislikeSongAsyncTask extends AsyncTask<Void, Void, Boolean> {

        //Declare variables
        private String mMusicId;
        private String mPlayerUrl;

        /**
         * Constructor
         */
        DislikeSongAsyncTask(String musicId, String playrUrl) {
            mMusicId = musicId;
            mPlayerUrl = playrUrl;
        }

        /**
         * Background method, get song and tell party playr is has ended
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            return mPartyPlayerService.dislikeCurrentSong(mMusicId, mPlayerUrl);
        }

        /**
         * Called when background has ended
         * @param bitmap
         */
        @Override
        protected void onPostExecute(Boolean bitmap)
        {
            //Check if the song updating was success full
            if (bitmap)
            {
                Log.d("update song ended", "success");
            }
            else
            {
                Log.d("update song ended", "error");
            }
        }
    }


    /**
     * Method for dealing with the UI updates
     * @param partyModel
     * @param partyMusicModel
     * @param partyPeopleModel
     */
    private void updatePartyDetailUI(PartyDetailModel partyModel, List<PartyMusicModel> partyMusicModel, List<PartyPeopleModel> partyPeopleModel)
    {
        //Setup variables
        mPartyModel = partyModel;
        mPartyMusicModel = partyMusicModel;
        mPartyPeopleModel = partyPeopleModel;
        Log.d("party host id", mPartyModel.getmPartyHostId());

        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        //Call more methods to update UI
        updateToolBarUI();
        updateMusicQueUI();
        updatePlayerState();
        nextSongUpdateQue(true);
    }

    /**
     * Method for updating the UI for music que
     */
    private void updateMusicQueUI()
    {
        //Set the music que adapter
        mMusicQueAdapter = new MusicQueAdapter(mPartyMusicModel,mPartyPeopleModel, this);
        mQueMusicRecycler.setAdapter(mMusicQueAdapter);
    }

    /**
     * Method for updating the Toolbar for the app
     */
    private void updateToolBarUI()
    {
        //Set party Title
        mToolbar.setTitle(mPartyModel.getmPartyName());

        //Check for who is the partyhost
        String partyHostId = mPartyModel.getmPartyHostId();
        for(PartyPeopleModel peopleData : mPartyPeopleModel) {

            if (peopleData.getmProfileId().equals(partyHostId))
            {
                //If the user is host
                if (peopleData.getmProfileFBId().equals(mPrefManager.getGeneralString(UniversalConstants.PREF_FB_ID)))
                {
                    mToolbar.setSubtitle(getString(R.string.you_are_host_string) + mPartyModel.getmPartyUrl());
                    isUserHost = true;
                }
                else
                {
                    mToolbar.setSubtitle(peopleData.getmPersonFirstName() + getString(R.string.no_host_string) + mPartyModel.getmPartyUrl());
                    isUserHost = false;
                }
            }
        }
    }


    /**
     * Method that is called when a event occurs in Sliding Planel
     * @return
     */
    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            /**
             * Called when the pannel is sliding
             * @param view
             * @param v
             */
            @Override
            public void onPanelSlide(View view, float v) {
            }

            /**
             * When the panel is collapsed, hide FAB
             * @param panel
             */
            @Override
            public void onPanelCollapsed(View panel) {
                mFab.hide();
            }

            /**
             * When the panel is expanded, hide FAB
             * @param panel
             */
            @Override
            public void onPanelExpanded(View panel) {
                mFab.hide();
            }

            /**
             * When the panel is anchored, hide FAB
             * @param panel
             */
            @Override
            public void onPanelAnchored(View panel) {
                mFab.hide();
            }

            /**
             * When the panel is hidden, show FAB
             * @param panel
             */
            @Override
            public void onPanelHidden(View panel) {
                mFab.show();
            }
        };
    }

    /**
     * Method for hiding the music search window
     * @param view
     */
    public void hideMusicSearch(View view)
    {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
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
        if (backgroundCount == 0) {
            animateBetweenColors(mToolbar, getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), defaultTransValue);
            animateBetweenColors(mDraggerTitle, getResources().getColor(R.color.bg_screen1), getResources().getColor(R.color.bg_screen2), defaultTransValue);
            animateBetweenColors(mDraggerSongsInput, getResources().getColor(R.color.dot_light_screen1), getResources().getColor(R.color.dot_light_screen2), defaultTransValue);

        } else if (backgroundCount == 1) {
            animateBetweenColors(mToolbar, getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3), defaultTransValue);
            animateBetweenColors(mDraggerTitle, getResources().getColor(R.color.bg_screen2), getResources().getColor(R.color.bg_screen3), defaultTransValue);
            animateBetweenColors(mDraggerSongsInput, getResources().getColor(R.color.dot_light_screen2), getResources().getColor(R.color.dot_light_screen3), defaultTransValue);

        } else if (backgroundCount == 2) {
            animateBetweenColors(mToolbar, getResources().getColor(R.color.bg_screen3), getResources().getColor(R.color.bg_screen4), defaultTransValue);
            animateBetweenColors(mDraggerTitle, getResources().getColor(R.color.bg_screen3), getResources().getColor(R.color.bg_screen4), defaultTransValue);
            animateBetweenColors(mDraggerSongsInput, getResources().getColor(R.color.dot_light_screen3), getResources().getColor(R.color.dot_light_screen4), defaultTransValue);

        } else if (backgroundCount == 3) {
            animateBetweenColors(mToolbar, getResources().getColor(R.color.bg_screen4), getResources().getColor(R.color.bg_screen1), defaultTransValue);
            animateBetweenColors(mDraggerTitle, getResources().getColor(R.color.bg_screen4), getResources().getColor(R.color.bg_screen1), defaultTransValue);
            animateBetweenColors(mDraggerSongsInput, getResources().getColor(R.color.dot_light_screen4), getResources().getColor(R.color.dot_light_screen1), defaultTransValue);
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


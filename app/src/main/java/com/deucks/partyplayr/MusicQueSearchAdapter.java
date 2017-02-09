package com.deucks.partyplayr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

/**
 * Adapter for handling the displaying of search results in recylcer view
 */
public class MusicQueSearchAdapter extends RecyclerView.Adapter<MusicQueSearchAdapter.MusicQueSearchHolder> {

    //Declare Variables
    private List<YoutubeSearchModel> mYoutubeSearchModel;
    private PartyDetailModel mPartyDetailModel;
    private Context mContext;
    private String mUserId;

    /**
     * Contructor for Adapter, called when created
     * @param youtubeSearchModel
     * @param partyDetailModel
     * @param userId
     * @param context
     */
    MusicQueSearchAdapter(List<YoutubeSearchModel> youtubeSearchModel, PartyDetailModel partyDetailModel, String userId, Context context)
    {
        //Initialize the variables
        mContext = context;
        mYoutubeSearchModel = youtubeSearchModel;
        mPartyDetailModel = partyDetailModel;
        mUserId = userId;
    }

    /**
     * Called when the view holder is created
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MusicQueSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_que_search_item, parent, false);
        MusicQueSearchHolder musicQueSearchHolder = new MusicQueSearchHolder(layoutView);
        return musicQueSearchHolder;
    }

    /**
     * Called when the the holder is bind wihth the adapter
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MusicQueSearchHolder holder, int position) {
        //Set the music name
        holder.mMusicName.setText(mYoutubeSearchModel.get(position).getmVideoName());
    }

    /**
     * Returns how many items there in the model
     * @return
     */
    @Override
    public int getItemCount() {
        return mYoutubeSearchModel.size();
    }

    /**
     * Class for creating the Holder
     */
    public class MusicQueSearchHolder extends RecyclerView.ViewHolder{

        //Declare Variables
        private TextView mMusicName;
        private Button mAddMusicButton;
        private ProgressBar mItemSpinnerBar;

        /**
         * Called when object is created
         */
        public MusicQueSearchHolder(View itemView) {
            super(itemView);

            //Set up the variables
            mMusicName = (TextView)itemView.findViewById(R.id.music_que_search_name);
            mAddMusicButton = (Button)itemView.findViewById(R.id.music_que_search_song_add);
            mItemSpinnerBar = (ProgressBar)itemView.findViewById(R.id.music_que_search_song_progress);


            //Listen for when the add music button is clicked
            mAddMusicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //Add the song
                new AddSongAsyncTask(getAdapterPosition(), mItemSpinnerBar, mContext, mAddMusicButton).execute();
                }
            });
        }

        /**
         * Async Task Class for Adding Song to Que
         */
        private class AddSongAsyncTask extends AsyncTask<Void, Void, Boolean> {
            //Setup variables
            private ProgressBar mItemSpinnerBar;
            private Context mContext;
            private Button mAddMusicButton;

            private String mMusicName;
            private String mMusicUrl;
            /**
             * Called when class is initiated, set variables
             */
            public AddSongAsyncTask(int position, ProgressBar itemSpinnerBar, Context context, Button addMusicButton) {
                //Set the variables
                mItemSpinnerBar = itemSpinnerBar;
                this.mContext = context;
                this.mAddMusicButton = addMusicButton;

                for (int i = 0; i < mYoutubeSearchModel.size(); i++)
                {
                    if (i == position)
                    {
                        mMusicName = mYoutubeSearchModel.get(i).getmVideoName();
                        mMusicUrl = mYoutubeSearchModel.get(i).getmVideoUrl();
                    }
                }

            }

            /**
             * Background thread operations
             */
            @Override
            protected Boolean doInBackground(Void... params) {
                //Return the status for adding the new song
                return new PartyPlayrService(mContext).addSongToQue(mMusicName, mMusicUrl, mPartyDetailModel.getmPartyUrl(), mUserId);
            }

            /**
             * Called before task is being run
             */
            @Override
            protected void onPreExecute() {
                //Initiate and show the progress dialog box
                mAddMusicButton.setVisibility(View.INVISIBLE);
                mItemSpinnerBar.setVisibility(View.VISIBLE);
            }

            /**
             * Called when the background task is done
             */
            @Override
            protected void onPostExecute(Boolean videoAdded) {

                //Check if video added
                if (videoAdded)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mAddMusicButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.check_icon));
                    }
                }

                //Update the UI
                mAddMusicButton.setVisibility(View.VISIBLE);
                mItemSpinnerBar.setVisibility(View.GONE);

            }
        }
    }
}

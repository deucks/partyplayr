package com.deucks.partyplayr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Adapter class for the PartyMusicModel in recylcerview que activity
 */
public class MusicQueAdapter extends RecyclerView.Adapter<MusicQueAdapter.MusicQueHolder> {

    //Declare variables
    private List<PartyMusicModel> mMusicModel;
    private List<PartyPeopleModel> mPeopleModel;
    private Context mContext;

    /**
     * Constructor for Adapter
     * @param musicModel
     * @param peopleModel
     * @param context
     */
    MusicQueAdapter(List<PartyMusicModel> musicModel, List<PartyPeopleModel> peopleModel, Context context)
    {
        //Initilize variables
        mMusicModel = musicModel;
        mContext = context;
        mPeopleModel = peopleModel;
    }

    /**
     * Called when the view holder is created
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MusicQueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_que_item, parent, false);
        MusicQueHolder musicQueHolder = new MusicQueHolder(layoutView);
        return musicQueHolder;
    }

    /**
     * When the view holder is binded with the adapter
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MusicQueHolder holder, int position) {
        //Setup the variables with the widgets in reycler view
        holder.mMusicImg.setImageBitmap(mMusicModel.get(position).getmCoverArtBitmap());
        holder.mMusicName.setText(mMusicModel.get(position).getMusicName());
        holder.mMusicArtist.setText(mMusicModel.get(position).getMusicArtist());

        //Logic for finding who posted the music
        for (PartyPeopleModel pplMdl:mPeopleModel)
        {
            if (pplMdl.getmProfileId().equals(mMusicModel.get(position).getUserId()))
            {
                //Set the submitters name and image
                holder.mMusicSubmitter.setText(pplMdl.getmPersonFirstName());
                holder.mMusicSubmitterImage.setImageDrawable(PartyPlayrHelper.getRoundedBitmapImage(mContext, pplMdl.getmProfileBitmap()));
            }
            else if (mMusicModel.get(position).getUserId().equals(mContext.getString(R.string.null_string)))
            {
                //If there is not submitter name, set to be empty
                holder.mMusicSubmitter.setText(UniversalConstants.EMPTY);
                holder.mMusicSubmitterImage.setImageDrawable(null);
            }
        }


    }

    /**
     * Get the number of items in the adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return mMusicModel.size();
    }

    /**
     * Holder class for the music
     */
    public class MusicQueHolder extends RecyclerView.ViewHolder{

        //Declare variables
        private ImageView mMusicImg;
        private TextView mMusicName;
        private TextView mMusicArtist;
        private TextView mMusicSubmitter;
        private ImageView mMusicSubmitterImage;


        /**
         * Called when object is created
         */
        public MusicQueHolder(View itemView) {
            super(itemView);

            //Set up the variables
            mMusicImg = (ImageView)itemView.findViewById(R.id.music_que_img);
            mMusicName = (TextView)itemView.findViewById(R.id.music_que_name);
            mMusicArtist = (TextView)itemView.findViewById(R.id.music_que_artist);
            mMusicSubmitter = (TextView)itemView.findViewById(R.id.music_que_submitter);
            mMusicSubmitterImage = (ImageView) itemView.findViewById(R.id.music_que_submitter_image);
        }

    }
}

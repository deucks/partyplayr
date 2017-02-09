package com.deucks.partyplayr;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Model class for holding the music at a party.
 */
public class PartyMusicModel {

    //Declare Variables
    private String mPartyMusicId;
    private String mPartyId;
    private String mMusicId;
    private String mMusicPlayed;
    private String mUserId;
    private String mMusicName;
    private String mMusicUrl;
    private String mMusicArtist;
    private String mMusicCoverArt;
    private Bitmap mCoverArtBitmap;


    /**
     * Contructor for model class
     * @param partyMusicId
     * @param partyId
     * @param musicId
     * @param musicPlayed
     * @param userId
     * @param musicName
     * @param musicUrl
     * @param musicArtist
     * @param coverArt
     * @param bitmap
     */
    public PartyMusicModel(String partyMusicId, String partyId, String musicId, String musicPlayed, String userId, String musicName, String musicUrl, String musicArtist, String coverArt, Bitmap bitmap)
    {
        //Setup variables
        mPartyMusicId = partyMusicId;
        mPartyId = partyId;
        mMusicId = musicId;
        mMusicPlayed = musicPlayed;
        mUserId = userId;
        mMusicName = musicName;
        mMusicUrl = musicUrl;
        mMusicArtist = musicArtist;
        mMusicCoverArt = coverArt;
        mCoverArtBitmap = bitmap;

    }

    /**
     * Return the partymusic Id
     * @return
     */
    public String getPartyMusicId() {
        return mPartyMusicId;
    }

    /**
     * Set the partymusic Id
     * @param partyMusicId
     */
    public void setPartyMusicId(String partyMusicId) {
        this.mPartyMusicId = partyMusicId;
    }

    /**
     * Get the party Id
     * @return
     */
    public String getPartyId() {
        return mPartyId;
    }

    /**
     * Set the party Id
     * @param partyId
     */
    public void setPartyId(String partyId) {
        this.mPartyId = partyId;
    }

    /**
     * Get the music Id
     * @return
     */
    public String getMusicId() {
        return mMusicId;
    }

    /**
     * Set the music Id
     * @param musicId
     */
    public void setMusicId(String musicId) {
        this.mMusicId = musicId;
    }

    /**
     * Get musicplayed variables
     * @return
     */
    public String getMusicPlayed() {
        return mMusicPlayed;
    }

    /**
     * set music played variables
     * @param musicPlayed
     */
    public void setMusicPlayed(String musicPlayed) {
        this.mMusicPlayed = musicPlayed;
    }

    /**
     * Get user id
     * @return
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * set user id
     * @param userId
     */
    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    /**
     * Get music name
     * @return
     */
    public String getMusicName() {
        return mMusicName;
    }

    /**
     * set music name
     * @param musicName
     */
    public void setMusicName(String musicName) {
        this.mMusicName = musicName;
    }

    /**
     * get thhe music url
     * @return
     */
    public String getMusicUrl() {
        return mMusicUrl;
    }

    /**
     * set the music url
     * @param musicUrl
     */
    public void setMusicUrl(String musicUrl) {
        this.mMusicUrl = musicUrl;
    }

    /**
     * get music artist
     * @return
     */
    public String getMusicArtist() {
        return mMusicArtist;
    }

    /**
     * set the music artist
     * @param musicArtist
     */
    public void setMusicArtist(String musicArtist) {
        this.mMusicArtist = musicArtist;
    }

    /**
     * get music cover art
     * @return
     */
    public String getmMusicCoverArt() {
        return mMusicCoverArt;
    }

    /**
     * get music cover art
     * @param mMusicCoverArt
     */
    public void setmMusicCoverArt(String mMusicCoverArt) {
        this.mMusicCoverArt = mMusicCoverArt;
    }

    /**
     * Get cover Art Bitmap
     * @return
     */
    public Bitmap getmCoverArtBitmap() {
        return mCoverArtBitmap;
    }

    /**
     * Set Cover Art Bitmao
     * @param mCoverArtBitmap
     */
    public void setmCoverArtBitmap(Bitmap mCoverArtBitmap) {
        this.mCoverArtBitmap = mCoverArtBitmap;
    }

}

package com.deucks.partyplayr;

import android.graphics.Bitmap;

/**
 * Class for holding the People Model Data
 */
public class PartyPeopleModel {

    //Declare variables
    private String mPersonFirstName;
    private String mPersonLastName;
    private String mPersonName;
    private String mProfileUrl;
    private String mProfileId;
    private String mProfileFBId;
    private Bitmap mProfileBitmap;

    /**
     * Constructor for class
     * @param profileId
     * @param personFirstName
     * @param personLastName
     * @param personName
     * @param profileUrl
     * @param profileFBId
     * @param profileBitmap
     */
    public PartyPeopleModel (String profileId, String personFirstName, String personLastName, String personName, String profileUrl, String profileFBId, Bitmap profileBitmap)
    {
        //Setup variables
        mProfileId = profileId;
        mPersonFirstName = personFirstName;
        mPersonLastName = personLastName;
        mPersonName = personName;
        mProfileUrl = profileUrl;
        mProfileFBId = profileFBId;
        mProfileBitmap = profileBitmap;
    }

    /**
     * Return the firstname
     * @return
     */
    public String getmPersonFirstName() {
        return mPersonFirstName;
    }

    /**
     * Set the firstname
     * @param mPersonFirstName
     */
    public void setmPersonFirstName(String mPersonFirstName) {
        this.mPersonFirstName = mPersonFirstName;
    }

    /**
     * Get the lastname
     * @return
     */
    public String getmPersonLastName() {
        return mPersonLastName;
    }

    /**
     * set the lastname
     * @param mPersonLastName
     */
    public void setmPersonLastName(String mPersonLastName) {
        this.mPersonLastName = mPersonLastName;
    }

    /**
     * get the persons name
     * @return
     */
    public String getmPersonName() {
        return mPersonName;
    }

    /**
     * set the persons name
     * @param mPersonName
     */
    public void setmPersonName(String mPersonName) {
        this.mPersonName = mPersonName;
    }

    /**
     * Get the url for profile pic
     * @return
     */
    public String getmProfileUrl() {
        return mProfileUrl;
    }

    /**
     * Set the url for profile pic
     * @param mProfileUrl
     */
    public void setmProfileUrl(String mProfileUrl) {
        this.mProfileUrl = mProfileUrl;
    }

    /**
     * Gte profile Id
     * @return
     */
    public String getmProfileId() {
        return mProfileId;
    }

    /**
     * Get profile id
     * @param mProfileId
     */
    public void setmProfileId(String mProfileId) {
        this.mProfileId = mProfileId;
    }

    /**
     * Get facebookId
     * @return
     */
    public String getmProfileFBId() {
        return mProfileFBId;
    }

    /**
     * Set the profile Id
     * @param mProfileFBId
     */
    public void setmProfileFBId(String mProfileFBId) {
        this.mProfileFBId = mProfileFBId;
    }

    /**
     * Get profileBitmap
     * @return
     */
    public Bitmap getmProfileBitmap() {
        return mProfileBitmap;
    }

    /**
     * Set profile Bitmap
     * @param mProfileBitmap
     */
    public void setmProfileBitmap(Bitmap mProfileBitmap) {
        this.mProfileBitmap = mProfileBitmap;
    }
}

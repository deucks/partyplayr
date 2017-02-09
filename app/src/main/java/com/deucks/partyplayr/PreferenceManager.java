package com.deucks.partyplayr;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for managing different preferences the user has.
 */
public class PreferenceManager {

    //Setup variables
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;
    private Context mContext;

    //Preference File Name,
    private static int PREF_PRIVATE = 0;

    /**
     * Contructor, called when contructed
     * @param context
     */
    public PreferenceManager(Context context)
    {
        this.mContext = context;
        mSharedPref = mContext.getSharedPreferences(UniversalConstants.PREF_FILE_NAME, PREF_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
    }

    /**
     * Called to store facebook login session variables
     * @param loggedIn
     * @param firstName
     * @param lastName
     * @param name
     * @param userId
     * @param profleURI
     */
    public void setFBUserSessionVariables(boolean loggedIn, String firstName,String lastName, String name, String userId, String profleURI)
    {
        mSharedPrefEditor.putBoolean(UniversalConstants.PREF_FB_LOGGED_IN, loggedIn);
        mSharedPrefEditor.putString(UniversalConstants.PREF_FB_FIRST_NAME, firstName.toString());
        mSharedPrefEditor.putString(UniversalConstants.PREF_FB_LAST_NAME, lastName.toString());
        mSharedPrefEditor.putString(UniversalConstants.PREF_FB_USER_NAME, name.toString());
        mSharedPrefEditor.putString(UniversalConstants.PREF_FB_ID, userId.toString());
        mSharedPrefEditor.putString(UniversalConstants.PREF_FB_PROFILE, profleURI.toString());
        mSharedPrefEditor.commit();
    }

    /**
     * method to recieve Facebook session variables
     * @return
     */
    public String[] getFBUserSessionVariables()
    {
        return new String[]{mSharedPref.getString(UniversalConstants.PREF_FB_FIRST_NAME, UniversalConstants.EMPTY),
                            mSharedPref.getString(UniversalConstants.PREF_FB_USER_NAME, UniversalConstants.EMPTY)};
    }

    /**
     * Method for returning just any string
     * @param constant
     * @return
     */
    public String getGeneralString(String constant)
    {
        return mSharedPref.getString(constant, UniversalConstants.EMPTY);
    }

    /**
     * Method for setting just any string
     * @param constant
     * @param value
     */
    public void setGeneralString(String constant, String value)
    {
        mSharedPrefEditor.putString(constant, value);
        mSharedPrefEditor.commit();
    }

    /**
     * Set that the app has already ran
     * @param isFirstTime
     */
    public void setFirstTimeLaunched(boolean isFirstTime)
    {
        mSharedPrefEditor.putBoolean(UniversalConstants.PREF_IS_FIRST_LAUNCH, isFirstTime);
        mSharedPrefEditor.commit();
    }

    /**
     * @return bool if first time launched
     */
    public boolean isFirstTimeLaunch() {
        return mSharedPref.getBoolean(UniversalConstants.PREF_IS_FIRST_LAUNCH, true);
    }

}

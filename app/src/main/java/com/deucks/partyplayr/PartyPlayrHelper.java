package com.deucks.partyplayr;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.List;

/**
 * Class for Helping with common functions related to PartyPlayrService
 */
public class PartyPlayrHelper {


    /**
     * Returns a rounded image
     * @param context
     * @param bitmap
     * @return
     */
    public static RoundedBitmapDrawable getRoundedBitmapImage(Context context, Bitmap bitmap)
    {
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(100.0f);
        roundedBitmapDrawable.setAntiAlias(true);

        return roundedBitmapDrawable;
    }

    /**
     * Gets the user id for the current instance
     * @param peopleModel
     * @param context
     * @return
     */
    public static String getCurrentUserId(List<PartyPeopleModel> peopleModel, Context context)
    {
        String facebookId = new PreferenceManager(context).getGeneralString(UniversalConstants.PREF_FB_ID);
        for (PartyPeopleModel pplMdl:peopleModel)
        {
            if (pplMdl.getmProfileFBId().equals(facebookId))
            {
                //Return the userid
                return pplMdl.getmProfileId();
            }
        }
        //return nothing if nothing is found
        return UniversalConstants.EMPTY;
    }

}

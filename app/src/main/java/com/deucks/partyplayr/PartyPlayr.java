package com.deucks.partyplayr;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that is created with Application
 */
public class PartyPlayr extends Application{

    //Declare Constants
    private static final String mPackageName = "com.deucks.partyplayr";
    private static final String mEncryptType = "SHA";

    /**
     * Called when class is created
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        printHashKey();
    }

    /**
     * Print the hash key for Facebook login
     */
    public void printHashKey()
    {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    mPackageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance(mEncryptType);
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}

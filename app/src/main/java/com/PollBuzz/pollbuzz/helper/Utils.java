package com.PollBuzz.pollbuzz.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {

    private static String authTokenPrefs = "authTokenSharedPreferences";

    public static void setLoginAuthToken(Context context, String authToken){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(authTokenPrefs, authToken);
        editor.apply();
    }

    public static String getAuthToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(authTokenPrefs, null);
    }


    public static void removeLoginAuthToken(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();
    }
}

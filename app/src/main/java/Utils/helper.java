package Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class helper {
    private static String profileSetUpPref = "profileSetUpSharedPreferences";

    public static void setProfileSetUpPref(Context context, Boolean profileSet){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(profileSetUpPref, profileSet);
        editor.apply();
    }

    public static Boolean getProfileSetUpPref(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(profileSetUpPref,false);
    }


    public static void removeProfileSetUpPref(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
    }
}

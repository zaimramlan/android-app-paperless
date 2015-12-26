package zieras.projectlayouts.baseclass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

import zieras.projectlayouts.LoginActivity;
import zieras.projectlayouts.MainActivity;

/**
 * Created by zieras on 22/12/2015.
 */
public class SessionManager {
    //the shared preference
    private SharedPreferences preferences;
    //editor to make changes to sharedpreferences
    private SharedPreferences.Editor editor;
    private Context context;
    //shared preference mode
    private int PRIVATE_MODE = 0;
    //shared preference name
    private static final String PREF_NAME = "PaperLessAppPref";
    //all shared preference keys
    private static final String IS_LOGGEDIN = "IsLoggedIn";
    //username of the current session
    public static final String KEY_USERNAME = "username";
    //user role of the current session
    public static final String KEY_USERROLE = "userrole";

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    //creates the logged in session
    public void createLoginSession(String username, String role) {
        //sets the current logged in state to true
        editor.putBoolean(IS_LOGGEDIN, true);
        //puts the username of the current session
        editor.putString(KEY_USERNAME, username);
        //puts the user role of the current session
        editor.putString(KEY_USERROLE, role);
        //commits all changes after putting values
        editor.commit();
    }

    //returns the user details of the current session
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        //acquire the details from the sharedpreferences
        user.put(KEY_USERNAME, preferences.getString(KEY_USERNAME, null));
        user.put(KEY_USERROLE, preferences.getString(KEY_USERROLE, null));
        return user;
    }

    //checks the current logged in status
    //if it is false, the user will be redirected to the login page (MainActivity)
    public void checkLogin() {
//        editor.putBoolean(IS_LOGGEDIN, false);
//        editor.commit();
        Log.d("LOGGEDIN", " " + isLoggedIn());

        if(!this.isLoggedIn()) {
            Intent intent = new Intent(context, LoginActivity.class);
            //close all other activity(s) related to the app
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //adds a new flag to start a new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    //logs out the current user and clears all session details
    public void logoutUser() {
        //clear all session details
        editor.clear();
        editor.commit();

        //redirects the user to the login page (MainActivity) after logging out
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //returns the current logged in status
    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGEDIN, false);
    }
}

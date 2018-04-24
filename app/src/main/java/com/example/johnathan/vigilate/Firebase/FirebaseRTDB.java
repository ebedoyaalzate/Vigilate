package com.example.johnathan.vigilate.Firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;

import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.channels.NonReadableChannelException;


/**
 * Created by JohnathanMB on 30/10/2017.
 */

public class FirebaseRTDB {
    private SharedPreferences sharedPreferencesSettings;


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private DatabaseReference newHelp = database.getReference(FirebaseRefencesRTDB.NEW_HELP);

    public void addNewHelp(String idUser, double lat, double longit){
        DatabaseReference newHelp = database.getReference(FirebaseRefencesRTDB.NEW_HELP+"/"+idUser);
        newHelp.child(New_Help.FIELD_LAT).setValue(lat);
        newHelp.child(New_Help.FIELD_LONG).setValue(longit);
        /*
        DatabaseReference newHelpAdded = newHelp.push();
        newHelpAdded.child(New_Help.FIELD_LAT).setValue(lat);
        newHelpAdded.child(New_Help.FIELD_LONG).setValue(longit);
        */
    }

    public DatabaseReference getNewHelp(){
        return newHelp;
    }

    public void deleteNewHelp(Context context, String childToDelete){
        sharedPreferencesSettings = context.getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, context.MODE_PRIVATE);
        Double lat = Double.parseDouble(sharedPreferencesSettings.getString(ReferencesSettings.LAT_LOCAL,"0"));
        Double longi = Double.parseDouble(sharedPreferencesSettings.getString(ReferencesSettings.LONG_LOCAL,"0"));

        DatabaseReference history = database.getReference(FirebaseRefencesRTDB.HISTORY).push();
        history.child(New_Help.ID_USER).setValue(childToDelete);
        history.child(New_Help.FIELD_LAT).setValue(lat);
        history.child(New_Help.FIELD_LONG).setValue(longi);

        DatabaseReference newHelp = database.getReference(FirebaseRefencesRTDB.NEW_HELP+"/"+childToDelete);
        newHelp.setValue(null);
    }

}

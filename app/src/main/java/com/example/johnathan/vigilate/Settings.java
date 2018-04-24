package com.example.johnathan.vigilate;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;


public class Settings extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner idioma;
    private Spinner sonido;
    private String idiomas[] ={"Ingles","Español"};
    private String sonidos[] ={"Ocean","Morning","Mystic"};
    private Button btnActiveNotification;
    SharedPreferences sharedPreferencesSettings;
    SharedPreferences.Editor editorSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_v2);
        btnActiveNotification = (Button) findViewById(R.id.btnActiveNotification);
        selectTextButton();


        btnActiveNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtnNotification();
            }
        });
    }

    public void selectTextButton(){
        sharedPreferencesSettings = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
        boolean booleanBtnNotifiacionActived = sharedPreferencesSettings.getBoolean(ReferencesSettings.NOTIFICATION_ACTIVED,true);
        if (booleanBtnNotifiacionActived){

            btnActiveNotification.setText("Desactivar");
        }else {

            btnActiveNotification.setText("Activar");
        }
    }

    private void onClickBtnNotification(){
        sharedPreferencesSettings = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, MODE_PRIVATE);
        boolean booleanBtnNotifiacionActived = sharedPreferencesSettings.getBoolean(ReferencesSettings.NOTIFICATION_ACTIVED,true);
        if (booleanBtnNotifiacionActived){
            editorSettings = sharedPreferencesSettings.edit();
            editorSettings.putBoolean(ReferencesSettings.NOTIFICATION_ACTIVED,false);
            btnActiveNotification.setText("Desactivar");
        }else {
            editorSettings = sharedPreferencesSettings.edit();
            editorSettings.putBoolean(ReferencesSettings.NOTIFICATION_ACTIVED,true);
            btnActiveNotification.setText("Activar");
        }
        editorSettings.commit();
    }
}

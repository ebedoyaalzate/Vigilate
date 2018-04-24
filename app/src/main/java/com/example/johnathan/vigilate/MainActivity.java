package com.example.johnathan.vigilate;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.johnathan.vigilate.Broadcasts.BtnDetected;
import com.example.johnathan.vigilate.Firebase.FirebaseRTDB;
import com.example.johnathan.vigilate.PreferenceReferences.ReferencesSettings;
import com.example.johnathan.vigilate.Services.ServiceLocationGPS;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.Reference;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private ImageView photoImageView;
    private TextView nameTextView;
    private GoogleApiClient googleApiClient;
    private Switch switch1;
    private TextView mensaje;
    private TextView activar;
    private Toolbar toolbar;
    private BroadcastReceiver btnDetected;
    private IntentFilter intentFilter;
    private Button btnStopAlarm;
    private BtnDetected btnDetectedSettings;
    private SharedPreferences sharedPreferencesSettings;
    private SharedPreferences.Editor editorSettings;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;



    @Override
    protected void onStart() {
        super.onStart();

        //firebaseAuth.addAuthStateListener(firebaseAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //inicializo el broadcast boot
        sendBroadcastBoot();

        //inicializo el sharedPeferenceSettings
        sharedPreferencesSettings = getSharedPreferences(ReferencesSettings.NAME_SHAREDPREFERENCE_SETTING, this.MODE_PRIVATE);
        //inicialiizo el editor de sharedPrefereceSettings
        editorSettings= sharedPreferencesSettings.edit();
        editorSettings.putBoolean(ReferencesSettings.NAME_BTNDETECTED_ACTIVED,false);
        editorSettings.commit();

        btnStopAlarm = (Button) findViewById(R.id.btnStopAlarm);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        switch1 =  findViewById(R.id.switch1);
        mensaje= findViewById(R.id.mensaje);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    //se activa la función para dectectar secuencia de botón
                    editorSettings.putBoolean(ReferencesSettings.NAME_BTNDETECTED_ACTIVED, true);
                    editorSettings.commit();

                    mensaje.setText("En caso de emergencia presiona 1 vez cualquier tecla de volumen");
                    mensaje.setBackground(getResources().getDrawable(R.drawable.gradient_message_detected_actived));

                }else{

                    //se desactiva la función para detectar secuencia de botón
                    editorSettings.putBoolean(ReferencesSettings.NAME_BTNDETECTED_ACTIVED, false);
                    editorSettings.commit();

                    mensaje.setText("Estás desprotegido, ACTÍVAME");
                    mensaje.setBackground(getResources().getDrawable(R.drawable.gradient_message_detected_no_actived));


                }
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            nameTextView.setText(account.getDisplayName());
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .into(photoImageView);
            String idUserLocal = account.getId();
            editorSettings.putString(ReferencesSettings.ID_USER_LOCAL,idUserLocal);
            editorSettings.commit();
            Log.d("MIAPP", account.getPhotoUrl().toString());
        }else{
            goLogInScreen();
        }

    }

    private void goLogInScreen() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    public  boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.menuSettings:
                //opciones settings
                settings();
                break;
            case R.id.menuLogOut:
                //opciones singOut
                logOut();
                break;

        }

        return true;
    }


    public void logOut() {
        //firebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()) {
                    goLogInScreen();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void revoke(View view) {
        //firebaseAuth.signOut();
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()) {
                    goLogInScreen();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo revocar el acceso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void location(){
        Intent intents = new Intent(this, MapsActivity.class);
        startActivity(intents);
    }

    public void settings(){
        Intent intents = new Intent(this, Settings.class);
        startActivity(intents);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sendBroadcastBoot(){
        Intent intent = new Intent("START_SERVICE_LISTENER");
        sendBroadcast(intent);
    }

    public void registerBtnDetected(){
        btnDetected = new BtnDetected();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        this.registerReceiver(btnDetected, intentFilter);
    }

    public void stopServiceLocationGps(){
        Intent intentServiceLocation = new Intent(this, ServiceLocationGPS.class);
        stopService(intentServiceLocation);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStopAlarm:
                boolean btnActive = sharedPreferencesSettings.getBoolean(ReferencesSettings.BTN_STOPSERVICE_ACTIVED,false);
                if(btnActive){
                    Toast.makeText(this,"Se ha descativado la alarma",Toast.LENGTH_SHORT).show();
                    stopServiceLocationGps();
                    //desactivo la función de este botón
                    editorSettings.putBoolean(ReferencesSettings.BTN_STOPSERVICE_ACTIVED,false);
                    //desactivo la actualización de la ubicación gps
                    editorSettings.putBoolean(ReferencesSettings.UPDATE_LOCATION_ACTIVED,false);
                    editorSettings.putBoolean(ReferencesSettings.ALERT_SENT, false);
                    editorSettings.commit();
                    String idLocal = sharedPreferencesSettings.getString(ReferencesSettings.ID_USER_LOCAL,"");
                    if(!idLocal.equals("")){
                        FirebaseRTDB firebaseRTDB = new FirebaseRTDB();
                        firebaseRTDB.deleteNewHelp(this,idLocal);
                    }
                }else{
                    Toast.makeText(this,"Actualmente NO tiene alarmas Activadas",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setUserdata(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        String correo = user.getEmail();
        String token = user.getUid();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(photoImageView);
    }


}

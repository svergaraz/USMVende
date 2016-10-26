package cl.telematica.android.usmvende;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class menu extends AppCompatActivity {
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    Button btnVender;
    Button btnComprar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().endsWith("REGISTRATION_SUCCESS")){
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token :" + token, Toast.LENGTH_LONG).show();
                }
                else if (intent.getAction().equals("REGISTRATION_ERROR")){
                    Toast.makeText(getApplicationContext(), "GCM Registration Error", Toast.LENGTH_LONG).show();
                }
                else{};
            }
        };
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(),"Google Play Service is not install/enabled in this device", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode,getApplicationContext());
            }
            else {
                Toast.makeText(getApplicationContext(),"This device does not support for Google Play Services", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Intent i = new Intent(this, RegistrationService.class);
            startService(i);
        }
        */
        btnComprar = (Button) findViewById(R.id.btnComprador);
        btnVender = (Button) findViewById(R.id.btnVendedor);

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this,RegistroProducto.class);
                startActivity(intent);
            }
        });
        /*
        Intent i = new Intent(this, RegistrationService.class);
        startService(i);
        */

    }
    /*
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("REGISTRATION_SUCCESS"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("REGISTRATION_ERROR"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
    */

}

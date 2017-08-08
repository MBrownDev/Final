package com.example.brown.afinal;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoPage extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    Boolean pass;

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_page);

        pass = getSharedPreferences("logInfo",MODE_PRIVATE).getBoolean("status", false);

        new Handler().postDelayed(new Runnable() {
            /*

             * Showing splash screen with a timer. This will be useful when you

             * want to show case your app logo / company

             */
            @Override

            public void run() {

                // This method will be executed once the timer is over

                // Start your app main activity

                if (!pass) {

                    Intent intent = new Intent(LogoPage.this, PasswordScreen.class);
                    startActivity(intent);
                } else if(pass){

                    Intent i = new Intent(LogoPage.this, MainActivity.class);
                    startActivity(i);
                    finish();
//                    if (isMyServiceRunning(MessagingService.class) == false) {
//
//                        Intent i = new Intent(LogoScreen.this, MainActivity.class);
//
//                        startActivity(i);
//
//                        // close this activity
//                        finish();
//                    } else if (isMyServiceRunning(MessagingService.class) == true){
//                        Intent i = new Intent(LogoScreen.this, KP.class);
//
//                        startActivity(i);
//                    }
                }
            }

        }, SPLASH_TIME_OUT);
    }
}

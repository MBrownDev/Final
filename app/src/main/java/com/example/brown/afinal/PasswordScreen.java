package com.example.brown.afinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordScreen extends AppCompatActivity {

    contactDatabase cdHelper;
    SQLiteDatabase db;
    public SharedPreferences preferences;
    public SharedPreferences.Editor edit;

    EditText password,confirm;
    String pw,cn;
    Button input;

    private void openDB(){
        cdHelper = new contactDatabase(this);
        db = cdHelper.helper.getWritableDatabase();
        cdHelper.open();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_screen);

        openDB();
        password = (EditText)findViewById(R.id.password);
        confirm = (EditText)findViewById(R.id.confirm);
        input = (Button)findViewById(R.id.input);
    }

    public void inputPassword(View view){
        pw = password.getText().toString();
        cn = confirm.getText().toString();

        if(pw.equals(cn)){
            cdHelper.insertPassword(pw);
            FirstOpen(true);
            Intent i = new Intent(PasswordScreen.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void FirstOpen(Boolean open){

        preferences = getSharedPreferences("logInfo", MODE_PRIVATE);
        edit = preferences.edit();
        edit.putBoolean("status",open);
        edit.apply();
        //preferences = getSharedPreferences("logInfo", MODE_PRIVATE).getString("password",password);
    }
}

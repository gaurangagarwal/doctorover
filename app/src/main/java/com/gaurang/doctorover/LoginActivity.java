package com.gaurang.doctorover;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.gaurangpc.doctor.R;

public class LoginActivity extends AppCompatActivity{

    DoctorDbHandler doctorDbHandler;
    EditText userNameET;
    EditText passET;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPref = getSharedPreferences("userInfo" , Context.MODE_PRIVATE);
        if(sharedPref.getString("Username", "") != "" && sharedPref.getString("Password", "")!= "") {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("launchNecessary" , false);
            startActivity(intent);
        }

//        doctorDbHandler = new DoctorDbHandler(this ,null, null,1);
//        TextView tv = (TextView)findViewById(R.id.databasePrint);
//        String databaseString = doctorDbHandler.databaseToString();
//        tv.setText(databaseString);
//
        userNameET = (EditText) findViewById(R.id.usernameInput);
        passET = (EditText) findViewById(R.id.passwordInput);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }


    public void openSignUpActivity(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void loginBtnPressed(View view) {
        String inputUserName = userNameET.getText().toString();
        String inputPass = passET.getText().toString();
        new DoctorDbHandler.FindDoctor(this, getSharedPreferences("userInfo", Context.MODE_PRIVATE), findViewById(android.R.id.content))
                                .execute(inputUserName, inputPass);

//        String name = doctorDbHandler.findDoctor(inputUserName, inputPass);
//        if(name == null) {
//            Toast.makeText(getApplicationContext() , "Incorrect Entry", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            Toast.makeText(getApplicationContext() , "Login Done : "+name, Toast.LENGTH_SHORT).show();
//        }
//        saveInfo(inputUserName , inputPass);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    // this is used to save the data input by user
//    private void saveInfo(String inputUserName , String inputPass) {
//        SharedPreferences sharedPref= getSharedPreferences("userInfo" , Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("Username" , inputUserName);
//        editor.putString("Password" , inputPass);
//        editor.apply();
//    }
}

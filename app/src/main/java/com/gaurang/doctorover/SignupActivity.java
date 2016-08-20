package com.gaurang.doctorover;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import java.io.IOException;

public class SignupActivity extends AppCompatActivity {


    EditText emailEditText;
    EditText passEditText;
    EditText nameEditText;
    EditText phoneEditText;
    RadioGroup genderRadioGroup;

    //    find
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        emailEditText = (EditText) findViewById(R.id.signUpEmail);
        passEditText = (EditText) findViewById(R.id.signUpPass);
        nameEditText = (EditText) findViewById(R.id.signUpName);
        phoneEditText = (EditText) findViewById(R.id.signUpPhone);
        genderRadioGroup = (RadioGroup) findViewById(R.id.signUpGender);
    }

    public void signUpBtnClick(View view) throws IOException {
        String email = emailEditText.getText().toString();
        String pass = passEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        int genderID = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton genderRadioBtn = (RadioButton) findViewById(genderID);
        String gender = genderRadioBtn.getText().toString();

        if(email.equals("") || pass.equals("") || name.equals("") || phone.equals("")) {
            Toast.makeText(getApplicationContext() , "Please, fill all fields", Toast.LENGTH_LONG).show();
            return;
        }


        String str = name+" "+pass+" "+ email+" "+phone+" "+gender;

//        emailEditText.setText(str);
        Doctor doctor = new Doctor(name, gender, email, phone, pass);

//        DoctorDbHandler doctorDbHandler = new DoctorDbHandler();

        new DoctorDbHandler.Insert(doctor, this).execute();
//        String msg  = doctorDbHandler.addDoctor(doctor);
//        String msg = addDoctor(doctor);
//        Log.d("MainActivity", msg);
//        Toast toast = new Toast(getApplicationContext(this), )
    }
    private static String LOG_TAG = "MainActivity";
    private static final String pcIP = "192.168.43.149";
    private static String ADD_DOCTOR_LINK = "http://"+pcIP+"/upload/index.php";

}

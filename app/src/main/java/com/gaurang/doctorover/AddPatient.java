package com.gaurang.doctorover;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import java.util.ArrayList;
import java.util.List;

public class AddPatient extends AppCompatActivity implements Handler.Callback {

    Spinner groupNameSpinner;
    General_Info general_info;
    ArrayList<Group> groupsArr;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        general_info = new General_Info(this);
        general_info.logMsg("Add patient Activity Started");
        handler = new Handler(this);

        groupNameSpinner = (Spinner) findViewById(R.id.addPatientInputGroupName);
        new GroupDBHandler.ShowGroups(this, handler, (ProgressBar) findViewById(R.id.editPatientGroupNameProgressBar)).execute(general_info.getUserLoggedId());

        // Set Margin (used in case of multi line comments in diag)
//        TextView consultHead = (TextView) findViewById(R.id.addPatientConsult);
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//
//
//        p.addRule(RelativeLayout.BELOW, R.id.addPatientInputDiag);
//        consultHead.setLayoutParams(p);

    }

    //Used to show list of groups in Add patient Activity in Spinner
    @Override
    public boolean handleMessage(Message msg) {
        ArrayList<Group> result = new ArrayList<>();
        switch (msg.what) {
            case GroupDBHandler.SHOW_GROUP_NAMES_FINISHED:
                // Let's see what we are having for dessert
                result = (ArrayList<Group>) msg.obj;
                break;
        }
        groupsArr = result;
        if(result.size() ==0) {
            if(general_info.isInternetConnected() == false) {
                general_info.showMessageInSnackBar(findViewById(android.R.id.content), "No Internet Connection");
            } else {
                general_info.showMessageInSnackBar(findViewById(android.R.id.content), "Create Group before Adding Patient");
            }
// Snackbar.make(findViewById(android.R.id.content) , "No Internet Connection", Snackbar.LENGTH_LONG).show();
            return false;
//            Toast.makeText(this, "Create a group to Add a patient" , Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
        }

        List<String> groupNamesArr = new ArrayList<String>();
        for (int i=0; i < result.size() ; i++){
            groupNamesArr.add(result.get(i).getName());
        }

        ArrayAdapter<String>  dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupNamesArr);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSpinner.setAdapter(dataAdapter);

        return false;
    }


    public void addPatientSaveBtnPressed(View view) {
        if(general_info.isInternetConnected() == false){
            general_info.showMessageInSnackBar(findViewById(android.R.id.content) , "No Internet Connection");
            return;
        }
        TextView nameTV = (TextView) findViewById(R.id.addPatientInputName);
        RadioGroup genderRG = (RadioGroup) findViewById(R.id.addPatientGender);
        TextView phoneTV = (TextView) findViewById(R.id.addPatientInputPhone);
        TextView ageTV = (TextView) findViewById(R.id.addPatientInputAge);
        TextView regTv = (TextView) findViewById(R.id.addPatientInputRegNo);
        TextView bedTV = (TextView) findViewById(R.id.addPatientInputBedNo);
        TextView roomTV = (TextView) findViewById(R.id.addPatientInputRoomNo);
        TextView consultTV = (TextView) findViewById(R.id.addPatientInputConsult);
        TextView diagTV = (TextView) findViewById(R.id.addPatientInputDiag);
        Spinner groupSP = (Spinner) findViewById(R.id.addPatientInputGroupName);

        int groupNamePosition = groupSP.getSelectedItemPosition();
        String groupName = groupSP.getItemAtPosition(groupNamePosition).toString();
        String groupID = groupsArr.get(groupNamePosition).getID();

        Group groupSelected = new Group(groupID , groupName);

//        Toast.makeText(getApplicationContext() , groupNamePosition +"  "+ groupsArr.get(groupNamePosition).getName(),Toast.LENGTH_SHORT).show();

        int genderID = genderRG.getCheckedRadioButtonId();
        RadioButton genderRB = (RadioButton) findViewById(genderID);
        String name = nameTV.getText().toString();
        String gender = genderRB.getText().toString();
        String diag = diagTV.getText().toString();
        String consult = consultTV.getText().toString();

//        String str = nameTV.getText().toString() + "  " + gender+"  " +  groupName + "  " + bedTV.getText().toString() + "  "+
//                      regTv.getText().toString() +"  "+  roomTV.getText().toString() + "  " + consultTV.getText().toString() +"  "+
//                        diagTV.getText().toString();

        PatientProfile patient = new PatientProfile(null, name , gender , ageTV.getText().toString() , phoneTV.getText().toString(),
                                groupSelected, regTv.getText().toString() , bedTV.getText().toString(), roomTV.getText().toString(),
                                    diag , consult);

        if(name.matches("") || diag.matches("") || consult.matches("")) {
            Toast.makeText(this, "Name, Diag. and Consult. are required fields" ,Toast.LENGTH_SHORT).show();
            return;
        }

//        general_info.logMsg("Name : "+nameTV.getText() + " Age : " + ageTV.getText());

        new PatientProfileDBHandler.addPatient(this , patient).execute();
//        Toast.makeText(getApplicationContext() ,str , Toast.LENGTH_SHORT ).show();
    }


}

package com.gaurang.doctorover;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import java.util.ArrayList;
import java.util.List;

public class AddObsv extends AppCompatActivity {

    private static String patientID;
    private static String patientName;
//    private static PatientObsv patientObsv = null;
    public static final String TEMPERATURE_DEFAULT = "Select Temp";
    public static final String DEFAULT_VALUE = "";
    public static final String CONDITION_DEFAULT = "Select Condition";
    public static final String CONDITION_CRITICAL = "Critical";
    public static final String CONDITION_SICK = "Sick";
    public static final String CONDITION_NORMAL = "Normal";
    public static final String RESPIRATION_DEFAULT = "Select Resp";
    public static final String OPERATIVE_DEFAULT = "Select operative";
    public static final String OPERATIVE_PRE = "Preoperative";
    public static final String OPERATIVE_POST = "Postoperative";
    public static final String OPERATIVE_OTHERS = "Others";
    ListView genExamListView;
    ArrayList<String> genExamArrayList = new ArrayList<>();

    private static General_Info general_info = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_obsv);
        general_info = new General_Info(this);

        patientID = general_info.getPatientID();
        patientName = general_info.getPatientName();

        TextView patientNameHead = (TextView) findViewById(R.id.addObservationsNameHead);
        patientNameHead.setText(patientName);
        createArraysForSpinners();

        genExamArrayList.add("Anemia");
        genExamArrayList.add("Jaundice");
        genExamArrayList.add("Ictrus");
        genExamArrayList.add("LNpathy");
        genExamArrayList.add("Pulmonary Edema");
        genExamArrayList.add("Cynosis");
        genExamListView = (ListView) findViewById(R.id.addObsvGenExamOptionsListView);
        addValuesToListView(genExamListView, genExamArrayList);
    }




    // if value is in default state , change value to DEFAULT_VAL
    private String checkDefaultVal(String val, String defaultVal) {
        if(val.equals(defaultVal)) {
            return DEFAULT_VALUE;
        }
        return val;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_obsv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addObsvSaveBtn) {
            if(general_info.isInternetConnected() == false) {
                general_info.showMessageInSnackBar(findViewById(android.R.id.content) , "No Internet Connection");
                return false;
            }

            PatientObsv patientObsv =  findValuesFromTextFields();
            if(patientObsv.getCond().equals("")) {
                Toast.makeText(this, "Condition is required field" , Toast.LENGTH_SHORT).show();
            } else {
                new PatientObsvDBHandler.AddObsv(this, patientObsv).execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private PatientObsv findValuesFromTextFields() {
        Spinner condSP = (Spinner) findViewById(R.id.addObsvConditionSpinner);
        Spinner operativeSP = (Spinner) findViewById(R.id.addObsvOperativeSpinner);
        TextView bloodTV = (TextView) findViewById(R.id.addObsvBloodInvInput);
        TextView radioTV = (TextView) findViewById(R.id.addObsvRadioInvInput);
        Spinner tempSP = (Spinner) findViewById(R.id.addObsvTempSpinner);
        TextView pulseTV = (TextView) findViewById(R.id.addObsvPulseInput);
        Spinner respSP = (Spinner) findViewById(R.id.addObsvRespSpinner);
        TextView genExamTV = (TextView) findViewById(R.id.addObsvGenExamInput);
        TextView chestTV = (TextView) findViewById(R.id.addObsvChestInput);
        TextView abdomenTV = (TextView) findViewById(R.id.addObsvAbdomenInput);
        TextView hernialGenitalsTV = (TextView) findViewById(R.id.addObsvHernialGenitalsInput);
        TextView adviceTV = (TextView) findViewById(R.id.addObsvAdviceInput);
        TextView priorOrdTV = (TextView) findViewById(R.id.addObsvPriorOrdInput);

        String genExamVal = "";
        for (int i=0;i<genExamArrayList.size(); i++) {
            RelativeLayout relativeLayout = (RelativeLayout) genExamListView.getChildAt(i);
            if(relativeLayout != null) {
                CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                if (checkBox.isChecked()) {
                    genExamVal += checkBox.getText() + "__";
                }
            }
        }
//        genExamVal = genExamVal.substring(0, genExamVal.length()-2); // remove last 2 __

        String condVal = condSP.getItemAtPosition(condSP.getSelectedItemPosition()).toString();
        String operativeVal = operativeSP.getItemAtPosition(operativeSP.getSelectedItemPosition()).toString();
        String bloodVal = bloodTV.getText().toString();
        String radioVal = radioTV.getText().toString();
        String tempVal = tempSP.getItemAtPosition(tempSP.getSelectedItemPosition()).toString();
        String pulseVal = pulseTV.getText().toString();
        String respVal = respSP.getItemAtPosition(respSP.getSelectedItemPosition()).toString();
//        if(genExamTV.getText().toString().equals("") == false) {
        genExamVal += genExamTV.getText().toString();
//        }
        if(genExamVal.length()  > 2) {
            if(genExamVal.charAt(genExamVal.length()-1) == '_' && genExamVal.charAt(genExamVal.length()-2) == '_') {
                genExamVal = genExamVal.substring(0, genExamVal.length()-2); // remove last 2 __
            }
        }

//        genExamVal = genExamVal.substring(0, genExamVal.length()-2); // removing last 2 "__"
//        Toast.makeText(this, genExamVal , Toast.LENGTH_SHORT).show();


        String chestVal = chestTV.getText().toString();
        String abdomenVal = abdomenTV.getText().toString();
        String hernialGenitalsVal = hernialGenitalsTV.getText().toString();
        String adviceVal = adviceTV.getText().toString();
        String priorOrdVal = priorOrdTV.getText().toString();




        // check if these values are in default state i.e. Select ...
        // For Spinners only
        condVal = checkDefaultVal(condVal, CONDITION_DEFAULT);
        operativeVal = checkDefaultVal(operativeVal , OPERATIVE_DEFAULT);
        tempVal = checkDefaultVal(tempVal , TEMPERATURE_DEFAULT);
        respVal = checkDefaultVal(respVal , RESPIRATION_DEFAULT);


        General_Info general_info = new General_Info(this);
        PatientObsv patientObsv = new PatientObsv(null , patientID, condVal, operativeVal, bloodVal, radioVal, tempVal,
                pulseVal, respVal, genExamVal, chestVal, abdomenVal , hernialGenitalsVal ,
                adviceVal , priorOrdVal, general_info.getUserLoggedId(), null);
        return patientObsv;
    }

    // creates array for 3 different spinner using in this activity
    private void createArraysForSpinners() {
        Spinner conditionSpinner = (Spinner) findViewById(R.id.addObsvConditionSpinner);
        Spinner operativeSpinner = (Spinner) findViewById(R.id.addObsvOperativeSpinner);
        Spinner tempSpinner = (Spinner) findViewById(R.id.addObsvTempSpinner);
        Spinner respSpinner = (Spinner) findViewById(R.id.addObsvRespSpinner);

        List<String> conditionArr = new ArrayList<String>();
        conditionArr.add(CONDITION_DEFAULT);
        conditionArr.add(CONDITION_NORMAL);
        conditionArr.add(CONDITION_SICK);
        conditionArr.add(CONDITION_CRITICAL);

        List<String> operativeArr = new ArrayList<>();
        operativeArr.add(OPERATIVE_DEFAULT);
        operativeArr.add(OPERATIVE_PRE);
        operativeArr.add(OPERATIVE_POST);
        operativeArr.add(OPERATIVE_OTHERS);


        List<String> tempArr = new ArrayList<String>();
        tempArr.add(TEMPERATURE_DEFAULT);
        tempArr.add("Febrile");
        tempArr.add("Afebrile");

        List<String> respArr = new ArrayList<String>();
        respArr.add(RESPIRATION_DEFAULT);
        respArr.add("Normal");
        respArr.add("Abnormal");

        addValuesToSpinner(conditionSpinner, conditionArr);
        addValuesToSpinner(operativeSpinner, operativeArr);
        addValuesToSpinner(tempSpinner, tempArr);
        addValuesToSpinner(respSpinner , respArr);


    }

    // Attaches values to  spinners
    private void addValuesToSpinner(Spinner spinner, List<String> arr) {
        ArrayAdapter<String>  dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arr);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }
    private void addValuesToListView(ListView listView , ArrayList<String> arr) {
        ArrayAdapter<String> arrayAdapter = new MyCustomAdapter(this, 0, arr);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

//    public void genExamListViewVisible(View view) {
//    }

    public void genExamListViewVisible(View view) throws InterruptedException {
        RelativeLayout rr = (RelativeLayout) findViewById(R.id.addObsvGenExamOptionsRR);
        ImageView arrowImg = (ImageView) findViewById(R.id.addObsvGenExamExpandImg);
        TextView arrowTV = (TextView) findViewById(R.id.addObsvGenExamExpandHead);
        if(genExamListView.getVisibility() == View.GONE){
            arrowTV.setText("Collapse");
            arrowImg.setImageResource(R.drawable.collapse_arrow);
            genExamListView.setVisibility(View.VISIBLE);
            genExamListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_in));
            rr.setVisibility(View.VISIBLE);
            rr.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_in));
        } else {
            arrowTV.setText("Expand");
            arrowImg.setImageResource(R.drawable.expand_arrow);
            rr.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_out));
            genExamListView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right_out));
            rr.setVisibility(View.GONE);
            genExamListView.setVisibility(View.GONE);
        }
    }

    // puts content to the list view
    private class MyCustomAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> optionList;
        public MyCustomAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.context = context;
            this.optionList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String option = optionList.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.friend_listview, null);

            TextView subHeadTV = (TextView) view.findViewById(R.id.friendToGroupHeader);
            subHeadTV.setVisibility(View.GONE);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.friendToGroupCheckBox);


//            SpannableString ss1 = new SpannableString(option);
//            ss1.setSpan(new RelativeSizeSpan(0.8f), 0 , ss1.length(),0);
            SpannableString ss2 = new SpannableString(option);
            ss2.setSpan(new RelativeSizeSpan(0.7f), 0 , ss2.length(),0);
            ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF909090")), 0, ss2.length(), 0);

            checkBox.setText("");
//            checkBox.append(ss1);
//            checkBox.append("\n");
            checkBox.append(ss2);


//            checkBox.setText(option);
//            checkBox.setTag(doctor.get_id());
            return view;
        }
    }

}

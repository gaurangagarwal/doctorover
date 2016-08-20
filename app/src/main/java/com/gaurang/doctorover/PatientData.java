package com.gaurang.doctorover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.gaurangpc.doctor.R;

import java.util.ArrayList;

// Activity to show the Observations
public class PatientData extends AppCompatActivity implements TaskCompleted<ArrayList<PatientObsv>> {

    private static General_Info general_info = null;
    private static ArrayList<PatientObsv> patientObsvArrList;  // overall array list of patient obsv
    private static int itemInSubList = 5; // size to load at a time
    private static ListView listView;
    private static Button btnLoadMore;
//    public static int PATIENT_REQUEST_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);
        general_info = new General_Info(this);

        patientObsvArrList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.patientDataListView);
        btnLoadMore = new Button(this);

        btnLoadMore.setText("Load More");
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                new PatientObsvDBHandler.ShowObsv(arg0.getContext()).
                        execute(String.valueOf(patientObsvArrList.size()), String.valueOf(itemInSubList));
            }
        });
        listView.addFooterView(btnLoadMore);


        setTitle(general_info.getPatientName());
        new PatientObsvDBHandler.ShowObsv(this).execute(String.valueOf(0), String.valueOf(itemInSubList));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.patientDataAddDataBtn) {
            Intent intent = new Intent(this, AddObsv.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskComplete(ArrayList<PatientObsv> result) {
        general_info.logMsg("Internet Connected : "+ general_info.isInternetConnected());
        if(result.size() <itemInSubList || general_info.isInternetConnected()==false) { // remove Btn if we have less number of results from expected value
            listView.removeFooterView(btnLoadMore);
        }

        // set scroll position
        int currentPosition = listView.getLastVisiblePosition();
//        float currentMoveFromTop =  listView.getY();

        patientObsvArrList.addAll(result); // add new results

        ArrayAdapter<PatientObsv> patientObsvArrayAdapter = new MyCustomArrayAdapter(this, 0, patientObsvArrList);

        if(result.size() == 0) { // if no results
            listView.setEmptyView(null);
        }
        listView.setAdapter(patientObsvArrayAdapter);
        listView.setSelectionFromTop(currentPosition, 0);
    }




    class MyCustomArrayAdapter extends ArrayAdapter<PatientObsv> {
        Context context;
        ArrayList<PatientObsv> patientObsvArrayList;
        public MyCustomArrayAdapter(Context context, int resource, ArrayList<PatientObsv> result) {
            super(context, resource, result);
            this.context = context;
            this.patientObsvArrayList = result;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PatientObsv patientObsv = patientObsvArrayList.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.obsv_listview, null);
//            view.setBackgroundColor();
            TextView condTV = (TextView) view.findViewById(R.id.obsvListCondVal);
            TextView operativeTV = (TextView) view.findViewById(R.id.obsvListOperativeVal);
            TextView bloodTV = (TextView) view.findViewById(R.id.obsvListBloodVal);
            TextView radioTV = (TextView) view.findViewById(R.id.obsvListRadioVal);
            TextView tempTV = (TextView) view.findViewById(R.id.obsvListTempVal);
            TextView pulseTV = (TextView) view.findViewById(R.id.obsvListPulseVal);
            TextView respTV = (TextView) view.findViewById(R.id.obsvListRespVal);
            TextView genExamTV = (TextView) view.findViewById(R.id.obsvListGenExamVal);
            TextView chestTV = (TextView) view.findViewById(R.id.obsvListChestVal);
            TextView abdomenTV = (TextView) view.findViewById(R.id.obsvListAbdomenVal);
            TextView hernialGenitalsTV = (TextView) view.findViewById(R.id.obsvListHernialsGenitalsVal);
            TextView adviceTV = (TextView) view.findViewById(R.id.obsvListAdviceVal);
            TextView priorOrdTV = (TextView) view.findViewById(R.id.obsvListPriorOrdVal);
            TextView doctorNameTV = (TextView) view.findViewById(R.id.obsvListDoctorNameVal);
            TextView dateTimeTV = (TextView) view.findViewById(R.id.obsvListDateTimeVal);
//            priorOrdTV.setText("Abcd");
//            general_info.logMsg("Respiration in patient Data : "+ patientObsv.getResp());
            setValueToTextView(condTV, patientObsv.getCond(), (TableRow) view.findViewById(R.id.obsvListCondTR));
            setValueToTextView(operativeTV, patientObsv.getOperative(), (TableRow) view.findViewById(R.id.obsvListOperativeTR));
            setValueToTextView(bloodTV , patientObsv.getBlood() , (TableRow)view.findViewById(R.id.obsvListBloodTR));
            setValueToTextView(radioTV , patientObsv.getRadio() , (TableRow)view.findViewById(R.id.obsvListRadioTR));
            setValueToTextView(tempTV , patientObsv.getTemp() , (TableRow)view.findViewById(R.id.obsvListTempTR));
            setValueToTextView(pulseTV , patientObsv.getPulse() , (TableRow)view.findViewById(R.id.obsvListPulseTR));
            setValueToTextView(respTV , patientObsv.getResp() , (TableRow)view.findViewById(R.id.obsvListRespTR));
            setValueToTextView(genExamTV , patientObsv.getGenExam() , (TableRow)view.findViewById(R.id.obsvListGenExamTR));
            setValueToTextView(chestTV , patientObsv.getChest(), (TableRow)view.findViewById(R.id.obsvListChestTR));
            setValueToTextView(abdomenTV , patientObsv.getAbdomen(), (TableRow)view.findViewById(R.id.obsvListAbdomenTR));
            setValueToTextView(hernialGenitalsTV , patientObsv.getHernialsGenitals(), (TableRow)view.findViewById(R.id.obsvListHernialsGenitalsTR));
            setValueToTextView(adviceTV , patientObsv.getAdvice(), (TableRow)view.findViewById(R.id.obsvListAdviceTR));
            setValueToTextView(priorOrdTV , patientObsv.getPriorOrd(), (TableRow)view.findViewById(R.id.obsvListPriorOrdTR));

            setValueToTextView(doctorNameTV , patientObsv.getDoctorName(), (TableRow)view.findViewById(R.id.obsvListDoctorDateTR));
            setValueToTextView(dateTimeTV , patientObsv.getDateTime(), (TableRow)view.findViewById(R.id.obsvListDoctorDateTR));
            if(condTV.getText().equals(AddObsv.CONDITION_CRITICAL)) {
                view.setBackgroundColor(Color.parseColor("#e89c9c"));
            } else if(condTV.getText().equals(AddObsv.CONDITION_SICK)) {
                view.setBackgroundColor(Color.parseColor("#fbf28a"));
            }
            

            return view;
        }
    }

    private void setValueToTextView(TextView tv, String val , TableRow TR) {
        if(val.equals("")) {
            TR.setVisibility(View.GONE);
        } else {
//            general_info.logMsg("Value Received  : " + val);
            tv.setText(val);
        }
    }

}


package com.gaurang.doctorover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment implements Handler.Callback {

    //    private Context context  = null;
    private Context context;
    private View view;

    public static String PATIENT_ID = "ID";
    public static String PATIENT_NAME = "Name";
    //    public static int PATIENT_REQUEST_CODE = 1001;
    private static General_Info general_info = null;
    private static Handler mFragmentHandler;
    private static final String TITLE = "Doctor Over";
    private static final String SORT_VAL = "sortVal";
    private static ListView patientListView;
    private static ProgressBar mainProgressBar;

    private static ArrayList<Integer> positionSubHeadArr;  // list of positions above which sub head appears
    private static boolean LIST_MESSAGE_RECEIVED = false;
    private static ArrayList<PatientProfile> arrPatientList = new ArrayList<>(); // list of patients
    private static final String[] arrSortItem = {"Date Added", "Patient", "Group Name", "Latest Obsv", "Consultant", "Condition"}; // do not change values of this array

    // if needed to change .. add elements to array dont delete
    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mainProgressBar = (ProgressBar) view.findViewById(R.id.mainProgressBar);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddPatient.class);
                getActivity().startActivity(intent, general_info.getActivityBundle());
            }
        });

        getActivity().setTitle(TITLE);
        general_info = new General_Info(context);

        setSortSpinner();
        mFragmentHandler = new Handler(this);

        new PatientProfileDBHandler.showPatients(context, mFragmentHandler, (ProgressBar) view.findViewById(R.id.mainProgressBar)).execute();
        return view;
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible == false) { // menun is not visible

        }
    }


    private void setSortSpinner() {

        final Spinner mainSortSpinner = (Spinner) view.findViewById(R.id.mainSortSpinner);
        ArrayAdapter<String> arrAdapterForSortSpinner =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, arrSortItem);
        mainSortSpinner.setAdapter(arrAdapterForSortSpinner);
        if (getSortVal() != null)
            mainSortSpinner.setSelection(arrAdapterForSortSpinner.getPosition(getSortVal()));

        mainSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSortVal(arrSortItem[position]);
                if (arrSortItem.length != 0 && LIST_MESSAGE_RECEIVED == true) {
                    mainProgressBar.setVisibility(View.VISIBLE);
                    Collections.sort(arrPatientList, new CustomComparator(arrSortItem[position]));
                    preProcessListViewItems();
                    mainProgressBar.setVisibility(View.GONE);
                    ArrayAdapter<PatientProfile> profileArrayAdapter = new MyCustomAdapter(context, 0, arrPatientList);
                    patientListView.setAdapter(profileArrayAdapter);
                }
//                Toast.makeText(context, arrSortItem[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSortVal(String sortVal) {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SORT_VAL, sortVal);
        editor.apply();
    }

    private String getSortVal() {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String sortVal = sharedPref.getString(SORT_VAL, null);
        return sortVal;
    }


    private class CustomComparator implements Comparator<PatientProfile> {
        String param;

        public CustomComparator(String param) {
            this.param = param;
        }

        @Override
        public int compare(PatientProfile lhs, PatientProfile rhs) {
            if (param.equals(arrSortItem[0])) { // Date => ID
                return Integer.parseInt(lhs.getID()) > Integer.parseInt(rhs.getID()) ? -1 : 1; // latest First
            } else if (param.equals(arrSortItem[1])) { // Name
                return lhs.getName().compareTo(rhs.getName());
            } else if (param.equals(arrSortItem[2])) { //Group
                return lhs.getGroup().getName().compareTo(rhs.getGroup().getName());
            } else if (param.equals(arrSortItem[3])) { // new Message
                try {
                    if (lhs.getGender() == null && rhs.getGender() == null) {
                        return 0;
                    } else if (lhs.getGender() == null) {
                        return 1; // rhs is greater
                    } else if (rhs.getGender() == null) {
                        return -1; // lhs is greater
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("KK:mma dd MMM,yy");
                    Date date1 = dateFormat.parse(lhs.getGender()); // gender stores the value of date of latest obsv
                    Date date2 = dateFormat.parse(rhs.getGender());
                    return date1.after(date2) ? -1 : 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            } else if (param.equals(arrSortItem[4])) { // consultant
                return lhs.getConsult().compareTo(rhs.getConsult());
            } else if (param.equals(arrSortItem[5])) { // condition
                //cric =1 , sick = 2, normal =3

//                return lhs.getCondition().compareTo(rhs.getCondition());
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(AddObsv.CONDITION_CRITICAL, "a");
                hashMap.put(AddObsv.CONDITION_SICK, "b");
                hashMap.put(AddObsv.CONDITION_NORMAL, "c");

                String lhsCondval = hashMap.get(lhs.getCondition());
                String rhsCondval = hashMap.get(rhs.getCondition());

                return lhsCondval.compareTo(rhsCondval);
            }
            return 0;
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        ArrayList<PatientProfile> result = new ArrayList<>();
        switch (msg.what) {
            case 1001:
                LIST_MESSAGE_RECEIVED = true;
                result = (ArrayList<PatientProfile>) msg.obj;
                break;
        }
        mainProgressBar.setVisibility(View.VISIBLE);
        String sortVal = getSortVal();
        if (sortVal != null) { // sorting the list if any sort condition is applied
            Collections.sort(result, new CustomComparator(sortVal));
        } else {
            Collections.sort(result, new CustomComparator(arrSortItem[0])); // else sort by name
        }
        arrPatientList = result;
        preProcessListViewItems();
        mainProgressBar.setVisibility(View.GONE);

        ArrayAdapter<PatientProfile> patientArrayAdapter = new MyCustomAdapter(context, 0, result);
        patientListView = (ListView) view.findViewById(R.id.mainPatientList);
        if (result.size() == 0) { // no patient till now
            patientListView.setEmptyView(null);
        }
        patientListView.setAdapter(patientArrayAdapter);

        final ArrayList<PatientProfile> finalResult = result;
        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PatientProfile patient = finalResult.get(position);
                startNewActivity(patient);
            }
        });
        patientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(context, "Long Click on " + finalResult.get(position).getName(), Toast.LENGTH_SHORT).show();
                String message = "Do you want to discharge "+finalResult.get(position).getName()+ " ?";
                AlertDialog alertDialog = AskOption(message, finalResult.get(position).getID());
                alertDialog.show();
                return true;
            }
        });


        return false;
    }

    private AlertDialog AskOption(String message, final String patientID) {
        AlertDialog updateDialogBox
                = new AlertDialog.Builder(context)
                .setTitle("Discharge Patient")
                .setMessage(message)
                .setPositiveButton("Discharge", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PatientProfileDBHandler.deletePatient(context, view, patientID).execute();
                        new PatientObsvDBHandler.DeleteObsv(context, view, patientID).execute();
                        new PatientProfileDBHandler.showPatients(context, mFragmentHandler, (ProgressBar) view.findViewById(R.id.mainProgressBar)).execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return updateDialogBox;
    }


    private void preProcessListViewItems() {
        positionSubHeadArr = new ArrayList<>();
        String sortVal = getSortVal();
        if (sortVal.equals(arrSortItem[0]) || sortVal.equals(arrSortItem[1]) || sortVal.equals(arrSortItem[3])) {
            return;
        }

        String str = "";
        String previousVal = null;
        for (int i = 0; i < arrPatientList.size(); i++) {
            if (sortVal.equals(arrSortItem[2])) { // group Name
                String groupName = arrPatientList.get(i).getGroup().getName();
                if (previousVal == null || previousVal.equals(groupName) == false) {
                    previousVal = groupName;
                    positionSubHeadArr.add(i);
                    str += String.valueOf(i) + " ";
                }
            } else if (sortVal.equals(arrSortItem[4])) { // consult
                String consult = arrPatientList.get(i).getConsult();
                if (previousVal == null || previousVal.equals(consult) == false) {
                    previousVal = consult;
                    positionSubHeadArr.add(i);
                    str += String.valueOf(i) + " ";
                }
            } else if (sortVal.equals(arrSortItem[5])) { // condition
                String condition = arrPatientList.get(i).getCondition();
                if (previousVal == null || previousVal.equals(condition) == false) {
                    previousVal = condition;
                    positionSubHeadArr.add(i);
                    str += String.valueOf(i) + " ";
                }
            }
        }
        general_info.logMsg("Sub Headings at : " + str);
    }


    // to display Patient List on Main Page
    private static class MyCustomAdapter extends ArrayAdapter<PatientProfile> {
        Context context;
        List<PatientProfile> objects;

        public MyCustomAdapter(Context context, int resource, List<PatientProfile> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PatientProfile patient = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.patient_listview, null);

            // Puts values to each list Item
            TextView patientName = (TextView) view.findViewById(R.id.listPatientName);
            TextView patientRoomBed = (TextView) view.findViewById(R.id.listPatientRoomBed);
            TextView patientConsult = (TextView) view.findViewById(R.id.listPatientConsult);  // this will hold either group Name or consult
            TextView patientDiag = (TextView) view.findViewById(R.id.listPatientDiag);
            patientName.setText(patient.getName());
            patientName.setTag(patient.getID()); // specical ID tag set to name Component
            patientRoomBed.setText(patient.getRoomNo() + "/" + patient.getBedNo());
            patientConsult.setText(patient.getConsult());
            patientDiag.setText(patient.getDiag());

            TextView newMessagetTV = (TextView) view.findViewById(R.id.listNewMessage);
//            newMessagetTV.setText(patient.getGender());
            if (patient.getAge().equals("0")) { // checking for new Messsgae
                newMessagetTV.setVisibility(View.GONE);
            }


            TextView sortValHeadTV = (TextView) view.findViewById(R.id.listTypeHead);
            SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String sortVal = sharedPref.getString(SORT_VAL, null);

            if (sortVal.equals(arrSortItem[0])) { // Date => ID
                sortValHeadTV.setVisibility(View.GONE);
            } else if (sortVal.equals(arrSortItem[1])) { // Name
                sortValHeadTV.setVisibility(View.GONE);
            } else if (sortVal.equals(arrSortItem[2])) { //Group
                String groupName = patient.getGroup().getName();
                if (positionSubHeadArr != null && positionSubHeadArr.size() != 0 && positionSubHeadArr.contains((Integer) position)) {
                    sortValHeadTV.setText(groupName);
                } else {
                    sortValHeadTV.setVisibility(View.GONE); // header given to each list view Item
                }
                patientConsult.setText(patient.getGroup().getName());
//                patientConsult.setText(patient.getConsult());
            } else if (sortVal.equals(arrSortItem[3])) { // new Message
                sortValHeadTV.setVisibility(View.GONE);
            } else if (sortVal.equals(arrSortItem[4])) { // consultant
                if (positionSubHeadArr != null && positionSubHeadArr.size() != 0 && positionSubHeadArr.contains(position)) {
                    sortValHeadTV.setText(patient.getConsult());
                } else {
                    sortValHeadTV.setVisibility(View.GONE);
                }
                patientConsult.setText(patient.getGroup().getName());
            } else if (sortVal.equals(arrSortItem[5])) { // condition
                if (positionSubHeadArr != null && positionSubHeadArr.size() != 0 && positionSubHeadArr.contains(position)) {
                    sortValHeadTV.setText(patient.getCondition());
                } else {
                    sortValHeadTV.setVisibility(View.GONE);
                }
            }


            String latestCond = patient.getCondition();
            RelativeLayout listPatientRR = (RelativeLayout) view.findViewById(R.id.listPatientRR);
            if (latestCond.equals(AddObsv.CONDITION_CRITICAL)) {
                listPatientRR.setBackgroundColor(context.getResources().getColor(R.color.lightRed));
            } else if (latestCond.equals(AddObsv.CONDITION_SICK)) {
                listPatientRR.setBackgroundColor(context.getResources().getColor(R.color.lightYellow));
            } else {
                listPatientRR.setBackgroundColor(context.getResources().getColor(R.color.lightPurple));
            }

            return view;
        }

//        private String getPatientLatestCond(PatientProfile patient) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = general_info.readFromFileForPatientData();
//                if (jsonObject != null && jsonObject.has("returnVal") && jsonObject.get("returnVal").equals("1")) {
//                    jsonObject.remove("returnVal");
//                    if (jsonObject.names() != null) {
//                        for (int i = 0; i < jsonObject.names().length(); i++) {
//                            JSONObject subJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));
//                            if (subJsonObject.getString("ID").equals(patient.getID())) { // get paticular patient Obsv
//                                JSONArray jsonArray = subJsonObject.getJSONArray("Data"); // get Data parameter
//                                if (jsonArray != null) {
//                                    JSONObject tempJsonObject = jsonArray.getJSONObject(0); // get first obsv
//                                    return tempJsonObject.getString("Cond");
//                                }
//                            }
//                        }
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return "";
//        }


    }


    private void startNewActivity(PatientProfile patient) {
        general_info.setPatientID(patient.getID());
        general_info.setPatientName(patient.getName());
        Intent intent = new Intent(context, PatientData.class);
        getActivity().startActivity(intent, general_info.getActivityBundle());
    }
}

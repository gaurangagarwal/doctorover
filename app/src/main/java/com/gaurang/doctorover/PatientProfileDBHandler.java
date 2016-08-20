package com.gaurang.doctorover;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PatientProfileDBHandler {


    public static class addPatient extends AsyncTask<Void, Void, String> {
        PatientProfile patient;
        Context context;
        General_Info general_info;
        private ProgressDialog pdia;

        public addPatient(Context cont, PatientProfile patient) {
            this.context = cont;
            this.patient = patient;
            general_info = new General_Info(cont);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = general_info.setProgressDialog();
        }

        @Override
        protected void onPostExecute(String result) {
            pdia.dismiss();
            if (result.equals("0")) {
                Toast.makeText(context, "Sorry try Again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = null;
            try {
                data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(patient.getName(), "UTF-8");
                data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(patient.getGender(), "UTF-8");
                data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(patient.getAge(), "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(patient.getPhone(), "UTF-8");
                data += "&" + URLEncoder.encode("groupID", "UTF-8") + "=" + URLEncoder.encode(patient.getGroup().getID(), "UTF-8");
                data += "&" + URLEncoder.encode("regNo", "UTF-8") + "=" + URLEncoder.encode(patient.getRegNo(), "UTF-8");
                data += "&" + URLEncoder.encode("bedNo", "UTF-8") + "=" + URLEncoder.encode(patient.getBedNo(), "UTF-8");
                data += "&" + URLEncoder.encode("roomNo", "UTF-8") + "=" + URLEncoder.encode(patient.getRoomNo(), "UTF-8");
                data += "&" + URLEncoder.encode("diag", "UTF-8") + "=" + URLEncoder.encode(patient.getDiag(), "UTF-8");
                data += "&" + URLEncoder.encode("consult", "UTF-8") + "=" + URLEncoder.encode(patient.getConsult(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String sb = general_info.callPhpScript("patientProfileDBHandler/addPatient.php", data);
            general_info.logMsg("Message received : " + sb);
            return sb;
        }

    }

    public static class deletePatient extends AsyncTask<Void, Void, Void> {
        private String patientID;
        private Context context;
        private View view;
        private General_Info general_info;

        public deletePatient(Context context, View view,String patientID) {
            this.patientID = patientID;
            this.context =context;
            this.view = view;
            this.general_info = new General_Info(context);
        }
        @Override
        protected Void doInBackground(Void... params) {
            String data = null;
            try {
                data = URLEncoder.encode("patientID", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientID), "UTF-8");
//                data += "&"+URLEncoder.encode("gender" , "UTF-8")+"="+URLEncoder.encode(patient.getGender(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(general_info.isInternetConnected() == false) {
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                return null;
            } else {
                JSONObject jsonObject = general_info.callPhpScriptForJson("patientProfileDBHandler/deletePatients.php", data);
                try {
                    String val = jsonObject.getString("returnVal");
//                    if(val.equals("1")) {
//
//                    } else {
////                        Toast.makeText(context, "Try Again" , Toast.LENGTH_SHORT).show();
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

//            JSONObject jsonObject2 = general_info.callPhpScriptForJson("patientProfileDBHandler/deleteObsv.php", data);

            return null;
        }
    }


    public static class showPatients extends AsyncTask<Void, Void, ArrayList<PatientProfile>> {
        private static final int MSG_FINISHED = 1001;
        //        TaskCompleted<ArrayList<PatientProfile>> mCallBack;
        General_Info general_info;
        Context context;
        private ProgressBar pdia;
        private Handler mCallersHandler;

        public showPatients(Context context,Handler handler, ProgressBar pdia) {
            this.pdia = pdia;
            pdia.setVisibility(View.VISIBLE);
            this.mCallersHandler = handler;
//            this.mCallBack = (TaskCompleted<ArrayList<PatientProfile>>) context;
            general_info = new General_Info(context);
            this.context = context;
        }


        @Override
        protected void onPostExecute(ArrayList<PatientProfile> result) {
            pdia.setVisibility(View.GONE);
            mCallersHandler.sendMessage(Message.obtain(mCallersHandler, MSG_FINISHED, result));
//            mCallBack.onTaskComplete(result);
        }

        @Override
        protected ArrayList<PatientProfile> doInBackground(Void... params) {
            String data = null;
            try {
                data = URLEncoder.encode("doctorID", "UTF-8") + "=" + URLEncoder.encode(general_info.getUserLoggedId(), "UTF-8");
//                data += "&"+URLEncoder.encode("gender" , "UTF-8")+"="+URLEncoder.encode(patient.getGender(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//            general_info.logMsg("Internet Connection : "+general_info.isInternetConnected());
//            if(general_info.isInternetConnected()){
//            }
//            general_info.logMsg("Is file written : "+general_info.PATIENT_DATA_WRITTEN_TO_FILE);
            JSONObject jsonObject = general_info.callPhpScriptForJson("patientProfileDBHandler/showPatients.php", data);
            ArrayList<String> compareResults = new ArrayList<>();
            if(jsonObject == null) {
                general_info.logMsg("Received : "+ null );
                try {
                    jsonObject = general_info.readFromFileForPatientData();
//                        general_info.logMsg("Writtern from file :D : "+jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                general_info.logMsg("Before : "+jsonObject.toString());
                JSONObject tempJsonObject = jsonObject;
                compareResults = general_info.setCompareResults(tempJsonObject);  // compare results stoers the value of Patient ID
//                general_info.logMsg("After : "+jsonObject.toString());
                String str = "";
                general_info.logMsg("Compare results size : "+compareResults.size());
                for (int i=0;i < compareResults.size(); i++) {
                    str+= compareResults.get(i)+" ";
                }
                general_info.logMsg(str);
//                Toast.makeText(context , str, Toast.LENGTH_SHORT).show();
                general_info.writeToFileForPatientData(jsonObject.toString());
            }

            ArrayList<PatientProfile> arrPatients = createPatientFromText(jsonObject,compareResults);

            return arrPatients;
        }

        private ArrayList<PatientProfile> createPatientFromText(JSONObject jsonObject, ArrayList<String> compareResults) {
            ArrayList<PatientProfile> arrPatient = new ArrayList<>();
            int compareResultsIndex= 0;

//            general_info.logMsg(jsonObject.toString());
            try {
                if (jsonObject != null && jsonObject.has("returnVal") && jsonObject.get("returnVal").equals("1")) {
                    general_info.logMsg(jsonObject.toString());
                    jsonObject.remove("returnVal");
                    if (jsonObject.names() != null) {
                        for (int i = 0; i < jsonObject.names().length(); i++) {
                            JSONObject tempJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));
//                        JSONArray jsonArray = getSortedList(jsonObject, "ID");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject tempJsonObject = jsonArray.getJSONObject(i);

                            Group group = new Group(tempJsonObject.getString("GroupID"), tempJsonObject.getString("GroupName"));

                            /// here age stores the value=> 1 means new message else 0
                            String newMessage = "0";
                            if(compareResultsIndex < compareResults.size() &&
                                            tempJsonObject.getString("ID").equals(compareResults.get(compareResultsIndex))) {
                                newMessage = "1";
                                compareResultsIndex++;
                            }

                            /// date time means latest obsv of patient date time
                            String dateTime = null;
                            String condition = AddObsv.CONDITION_NORMAL;
                            if(tempJsonObject.has("Data") && tempJsonObject.getJSONArray("Data").length()!=0) {
                                if(tempJsonObject.getJSONArray("Data").getJSONObject(0)!=null) {
                                    dateTime = tempJsonObject.getJSONArray("Data").getJSONObject(0).getString("DateTime");
                                    condition = tempJsonObject.getJSONArray("Data").getJSONObject(0).getString("Cond");
                                }
                            }

                            // here gender stores date time of latest obsv
                            PatientProfile patientProfile = new PatientProfile(tempJsonObject.getString("ID"), tempJsonObject.getString("Name"),
                                    dateTime, newMessage, tempJsonObject.getString("Phone"),
                                    group, tempJsonObject.getString("RegNo"), tempJsonObject.getString("BedNo"), tempJsonObject.getString("RoomNo"),
                                    tempJsonObject.getString("Diag"), tempJsonObject.getString("Consult"));
                            patientProfile.setCondition(condition);

                            arrPatient.add(patientProfile);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            if(sb.equals("null") || sb.equals("")) {
//                return  arrPatient;
//            }
//            String [] arrEachPatient = sb.split("<br>");
//            for (int i=0; i<arrEachPatient.length ; i++) {
//                String patientRawData = arrEachPatient[i];
//                String[] patientInfo = patientRawData.split("&nbsp;");
//                Group group = new Group(patientInfo[5],patientInfo[6]);
//                PatientProfile patient = new PatientProfile(patientInfo[0], patientInfo[1],patientInfo[2],
//                        patientInfo[3],patientInfo[4],group, patientInfo[7],patientInfo[8],
//                            patientInfo[9], patientInfo[10],patientInfo[11]);
//                arrPatient.add(patient);
//            }
            return arrPatient;
        }

        public static JSONArray getSortedList(JSONObject jsonObject, String sortOn) throws JSONException {
            Log.d("MainActivity", "Get Sorted array");
            List<JSONObject> list = new ArrayList<>();
            for (int i = 0; i < jsonObject.names().length(); i++) {
                JSONObject tempJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));
                list.add(tempJsonObject);
            }
//            for (int i = 0; i < array.length(); i++) {
//                list.add(array.getJSONObject(i));
//            }
            Collections.sort(list, new SortBasedOnName(sortOn));

            JSONArray resultArray = new JSONArray(list);

            return resultArray;
        }

        public static class SortBasedOnName implements Comparator<JSONObject> {
            private String sortOn;

            public SortBasedOnName(String sortOn) {
                this.sortOn = sortOn;
            }

            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    return lhs.getString(sortOn).compareTo(rhs.getString(sortOn));
//                    return lhs.getString("Name") > rhs.getString("Name") ? 1 : (lhs
//                            .getInt("Name") < rhs.getInt("Name") ? -1 : 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;

            }
        }

    }
}

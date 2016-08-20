package com.gaurang.doctorover;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PatientObsvDBHandler {
    public static class AddObsv extends AsyncTask<Void, Void,JSONObject> {
        Context context;
        PatientObsv patientObsv;
        General_Info general_info;
        private ProgressDialog pdia;
        public AddObsv(Context context, PatientObsv patientObsv) {
            this.context= context;
            this.patientObsv = patientObsv;
            this.general_info = new General_Info(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = general_info.setProgressDialog();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String data  = null;
            try {
                data = URLEncoder.encode("patientID", "UTF-8")+ "=" + URLEncoder.encode(patientObsv.getPatientID(), "UTF-8");
                data += "&"+URLEncoder.encode("cond", "UTF-8")+ "=" + URLEncoder.encode(patientObsv.getCond(), "UTF-8");
                data += "&"+URLEncoder.encode("operative", "UTF-8")+ "=" + URLEncoder.encode(patientObsv.getOperative(), "UTF-8");
                data += "&"+URLEncoder.encode("blood" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getBlood(), "UTF-8");
                data += "&"+URLEncoder.encode("radio" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getRadio(), "UTF-8");
                data += "&"+URLEncoder.encode("temp" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getTemp(), "UTF-8");
                data += "&"+URLEncoder.encode("pulse" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getPulse(), "UTF-8");
                data += "&"+URLEncoder.encode("resp" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getResp(), "UTF-8");
                data += "&"+URLEncoder.encode("genExam" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getGenExam(), "UTF-8");
                data += "&"+URLEncoder.encode("chest" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getChest(), "UTF-8");
                data += "&"+URLEncoder.encode("abdomen" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getAbdomen(), "UTF-8");
                data += "&"+URLEncoder.encode("hernialGenitals" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getHernialsGenitals(), "UTF-8");
                data += "&"+URLEncoder.encode("advice" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getAdvice(), "UTF-8");
                data += "&"+URLEncoder.encode("priorOrd" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getPriorOrd(), "UTF-8");
                data += "&"+URLEncoder.encode("doctorID" , "UTF-8")+"="+URLEncoder.encode(patientObsv.getDoctorName(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("patientObsvDBHandler/addObsv.php", data);
            general_info.logMsg("Message received : " + jsonObject.toString());
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pdia.dismiss();
            try {
                if(jsonObject.has("returnVal")) {
                    String returnVal = jsonObject.getString("returnVal");
                     if(returnVal.equals("1")) {
                         Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                         Intent intent = new Intent(context, PatientData.class);
                         context.startActivity(intent);
                     } else {
                         Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                     }
                } else {
                    Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // data from online and offline source are diffferent we have different procedures
    public static ArrayList<PatientObsv> createObsvFromJsonOffline(JSONObject jsonObject, General_Info general_info) {
        ArrayList<PatientObsv> patientObsvsArr = new ArrayList<>();
        try {
            if(jsonObject!= null && jsonObject.has("returnVal") && jsonObject.get("returnVal").equals("1")) {
//                    general_info.logMsg(jsonObject.toString());
                jsonObject.remove("returnVal");
                if(jsonObject.names() != null) {
//                        general_info.logMsg("not null tak toh aya length : "+jsonObject.names().length());
                    for (int i = 0; i < jsonObject.names().length(); i++) {
//                            general_info.logMsg(jsonObject.get(jsonObject.names().getString(i)).toString());
                        JSONObject subJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));

//                            general_info.logMsg("PatientID : "+subJsonObject.getString("ID"));
                        if(subJsonObject.getString("ID").equals(general_info.getPatientID())) { // get paticular patient Obsv
//                                general_info.logMsg("patientID match");
                            JSONArray jsonArray = subJsonObject.getJSONArray("Data"); // get Data parameter
//                                general_info.logMsg(jsonArray.toString());
                            if(jsonArray != null) {
                                general_info.logMsg(jsonArray.toString());
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject tempJsonObject = jsonArray.getJSONObject(j);
//                                        general_info.logMsg("Condition : "+ tempJsonObject.getString("Cond"));
                                    PatientObsv patientObsv = new PatientObsv(tempJsonObject.getString("ID"), tempJsonObject.getString("PatientID"),
                                            tempJsonObject.getString("Cond"), tempJsonObject.getString("Operative"), tempJsonObject.getString("Blood"), tempJsonObject.getString("Radio"),
                                            tempJsonObject.getString("Temp"), tempJsonObject.getString("Pulse"), tempJsonObject.getString("Resp"),
                                            tempJsonObject.getString("GenExam"), tempJsonObject.getString("Chest"), tempJsonObject.getString("Abdomen"),
                                            tempJsonObject.getString("HernialGenitals"), tempJsonObject.getString("Advice"), tempJsonObject.getString("PriorOrd"),
                                            tempJsonObject.getString("doctorName"), tempJsonObject.getString("DateTime"));
                                    patientObsvsArr.add(patientObsv);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return patientObsvsArr;
    }

    // used when we have data coming from online source
    public static ArrayList<PatientObsv> createObsvFromJsonOnline(JSONObject jsonObject, General_Info general_info) {
        ArrayList<PatientObsv> patientObsvsArr = new ArrayList<>();
        try {
            if(jsonObject!= null && jsonObject.has("returnVal") && jsonObject.get("returnVal").equals("1")) {
                general_info.logMsg(jsonObject.toString());
                jsonObject.remove("returnVal");
                if(jsonObject.names() != null) {
                    general_info.logMsg("not null tak toh aya length : "+jsonObject.names().length());
                    for (int j = 0; j < jsonObject.length(); j++) {
                        JSONObject tempJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(j));
                        general_info.logMsg("Condition : "+ tempJsonObject.getString("Cond"));
                        PatientObsv patientObsv = new PatientObsv(tempJsonObject.getString("ID"), tempJsonObject.getString("PatientID"),
                                tempJsonObject.getString("Cond"),tempJsonObject.getString("Operative"), tempJsonObject.getString("Blood"),
                                tempJsonObject.getString("Radio"), tempJsonObject.getString("Temp"), tempJsonObject.getString("Pulse"),
                                tempJsonObject.getString("Resp"), tempJsonObject.getString("GenExam"), tempJsonObject.getString("Chest"),
                                tempJsonObject.getString("Abdomen"), tempJsonObject.getString("HernialGenitals"), tempJsonObject.getString("Advice"),
                                tempJsonObject.getString("PriorOrd"), tempJsonObject.getString("doctorName"), tempJsonObject.getString("DateTime"));
                        patientObsvsArr.add(patientObsv);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return patientObsvsArr;
    }

    public static class ShowObsv extends AsyncTask<String, Void, ArrayList<PatientObsv>> {
        TaskCompleted<ArrayList<PatientObsv>> mCallback;
        General_Info general_info;
        Context context;
        private  ProgressDialog pdia;
        public ShowObsv(Context context) {
            this.mCallback= (TaskCompleted<ArrayList<PatientObsv>>) context;
            this.general_info = new General_Info(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = general_info.setProgressDialog();
        }
        @Override
        protected ArrayList<PatientObsv> doInBackground(String... params) {
            String offset = params[0];
            String limit = params[1];
            String data  = null;

            try {
//                data = URLEncoder.encode("doctorID", "UTF-8") + "=" + URLEncoder.encode(general_info.getUserLoggedId(), "UTF-8");
                data = URLEncoder.encode("patientID", "UTF-8")+ "=" + URLEncoder.encode(general_info.getPatientID(), "UTF-8");
                data+= "&"+URLEncoder.encode("offset", "UTF-8")+ "=" + URLEncoder.encode(offset, "UTF-8");
                data+= "&"+URLEncoder.encode("limit", "UTF-8")+ "=" + URLEncoder.encode(limit, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            boolean useOfflineData = false;
            JSONObject jsonObject = general_info.callPhpScriptForJson("patientObsvDBHandler/showObsv.php", data);
            if(jsonObject == null) {
                try {
                    jsonObject = general_info.readFromFileForPatientData();
                    useOfflineData = true;
//                    general_info.setInternetConnected(false);
//                        general_info.logMsg("Writtern from file :D : "+jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<PatientObsv> patientObsvArrayList;
            if(useOfflineData == true){
                patientObsvArrayList =  createObsvFromJsonOffline( jsonObject, general_info);
            } else {
                patientObsvArrayList = createObsvFromJsonOnline(jsonObject, general_info);
            }
            general_info.logMsg("Message received : " + patientObsvArrayList.size());
            return patientObsvArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<PatientObsv> patientObsvsArr) {
            pdia.dismiss();
            mCallback.onTaskComplete(patientObsvsArr);

        }
    }


    public static class DeleteObsv extends AsyncTask<Void, Void, Integer> {

        private Context context;
        private View view;
        private String patientID;
        private General_Info general_info;
        public DeleteObsv(Context context, View view,String patientID) {
            this.general_info = new General_Info(context);
            this.view = view;
            this.patientID = patientID;
            this.context = context;
        }

        @Override
        protected void onPostExecute(Integer val) {
            super.onPostExecute(val);
            if(val == 1) {
                Toast.makeText(context, "Discharge Successful", Toast.LENGTH_SHORT).show();
            } else if(val == 0) {
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String data = null;
            try {
                data = URLEncoder.encode("patientID", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientID), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(general_info.isInternetConnected() == false) {
                Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                return -1;
            } else {
                JSONObject jsonObject = general_info.callPhpScriptForJson("patientObsvDBHandler/deleteObsv.php", data);
                try {
                    String val = jsonObject.getString("returnVal");
                    if(val.equals("1")) {
                        return 1;
//                        Toast.makeText(context, "Delete Successful" , Toast.LENGTH_SHORT).show();
                    } else {
                        return 0;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return -1;
        }
    }
}

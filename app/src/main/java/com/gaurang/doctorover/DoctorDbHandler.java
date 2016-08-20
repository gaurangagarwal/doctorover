package com.gaurang.doctorover;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class DoctorDbHandler {

//    public static General_Info general_info = new General_Info();

    public static final String LOG_TAG  = "MainActivity";
    public static final int MSG_FINISHED_FIND_FRIEND = 101;
//    public static final int MSG_FINISHED_ADD_GROUP_FIND_FRIEND = 102;

//    public static final String URL_SUBLINK  = "http://192.168.43.149/upload/doctorDBHandler/";
    public void MyDBHandler(){    }


    //  USed to Send friend Request to other doctors
    public static class SendFriendRequest extends AsyncTask<String, Void, String> {
        private Context context;
        Button btn;
        private static General_Info general_info;
        private ProgressDialog pdia;
        public SendFriendRequest(Button btn, Context cont){
            this.context = cont;
            this.btn = btn;
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
            Log.d("MainActivity", "On Task Cmplete : Add Friend Btn");
            Log.d("MainActivity", result);
            if(result.equals("11")) {  // 11 denotes everything is done
                Toast.makeText(context , "Sent" , Toast.LENGTH_SHORT).show();
                btn.setText("Friends");
                btn.setEnabled(false);
                btn.setBackgroundColor(context.getResources().getColor(R.color.lightPurple));
            } else {
                Toast.makeText(context , "Request Unsuccuessful" , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String senderID = params[0]; // id of the user to whom request is to be sent
            String ReceiverID = params[1]; // id of user who sends the request
            String data  = null;
            general_info.logMsg(senderID + "  " + ReceiverID);
            try {
                data = URLEncoder.encode("senderID", "UTF-8")+ "=" + URLEncoder.encode(senderID, "UTF-8");
                data += "&"+URLEncoder.encode("receiverID" , "UTF-8")+"="+URLEncoder.encode(ReceiverID, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String sb = general_info.callPhpScript("doctorDBHandler/SendFriendRequest.php" ,data );
            return sb;
        }
    }

    public static class FindFriend extends AsyncTask<String, Void, ArrayList<Doctor>>  {


        //        private TaskCompleted<ArrayList<Doctor>> mCallback;
        private Context context;
        private static General_Info general_info;
        private ProgressBar pdia;
        private Handler mCallersHandler;
        private String searchInput;

        public FindFriend(Context cont, Handler handler , ProgressBar pdia) {
            this.pdia = pdia;
            pdia.setVisibility(View.VISIBLE);
            this.mCallersHandler = handler;
//            this.mCallback = (TaskCompleted<ArrayList<Doctor>>) cont;
            this.context = cont;
            general_info = new General_Info(cont);
        }

        @Override
        protected void onPostExecute(ArrayList<Doctor> result) {
            pdia.setVisibility(View.GONE);
//            if(searchInput.equals(AddGroup.SEARCH_INPUT_FRIEND_LIST)) {
//                mCallersHandler.sendMessage(Message.obtain(mCallersHandler, MSG_FINISHED_ADD_GROUP_FIND_FRIEND, result));
//            } else {
                mCallersHandler.sendMessage(Message.obtain(mCallersHandler, MSG_FINISHED_FIND_FRIEND, result));
//            }
        }

        @Override
        protected ArrayList<Doctor> doInBackground(String... params) {
            searchInput  = params[0];
            String id  =  params[1]; // id of the doctor who has made this search request
//            String link  = URL_SUBLINK+"findFriend.php";
//            Log.d(LOG_TAG , searchInput+" "+id);
//            Log.d(LOG_TAG ,link);
            String data  = null;
            try {
                data = URLEncoder.encode("search", "UTF-8")+ "=" + URLEncoder.encode(searchInput, "UTF-8");
                data += "&"+URLEncoder.encode("id" , "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "Finding the friends");
            String sb = general_info.callPhpScript("doctorDBHandler/findFriend.php" ,data );
            general_info.logMsg("Messga ereceived"+sb);
            ArrayList<Doctor> arrDoctor = new ArrayList<Doctor>();
            if (sb == null || sb == "") {
                return arrDoctor;
            }
//            return null;
            arrDoctor =  createDoctorsFromString(sb);
            return arrDoctor;
        }

        private ArrayList<Doctor> createDoctorsFromString(String sb) {
            Log.d(LOG_TAG , sb);
            ArrayList<Doctor> arrDoctor = new ArrayList<Doctor>();
            if(sb.equals("null")) { // no results found
                return arrDoctor;
            }
            String[] doctorString = sb.split("<br>"); // spliting the doctor data
            for(int i = 0; i< doctorString.length ; i++) {
                Log.d(LOG_TAG , doctorString[i]);
                String[] doctorInfo = doctorString[i].split("&nbsp;");
                // order: ID_Name|_|Gender|_|Email|_|Phone|_|SentInfo
                // 1=> Request already sent , 0=> New Request
                //  password  of dcotor here stores the request sent
                Doctor doctor = new Doctor(Integer.parseInt(doctorInfo[0]),doctorInfo[1], doctorInfo[2],doctorInfo[3], doctorInfo[4], doctorInfo[5]);
                String str = "ID: "+doctor.get_id()+"\n"+
                        "Name: "+doctor.get_name()+"\n"+
                        "Gender: "+ doctor.get_gender()+"\n"+
                        "Email: "+doctor.get_email()+"\n"+
                        "Phone: "+doctor.get_phone();
                Log.d(LOG_TAG, str);
                arrDoctor.add(doctor);
            }
            return arrDoctor;
        }
    }


    public static class FindDoctor extends AsyncTask<String, Void, JSONObject> {

        private Context context;
        private String username;
        private String pass;
        private SharedPreferences sharedPref;
        private static General_Info general_info;
        private ProgressDialog pdia;
        private View view;

        public FindDoctor(Context cont, SharedPreferences shredPref, View view) {
            this.context  = cont;
            this.sharedPref = shredPref;
            this.view = view;
            general_info = new General_Info(cont);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = general_info.setProgressDialog();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            pdia.dismiss();
            if(result != null && result.has("returnVal")) {
                String returnVal = (String) result.remove("returnVal");
                if(returnVal.equals("1")) {
                    try {
                        JSONObject jsonObject = result.getJSONObject("0");
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Username", username);
                        editor.putString("Password", pass);
                        editor.putInt("ID", Integer.parseInt(jsonObject.getString("ID")));
                        editor.putString("Email", jsonObject.getString("Email"));
                        editor.putString("Name", jsonObject.getString("Name"));
                        editor.putString("Phone", jsonObject.getString("Phone"));
                        editor.apply();
                        Toast.makeText(context , "Login Successful" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context , WelcomeActivity.class);
                        intent.putExtra("launchNecessary", false);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context , "Incorrect Entry",Toast.LENGTH_SHORT ).show();
                }
            } else {
                general_info.showMessageInSnackBar(view, "No Internet Connection");
            }


//            if(!result.equals("-1")) { // result -1 denotes that the entry is incorrect
//                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            this.username = params[0];
            this.pass = params[1];
            String data  = null;
            try {
                data = URLEncoder.encode("username", "UTF-8")+ "=" + URLEncoder.encode(username, "UTF-8");
                data += "&"+URLEncoder.encode("pass" , "UTF-8")+"="+URLEncoder.encode(pass, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("doctorDBHandler/findDoctor.php", data);
            return jsonObject;
        }
    }


    // Insert a doctor info into the Doctor db
    public static class Insert extends AsyncTask<Void, Void, JSONObject> {
        private static final String LOG_TAG = "MainActivity";
        private Doctor doctor;
        Context context;
        private static General_Info general_info;
        private ProgressDialog pdia;

        public Insert(Doctor doctor, Context cont) {
            this.doctor = doctor;
            this.context = cont;
            general_info = new General_Info(cont);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = general_info.setProgressDialog();
        }

        protected JSONObject doInBackground(Void... params) {
            String data = null;
            try {
                data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(doctor.get_name(), "UTF-8");
                data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(doctor.get_pass(), "UTF-8");
                data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(doctor.get_phone(), "UTF-8");
                data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(doctor.get_email(), "UTF-8");
                data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(doctor.get_gender(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("doctorDBHandler/insert.php", data);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pdia.dismiss();
            try {
                if(jsonObject!= null && jsonObject.has("returnVal")) {
                    String returnVal = jsonObject.getString("returnVal");
                    if(returnVal.equals("1")) {
                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context , LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                    } else {
                        Toast.makeText(context, jsonObject.getString("returnVal"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}

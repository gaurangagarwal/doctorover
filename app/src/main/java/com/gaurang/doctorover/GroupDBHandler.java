package com.gaurang.doctorover;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GroupDBHandler {

//    public static General_Info general_info = new General_Info();
    public static  final int SHOW_GROUP_NAMES_FINISHED = 100;
    public static final int SHOW_MEMBERS_FINISHED = 101;
    public static final int SHOW_SAVE_METHOD_FINISHED = 102;
    public GroupDBHandler () {
    }

    // Shows Groups of a doctor .. Input => DoctorId as String
    public static class ShowGroups extends  AsyncTask<String, Void, JSONObject> {
        private static General_Info general_info = null;
        private ProgressBar pdia;
        private Handler mCallersHandler;
//        private TaskCompleted<ArrayList<Group>> mCallBack;
        public ShowGroups(Context context, Handler handler, ProgressBar pdia) {
            this.pdia = pdia;
            pdia.setVisibility(View.VISIBLE);
            this.mCallersHandler = handler;
            general_info = new General_Info(context);
//            general_info.logMsg("Show groups Constructor");
//            this.mCallBack = (TaskCompleted<ArrayList<Group>>) context;
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            String doctorID = params[0];
            general_info.logMsg("DoctorId " + doctorID);
            String data = null;
            try {
                data = URLEncoder.encode("doctorID", "UTF-8")+ "=" + URLEncoder.encode(doctorID, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("groupDBHandler/showGroups.php" , data);
            if(jsonObject != null)
                general_info.logMsg(jsonObject.toString());
            else
                general_info.logMsg("Json is null");
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pdia.setVisibility(View.GONE);
            general_info.logMsg("OnPostExecute");
            ArrayList<Group> arrGroup = new ArrayList<>();
            try {
                if(jsonObject!= null && jsonObject.has("returnVal") && jsonObject.get("returnVal").equals("1")) {
                    general_info.logMsg(jsonObject.toString());
                    jsonObject.remove("returnVal");
                    if(jsonObject.names() != null) {
                        for (int i = 0; i < jsonObject.names().length(); i++) {
                            JSONObject tempJsonObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));
                            Group group = new Group(tempJsonObject.getString("ID"), tempJsonObject.getString("GroupName"));
                            arrGroup.add(group);
                        }
                    }
                }
                mCallersHandler.sendMessage(Message.obtain(mCallersHandler, SHOW_GROUP_NAMES_FINISHED, arrGroup));
//                mCallBack.onTaskComplete(arrGroup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



    public static class ShowMembers extends AsyncTask<String, Void,JSONObject>{

        private Context context;
        private General_Info general_info;
        private Handler mCallersHandler;
        private ProgressBar pdia;

        public ShowMembers(Context context, Handler handler, ProgressBar pdia){
            this.pdia = pdia;
            pdia.setVisibility(View.VISIBLE);
            this.context = context;
            this.mCallersHandler = handler;
            general_info = new General_Info(context);
        }


        @Override
        protected JSONObject doInBackground(String... params) {
            String groupID = params[0];
            String doctorID = general_info.getUserLoggedId();

            String data = null;
            try {
                data = URLEncoder.encode("groupID", "UTF-8")+ "=" + URLEncoder.encode(groupID, "UTF-8");
                data += "&"+URLEncoder.encode("doctorID", "UTF-8")+ "=" + URLEncoder.encode(doctorID, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("groupDBHandler/showMembers.php" , data);
            if(jsonObject !=null) {
                general_info.logMsg(jsonObject.toString());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdia.setVisibility(View.GONE);

            ArrayList<Doctor> doctorArr = new ArrayList<>();
            if(jsonObject != null) {
                jsonObject.remove("returnVal");
                if(jsonObject!= null) {
                    for (int i = 0; i < jsonObject.names().length(); i++) {
                        try {
                            JSONObject tempJSONObject = (JSONObject) jsonObject.get(jsonObject.names().getString(i));
                            String email = tempJSONObject.getString("Email");
                            String name = tempJSONObject.getString("Name");
                            String inGroup = tempJSONObject.getString("inGroup");
                            String id = tempJSONObject.getString("ID");
                            Doctor doctor = new Doctor(Integer.parseInt(id), name, inGroup, email, null, null); // Gender Holders inGroup Val
                            doctorArr.add(doctor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            mCallersHandler.sendMessage(Message.obtain(mCallersHandler, SHOW_MEMBERS_FINISHED, doctorArr));
        }
    }


    public static class EditGroup extends AsyncTask<String, Void,JSONObject>{
        private Context context;
        private General_Info general_info;
        private Handler mCallersHandler;
        private  ProgressDialog pdia;

        public EditGroup(Context context, Handler handler){
            this.context = context;
            this.mCallersHandler = handler;
            general_info = new General_Info(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             pdia = general_info.setProgressDialog();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String IDList = params[0];
            String groupID = params[1];
            String data = null;
            try {
                data = URLEncoder.encode("groupID", "UTF-8")+ "=" + URLEncoder.encode(groupID, "UTF-8");
                data += "&"+URLEncoder.encode("IDList", "UTF-8")+ "=" + URLEncoder.encode(IDList, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = general_info.callPhpScriptForJson("groupDBHandler/updateGroup.php" , data);
            if(jsonObject !=null) {
                general_info.logMsg(jsonObject.toString());
            }
            return jsonObject;
//            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            pdia.dismiss();
            mCallersHandler.sendMessage(Message.obtain(mCallersHandler, SHOW_SAVE_METHOD_FINISHED, jsonObject));

        }
    }

    public static class AddGroup extends AsyncTask<ArrayList<Integer>, Void, String> {
        Context cont;
        String groupName;
        private static General_Info general_info = null;
        private ProgressDialog pdia;
        AddGroup(Context cont , String groupName) {
            this.cont = cont;
            this.groupName = groupName;
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
            general_info.logMsg(result);
            Toast.makeText(cont , "Group Created", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(cont, MainActivity.class);
            cont.startActivity(intent);
        }

        @Override
        protected String doInBackground(ArrayList<Integer>... params) {
            ArrayList<Integer> doctorIDs = params[0];
            String idList = "";
            for (int i=0; i<doctorIDs.size() ; i++) {
                idList += doctorIDs.get(i)+"_";
            }
            general_info.logMsg(idList+ "  " + groupName);
            String data = null;
            try {
                data = URLEncoder.encode("IDList", "UTF-8")+ "=" + URLEncoder.encode(idList, "UTF-8");
                data += "&"+URLEncoder.encode("GroupName", "UTF-8")+ "=" + URLEncoder.encode(groupName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String sb = general_info.callPhpScript("groupDBHandler/addGroup.php" , data);
            return sb;
        }
    }
}

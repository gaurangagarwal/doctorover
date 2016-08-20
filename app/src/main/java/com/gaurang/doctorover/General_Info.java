package com.gaurang.doctorover;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.example.gaurangpc.doctor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

// This class has all general method that can be used by all other classes
public class General_Info {
    private Context  context;
    public static final String PATIENT_INFO_FILE = "patientInfo";
    private static final String LOG_TAG  = "MainActivity";
    private static final String PATIENT_ID  = "patientID";
    private static final String PATIENT_NAME  = "patientName";
//    private static final String URL_SUBLINK  = "http://192.168.1.129/upload/";
    private static final String URL_SUBLINK  = "http://doctorapp.pe.hu/";
    public static final String PATIENT_DATA_TXT_FILE  = "patient_data.txt";
    private static boolean IS_INTERNET_CONNECTED  = true;


    public General_Info(Context cont) {
        this.context = cont;
    }
    public General_Info() {
    }


    public Bundle getActivityBundle() {
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
        return activityOptions.toBundle();
    }


    public ArrayList<String> setCompareResults(JSONObject newObj) {
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONObject oldObj =  readFromFileForPatientData();
            if(oldObj == null) {
                return result;
            }
//            Log.d(LOG_TAG , newObj.toString());
            oldObj.remove("returnVal");
            newObj.remove("returnVal");
//            Log.d(LOG_TAG, newObj.toString());

            if (oldObj !=null && newObj != null) {
                int oldArrIndex = 0, newArrIndex = 0;
//                Log.d(LOG_TAG , oldObj.length() + "  "+ newObj.length());
                while(oldArrIndex < oldObj.length() && newArrIndex < newObj.length()) {
//                    Log.d(LOG_TAG , oldArrIndex+ "  "+ newArrIndex);
                    JSONObject oldSubJson = (JSONObject) oldObj.get(oldObj.names().getString(oldArrIndex));
                    String oldIDIndexStr = oldSubJson.getString("ID");
                    int oldIDIndexInt = Integer.parseInt(oldIDIndexStr);
                    JSONObject newSubJson = (JSONObject) newObj.get(newObj.names().getString(newArrIndex));
                    String newIDIndexStr = newSubJson.getString("ID");
                    int newIDIndexInt = Integer.parseInt(newIDIndexStr);

                    if (newIDIndexInt > oldIDIndexInt) {
                        result.add(newIDIndexStr);
                        oldArrIndex++;
                    } else if (newIDIndexInt < oldIDIndexInt) {
                        // not possible
                        newArrIndex++;
                    } else { // equal => same patient
                        JSONArray oldSubJsonArr = oldSubJson.getJSONArray("Data");
                        JSONArray newSubJsonArr = newSubJson.getJSONArray("Data");
//                        Log.d(LOG_TAG , "OldSubJsonArr "+oldSubJsonArr.toString()+" newSubJsonArr "+newSubJsonArr.toString());
                        if (newSubJsonArr.length() == 0 && oldSubJsonArr.length() == 0) {
                            // do nothing
                        } else if (newSubJsonArr.length() != 0 && oldSubJsonArr.length() == 0) {
                            result.add(newIDIndexStr);
                        } else if (newSubJsonArr.length() == 0 && oldSubJsonArr.length() != 0) {
                            // not possible
                        } else { // both are not null
                            String oldSubDataIDStr = oldSubJsonArr.getJSONObject(0).getString("ID");
                            int oldSubDataIDInt = Integer.parseInt(oldSubDataIDStr);
                            String newSubDataIDStr = newSubJsonArr.getJSONObject(0).getString("ID");
                            int newSubDataIDInt = Integer.parseInt(newSubDataIDStr);
                            if (oldSubDataIDInt > newSubDataIDInt) {
                                // not possible
                            } else if (oldSubDataIDInt < newSubDataIDInt) {
                                result.add(newIDIndexStr);
                            } else {// equal
                                // => no new data added
                            }
                        }
                        newArrIndex++;
                        oldArrIndex++;
                    }
                }
//                Log.d(LOG_TAG , "NewArrIndex: " + newArrIndex + " New Obj Lenght  " +newObj.length());
                for (int i=newArrIndex ;i< newObj.length(); i++) {
                    JSONObject newSubJson = (JSONObject) newObj.get(newObj.names().getString(newArrIndex));
                    String newIDIndexStr = newSubJson.getString("ID");
                    result.add(String.valueOf(newIDIndexStr));
                }
            }
            newObj.put("returnVal","1");
            oldObj.put("returnVal","1");

//            Log.d(LOG_TAG ,"Undo thew done :" + newObj.toString()+"------");

        } catch (JSONException e) {
            logMsg("Errorr  " + e.toString());
//            Log.d(LOG_TAG, "Errorr  " + e.toString());
        }

        return  result;
    }


    public void showMessageInSnackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message ,Snackbar.LENGTH_SHORT);
        View v = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
        params.gravity = Gravity.TOP;
        v.setLayoutParams(params);
        snackbar.show();
    }

    public boolean isInternetConnected() {
            return IS_INTERNET_CONNECTED;
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;

//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
//        return haveConnectedWifi || haveConnectedMobile;
    }



    public void deleteFileForPatientData() {

        context.deleteFile(PATIENT_DATA_TXT_FILE);
    }

    public void writeToFileForPatientData(String data) {
        try {

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(PATIENT_DATA_TXT_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            logMsg("File has been written");
//            Log.d(LOG_TAG, "File has been written");
        }
        catch (IOException e) {
            logMsg("File write failed: " + e.toString());
//            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public JSONObject readFromFileForPatientData() throws JSONException {
        String ret = "";

        File file = new File(context.getFilesDir().toString()+"//"+PATIENT_DATA_TXT_FILE);
        if(file.exists() == false) {
            return null;
        }


        try {
            InputStream inputStream = context.openFileInput(PATIENT_DATA_TXT_FILE);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            logMsg("File not found: " + e.toString());
            return null;
        } catch (IOException e) {
            logMsg("Can not read file: " + e.toString());
            return null;
        }
        JSONObject jsonObject = new JSONObject(ret);
        return jsonObject;
    }

    public ProgressDialog setProgressDialog() {
        ProgressDialog pdia = new ProgressDialog(context);
        pdia.setMessage("Loading...");
        pdia.show();
        return pdia;
    }

    public void logMsg(String msg) {
//        Log.d(LOG_TAG, msg);
    }

    public String getUserLoggedId() {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String idAsString = String.valueOf(sharedPref.getInt("ID", -1)); // id of user logged in
        if(idAsString.equals("-1")) { // if id does not exists
            return null;
        }
        return  idAsString;
    }

    public String getUserLoggedName() {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPref.getString("Name", ""); // id of user logged in
        if(name.equals("")) { // if id does not exists
            return null;
        }
        return  name;
    }

    public String getUserLoggedPhone() {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String phone = sharedPref.getString("Phone", ""); // id of user logged in
        if(phone.equals("")) { // if id does not exists
            return null;
        }
        return  phone;
    }

    public String getUserLoggedEmail() {
        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String email = sharedPref.getString("Email", ""); // id of user logged in
        if(email.equals("")) { // if id does not exists
            return null;
        }
        return email;
    }

    public String getPatientID() {
        SharedPreferences sharedPref = context.getSharedPreferences(PATIENT_INFO_FILE, Context.MODE_PRIVATE);
        String patientID = sharedPref.getString(PATIENT_ID, ""); // id of user logged in
        return patientID;
    }

    public String getPatientName() {
        SharedPreferences sharedPref = context.getSharedPreferences(PATIENT_INFO_FILE, Context.MODE_PRIVATE);
        String patientID = sharedPref.getString(PATIENT_NAME, ""); // id of user logged in
        return patientID;
    }

    public void setPatientID(String patientID) {
        SharedPreferences sharedPref = context.getSharedPreferences(PATIENT_INFO_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PATIENT_ID,patientID );
        editor.apply();
    }

    public void setPatientName(String patientName) {
        SharedPreferences sharedPref = context.getSharedPreferences(PATIENT_INFO_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PATIENT_NAME,patientName );
        editor.apply();
    }


    // this method is used to call the php script and return string
    public String callPhpScript(String restLink, String data) {
        logMsg("Called");
//        Log.d(LOG_TAG , "Called");
        String link  = URL_SUBLINK+restLink;
        logMsg(data);
//        Log.d(LOG_TAG , data);
        logMsg(link);
//        Log.d(LOG_TAG ,link);
        String sb = "";
        if(data == null || restLink == null) {
            return sb;
        }
        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            logMsg("Connected");
//            Log.d(LOG_TAG, "Connected");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            String line = "";
            while ((line = br.readLine()) != null) {
                logMsg(line);
//                Log.d(LOG_TAG, line);
                sb += line;
            }
            wr.close();
        } catch (Exception e) {
            IS_INTERNET_CONNECTED = false;
            logMsg("Exception: " + e.getMessage());
//            Log.d(LOG_TAG, "Exception: " + e.getMessage());
            return "";
        }
        if(sb.equals("")) {
            IS_INTERNET_CONNECTED = false;
        } else {
            IS_INTERNET_CONNECTED = true;
        }
        return sb;
    }

    // this method is used to call the php script and return string
    public JSONObject callPhpScriptForJson(String restLink, String data) {
        logMsg("Called");
//        Log.d(LOG_TAG , "Called");
        String link  = URL_SUBLINK+restLink;
        logMsg(data);
        logMsg(link);
//        Log.d(LOG_TAG , data);
//        Log.d(LOG_TAG ,link);
        String sb = "";
        JSONObject jsonObject = null;
        if(data == null || restLink == null) {
            return null;
        }
        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            logMsg("Connected");
//            Log.d(LOG_TAG, "Connected");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            while ((line = br.readLine()) != null) {
//                Log.d(LOG_TAG, line);
                sb += line;
            }
            try {
                jsonObject = new JSONObject(sb);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            wr.close();
        } catch (Exception e) {
            logMsg("Exception: " + e.getMessage());
//            Log.d(LOG_TAG, "Exception: " + e.getMessage());
            IS_INTERNET_CONNECTED = false;
            return null;
        }
//        Log.d(LOG_TAG , "Special Request jsonReturn : "+ jsonObject);

        IS_INTERNET_CONNECTED = true;

        return jsonObject;
    }


}

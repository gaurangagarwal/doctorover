package com.gaurang.doctorover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaurangpc.doctor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditGroup extends Fragment implements Handler.Callback {
    private View view;
    private Context context;
    private Handler mFragmentHandler;
    private Spinner groupNameSpinner;
    private General_Info general_info;
    public static final String DEFAULT_SELECT_GROUP = "Select a Group";
    private ArrayList<Group> groupsArr;
    private ArrayList<Doctor> doctorsArr;
    private ListView editGroupListView;

    private static ArrayList<Integer> positionSubHeadArr;
    private String groupIDSelectedFromSpinner = null;

    public EditGroup() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String message = "";
        String IDList = general_info.getUserLoggedId()+"_";
        if(id == R.id.groupSaveBtn) {
            general_info.logMsg("Number of elemenst in list : "+doctorsArr.size());
            for (int i=0 ;i < doctorsArr.size(); i++) {
                Doctor doctor = doctorsArr.get(i);
                RelativeLayout relativeLayout = (RelativeLayout) editGroupListView.getChildAt(i);
                if(relativeLayout != null) {
//                general_info.logMsg("Childs : "+ relativeLayout.getChildCount());
                    CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                    if (checkBox.isChecked()) {
                        IDList += doctor.get_id() + "_";
                        if (doctor.get_gender().equals("1")) {

                        } else {
                            message += "Add " + doctor.get_name() + "\n";
                        }
                    } else {
                        if (doctor.get_gender().equals("1")) {
                            message += "Remove " + doctor.get_name() + "\n";
                        } else {

                        }
                    }
                }
            }

            AlertDialog alertDialog = AskOption(message, IDList);
            alertDialog.show();

//            Toast.makeText(context, "Save Btn pressed", Toast.LENGTH_SHORT).show();
        }
        return false;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group, container, false);
        context = container.getContext();
        mFragmentHandler = new Handler(this);
        groupNameSpinner = (Spinner) view.findViewById(R.id.editGroupGroupName); // spinner
        general_info = new General_Info(context);
        editGroupListView = (ListView) view.findViewById(R.id.editGroupListView);

        getActivity().setTitle("Edit Group");

        new GroupDBHandler.ShowGroups(context, mFragmentHandler , (ProgressBar)view.findViewById(R.id.editGroupGroupNameProgressBar)).execute(general_info.getUserLoggedId());


        return view;
    }


    private AlertDialog AskOption (String message, final String IDList) {
        AlertDialog updateDialogBox
                    =  new AlertDialog.Builder(context)
                        .setTitle("Save Changes ?")
                        .setMessage(message)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(groupIDSelectedFromSpinner != null)
                                    new GroupDBHandler.EditGroup(context, mFragmentHandler).execute(IDList, groupIDSelectedFromSpinner);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
        return  updateDialogBox;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case GroupDBHandler.SHOW_GROUP_NAMES_FINISHED: {
                setGroupNamestoSpinner((ArrayList<Group>) msg.obj);
                break;
            }
            case GroupDBHandler.SHOW_MEMBERS_FINISHED: {
                setGroupMemberstoListView((ArrayList<Doctor>) msg.obj);
                break;
            }
            case GroupDBHandler.SHOW_SAVE_METHOD_FINISHED: {
                JSONObject jsonObject = (JSONObject) msg.obj;
                if(jsonObject != null) {
                    try {
                        if(jsonObject.getString("returnVal").equals("1")) {
                            Toast.makeText(context , "Saved Successfully", Toast.LENGTH_SHORT).show();
                            general_info.logMsg("Progress bar : " + view.findViewById(R.id.editGroupMembersProgressBar).toString());
                            new GroupDBHandler.ShowMembers(context ,mFragmentHandler, (ProgressBar)view.findViewById(R.id.editGroupMembersProgressBar))
                                                                                        .execute(groupIDSelectedFromSpinner);
                        } else {
                            general_info.showMessageInSnackBar(view.findViewById(android.R.id.content), "Try Again");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    general_info.showMessageInSnackBar( view.findViewById(android.R.id.content) , "No Internet Connection");
                }
            }
        }


        return false;
    }

    // finds the position of sub heads
    private void preProcessListViewItems(){
        positionSubHeadArr = new ArrayList<>();
        String str = "";
        String preivousInGroupData = null;
        for (int i=0;i < doctorsArr.size();  i++) {
            String inGroup = doctorsArr.get(i).get_gender();
            if (preivousInGroupData == null || preivousInGroupData.equals(inGroup) == false) {
                preivousInGroupData = inGroup;
                positionSubHeadArr.add(i);
                str += String.valueOf(i) + " ";
            }
        }
        general_info.logMsg("Sub Headfign at : "+str);
    }

    private void setGroupMemberstoListView(ArrayList<Doctor> doctorArr) {
        doctorsArr = doctorArr;
        preProcessListViewItems();
        ArrayAdapter<Doctor> arrayAdapter = new MyCustomAdapter(context , 0, doctorsArr);
        if(doctorsArr.size() == 0) { // if doctors has no friends
            editGroupListView.setEmptyView(null);
        }
        editGroupListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        editGroupListView.setAdapter(arrayAdapter);
    }

    private void setGroupNamestoSpinner(ArrayList<Group> result) {
//        final ArrayList<Group> result = new ArrayList<>();
        result.add(0,new Group(String.valueOf(-1), DEFAULT_SELECT_GROUP));
        groupsArr = result;
        if(result.size() ==0) { // result always has at least one because of DEFAULT_SELECT_GROUP
            Toast.makeText(context, "Firstly, Create a Group", Toast.LENGTH_SHORT).show();

            MainFragment mainFragment= new MainFragment();  // start Main Fragment
            this.getFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentContainer, mainFragment)
                    .addToBackStack(null)
                    .commit();
            return;
        }

        List<String> groupNamesArr = new ArrayList<String>();
        for (int i=0; i < result.size() ; i++){
            groupNamesArr.add(result.get(i).getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, groupNamesArr);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupNameSpinner.setAdapter(dataAdapter);

        final ArrayList<Group> tempArrGroup = result;
        groupNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(context, tempArrGroup.get(position).getName(), Toast.LENGTH_SHORT).show();
                if (tempArrGroup.get(position).getName().equals(DEFAULT_SELECT_GROUP) == false) {
                    startProcessToAddMembers(tempArrGroup , position);
//                    general_info.logMsg("Group Id of sle");
//                    Toast.makeText(context , "Group ID selected : "+tempArrGroup.get(position).getID() , Toast.LENGTH_SHORT).show();
                } else {
                    setGroupMemberstoListView(new ArrayList<Doctor>());
                    groupIDSelectedFromSpinner = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                general_info.logMsg("Nothing Selected");
            }
        });
    }




    private void startProcessToAddMembers(ArrayList<Group> tempArrGroup, int position) {
        general_info.logMsg("Progress bar : "+ view.findViewById(R.id.editGroupMembersProgressBar).toString());
        new GroupDBHandler.ShowMembers(context, mFragmentHandler , (ProgressBar) view.findViewById(R.id.editGroupMembersProgressBar))
                .execute(tempArrGroup.get(position).getID());
        groupIDSelectedFromSpinner = tempArrGroup.get(position).getID();
    }



    private class MyCustomAdapter extends ArrayAdapter<Doctor> {
        Context context;
        ArrayList<Doctor> doctorList;

        public MyCustomAdapter(Context context, int resource, ArrayList<Doctor> objects) {
            super(context, resource, objects);
            this.context = context;
            this.doctorList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Doctor doctor = doctorList.get(position);
            View view;
//            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friend_listview, null);
//            } else {
//                view = convertView;
//            }

            String inGroup = doctor.get_gender();
//            general_info.logMsg(doctor.get_name() + " Ingroup Data : " + inGroup + " previous data : "+ previousInGroupData);
            TextView subHeadTV = (TextView) view.findViewById(R.id.friendToGroupHeader);

            if(positionSubHeadArr.contains(position)) {
                if (inGroup.equals("1")) { // means person already in Group
                    subHeadTV.setText("Members in Group");
                } else { // means person not in group
                    subHeadTV.setText("Other Friends");
                }
            } else {
                subHeadTV.setVisibility(View.GONE);
            }


//            if(inGroup.equals(previousInGroupData) == false) {
//                previousInGroupData = inGroup;
//                if (inGroup.equals("1")) { // means person already in Group
//                    subHeadTV.setText("Members in Group");
//                } else { // means person not in group
//                    subHeadTV.setText("Other Friends");
//                }
//            } else {
////                general_info.logMsg(doctor.get_name() + "--------------------- Ingroup Data : "+ previousInGroupData);
//                subHeadTV.setVisibility(View.GONE);
//            }

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.friendToGroupCheckBox);

            SpannableString ss1 = new SpannableString(doctor.get_name());
            ss1.setSpan(new RelativeSizeSpan(1.1f), 0 , ss1.length(),0);

            SpannableString ss2 = new SpannableString(doctor.get_email());
            ss2.setSpan(new RelativeSizeSpan(0.8f), 0 , ss2.length(),0);
            ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF909090")), 0, ss2.length(), 0);
//            ss2.setSpan(new Span, 0, ss2.length(), 0);
            checkBox.setText("");
            checkBox.append(ss1);
            checkBox.append("\n");
            checkBox.append(ss2);
//            checkBox.setText(doctor.get_name() + "    " + doctor.get_email());
            checkBox.setTag(doctor.get_id());

//            checkBox.setText(Html.fromHtml("<b>"+doctor.get_name()+"<b>"));
            if(inGroup.equals("1")) { // gender holds inGroup data... 1 means doctor was in this group
                checkBox.setChecked(true);
            }
            return view;
        }
    }
}

package com.gaurang.doctorover;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gaurangpc.doctor.R;

import java.util.ArrayList;

// the add Group Activity

public class AddGroup extends Fragment implements Handler.Callback {

    public static final String LOG_TAG = "MainActivity";
    General_Info general_info;
    ArrayList<Doctor> doctorList;
    private View view;
    private Handler mFragmentHandler;
    private Context context;
    public final static String SEARCH_INPUT_FRIEND_LIST = "Friend_List" ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_add_group, container, false);
        context = container.getContext();
        general_info = new General_Info(context);

        mFragmentHandler = new Handler(this);

        getActivity().setTitle("Create Group");

//        Button addGroupSaveGroupBtn = (Button) view.findViewById(R.id.addGroupSaveGroup);
//        addGroupSaveGroupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addGroupSaveGroupBtnPressed(v);
//            }
//        });

        String idAsString = general_info.getUserLoggedId(); // get id of user logged in

        new DoctorDbHandler.FindFriend(context, mFragmentHandler , (ProgressBar)view.findViewById(R.id.addGroupProgressBar)).execute(SEARCH_INPUT_FRIEND_LIST, idAsString);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.groupSaveBtn) {
            addGroupSaveGroupBtnPressed();
        }
        return false;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(!menuVisible) {

        }
    }

    public void addGroupSaveGroupBtnPressed() {
        if(doctorList.size() <=0) {
            return;
        }
        ListView listView = (ListView) view.findViewById(R.id.addGroupFriendList);
        ArrayList<Integer> doctorInGroupIDs = new ArrayList<Integer>(); // id of doctors in group
        doctorInGroupIDs.add(Integer.parseInt(general_info.getUserLoggedId())); // add the user creating group to this array

        for (int i=0;i<doctorList.size(); i++) {
            RelativeLayout relativeLayout = (RelativeLayout) listView.getChildAt(i);
            if(relativeLayout != null) {
                CheckBox checkBox = (CheckBox) relativeLayout.getChildAt(1);
                if (checkBox.isChecked()) {
                    doctorInGroupIDs.add((Integer) checkBox.getTag()); // tag contains the id of doctor
//                Log.d(LOG_TAG, checkBox.getText() + "  " + checkBox.isChecked());
                }
            }
        }
        EditText editText = (EditText) view.findViewById(R.id.addGroupName);
        String groupName = editText.getText().toString();
        if(groupName.equals("") || doctorInGroupIDs.size() == 1 ) {

            return;
        }
        new GroupDBHandler.AddGroup(context , groupName).execute(doctorInGroupIDs);
    }


    // Puts friends in List view
    @Override
    public boolean handleMessage(Message msg) {
        ArrayList<Doctor> result = new ArrayList<>();
        switch (msg.what) {
            case DoctorDbHandler.MSG_FINISHED_FIND_FRIEND:
                // Let's see what we are having for dessert
                result = (ArrayList<Doctor>) msg.obj;
                break;
        }
        doctorList = result;
        general_info.logMsg(""+result.size());

        ArrayAdapter<Doctor> doctorArrayAdapter =
                new MyCustomAdapter(context, 0, result);

        ListView listView = (ListView) view.findViewById(R.id.addGroupFriendList);

        if(result.size() == 0) { // if doctors has no friends
            listView.setEmptyView(null);
        }
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(doctorArrayAdapter);

        return false;
    }


    // puts content to the list view
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.friend_listview, null);

            TextView subHeadTV = (TextView) view.findViewById(R.id.friendToGroupHeader);
            subHeadTV.setVisibility(View.GONE);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.friendToGroupCheckBox);

            SpannableString ss1 = new SpannableString(doctor.get_name());
            ss1.setSpan(new RelativeSizeSpan(1.1f), 0 , ss1.length(),0);
            SpannableString ss2 = new SpannableString(doctor.get_email());
            ss2.setSpan(new RelativeSizeSpan(0.8f), 0 , ss2.length(),0);
            ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF909090")), 0, ss2.length(), 0);

            checkBox.setText("");
            checkBox.append(ss1);
            checkBox.append("\n");
            checkBox.append(ss2);

//            checkBox.setText(doctor.get_name() + "    "+doctor.get_email());
            checkBox.setTag(doctor.get_id());
            return view;
        }
    }
}

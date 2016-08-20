package com.gaurang.doctorover;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gaurangpc.doctor.R;

import java.util.ArrayList;

public class AddFriend extends Fragment implements Handler.Callback {

    String idAsString;
    Button requestSentToBtn;
    General_Info general_info;
    private View view;
    private Context context;
    private static Handler mFragmentHandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_add_friend, container, false);
        context = container.getContext();
        general_info = new General_Info(context);
        mFragmentHandler = new Handler(this);

        getActivity().setTitle("Add Friends");

        Button searchFriendsBtn = (Button) view.findViewById(R.id.addFriendSearchBtn);
        searchFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendSearchBtnPressed(v);
            }
        });
        Button addFriendsBtn0 = (Button) view.findViewById(R.id.addFriendNewFriendAddBtn0);
        addFriendsBtn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendNewFriendBtnPressed(v);
            }
        });
        Button addFriendsBtn1 = (Button) view.findViewById(R.id.addFriendNewFriendAddBtn1);
        addFriendsBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendNewFriendBtnPressed(v);
            }
        });
        Button addFriendsBtn2 = (Button) view.findViewById(R.id.addFriendNewFriendAddBtn2);
        addFriendsBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendNewFriendBtnPressed(v);
            }
        });


        return view;
    }

    // this method is called when search btn is pressed


    // Seacrh a new Friend in the database and Display his details
    public void addFriendSearchBtnPressed(View v) {
        EditText inputUserName = (EditText) view.findViewById(R.id.addFriendName); // get the input username of the friend
        String username = inputUserName.getText().toString();

        SharedPreferences sharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String idAsString = general_info.getUserLoggedId();
        Log.d("MainActivity" , idAsString+ " Find Request Sent");
        new DoctorDbHandler.FindFriend(context, mFragmentHandler, (ProgressBar) view.findViewById(R.id.addFriendProgressBar)).execute(username, idAsString); // this is used to pass the context
    }

    // add btn next to name is pressed
    public void addFriendNewFriendBtnPressed(View v) {
        Button requestSentToBtn = (Button) v;
        general_info.logMsg("Tag : "+requestSentToBtn.getTag());
        String receiverID = String.valueOf(requestSentToBtn.getTag()); // tag consists of phone number of doctor to whom request has to be sent
        new DoctorDbHandler.SendFriendRequest(requestSentToBtn, context).execute(general_info.getUserLoggedId(), receiverID);
    }

    @Override
    public boolean handleMessage(Message msg) {
        ArrayList<Doctor> result = new ArrayList<>();
        switch (msg.what) {
            case DoctorDbHandler.MSG_FINISHED_FIND_FRIEND:
                // Let's see what we are having for dessert
                result = (ArrayList<Doctor>) msg.obj;
                break;
        }

        Log.d("MainActivity", "On Task Cmplete : Search");
        int[] friendNamesArray = {R.id.addFriendNewFriendName0,R.id.addFriendNewFriendName1,R.id.addFriendNewFriendName2};
        int[] friendBtnArray = {R.id.addFriendNewFriendAddBtn0,R.id.addFriendNewFriendAddBtn1,R.id.addFriendNewFriendAddBtn2};
        int[] friendRelativeLayoutArray = {R.id.addFriendNewFriendRelativeLayout0,
                R.id.addFriendNewFriendRelativeLayout1,
                R.id.addFriendNewFriendRelativeLayout2};

        // Clearing the space
        for (int i=0; i<friendNamesArray.length; i++) {
            view.findViewById(friendRelativeLayoutArray[i]).setVisibility(View.GONE);
            view.findViewById(friendBtnArray[i]).setEnabled(true);
        }
        view.findViewById(R.id.addFriendNoResults).setVisibility(View.GONE);

        // No results found
        if (result.size() == 0) {
            view.findViewById(R.id.addFriendNoResults).setVisibility(View.VISIBLE);
            return false;
        }

        // the number of results is restriced to max 3.
        for (int i=0; i<result.size(); i++) {
            view.findViewById(friendRelativeLayoutArray[i]).setVisibility(View.VISIBLE);
            Doctor doctor = result.get(i);
            TextView tv = (TextView) view.findViewById(friendNamesArray[i]);

            SpannableString ss1 = new SpannableString(doctor.get_name());
            ss1.setSpan(new RelativeSizeSpan(1.2f), 0 , ss1.length(),0);

            SpannableString ss2 = new SpannableString(doctor.get_email());
            ss2.setSpan(new RelativeSizeSpan(0.9f), 0 , ss2.length(),0);
            ss2.setSpan(new ForegroundColorSpan(Color.parseColor("#FF909090")), 0, ss2.length(), 0);
//            ss2.setSpan(new Span, 0, ss2.length(), 0);
            tv.setText("");
            tv.append(ss1);
            tv.append("\n");
            tv.append(ss2);


//            String str = doctor.get_name()+"\n"+
//                    doctor.get_email()+"\n"+
//                    doctor.get_phone();
//            tv.setText(str);


            Button btn = (Button) view.findViewById(friendBtnArray[i]);
            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn.setTextColor(Color.WHITE);

            String req = "ADD";
            Log.d("MainActivity", "Sent Status" + doctor.get_pass());
            if(doctor.get_pass().equals("1")) {  // here pass stores the request data
                req = "Friends";
                btn.setEnabled(false);
                btn.setBackgroundColor(getResources().getColor(R.color.lightPurple));
            }
            btn.setTag(doctor.get_id()); // setting tag as the id of the person to recognize the btn press
            btn.setText(req);
        }

        return false;
    }
}

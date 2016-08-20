package com.gaurang.doctorover;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gaurangpc.doctor.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private View view;
    private Context context;
    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_about, container, false);
        context = container.getContext();

        getActivity().setTitle("About Us");
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            TextView versionTV = (TextView) view.findViewById(R.id.aboutVersionValue);
            versionTV.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        TextView link = (TextView) view.findViewById(R.id.aboutIcon8Body);
        String linkText = "Images used are from <a href='https://icons8.com'>Icon8</a>";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        TextView link2 = (TextView) view.findViewById(R.id.aboutFreePikBody);
        String linkText2 = "Icons made by <a href='http://www.flaticon.com/authors/freepik' >Freepik</a> from <a href='http://www.flaticon.com'>www.flaticon.com</a> is licensed by <a href='http://creativecommons.org/licenses/by/3.0/'>CC 3.0 BY</a>";
        link2.setText(Html.fromHtml(linkText2));
        link2.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

}

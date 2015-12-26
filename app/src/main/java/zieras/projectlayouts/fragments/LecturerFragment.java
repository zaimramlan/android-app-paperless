package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import zieras.projectlayouts.R;

public class LecturerFragment extends Fragment{

    OnButtonClickListener buttonClickCallback;
    private Button btnrecord;
    private Button btnview;
    private Button btndevices;
    private Button btnlogout;

    public interface OnButtonClickListener {
        public void onButtonClickRecord(View v);
        public void onButtonClickView();
        public void onButtonClickDevices();
        public void onButtonClickLogout();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            buttonClickCallback = (OnButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " The MainActivity activity must " +
                    "implement OnButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lecturerfragment, container, false);
        btnrecord = (Button) v.findViewById(R.id.bRecordAttend);
        btnrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickRecord(view);
            }
        });

        btnview = (Button) v.findViewById(R.id.bViewAttend);
        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickView();
            }
        });

        btndevices = (Button) v.findViewById(R.id.bDevice);
        btndevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickDevices();
            }
        });

        btnlogout = (Button) v.findViewById(R.id.bLogout1);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickLogout();
            }
        });
        return v;
    }
}

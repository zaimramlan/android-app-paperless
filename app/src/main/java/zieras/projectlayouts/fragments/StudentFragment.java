package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.R;

public class StudentFragment extends Fragment{

    OnButtonClickListener buttonClickCallback;
    private TextView welcomeview;
    private Button btnupdate;
    private Button btnpasschange;
    private Button btnlogout;

    public interface OnButtonClickListener {
        public void onButtonClickUpdate();
        public void onButtonClickChangePass();
        public void onButtonClickLogout();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            buttonClickCallback = (OnButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " The MainActivity activity must " +
                    "implement OnContactSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.studentfragment, container, false);
        String[] namePieces = MainActivity.currentStudent.getName().split(" ");

        welcomeview = (TextView) v.findViewById(R.id.welcomeMsg);
        welcomeview.setText("Assalamualaikum, \n" + namePieces[0] + "!");

        btnupdate = (Button) v.findViewById(R.id.bUpdateMAC);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickUpdate();
            }
        });

        btnpasschange = (Button) v.findViewById(R.id.bPassChg);
        btnpasschange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickChangePass();
            }
        });

        btnlogout = (Button) v.findViewById(R.id.bLogout2);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickLogout();
            }
        });
        return v;
    }
}

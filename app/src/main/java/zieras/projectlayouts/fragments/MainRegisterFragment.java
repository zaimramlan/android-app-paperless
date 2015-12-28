package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import zieras.projectlayouts.R;

public class MainRegisterFragment extends Fragment {
    private OnButtonClickListener buttonClickCallback;
    private EditText nameview, matricview, macview;
    private Button bFindMac, bRegister;
    public static String currentDeviceMac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.mainregisterfragment, container, false);

        nameview = (EditText) v.findViewById(R.id.editName);
        nameview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    buttonClickCallback.hideKeyboard(v);
                }
            }
        });

        matricview = (EditText) v.findViewById(R.id.editMatric);
        matricview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    buttonClickCallback.hideKeyboard(v);
                }
            }
        });

        macview = (EditText) v.findViewById(R.id.confirmNewPass);
        macview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    buttonClickCallback.hideKeyboard(v);
                }
            }
        });
        macview.setText(currentDeviceMac);
        Log.d("TESTST", "SEFSDFSDF");

        bFindMac = (Button) v.findViewById(R.id.bFindMac);
        bFindMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onButtonClickFindMacRegister();
            }
        });

        bRegister = (Button) v.findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameview.getText().toString();
                String matric = matricview.getText().toString();
                String mac = macview.getText().toString();
                buttonClickCallback.onButtonClickRegister(name,matric,mac);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        macview.setText(currentDeviceMac);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnButtonClickListener) {
            buttonClickCallback = (OnButtonClickListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        buttonClickCallback = null;
    }

    public interface OnButtonClickListener {
        public void onButtonClickRegister(String name,String matric,String mac);
        public void onButtonClickFindMacRegister();
        public void hideKeyboard(View v);
    }
}

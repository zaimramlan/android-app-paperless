package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.R;

public class UpdateMacFragment extends Fragment {

    private OnButtonClickListener buttonClickCallback;
    private Button btnupdate;
    private EditText editMac;
    private TextView oldMac;
    private String currentDeviceMac = "";

    public interface OnButtonClickListener {
        public void onClickUpdateMac(String newMac);
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
        View v = inflater.inflate(R.layout.updatemacfragment, container, false);
        oldMac = (TextView) v.findViewById(R.id.oldMac);
        oldMac.setText(MainActivity.currentStudent.getMacAddress());

        editMac = (EditText) v.findViewById(R.id.confirmNewPass);
        editMac.setText(currentDeviceMac);
        editMac.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        btnupdate = (Button) v.findViewById(R.id.bUpdate);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onClickUpdateMac(editMac.getText().toString());
            }
        });
        return v;
    }

    //hides the keyboard of the device
    public void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setCurrentDeviceMac(String currentDeviceMac) {
        this.currentDeviceMac = currentDeviceMac;
    }
}

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

import zieras.projectlayouts.R;

public class ChangePassFragment extends Fragment {

    private OnButtonClickListener buttonClickCallback;
    private Button btnchange;
    private EditText newPass, confirmNewPass;

    public interface OnButtonClickListener {
        public void onClickChangePass(String newPass, String confirmNewPass);
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
        View v = inflater.inflate(R.layout.changepassfragment, container, false);
        newPass = (EditText) v.findViewById(R.id.newPass);
        newPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        confirmNewPass = (EditText) v.findViewById(R.id.confirmNewPass);
        confirmNewPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        btnchange = (Button) v.findViewById(R.id.bChange);
        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickCallback.onClickChangePass(newPass.getText().toString(), confirmNewPass.getText().toString());
            }
        });
        return v;
    }

    //hides the keyboard of the device
    public void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

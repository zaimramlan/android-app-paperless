package zieras.projectlayouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameview, matricview, macview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("PaperLess");
        alertDialog.setMessage("The Bluetooth MAC Address displayed is for this phone." +
                "\nIf you would like to register a different Bluetooth MAC Address, kindly tap on the 'FIND MAC ADDRESS' button.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Got It",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        nameview = (EditText) findViewById(R.id.editName);
        nameview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        matricview = (EditText) findViewById(R.id.editMatric);
        matricview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        macview = (EditText) findViewById(R.id.confirmNewPass);
        macview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        if(bluetoothAdapter != null) {
            String currentDeviceMac = bluetoothAdapter.getAddress();
            macview.setText(currentDeviceMac);
        }
    }

    //hides the keyboard of the device
    public void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void registerStudent(View v) {
        String name = nameview.getText().toString();
        String matric = matricview.getText().toString();
        String mac = macview.getText().toString();

        if(!name.equals("") && !matric.equals("") && !mac.equals("")) {
            RegisterTask task = new RegisterTask();
            task.execute(name.toLowerCase(), matric, mac.toLowerCase());
        } else {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("PaperLess");
            progressDialog.setMessage("Registering new student");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = getResources().getString(R.string.register_url);
            try {
                URL ulrn = new URL(url);
                //opens the connection
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                //ensure that there is no caching to prevent the wrong parameters being used
                con.setUseCaches(false);
                con.setDefaultUseCaches(false);

                //sets the request method to POST
                con.setRequestMethod("POST");
                //adds the parameters for the request body
                String urlparam = "name=" + params[0] + "&matric=" + params[1] + "&mac=" + params[2];
                //sends the POST request
                con.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                dos.writeBytes(urlparam);
                dos.flush();
                dos.close();

                InputStream response = con.getInputStream();
                response.close();
                con.disconnect();
            }
            catch (MalformedURLException e){
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }

            return params[0] + "-" + params[1];
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            //put auto-generated login details into respective variables
            String[] loginDetails = result.split("-");
            String[] firstName = loginDetails[0].split(" ");

            //notify the new user of the current login details
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("PaperLess login details:");
            alertDialog.setMessage("Username: " + loginDetails[1] +
                    "\nPassword: " + firstName[0] + "pass" +
                    "\n\nKindly change your password after your first login.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }
}

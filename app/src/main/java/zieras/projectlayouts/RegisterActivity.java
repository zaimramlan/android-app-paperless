package zieras.projectlayouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import zieras.projectlayouts.fragments.FindMacRegisterFragment;
import zieras.projectlayouts.fragments.MainRegisterFragment;

public class RegisterActivity extends AppCompatActivity implements MainRegisterFragment.OnButtonClickListener, FindMacRegisterFragment.OnButtonClickListener {
    private BluetoothAdapter bluetoothAdapter;
    private FragmentManager fm = getFragmentManager();
    public static ArrayList<BluetoothDevice> scannedDevices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //instantiates the bluetooth adapter
        String currentDeviceMac = "";
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null) {
            currentDeviceMac = bluetoothAdapter.getAddress();
        }

        //sets the main register layout
        MainRegisterFragment mrf = new MainRegisterFragment();
        MainRegisterFragment.currentDeviceMac = currentDeviceMac;
        fm.beginTransaction().replace(R.id.flr1, mrf).setTransition(FragmentTransaction.TRANSIT_ENTER_MASK).commit();

        //display alert dialog to inform user regarding the MAC Address displayed
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("PaperLess");
        alertDialog.setMessage("The Bluetooth MAC Address displayed is for this phone." +
                "\n\nIf you would like to register a different Bluetooth MAC Address, kindly tap on the 'FIND MAC ADDRESS' button.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Got It",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /*
     * Bluetooth related methods
     */
    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                scannedDevices.add(device);
                String result = "Device name:\n" + device.getName() +
                        "\nMAC Address:\n" + device.getAddress();
                FindMacRegisterFragment.listAdapter.add(result);
                FindMacRegisterFragment.listAdapter.notifyDataSetChanged();
            }

            // When discovery finishes
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Device scanning completed.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //hides the keyboard of the device
    public void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("RegisterActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("RegisterActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    public void onDeviceSelected(int position) {
        String newDeviceMac = scannedDevices.get(position).getAddress();
        MainRegisterFragment.currentDeviceMac = newDeviceMac;
        // when the button is pressed when it is still discovering, cancel the discovery
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        onBackPressed();
    }

    public void onButtonClickFindMacRegister() {
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth.", Toast.LENGTH_LONG).show();
        } else if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please turn on your Bluetooth.", Toast.LENGTH_SHORT).show();
        } else {
            FindMacRegisterFragment fmrf = new FindMacRegisterFragment();
            fm.beginTransaction().replace(R.id.flr1, fmrf).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();

            if (bluetoothAdapter.isDiscovering()) {
                // the button is pressed when it discovers, so cancel the discovery
                bluetoothAdapter.cancelDiscovery();
            } else {
                //sets to false when the user starts the discovery
                scannedDevices.clear();
                bluetoothAdapter.startDiscovery();

                IntentFilter intent = new IntentFilter();
                intent.addAction(BluetoothDevice.ACTION_FOUND);
                intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(broadcastReceiver, intent);
                Toast.makeText(this, "Scanning for devices...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onButtonClickRegister(String name, String matric, String mac) {
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

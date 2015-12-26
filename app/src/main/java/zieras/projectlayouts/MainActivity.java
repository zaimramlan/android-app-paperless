package zieras.projectlayouts;

import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import zieras.projectlayouts.asynctasks.AttendanceUpdateTask;
import zieras.projectlayouts.asynctasks.ChangeMacTask;
import zieras.projectlayouts.asynctasks.ChangePasswordTask;
import zieras.projectlayouts.asynctasks.GetStudentsTask;
import zieras.projectlayouts.asynctasks.LocalStorageUpdateTask;
import zieras.projectlayouts.baseclass.SessionManager;
import zieras.projectlayouts.baseclass.Student;
import zieras.projectlayouts.fragments.ChangePassFragment;
import zieras.projectlayouts.fragments.LecturerFragment;
import zieras.projectlayouts.fragments.StudentFragment;
import zieras.projectlayouts.fragments.TakeAttendanceFragment;
import zieras.projectlayouts.fragments.UpdateMacFragment;
import zieras.projectlayouts.fragments.ViewAttendanceFragment;
import zieras.projectlayouts.fragments.ViewDevicesFragment;

public class MainActivity extends AppCompatActivity implements StudentFragment.OnButtonClickListener, LecturerFragment.OnButtonClickListener, UpdateMacFragment.OnButtonClickListener, ChangePassFragment.OnButtonClickListener{
    // the session manager object
    private SessionManager sessionManager;

    // the students database available to all classes
    public static ArrayList<Student> students = new ArrayList<Student>();
    public static ArrayList<Student> studentsAttended = new ArrayList<Student>();
    public static Student currentStudent;

    // bluetooth adapter to handle bluetooth interactions
    private BluetoothAdapter bluetoothAdapter;

    // the fragment manager to manage all fragments
    private FragmentManager fm = getFragmentManager();

    // isCancelled boolean to know whether the user cancelled the discovery or not
    private Boolean isCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if(sessionManager.isLoggedIn()) {
            // allows app to access internet
            StrictMode.ThreadPolicy policy = new StrictMode
                    .ThreadPolicy
                    .Builder()
                    .permitAll()
                    .build();
            StrictMode.setThreadPolicy(policy);

            // get bluetooth adapter instance
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            HashMap<String, String> user = sessionManager.getUserDetails();
            Boolean isLecturer = user.get("userrole").equals("lecturer");

            if (isLecturer) {
                LecturerFragment lf = new LecturerFragment();
                fm.beginTransaction().add(R.id.fl1, lf).commit();
                updateDevices();
            } else {
                if(currentStudent == null) {
                    String url = getResources().getString(R.string.fetch_url);
                    GetStudentsTask task = new GetStudentsTask(this, url, isLecturer);
                    task.execute();
                } else {
                    StudentFragment sf = new StudentFragment();
                    fm.beginTransaction().add(R.id.fl1, sf).commit();
                }
            }
        } else {
            finish();
        }
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

    @Override
    public void onBackPressed(){
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("HomeActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("HomeActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    //LecturerFragment
    public void onButtonClickRecord(View v){
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please turn on your Bluetooth.", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isDiscovering()) {
                //sets to true if the user cancels the discovery
                isCancelled = true;
                // the button is pressed when it discovers, so cancel the discovery
                bluetoothAdapter.cancelDiscovery();
            } else {
                studentsAttended.clear();
                if(TakeAttendanceFragment.listAdapter != null) {
                    TakeAttendanceFragment.listAdapter.clear();
                }
                //sets to false when the user starts the discovery
                isCancelled = false;
                bluetoothAdapter.startDiscovery();

                IntentFilter intent = new IntentFilter();
                intent.addAction(BluetoothDevice.ACTION_FOUND);
                intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(broadcastReceiver, intent);

                if(v == this.findViewById(R.id.bRecordAttend)) {
                    TakeAttendanceFragment taf = new TakeAttendanceFragment();
                    if (findViewById(R.id.fl2) != null) {
                        //layout-sw600dp
                        fm.beginTransaction().replace(R.id.fl2, taf).commit();
                    } else {
                        //one fragment
                        fm.beginTransaction().replace(R.id.fl1, taf).addToBackStack(null).commit();
                    }
                }

                Toast.makeText(this, "Attendance taking in progress.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //LecturerFragment
    public void onButtonClickView(){
        ViewAttendanceFragment vaf = new ViewAttendanceFragment();
        if (findViewById(R.id.fl2) != null) {
            //layout-sw600dp
            fm.beginTransaction().replace(R.id.fl2, vaf).commit();
        }
        else {
            //one fragment
            fm.beginTransaction().replace(R.id.fl1, vaf).addToBackStack(null).commit();
        }
    }

    //LecturerFragment
    public void onButtonClickDevices(){
        ViewDevicesFragment vdf = new ViewDevicesFragment();
        if (findViewById(R.id.fl2) != null) {
            //layout-sw600dp
            fm.beginTransaction().replace(R.id.fl2, vdf).commit();
        }
        else {
            //one fragment
            fm.beginTransaction().replace(R.id.fl1, vdf).addToBackStack(null).commit();
        }
    }

    //StudentFragment
    public void onButtonClickUpdate(){
        UpdateMacFragment umf = new UpdateMacFragment();
        umf.setCurrentDeviceMac(bluetoothAdapter.getAddress());
        if (findViewById(R.id.fl2) != null) {
            //layout-sw600dp
            fm.beginTransaction().replace(R.id.fl2, umf).commit();
        }
        else {
            //one fragment
            fm.beginTransaction().replace(R.id.fl1, umf).addToBackStack(null).commit();
        }
    }

    //StudentFragment
    public void onClickUpdateMac(String newMac) {
        String url = getResources().getString(R.string.change_mac_url);
        String name = currentStudent.getName();
        String matric = currentStudent.getMatricNo();

        ChangeMacTask task = new ChangeMacTask(this, url);
        task.execute(name,matric,newMac);
        currentStudent.setMacAddress(newMac);
        onBackPressed();
    }

    //StudentFragment
    public void onButtonClickChangePass() {
        ChangePassFragment cpf = new ChangePassFragment();
        if (findViewById(R.id.fl2) != null) {
            //layout-sw600dp
            fm.beginTransaction().replace(R.id.fl2, cpf).commit();
        }
        else {
            //one fragment
            fm.beginTransaction().replace(R.id.fl1, cpf).addToBackStack(null).commit();
        }
    }

    public void onClickChangePass(String newPass, String confirmNewPass) {
        String url = getResources().getString(R.string.change_pass_url);
        Boolean isSamePassword = newPass.equals(confirmNewPass);
        Boolean hasMinPasswordLength = newPass.length() > 4 && confirmNewPass.length() > 4;

        if(isSamePassword && hasMinPasswordLength) {
            ChangePasswordTask task = new ChangePasswordTask(this, url);
            task.execute(currentStudent.getMatricNo(), newPass);
            onBackPressed();
        } else if(!isSamePassword){
            Toast.makeText(this, "Passwords entered are not the same.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Password is too short.\n(Min length is 4 characters)", Toast.LENGTH_SHORT).show();
        }
    }

    //StudentFragment and LecturerFragment
    public void onButtonClickLogout() {
        // nullifies the current student object
        currentStudent = null;
        // logs the user out of the session
        sessionManager.logoutUser();
    }

    public void uploadAttendance(View v){
        String url = getResources().getString(R.string.attendance_update_url);
        if(studentsAttended.isEmpty())
            Toast.makeText(this, "Cannot upload an empty list.", Toast.LENGTH_SHORT).show();
        else {
            AttendanceUpdateTask task = new AttendanceUpdateTask(this, url);
            task.setAttendanceList(studentsAttended);
            task.execute();
        }
    }

    /*
     * method overloading for updating the devices
     */

    //checks the network if it is available for the app
    //to pull data from the online database
    public void updateDevices(View v){
        // populate the users from database
        try {
            if(isNetworkAvailable()) updateLocalStorage();
            else accessLocalStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        onBackPressed();
    }

    //updates the data from the localstorage
    public void updateDevices() {
        // populate the users from the storage
        try {
            accessLocalStorage();
        } catch (Exception e) {
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

                //If the MAC Address discovered is in students array, the student is added to the list
                for (Student student:students)
                    if(device.getAddress().equals(student.getMacAddress().trim())) {
                        TakeAttendanceFragment.listAdapter.add(student.getName() + "\n" + student.getMatricNo());
                        TakeAttendanceFragment.listAdapter.notifyDataSetChanged();
                        studentsAttended.add(student);
                    }
            }

            // When discovery finishes
            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //if the user cancels the discovery
                if(isCancelled) {
                    Toast.makeText(getApplicationContext(), "Attendance taking cancelled.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Attendance taken.", Toast.LENGTH_SHORT).show();
                }
                //When there are no matching devices found
                if(TakeAttendanceFragment.listAdapter.isEmpty()) TakeAttendanceFragment.listAdapter.add("No one!");
            }
        }
    };

    /*
    * Checks for internet connection
    */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        /*
        * if no network is available networkInfo will be null
        * otherwise check if we are connected
        */
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    /*
    * Local storage attributes and methods
    */
    public static boolean JSONReadTaskFinished = false;
    private String localStorageFile;

    public void updateLocalStorage() throws IOException {
        localStorageFile = getResources().getString(R.string.local_storage_file);

        //accesses the web to fetch the JSON response
        String url = getResources().getString(R.string.fetch_url);

        // Retrieve students from the database
        boolean isLecturerFragment = true;
        GetStudentsTask task1 = new GetStudentsTask(this, url, isLecturerFragment);
        task1.execute();

        // Executes updating local storage task
        LocalStorageUpdateTask task2 = new LocalStorageUpdateTask(this, localStorageFile, students);
        task2.execute();
    }

    public void accessLocalStorage() {
        localStorageFile = getResources().getString(R.string.local_storage_file);
        String data = "";

        try{
            FileInputStream fin = openFileInput(localStorageFile);
            int c;
            while( (c = fin.read()) != -1){
                data += Character.toString((char)c);
            }
            fin.close();
            Toast.makeText(this, "Students list loaded.", Toast.LENGTH_SHORT).show();
            populateStudents(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
    * Populate students arraylist from the storage
    */

    // Populate data from JSON response
    public void populateStudents(ArrayList<Object> list) {
        // convert the general arraylist into students arraylist
        students.clear();
        for(Object object: list)
            students.add((Student)object);
    }

    // Populate data from the local storage
    public void populateStudents(String data) {
        String name = "", matric = "", mac = "";

        students.clear();
        String[] dataLines = data.split(System.getProperty("line.separator"));

        for (int i=0; i<dataLines.length; i++) {
            String[] singleData = dataLines[i].split("\\s+");
            name = "";

            for (int j=1; j<singleData.length; j++) {
                if(singleData[j].equals("Mac:")) mac = singleData[++j];
                else if(singleData[j].equals("Matric:")) matric = singleData[++j];
                else name += singleData[j] + " ";
            }
            students.add(new Student(name, matric, mac));
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}

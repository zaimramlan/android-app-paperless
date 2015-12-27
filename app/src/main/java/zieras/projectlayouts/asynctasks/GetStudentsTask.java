package zieras.projectlayouts.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.R;
import zieras.projectlayouts.baseclass.SessionManager;
import zieras.projectlayouts.baseclass.Student;
import zieras.projectlayouts.fragments.StudentFragment;

/**
 * Created by zieras on 21/12/2015.
 */
public class GetStudentsTask extends AsyncTask<String, Void, String> {
    private Boolean isLecturerFragment;
    private String url = "";
    private Context context;
    private ProgressDialog progressDialog;
    private String jsonResult;
    private ArrayList<Object> data;
    private HashMap<String, String> currentUser;

    public GetStudentsTask(Context context, String url, Boolean isLecturerFragment) {
        this.context = context;
        this.url = url;
        this.isLecturerFragment = isLecturerFragment;
        data = new ArrayList<Object>();
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        currentUser = sessionManager.getUserDetails();
    }

    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("PaperLess");
        if(isLecturerFragment) {
            MainActivity.JSONReadTaskFinished = false;
            progressDialog.setMessage("Fetching students...");
        } else {
            progressDialog.setMessage("Logging in...");
        }

        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        jsonResult = "";
        try {
            URL ulrn = new URL(url);
            //opens the connection
            HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
            //ensure that there is no caching to prevent the wrong parameters being used
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            //since this task is shared between lecturer and students fragment,
            //the students fragment will send a request to only get the information
            //from the current user
            if(!isLecturerFragment) {
                //sets the request method to POST
                con.setRequestMethod("POST");
                //adds the parameters for the request body
                String urlparam = "matric=" + currentUser.get("username");
                //sends the POST request
                con.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                dos.writeBytes(urlparam);
                dos.flush();
                dos.close();
            }

            //gets the response from the url
            InputStream response = con.getInputStream();
            jsonResult = inputStreamToString(response).toString();
            response.close();
            con.disconnect();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        populateData();

        if(isLecturerFragment) {
            ((MainActivity)context).populateStudents(data);
            MainActivity.JSONReadTaskFinished = true;
        } else if(!isLecturerFragment) {
            StudentFragment sf = new StudentFragment();
            MainActivity.currentStudent = (Student) data.get(0);
            ((MainActivity) context).getFragmentManager().beginTransaction().replace(R.id.fl1, sf).commit();
        }

        progressDialog.dismiss();
    }

    //parse the JSON response into proper string
    private StringBuilder inputStreamToString(InputStream is) {
        String inputLine = "";
        StringBuilder response = new StringBuilder();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
        try {
            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    // populate the data from the JSON into ArrayList
    public void populateData() {
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("stu_info");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                //get the values from the JSON Object
                String name = jsonChildNode.optString("name");
                String matric = jsonChildNode.optString("matric");
                String mac = jsonChildNode.optString("mac");
                data.add(new Student(name, matric, mac.toUpperCase()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

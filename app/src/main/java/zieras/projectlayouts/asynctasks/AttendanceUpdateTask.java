package zieras.projectlayouts.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import zieras.projectlayouts.baseclass.Student;


/**
 * Created by zieras on 22/12/2015.
 */
public class AttendanceUpdateTask extends AsyncTask<String, Void, String> {
    private Context context;
    private String url = "";
    private ProgressDialog progressDialog;
    private ArrayList<Student> studentsAttended;

    public  AttendanceUpdateTask(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("PaperLess");
        progressDialog.setMessage("Uploading attendance to server...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        JSONArray jsonMainNode = new JSONArray();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String datestamp = dateFormat.format(date);

        //converts the studentsAttended arraylist into JSONArray
        for (int i=0; i<studentsAttended.size(); i++) {
            String name = studentsAttended.get(i).getName();
            String matric = studentsAttended.get(i).getMatricNo();
            try {
                //puts the arraylist details into a JSONObject
                JSONObject jsonChildNode = new JSONObject();
                jsonChildNode.put("datestamp", datestamp);
                jsonChildNode.put("name", name);
                jsonChildNode.put("matric", matric);
                //puts the JSONObject into the jsonMainNode of type JSONArray
                jsonMainNode.put(jsonChildNode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //creates a jsonrequest object to put the json array into one place
        JSONObject jsonRequest = new JSONObject();

        try {
            URL ulrn = new URL(url);
            //opens the connection
            HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
            //ensure that there is no caching to prevent the wrong parameters being used
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            //sets the request method to POST
            con.setRequestMethod("POST");
            //puts the jsonarray into the jsonrequest
            jsonRequest.put("stu_attend", jsonMainNode);
            //adds the parameters for the request body
            String urlparam = "jsonarray=" + jsonRequest.toString();
            //sends the POST request
            con.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeBytes(urlparam);
            dos.flush();
            dos.close();

            InputStream response = con.getInputStream();
            response.close();
            con.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        Toast.makeText(context, "Attendance uploaded.", Toast.LENGTH_SHORT).show();
    }

    public void setAttendanceList(ArrayList<Student> studentsAttended) {
        this.studentsAttended = studentsAttended;
    }
}

package zieras.projectlayouts.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zieras on 22/12/2015.
 */
public class ChangeMacTask extends AsyncTask<String, Void, String> {
    private ProgressDialog progressDialog;
    private Context context;
    private String url, jsonResult;

    public ChangeMacTask(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("PaperLess");
        progressDialog.setMessage("Updating MAC Address...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String name = params[0];
        String matric = params[1];
        String newMac = params[2];

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
            String urlparam = "name=" + name + "&matric=" + matric + "&mac=" + newMac;
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

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        Toast.makeText(context, "MAC Address updated.", Toast.LENGTH_SHORT).show();
    }
}

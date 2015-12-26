package zieras.projectlayouts.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.baseclass.Student;

/**
 * Created by zieras on 22/12/2015.
 */
public class LocalStorageUpdateTask extends AsyncTask<String, Void, String> {
    private Context context;
    private String localStorageFile;
    private ProgressDialog progressDialog;
    private ArrayList<Student> students;

    public LocalStorageUpdateTask(Context context, String localStorageFile, ArrayList<Student> students) {
        this.context = context;
        this.localStorageFile = localStorageFile;
        this.students = students;
    }

    @Override
    protected String doInBackground(String... params) {
        // waits for JSONReadTask to finish before saving to local storage
        while(!MainActivity.JSONReadTaskFinished) {
            try {
                Log.d("LocalStorageUpdateTask", "Waiting for JSONReadTask to finish...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // compress the arraylist into a single string
        String studentData = "";
        for(Student student: students)
            studentData += student.toString() + "\n";

        try {
            FileOutputStream fOut = context.openFileOutput(localStorageFile, context.MODE_WORLD_READABLE);
            fOut.write(studentData.getBytes());
            fOut.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "Students list updated.", Toast.LENGTH_SHORT).show();
    }
}

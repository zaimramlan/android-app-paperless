package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.R;
import zieras.projectlayouts.baseclass.Student;

public class ViewAttendanceFragment extends Fragment{
    private TextView textview;
    private ListView listView;
    public static ArrayAdapter<String> listAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewattendancefragment, container, false);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        textview = (TextView) v.findViewById(R.id.students);
        textview.setText(dateFormat.format(date) + " Attendance List:");

        listView = (ListView) v.findViewById(R.id.list);
        listView.setAdapter(listAdapter);

        display();
        return v;
    }

    // display all devices from the local storage
    public void display(){
        listAdapter.clear();
        if(MainActivity.studentsAttended.isEmpty())
            listAdapter.add("Nobody attended.");
        else
            for(Student student: MainActivity.studentsAttended)
                listAdapter.add(student.getName() + " " + student.getMatricNo() + "\n" + student.getMacAddress());
    }
}

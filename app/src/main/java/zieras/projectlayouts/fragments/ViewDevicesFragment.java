package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import zieras.projectlayouts.MainActivity;
import zieras.projectlayouts.R;
import zieras.projectlayouts.baseclass.Student;

public class ViewDevicesFragment extends Fragment{
    private ListView listview;
    private ArrayAdapter<String> listAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewdevicesfragment, container, false);

        listview = (ListView) v.findViewById(R.id.list);
        listview.setAdapter(listAdapter);

        display();
        return v;
    }

    // display all devices from the local storage
    public void display(){
        listAdapter.clear();
        if(MainActivity.students.isEmpty())
            listAdapter.add("No devices yet.");
        else
            for(Student student: MainActivity.students)
                listAdapter.add("Name: \n" + student.getName() + "\nMatric Number: \n" + student.getMatricNo() + "\nMAC Address: \n" + student.getMacAddress());
    }
}

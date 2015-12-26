package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import zieras.projectlayouts.R;

public class TakeAttendanceFragment extends Fragment{
    private ListView listview;
    public static ArrayAdapter<String> listAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.takeattendancefragment, container, false);
        listview = (ListView) v.findViewById(R.id.list);
        listview.setAdapter(listAdapter);
        return v;
    }
}

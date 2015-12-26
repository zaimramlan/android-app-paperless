package zieras.projectlayouts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import zieras.projectlayouts.R;
public class FindMacRegisterFragment extends Fragment {
    private OnButtonClickListener buttonClickCallback;
    private ListView listview;
    public static ArrayAdapter<String> listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.findmacregisterfragment, container, false);
        listview = (ListView) v.findViewById(R.id.deviceList);
        listview.setAdapter(listAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buttonClickCallback.onDeviceSelected(position);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        if (activity instanceof OnButtonClickListener) {
            buttonClickCallback = (OnButtonClickListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnButtonClickListener {
        public void onDeviceSelected(int position);
    }
}

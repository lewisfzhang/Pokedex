package com.example.pokedex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// A fragment is a combination of XML and a java class, much like an Activity, this is the template of a Java Controller for a fragment
public class ListFragment extends Fragment implements View.OnClickListener {


    @NonNull
    public static ListFragment newInstance() { return new ListFragment(); }

    /*
    The onCreateView method is called when Fragment should create its View object hierarchy,
    either dynamically or via XML layout inflation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // define the xml file for the fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    ImageView image;
    TextView name;
    Button back;
    // this is triggered soon after onCreateView() specifically when host activity has completed `onCreate()`, and any view setup should be done here
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        ArrayList<Integer> idx = mainActivity.idx;

        name = getView().findViewById(R.id.poke_name);
        name.setText(String.format("Search Results (%d)", idx.size()));
        back = getView().findViewById(R.id.backButton);
        back.setOnClickListener(this);

        // Sample data for us to populate the RecyclerView with
        ArrayList<Pokemon> data = new ArrayList<>();
        for (int i : idx) {
            data.add(mainActivity.list.get(i));
        }

        // Setting up the RecyclerView
        RecyclerView recycler = getView().findViewById(R.id.recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(manager);
        RecyclerView.Adapter adapter = new Adapter(getContext(), data);
        recycler.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}

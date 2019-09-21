package com.example.pokedex;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    Switch toggleView;

    RecyclerView recycler;
    RecyclerView.LayoutManager linearManager;
    RecyclerView.LayoutManager gridManager;
    RecyclerView.Adapter adapter;
    boolean is_linear_manager = true;
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

        toggleView = getView().findViewById(R.id.toggleView);
        toggleView.setOnClickListener(this);

        // Setting up the RecyclerView
        recycler = getView().findViewById(R.id.recycler);
        linearManager = new LinearLayoutManager(getContext());
        gridManager = new GridLayoutManager(getContext(), 2);
        recycler.setLayoutManager(linearManager); // default layout is linear
        adapter = new Adapter(getContext(), data);
        recycler.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.toggleView:
                toggleLayout();
                break;
        }
    }

    private void toggleLayout() {
        if (is_linear_manager) { // use grid
            recycler.setLayoutManager(gridManager);
            is_linear_manager = false;
        } else { // use linear
            recycler.setLayoutManager(linearManager);
            is_linear_manager = true;
        }
    }
}

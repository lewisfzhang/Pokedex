package com.example.pokedex;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/*
In a fragment oriented architecture, activities become NAVIGATIONAL CONTAINERS, which are primarily
responsible for navigation to other activities, presenting fragments and passing data. Fragments
enable re-ues of parts of your screen including views and event logic across disparate activities
(i.e. using the same list across different data sources within an app)

In our fragment oriented architecture we will split responsibilities:

Activties - Navigation to other activities, presenting navigational components, hiding and showing
relevant fragments using `FragmentManager`, receiving data from intents and passing data between fragments

Fragments - contain most layouts and views displaying relevant content, event handling logic with
relevant views, retrieval and storage of data
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // FragmentManager allows you to add the fragment dynamically, allowing you to add, remove and replace fragments in the layout of your activity at runtime
    private static FragmentManager fm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                // Depending on which navigation item is clicked, decide which fragment to show
                case R.id.navigation_home:
                    // If the home button is pressed we want to call on the fragment illustrated by `fragment_list.xml`
                    ListFragment frag = new ListFragment();
                    // Begin the transaction, replace the contents of the container with the new fragment, and commit (complete the changes you made above)
                    fm.beginTransaction().replace(R.id.frame, frag).addToBackStack("").commit();
                    /*
                    The purpoes of addToBackStack is so that back-click functionality on the phone works properly,
                    this is SUPER USEFUL especially if you add multiple changed to the transaction
                     */
                    return true;
                case R.id.navigation_dashboard:
                    // OtherFragment otherFrag = new OtherFragment();
                    // fm.beginTransaction().replace(R.id.frame, otherFrag).addToBackStack("").commit();
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    Button searchButton;
    Spinner spinner;
    AutoCompleteTextView searchName;
    EditText minAttack;
    EditText minDefense;
    EditText minHealth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm = getSupportFragmentManager();

        readJSON();
        findTypes();

        MultiSpinner multiSpinner = findViewById(R.id.typeSelector);
        List<String> type_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.type_array)));
        multiSpinner.setItems(type_list, getString(R.string.any), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                Log.d("i", Arrays.toString(selected));
                // do stuff here
            }
        });


//        // https://developer.android.com/guide/topics/ui/controls/spinner
//        spinner = findViewById(R.id.spinner);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.type_array, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);

//        type = findViewById(R.id.typeSelector);
//        if (type.isChecked()) {
//            type.setChecked(false);
//        }

        minAttack = findViewById(R.id.minAttack);
        minDefense = findViewById(R.id.minDefense);
        minHealth = findViewById(R.id.minHealth);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, names);
        searchName = findViewById(R.id.searchName);
        searchName.setAdapter(arrayAdapter);
        searchName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // choose pokemon
                Object o = adapterView.getItemAtPosition(i);
                if (o instanceof String) {
                    // go to next fragment
                    Log.d("i", (String) o);
                } else {
                    Log.d("i", "Error: Unable to select item");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                break;
        }
    }

    private ArrayList<Pokemon> list = new ArrayList<>();
    private String[] names;

    private void readJSON() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getApplicationContext().getAssets().open("pokeData.json")))) {
            JSONObject j = new JSONObject(br.readLine());
            Iterator<String> keys = j.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                Pokemon p = new Pokemon(key, j.getJSONObject(key));
                list.add(p);
            }
            Log.d("i", "List size: "+list.size());

            names = new String[list.size()];
            for (int i=0; i<list.size(); i++) {
                names[i] = list.get(i).name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> types = new ArrayList<>();

    private void findTypes() {
        HashSet<String> h = new HashSet<>();
        try {
            for (Pokemon p : list) {
                for (int i = 0; i < p.type.length(); i++) {
                    String s = p.type.getString(i);
                    if (!h.contains(s)) {
                        h.add(s);
                        types.add(s);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("i", types.size()+" "+types.toString());
    }

    private ArrayList<Integer> byType(String key) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).isType(key)) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    private ArrayList<Integer> attackCutoff(int x) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).attack > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    private ArrayList<Integer> defenseCutoff(int x) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).defense > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    private ArrayList<Integer> healthCutoff(int x) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i=0; i<list.size(); i++) {
            if (list.get(i).hp > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

}

class Pokemon {
    public String name;
    public int number;
    public int attack;
    public int defense;
    public String flavorText;
    public int hp;
    public int sp_atk;
    public int sp_def;
    public String species;
    public int speed;
    public int total;
    public JSONArray type;

    public Pokemon(String name, JSONObject data) {
        this.name = name;
        try {
            number = data.getInt("#");
            attack = data.getInt("Attack");
            defense = data.getInt("Defense");
            flavorText = data.getString("FlavorText");
            hp = data.getInt("HP");
            sp_atk = data.getInt("Sp. Atk");
            sp_def = data.getInt("Sp. Def");
            species = data.getString("Species");
            speed = data.getInt("Speed");
            total = data.getInt("Total");
            type = data.getJSONArray("Type");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("{Name: %s, attack: %d, defense: %d, hp: %d, type: %s}", name, attack, defense, hp, type.toString());
    }

    public boolean isType(String key) {
        try {
            for (int i = 0; i < type.length(); i++) {
                if (key.equals(type.getString(i))) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

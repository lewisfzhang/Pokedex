package com.example.pokedex;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    Button searchButton;
    Spinner spinner;
    AutoCompleteTextView searchName;
    EditText minAttack;
    EditText minDefense;
    EditText minHealth;

    String name_to_pass = "test";
    private boolean[] type_selected = new boolean[18];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm = getSupportFragmentManager();

        readJSON();
        findTypes();

        for (int i=0; i<type_selected.length; i++) type_selected[i] = true;
        MultiSpinner multiSpinner = findViewById(R.id.typeSelector);
        List<String> type_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.type_array)));
        multiSpinner.setItems(type_list, getString(R.string.any), new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                type_selected = selected;
                Log.d("i", Arrays.toString(selected));
                // do stuff here
            }
        });

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
                    String s = (String) o;
                    Log.d("i", s);

                    Intent intent = new Intent(MainActivity.this, PokeInfo.class);
                    int idx = findIndexName(s);
                    if (idx == -1) Log.d("e", "index should not be -1");

                    Pokemon p = list.get(idx);
                    intent.putExtra("name", p.name);
                    intent.putExtra("number", p.number);
                    intent.putExtra("attack", p.attack);
                    intent.putExtra("defense", p.defense);
                    intent.putExtra("flavorText", p.flavorText);
                    intent.putExtra("hp", p.hp);
                    intent.putExtra("sp_atk", p.sp_atk);
                    intent.putExtra("sp_def", p.sp_def);
                    intent.putExtra("species", p.species);
                    intent.putExtra("speed", p.speed);
                    intent.putExtra("total", p.total);
                    intent.putExtra("type", p.type.toString());

                    startActivityForResult(intent, 1);

                } else {
                    Log.d("i", "Error: Unable to select item");
                }
            }
        });

    }

    ArrayList<Integer> idx = new ArrayList<>();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                idx = searchFilters();
                if (idx.size() == 0) {
                    Toast.makeText(this, "No Pokemon Found", Toast.LENGTH_SHORT).show();
                } else {
                    ListFragment frag = new ListFragment();
                    // Begin the transaction, replace the contents of the container with the new fragment, and commit (complete the changes you made above)
                    fm.beginTransaction().replace(R.id.frame, frag).addToBackStack("").commit();
                }
                break;
        }
    }

    private ArrayList<Integer> searchFilters() {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i=0; i<list.size(); i++) out.add(i);

        String s;
        s = minAttack.getText().toString();
        int min_attack = (s.isEmpty()) ? 0 : Integer.parseInt(s);
        out = attackCutoff(min_attack, out);
        Log.d("i", "Results remaining: "+out.size());

        s = minDefense.getText().toString();
        int min_defense = (s.isEmpty()) ? 0 : Integer.parseInt(s);
        out = defenseCutoff(min_defense, out);
        Log.d("i", "Results remaining: "+out.size());

        s = minHealth.getText().toString();
        int min_health = (s.isEmpty()) ? 0 : Integer.parseInt(s);
        out = healthCutoff(min_health, out);
        Log.d("i", "Results remaining: "+out.size());

        String[] type_options = {"Grass", "Ice", "Psychic", "Dark", "Bug", "Steel", "Ghost", "Rock",
                "Flying", "Normal", "Water", "Dragon", "Fairy", "Poison", "Electric", "Fire", "Ground", "Fighting"};
        Log.d("i", Arrays.toString(type_selected));

        boolean all_true = true; // check to see if at least one type selection has been made
        for (boolean b : type_selected) {
            if (!b) all_true = false;
        }
        if (!all_true) {
            for (int i = 0; i < type_selected.length; i++) {
                if (type_selected[i]) {
                    out = byType(type_selected, type_options, out);
                    Log.d("i", "Results remaining: " + out.size());
                }
            }
        }
        Log.d("a", out.toString());
        return out;
    }

    public ArrayList<Pokemon> list = new ArrayList<>();
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

    public int findIndexName(String s) {
        for (int i=0; i<list.size(); i++) {
            if (s.equals(list.get(i).name))
                return i;
        }
        return -1; // will return index out of exception error for following code
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

    private ArrayList<Integer> byType(boolean[] type_bool, String[] type_opt, ArrayList<Integer> idx) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i : idx) {
            if (allTypes(type_bool, type_opt, list.get(i))) { // toggle between allTypes and oneType depending which one required by the project
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    // pokemon must be all of the following types selected
    private boolean allTypes(boolean[] type_bool, String[] type_opt, Pokemon p) {
        for (int i=0; i<type_bool.length; i++) {
            if (type_bool[i] && !p.isType(type_opt[i])) return false;
        }
        return true;
    }

    // pokemon only has to be one of the following types
    private boolean oneType(boolean[] type_bool, String[] type_opt, Pokemon p) {
        for (int i=0; i<type_bool.length; i++) {
            if (type_bool[i] && p.isType(type_opt[i])) return true;
        }
        return false;
    }

    private ArrayList<Integer> attackCutoff(int x, ArrayList<Integer> idx) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i : idx) {
            if (list.get(i).attack > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    private ArrayList<Integer> defenseCutoff(int x, ArrayList<Integer> idx) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i : idx) {
            if (list.get(i).defense > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

    private ArrayList<Integer> healthCutoff(int x, ArrayList<Integer> idx) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i : idx) {
            if (list.get(i).hp > x) {
                arr.add(i); // add by index
            }
        }
        return arr;
    }

}


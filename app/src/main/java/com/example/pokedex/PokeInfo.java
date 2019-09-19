package com.example.pokedex;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PokeInfo extends AppCompatActivity implements View.OnClickListener {

    Button reset;
    Button search;
    TextView name;
    String poke_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_info);

        poke_name = getIntent().getStringExtra("name");
        int number = getIntent().getIntExtra("number", 0);
        int attack = getIntent().getIntExtra("attack", 0);
        int defense = getIntent().getIntExtra("defense", 0);
        String flavorText = getIntent().getStringExtra("flavorText");
        int hp = getIntent().getIntExtra("hp", 0);
        int sp_atk = getIntent().getIntExtra("sp_atk", 0);
        int sp_def = getIntent().getIntExtra("sp_def", 0);
        String species = getIntent().getStringExtra("species");
        int speed = getIntent().getIntExtra("speed", 0);
        int total = getIntent().getIntExtra("total", 0);
        String type = getIntent().getStringExtra("type");

        String[] parts = poke_name.split(" ");
        String query = parts[0];
        if (parts.length > 1 && parts[1].equals("(")) query = String.format("%s-%s", query, parts[2]);
        String url = String.format("http://img.pokemondb.net/artwork/%s.jpg",query).toLowerCase();
        ImageView itemView = findViewById(R.id.poke_pic);
        Glide.with(itemView)  //2
                .load(url) //3
                .centerCrop() //4
                .placeholder(R.drawable.pokeball) //5
                .error(R.drawable.pokeball) //6
                .fallback(R.drawable.pokeball) //7
                .into(itemView); //8

        name = findViewById(R.id.displayName);
        TextView numbers = findViewById(R.id.poke_name);
        TextView attacks = findViewById(R.id.attack);
        TextView defenses = findViewById(R.id.defense);
        TextView hps = findViewById(R.id.hp);
        TextView sp_atks = findViewById(R.id.sp_atk);
        TextView sp_defs = findViewById(R.id.sp_def);
        TextView speciess = findViewById(R.id.species);
        TextView speeds = findViewById(R.id.speed);
        TextView totals = findViewById(R.id.total);
        TextView types = findViewById(R.id.type);
        TextView flavorTexts = findViewById(R.id.flavorText);

        name.setText(poke_name);
        numbers.setText("#: "+number);
        attacks.setText("Attack: "+attack);
        defenses.setText("Defense: "+defense);
        hps.setText("HP: "+hp);
        sp_atks.setText("Sp. Atk: "+sp_atk);
        sp_defs.setText("Sp. Def: "+sp_def);
        speciess.setText("Species: "+species);
        speeds.setText("Speed: "+speed);
        totals.setText("Total: "+total);
        types.setText("Types: "+type);
        flavorTexts.setText("Flavor Text: "+flavorText);

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(this);
        search = findViewById(R.id.webSearch);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.reset:
                intent = new Intent(PokeInfo.this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.webSearch:
                intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, poke_name); // query contains search string
                startActivity(intent);
        }
    }
}

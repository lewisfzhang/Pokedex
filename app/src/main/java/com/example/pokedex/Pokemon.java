package com.example.pokedex;

import org.json.JSONArray;
import org.json.JSONObject;

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
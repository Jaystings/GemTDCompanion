package com.jaystings.gemtdcompanion2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SpecialView extends AppCompatActivity {

    public ArrayList<String> gems;
    public ArrayList<String> gemRecipies;
    public final String [] INGREDIENT_SLATES = {"Air Slate", "Slow Slate", "Poison Slate",
        "Range Slate", "Hold Slate", "Opal Vein Slate", "Spell Slate", "Damage Slate"};
    public ArrayList<String> craftableGems;
    public ArrayList<String> matchedGems; // gems you have some ingredients for, but not all
    public LinearLayout lovSpecialGems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_view);
        gems = getIntent().getStringArrayListExtra("gems");
        if(gems == null){
            gems = new ArrayList<String>();
        }
        gemRecipies = new ArrayList<String>();

        try {
            InputStream ins = getResources().openRawResource(R.raw.recipes);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            String line = reader.readLine();
            while(line!=null){
                gemRecipies.add(line);
                //System.out.println(line); // debug
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateGems();
    }

    public void updateGems(){
        // initialize craftableGems
        craftableGems = new ArrayList<String>();
        matchedGems = new ArrayList<String>();

        lovSpecialGems = findViewById(R.id.lovSpecialGems);
        lovSpecialGems.removeAllViews();

        if(gems.isEmpty()){
            TextView lblNoGems = new TextView(this);
            lblNoGems.setText(R.string.noGems);

        } else {

            // fill craftableGems and matchedGems
            for(String recipe : gemRecipies){
                boolean found = false;
                for(String gem : gems){
                    if(recipeHas(recipe, gem)) {
                        found = true;
                        //System.out.println("Found " + gem);
                        // if the recipe contains commas, then this is not the last gem in it
                        if (recipe.contains(gem + ",")) {
                            recipe = recipe.replace(gem + ",", "");
                            // if it does not

                        } else if (recipe.contains("," + gem) && !recipe.contains("," + gem + "/")) {
                            recipe = recipe.replace("," + gem, "");
                        } else if (recipe.contains(",") && (recipe.contains("," + gem + "/")
                                || recipe.contains("/" + gem))) {
                            recipe = recipe.substring(0, recipe.indexOf(","));
                        } else if(recipe.contains("-"+gem+"/") || recipe.contains("/"+gem)){
                            recipe = recipe.substring(0,recipe.indexOf("-"));
                            craftableGems.add(recipe);
                        } else {
                            //
                            recipe = recipe.replace("-"+gem,"");
                            craftableGems.add(recipe);
                            break;
                        }
                    }
                }
                // if we didn't find all of the matches for the recipe, add it to the partially
                // finished ones
                if(found == true
                        && craftableGems.contains(recipe.replace("-", "")) == false
                        && matchedGems.contains(recipe.replace("-", "")) == false){
                    //System.out.println("adding "+recipe+" to craftable gems");
                    matchedGems.add(recipe);
                }
            }

            // display craftable, then one off, then two off recipes
            System.out.println("Craftable Gems:");
            for(String gem : craftableGems){
                String gemName = gem;
                //System.out.println(gemName);
                displayGem(lovSpecialGems, "",gemName);
            }
            System.out.println("One-Off:");
            for(String gem : matchedGems){
                if(count(gem, ",") == 0) {
                    String gemName = gem.substring(0, gem.indexOf("-"));
                    String gemReqs = gem.substring(gem.indexOf('-'), gem.length());
                    //System.out.println(gemName);
                    displayGem(lovSpecialGems, gemReqs, gemName);
                }
            }
            System.out.println("Two-Off:");
            for(String gem : matchedGems){
                if(count(gem, ",") == 1) {
                    String gemName = gem.substring(0, gem.indexOf("-"));
                    String gemReqs = gem.substring(gem.indexOf('-'), gem.length());
                    //System.out.println(gemName);
                    displayGem(lovSpecialGems, gemReqs, gemName);
                }
            }

            // fill gem text label
            TextView txtCurrentGems = findViewById(R.id.txtCurrentGems);
            txtCurrentGems.setText("");
            for(String gem : gems){
                txtCurrentGems.setText(txtCurrentGems.getText()+gem+" ");
            }
        }
    }

    public boolean recipeHas(String recipe, String gem){
        boolean hasGem = false;
        if(recipe.contains("-"+gem) || recipe.contains(","+gem)
        || recipe.contains(","+gem+"/") || recipe.contains("/"+gem)){
            hasGem = true;
        }
        return hasGem;
    }

    public int count(String str, String substr){
        int count = 0;
        while(str.contains(substr) == true){
            count++;
            str = str.replace(substr, "");
        }
        return count;
    }

    public void displayGem(LinearLayout lovLayout, String requirements, String gemName){
        //if requirements is empty, create a, "make gem," button
        LinearLayout lohGemNameReqs = new LinearLayout(getApplicationContext());
        lohGemNameReqs.setOrientation(LinearLayout.HORIZONTAL);
        TextView txtName = new TextView(this);
        txtName.setText(gemName);
        lohGemNameReqs.addView(txtName);
        if(requirements.isEmpty()){
            Button btnMakeGem = new Button(this);
            btnMakeGem.setText("Create Gem");
            btnMakeGem.setTag(gemName);
            btnMakeGem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check specialGems for the gems to remove from v.getTag
                    String gemName = (String)v.getTag();
                    for(String gem : gemRecipies){
                        String thisGemName = gem.substring(0,gem.indexOf("-"));
                        if(thisGemName.equals(gemName)){
                            String strRegents = gem.substring(gem.indexOf("-")+1, gem.length());
                            String [] regents = strRegents.split(",");
                            for (String regent : regents){
                                //System.out.println(regent); // debug
                                if(regent.contains("/")){
                                    String [] options = regent.split("/");
                                    for(String option : options){
                                        if(gems.contains(option)){
                                            System.out.println("removing "+option);
                                            gems.remove(option);
                                            break;
                                        }
                                    }
                                } else {
                                    System.out.println("removing "+regent);
                                    gems.remove(regent);
                                }
                            }
                        }
                    }
                    for (String slate : INGREDIENT_SLATES){
                        if(gemName.equals(slate)){
                            gems.add(slate);
                        }
                    }
                    updateGems();
                }
            });
            lohGemNameReqs.addView(btnMakeGem);
        } else {
            TextView txtReqs = new TextView(this);
            txtReqs.setText(requirements);
            lohGemNameReqs.addView(txtReqs);
        }
        lovSpecialGems.addView(lohGemNameReqs);
    }

    public void gotoGemEntry(View v){
        Intent i = new Intent(this, GemEntry.class);
        i.putExtra("gems", gems);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GemEntry.class);
        intent.putExtra("gems", gems);
        startActivity(intent);
    }
}
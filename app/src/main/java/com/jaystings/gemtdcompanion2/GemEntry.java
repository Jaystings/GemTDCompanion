package com.jaystings.gemtdcompanion2;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class GemEntry extends AppCompatActivity {

    public static ArrayList<String> gems;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.activity_gem_entry);
        gems = getIntent().getStringArrayListExtra("gems");
        if(gems == null){
            gems = new ArrayList<String>();
        }
        Hashtable<String, Integer> dict = new Hashtable<String, Integer>();
        ArrayList<Map.Entry<String, Integer>> sortedBackpack = sortGems(dict);
        for(int i=0; i<sortedBackpack.size(); i++) {
            String strGem = sortedBackpack.get(i).toString();
            String strGemName = strGem.substring(0, strGem.indexOf('='));
            int lblid = res.getIdentifier("lbl"+strGemName, "id",
                    this.getBaseContext().getPackageName());
            TextView lblGemAmount = findViewById(lblid);
            String strAmount = strGem.substring(strGem.indexOf('=')+1);
            lblGemAmount.setText(String.valueOf(strAmount));
        }
        updateGems();
    }

    public void gotoSpecial(View v){
        Intent i = new Intent(this, SpecialView.class);
        i.putExtra("gems", gems);
        startActivity(i);
    }

    public void updateGems(){
        TextView txtCurrentGems = findViewById(R.id.txtCurrentGems);
        txtCurrentGems.setText("");

        Hashtable<String, Integer> dict = new Hashtable<String, Integer>();
        ArrayList<Map.Entry<String, Integer>> sortedBackpack = sortGems(dict);

        txtCurrentGems.setText(sortedBackpack.toString());
    }

    public void rearrange(View v){
        // add a gold bar to separate already acquired gems, if necessary.
        String barTag = "Gold Bar";
        LinearLayout lovGems = findViewById(R.id.lovGems);
        int len = lovGems.getChildCount();
        for(int i=0; i<len-1; i++){
            View thisView = lovGems.getChildAt(i);
            if(thisView.getTag() != null) {
                String tag = (String) thisView.getTag();
                if (tag.equals(barTag)) {
                    lovGems.removeView(thisView);
                }
            }
        }
        Hashtable<String, Integer> dict = new Hashtable<String, Integer>();
        ArrayList<Map.Entry<String, Integer>> sortedBackpack = sortGems(dict);
        int i;
        for(i=0; i<sortedBackpack.size(); i++){
            String strGem = sortedBackpack.get(i).toString();
            String strGemName = strGem.substring(0, strGem.indexOf('='));
            System.out.println(strGemName);
            int lohid = res.getIdentifier("loh"+strGemName, "id",
                    this.getBaseContext().getPackageName());
            LinearLayout lohGem = findViewById(lohid);
            lovGems.removeView(lohGem);
            lovGems.addView(lohGem, i);
        }
        if(i!=0){
            ImageView imgMyBar = new ImageView(getBaseContext());
            imgMyBar.setImageResource(R.drawable.goldbar);
            imgMyBar.setTag(barTag);
            lovGems.addView(imgMyBar, i);
        }
    }

    public static ArrayList<Map.Entry<String, Integer>> sortGems(Hashtable<String, Integer> t){
        for(String gem : gems){
            if(t.containsKey(gem)){
                t.put(gem, t.get(gem) + 1);
            } else {
                t.put(gem, 1);
            }
        }
        //Transfer as List and sort it
        ArrayList<Map.Entry<String, Integer>> l = new ArrayList(t.entrySet());
        Collections.sort(l, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return l;
    }

    public void addGem(View v){
        String viewId = getResources().getResourceEntryName(v.getId());
        gems.add(viewId);
        int lblid = res.getIdentifier("lbl"+viewId, "id",
                this.getBaseContext().getPackageName());
        TextView lblGemAmount = findViewById(lblid);
        int intAmount = Integer.parseInt(lblGemAmount.getText().toString());
        lblGemAmount.setText(String.valueOf(intAmount + 1));
        updateGems();
    }

    public void remGem(View v){
        String viewId = getResources().getResourceEntryName(v.getId());
        viewId = viewId.replace("Minus", "");
        //System.out.println(viewId); //debug
        gems.remove(new String(viewId));
        int lblid = res.getIdentifier("lbl"+viewId, "id",
                this.getBaseContext().getPackageName());
        TextView lblGemAmount = findViewById(lblid);
        int intAmount = Integer.parseInt(lblGemAmount.getText().toString());
        if(intAmount != 0){
            lblGemAmount.setText(String.valueOf(intAmount - 1));
        }
        updateGems();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, GemEntry.class);
        intent.putExtra("gems", gems);
        startActivity(intent);
    }
}
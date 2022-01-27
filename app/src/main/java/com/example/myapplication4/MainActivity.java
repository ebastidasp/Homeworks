package com.example.myapplication4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private static String url = "https://mapas.igac.gov.co/server/rest/services/carto/carto10000laplata41396/MapServer/ 4?f=pjson";

    ArrayList<HashMap<String,String>> nameList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listview);
        nameList = new ArrayList<>();

        new getNames().execute();

    }

    private class getNames extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            ListAdapter listAdapter = new SimpleAdapter(MainActivity.this, nameList, R.layout.item, new String[]{"name"}, new int[]{R.id.name});

            listView.setAdapter(listAdapter);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected  Void doInBackground(Void... voids){
            Handler handler = new Handler();

            String jsonString=handler.httpServiceCall(url);
            if(jsonString != null){
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    System.out.println(jsonObject);
                    JSONArray names = jsonObject.getJSONArray("layers");
                    for(int i = 0; i < names.length(); i++){
                        JSONObject jsonObject1 = names.getJSONObject(i);
                        String id = jsonObject1.getString("id");
                        String name = jsonObject1.getString("name");

                        HashMap <String, String> nameMap = new HashMap<>();

                        nameMap.put("id", id);
                        nameMap.put("name", name);

                        System.out.println(nameMap);

                        nameList.add(nameMap);

                    }
                } catch (JSONException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Server Error1", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Server Error2", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }
}
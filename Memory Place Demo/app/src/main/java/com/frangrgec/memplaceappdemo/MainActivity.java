package com.frangrgec.memplaceappdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView placesList;
    static ArrayList<String> places;
    static ArrayList<LatLng> locations;
    static ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placesList=findViewById(R.id.placesList);

        SharedPreferences sharedPreferences=this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        places= new ArrayList<>();
        locations=new ArrayList<>();
        ArrayList<String> latitudes= new ArrayList<>();
        ArrayList<String> longitudes= new ArrayList<>();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        //Gets the places and location if there are any
        try
        {
            latitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Latitudes", ObjectSerializer.serialize((new ArrayList<>()))));
            longitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Longitudes", ObjectSerializer.serialize((new ArrayList<>()))));

            places= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Places", ObjectSerializer.serialize((new ArrayList<>()))));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //Sets the location if there are any if not sets the default
        if(!places.isEmpty()&&!latitudes.isEmpty()&&!longitudes.isEmpty())
        {
            if(places.size()==latitudes.size()&& latitudes.size()==longitudes.size())
            {
                for(int i=0; i<latitudes.size(); i++ )
                {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        }
        else
        {
            places.add("Add new location...");
            locations.add(new LatLng(0,0));

        }


        adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places);
        placesList.setAdapter(adapter);

        //Goes to the places on the map
        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent= new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("Position", position);
                startActivity(intent);
            }
        });




    }
}

package com.frangrgec.weatherappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button searchButton;
    EditText userInput;

    //http://api.openweathermap.org/data/2.5/weather?q=osijek&appid=0c07d03df7e1cdeaa8b11564237accc1


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton=findViewById(R.id.searchButton);
        userInput= findViewById(R.id.editText);


        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String encodedCityName;
                try
                {
                    encodedCityName = URLEncoder.encode(userInput.getText().toString(), "UTF-8");

                    String url="http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=0c07d03df7e1cdeaa8b11564237accc1";
                    Intent intent=new Intent(MainActivity.this, DataActivity.class);
                    intent.putExtra("URL", url);
                    startActivity(intent);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }

            }
        });

    }
}

package com.frangrgec.weatherappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataActivity extends AppCompatActivity {

    Button goBackButton;
    TextView textView1;
    TextView textView2;

    //Downloads the JSON api weather file and on postExecute extracts the data and presents it
    public class JSONDownload extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            URL url;
            HttpURLConnection httpURLConnection;
            String result="";


            try
            {
                url=new URL(urls[0]);
                httpURLConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream= httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data= reader.read();

                while (data != -1)
                {
                    char temp=(char)data;
                    result+=temp;
                    data=reader.read();
                }

                reader.close();
                inputStream.close();

                return result;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            if(result!=null)
            {

                try
                {
                    JSONObject jsonObject= new JSONObject(result);
                    String weatherInfo= jsonObject.getString("weather");

                    JSONArray jsonArray= new JSONArray(weatherInfo);

                    for(int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonPart=jsonArray.getJSONObject(i);
                        textView1.setText(jsonPart.getString("main"));
                        textView2.setText(jsonPart.getString("description"));
                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(DataActivity.this, "No city by that name!", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                textView1.setText("No data");
                textView2.setText("No data");
                Toast.makeText(DataActivity.this, "No city by that name!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        goBackButton=findViewById(R.id.goBackButton);
        textView1=findViewById(R.id.textView1);
        textView2=findViewById(R.id.textView2);

        JSONDownload task= new JSONDownload();
        task.execute(getIntent().getStringExtra("URL"));

        goBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent= new Intent(DataActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

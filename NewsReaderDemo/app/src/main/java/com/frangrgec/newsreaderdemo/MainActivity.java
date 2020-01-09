package com.frangrgec.newsreaderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> title;
    ArrayList<String> url;
    ArrayAdapter<String> adapter;

    //API KEY = 055edc5362a94fc690dd4a27e7576529

    SQLiteDatabase articlesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.listView);

        title = new ArrayList<>();
        url = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, title);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent=new Intent(getApplicationContext(),NewsActivity.class);

                intent.putExtra("newsUrl", url.get(position));
                startActivity(intent);
            }

        });

        //Creates a database and a table in it if it doesn't exists
        articlesDB= this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, title VARCHAR, content VARCHAR, url VARCHAR)");

        DownloadTask task= new DownloadTask();

        //Downloads the JSON file from the google news api
        try {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"https://newsapi.org/v2/top-headlines?country=us&category=technology&apiKey=055edc5362a94fc690dd4a27e7576529");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        updateListView();

    }

    //Updates the list view with news
    public void updateListView()
    {
        Cursor c = articlesDB.rawQuery("SELECT * FROM articles", null);

        int titleIndex = c.getColumnIndex("title");
        int urlIndex=c.getColumnIndex("url");

        if(c.moveToFirst())
        {
            title.clear();
            url.clear();

            do{
                title.add(c.getString(titleIndex));
                url.add(c.getString(urlIndex));

            }while(c.moveToNext());

            c.close();
            adapter.notifyDataSetChanged();
        }



    }


    //Download JSON news content
    public class DownloadTask extends AsyncTask<String,Void,String>
    {

        //Download the JSON file
        private String downloadFileFromInternet(String url)
        {
            StringBuilder sb = new StringBuilder();
            InputStream inStream;
            try
            {
                URL link = new URL(url);
                inStream = link.openStream();
                int i;
                int total = 0;
                byte[] buffer = new byte[8 * 1024];
                while((i=inStream.read(buffer)) != -1)
                {
                    if(total >= (1024 * 1024))
                    {
                        return "";
                    }
                    total += i;
                    sb.append(new String(buffer,0,i));
                }
            }catch(Exception e )
            {
                e.printStackTrace();
                return null;
            }catch(OutOfMemoryError e)
            {
                e.printStackTrace();
                return null;
            }
            return sb.toString();
        }

        //Process the JSON file
        @Override
        protected String doInBackground(String... urls)
        {

            try {

                String result=downloadFileFromInternet(urls[0]);

                JSONObject jsonObject= new JSONObject(result);
                JSONArray articles= jsonObject.getJSONArray("articles");

                int articleId=20;

                if(articles.length()<20)
                {
                    articleId=articles.length();
                }

                articlesDB.execSQL("DELETE FROM articles");

                for (int i=0; i<articleId;i++)
                {
                    JSONObject source= articles.getJSONObject(i);
                    if(!source.isNull("title")&&!source.isNull("url"))
                    {
                        String articleTitle= source.getString("title");
                        //Log.i("indexT",articleTitle);

                       /* String articleContent=downloadFileFromInternet(source.getString("url"));
                        Log.i("indexC","POCETAK\n"+articleContent);*/

                        String sql="INSERT INTO articles (articleID, title, content, url) VALUES (? , ? , ? , ?)";
                        SQLiteStatement statement = articlesDB.compileStatement(sql);

                            statement.bindString(1, String.valueOf(articleId));
                            statement.bindString(2, articleTitle);
                            statement.bindString(4, source.getString("url"));

                            statement.execute();


                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        //Update the view with the JSON file content
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateListView();
        }
    }
}

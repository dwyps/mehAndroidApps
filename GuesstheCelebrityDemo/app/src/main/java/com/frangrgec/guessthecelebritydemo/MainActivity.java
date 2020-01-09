package com.frangrgec.guessthecelebritydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity{

    Button startButton;
    Button ans1;
    Button ans2;
    Button ans3;
    Button ans4;
    ImageView imageView;
    ArrayList<String> name;
    ArrayList<String> imageURL;
    int actorRnd;
    Integer[] tempRnd;


    //Downloads the actor images in the background
    public class ImageDownload extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls)
        {
            HttpURLConnection httpURLConnection;
            URL url;

            try
            {
                url=new URL(urls[0]);
                httpURLConnection= (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();
                InputStream in= httpURLConnection.getInputStream();

                Bitmap image = BitmapFactory.decodeStream(in);

                return image;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Downloads the html code in the background
    public class SiteInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1) {
                    char temp = (char) data;
                    result += temp;
                    data = reader.read();
                }

                //System.out.println(result);
                inputStream.close();

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            String[] splitResult= result.split("<div class=\"sidebarInnerContainer\">");



            System.out.println(splitResult[0]);

            Pattern pattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher = pattern.matcher(splitResult[0]);

            while(matcher.find())
            {
                imageURL.add(matcher.group(1));
            }

            pattern = Pattern.compile("alt=\"(.*?)\"/>");
            matcher = pattern.matcher(result);

            while(matcher.find())
            {
                name.add(matcher.group(1));
            }

            setImage();
            setAnswers();

        }

    }


    //Sets the buttons at the beginning of the game
    public void setButton(Button button, int duration, float alpha, Boolean state)
    {
             button.animate().alpha(alpha).setDuration(duration);
             button.setEnabled(state);
    }

    //Gets the site html code
    public void getURL()
    {
        SiteInfo task= new SiteInfo();

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://www.posh24.se/kandisar");
    }

    //Checks for same answers
    public void checkRandom()
    {
        tempRnd= new Integer[]{0,0,0};

        for (int i=0; i<tempRnd.length;i++)
        {
            int rnd=new Random().nextInt(name.size());

            if (rnd == actorRnd)
            {
                checkRandom();
            }

            tempRnd[i]=rnd;
        }

    }


    //Gets and sets a random actor picture
    public void setImage()
    {

        actorRnd=new Random().nextInt(imageURL.size());

        ImageDownload task= new ImageDownload();

        Bitmap image;

        try {

            image = task.execute(imageURL.get(actorRnd)).get();
            imageView.setImageBitmap(image);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ans1.setText(name.get(1));
    }

    //Sets the button answers randomly
    public void setAnswers()
    {
        int answer = new Random().nextInt(3);

        checkRandom();

            switch (answer)
            {
                case 0:

                    ans1.setText(name.get(actorRnd));
                    ans1.setTag(1);
                    ans2.setText(name.get(tempRnd[0]));
                    ans3.setText(name.get(tempRnd[1]));
                    ans4.setText(name.get(tempRnd[2]));
                    break;

                case 1:
                    ans1.setText(name.get(tempRnd[0]));
                    ans2.setText(name.get(actorRnd));
                    ans2.setTag(1);
                    ans3.setText(name.get(tempRnd[1]));
                    ans4.setText(name.get(tempRnd[2]));
                    break;

                case 2:
                    ans1.setText(name.get(tempRnd[0]));
                    ans2.setText(name.get(tempRnd[1]));
                    ans3.setText(name.get(actorRnd));
                    ans3.setTag(1);
                    ans4.setText(name.get(tempRnd[2]));
                    break;

                case 3:
                    ans1.setText(name.get(tempRnd[0]));
                    ans2.setText(name.get(tempRnd[1]));
                    ans3.setText(name.get(tempRnd[2]));
                    ans4.setText(name.get(actorRnd));
                    ans4.setTag(1);
                    break;
            }

    }


    //Checks the user choices and puts a new round
    public void userChoiceButton(View view)
    {
        if(view.getTag().equals(1))
        {

            Toast.makeText(this, "Correct, the actor was: " + name.get(actorRnd), Toast.LENGTH_SHORT).show();
            view.setTag(0);
            setImage();
            setAnswers();

        }
        else
        {

            Toast.makeText(this, "Wrong, the actor was: " + name.get(actorRnd), Toast.LENGTH_SHORT).show();
            ans1.setTag(0);
            ans2.setTag(0);
            ans3.setTag(0);
            ans4.setTag(0);
            setImage();
            setAnswers();

        }
    }

    //Starts the game field
    public void startGame(View view)
    {

        setButton(startButton,500,0f,false);
        setButton(ans1,1000,1f,true);
        setButton(ans2,1000,1f,true);
        setButton(ans3,1000,1f,true);
        setButton(ans4,1000,1f,true);
        imageView.animate().alpha(1f).setDuration(1000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
        startButton=findViewById(R.id.startButton);

        ans1=findViewById(R.id.answer);
        ans2=findViewById(R.id.answer1);
        ans3=findViewById(R.id.answer2);
        ans4=findViewById(R.id.answer3);

        name= new ArrayList<>();
        imageURL= new ArrayList<>();


        getURL();


    }
}


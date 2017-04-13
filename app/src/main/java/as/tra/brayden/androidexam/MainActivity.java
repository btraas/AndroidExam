package as.tra.brayden.androidexam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    public static String JSON_URL;
    private EditText urlView;
    private Activity thisActivity;

    private TextView waitSecondsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String url = getString(R.string.cats_url);

        ImageView bmImage = (ImageView)findViewById(R.id.imageView);
        thisActivity = this;
        waitSecondsView = (TextView) findViewById(R.id.updateSeconds);

        try {

            String link = getString(R.string.cats_url);


            String secondsString = waitSecondsView.getText().toString();
            (new LoopingImageTask(this, link, bmImage, Integer.parseInt(secondsString))).execute();

            //(new DownloadImageTask(bmImage))
            //        .execute(link);

        } catch (Exception e) {

            e.printStackTrace();
            //Messaging.showError((Activity)container.getContext(), e.getMessage());


        }


    }


    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public static int COUNTER = 0;
        public static String LAST_URL = "";

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }



        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            Bitmap mIcon11 = null;
            try {

                // Old Direct stream
                /*
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                */

                // stackoverflow.com/questions/2659000/java-how-to-find-the-redirected-url-of-a-url
                URLConnection con = new URL( urldisplay ).openConnection();
                //System.out.println( "orignal url: " + con.getURL() );
                con.connect();
                //System.out.println( "connected url: " + con.getURL() );
                InputStream is = con.getInputStream();
                //System.out.println( "redirected url: " + con.getURL() );
                LAST_URL = con.getURL().toString();
                mIcon11 = BitmapFactory.decodeStream(is);
                is.close();


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            COUNTER++;
            bmImage.setImageBitmap(result);

        }
    }

    private static class LoopingImageTask extends AsyncTask<Void, Void, Void> {
        ImageView bmImage;
        private int seconds;
        Activity a;
        final String urldisplay;
        static boolean PAUSED = false;

        public LoopingImageTask(Activity a, String url, ImageView bmImage, int seconds) {
            this.bmImage = bmImage;
            this.seconds = seconds;
            this.a = a;
            this.urldisplay = url;
        }



        protected Void doInBackground(Void... urls) {
            //final String urldisplay = urls[0];

            try {
                Thread.sleep(1000 * seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(!PAUSED) {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new DownloadImageTask(bmImage)).execute(urldisplay);
                    }
                });
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            TextView seconds = (TextView)a.findViewById(R.id.updateSeconds);

            (new LoopingImageTask(a, urldisplay, bmImage, Integer.parseInt(seconds.getText().toString()))).execute();

        }
    }


    public void openInfo(View v) {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("counter", DownloadImageTask.COUNTER );
        intent.putExtra("url", DownloadImageTask.LAST_URL);
        this.startActivity(intent);
    }

    public void pause(View v) {
        LoopingImageTask.PAUSED = true;
    }

    public void resume(View v) {
        LoopingImageTask.PAUSED = false;
    }


}

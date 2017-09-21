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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
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

        ToggleButton t = (ToggleButton)findViewById(R.id.togglePause);
        t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LoopingImageTask.PAUSED = !b;
                String msg = b ? "resumed" : "paused";
                Toast.makeText(thisActivity, "Downloading "+msg, Toast.LENGTH_SHORT).show();
            }
        });

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
        private Activity a;

        public DownloadImageTask(Activity a, ImageView bmImage) {
            this.bmImage = bmImage;
            this.a = a;
        }



        protected Bitmap doInBackground(String... urls) {

            final String urldisplay = urls[0];

            Bitmap mIcon11 = null;
            try {

                // Old Direct stream
                /*
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                */

                // stackoverflow.com/questions/2659000/java-how-to-find-the-redirected-url-of-a-url
                final URL connectURL = new URL( urldisplay );
                final URLConnection con = connectURL.openConnection();
                //System.out.println( "orignal url: " + con.getURL() );

                try {
                    con.connect();
                    InputStream is = con.getInputStream();
                    mIcon11 = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (UnknownHostException e) {

                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(a, "Unknown host: "+con.getURL().getHost(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //System.out.println( "connected url: " + con.getURL() );
                //InputStream is = con.getInputStream();
                //System.out.println( "redirected url: " + con.getURL() );
                LAST_URL = con.getURL().toString();



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

            if(!PAUSED && seconds > 0) {

                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new DownloadImageTask(a, bmImage)).execute(urldisplay);
                    }
                });
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            final TextView seconds = (TextView)a.findViewById(R.id.updateSeconds);

            final String secondsText = seconds.getText().toString();
            int secondsInt;
            try {
                secondsInt = Integer.parseInt(secondsText);
                if(secondsInt <= 0) throw new NumberFormatException("<= 0 not allowed!");
            } catch (NumberFormatException e) {
                secondsInt = 1;
                if(!secondsText.trim().isEmpty()) {
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(a, "Invalid number: " + secondsText, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            (new LoopingImageTask(a, urldisplay, bmImage, secondsInt)).execute();

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
        Toast.makeText(this, "Downloading paused", Toast.LENGTH_SHORT).show();
    }

    public void resume(View v) {
        LoopingImageTask.PAUSED = false;
        Toast.makeText(this, "Downloading resumed", Toast.LENGTH_SHORT).show();
    }


}

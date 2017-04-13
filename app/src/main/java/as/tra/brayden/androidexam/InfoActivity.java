package as.tra.brayden.androidexam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        int counter = getIntent().getIntExtra("counter", 0);

        getSupportActionBar().setTitle("#"+counter);

        TextView urlDest = (TextView) findViewById(R.id.urlDestination);
        urlDest.setText(getIntent().getStringExtra("url"));

    }
}

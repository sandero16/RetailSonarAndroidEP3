package be.retailsonar.retailsonar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import be.retailsonar.be.retailsonar.objects.CustomEigenschapIngevuld;
import be.retailsonar.be.retailsonar.objects.EigenschappenIngevuld;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class AlleTakenActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] filiaalNamen;
    private TextView tv;
    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alle_taken);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MijnAdapter(((GlobaleVariabelen) getApplication()).getFiliaalNamen(), ((GlobaleVariabelen) getApplication()).getAddresLijst());
        mRecyclerView.setAdapter(mAdapter);

        tv=(TextView)findViewById(R.id.subtitle);
        tf= Typeface.createFromAsset(this.getAssets(),"fonts/Sofia Pro Bold.ttf");

        tv.setTypeface(tf);

        tv=(TextView)findViewById(R.id.Filialen);
        tf= Typeface.createFromAsset(this.getAssets(),"fonts/Quicksand_Bold.otf");

        tv.setTypeface(tf);

        Button mEmailSignInButton = (Button) findViewById(R.id.logout);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlleTakenActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}

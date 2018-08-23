package be.retailsonar.retailsonar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import be.retailsonar.be.retailsonar.objects.CustomEigenschapIngevuld;
import be.retailsonar.be.retailsonar.objects.EigenschappenIngevuld;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class SettingsActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private MijnAdapterSettings mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int filiaalId;
    private String[] filiaalNamen;
    private GetEigenschappenTask task;
    private TextView tv;
    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        tv=(TextView)findViewById(R.id.subtitle);
        tf= Typeface.createFromAsset(this.getAssets(),"fonts/Sofia Pro Bold.ttf");

        tv.setTypeface(tf);


        tv=(TextView)findViewById(R.id.Settings);
        tf= Typeface.createFromAsset(this.getAssets(),"fonts/Quicksand_Bold.otf");

        tv.setTypeface(tf);

        Button mEmailSignInButton = (Button) findViewById(R.id.logout);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        Button Gotomaps = (Button) findViewById(R.id.mapsbutton);
        Gotomaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, keuzeMapsActivity.class);
                startActivity(i);
            }
        });

        Intent data = getIntent();
        Bundle dataBundle = data.getExtras();
        if(dataBundle!=null&&!dataBundle.isEmpty()) if(data.hasExtra("filiaalId")){
            filiaalId = dataBundle.getInt("filiaalId");
            filiaalId = Character.getNumericValue(((GlobaleVariabelen) getApplication()).getIdLijst()[filiaalId].charAt(0));
        }
        ((GlobaleVariabelen) getApplication()).setFiliaalid(filiaalId);
        System.out.println(filiaalId);

        task = new GetEigenschappenTask(filiaalId);
        executeTask();

        synchronized (this){
            while(((GlobaleVariabelen) getApplication()).getCustomEigenschapIngevuldLijst()==null&&!((GlobaleVariabelen) getApplication()).isTaakUitgevoerd()){
                try {
                    this.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_settings);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MijnAdapterSettings(((GlobaleVariabelen) getApplication()).getEigenschappenIngevuldLijst(), ((GlobaleVariabelen) getApplication()).getCustomEigenschapIngevuldLijst());
        mRecyclerView.setAdapter(mAdapter);

        Button button = (Button) findViewById(R.id.sla_eigenschappen_op_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String[] s = mAdapter.getInhoudEditTextViewHolders();
                PostEigenschappenTask task = new PostEigenschappenTask(mAdapter.getEigenschappenIngevuldLijst(), mAdapter.getCustomEigenschapIngevuldLijst(), mAdapter.getInhoudEditTextViewHolders());
                task.execute();
            }
        });

        Button buttonTaken = (Button) findViewById(R.id.taken_weergeven);
        buttonTaken.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, TakenActivity.class);
                startActivity(i);
            }
        });


    }

    private void executeTask(){
        task.execute((Void) null);
    }
    public class GetEigenschappenTask extends AsyncTask<Void, Void, Boolean> {

        public int filiaalId;
        public String ipServer = ((GlobaleVariabelen) getApplication()).getIpServer();
        private JWT jwt = new JWT(((GlobaleVariabelen) getApplication()).getJWTKey());

        public GetEigenschappenTask(int filiaalId) {
            this.filiaalId = filiaalId;
        }

        @Override
        public void onPreExecute(){
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(false);
            System.out.println("preExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                System.out.println("start doInBackground getEigenschappenTask");
                URL url = new URL("http://" + ((GlobaleVariabelen) getApplication()).getIpServer() + ":8080/RetailSonarREST/rest_service/bedrijf/eigenschappen/get/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //JWT token toevoegen aan http-get message
                filiaalId = filiaalId-1;

                connection.addRequestProperty("Authorization", "Bearer " + jwt.maakJWTMetLoginEnId(((GlobaleVariabelen) getApplication()).getLoggedInUser(), filiaalId));
                ((GlobaleVariabelen) getApplication()).setHuidigFiliaalId(filiaalId);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int response = connection.getResponseCode();
                if(response != HttpURLConnection.HTTP_OK) {
                    System.out.println("doInBackground in SettingsActivity returnt response code: " + response);
                    return false;
                }
                //String header = connection.getHeaderField(2);
                //header = header.substring("Bearer".length()).trim();;

                String result = null;
                StringBuffer sb = new StringBuffer();
                InputStream is = null;

                try {
                    is = new BufferedInputStream(connection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String inputLine = "";
                    while ((inputLine = br.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    result = sb.toString();
                }
                catch (Exception e) {
                    System.out.println("Error reading InputStream");
                    result = null;
                }
                finally {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (IOException e) {
                            System.out.println("Error closing InputStream");
                        }
                    }
                }

                String body = sb.toString();
                body = body.substring("Bearer".length()).trim();

                Key key = ((GlobaleVariabelen) getApplication()).getJWTKey();
                Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(body);


                List<String> takenLijst = (ArrayList<String>) claims.getBody().get("taken");
                String[] taken = new String[takenLijst.size()];

                int i = 0;
                for(String s : takenLijst){
                    taken[i] = s;
                    i++;
                }
                ((GlobaleVariabelen) getApplication()).setTaken(taken);
                ((GlobaleVariabelen) getApplication()).setEigenschappenIngevuldLijst(EigenschappenIngevuld.genereerListUitHashMaps((List<LinkedHashMap>) claims.getBody().get("eigenschappenIngevuld")));
                ((GlobaleVariabelen) getApplication()).setCustomEigenschapIngevuldLijst(CustomEigenschapIngevuld.genereerListUitHashMaps((List<LinkedHashMap>) claims.getBody().get("customEigenschappenIngevuld")));

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            } else {
                System.out.println("error in OpenstaandeTakenActivity onPostExecute()");
            }
        }

        @Override
        protected void onCancelled() {
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(true);
            System.out.println("UserActivity cancelled");
        }
    }

    public class PostEigenschappenTask extends AsyncTask<Void, Void, Boolean> {

        public HashMap<String, Object> claims;
        public String ipServer = ((GlobaleVariabelen) getApplication()).getIpServer();
        private JWT jwt = new JWT(((GlobaleVariabelen) getApplication()).getJWTKey());

        public PostEigenschappenTask(List<EigenschappenIngevuld> eigenschappenLijst, List<CustomEigenschapIngevuld> customEigenschappenLijst, String[] inhouden) {
            claims = ((GlobaleVariabelen)getApplication()).getClaimsFromEigenschappen(eigenschappenLijst, customEigenschappenLijst,inhouden);
        }

        @Override
        public void onPreExecute(){
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(false);
            System.out.println("preExecute");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String requestUrl = "http://" + ((GlobaleVariabelen) getApplication()).getIpServer() + ":8080/RetailSonarREST/rest_service/bedrijf/eigenschappen/set/";
                String payload = "Bearer " + jwt.maakJWTVoorPostEigenschappen(claims);
                URL url = new URL(requestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + jwt.maakJWTMetLoginEnId(((GlobaleVariabelen) getApplication()).getLoggedInUser(), ((GlobaleVariabelen)getApplication()).getHuidigFiliaalId()));
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();
                int response = connection.getResponseCode();
                if(response != HttpURLConnection.HTTP_OK) {
                    System.out.println("doInBackground in SettingsActivity returnt response code: " + response);
                    return false;
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            } else {
                System.out.println("error in OpenstaandeTakenActivity onPostExecute()");
            }
        }

        @Override
        protected void onCancelled() {
            ((GlobaleVariabelen) getApplication()).setTaakUitgevoerd(true);
            System.out.println("UserActivity cancelled");
        }
    }
}

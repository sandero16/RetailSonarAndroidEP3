package be.retailsonar.retailsonar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class UserActivity extends Activity implements View.OnClickListener {

    private Button button;
    private EditText editText;
    private String methode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button openstaandeTaken = findViewById(R.id.openstaande_taken);
        openstaandeTaken.setOnClickListener(this);

        Button afgeslotenTaken = findViewById(R.id.afgesloten_taken);
        afgeslotenTaken.setOnClickListener(this);

        Button alleTaken = findViewById(R.id.alle_taken);
        alleTaken.setOnClickListener(this);

        Button mEmailSignInButton = (Button) findViewById(R.id.logout);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent i;

        if (id == R.id.alle_taken) {
            System.out.println("knop alle_taken ingedrukt");
            UserGetBedrijfTask bedrijfTask = new UserGetBedrijfTask(((GlobaleVariabelen) getApplication()).getIpServer());
            bedrijfTask.execute();
        }
    }

    public class UserGetBedrijfTask extends AsyncTask<Void, Void, Boolean> {

        public String ipServer;
        private JWT jwt = new JWT(((GlobaleVariabelen) getApplication()).getJWTKey());

        public UserGetBedrijfTask(String ipServer) {
            this.ipServer = ipServer;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("http://" + ((GlobaleVariabelen) getApplication()).getIpServer() + ":8080/RetailSonarREST/rest_service/bedrijf/get");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //JWT token toevoegen aan http-get message
                connection.addRequestProperty("Authorization", "Bearer " + jwt.maakGetBedrijfJWT(((GlobaleVariabelen) getApplication()).getLoggedInUser(), ((GlobaleVariabelen) getApplication()).getId()));
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int response = connection.getResponseCode();
                String header = connection.getHeaderField(2);
                header = header.substring("Bearer".length()).trim();

                Key key = ((GlobaleVariabelen) getApplication()).getJWTKey();
                Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(header);

                List<String> filiaalNamen = extractFiliaalNamen(claims.getBody());
                List<String> addressen = extractAddressen(claims.getBody());
                List<String> ids = extractIds(claims.getBody());

                String[] filialen = new String[filiaalNamen.size()];
                String[] addresLijst = new String[addressen.size()];
                String[] idLijst = new String[addressen.size()];

                for(int i=0;i<filiaalNamen.size();i++){
                    filialen[i] = filiaalNamen.get(i);
                }
                for(int i=0;i<addressen.size();i++){
                    addresLijst[i] = addressen.get(i);
                }
                for(int i=0;i<ids.size();i++){
                    idLijst[i] = ids.get(i);
                }


                ((GlobaleVariabelen) getApplication()).setFiliaalNamen(filialen);
                ((GlobaleVariabelen) getApplication()).setAddresLijst(addresLijst);
                ((GlobaleVariabelen) getApplication()).setIdLijst(idLijst);

                if(response != HttpURLConnection.HTTP_OK) {
                    System.out.println("doInBackground returnt response code: " + response);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        private List<String> extractFiliaalNamen(Map<String,Object> claims){
            List<String> filiaalNamen = new LinkedList<>();

            String filiaal = null;
            filiaal = (String) claims.get("Filiaal1");

            int i=2;
            while(filiaal!=null){
                filiaalNamen.add(filiaal);
                filiaal = (String) claims.get("Filiaal"+i);
                i++;
            }
            return filiaalNamen;
        }

        private List<String> extractAddressen(Map<String,Object> claims){
            List<String> addressen = new LinkedList<>();

            String addres = null;
            addres = (String) claims.get("Addres1");

            int i=2;
            while(addres!=null){
                addressen.add(addres);
                addres = (String) claims.get("Addres"+i);
                i++;
            }
            return addressen;
        }

        private List<String> extractIds(Map<String,Object> claims){
            List<String> ids = new LinkedList<>();

            String id = null;
            id = (String) claims.get("Id1");

            int i=2;
            while(id!=null){
                ids.add(id);
                id = (String) claims.get("Id"+i);
                i++;
            }
            return ids;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent i = new Intent(UserActivity.this, AlleTakenActivity.class);
                startActivity(i);
            } else {
                System.out.println("error in UserActivity onPostExecute()");
            }
        }

        @Override
        protected void onCancelled() {
            System.out.println("UserActivity cancelled");
        }
    }
}

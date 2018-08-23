package be.retailsonar.retailsonar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LauncherActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import be.retailsonar.retailsonar.JWT;

import be.retailsonar.retailsonar.GlobaleVariabelen;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mytv1;
    private TextView mytv2;
    private Typeface myfont1;
    private Typeface myfont2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.RSRood));

        mytv1=(TextView)findViewById(R.id.Titel1);
        mytv2=(TextView)findViewById(R.id.Titel2);
        myfont1= Typeface.createFromAsset(this.getAssets(),"fonts/Sofia Pro Bold.ttf");
        myfont2=Typeface.createFromAsset(this.getAssets(),"fonts/permanentmarker.ttf");

        mytv1.setTypeface(myfont1);
        mytv2.setTypeface(myfont2);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                   // attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        if(password==null||password.equals("")) return false;
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String mPassword;
        private JWT jwt = new JWT(((GlobaleVariabelen) getApplication()).getJWTKey());

        UserLoginTask(String uname, String password) {
            login = uname;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
            InputStream inputStream;
            System.out.println("http://"+((GlobaleVariabelen)getApplication()).getIpServer()+":8080/RetailSonarREST/rest_service/authentication/login/");
            URL url = new URL("http://"+((GlobaleVariabelen)getApplication()).getIpServer()+":8080/RetailSonarREST/rest_service/authentication/login/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Authorization", "Bearer " + jwt.maakLoginJWT(login, mPassword));
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int response = connection.getResponseCode();

            //We halen het 2e headerfield uit het http response, deze bevat het jwt token
            //String header = connection.getHeaderField(2);
            //String[] gedecodeerdeHeader = JWT.decodeer(header);




            if(response != HttpURLConnection.HTTP_OK) return false;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            ((GlobaleVariabelen)getApplication()).setLoggedInUser(login);

            //De lijst van filialen ophalen die bij deze gebruiker horen

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
            mAuthTask = null;
            showProgress(false);

            if (success) {
                ((GlobaleVariabelen)getApplication()).setLoggedInUser(login);
                Intent i = new Intent(LoginActivity.this, AlleTakenActivity.class);
                startActivity(i);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


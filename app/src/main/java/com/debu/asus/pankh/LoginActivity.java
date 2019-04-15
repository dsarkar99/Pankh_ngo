package com.debu.asus.pankh;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity  {

    Toolbar toolbar;
    int totalPoints;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private Button signOutButton;
    private ProgressDialog mProgressDialog;
    private int RC_SIGN_IN = 0;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private FirebaseAuth mAuth;

    String name;
    String uid;


    String mail;

    Uri p;
    String mno;

    private Bitmap bitmap;

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    String email,password,pass,ea;

    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);



        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Signup.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setTitle("Alert!");
                alertDialogBuilder.setMessage("Contact Anshuman Gupta for any Pankh related issues");
                alertDialogBuilder.setIcon(R.drawable.logo);
                alertDialogBuilder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                alertDialogBuilder.show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnline()) {

                    email = inputEmail.getText().toString();
                    password = inputPassword.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    data_send();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"You are not Connected to Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


/*        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }*/





    }


/*    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(currentUser==null)
        {
            updateUI(false);
        }
        else
        {
            showProgressDialog();
            updateUI(true);
        }

    }*/






    private void updateUI(boolean signedIn) {
        if (signedIn) {

            hideProgressDialog();
            Toast.makeText(this,"Welcome Back "+name,Toast.LENGTH_LONG).show();
            Intent i=new Intent(this,MainActivity.class);

            startActivity(i);
            finish();
        } else {
            Toast.makeText(this,"Couldn't Log In sorry!",Toast.LENGTH_LONG).show();
        }
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading... ");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }

    }

    private void data_send() {
        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            private ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Please Wait...", null);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Toast.makeText(LoginActivity.this, s,Toast.LENGTH_LONG).show();

                try {

                    JSONObject Object = new JSONObject(s);
                    name= Object.getString("name");
                    uid= Object.getString("uid");
                    mno= Object.getString("mno");
                    mail= Object.getString("email");
                    pass= Object.getString("pass");
                    ea= Object.getString("ea");
                    //uid =Object.getString("uid");
                    //pic= Object.getString("plink");
                    //Toast.makeText(LoginActivity.this, ea,Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("hasloggedin",true);
                    editor.putString("name", name);
                    editor.putString("uid", uid);
                    editor.putString("email", mail);
                    editor.putString("mno", mno);
                    editor.putString("pass", pass);
                    editor.putString("json",s);
                    editor.apply();
                    loading.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } catch (JSONException e) {
                    loading.dismiss();
                    Toast.makeText(LoginActivity.this, "Some problem occured!",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put("email", email);
                data.put("pass", password);

/*                data.put("url",s.getText().toString());
                data.put("email",email);
                data.put("uid",uid);*/
                String result = rh.sendPostRequest(getString(R.string.host)+"get_info.php",data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}


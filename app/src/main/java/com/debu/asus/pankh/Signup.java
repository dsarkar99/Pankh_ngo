package com.debu.asus.pankh;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.adxmi.android.os.PointsManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;

public class Signup extends AppCompatActivity {

    private Bitmap bitmap;

    private Uri filePath;
    EditText fname,femail,fphn, fpassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private int PICK_IMAGE_REQUEST = 1;
    ImageView iv;
    SharedPreferences sharedpreferences;

    //public static final String SIGNUP_URL = getString;
    //http://localhost:81


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
/*        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Signup.this, MainActivity.class));
            finish();
        }*/

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        fname = (EditText) findViewById(R.id.fname);
        femail = (EditText) findViewById(R.id.email);
        fphn= (EditText) findViewById(R.id.phn);
        fpassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signup.this);
                alertDialogBuilder.setTitle("Alert!");
                alertDialogBuilder.setMessage("Contact Anshuman Gupta for any Pankh related issues.\n"  );
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

        iv=(ImageView)findViewById(R.id.iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

/*        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, ResetPasswordActivity.class));
            }
        });*/

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,LoginActivity.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOnline()) {

                    String email = femail.getText().toString().trim();
                    String password = fpassword.getText().toString().trim();

                    if (TextUtils.isEmpty(fname.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "Enter full name!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(fphn.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "Enter mobile no.!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);
/*                    //create user
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Toast.makeText(Signup.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        //Toast.makeText(Signup.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Signup.this);
                                        alertDialogBuilder.setTitle("Alert!");
                                        alertDialogBuilder.setMessage("You can try SIGNING UP later\n" +
                                                "or\n" +
                                                "Contact Anshuman Gupta for any Pankh related issues.\n"  );
                                        alertDialogBuilder.setIcon(R.drawable.logo);
                                        alertDialogBuilder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();

                                            }
                                        });
                                        alertDialogBuilder.show();
                                    } else {
                                        signup();

                                    }
                                }
                            });*/

                                signup();
                }
                else
                {
                        Toast.makeText(Signup.this,"You are not Connected to Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(Signup.this.getContentResolver(), filePath);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void signup(){
        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            //private ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(Signup.this, "Please Wait...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();

                progressBar.setVisibility(View.GONE);

                if(s.equals("acnt_exist"))
                {
                    Toast.makeText(Signup.this,"Account already exists! Try to Log In",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(Signup.this,s,Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Signup.this, LoginActivity.class));
                    finish();
                }

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put("name",fname.getText().toString().trim());
                //data.put("uid",auth.getUid());
                data.put("email", femail.getText().toString().trim());
                data.put("mno",fphn.getText().toString().trim());
                data.put("pass", fpassword.getText().toString().trim());

/*                data.put("url",s.getText().toString());
                data.put("email",email);
                data.put("uid",uid);*/
                String result = rh.sendPostRequest(getString(R.string.host)+"signup.php",data);

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
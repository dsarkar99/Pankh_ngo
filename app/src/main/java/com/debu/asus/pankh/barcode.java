package com.debu.asus.pankh;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class barcode extends AppCompatActivity {

    String TAG = "GenerateQRCode";
    ImageView qrImage;
    String inputValue;
    //String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    QRGEncoder qrgEncoder;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedPreferences;
    Button refresh;
    ProgressDialog pd;
    Toolbar toolbar;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

         pd=new ProgressDialog(this);
         pd.setMessage("Fetching barcode");
         pd.show();

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String json= sharedPreferences.getString("json",null);
        String pass= sharedPreferences.getString("pass",null);
        String email= sharedPreferences.getString("email",null);
        fetch_json(email,pass);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        refresh = (Button) findViewById(R.id.ref);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(barcode.this,MainActivity.class));
        finish();
    }

    private void fetch_json(final String email, final String pass) {
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(barcode.this, "Please Wait...", null);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                assert s != null;
                if (s.length() > 0) {
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    qrgEncoder = new QRGEncoder(
                            s, null,
                            QRGContents.Type.TEXT,
                            smallerDimension);
                    try {
                        bitmap = qrgEncoder.encodeAsBitmap();
                        qrImage.setImageBitmap(bitmap);
                        pd.dismiss();
                        countdown();
                    } catch (WriterException e) {
                        Log.v(TAG, e.toString());
                    }

                } else {
                    Toast.makeText(barcode.this,"Error",Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(itemView.getContext(), s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];



                HashMap<String,String> data = new HashMap<>();
                data.put("email", email);
                data.put("pass", pass);

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

    void countdown()
    {
        final TextView tv = (TextView) findViewById( R.id.timer );
        new CountDownTimer(30000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                tv.setText("You will be automatically redirected to Home page after "+String.format("%d mins %d secs",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                tv.setText("Time's Up!");
                startActivity(new Intent(barcode.this,MainActivity.class));
                finish();
            }
        }.start();
    }
}

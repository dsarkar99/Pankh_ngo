package com.debu.asus.pankh;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import net.adxmi.android.os.PointsManager;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;
import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;


public class supdate extends Fragment implements View.OnClickListener  {

    Button btnc,btnu;
    EditText status;
    ImageView iv;
    SharedPreferences sharedpreferences;



    public static final String UPLOAD_URL = "http://192.168.0.6:81/pankhngo/upload.php";
    public static final String UPLOAD_KEY = "image";



    private int PICK_IMAGE_REQUEST = 1;
EditText s;

    private Bitmap bitmap;

    private Uri filePath;
    public supdate() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_supdate, container, false);

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        btnc = (Button)v.findViewById(R.id.btnc);
        btnu = (Button) v.findViewById(R.id.btnu);
        status = (EditText) v.findViewById(R.id.status);
        s = (EditText) v.findViewById(R.id.link);
        iv = (ImageView)v.findViewById(R.id.iv);

        btnc.setOnClickListener(this);
        btnu.setOnClickListener(this);

        return v;
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                iv.setImageBitmap(bitmap);
                btnu.setVisibility(View.VISIBLE);
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

    private void uploadImage(){
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                PointsManager.getInstance(getContext()).awardPoints(10);
                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("PANKH NGO!")
                        .setContentText("You have Earned 10 points!!")
                        .show();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new Newsfeed()).addToBackStack("my_fragment").commit();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String name = sharedpreferences.getString("name",null);
                String pic = sharedpreferences.getString("pic",null);

                FirebaseAuth auth=FirebaseAuth.getInstance();
                String uid = auth.getUid();
                String email=sharedpreferences.getString("email",null);
                String uploadImage = getStringImage(bitmap);



                Date date= new Date();
                long time = date.getTime();



                HashMap<String,String> data = new HashMap<>();
                data.put("name",name);
                data.put(UPLOAD_KEY, uploadImage);
                data.put("status",status.getText().toString());
                if(pic==null)
                {
                    data.put("pic","https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png");
                }
                else
                {
                    data.put("pic",pic);
                }

                data.put("ts", String.valueOf(time));
                data.put("url",s.getText().toString());
                data.put("email",email);
                data.put("uid",uid);
                String result = rh.sendPostRequest(UPLOAD_URL,data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }


    public void onClick(View v) {
        if (v == btnc) {
            showFileChooser();
        }

        if(v == btnu){
            uploadImage();
        }
    }
}

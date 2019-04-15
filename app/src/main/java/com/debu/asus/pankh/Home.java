package com.debu.asus.pankh;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.debu.asus.pankh.app.AppController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.simple.parser.*;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Objects;

import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;


public class Home extends Fragment {

    SliderLayout sliderLayout;
    String a,b,c,d,e,f,g;
    RelativeLayout col;
    Bitmap bitmap;
    String pic;
    ImageView imageView;
    TextView ea;
    SharedPreferences sharedpreferences;
    private ProgressDialog loading;
    DatabaseReference table_user;

    String url[];

    public static final String MyPREFERENCES = "MyPrefs" ;
    public Home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        loading = ProgressDialog.show(getActivity(), null, "Please Wait...");
        loading.setIcon(R.drawable.logo);

        table_user = FirebaseDatabase.getInstance().getReference().child("pankhngo").child("img_url");

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String uid =sharedpreferences.getString("uid",null);
                String name =sharedpreferences.getString("name",null);
                get_events(uid,name);
                //ea.setText("Till Now you have attended "+ sharedpreferences.getString("ea",null)+" Events");
            }
        }, 2000);



        //Bundle b=getActivity().getIntent().getExtras();
        col = v.findViewById(R.id.col);
        //imageView=(ImageView)v.findViewById(R.id.profile);
        ea= (TextView) v.findViewById(R.id.ea);
        Button btn=(Button) v.findViewById(R.id.post);


        /*brc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),barcode.class));
            }
        });*/
/*        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new Newsfeed()).addToBackStack("my_fragment").commit();
            }
        });*/



        sliderLayout = v.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.SLIDE); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(2); //set scroll delay in seconds :

        setSliderViews();

        return v;
    }


    private void setSliderViews() {
        table_user.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               a=dataSnapshot.child("1").getValue(String.class);
               b=dataSnapshot.child("2").getValue(String.class);
               c=dataSnapshot.child("3").getValue(String.class);
               d=dataSnapshot.child("4").getValue(String.class);
               e=dataSnapshot.child("5").getValue(String.class);
               f=dataSnapshot.child("6").getValue(String.class);
               g=dataSnapshot.child("7").getValue(String.class);

               //Toast.makeText(getContext(), a,Toast.LENGTH_LONG).show();


               for (int i = 0; i <= 6; i++) {

                   SliderView sliderView = new SliderView(getContext());

                   switch (i) {
                       case 0:
                           sliderView.setDescription("");
                           sliderView.setImageUrl(a);
                           //Toast.makeText(getActivity(), a,Toast.LENGTH_LONG).show();
                           break;
                       case 1:
                           sliderView.setDescription("");
                           sliderView.setImageUrl(b);
                           //Toast.makeText(getContext(), b,Toast.LENGTH_LONG).show();
                           break;
                       case 2:
                           sliderView.setImageUrl(c);

                           break;
                       case 3:
                           sliderView.setImageUrl(d);
                           break;

                       case 4:
                           sliderView.setImageUrl(e);
                           break;

                       case 5:
                           sliderView.setImageUrl(f);
                           break;
                       case 6:
                           sliderView.setImageUrl(g);
                           break;
                   }

                   sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);

                   //at last add this view in your layout :
                   sliderLayout.addSliderView(sliderView);
               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });





        loading.dismiss();
    }

    //getting number of events attended
    void get_events(final String uid, final String name)
    {
        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Bitmap,Void,String> {


            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getContext(),temp,Toast.LENGTH_LONG).show();

                if(isOnline()) {

                    Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
                    if ((Integer.parseInt(s.trim())) != 9) {
                        ea.setText("You have attended " + s + "Events");
                    } else {
                        ea.setText("Congratulations! You have Successfully attended " + s +" Events");
                    }


                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("ea", s);
                    editor.apply();
/*                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ea", s);
                editor.apply();*/
                    loading.dismiss();
                }
                else {
                    ea.setText("Please Check Your Internet Connection");
                    ea.setBackgroundColor(getResources().getColor(R.color.btn_logut_bg));
                    loading.dismiss();
                    Toast.makeText(getContext(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put("uid", uid);
                data.put("name", name);

                String result = rh.sendPostRequest(getString(R.string.host)+"count_event.php",data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

    }



    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }




}

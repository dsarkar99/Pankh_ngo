package com.debu.asus.pankh;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.debu.asus.pankh.adapter.Eventadapter;
import com.debu.asus.pankh.adapter.achieveadapter;
import com.debu.asus.pankh.app.AppController;
import com.debu.asus.pankh.app.MyApplication;
import com.debu.asus.pankh.data.Event;
import com.debu.asus.pankh.data.achieve;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;
import static net.adxmi.android.b.a.h.a.R;


public class Profile extends Fragment implements Eventadapter.ContactsAdapterListener,achieveadapter.ContactsAdapterListener {

RecyclerView recyclerView,recyclerView1;
    private List<Event> contactList;
    private List<achieve> achieveList;
    private Eventadapter mAdapter;
    private achieveadapter mAdapter1;
    ProgressDialog mProgressDialog;
    ProgressDialog pd;
    String json,uid;

    View v;

    SharedPreferences data;
Bitmap bitmap;
    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerView = v.findViewById(R.id.revent);
        contactList = new ArrayList<>();
        mAdapter = new Eventadapter(getActivity(), contactList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView1 = v.findViewById(R.id.achieve);
        achieveList = new ArrayList<>();
        mAdapter1 = new achieveadapter(getActivity(), achieveList, this);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getContext());
        recyclerView1.setLayoutManager(mLayoutManager1);
        recyclerView1.setAdapter(mAdapter1);


        pd=new ProgressDialog(getContext());
        pd.setMessage("Loading Events");

        data=v.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        json=data.getString("json",null);
        uid=data.getString("uid",null);

        jsonreq();
        jsonreq_achieve();


        return v;
    }

    void jsonreq(){
        // making fresh volley request and getting json
        JsonArrayRequest jsonReq = new JsonArrayRequest(
                getString(R.string.host)+"get_event.php", new Response.Listener<JSONArray>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {
                //Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_LONG).show();
                if(response.toString().length()==0)
                {

                    Toast.makeText(getActivity(),"Sorry, No available Events!",Toast.LENGTH_LONG).show();
                }
                else
                {   pd.show();
                    parseJsonFeed(response);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }



    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONArray response) {

            List<Event> items = new Gson().fromJson(response.toString(), new TypeToken<List<Event>>() {
            }.getType());


            //JSONArray feedObj = (JSONArray) feedArray.get(i);


            ArrayList<String> al = new ArrayList<>();

            Event item = new Event();
            for (int i = 0; i < items.size(); i++) {

                //al.add(items.get(i).getName());

                item.setname(items.get(i).getname());
                item.setLocation(items.get(i).getlocation());
                item.setDate(items.get(i).getDate());
                item.setDesp(items.get(i).getDesp());


                contactList.clear();
                contactList.addAll(items);


            }


        // notify data changes to list adapater
        pd.dismiss();
        mAdapter.notifyDataSetChanged();
        //hideProgressDialog();
    }

    @Override
    public void onContactSelected(Event contact) {

    }

    void jsonreq_achieve()
        {
            @SuppressLint("StaticFieldLeak")
            class UploadImage extends AsyncTask<Bitmap,Void,String> {

                private ProgressDialog loading;
                RequestHandler rh = new RequestHandler();

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading = ProgressDialog.show(getActivity(), "Please Wait...", null);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(getContext(), s,Toast.LENGTH_LONG).show();
                    if(isOnline()) {
                        if (!s.equals("na_event")) {

                            // Toast.makeText(LoginActivity.this, s,Toast.LENGTH_LONG).show();
                            List<achieve> items = new Gson().fromJson(s, new TypeToken<List<achieve>>() {
                            }.getType());
                            achieve item = new achieve();
                            for (int i = 0; i < items.size(); i++) {
                                item.setename(items.get(i).getename());
                                item.setDate(items.get(i).getDate());
                                achieveList.clear();
                                achieveList.addAll(items);
                            }
                            // notify data changes to list adapater
                            loading.dismiss();
                            mAdapter1.notifyDataSetChanged();
                        } else {
                            loading.dismiss();
                            Toast.makeText(getContext(), "You have not attended any events till now!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {   loading.dismiss();
                        Toast.makeText(getContext(), "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected String doInBackground(Bitmap... params) {
                    bitmap = params[0];

                    HashMap<String,String> data = new HashMap<>();
                    data.put("uid", uid);

                    String result = rh.sendPostRequest(getString(R.string.host)+"get_achieve.php",data);

                    return result;
                }
            }

            UploadImage ui = new UploadImage();
            ui.execute(bitmap);
        }

    @Override
    public void onContactSelected(achieve contact) {

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

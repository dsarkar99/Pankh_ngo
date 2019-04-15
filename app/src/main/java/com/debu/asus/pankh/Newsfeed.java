package com.debu.asus.pankh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.debu.asus.pankh.adapter.FeedListAdapter;
import com.debu.asus.pankh.app.AppController;
import com.debu.asus.pankh.data.FeedItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class Newsfeed extends Fragment {

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    //ImageView imageView;
    SwipeRefreshLayout mSwipeRefreshLayout;


    ProgressDialog pd;

    public Newsfeed() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        listView = (ListView) v.findViewById(R.id.list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.link);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new Newsfeed()).addToBackStack("my_fragment").commit();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        feedItems = new ArrayList<>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);


        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(getString(R.string.host)+"ngo.php");
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONArray(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {

            jsonreq();

        }

        jsonreq();

        return v;
    }

    void jsonreq(){
        // making fresh volley request and getting json
        JsonArrayRequest jsonReq = new JsonArrayRequest(
                getString(R.string.host)+"ngo.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                parseJsonFeed(response);
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
        pd=new ProgressDialog(getContext());
        pd.setMessage("Loading");
        pd.show();
        //JSONArray feedArray = response.getJSONArray(Integer.parseInt("feed"));

        List<FeedItem> items = new Gson().fromJson(response.toString(), new TypeToken<List<FeedItem>>() {
        }.getType());


        //JSONArray feedObj = (JSONArray) feedArray.get(i);

        ArrayList<String> al=new ArrayList<>();

        FeedItem item = new FeedItem();
        for (int i = 0; i < items.size(); i++) {

            //al.add(items.get(i).getName());

            item.setId(items.get(i).getId());
            item.setName(items.get(i).getName());
            item.setProfilePic(items.get(i).getProfilePic());
            String s=items.get(i).getImge();
            item.setImge(s);
            item.setStatus(items.get(i).getStatus());
            item.setUrl(items.get(i).getUrl());
            item.setTimeStamp(items.get(i).getTimeStamp());

            if(feedItems!=null) {
                feedItems.clear();
            }
            feedItems.addAll(items);
            pd.dismiss();

        }

        // notify data changes to list adapater

        listAdapter.notifyDataSetChanged();
    }




}

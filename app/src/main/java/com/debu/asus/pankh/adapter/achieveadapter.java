package com.debu.asus.pankh.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.debu.asus.pankh.R;

import com.debu.asus.pankh.data.achieve;

import java.util.HashMap;
import java.util.List;

public class achieveadapter extends RecyclerView.Adapter<achieveadapter.MyViewHolder> {
    private Context context;
    private List<achieve> achieveList;
    private ContactsAdapterListener listener;
    String s;
    View itemView;

    String name,email,mno,uid,event;
    String ea;

    Bitmap bitmap;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView name,date;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ename);
            date= (TextView) view.findViewById(R.id.edate);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public achieveadapter(Context context, List<achieve> achieveList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.achieveList = achieveList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achieve_row, parent, false);
        //Toast.makeText(itemView.getContext(), mno,Toast.LENGTH_LONG).show();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final achieve pdf = achieveList.get(position);
        holder.name.setText(pdf.getename());
        holder.date.setText("Date: " + pdf.getDate());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(itemView.getContext(), "You have attended "+pdf.getename()+" Event on "+pdf.getDate(),Toast.LENGTH_LONG).show();

            }
        });
    }




    @Override
    public int getItemCount() {
        return achieveList.size();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(achieve contact);
    }
}
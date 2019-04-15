package com.debu.asus.pankh.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.debu.asus.pankh.RequestHandler;
import com.debu.asus.pankh.data.Event;
import com.debu.asus.pankh.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;

/**
 * Created by ravi on 16/11/17.
 */

public class Eventadapter extends RecyclerView.Adapter<Eventadapter.MyViewHolder>  {
    private Context context;
    private List<Event> contactList;
    private ContactsAdapterListener listener;
    String s;
    View itemView;

    String name,email,mno,uid,event;
    String ea;

    Bitmap bitmap;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView name,location,date,desp;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ename);
            location= (TextView) view.findViewById(R.id.elocation);
            date= (TextView) view.findViewById(R.id.edate);
            desp= (TextView) view.findViewById(R.id.edesp);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public Eventadapter(Context context, List<Event> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_row, parent, false);

        SharedPreferences data=itemView.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        name =data.getString("name",null);
        uid =data.getString("uid",null);
        email =data.getString("email",null);
        mno =data.getString("mno",null);
        ea=data.getString("ea",null);
        //Toast.makeText(itemView.getContext(), mno,Toast.LENGTH_LONG).show();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Event pdf = contactList.get(position);
        holder.name.setText(pdf.getname());

        holder.location.setText(pdf.getlocation());
        holder.date.setText("Date:"+pdf.getDate());
        holder.desp.setText(pdf.getDesp());
           holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if ((Integer.parseInt(ea.trim()))!=9)
                    {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
                    alertDialogBuilder.setTitle("Alert! You can attend one event at a time");
                    alertDialogBuilder.setMessage("Are you interested to attend " + pdf.getname() + " Event in " + pdf.getlocation());
                    alertDialogBuilder.setIcon(R.drawable.logo);
                    alertDialogBuilder.setPositiveButton("Interested", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            event = pdf.getname();
                            json_send(itemView);

                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialogBuilder.show();
                    }
                    else
                    {

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(itemView.getContext());
                        alertDialogBuilder.setTitle("Dear "+name);
                        alertDialogBuilder.setIcon(R.drawable.logo);
                        alertDialogBuilder.setMessage("You have already attended 9 events.\n" +
                                "Its time to collect your Pankh Certificate\n" +
                                "Thank You\n" +
                                "\nDO YOU WANT TO ATTEND MORE EVENTS?\n");
                        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                        alertDialogBuilder.setPositiveButton("Yes,I want to attend more!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                event = pdf.getname();
                                json_send(itemView);

                            }
                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialogBuilder.show();
/*                        Snackbar sb= Snackbar.make(itemView,"Sorry,You have already completed 9 Events!", Snackbar.LENGTH_LONG);
                        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        sb.show();*/
                    }



                        //Toast.makeText(context, "Money of " + pdf.getname() + " is Paid!", Toast.LENGTH_LONG).show();
                    }
            });

        }




    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Event contact);
    }


    private void json_send(final View itemView) {
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(itemView.getContext(), "Please Wait...", null);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                 loading.dismiss();
/*                Snackbar sb= Snackbar.make(itemView,s, Snackbar.LENGTH_LONG);
                //sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                sb.show();*/
                Toast.makeText(itemView.getContext(), s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];



                HashMap<String,String> data = new HashMap<>();
                data.put("name", name);
                data.put("uid", uid);
                data.put("email", email);
                data.put("mno", mno);
                data.put("event", event);

/*                data.put("url",s.getText().toString());
                data.put("email",email);
                data.put("uid",uid);*/
                String result = rh.sendPostRequest(itemView.getContext().getString(R.string.host)+"interested.php",data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }
}

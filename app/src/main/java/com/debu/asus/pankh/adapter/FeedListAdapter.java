package com.debu.asus.pankh.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.debu.asus.pankh.FeedImageView;
import com.debu.asus.pankh.MainActivity;
import com.debu.asus.pankh.Newsfeed;
import com.debu.asus.pankh.R;
import com.debu.asus.pankh.RequestHandler;
import com.debu.asus.pankh.app.AppController;
import com.debu.asus.pankh.data.FeedItem;




import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;

public class FeedListAdapter extends BaseAdapter  {
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	public static final String DEL_URL = "http://192.168.0.6:81/pankhngo/del.php";

	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;
	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			assert inflater != null;

			convertView = inflater.inflate(R.layout.feed_item, null);
		}

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();
		final FeedItem item = feedItems.get(position);
		final TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);
		final TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic);
		FeedImageView feedImageView = (FeedImageView) convertView
				.findViewById(R.id.feedImage1);

		final Button del=(Button) convertView.findViewById(R.id.del);
		SharedPreferences pref=convertView.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		final String s=pref.getString("name",null);
		if(Objects.equals(item.getName(), s))
		{    del.setVisibility(View.VISIBLE);
			final View finalConvertView = convertView;
			del.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View view) {
					//Toast.makeText(view.getContext(),""+item.getId(),Toast.LENGTH_SHORT).show();
					uploadImage(String.valueOf(item.getId()), finalConvertView);

				}
			});
		}
		else
		{
			del.setVisibility(View.GONE);
		}










		name.setText(item.getName());

		// Converting timestamp into x ago format
		CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
				Long.parseLong(item.getTimeStamp()),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
		timestamp.setText(timeAgo);

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getStatus())) {
			statusMsg.setText(item.getStatus());
			statusMsg.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
		}

		// Checking for null feed url
		if (item.getUrl() != null) {
			url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
					+ item.getUrl() + "</a> "));

			// Making url clickable
			url.setMovementMethod(LinkMovementMethod.getInstance());
			url.setVisibility(View.VISIBLE);
		} else {
			// url is null, remove from the view
			url.setVisibility(View.GONE);
		}

		// user profile pic
		profilePic.setImageUrl(item.getProfilePic(), imageLoader);

		// Feed image
		if (item.getImge() != null) {
			feedImageView.setImageUrl(item.getImge(), imageLoader);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView
					.setResponseObserver(new FeedImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			feedImageView.setVisibility(View.GONE);
		}

		return convertView;
	}

	void uploadImage(final String id, final View convertview){
		final Bitmap[] bitmap = new Bitmap[1];
		@SuppressLint("StaticFieldLeak")
		class UploadImage extends AsyncTask<Bitmap,Void,String> {

			ProgressDialog loading;
			RequestHandler rh = new RequestHandler();

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(convertview.getContext(), "Deleting...", null,true,true);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				loading.dismiss();
				Toast.makeText(convertview.getContext(), s, Toast.LENGTH_SHORT).show();
				FragmentManager fragmentManager = ((FragmentActivity)convertview.getContext()).getSupportFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, new Newsfeed()).commit();
			}

			@Override
			protected String doInBackground(Bitmap... params) {
				bitmap[0] = params[0];

				HashMap<String,String> data = new HashMap<>();
				data.put("id",id);

				return rh.sendPostRequest(DEL_URL,data);
			}
		}
		UploadImage ui = new UploadImage();
		ui.execute(bitmap[0]);
	}


}

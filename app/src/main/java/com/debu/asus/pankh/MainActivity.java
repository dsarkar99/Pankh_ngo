package com.debu.asus.pankh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import net.adxmi.android.os.EarnPointsOrderInfo;
import net.adxmi.android.os.EarnPointsOrderList;
import net.adxmi.android.os.PointsChangeNotify;
import net.adxmi.android.os.PointsEarnNotify;
import net.adxmi.android.os.PointsManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.debu.asus.pankh.LoginActivity.MyPREFERENCES;

public class MainActivity extends AppCompatActivity implements PointsChangeNotify,
        PointsEarnNotify,View.OnClickListener {

    Toolbar toolbar;
    int totalPoints;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    GoogleApiClient mGoogleApiClient;
    TextView tv;

    SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs" ;

    String uid,name;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        transparentToolbar();


        tv= (TextView) findViewById(R.id.tvname);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setText("Welcome "+ sharedpreferences.getString("name",null)+"!");
                //ea.setText("Till Now you have attended "+ sharedpreferences.getString("ea",null)+" Events");
            }
        }, 3000);

        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);

            titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            titleTextView.setTextColor(getResources().getColor(R.color.black));
            titleTextView.setFocusable(true);
            titleTextView.setFocusableInTouchMode(true);
            titleTextView.requestFocus();
            titleTextView.setSingleLine(true);
            titleTextView.setSelected(true);
            titleTextView.setMarqueeRepeatLimit(-1);

        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }

        ImageView imageView = (ImageView) findViewById(R.id.iv);

        Glide.with(this)
                .load("https://pankhfoundation.in/wp-content/uploads/2018/09/logo.png")
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        totalPoints = PointsManager.getInstance(this).queryPoints();

/*        PointsManager.getInstance(MainActivity.this).awardPoints(25);

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Great!")
                .setContentText(25+" Points Successfully Received")
                .setCustomImage(R.drawable.custom_img)
                .show();*/

        loadFragment(new Home());
        //transparentToolbar();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        DataModel[] drawerItem = new DataModel[3];

        drawerItem[1] = new DataModel(R.drawable.connect, "Profile");
        drawerItem[0] = new DataModel(R.drawable.ic_home, "Home");
        drawerItem[2] = new DataModel(R.drawable.ic_feed, "News Feed");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setupDrawerToggle();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    toolbar.setTitle("Profile");
                    fragment = new Profile();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_home:
                    toolbar.setTitle("Home");
                    fragment = new Home();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_feed:
                    toolbar.setTitle("News Feed");
                    fragment = new Newsfeed();
                    loadFragment(fragment);
                    return true;
/*                case R.id.navigation_status:
                    toolbar.setTitle("Write Status");
                    fragment = new supdate();
                    loadFragment(fragment);
                    return true;*/
              /*case R.id.navigation_cart:
                    //toolbar.setTitle("Cart");
                    fragment = new CartFragment();
                    loadFragment(fragment);
                    return true;
*/

            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {

        // load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("my_fragment").commit();
    }

    @Override
    public void onClick(View view) {
        if(view==findViewById(R.id.post))
        {
            loadFragment(new Newsfeed());
        }
    }

/*    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }*/

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new Home();

                break;
            case 1:
                fragment = new Newsfeed();
                break;
            case 2:
                fragment = new Profile();
                break;

            default:
                break;
        }

        if (fragment != null) {
            loadFragment(fragment);
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onPointBalanceChange(int i) {

    }

    @Override
    public void onPointEarn(Context context, EarnPointsOrderList list) {

        for (int i = 0; i < list.size(); ++i) {
            EarnPointsOrderInfo info = list.get(i);
            Log.i("Adxmi", info.getMessage());
        }

    }

    // Showing Pounts on ToolBar Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.points).setTitle("Points :"+totalPoints);
        menu.findItem(R.id.ltxt).setTitle("Sign Out");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

/*            case R.id.points:
                return true;*/

            case R.id.ltxt:
                signout();
                return true;

            case R.id.code:
                check_eligible(sharedpreferences.getString("uid",null),sharedpreferences.getString("name",null));
                return true;


/*            case R.id.sync:
                Intent intent = getIntent();
                finish();
                startActivity(intent);*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void share()
    {
        try
        { Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = Config.share_text+"\n";
            sAux = sAux + Config.share_link+"\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        }
        catch(Exception e)
        { //e.toString();
        }
    }

    void signout()
    {
        SharedPreferences setting =getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("hasloggedin",false);
        editor.remove("name").apply();
        editor.apply();



        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();


        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(i);
        finish();
/*        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });*/
    }


    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() >0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
/*            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);*/
        }

    }

    private void transparentToolbar() {
/*        if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }*/
        if (Build.VERSION.SDK_INT >= 17) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            //setWindowFlag(this, WindowManager.LayoutParams.FLAG_FULLSCREEN, false);
            //getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void check_eligible(final String uid, final String name) {


        @SuppressLint("StaticFieldLeak")
        class UploadImage extends AsyncTask<Bitmap,Void,String> {

            private ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(MainActivity.this, "Please Wait...", null);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //Toast.makeText(MainActivity.this, s,Toast.LENGTH_LONG).show();
                if(s.toString().equals("Y"))
                {   loading.dismiss();
                    startActivity(new Intent(MainActivity.this,barcode.class));
                    finish();
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Sorry ,It seems you have not signed up for any Event. SIGN UP for an Event in the Profile Section to access this Barcode.\n"+
                                                   "\nThank You!\n");
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Take me there.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            loading.dismiss();
                            loadFragment(new Profile());

                        }
                    })
                            .setNegativeButton("I'll do it Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    loading.cancel();
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialogBuilder.show();
                }


            }

            @Override
            protected String doInBackground(Bitmap... params) {
                bitmap = params[0];

                HashMap<String,String> data = new HashMap<>();
                data.put("name", name);
                data.put("uid", uid);

/*                data.put("url",s.getText().toString());
                data.put("email",email);
                data.put("uid",uid);*/
                String result = rh.sendPostRequest(getString(R.string.host)+"barcode_check.php",data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }

    private String downloadText() {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = openHttpConnection();
        } catch (IOException e1) {
            return "";
        }

        String str = "";
        if (in != null) {
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char[] inputBuffer = new char[BUFFER_SIZE];
            try {
                while ((charRead = isr.read(inputBuffer)) > 0) {
                    // ---convert the chars to a String---
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    str += readString;
                    inputBuffer = new char[BUFFER_SIZE];
                }
                in.close();
            } catch (IOException e) {
                return "";
            }
        }
        return str;
    }

    private InputStream openHttpConnection() throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(getString(R.string.host)+"deploy/versioninfo.txt");
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    public static int getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return  Integer.parseInt(pi.versionName);
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    void check_version()
    {
        if(downloadText().compareTo(""+getVersionCode(getApplicationContext()))!=0)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage("App Update Available!\n"+
            "\nUpdate Now to continue with this App\n");
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
            alertDialogBuilder.setPositiveButton("Update Now.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.host)+"deploy/release.apk"));
                    startActivity(updateIntent);


                }
            })
                    .setNegativeButton("I'll do it Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                            finish();
                        }
                    });
            alertDialogBuilder.show();


        }
    }

}

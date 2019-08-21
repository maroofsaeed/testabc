package maroof.oldhindisongs;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maroof.oldhindisongs.App.AppController;
import maroof.oldhindisongs.R;
import maroof.oldhindisongs.utils.CategoriesAdapter;
import maroof.oldhindisongs.utils.Constants;
import maroof.oldhindisongs.utils.NewsAdapter;
import maroof.oldhindisongs.utils.Utilities;

public class NewsActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    private ListView listCategories;

    private int addLimitCount, packageLimitCount;

    private static final int MY_SOCKET_TIMEOUT_MS = 30000;

    private List Cats;
    private NewsAdapter adapterCategories;

    private SwipeRefreshLayout swipeContainer;

    private TextView txtDesc;

    private AdView mAdView;

    private AdView mAdMobAdView;
    InterstitialAd mAdMobInterstitialAd;

    private Categories itemToOpen;

    private ArrayList<Categories> listCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        mAdView = (AdView) findViewById(R.id.adView);


        initializeAdd();

        addLimitCount = 0;

        listCategories = (ListView) findViewById(R.id.listViewCategories);
        Cats = new ArrayList();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutCategory);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //doCategoriesData();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        txtDesc = (TextView) findViewById(R.id.txtDesc);

        listCat = (ArrayList<Categories>) getIntent().getSerializableExtra("catsArr");


        try{
            if(listCat.size() > 0){

                for(int i=0; i<listCat.size(); i++){

                    Categories c = listCat.get(i);
                    System.out.println("data is " + c.getTitle());
                    if(c.isStatus()){
                        Cats.add(c);
                    }

                    if(i == 0){
                        //txtDesc.setText(c.getMainCatDescription());
                        if(c.isPackageIsAddActive()){
                            AdRequest adRequest = new AdRequest.Builder()
                                    .build();
                            mAdView.loadAd(adRequest);
                        }
                    }
                }


                adapterCategories = new NewsAdapter(this, Cats);
                listCategories.setAdapter(adapterCategories);

                listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if(getIntent().getStringExtra("title").toString().equals("News")){
                            itemToOpen = (Categories)Cats.get(position);

                            Intent i = new Intent(NewsActivity.this, NewsDetailActivity.class);
                            i.putExtra("title", itemToOpen.getTitle());
                            i.putExtra("description", itemToOpen.getKeyword());
                            i.putExtra("img", itemToOpen.getImage());
                            startActivity(i);
                        } else {
                            System.out.println("sdfdsfdsfdsf");

                            itemToOpen = (Categories)Cats.get(position);

                            if(itemToOpen.isRedirection()){
                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(itemToOpen.getRedirectionApp());
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(launchIntent);//null pointer check in case package name was not found
                                } else {
                                    launchIntent = new Intent(Intent.ACTION_VIEW);
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    launchIntent.setData(Uri.parse("market://details?id=" + itemToOpen.getRedirectionApp()));
                                    startActivity(launchIntent);
                                }
                            } else {

                                addLimitCount++;

                                if(addLimitCount == 1){
                                    System.out.println("i am at position 1");
                                    if(itemToOpen.isPackageIsAddActive()) {
                                        showInterstitialAd();
                                        if(itemToOpen.getPackageAddlimit() == 1){
                                            addLimitCount = 0;
                                        }
                                    } else {
                                        openVideosIntent();
                                    }
                                } else {
                                    if(addLimitCount == itemToOpen.getPackageAddlimit()){
                                        if(itemToOpen.isPackageIsAddActive()) {
                                            System.out.println("i am at position 2 " + itemToOpen.getPackageAddlimit());
                                            showInterstitialAd();
                                            addLimitCount = 0;
                                        } else{
                                            openVideosIntent();
                                        }
                                    } else {
                                        System.out.println("i am at position 3 " + addLimitCount);
                                        openVideosIntent();
                                    }
                                }

                            }
                        }

                    }
                });
            }
        } catch(Exception e){
            System.out.println("error on cats is " + e);
        }


        //doCategoriesData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInterstitialAd() {
        if (mAdMobInterstitialAd.isLoaded()) {
            mAdMobInterstitialAd.show();
        } else {
            openVideosIntent();
        }
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

        if(mAdMobInterstitialAd != null){
            initializeAdd();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public void openVideosIntent(){
        Intent i = new Intent(NewsActivity.this, MainActivity.class);
        i.putExtra("category", itemToOpen.getTitle());
        i.putExtra("keyword", itemToOpen.getKeyword());
        i.putExtra("desc", itemToOpen.getDescription());
        i.putExtra("limit", String.valueOf(itemToOpen.getLimitCount()));
        i.putExtra("isActiveAd", itemToOpen.isPackageIsAddActive());
        startActivity(i);
    }

    private void doCategoriesData() {
        showProgressDialog();

        String url = Constants.EndpointUrl + Constants.Categories_API;
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response is " + response);
                hideProgressDialog();
                swipeContainer.setRefreshing(false);
                parseCategoriesData(response);
                sendFCMId();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error is " + error);
                hideProgressDialog();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ID",Constants.PackageId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(sr,
                "tag_categories");

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    private void parseCategoriesData(String response){
        try{

            JSONArray mainArray = new JSONArray(response);

            if(mainArray.length() > 0){
                System.out.println("array size is " + mainArray.length());
                Cats.clear();
                for(int i=0; i<mainArray.length(); i++){
                    JSONObject innerData = mainArray.getJSONObject(i);
                    boolean status = innerData.getBoolean("Status");
                    if(status){
                        Cats.add(new Categories(innerData.getInt("ID"), innerData.getInt("PackageID"), innerData.getString("Title"), innerData.getString("Keyword"), innerData.getBoolean("Isplaylist"), innerData.getString("Playlistcode"), innerData.getBoolean("IsRedirection"), innerData.getString("RedirectionApp"), innerData.getInt("Sortorder"), innerData.getString("Image"), innerData.getBoolean("IsLimit"), innerData.getInt("PackageAddlimit"), innerData.getString("Description"), innerData.getBoolean("Status"), innerData.getString("Date"), innerData.getInt("PackageAddlimit"), innerData.getBoolean("PackageIsAddActive"), innerData.getInt("MainCatID"), innerData.getString("MainCatName"), innerData.getString("MainCatDescription"), innerData.getString("MainCatImage"), "Songs", innerData.getBoolean("PackageIsAddmob"), innerData.getBoolean("PackageIsStartapp")));
                    }


                    if(i == 0){
                        txtDesc.setText(innerData.getString("PackageDescription"));
                        if(innerData.getBoolean("PackageIsAddActive")){
                            AdRequest adRequest = new AdRequest.Builder()
                                    .build();
                            mAdView.loadAd(adRequest);
                        }
                    }
                }

                adapterCategories = new NewsAdapter(this, Cats);
                listCategories.setAdapter(adapterCategories);

                listCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("sdfdsfdsfdsf");

                        itemToOpen = (Categories)Cats.get(position);

                        if(itemToOpen.isRedirection()){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(itemToOpen.getRedirectionApp());
                            if (launchIntent != null) {
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(launchIntent);//null pointer check in case package name was not found
                            } else {
                                launchIntent = new Intent(Intent.ACTION_VIEW);
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                launchIntent.setData(Uri.parse("market://details?id=" + itemToOpen.getRedirectionApp()));
                                startActivity(launchIntent);
                            }
                        } else {

                            addLimitCount++;

                            if(addLimitCount == 1){
                                System.out.println("i am at position 1");
                                if(itemToOpen.isPackageIsAddActive()) {
                                    showInterstitialAd();
                                    if(itemToOpen.getPackageAddlimit() == 1){
                                        addLimitCount = 0;
                                    }
                                } else {
                                    openVideosIntent();
                                }
                            } else {
                                if(addLimitCount == itemToOpen.getPackageAddlimit()){
                                    if(itemToOpen.isPackageIsAddActive()) {
                                        System.out.println("i am at position 2 " + itemToOpen.getPackageAddlimit());
                                        showInterstitialAd();
                                        addLimitCount = 0;
                                    } else{
                                        openVideosIntent();
                                    }
                                } else {
                                    System.out.println("i am at position 3 " + addLimitCount);
                                    openVideosIntent();
                                }
                            }

                        }
                    }
                });
            }
        } catch (Exception e){
            System.out.println("error is " + e);
            hideProgressDialog();

        }


    }

    public void initializeAdd(){
        mAdMobInterstitialAd = new InterstitialAd(this);
        mAdMobInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_ad));
        AdRequest adRequest1 = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdMobInterstitialAd.loadAd(adRequest1);
        mAdMobInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
                openVideosIntent();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }
        });
    }

    private void sendFCMId() {

        String url = Constants.EndpointUrl + Constants.SaveFCMId;
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response of fcm token is " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error is " + error);
                hideProgressDialog();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("uid", Utilities.getObjFrmShared(getApplicationContext(), Constants.fcm_token, ""));
                params.put("pid", Constants.PackageId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(sr,
                "tag_save_fcm_token");

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);
    }

    /*@Override
    public void onBackPressed() {
        new AlertDialog.Builder(CategoriesActivity.this)
                .setTitle("Rate Our App")
                .setMessage("Please give your feedback by rating our app on Google play")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openRating();
                    }
                })
                .show();
    }*/

    public void openRating(){
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }
}

package maroof.oldhindisongs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.api.client.googleapis.util.Utils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maroof.oldhindisongs.App.AppController;
import maroof.oldhindisongs.R;
import maroof.oldhindisongs.SectionedRecyclerView.SectionedRecyclerViewAdapter;
import maroof.oldhindisongs.SectionedRecyclerView.StatelessSection;
import maroof.oldhindisongs.utils.Constants;
import maroof.oldhindisongs.utils.Utilities;

public class MainPan extends AppCompatActivity {

    private String auth_key = "AAAAv3i6LSg:APA91bFFcsJG_raFXGSGQ8h9woIvBwaVY8xYZX7EXERXWAedjcKaFBbKjal_HWwJ837UptRTG7T0I3xS2t82zD9Xxi_-H007zm37-PJRr0WZrEc23pYp-uq74CEqBDoUBji0n1fXU3fO";

    private SwipeRefreshLayout swipeContainer;

    private RecyclerView recyclerView;

    private SectionedRecyclerViewAdapter sectionAdapter;

    private ProgressDialog pDialog;

    private AdView mAdView;

    private InterstitialAd mAdMobInterstitialAd;

    private int addLimitCount;

    private Categories itemToOpen;

    private static final int MY_SOCKET_TIMEOUT_MS = 30000;

    private List Cats;

    private TextView txtDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pan);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        mAdView = (AdView) findViewById(R.id.adView);

        txtDesc = (TextView) findViewById(R.id.txtDesc);

        System.out.println("token is " + Utilities.getObjFrmShared(getApplicationContext(), Constants.fcm_token, ""));

        initializeAdd();

        addLimitCount = 0;

        Cats = new ArrayList();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutDetail);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                doCategoriesData();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        doCategoriesData();

        //doSubscribeTopic(Utilities.getObjFrmShared(getApplicationContext(), Constants.fcm_token, ""));

        FirebaseMessaging.getInstance().subscribeToTopic("fivestarhindisongslataoldhindisongs");

        System.out.println("subscribed to topic");
    }



    public class CatalogSection extends StatelessSection {

        final static int Songs = 0;
        final static int News = 1;

        final int topic;

        String title;
        ArrayList<Categories> list;
        ArrayList<Categories> list1;
        int imgPlaceholderResId;

        public CatalogSection(int topic, ArrayList<Categories> data, ArrayList<Categories> data1, String name) {
            super(R.layout.section_header, R.layout.section_footer, R.layout.section_main_item);

            this.topic = topic;


            switch (topic) {
                case Songs:
                    this.title = name;
                    this.list = data;
                    this.list1 = data1;
                    this.imgPlaceholderResId = R.mipmap.ic_launcher_round;
                    break;
                case News:
                    this.title = name;
                    this.list1 = data1;
                    this.imgPlaceholderResId = R.mipmap.ic_launcher_round;
                    break;
            }

        }

        private List<String> getNews(int arrayResource) {
            return new ArrayList<>(Arrays.asList(getResources().getStringArray(arrayResource)));
        }

        @Override
        public int getContentItemsTotal() {

            int size = 0;

            if(this.topic == 0){
                size = list.size();
            }

            if(this.topic == 1){
                size = list1.size();
            }

            return size;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            if(this.topic == 0){
                return new ItemViewHolder(view);
            }

            if(this.topic == 1){
                return new ItemViewHolder(view);
            }

            return new ItemViewHolder(view);
        }


        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {


            if(this.topic == 0){
                final ItemViewHolder itemHolder = (ItemViewHolder) holder;

                final Categories model = list.get(position);


                itemHolder.tvHeader.setText(model.getTitle());

                itemHolder.tvDate.setText(model.getDescription());
                itemHolder.imgItem.setImageResource(imgPlaceholderResId);

                ImageLoader imageLoader = ImageLoader.getInstance();

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        //.showImageForEmptyUri()
                        //.showImageOnFail(R.drawable.ani_cat_five)
                        .cacheInMemory(true)
                        .displayer(new RoundedBitmapDisplayer(1000))
                        .showImageOnLoading(R.mipmap.ic_launcher).build();

                imageLoader.displayImage(model.getImage(), itemHolder.imgItem, options);

                itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int modelId = model.getId();

                        if(model.getType().equals("news")){
                            Intent i = new Intent(MainPan.this, NewsDetailActivity.class);
                            i.putExtra("title", model.getTitle());
                            i.putExtra("description", model.getKeyword());
                            i.putExtra("img", model.getImage());
                            startActivity(i);
                        } else {
                            itemToOpen = model;

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

            /*if(this.topic == 1){
                final ItemViewHolder itemHolder = (ItemViewHolder) holder;

                final News model = list1.get(position);



                itemHolder.tvHeader.setText(model.getTitle());


                itemHolder.imgItem.setImageResource(imgPlaceholderResId);

                itemHolder.tvDate.setText("");


                itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int modelId = model.getId();
                    }
                });
            }*/

        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvTitle.setText(title);
        }

        @Override
        public RecyclerView.ViewHolder getFooterViewHolder(View view) {
            return new FooterViewHolder(view);
        }

        @Override
        public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
            final FooterViewHolder footerHolder = (FooterViewHolder) holder;

            footerHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //System.out.println("list size is " + list1.size());
                    //Toast.makeText(MainPan.this, "title is " + title + " and list size is " + list.size(), Toast.LENGTH_LONG).show();
                    if(title.equals("News")){
                        Intent i = new Intent(MainPan.this, NewsActivity.class);
                        i.putExtra("catsArr", list1);
                        i.putExtra("title", title);

                        startActivity(i);
                    } else {
                        Intent i = new Intent(MainPan.this, CategoriesActivity.class);
                        i.putExtra("catsArr", list1);
                        i.putExtra("title", title);

                        startActivity(i);
                    }


                }
            });
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public HeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;

        public FooterViewHolder(View view) {
            super(view);

            rootView = view;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final ImageView imgItem;
        private final TextView tvHeader;
        private final TextView tvDate;

        public ItemViewHolder(View view) {
            super(view);

            rootView = view;
            imgItem = (ImageView) view.findViewById(R.id.imgItem);
            tvHeader = (TextView) view.findViewById(R.id.tvHeader);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
        }
    }





    //new ui

    class SongsSection extends StatelessSection {

        String title;
        List<Categories> list;
        ArrayList<Categories> list1;

        public SongsSection(String title, List<Categories> list, ArrayList<Categories> list1) {
            super(R.layout.section_header_grid, R.layout.section_item_grid);

            this.title = title;
            this.list = list;
            this.list1 = list1;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new SongsItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final SongsItemViewHolder itemHolder = (SongsItemViewHolder) holder;

            ImageLoader imageLoader = ImageLoader.getInstance();

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)

                    //.showImageForEmptyUri()
                    //.showImageOnFail(R.drawable.ani_cat_five)
                    .cacheInMemory(true)
                    //.displayer(new RoundedBitmapDisplayer(1000))
                    .showImageOnLoading(R.mipmap.ic_launcher).build();

            imageLoader.displayImage(list.get(position).getImage(), itemHolder.imgItem, options);

            String name = list.get(position).getTitle();
            String category = list.get(position).getDescription();

            itemHolder.tvItem.setText(name);
            itemHolder.tvSubItem.setText(category);

            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    int modelId = list.get(sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition())).getId();

                    itemToOpen = list.get(sectionAdapter.getPositionInSection(itemHolder.getAdapterPosition()));

                    if(itemToOpen.getType().equals("news")){
                        Intent i = new Intent(MainPan.this, NewsDetailActivity.class);
                        i.putExtra("title", itemToOpen.getTitle());
                        i.putExtra("description", itemToOpen.getKeyword());
                        i.putExtra("img", itemToOpen.getImage());
                        startActivity(i);
                    } else {
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

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new SongsHeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            SongsHeaderViewHolder headerHolder = (SongsHeaderViewHolder) holder;

            headerHolder.tvTitle.setText(title);

            headerHolder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("array size is " + list1.size());
                    if(title.equals("News")){
                        Intent i = new Intent(MainPan.this, NewsActivity.class);
                        i.putExtra("catsArr", list1);
                        i.putExtra("title", title);

                        startActivity(i);
                    } else {
                        Intent i = new Intent(MainPan.this, CategoriesActivity.class);
                        i.putExtra("catsArr", list1);
                        i.putExtra("title", title);

                        startActivity(i);
                    }

                }
            });
        }
    }

    class SongsHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final Button btnMore;

        public SongsHeaderViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            btnMore = (Button) view.findViewById(R.id.btnMore);
        }
    }

    class SongsItemViewHolder extends RecyclerView.ViewHolder {

        private final View rootView;
        private final TextView tvItem;
        private final TextView tvSubItem;
        private final ImageView imgItem;

        public SongsItemViewHolder(View view) {
            super(view);

            rootView = view;
            tvItem = (TextView) view.findViewById(R.id.tvItem);
            tvSubItem = (TextView) view.findViewById(R.id.tvSubItem);
            imgItem = (ImageView) view.findViewById(R.id.imgItem);
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

    public void openVideosIntent(){
        Intent i = new Intent(MainPan.this, MainActivity.class);
        i.putExtra("category", itemToOpen.getTitle());
        i.putExtra("keyword", itemToOpen.getKeyword());
        i.putExtra("desc", itemToOpen.getDescription());
        i.putExtra("limit", String.valueOf(itemToOpen.getLimitCount()));
        i.putExtra("isActiveAd", itemToOpen.isPackageIsAddActive());
        startActivity(i);
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

    private void doCategoriesData() {
        showProgressDialog();

        String url = Constants.EndpointUrl + Constants.Pannels_Categories_API;
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response is " + response);
                hideProgressDialog();
                swipeContainer.setRefreshing(false);
                parseCategoriesData(response);
                doNewsData();
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
                params.put("pid",Constants.PackageId);
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

            ArrayList<String> pannels = new ArrayList<String>();

            if(mainArray.length() > 0){
                System.out.println("array size is " + mainArray.length());
                Cats.clear();
                for(int i=0; i<mainArray.length(); i++){

                        JSONObject innerData = mainArray.getJSONObject(i);
                        boolean status = innerData.getBoolean("PackageStatus");

                        String name = innerData.getString("MainCatName").toString();

                        if (!pannels.contains(name)) {
                            if(status){
                                pannels.add(name);
                            }
                        }

                        if(status){

                            Cats.add(new Categories(innerData.getInt("ID"), innerData.getInt("PackageID"), innerData.getString("Title"), innerData.getString("Keyword"), innerData.getBoolean("Isplaylist"), innerData.getString("Playlistcode"), innerData.getBoolean("IsRedirection"), innerData.getString("RedirectionApp"), innerData.getInt("Sortorder"), innerData.getString("Image"), innerData.getBoolean("IsLimit"), innerData.getInt("PackageAddlimit"), innerData.getString("Description"), innerData.getBoolean("Status"), innerData.getString("Date"), innerData.getInt("PackageAddlimit"), innerData.getBoolean("PackageIsAddActive"), innerData.getInt("MainCatID"), innerData.getString("MainCatName"), innerData.getString("MainCatDescription"), innerData.getString("MainCatImage"), "songs", innerData.getBoolean("PackageIsAddmob"), innerData.getBoolean("PackageIsStartapp")));

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


                //creating pannels

                System.out.println("pannel size is " + pannels.size());

                if(pannels.size() > 0){

                    sectionAdapter.removeAllSections();

                    for(int i=0; i<pannels.size(); i++){
                        String pannel = pannels.get(i).toString().toLowerCase();
                        String pannelStr = pannels.get(i).toString();

                        ArrayList<Categories> data = new ArrayList<>();
                        ArrayList<Categories> data1 = new ArrayList<>();

                        int a = 0;
                        for(int j=0; j<Cats.size(); j++){
                            Categories cat = (Categories) Cats.get(j);
                            if(cat.getMainCatName().toString().toLowerCase().equals(pannel)){

                                if(a < 4){
                                    data.add(cat);

                                }

                                if(a == 4){
                                    //a = 0;
                                    break;
                                }

                                a++;
                            }
                        }

                        for(int j=0; j<Cats.size(); j++){
                            Categories cat = (Categories) Cats.get(j);
                            if(cat.getMainCatName().toString().toLowerCase().equals(pannel)){
                                data1.add(cat);


                            }
                        }

                        //sectionAdapter.addSection(new SongsSection(pannelStr, data, data1));
                        sectionAdapter.addSection(new CatalogSection(0, data, data1, pannelStr));
                    }

                    GridLayoutManager glm = new GridLayoutManager(getApplicationContext(), 2);
                    glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch(sectionAdapter.getSectionItemViewType(position)) {
                                case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                                    return 2;
                                default:
                                    return 1;
                            }
                        }
                    });

                    //recyclerView.setLayoutManager(glm);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(sectionAdapter);
                }

            }
        } catch (Exception e){
            System.out.println("error is " + e);
            hideProgressDialog();

        }


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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainPan.this)
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
    }

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

    private void doNewsData() {
        //showProgressDialog();

        String url = Constants.EndpointUrl + Constants.Pannels_News_Api;
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response of news is " + response);
                //hideProgressDialog();

                try{
                    JSONArray mainArray = new JSONArray(response);

                    ArrayList CatsData = new ArrayList();
                    ArrayList<Categories> data = new ArrayList<>();
                    ArrayList<Categories> data1 = new ArrayList<>();

                    if(mainArray.length() > 0){
                        for(int i=0; i<mainArray.length(); i++){
                            JSONObject innerData = mainArray.getJSONObject(i);


                            data.add(new Categories(innerData.getInt("ID"), 1, innerData.getString("Title"), innerData.getString("Description"), false, "", false, "", innerData.getInt("Sortorder"), innerData.getString("Image"), false, 1, "", true, innerData.getString("Date"), 1, true, 1, "", "", "", "news", false, false));
                            data1.add(new Categories(innerData.getInt("ID"), 1, innerData.getString("Title"), innerData.getString("Description"), false, "", false, "", innerData.getInt("Sortorder"), innerData.getString("Image"), false, 1, "", true, innerData.getString("Date"), 1, true, 1, "", "", "", "news", false, false));

                        }

                        sectionAdapter.addSection(new CatalogSection(0, data, data1, "News"));
                        sectionAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e){

                }


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
                //params.put("pid",Constants.PackageId);
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

    private void doSubscribeTopic(String token) {

        String url = "https://iid.googleapis.com/iid/v1/" + token + "/rel/topics/fittappshindisadsongs";
        StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("response of adding topic is " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error in adding topic is " + error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                //params.put("pid",Constants.PackageId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("Authorization","key=" + auth_key);
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(sr,
                "tag_add_topics");
    }
}

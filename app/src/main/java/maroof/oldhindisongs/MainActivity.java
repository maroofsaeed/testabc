package maroof.oldhindisongs;

import android.graphics.drawable.RippleDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import maroof.oldhindisongs.utils.CategoriesAdapter;
import maroof.oldhindisongs.utils.Constants;
import maroof.oldhindisongs.utils.VideoAdapter;
import maroof.oldhindisongs.utils.YoutubeAdapter;
import maroof.oldhindisongs.utils.YoutubeResults;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;

import com.google.api.services.youtube.model.*;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Iterator;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, VideoAdapter.OnDataChangeListener {

    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    View v;

    public static final int ITEMS_PER_AD = 6;

    private String youtube_url = "";

    private static final int NATIVE_EXPRESS_AD_HEIGHT = 100;

    private static YouTube youtube;

    RecyclerView recyclerView;

    private String keyword_txt;

    private TextView txtDesc;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String AD_UNIT_ID = "ca-app-pub-7903940922696389/3472022259";

    private static final String BUTTON_TEXT = "Call YouTube Data API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    private long NUMBER_OF_VIDEOS_RETURNED = 50;

    private ListView listDetail;

    ArrayList<Object> videos = new ArrayList<>();

    private YoutubeResults itemToOpenVideo;

    private List DetailList;
    private YoutubeAdapter adapterYoutube;

    private VideoAdapter adapter;

    private List<SearchResult> listGlobalYoutube;

    private SwipeRefreshLayout swipeContainer;

    InterstitialAd mAdMobInterstitialAd;

    private int addlimitCount, packageLimitCount;

    private AdView mAdView;

    private boolean isAddActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("category"));

        packageLimitCount = Integer.parseInt(getIntent().getStringExtra("limit"));

        addlimitCount = 1;

        isAddActive = getIntent().getBooleanExtra("isActiveAd",false);

        mAdView = (AdView) findViewById(R.id.adView);

        if(isAddActive) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        keyword_txt = getIntent().getStringExtra("keyword");

        //listDetail = (ListView) findViewById(R.id.listViewDetail);

        DetailList = new ArrayList();

        if(isAddActive) {
            initializeAdd();
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutDetail);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResultsFromApi();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //DetailList.add(new YoutubeResults("Idq6T1BspPPw8", "Now Playing Kumar Sanu Hit Songs Non Stop | Audio Jukebox", "https://i.ytimg.com/vi/q6T1BspPPw8/default.jpg"));

        //adapterYoutube = new YoutubeAdapter(MainActivity.this, DetailList);
        //listDetail.setAdapter(adapterYoutube);

        txtDesc = (TextView) findViewById(R.id.txtDesc);

        txtDesc.setText(getIntent().getStringExtra("desc"));

        String limit = getIntent().getStringExtra("limit");

        if(limit != null || !limit.equals("") || !limit.equals("0")){
            //NUMBER_OF_VIDEOS_RETURNED = Long.valueOf(limit);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        adapter = new VideoAdapter(getApplicationContext(),videos, isAddActive);

        adapter.setOnDataChangeListener(new VideoAdapter.OnDataChangeListener(){
            public void onDataChanged(int size, int count, String url){
                System.out.println("data size is " + size);

                youtube_url = url;

                if(isAddActive) {
                    showInterstitialAd();
                } else {
                    openVideosIntent();
                }

            }
        });

        recyclerView.setAdapter(adapter);

        getResultsFromApi();
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

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        /*if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }*/
        new MakeRequestTask().execute();
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    /*mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");*/
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(/*GoogleAccountCredential credential*/) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            /*mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("YouTube Data API Android Quickstart")
                    .build();*/

            youtube = new YouTube.Builder(transport, jsonFactory, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();

            //youtube search
            YouTube.Search.List search = youtube.search().list("id,snippet");
            String apiKey = Constants.youtube_api_key;
            search.setKey(apiKey);
            search.setQ(keyword_txt);

            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");

            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();



            listGlobalYoutube = searchResultList;
            /*if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), keyword_txt);
            }*/




            /*ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("HARAMAINGALLERY")
                    .execute();



            List<Channel> channels = result.getItems();


            if (channels != null) {
                Channel channel = channels.get(0);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            }*/

            channelInfo.add("This channel's ID is " + "sd" + ". " +
                    "Its title is '" + "asdasd" + ", " +
                    "and it has " + "123" + " views.");
            return channelInfo;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                System.out.println("No results returned.");
                //mOutputText.setText("No results returned.");
            } else {
                //output.add(0, "Data retrieved using the YouTube Data API:");
                //mOutputText.setText(TextUtils.join("\n", output));
                if (output != null) {
                    //
                    System.out.println("list size is " + listGlobalYoutube.size());
                    prettyPrint(listGlobalYoutube.iterator(), keyword_txt);
                } else {
                    System.out.println("output is null");
                }
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    /*mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());*/
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }

    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        //DetailList.clear();

        videos.clear();

        adapter.notifyDataSetChanged();



        swipeContainer.setRefreshing(false);

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");

                videos.add(new YoutubeResults(rId.getVideoId(), singleVideo.getSnippet().getTitle(), thumbnail.getUrl()));
            }
        }

        System.out.println("Array size is " + videos.size());

        

        if(isAddActive) {
            //addNativeExpressAds();
            //setUpAndLoadNativeExpressAds();
        }

        if (adapter!=null)
            adapter.notifyDataSetChanged();

        //adapterYoutube = new YoutubeAdapter(MainActivity.this, DetailList);
        //listDetail.setAdapter(adapterYoutube);

        //


        /*listDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("sdfdsfdsfdsf");
                itemToOpenVideo = (YoutubeResults)DetailList.get(position);
                if(isAddActive) {
                    showInterstitialAd();
                } else {
                    openVideosIntent();
                }
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mAdMobInterstitialAd != null){
            initializeAdd();
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
        Intent intent = new Intent(MainActivity.this, YouTubePlayerActivity.class);
        intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, youtube_url);
        intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);
        intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);
        intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);
        intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showInterstitialAd() {
        if (mAdMobInterstitialAd.isLoaded()) {
            mAdMobInterstitialAd.show();
        } else {
            openVideosIntent();
        }
    }

    void addNativeExpressAds()
    {
        for (int i = 0; i <= videos.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(getApplicationContext());
            videos.add(i, adView);
        }
    }

    private void setUpAndLoadNativeExpressAds() {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                try {

                    final float scale = getResources().getDisplayMetrics().density;
                    // Set the ad size and ad unit ID for each Native Express ad in the items list.
                    for (int i = 0; i <= videos.size(); i += ITEMS_PER_AD) {
                        final NativeExpressAdView adView =
                                (NativeExpressAdView) videos.get(i);
                        CardView cardView = (CardView) findViewById(R.id.ad_card_view);

                        if (cardView==null)
                        {
                            cardView = (CardView) findViewById(R.id.ad_card_view);
                        }


                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                        int width = displayMetrics.widthPixels;

                        //final int adWidth = cardView.getWidth() - cardView.getPaddingLeft() - cardView.getPaddingRight();
                        final int adWidth = width;

                        AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);


                        if (adView.getAdSize()==null)
                            adView.setAdSize(adSize);

                        if (adView.getAdUnitId()==null)
                            adView.setAdUnitId(AD_UNIT_ID);


                    }

                    // Load the first Native Express ad in the items list.
                    loadNativeExpressAd(0);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadNativeExpressAd(final int index) {

        if (index >= videos.size()) {
            return;
        }

        Object item = videos.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        // Load the Native Express ad.
        if (adView.getAdUnitId()!=null)
        {
            adView.loadAd(new AdRequest.Builder().addTestDevice("7F458D8A27C9A51F280FAFC86EBC6B23").build());
        }
        else
        {
            adView.setAdUnitId(AD_UNIT_ID);
            adView.loadAd(new AdRequest.Builder().addTestDevice("7F458D8A27C9A51F280FAFC86EBC6B23").build());
        }
    }

    public void onDataChanged(int size, int count, String url){

    };
}

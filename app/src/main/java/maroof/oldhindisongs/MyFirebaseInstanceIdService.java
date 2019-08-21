package maroof.oldhindisongs;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import maroof.oldhindisongs.App.AppController;
import maroof.oldhindisongs.utils.Constants;
import maroof.oldhindisongs.utils.Utilities;

/**
 * Created by Maroof on 7/4/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String REG_TOKEN = "REG_TOKEN";



    private static final int MY_SOCKET_TIMEOUT_MS = 30000;

    @Override
    public void onTokenRefresh() {

        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);

        Utilities.saveInShared(getApplicationContext(), Constants.fcm_token, recent_token);

        //doSubscribeTopic(recent_token);
    }


}

package maroof.oldhindisongs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import maroof.oldhindisongs.CategoriesActivity;
import maroof.oldhindisongs.R;
import maroof.oldhindisongs.utils.MySingleton;

import static android.R.attr.bitmap;
import static android.R.attr.reqFiveWayNav;
import static maroof.oldhindisongs.App.AppController.TAG;

/**
 * Created by Maroof on 7/4/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //message will contain the Push Message
        final String message = remoteMessage.getData().get("text");

        final String description = remoteMessage.getData().get("description");

        System.out.println("text is " + message);
        System.out.println("desc is " + description);
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");

        final String redirecturl = remoteMessage.getData().get("redirurl");

        System.out.println("redirecturl is " + redirecturl);

        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
        final String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");

        //To get a Bitmap image from the URL received
        //bitmap = getBitmapfromUrl(imageUri);

        sendNotification(message, imageUri, TrueOrFlase, description, redirecturl, this);

        /*Glide.with(this)
                .load(imageUri)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //imageView.setImageBitmap(resource);
                        sendNotification(message, resource, TrueOrFlase, description, redirecturl);
                    }
                });

                */


        /*Intent intent = new Intent(this, CategoriesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("Hindi Sad Songs");
        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());*/
    }

    private void sendNotification(String messageBody, String imgUrl, String TrueOrFalse, String description, String redirectUrl, final Context ctx) {

        if(redirectUrl.equals("nodata")){
            System.out.println("i am here ans redirecturi is nodata" + redirectUrl);
            Intent intent = new Intent(this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("AnotherActivity", TrueOrFalse);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    //.setLargeIcon(image)/*Notification icon image*/
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(messageBody)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setContentText(description)
                    //.setStyle(new NotificationCompat.BigPictureStyle()
                    //      .bigPicture(image))/*Notification with Image*/
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);


            if(imgUrl.equals("nodata")){
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String CHANNEL_ID = getString(R.string.channel_name);
                    CharSequence name = getString(R.string.channel_name);
                    String Description = getString(R.string.channel_name);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setDescription(Description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mChannel.setShowBadge(false);
                    notificationManager.createNotificationChannel(mChannel);
                }

                Intent resultIntent = new Intent(ctx, MainPan.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                notificationBuilder.setContentIntent(resultPendingIntent);

                notificationBuilder.setChannelId(getString(R.string.channel_name));

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            } else {
                ImageRequest imageRequest = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            String CHANNEL_ID = getString(R.string.channel_name);
                            CharSequence name = getString(R.string.channel_name);
                            String Description = getString(R.string.channel_name);
                            int importance = NotificationManager.IMPORTANCE_HIGH;
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                            mChannel.setDescription(Description);
                            mChannel.enableLights(true);
                            mChannel.setLightColor(Color.RED);
                            mChannel.enableVibration(true);
                            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                            mChannel.setShowBadge(false);
                            notificationManager.createNotificationChannel(mChannel);
                        }

                        Intent resultIntent = new Intent(ctx, MainPan.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                        notificationBuilder.setContentIntent(resultPendingIntent);

                        notificationBuilder.setChannelId(getString(R.string.channel_name));

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error in getting notification image");
                    }

                });

                MySingleton.getmInstance(this).addToRequestQue(imageRequest);
            }



        } else {
            System.out.println("open rating and value is " + redirectUrl);
            openRating(messageBody, imgUrl, TrueOrFalse, description, redirectUrl);
        }

    }

    public void openRating(String messageBody, String imageUrl, String TrueOrFalse, String description, String redirectUrl){
        Uri uri = Uri.parse("market://details?id=" + redirectUrl);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, goToMarket,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    //.setLargeIcon(image)/*Notification icon image*/
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(messageBody)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setContentText(description)
                    //.setStyle(new NotificationCompat.BigPictureStyle()
                    //      .bigPicture(image))/*Notification with Image*/
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);


            if(imageUrl.equals("nodata")){
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            } else {
                ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error in getting notification image");
                    }

                });

                MySingleton.getmInstance(this).addToRequestQue(imageRequest);
            }

            //startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            System.out.println("on notification builder redirect portion and error is " + e.toString());
        }
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }


}
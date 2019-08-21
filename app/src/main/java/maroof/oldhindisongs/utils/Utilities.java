package maroof.oldhindisongs.utils;

/**
 * Created by Maroof on 6/18/2017.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Utilities {

    public static void removeAllShared(Context con) {

        SharedPreferences sp = con
                .getApplicationContext()
                .getSharedPreferences(Constants.PREFRENCE, Context.MODE_PRIVATE);
        sp.edit().clear().commit();

    }

    public static void saveInShared(Context con, String key, String value) {

        SharedPreferences sp = con
                .getApplicationContext()
                .getSharedPreferences(Constants.PREFRENCE, Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();

        sp.edit().putString(key, value).commit();

    }

    public static String getObjFrmShared(Context c, String key, String defaultValue) {

        SharedPreferences sp = c.getApplicationContext().getSharedPreferences(
                Constants.PREFRENCE, Context.MODE_PRIVATE);

        return sp.getString(key, defaultValue);

    }
    public static String loadJSONFromAsset(Context con,String country,String gender) {
        String json="",age = "";

        try {

            InputStream is = con.getAssets().open("Q1.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");
            JSONObject j=new JSONObject(json);
            JSONArray jCountry=j.optJSONArray("key");
            JSONArray jAge=j.optJSONArray("value");
            for (int i = 0; i < jCountry.length(); i++) {
                if(jCountry.getString(i).equalsIgnoreCase(country)){
                    //String s=jCountry.getString(i);
                    String g=jAge.getString(i);

                    String gg[]=g.split(" ");
                    if(gender.equalsIgnoreCase("Male"))
                        age=gg[0].substring(1).trim();
                    else
                        age=gg[1].substring(1).trim();



                }
            }


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return age;

    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable ImageOperations(Context ctx, String url, String saveFilename) {
        try {
            InputStream is = (InputStream) fetch(url);
            Drawable d = Drawable.createFromStream(is, saveFilename);
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }


    public static String getShortMonthName(int num){
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return monthNames[num];

    }

    public static int[] getThreeLowest(Integer[] array) {
        int[] lowestValues = new int[4];
        Arrays.fill(lowestValues, Integer.MAX_VALUE);

        for(int n : array) {
            if(n < lowestValues[3]) {
                lowestValues[3] = n;
                Arrays.sort(lowestValues);
            }
        }
        return lowestValues;
    }

    public static String loadJSONFromAsset(Activity activity, String filename) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
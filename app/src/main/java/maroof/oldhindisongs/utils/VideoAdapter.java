package maroof.oldhindisongs.utils;

/**
 * Created by Maroof on 8/16/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.youtube.player.YouTubePlayer;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maroof.oldhindisongs.R;

/**
 * Created by Sid on 04-02-2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Object> videos;
    boolean isAd;
    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    public OnDataChangeListener mOnDataChangeListener;

    private int counter = 0;

    public VideoAdapter(Context context, ArrayList<Object> videos, boolean isAdActive)
    {
        this.context = context;
        this.videos = videos;
        this.isAd = isAdActive;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_layout, parent, false);

            return new ViewHolder(v);

        }
        else
        {
            View nativeExpressLayoutView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.native_express_ad_container,
                    parent, false);
            return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }

    }



    @Override
    public int getItemViewType(int position) {
        //return (position % 6 == 0) ? NATIVE_EXPRESS_AD_VIEW_TYPE
         //       : MENU_ITEM_VIEW_TYPE;

        return MENU_ITEM_VIEW_TYPE;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hold, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:

                final YoutubeResults videoInfo = (YoutubeResults) videos.get(position);
                ViewHolder holder = (ViewHolder) hold;
                Glide.with(context).load(videoInfo.getThumbnail()).into(holder.thumb);
                holder.title.setText(videoInfo.getTitle());

                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        counter = 2;

                        System.out.println("on card click");

                        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

                        Pattern compiledPattern = Pattern.compile(pattern);
                        Matcher matcher = compiledPattern.matcher(videoInfo.getVideo());

                        if (mOnDataChangeListener != null) {
                            mOnDataChangeListener.onDataChanged(counter, 0, videoInfo.getVideo());
                        }

                        /*if(matcher.find()) {

                            System.out.println("on matcher and video url is " + matcher.group());

                            if (mOnDataChangeListener != null) {
                                mOnDataChangeListener.onDataChanged(counter, 0, matcher.group());
                            }
                        }*/
                    }
                });



                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) hold;
                NativeExpressAdView adView =
                        (NativeExpressAdView) videos.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // NativeExpressAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled NativeExpressAdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);

            default:

        }


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title,link,views,updated;
        ImageView watch, install;
        CardView card;


        ViewHolder(View view) {
            super(view);

            thumb = (ImageView) view.findViewById(R.id.thumb);

            title = (TextView)view.findViewById(R.id.title);

            card = (CardView) view.findViewById(R.id.card);
        }


    }

    public interface OnDataChangeListener{
        public void onDataChanged(int size, int count, String youtube_url);
    }

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
}
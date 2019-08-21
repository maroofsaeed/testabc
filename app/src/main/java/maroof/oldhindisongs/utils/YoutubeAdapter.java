package maroof.oldhindisongs.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import maroof.oldhindisongs.App.AppController;
import maroof.oldhindisongs.Categories;
import maroof.oldhindisongs.R;

/**
 * Created by Maroof on 6/19/2017.
 */

public class YoutubeAdapter extends ArrayAdapter<YoutubeResults> {

    private static Context mContext;
    List<YoutubeResults> mylist;
    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    public static com.android.volley.toolbox.ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public YoutubeAdapter(Context _context, List<YoutubeResults> _mylist) {
        super(_context, R.layout.list_item_details, _mylist);
        this.mylist = _mylist;
        this.mContext = _context;
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 10 == 0) ? NATIVE_EXPRESS_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                YoutubeResults row = getItem(position);


                YoutubeAdapter.YoutubeViewHolder holder;

                if (convertView == null) {
                    convertView = new LinearLayout(getContext());
                    String inflater = Context.LAYOUT_INFLATER_SERVICE;
                    LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
                    convertView = vi.inflate(R.layout.list_item_details, parent, false);

                    //
                    holder = new YoutubeAdapter.YoutubeViewHolder();
                    holder.imglogo = (NetworkImageView) convertView.findViewById(R.id.imgItem);
                    holder.title = (TextView)convertView.findViewById(R.id.tvHeader);


                    //
                    convertView.setTag(holder);
                }
                else{
                    holder = (YoutubeAdapter.YoutubeViewHolder) convertView.getTag();
                }


                //
                //holder.populateFrom(row);
                holder.populateFrom(row);
                //

                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:

                YoutubeAdapter.NativeExpressAdViewHolder holder1;

                if (convertView == null) {
                    convertView = new LinearLayout(getContext());
                    String inflater = Context.LAYOUT_INFLATER_SERVICE;
                    LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
                    convertView = vi.inflate(R.layout.native_express_ad_container, parent, false);

                    //
                    holder1 = new YoutubeAdapter.NativeExpressAdViewHolder(convertView);

                    //
                    convertView.setTag(holder1);
                }
                else{
                    holder1 = (YoutubeAdapter.NativeExpressAdViewHolder) convertView.getTag();
                }
                break;
        }

        return convertView;
    }

    static class YoutubeViewHolder {
        public ImageView img;
        public TextView title;
        public TextView subTitle;
        public TextView txtOther;
        public NetworkImageView imglogo;

        void populateFrom(YoutubeResults p) {
            title.setText(p.getTitle());
            imglogo.setImageUrl(p.getThumbnail(), imageLoader);

        }


    }

    class NativeExpressAdViewHolder {

        public View v;

        NativeExpressAdViewHolder(View view) {
            this.v = view;
        }
    }
}

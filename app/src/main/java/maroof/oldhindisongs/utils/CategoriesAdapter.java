package maroof.oldhindisongs.utils;

import android.content.Context;
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

public class CategoriesAdapter extends ArrayAdapter<Categories> {

    private static Context mContext;
    List<Categories> mylist;

    public static com.android.volley.toolbox.ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CategoriesAdapter(Context _context, List<Categories> _mylist) {
        super(_context, R.layout.list_item_categories, _mylist);
        this.mylist = _mylist;
        this.mContext = _context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Categories row = getItem(position);


        CategoriesViewHolder holder;

        if (convertView == null) {


            convertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.list_item_categories, parent, false);

            //
            holder = new CategoriesViewHolder();
            holder.imglogo = (NetworkImageView) convertView.findViewById(R.id.imgItem);
            holder.title = (TextView)convertView.findViewById(R.id.tvHeader);
            holder.subTitle = (TextView) convertView.findViewById(R.id.textsub);


            //
            convertView.setTag(holder);
        }
        else{
            holder = (CategoriesViewHolder) convertView.getTag();
        }


        //
        //holder.populateFrom(row);
        holder.populateFrom(row);
        //
        return convertView;
    }

    static class CategoriesViewHolder {
        public ImageView img;
        public TextView title;
        public TextView subTitle;
        public TextView txtOther;
        public NetworkImageView imglogo;

        void populateFrom(Categories p) {
            title.setText(p.getTitle());
            subTitle.setText(p.getDescription());
            imglogo.setImageUrl(p.getImage(), imageLoader);

        }


    }
}

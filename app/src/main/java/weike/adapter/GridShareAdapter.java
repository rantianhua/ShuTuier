package weike.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import weike.shutuier.R;

/**
 * Created by Rth on 2015/4/3.
 */
public class GridShareAdapter extends BaseAdapter {

    private String[] shareTv = null;
    private Integer[] shareIcon = null;
    private Resources res = null;
    private LayoutInflater inflater = null;
    private int size ;

    public GridShareAdapter(Context con,String[] tv,Integer[] icon) {
        shareTv = tv;
        shareIcon = icon;
        res = con.getResources();
        size = res.getDimensionPixelSize(R.dimen.grid_share_item_size);
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return shareIcon.length;
    }

    @Override
    public Object getItem(int position) {
        return shareTv[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.grid_share_item,parent,false);
        }
        TextView view = (TextView) convertView;
        view.setText(shareTv[position]);
        Drawable icon = res.getDrawable(shareIcon[position]);
        icon.setBounds(0, 0, size, size);
        view.setCompoundDrawables(null,icon,null,null);
        return view;
    }
}

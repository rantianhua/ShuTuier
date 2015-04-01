package weike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import weike.shutuier.R;

/**
 * Created by Rth on 2015/3/24.
 */
public class GridCollegeAdapter extends BaseAdapter {

    private String[] data = null;
    private LayoutInflater inflater = null;
    private RelativeLayout.LayoutParams params = null;

    public GridCollegeAdapter(Context context,String[] data) {
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.tv_colleges,parent,false);
        }
        TextView tv = (TextView) convertView;
        tv.setText(data[position]);
        return convertView;
    }

}

package weike.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import weike.data.BookItem;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/3/24.
 */
public class GridAdapter extends BaseAdapter {

    private List<BookItem> list = null;
    private LayoutInflater inflater = null;
    private RelativeLayout.LayoutParams params = null;

    public GridAdapter(Context context) {
        list = new ArrayList<>();
        for (int i = 0;i<10;i++){
            list.add(new BookItem());
        }
        inflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
       //int width = Utils.getWindowWidth(context);
       int height = resources.getDimensionPixelSize(R.dimen.photoMarginLeft);
        resources =  null;
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item,parent,false);
            hodler = new ViewHodler();
            hodler.img = (ImageView) convertView.findViewById(R.id.img_grid_item);
            hodler.tvAlign = (TextView) convertView.findViewById(R.id.tv_align_grid_img);
            hodler.tvName = (TextView) convertView.findViewById(R.id.tv_name_grid);
            hodler.img.setLayoutParams(params);
            hodler.img.setImageResource(R.drawable.def);
            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHodler {
        ImageView img ;
        TextView tvAlign,tvName;
    }
}

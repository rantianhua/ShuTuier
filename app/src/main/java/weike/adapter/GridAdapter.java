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

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

import weike.shutuier.R;
import weike.util.Mysingleton;

/**
 * Created by Rth on 2015/3/24.
 */
public class GridAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private RelativeLayout.LayoutParams params = null;
    private ArrayList<Map<String,String>> data= null;
    private ImageLoader loader =  null;

    public GridAdapter(Context context,ArrayList<Map<String,String>> data) {
        this.data = data;
        inflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
       int height = resources.getDimensionPixelSize(R.dimen.photoMarginLeft);
        resources =  null;
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height);
        loader = Mysingleton.getInstance(context).getImageLoader();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
            hodler.tvDate = (TextView) convertView.findViewById(R.id.tv_date_grid);
            hodler.img.setLayoutParams(params);
            hodler.img.setImageResource(R.drawable.def);
            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler) convertView.getTag();
        }
        Map<String,String> map = data.get(position);
        loader.get(map.get("InternetImg"),ImageLoader.getImageListener(hodler.img,R.drawable.def,R.drawable.def));
        hodler.tvAlign.setText(map.get("close"));
        //hodler.tvDate.setText(map.get("date"));
        return convertView;
    }

    static class ViewHodler {
        ImageView img ;
        TextView tvAlign,tvDate;
    }
}

package weike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import weike.shutuier.R;

/**
 * Created by Rth on 2015/4/18.
 * 该类是MainClassifyFragment中展示主分类的ListView的适配器
 */
public class MainClassifyAdapter  extends BaseAdapter{

    private String[] mainClassifys = null;
    private String[] subClassifys = null;
    private LayoutInflater inflater = null;

    public MainClassifyAdapter(String[] main,String[]  sub,Context con) {
        mainClassifys = main;
        subClassifys = sub;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return mainClassifys.length;
    }

    @Override
    public Object getItem(int position) {
        return mainClassifys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_main_classify,null);
            hodler = new ViewHodler();
            hodler.tvMain = (TextView) convertView.findViewById(R.id.tv_main_classify_items);
            hodler.tvSub = (TextView) convertView.findViewById(R.id.tv_main_classify_sub_items);
            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler)convertView.getTag();
        }
        hodler.tvMain.setText(mainClassifys[position]);
        hodler.tvSub.setText(subClassifys[position]);
        return convertView;
    }

    class ViewHodler {
        TextView tvMain,tvSub;
    }
}

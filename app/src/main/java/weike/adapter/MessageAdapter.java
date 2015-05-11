package weike.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import weike.data.MessageBookData;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/4/29.
 */
public class MessageAdapter extends BaseAdapter {

    private List<MessageBookData> list = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Resources res = null;

    public MessageAdapter(Context con,List<MessageBookData> data){
        inflater = LayoutInflater.from(con);
        res = con.getResources();
        list.addAll(data);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_message,null);
            hodler = new ViewHodler();
            hodler.tvContent = (TextView) convertView.findViewById(R.id.tv_message_content);
            hodler.tvOder = (TextView) convertView.findViewById(R.id.tv_message_order);
            convertView.setTag(hodler);
        }else {
            hodler = (ViewHodler) convertView.getTag();
        }
        MessageBookData messageData = list.get(position);
        hodler.tvContent.setText(res.getString(R.string.have_message,messageData.getName()));
        hodler.tvOder.setText((list.size() - position)+"");
        return convertView;
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

    class ViewHodler {
        TextView tvOder,tvContent;
    }

    public void updataData(List<MessageBookData> newData) {
        list.clear();
        list.addAll(newData);
        notifyDataSetChanged();
    }
}

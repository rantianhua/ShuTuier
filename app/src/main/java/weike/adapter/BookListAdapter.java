package weike.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import weike.data.BookItem;
import weike.shutuier.R;
import weike.util.Mysingleton;

/**
 * Created by Rth on 2015/2/23.
 */
public class BookListAdapter extends BaseAdapter {

    private List<BookItem> list;
    private Context context;

    public BookListAdapter(List<BookItem> list,Context con) {
        this.list = list;
        this.context = con;
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
            ViewHolder holder = null;
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null);
                holder = new ViewHolder();
                holder.imgView = (ImageView) convertView.findViewById(R.id.img_item);
                holder.tvName = (TextView) convertView.findViewById(R.id.textview_item_book_name);
                holder.tvHowOld = (TextView) convertView.findViewById(R.id.textview_item_book_degree);
                holder.tvAuthor = (TextView) convertView.findViewById(R.id.textview_item_book_author);
                holder.tvPublisher = (TextView) convertView.findViewById(R.id.textview_item_book_press);
                holder.tvDetail = (TextView) convertView.findViewById(R.id.textview_item_book_description);
                holder.tvStatue = (TextView) convertView.findViewById(R.id.textview_item_goal);
                holder.tvOPrice = (TextView) convertView.findViewById(R.id.textview_item_origin_price);
                holder.tvSPrice = (TextView) convertView.findViewById(R.id.textview_item_sell_price);
                holder.tvSNum = (TextView) convertView.findViewById(R.id.tv_list_item_share);
                holder.tvMNum = (TextView) convertView.findViewById(R.id.tv_list_item_message);
                convertView.setTag(holder);
            }else {
                holder =(ViewHolder) convertView.getTag();
            }
            try {
                holder.tvName.setText(list.get(position).getBookName());
                holder.tvHowOld.setText(list.get(position).getHowOld());
                holder.tvAuthor.setText(list.get(position).getAuthorName());
                holder.tvPublisher.setText(list.get(position).getPublisher());
                holder.tvDetail.setText(list.get(position).getDetail());
                holder.tvStatue.setText(list.get(position).getStatue());
                holder.tvOPrice.setText("￥"+list.get(position).getOriginPrice());
                holder.tvSPrice.setText("￥"+list.get(position).getSellPrice());
                holder.tvSNum.setText(list.get(position).getShareNumber()+"");
                holder.tvMNum.setText(list.get(position).getMessageNumber()+"");
            } catch (Exception e) {
                Log.e("BookListAdapter","error in setTextView",e);
            }
            ImageLoader loader = Mysingleton.getInstance(context).getImageLoader();
            loader.get(list.get(position).getImgUrl(),ImageLoader.getImageListener(holder.imgView,R.drawable.def,R.drawable.def),120,160);
            loader = null;
            return convertView;
        }

    static class ViewHolder {
        ImageView imgView;
        TextView tvName,tvHowOld,tvAuthor,tvPublisher, tvDetail,tvStatue,tvOPrice,tvSPrice,tvSNum,tvMNum;
    }

}
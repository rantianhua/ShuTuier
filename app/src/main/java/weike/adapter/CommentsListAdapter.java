//package weike.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.android.volley.toolbox.ImageLoader;
//
//import java.util.List;
//import java.util.Map;
//
//import weike.shutuier.R;
//import weike.util.Mysingleton;
//
///**
// * Created by Rth on 2015/2/23.
// */
//public class CommentsListAdapter extends BaseAdapter {
//
//    private List<Map<String,String>> list;
//    private Context context;
//
//    public CommentsListAdapter(List<Map<String,String>> list, Context con) {
//        this.list = list;
//        this.context = con;
//    }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            if(convertView == null) {
//                convertView = LayoutInflater.from(context).inflate(R.layout.comments_list_item,null);
//                holder = new ViewHolder();
//                holder.imgView = (ImageView) convertView.findViewById(R.id.img_comment_photo);
//                holder.tvName = (TextView) convertView.findViewById(R.id.tv_comment_name);
//                holder.tvContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
//                convertView.setTag(holder);
//            }else {
//                holder =(ViewHolder) convertView.getTag();
//            }
//            try {
//                holder.tvName.setText(list.get(position).get("thirdName"));
//                holder.tvContent.setText(list.get(position).get("mark1"));
//            } catch (Exception e) {
//                Log.e("BookListAdapter","error in setTextView",e);
//            }
//            ImageLoader loader = Mysingleton.getInstance(context).getImageLoader();
//            loader.get(list.get(position).get("Head"),ImageLoader.getImageListener(holder.imgView,R.drawable.def,R.drawable.def),20,20);
//            loader = null;
//            return convertView;
//        }
//
//    static class ViewHolder {
//        ImageView imgView;
//        TextView tvName,tvContent;
//    }
//
//}

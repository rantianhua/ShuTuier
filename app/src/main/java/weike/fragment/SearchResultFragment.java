package weike.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.BookListAdapter;
import weike.data.BookItem;
import weike.data.ListBookData;
import weike.myenums.SearchFragmentsLabel;
import weike.shutuier.BookDetailActivity;
import weike.shutuier.MainActivity;
import weike.shutuier.R;
import weike.shutuier.SearchActivity;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/4/18.
 */
public class SearchResultFragment extends Fragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listview_search_classifys)
    ListView listView;
    @InjectView(R.id.rl_loading_data)
    RelativeLayout rlLoading;

    private ListBookData data = null;
    private Handler searchHan = null;
    public static final String TAG = "SearchResultFragment";
    private BookListAdapter adapter = null;
    private ProgressBar pb = null;
    private TextView tvRemind = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data  = ListBookData.getInstance(Constants.TYPE_6);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_list,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        rlLoading.setVisibility(View.VISIBLE);
        pb = (ProgressBar) rlLoading.findViewById(R.id.progressBar_loading);
        tvRemind = (TextView) rlLoading.findViewById(R.id.tv_loading);
        adapter = new BookListAdapter(data.getList(),getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    //视图创建完后开始搜索
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchBook();
    }

    //搜索
    private void searchBook() {
        if(!MainActivity.netConnect)  {
            showToast("网络不可用！");
            return;
        }
        if(searchHan == null) {
            initSearchHandler();
        }
        String s = getArguments().getString("search");
        try{
            HttpTask task = new HttpTask(getActivity(),Constants.SEARCHLINK+s,searchHan,Constants.TYPE_6,null);
            HttpManager.startTask(task);
        }catch ( Exception e ) {
            Log.e(TAG,"error in search book",e);
        }
    }

    private void initSearchHandler() {
        searchHan = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(rlLoading.getVisibility() == View.VISIBLE) {
                    rlLoading.setVisibility(View.INVISIBLE);
                }
                if(msg.what == 0) {
                    //更新数据
                    adapter.updateData(data.getList());
                    checkData();
                }else {
                    showToast("搜索失败，稍后再试!");
                }
            }
        };
    }

    //检查数据是否为空
    private void checkData() {
        if(adapter.getCount() == 0) {
           remindNullData();
        }
    }

    //提醒用户没有搜索到结果
    private void remindNullData() {
        if(rlLoading.getVisibility() == View.INVISIBLE) {
            rlLoading.setVisibility(View.VISIBLE);
        }
        if(pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.INVISIBLE);
        }
        tvRemind.setVisibility(View.VISIBLE);
        tvRemind.setText("没有您要的信息");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    public static SearchResultFragment getInstance(String s) {
        SearchActivity.currentFragment = SearchFragmentsLabel.SearchResult;
        Bundle bundle = new Bundle();
        bundle.putString("search",s);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ListBookData.recycle(Constants.TYPE_6);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookItem item = (BookItem)listView.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ITEM_ID,item.getId());
        intent.putExtra(Constants.REQUEST_FROM_FRAGMENT,Constants.TYPE_6);
        startActivity(intent);
        getActivity().finish();
    }
}

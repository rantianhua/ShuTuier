package weike.fragment.hometabs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import weike.adapter.BookListAdapter;
import weike.data.ListBookData;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/2/14.
 */
public class ProgramFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {

    private static ProgramFragment fragment = null;
    private final String dataLink = Constants.OLink+"编程";
    private SwipeRefreshLayout refreshLayout = null;
    private BookListAdapter adapter = null;
    private ListBookData data = null;
    private Handler handler = null;
    private Boolean isDataInited = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ListBookData.getInstance(Constants.TYPE_2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        refreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(this);
        ListView listView = (ListView) v.findViewById(R.id.listview);
        adapter = new BookListAdapter(data.getList(),getActivity());
        listView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isDataInited) {
            initData();
        }
    }

    private void initData() {
        isDataInited = true;
        getData();
    }

    private void getData(){
        if(handler == null) {
            initHandler();
        }
        //开始网络任务，下载数据
        HttpTask textbookTask = new HttpTask(getActivity(),dataLink,handler,Constants.TYPE_2,null);
        HttpManager.startTask(textbookTask);
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                switch (msg.what) {
                    case 0:
                        adapter.notifyDataSetChanged();
                        Log.i("ProgrameFragment", "hh" + data.getList().size());
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "哎呀！下载出了问题", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(),"哎呀！解析出了问题",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public static ProgramFragment getInstance() {
        if(fragment == null) {
            fragment = new ProgramFragment();
        }
        return fragment;
    }

    @Override
    public void onRefresh() {
        getData();
    }
}

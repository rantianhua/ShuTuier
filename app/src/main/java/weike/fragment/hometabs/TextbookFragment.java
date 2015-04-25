package weike.fragment.hometabs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.BookListAdapter;
import weike.adapter.GridCollegeAdapter;
import weike.data.BookItem;
import weike.data.ListBookData;
import weike.shutuier.BookDetailActivity;
import weike.shutuier.MainActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;

/**
 * Created by Rth on 2015/2/14.
 */
public class TextbookFragment extends Fragment   implements SwipeRefreshLayout.OnRefreshListener
        ,AdapterView.OnItemClickListener ,View.OnClickListener{

    @InjectView(R.id.listview)
    ListView listView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout  refreshLayout;
    @InjectView(R.id.img_up_down)
    ImageView showGrid;
    @InjectView(R.id.tv_choose_college)
    TextView tvCollege;
    @InjectView(R.id.rl_college)
    RelativeLayout rlCollege;
    @InjectView(R.id.gridView_colleges)
    GridView gridView;
    @InjectView(R.id.rl_tab_textbook_loading)
    RelativeLayout rlLoading;

    private static TextbookFragment fragment = null;
    private BookListAdapter adapter = null;
    private ListBookData data = null;
    private final String dataLink = Constants.OLink + "0";
    private Handler handler = null;
    private boolean isDatainited = false;
    private int action = 0;
    Animation rotate1 = null,rotate2;  //旋转动画
    private String[] colleges = {"全部","计算机院","通信工程","电子工程"
            ,"机电学院","物光学院","经管学院","数统学院","人文学院"
            ,"外国语学院","软件学院","微电子院","空间学院","材料与纳米",
            "国际教育","网络教育"};
    private ScaleAnimation expand = null, fold = null;
    private boolean  changeCollege = false;
    private String preText  = null;
    private List<BookItem> temp = new ArrayList<>();
    private ProgressBar pb = null;
    private TextView tvMessage = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        data = ListBookData.getInstance(Constants.TYPE_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_textbook,container,false);
        initView(v);
        return v;
    }

    private void initView( View v) {
        ButterKnife.inject(this, v);

        rlLoading.setVisibility(View.VISIBLE);
        pb = (ProgressBar) rlLoading.findViewById(R.id.progressBar_loading);
        tvMessage = (TextView) rlLoading.findViewById(R.id.tv_loading);

        refreshLayout.setColorSchemeResources(R.color.section_selected);
        refreshLayout.setOnRefreshListener(this);
        adapter = new BookListAdapter(data.getList(),getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        GridCollegeAdapter collegeAdapter = new GridCollegeAdapter(getActivity(),colleges);
        gridView.setAdapter(collegeAdapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(this);

        showGrid.setOnClickListener(this);
        gridView.setVisibility(View.INVISIBLE);

        tvCollege.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                preText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!changeCollege && !preText.equals(s.toString())) {
                    changeCollege = true;
                }
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isDatainited) {
            initData();
        }else {
            stopLoading();
        }
    }

    private void initData() {
        isDatainited= true;
        getData();
    }

    private void getData(){
        if(MainActivity.netConnect){
            if(handler == null) {
                initHandler();
            }
            //开始网络任务，下载数据
            HttpTask textbookTask = new HttpTask(getActivity(),dataLink,handler,Constants.TYPE_1,null);
            HttpManager.startTask(textbookTask);
        }else {
            stopLoading();
            Toast.makeText(getActivity(),"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    private void initHandler() {

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                stopLoading();
                switch (msg.what) {
                    case 0:
                        adapter.updateData(data.getList());
                        checkNULLData();
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"哎呀！下载出了问题",Toast.LENGTH_SHORT).show();
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

    private void checkNULLData() {
        //检查数据源是否为空
        if(adapter.getCount() == 0) {
            remindNullData();
        }
    }

    private void remindNullData() {
        //提示用户没有加载到数据
        if(rlLoading.getVisibility() == View.INVISIBLE) {
            rlLoading.setVisibility(View.VISIBLE);
        }
        pb.setVisibility(View.INVISIBLE);
        tvMessage.setText("暂时没有数据，稍后刷新试试。");
    }

    public static TextbookFragment getInstance() {
        if(fragment == null) {
            fragment = new TextbookFragment();
        }
        return fragment;
    }

    private void stopLoading() {
        if(rlLoading.getVisibility() == View.VISIBLE) rlLoading.setVisibility(View.INVISIBLE);
        if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        stopLoading();
        data.getList().clear();
        getData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridView_colleges:
                changeTvCollege(position);
                break;
            case R.id.listview:
                showDetail(position);
                break;
         }

    }

    private void changeTvCollege(int position) {
        switch (position) {
            case 0:
                tvCollege.setText("全部");
                break;
            case 1:
                tvCollege.setText("计算机学院");
                break;
            case 2:
                tvCollege.setText("通信工程学院");
                break;
            case 3:
                tvCollege.setText("电子工程学院");
                break;
            case 4:
                tvCollege.setText("机电工程学院");
                break;
            case 5:
                tvCollege.setText("物理与光电工程学院");
                break;
            case 6:
                tvCollege.setText("经济管理学院");
                break;
            case 7:
                tvCollege.setText("数学与统计学院");
                break;
            case 8:
                tvCollege.setText("人文学院");
                break;
            case 9:
                tvCollege.setText("外国语学院");
                break;
            case 10:
                tvCollege.setText("软件学院");
                break;
            case 11:
                tvCollege.setText("微电子学院");
                break;
            case 12:
                tvCollege.setText("空间科学与技术学院");
                break;
            case 13:
                tvCollege.setText("先进材料与纳米科技");
                break;
            case 14:
                tvCollege.setText("国际教育学院");
                break;
            case 15:
                tvCollege.setText("网络与继续教育学院");
                break;
            default:
                break;
        }
    }

    private void showDetail(int position) {
        BookItem item = (BookItem)listView.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
        intent.putExtra(Constants.EXTRA_ITEM_ID,item.getId());
        intent.putExtra(Constants.REQUEST_FROM_FRAGMENT,Constants.TYPE_1);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_up_down:
                startRoted();
                break;
         }
    }

    private void startRoted() {
        if(expand == null) {
            expand = (ScaleAnimation)AnimationUtils.loadAnimation(getActivity(),R.anim.gridview_colleges_expand);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    gridView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if(fold == null) {
            fold = (ScaleAnimation) AnimationUtils.loadAnimation(getActivity(),R.anim.gridview_colleges_fold);
            fold.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    gridView.setVisibility(View.GONE);
                    //更新listView的内容
                    if(changeCollege) {
                        upDateListData();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if(rotate1 == null) {
            rotate1 = AnimationUtils.loadAnimation(getActivity(),R.anim.view_rotate_1);
            rotate1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                     gridView.startAnimation(expand);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if(rotate2 == null) {
            rotate2 = AnimationUtils.loadAnimation(getActivity(),R.anim.view_rotate_2);
            rotate2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    gridView.startAnimation(fold);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if(action == 0) {
            showGrid.startAnimation(rotate1);
            action = 1;
            return;
        }
        if(action == 1) {
            showGrid.startAnimation(rotate2);
            action = 0;
        }
    }

    //更新ListView显示的内容
    private void upDateListData() {
        refreshLayout.setRefreshing(true);
        new ChangeListData().execute();
    }

    private class ChangeListData extends AsyncTask<Void,Void,List<BookItem>> {
        @Override
        protected List<BookItem> doInBackground(Void... params) {
            if(tvCollege.getText().equals("全部")) {
                return data.getList();
            }else {
                temp.clear();
                for(BookItem item : data.getList()) {
                    if(item.getSubClassify().equals(tvCollege.getText().toString())) {
                        temp.add(item);
                    }
                }
                return temp;
            }
        }

        @Override
        protected void onPostExecute(List<BookItem>  maps) {
            super.onPostExecute(maps);
            refreshLayout.setRefreshing(false);
            changeCollege = false;
            adapter.updateData(maps);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

}

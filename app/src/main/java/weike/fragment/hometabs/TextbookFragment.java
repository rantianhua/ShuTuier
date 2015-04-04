package weike.fragment.hometabs;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.BookListAdapter;
import weike.adapter.GridCollegeAdapter;
import weike.data.BookItem;
import weike.data.ListBookData;
import weike.fragment.CollegesDialogFragment;
import weike.shutuier.BookDetailActivity;
import weike.shutuier.R;
import weike.util.ConnectionDetector;
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

    private static TextbookFragment fragment = null;
    private BookListAdapter adapter = null;
    private ListBookData data = null;
    private final String dataLink = Constants.OLink + "教材";
    private Handler handler = null;
    private boolean isDatainited = false;
    private int action = 0;
    Animation rotate1 = null,rotate2;  //旋转动画
    private CollegesDialogFragment collegesDialogFragment = null;
    private String[] colleges = {"全部","计算机院","通电学院","电院"
            ,"机电学院","物光学院","经管学院","数统学院","人文学院"
            ,"外国语学院","软件学院","微电子院","空间学院","材料与纳米",
            "国际学院","网络学院"};

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

        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.APPEARING, transition.getAnimator(LayoutTransition.APPEARING));
        transition.setAnimator(LayoutTransition.DISAPPEARING,transition.getAnimator(LayoutTransition.DISAPPEARING));
        gridView.setLayoutTransition(transition);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isDatainited) {
            initData();
        }
    }

    private void initData() {
        isDatainited= true;
        getData();
    }

    private void getData(){
        if(ConnectionDetector.isConnectingToInternet(getActivity())){
            if(handler == null) {
                initHandler();
            }
            //开始网络任务，下载数据
            HttpTask textbookTask = new HttpTask(getActivity(),dataLink,handler,Constants.TYPE_1,null);
            HttpManager.startTask(textbookTask);
        }else {
            Toast.makeText(getActivity(),"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }


    private void initHandler() {

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        adapter.notifyDataSetChanged();
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

    public static TextbookFragment getInstance() {
        if(fragment == null) {
            fragment = new TextbookFragment();
        }
        return fragment;
    }

    @Override
    public void onRefresh() {
        getData();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridView_colleges:
                Toast.makeText(getActivity(),"点击了"+position,Toast.LENGTH_LONG).show();
                break;
            case R.id.listview:
                showDetail(position);
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
        if(rotate1 == null) {
            rotate1 = AnimationUtils.loadAnimation(getActivity(),R.anim.view_rotate_1);
            rotate1.setAnimationListener(new Animation.AnimationListener() {
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
        if(rotate2 == null) {
            rotate2 = AnimationUtils.loadAnimation(getActivity(),R.anim.view_rotate_2);
            rotate2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    gridView.setVisibility(View.GONE);
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

}

package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.myenums.SearchFragmentsLabel;
import weike.shutuier.R;
import weike.shutuier.SearchActivity;

/**
 * Created by Rth on 2015/4/18.
 */
public class SubClassifyFragment extends Fragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listview_search_classifys)
    ListView listView;

    private ParentClassify parentClassify = ParentClassify.LITERATURE;

    //记录父分类的枚举类
    private enum ParentClassify{
        LITERATURE,
        POPULAR,
        CULTURE,
        LIFE,
        MANAGEMENT,
        SCIENCE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_list,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this, v);
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1,getListData()));
        listView.setOnItemClickListener(this);
    }

    public List<String> getListData() {
        List<String> list = new ArrayList<>();
        //根据主分类中的项初始化子分类的数据
        switch (getArguments().getInt("pos")) {
            case 0:
                list.add("小说");
                list.add("随笔");
                list.add("散文");
                list.add("诗歌");
                list.add("杂文");
                parentClassify = ParentClassify.LITERATURE;
                break;
            case 1:
                list.add("漫画");
                list.add("青春");
                list.add("推理");
                list.add("悬疑");
                list.add("科幻");
                list.add("言情");
                list.add("武侠");
                parentClassify = ParentClassify.POPULAR;
                break;
            case 2:
                list.add("历史");
                list.add("心理学");
                list.add("哲学");
                list.add("传记");
                list.add("社会学");
                list.add("设计");
                list.add("艺术");
                list.add("政治");
                list.add("建筑");
                list.add("佛教");
                list.add("绘画");
                list.add("戏剧");
                list.add("人文");
                list.add("宗教");
                list.add("军事");
                list.add("美术");
                list.add("考古");
                parentClassify = ParentClassify.CULTURE;
                break;
            case 3:
                list.add("爱情");
                list.add("旅行");
                list.add("励志");
                list.add("女性");
                list.add("摄影");
                list.add("美食");
                list.add("职场");
                list.add("教育");
                list.add("情感");
                list.add("健康");
                list.add("手工");
                list.add("人际关系");
                list.add("养生");
                list.add("两性");
                list.add("家居");
                parentClassify = ParentClassify.LIFE;
                break;
            case 4:
                list.add("经济学");
                list.add("管理");
                list.add("金融");
                list.add("商业");
                list.add("投资");
                list.add("营销");
                list.add("创业");
                list.add("广告");
                list.add("股票");
                list.add("策划");
                parentClassify = ParentClassify.MANAGEMENT;
                break;
            case 5:
                list.add("科普");
                list.add("互联网");
                list.add("科学");
                list.add("交互设计");
                list.add("用户体验");
                list.add("通信");
                parentClassify = ParentClassify.SCIENCE;
                break;
        }
        return list;
    }

    public static SubClassifyFragment getInstance(int postion) {
        SearchActivity.currentFragment = SearchFragmentsLabel.SubClassify;
        Bundle bundle = new Bundle();
        bundle.putInt("pos",postion);
        SubClassifyFragment fragment = new SubClassifyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView) view;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in,R.anim.left_out)
                .replace(R.id.container_search,SearchResultFragment.getInstance(tv.getText().toString()))
                .commit();
    }
}

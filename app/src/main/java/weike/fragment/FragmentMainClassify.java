package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.MainClassifyAdapter;
import weike.myenums.SearchFragmentsLabel;
import weike.shutuier.R;
import weike.shutuier.SearchActivity;

/**
 * Created by Rth on 2015/4/17.
 * 二手书的主分类
 */

public class FragmentMainClassify extends Fragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listview_search_classifys)
    ListView listView;

    private String[] mainClassify = {"文学","流行","文化","生活","经管 ","科技"};
    private String[] subClassify = {"小说/散文/诗歌","漫画/青春/推理","历史/心理学/哲学"
            ,"爱情/旅行/励志","经济学/管理/股票 ","科普/互联网/交互设计"};

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
        ButterKnife.inject(this,v);
        MainClassifyAdapter adapter = new MainClassifyAdapter(mainClassify,subClassify,getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public static FragmentMainClassify getInstance(){
        SearchActivity.currentFragment = SearchFragmentsLabel.MainClassify;
        return new FragmentMainClassify();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in,R.anim.left_out)
                .replace(R.id.container_search,SubClassifyFragment.getInstance(position))
                .commit();
    }
}

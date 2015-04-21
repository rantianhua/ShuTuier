package weike.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.OpenSearchRecordLisenter;
import weike.myenums.SearchFragmentsLabel;
import weike.shutuier.R;
import weike.shutuier.SearchActivity;
import weike.util.Constants;

/**
 * Created by Rth on 2015/4/18.
 * 搜索记录
 */
public class SearchRecordFragment extends Fragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listview_search_classifys)
    ListView listView;

    private List<String> listData = new ArrayList<>();
    public static OpenSearchRecordLisenter lisenter = null;
    private static EditText etSearch = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_list,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        getListData();
        if(listData.size() > 0) {
            //为listView添加底部试图
            addFooterView();
            listView.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_expandable_list_item_1,listData));
            listView.setOnItemClickListener(this);
        }
    }

    private void addFooterView() {
        TextView textView = new TextView(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setText("清除搜索历史");
        textView.setPadding(0,10,0,10);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除搜索历史记录
                SharedPreferences sp = getActivity().getSharedPreferences(Constants.SP_SEARCH_RECORD,0);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                listView.setVisibility(View.GONE);
            }
        });
        listView.addFooterView(textView);
    }

    public void getListData() {
        SharedPreferences sp = getActivity().getSharedPreferences(Constants.SP_SEARCH_RECORD, 0);
        Set<String> set = sp.getStringSet(Constants.RECORD_SET,null);
        if(set != null) {
            for(String s : set) {
                listData.add(s);
            }
        }
    }

    public static SearchRecordFragment getInstance(EditText et) {
        SearchActivity.currentFragment = SearchFragmentsLabel.SearchRecord;
        etSearch = et;
        return new SearchRecordFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.search_with_classify) {
            saveSearchHistory(etSearch.getText().toString());
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    //关闭软键盘
    private void hideSoftKeys() {
        InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etSearch.getWindowToken(),0);
    }

    //保存搜索的历史记录
    private void saveSearchHistory(String s) {
        if(!TextUtils.isEmpty(s)) {
            SharedPreferences sp = getActivity().getSharedPreferences(Constants.SP_SEARCH_RECORD, 0);
            Set<String> newSet = new LinkedHashSet<>();
            Set<String> set = sp.getStringSet(Constants.RECORD_SET,null);
            if(set != null) {
                newSet.addAll(set);
                set = null;
            }
            newSet.add(s);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.putStringSet(Constants.RECORD_SET,newSet);
            editor.apply();
            gotoSearch(s);
            etSearch.setText("");
        }
    }

    private void changeFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.right_in,R.anim.left_out)
                .replace(R.id.container_search,fragment)
                .commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tv = (TextView)view;
        gotoSearch(tv.getText().toString());
    }

    //进入结果页面搜索输入搜索
    private void gotoSearch(String s) {
        hideSoftKeys();
        changeFragment(SearchResultFragment.getInstance(s));
    }


    @Override
    public void onResume() {
        super.onResume();
        if(lisenter != null) {
            lisenter.openRecord();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lisenter != null) {
            lisenter.closeRecord();
            lisenter = null;
        }
    }
}

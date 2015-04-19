package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.myenums.SearchFragmentsLabel;
import weike.shutuier.R;
import weike.shutuier.SearchActivity;

/**
 * Created by Rth on 2015/4/18.
 */
public class SearchResultFragment extends Fragment {

    @InjectView(R.id.listview_search_classifys)
    ListView listView;

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
    }

    public static SearchResultFragment getInstance() {
        SearchActivity.currentFragment = SearchFragmentsLabel.SearchResult;
        return new SearchResultFragment();
    }
}

package weike.shutuier;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import myinterface.OpenSearchRecordLisenter;
import weike.fragment.FragmentMainClassify;
import weike.fragment.SearchRecordFragment;
import weike.myenums.SearchFragmentsLabel;

/**
 * Created by Rth on 2015/4/17.
 */
public class SearchActivity extends ActionBarActivity implements OpenSearchRecordLisenter,View.OnClickListener {

    @InjectView(R.id.toolbar_search)
    Toolbar toolbar;

    private View customSearchView = null;
    public static SearchFragmentsLabel currentFragment = SearchFragmentsLabel.MainClassify;   //记录当前显示的fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        customSearchView = LayoutInflater.from(this).inflate(R.layout.et_searchview,null);
        customSearchView.setOnClickListener(this);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,18,0);
        getSupportActionBar().setCustomView(customSearchView,params);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(R.id.container_search, FragmentMainClassify.getInstance()).commit();
    }

    private void changeFragment(Fragment fragment,int in,int out) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(in, out)
                .replace(R.id.container_search, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if(currentFragment != SearchFragmentsLabel.MainClassify) {
                changeFragment(FragmentMainClassify.getInstance(),R.anim.left_in,R.anim.right_out);
            }else {
                onBackPressed();
            }
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        SearchRecordFragment.lisenter = this;
        changeFragment(SearchRecordFragment.getInstance((EditText) customSearchView),R.anim.right_in,R.anim.left_out);
    }

    @Override
    public void openRecord() {
        customSearchView.setOnClickListener(null);
    }

    @Override
    public void closeRecord() {
        customSearchView.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment != SearchFragmentsLabel.MainClassify) {
            changeFragment(FragmentMainClassify.getInstance(),R.anim.left_in,R.anim.right_out);
        }else {
            super.onBackPressed();
        }
    }
}


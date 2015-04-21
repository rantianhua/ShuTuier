package weike.shutuier;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.FeedbackFragment;

/**
 * Created by Rth on 2015/4/17.
 */
public class FeedbackActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar_feedback)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setTitle("用户反馈");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(R.id.container_feedback,new FeedbackFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}

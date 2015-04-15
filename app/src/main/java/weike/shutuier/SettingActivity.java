package weike.shutuier;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.SettingFragment;
import weike.util.FragmentLabel;

/**
 * Created by Rth on 2015/4/15.
 */
public class SettingActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setTitle(FragmentLabel.Commit.getValue());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(R.id.container_setting, SettingFragment.getInstance()).commit();
    }
}

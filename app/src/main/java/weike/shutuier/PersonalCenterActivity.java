package weike.shutuier;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.PersonalFragment;

/**
 * Created by Rth on 2015/3/23.
 */
public class PersonalCenterActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar_center)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initView();

    }

    private void initView() {
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("个人中心");

        getSupportFragmentManager().beginTransaction().add(R.id.container, PersonalFragment.getInstance()).commit();
    }
}

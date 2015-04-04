package weike.shutuier;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.ShareFragment;

/**
 * Created by Rth on 2015/4/2.
 */
public class TestActivity extends FragmentActivity {

    @InjectView(R.id.tv_share)
    TextView tvShare;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
    }


    private void initView() {
        ButterKnife.inject(this);
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //controller.openShare(TestActivity.this,false);
                showCustomView();
            }
        });
    }

    //展示自定义分享UI
    private void showCustomView() {
        ShareFragment fragment = new ShareFragment();
        fragment.show(getSupportFragmentManager(),"share");
    }

}

package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.fragment.hometabs.GiveFragment;
import weike.fragment.hometabs.KaoyanFragment;
import weike.fragment.hometabs.LatestFragment;
import weike.fragment.hometabs.ProgramFragment;
import weike.fragment.hometabs.TextbookFragment;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/2/9.
 */
public class HomeFragment extends Fragment {

    private static HomeFragment homeFragment = null;

    @InjectView(R.id.tv_home_latest)
    TextView tvLatest;
    @InjectView(R.id.tv_home_textbook)
    TextView tvTextbook;
    @InjectView(R.id.tv_home_program)
    TextView tvProgram;
    @InjectView(R.id.tv_home_kaoyan)
    TextView tvKaoyan;
    @InjectView(R.id.tv_home_give)
    TextView tvGvie;
    @InjectView(R.id.vp_home)
    ViewPager vp;

    private List<Fragment> frags = null;
    private FragmentPagerAdapter adapter = null;
    private int unSelectColor,selectedColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedColor = getActivity().getResources().getColor(R.color.tv_selected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        unSelectColor = tvGvie.getCurrentTextColor();

        frags = new ArrayList<>();
        frags.add(LatestFragment.getInstance());
        frags.add(TextbookFragment.getInstance());
        frags.add(ProgramFragment.getInstance());
        frags.add(KaoyanFragment.getInstance());
        frags.add(GiveFragment.getInstance());

        adapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return frags.get(position);
            }

            @Override
            public int getCount() {
                return frags.size();
            }
        };
        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        tvLatest.setTextColor(selectedColor);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        tvLatest.setTextColor(selectedColor);
                    case 1:
                        tvTextbook.setTextColor(selectedColor);
                        break;
                    case 2:
                        tvProgram.setTextColor(selectedColor);
                        break;
                    case 3:
                        tvKaoyan.setTextColor(selectedColor);
                        break;
                    case 4:
                        tvGvie.setTextColor(selectedColor);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    protected void resetTextView() {
        tvLatest.setTextColor(unSelectColor);
        tvGvie.setTextColor(unSelectColor);
        tvKaoyan.setTextColor(unSelectColor);
        tvProgram.setTextColor(unSelectColor);
        tvTextbook.setTextColor(unSelectColor);
    }

    public static HomeFragment getInstance() {
        if(homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

}

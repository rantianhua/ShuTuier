package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.GridAdapter;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/3/24.
 */
public class MySellFragment extends Fragment {

    @InjectView(R.id.gridView)
    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.grid_books,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        GridAdapter adapter = new GridAdapter(getActivity());
        gridView.setAdapter(adapter);
    }

    public static MySellFragment getInstance() {
        return new MySellFragment();
    }
}

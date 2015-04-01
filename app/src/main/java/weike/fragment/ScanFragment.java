package weike.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import weike.shutuier.R;

/**
 * Created by Rth on 2015/2/9.
 */

public class ScanFragment extends Fragment {

    private static ScanFragment scanFragment = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collection,container,false);
        return v;
    }

    public static ScanFragment getInstance() {
        if(scanFragment == null) {
            scanFragment = new ScanFragment();
        }
        return scanFragment;
    }
}

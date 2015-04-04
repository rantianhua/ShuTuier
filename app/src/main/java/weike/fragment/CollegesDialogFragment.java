package weike.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.adapter.GridCollegeAdapter;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/4/2.
 */
public class CollegesDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    @InjectView(R.id.gridView_colleges)
    GridView gridView;

    private String[] colleges = {"全部","计算机院","通电学院","电院"
            ,"机电学院","物光学院","经管学院","数统学院","人文学院"
            ,"外国语学院","软件学院","微电子院","空间学院","材料与纳米",
            "国际学院","网络学院"};
    private static final String OFFSETTOP = "offset";
    private static final String HEIGHT = "height";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_colleges);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.grid_colleges,null,false);
        initView(v);
        dialog.setContentView(v);
        return dialog;
    }

    private void initView(View v) {
        ButterKnife.inject(this,v);
        GridCollegeAdapter collegeAdapter = new GridCollegeAdapter(getActivity(),colleges);
        gridView.setAdapter(collegeAdapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),"选择了"+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        //params.y = (int)getArguments().get(OFFSETTOP);
        params.y = 200;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public static CollegesDialogFragment getInstance(int topOffset,int height) {
        CollegesDialogFragment fragment = new CollegesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OFFSETTOP,topOffset);
        bundle.putInt(HEIGHT,height);
        fragment.setArguments(bundle);
        return fragment;
    }
}

package weike.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Constants;

/**
 * Created by Rth on 2015/3/29.
 */
public class CoverDialogFragment extends DialogFragment implements View.OnClickListener {

    @InjectView(R.id.ibn_picture)
    ImageButton picture;
    @InjectView(R.id.ibn_take_picture)
    ImageButton take;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_cover);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.view_covers,null);
        ButterKnife.inject(this,v);
        picture.setOnClickListener(this);
        take.setOnClickListener(this);
        dialog.setContentView(v);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibn_take_picture:
                sendResult(0);
                break;
            case R.id.ibn_picture:
                sendResult(1);
                break;
        }
    }


    //将用户选择的结果返回SellFragment
    private void sendResult(int i) {
        if(getTargetFragment() == null) return;
        Intent intent = new Intent();
        if(i == 0) {
            intent.putExtra(Constants.EXTRA_PIC,"拍照");
        }else {
            intent.putExtra(Constants.EXTRA_PIC,"图片");
        }
        this.dismiss();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        intent = null;
    }


}

package weike.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;

/**
 * Created by Rth on 2015/3/28.
 */
public class ContactDialogFragment extends DialogFragment implements View.OnClickListener{

    @InjectView(R.id.tv_contact_none)
    TextView tvNone;
    @InjectView(R.id.tv_contact_qq)
    TextView tvQQ;
    @InjectView(R.id.tv_contact_phone)
    TextView tvPhone;
    @InjectView(R.id.tv_contact_wx)
    TextView tvWx;
    @InjectView(R.id.tv_contact_email)
    TextView tvEmail;
    @InjectView(R.id.tv_contact_cancel)
    TextView tvCancel;
    @InjectView(R.id.tv_contact_end)
    TextView tvEnd;
    @InjectView(R.id.line_contact_one)
    View line1;
    @InjectView(R.id.line_contact_two)
    View line2;
    @InjectView(R.id.line_contact_three)
    View line3;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(),R.style.dialog_contacts);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_contacts,null,false);
        initView(v);
        //dialog.setTitle("选择联系方式");
        dialog.setContentView(v);
        return dialog;
    }

    private void initView(View v) {
        ButterKnife.inject(this, v);
        tvEnd.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvQQ.setText("1348748184");
        tvPhone.setText("15929733174");
        tvWx.setText("rth");
        tvEmail.setText("15953163807@163.com");
        tvQQ.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvWx.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_contact_end:
                break;
            case R.id.tv_contact_cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(400, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}

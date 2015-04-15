package weike.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.Utils;

/**
 * Created by Rth on 2015/3/28.
 */
public class IsbnDialogFragment extends DialogFragment implements View.OnClickListener{

    @InjectView(R.id.et_isbn)
    EditText etIsbn;
    @InjectView(R.id.btn_sure_isbn)
    Button ok;
    @InjectView(R.id.btn_cancel_isbn)
    Button cancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_isbn);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_input_isbn,null,false);
        ButterKnife.inject(this,v);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(v);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sure_isbn:
                String isbn = etIsbn.getText().toString();
                if(!TextUtils.isEmpty(isbn)) {
                    finishIsbn(isbn);
                    this.dismiss();
                }
                break;
            case R.id.btn_cancel_isbn:
                this.dismiss();
                break;
        }
    }

    private void finishIsbn(String isbn) {
        if(getTargetFragment() == null) return;
        Intent i = new Intent();
        i.putExtra(Constants.EXTRA_ISBN, isbn);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,i);
        i = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(Utils.getWindowWidth(getActivity())-40, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}

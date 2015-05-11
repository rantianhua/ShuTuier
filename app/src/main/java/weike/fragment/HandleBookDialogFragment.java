package weike.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weike.data.ChangBookSateData;
import weike.shutuier.MainActivity;
import weike.shutuier.R;
import weike.util.Constants;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.Utils;

/**
 * Created by Rth on 2015/3/29.
 */
public class HandleBookDialogFragment extends DialogFragment implements View.OnClickListener {

    @InjectView(R.id.tv_center_complete_trade)
    TextView tvComplete;
    @InjectView(R.id.tv_center_delete_trade)
    TextView tvDelete;
    @InjectView(R.id.tv_center_cancel)
    TextView tvCancel;
    @InjectView(R.id.progress_center_complete)
    ProgressBar pbComplete;
    @InjectView(R.id.progress_center_delete)
    ProgressBar pbDelete;

    private Handler han = null;
    public static final String TAG = "HandleBookDialogFragment";
    private int whichAciton;    //记录何种操作

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog_cover);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.view_bottom_handle_mybook,null);
        initView(v);

        tvComplete.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        dialog.setContentView(v);
        return dialog;
    }

    private void initView(View v) {
        ButterKnife.inject(this, v);
        //0表示未交易，1表示已交易
        tvComplete.setVisibility(getArguments().getString(Constants.HANDLE_BOOK_PRESTATE).equals("0") ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(Utils.getWindowWidth(getActivity()) - 20,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM);
        window = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_center_delete_trade:
                whichAciton = 1;
                doAction(1);
                break;
            case R.id.tv_center_complete_trade:
                whichAciton = 0;
                doAction(0);
                break;
            case R.id.tv_center_cancel:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    private void doAction(int i) {
        if(MainActivity.netConnect) {
            tvDelete.setEnabled(false);
            tvComplete.setEnabled(false);
            changeBook(i);
        }else {
            Toast.makeText(getActivity(),"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    private void changeBook(int i) {
        if(han == null) {
            initHandler();
        }
        ChangBookSateData data = ChangBookSateData.getInstance();
        data.setId(getArguments().getString(Constants.HANDLE_BOOK_ID));
        if(i == 0) {
            //完成交易
            if(pbComplete.getVisibility() == View.INVISIBLE) {
                pbComplete.setVisibility(View.VISIBLE);
            }
            data.setClose("0");
        }else {
            //删除交易
            if(pbDelete.getVisibility() == View.INVISIBLE) {
                pbDelete.setVisibility(View.VISIBLE);
            }
            data.setClose("1");
        }
        HttpTask task = new HttpTask(getActivity(), Constants.CHANGEBOOK,han,TAG,"post");
        HttpManager.startTask(task);
    }

    private void initHandler() {
        han = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(pbComplete.getVisibility() == View.VISIBLE) {
                    pbComplete.setVisibility(View.INVISIBLE);
                }
                if(pbDelete.getVisibility() == View.VISIBLE) {
                    pbDelete.setVisibility(View.INVISIBLE);
                }
                if(msg.what == 0) {
                    boolean res = false;
                    try {
                        JsonReader reader = new JsonReader(new StringReader((String)msg.obj));
                        reader.beginObject();
                        while (reader.hasNext()) {
                            if(reader.nextName().equals("msg")) {
                                res = reader.nextBoolean();
                                break;
                            }
                        }
                        reader.endObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(res) {
                        Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT).show();
                        callBack();
                    }else {
                        Toast.makeText(getActivity(),"操作失败",Toast.LENGTH_SHORT).show();
                        tvComplete.setEnabled(true);
                        tvDelete.setEnabled(true);
                    }
                }else {
                    Toast.makeText(getActivity(),"操作失败",Toast.LENGTH_SHORT).show();
                    tvComplete.setEnabled(true);
                    tvDelete.setEnabled(true);
                }
            }
        };
    }

    //返回处理信息给targetFragment
    private void callBack() {
        if(getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.HANDLE_BOOK_ACTION,whichAciton);
        intent.putExtra(Constants.HANDLE_BOOK_POSITION, getArguments().getInt(Constants.HANDLE_BOOK_POSITION));
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        this.dismiss();
    }

    public static HandleBookDialogFragment getInstance(String id,int pos,String preSate) {
        Bundle bun = new Bundle();
        bun.putString(Constants.HANDLE_BOOK_ID,id);
        bun.putInt(Constants.HANDLE_BOOK_POSITION,pos);
        bun.putString(Constants.HANDLE_BOOK_PRESTATE, preSate);
        HandleBookDialogFragment fragment = new HandleBookDialogFragment();
        fragment.setArguments(bun);
        return fragment;
    }
}

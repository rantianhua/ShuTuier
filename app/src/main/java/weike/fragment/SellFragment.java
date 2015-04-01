package weike.fragment;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import qiniu.AuthException;
import qiniu.PutPolicy;
import weike.data.CommitBookData;
import weike.shutuier.R;
import weike.util.ConnectionDetector;
import weike.util.Constants;
import weike.util.DouBanTask;
import weike.util.HttpManager;
import weike.util.HttpTask;
import weike.util.Mysingleton;
import weike.util.Utils;

/**
 * Created by Rth on 2015/2/28.
 */
public class SellFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener{

    private static SellFragment sellFragment = null;
    private EditText etBookName,etAuthor,
            etPublisher,etOPrice,etSPrice,etDescription,etRemarks,etNumber,etSendCondition;
    private Spinner mainClassify,subClassify,howOld,college;
    private CheckBox cbPay,cbGive,cbSell,cbAskBuy;
    private ImageView imgCover;
    private Button btnAddCover,btnReset,btnSend,btnIsbn;
    private TableRow trSendCondition,trSellPrice,trCollege;
    private TextView howCommit = null,tvSPrice = null;
    private TableLayout tableInput = null;

    private  String picPath =null;  //待上传图片的路径
    private String fileName = null; //图片名
    private Handler isbnHan = null,commitHandler = null;
    private final int REQUEST_ISBN = 20;
    private final int ALBUM_OK = 30;
    private final int REQUEST_PIC_WAY = 40;
    private ProgressDialog pd = null;
    private   ArrayAdapter<String> arrayAdapter = null; //subClassify的适配器
    private String isbn = null; //记录isbn号
    private String mainCf = "教材";   //记录选择的主分类
    private String subCf = "全部";    //记录选择的子分类
    private String oOrn = "全新"; //记录选择的新旧程度
    private LayoutTransition layoutTransition = null;   //布局动画
    private String coverDouBanUrl = null; //从豆瓣获取的封面url

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chushou,container,false);
        initView(v) ;
        return v;
    }

    private void initView(View v) {
        btnIsbn = (Button) v.findViewById(R.id.btn_isbn);
        etBookName = (EditText) v.findViewById(R.id.et_book_name);
        etAuthor = (EditText) v.findViewById(R.id.et_book_author);
        etPublisher = (EditText) v.findViewById(R.id.et_book_publisher);
        etNumber = (EditText) v.findViewById(R.id.et_book_number);
        etSendCondition = (EditText) v.findViewById(R.id.et_send_condition);
        mainClassify = (Spinner) v.findViewById(R.id.spinner_main_classify);
        subClassify = (Spinner) v.findViewById(R.id.spinner_sub_classify);
        howOld = (Spinner) v.findViewById(R.id.spinner_how_old);
        college = (Spinner) v.findViewById(R.id.spinner_college);
        etOPrice = (EditText) v.findViewById(R.id.et_origin_price);
        etSPrice = (EditText) v.findViewById(R.id.et_sell_price);
        cbPay = (CheckBox) v.findViewById(R.id.cb_pay);
        cbGive = (CheckBox) v.findViewById(R.id.cb_send);
        cbSell = (CheckBox) v.findViewById(R.id.cb_sell);
        cbAskBuy = (CheckBox) v.findViewById(R.id.cb_ask_buy);
        etDescription = (EditText) v.findViewById(R.id.et_descripition);
        etRemarks = (EditText) v.findViewById(R.id.et_remarks);
        imgCover = (ImageView) v.findViewById(R.id.img_book_cover);
        btnAddCover = (Button) v.findViewById(R.id.btn_add_book_cover);
        btnReset = (Button) v.findViewById(R.id.btn_reset);
        btnSend = (Button) v.findViewById(R.id.btn_send);
        trSendCondition = (TableRow) v.findViewById(R.id.tr_send_condition);
        trSellPrice = (TableRow) v.findViewById(R.id.tr_sell_price);
        trCollege = (TableRow) v.findViewById(R.id.tr_college);
        howCommit = (TextView) v.findViewById(R.id.tv_how_commit);
        tvSPrice =  (TextView) v.findViewById(R.id.tv_sell_price_label);
        tableInput = (TableLayout) v.findViewById(R.id.tablayout_input);

        btnIsbn.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        mainClassify.setOnItemSelectedListener(this);
        subClassify.setOnItemSelectedListener(this);
        howOld.setOnItemSelectedListener(this);
        college.setOnItemSelectedListener(this);
        cbPay.setChecked(true);      //默认选中“付款”
        cbSell.setChecked(true);  //默认动作为“出售”;
        cbPay.setOnClickListener(this);
        cbGive.setOnClickListener(this);
        cbAskBuy.setOnClickListener(this);
        cbSell.setOnClickListener(this);
        btnAddCover.setOnClickListener(this);

        setLayoutAnimation();
    }

    //处理异步加载书籍信息的Handler
    private void initIsbnHan() {
        isbnHan  = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
                if (msg.what == 0) {
                    String data = (String) msg.obj;
                    completeBaseInfo(data);
                }else if(msg.what == 1){
                    Toast.makeText(getActivity(),"ISBN有误，请输入正确的ISBN号",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"查询图书信息失败！",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void completeBaseInfo(String data)  {
        if(data != null) {
            Map<String,String> map = Utils.cutDoubanData(data);
            if(map.size() > 0 ) {
                try{
                    //得到了正确的图书信息
                    etDescription.setText(map.get("summary"));
                    etAuthor.setText(map.get("author"));
                    String originPrice = map.get("price");
                    Log.i("origin_price","it's length is " + originPrice.length() );
                    etOPrice.setText(originPrice);
                    etBookName.setText(map.get("title"));
                    etPublisher.setText(map.get("publisher"));
                    ImageLoader loader = Mysingleton.getInstance(getActivity()).getImageLoader();
                    int w  = getResources().getDimensionPixelSize(R.dimen.img_cover_width);
                    int h  = getResources().getDimensionPixelSize(R.dimen.img_cover_height);
                    coverDouBanUrl = map.get("image").replaceAll("\\\\","");
                    loader.get(coverDouBanUrl,ImageLoader.getImageListener(imgCover,R.drawable.def,R.drawable.def),w,h);
                    if(!btnAddCover.getText().toString().equals("更改封面"))
                        btnAddCover.setText("更改封面");
                    loader = null;
                }catch (NullPointerException e) {
                    Log.e("SellFragment","error in completeBaseInfo",e);
                    //没有查到该图书信息
                    Toast.makeText(getActivity(),"查询图书信息失败！lalal",Toast.LENGTH_SHORT).show();
                }
            }
            map =  null;
        }
        data = null;
    }

    public static SellFragment getInstance( ){
        if(sellFragment == null) {
            sellFragment = new SellFragment();
        }
        return sellFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_sub_classify:
                subCf = subClassify.getItemAtPosition(position).toString();
                break;
            case R.id.spinner_college:
                subCf = college.getItemAtPosition(position).toString();
                break;
            case R.id.spinner_main_classify:
                updateSubClassify(position);
                mainCf = mainClassify.getItemAtPosition(position).toString();
                break;
            case R.id.spinner_how_old:
                oOrn = howOld.getItemAtPosition(position).toString();
                break;
            default:
                break;
        }
    }

    private void updateSubClassify(int position) {
        if(arrayAdapter != null) {
            arrayAdapter = null;
        }
        switch (position) {
            case 0:
                if(trCollege.getVisibility() == View.GONE) {
                    trCollege.setVisibility(View.VISIBLE);
                }
                if(subClassify.getVisibility() == View.VISIBLE) {
                    subClassify.setVisibility(View.GONE);
                }
                break;
            case 1:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.LITERATURE);
                break;
            case 2:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.POPULAR);
                break;
            case 3:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.CULTURE);
                break;
            case 4:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.LIFE);
                break;
            case 5:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.MANAGEMENT);
                break;
            case 6:
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, Constants.SCIENCE);
                break;
            default:
                break;
        }
        if(position != 0) {
            if(trCollege.getVisibility() == View.VISIBLE) {
                trCollege.setVisibility(View.GONE);
            }
            if(subClassify.getVisibility() == View.GONE) {
                subClassify.setVisibility(View.VISIBLE);
            }
        }
        if(arrayAdapter == null) {
            return;
        }
        arrayAdapter.setDropDownViewResource(android.R.layout. simple_spinner_dropdown_item);
        subClassify.setAdapter(arrayAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_sell:
                //选中出售
                if(!cbSell.isChecked()) {
                    cbSell.setChecked(true);
                }
                cbAskBuy.setChecked(false); //取消“求购”的选中状态
                howCommit.setText("出售方式：");
                if(trSellPrice.getVisibility() == View.VISIBLE) {
                    tvSPrice.setText("售价：");
                }
                break;
            case R.id.cb_ask_buy:
                //选中求购
                if(!cbAskBuy.isChecked()) {
                    cbAskBuy.setChecked(true);
                }
                cbSell.setChecked(false);
                howCommit.setText("求购方式：");
                if(trSellPrice.getVisibility() == View.VISIBLE) {
                    tvSPrice.setText("求购价：");
                }
                break;
            case R.id.cb_pay:
                //点击“付款”
                if(!cbPay.isChecked()){
                    if(trSellPrice.getVisibility() == View.VISIBLE) {
                        trSellPrice.setVisibility(View.GONE);    //让”售价“一栏不可见
                    }
                }else {
                    if(trSellPrice.getVisibility() != View.VISIBLE) {
                        trSellPrice.setVisibility(View.VISIBLE);    //让”售价“一栏可见
                    }
                }
                break;
            case R.id.cb_send:
                //点击“赠送”
                if(!cbGive.isChecked()){
                    if(trSendCondition.getVisibility() == View.VISIBLE) {
                        trSendCondition.setVisibility(View.GONE);    //让”赠送条件“一栏不可见
                    }
                }else {
                    if(trSendCondition.getVisibility() != View.VISIBLE) {
                        trSendCondition.setVisibility(View.VISIBLE);    //让”赠送条件“一栏可见
                    }
                }
                break;
            case R.id.btn_add_book_cover:
                chooseCoverWay();
                break;
            case R.id.btn_isbn:
                showDialog();
                break;
            case R.id.btn_reset:
                reset();
                break;
            case R.id.btn_send:
                //图片上传完成后发布信息
                if(coverDouBanUrl == null) {
                    if(picPath == null) {
                        Toast.makeText(getActivity(),"信息不完整！",Toast.LENGTH_SHORT).show();
                    }else {
                        upLoadPicture();
                    }
                }else {
                    if(canCommit(coverDouBanUrl)) {
                        System.out.println(CommitBookData.getInstance().toString());
                        commitBook();
                    }else {
                        Toast.makeText(getActivity(),"信息不完整！",Toast.LENGTH_SHORT).show();
                        System.out.println(CommitBookData.getInstance().toString());
                    }
                }
                break;
        }
    }

    private void commitBook() {
        //检查网络
        if(ConnectionDetector.isConnectingToInternet(getActivity())) {
            if(pd == null) {
                pd = new ProgressDialog(getActivity());
            }
            pd.setMessage("正在发布...");
            pd.show();
            if(commitHandler == null) {
                initCommitHandler();
            }
            HttpTask task = new HttpTask(getActivity(),Constants.COMMITLINK,commitHandler,"CommitBook","post");
            HttpManager.startTask(task);
        }else {
            Toast.makeText(getActivity(),"网络未连接！",Toast.LENGTH_SHORT).show();
        }

    }

    private void initCommitHandler() {
        commitHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(pd != null) {
                    pd.dismiss();
                    pd = null;
                }
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getActivity(),"发布成功！",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"发布失败！",Toast.LENGTH_SHORT).show();
                        String result = (String) msg.obj;
                        Log.d("commitBook",result);
                        result = null;
                        break;
                }
            }
        };
    }

    //显示Dialog让用户选择获取图片方式
    private void chooseCoverWay() {
        CoverDialogFragment cover = new CoverDialogFragment();
        cover.setTargetFragment(this,REQUEST_PIC_WAY);
        cover.show(getFragmentManager(),"pic_way");
    }

    private void reset(){
        etBookName.setText("");
        etPublisher.setText("");
        etAuthor.setText("");
        mainClassify.setSelection(0,false);
        subClassify.setAdapter(null);
        etNumber.setText("");
        howOld.setSelection(0);
        etOPrice.setText("");
        if(etSPrice.getVisibility() == View.VISIBLE) {
            etSPrice.setText("");
        }
        if(etSendCondition.getVisibility() == View.VISIBLE) {
            etSendCondition.setText("");
        }
        etDescription.setText("");
        etRemarks.setText("");
        imgCover.setImageResource(R.drawable.def);
    }

    private boolean canCommit(String coverUrl) {
        if(coverUrl == null) return false;
        CommitBookData commitData = CommitBookData.getInstance();
        CharSequence text = etBookName.getText();
        if(TextUtils.isEmpty(text)) {
            CommitBookData.clear();
            return false;
        }
        commitData.setBookName(text.toString());
        text = etAuthor.getText();
        if(TextUtils.isEmpty(text)) {
            CommitBookData.clear();
            return false;
        }
        commitData.setBookAuthor(text.toString());
        text = etPublisher.getText();
        if(TextUtils.isEmpty(text)) {
            CommitBookData.clear();
            return false;
        }
        commitData.setPublisher(text.toString());
        text = etNumber.getText();
        if(TextUtils.isEmpty(text)) {
            CommitBookData.clear();
            return false;
        }
        commitData.setBookNumber(text.toString());
        text= etOPrice.getText();
        if(TextUtils.isEmpty(text)) {
            CommitBookData.clear();
            return false;
        }
        commitData.setoPrice(text.toString());
        commitData.setMainClassify(mainCf);
        commitData.setSubClassify(subCf);
        commitData.setHowOld(oOrn);
        StringBuilder status = new StringBuilder();
        if(cbSell.isChecked()) {
            status.append("出售");
            if(cbGive.isChecked()) {
                status.append("/");
                status.append("赠送");
            }
        }else {
            status.append("求购");
            if(cbGive.isChecked()) {
                status.append("/");
                status.append("求赠");
            }
        }
        commitData.setStatus(status.toString());
        status =  null;
        if(cbGive.isChecked()) {
            text = etSendCondition.getText();
            if(TextUtils.isEmpty(text)){
                CommitBookData.clear();
                return false ;
            }
            commitData.setSendCondition(text.toString());
        }else {
            commitData.setSendCondition("");
        }
        if(cbPay.isChecked()) {
            text = etSPrice.getText();
            if(TextUtils.isEmpty(text)){
                CommitBookData.clear();
                return false ;
            }
            commitData.setsPrice(text.toString());
        }
        text = etDescription.getText();
        if(!TextUtils.isEmpty(text)){
            commitData.setDescription(text.toString());
        }else {
            commitData.setDescription("");
        }
        text = etRemarks.getText();
        if(!TextUtils.isEmpty(text)){
            commitData.setRemark(text.toString());
        }else {
            commitData.setRemark("");
        }
        commitData.setCoverUrl(coverUrl);
        if(isbn != null) {
            commitData.setIsbn(isbn);
        }else {
            commitData.setIsbn("");
        }
        return true;
    }

    private void showDialog() {
        IsbnDialogFragment isbnDialogFragment = new IsbnDialogFragment();
        isbnDialogFragment.setTargetFragment(this,REQUEST_ISBN);
        isbnDialogFragment.show(getFragmentManager(),"isbn");
    }

    //委派相机应用
    private void dispatchTakenPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //确保程序能处理返回的Intent
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            fileName = System.currentTimeMillis() + ".jpg";      //图片名
            Uri imageUri = Uri.fromFile(new File(Utils.getPicturePath(),fileName));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);   //将照片存放在指定位置
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            picPath = Utils.getPicturePath() + "/" + fileName;
            updateCover();
        }else if(requestCode == REQUEST_ISBN && resultCode == Activity.RESULT_OK) {
            //根据isbn查询图书信息
            isbn = data.getStringExtra(Constants.EXTRA_ISBN);
            getBookInfo();
        }else if(requestCode == ALBUM_OK && resultCode == Activity.RESULT_OK && data != null) {
            Uri seleectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(seleectedImage,filePathColumn,null,null,null) ;
            if(cursor != null && cursor.moveToFirst()) {
                int columIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                picPath = cursor.getString(columIndex);
                cursor.close();
                cursor = null;
                updateCover();
            }
            data = null;
        }else if(requestCode == REQUEST_PIC_WAY && resultCode == Activity.RESULT_OK && data !=null ) {
            getCover(data.getStringExtra(Constants.EXTRA_PIC));
            data = null;
        }
    }

    private void getCover(String stringExtra) {
        if(stringExtra.equals("拍照")){
            dispatchTakenPicture();
        }else {
            chooseFromLocal();
        }
    }

    //从手机图库中选择
    private void chooseFromLocal() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (albumIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_OK);
        }
    }

    private void updateCover() {
        coverDouBanUrl = null;
        //获取照片
        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        imgCover.setImageBitmap(bitmap);
        if(!btnAddCover.getText().toString().equals("更改封面"))
            btnAddCover.setText("更改封面");
    }

    private void getBookInfo() {
        //检查联网状态
        if(ConnectionDetector.isConnectingToInternet(getActivity())) {
            //输入isbn号后利用豆瓣api获取书的信息
            if(isbnHan == null) {
                initIsbnHan();
            }
            String url = Utils.getDouBanUrl(isbn);
            if(pd == null) {
                pd = new ProgressDialog(getActivity());
                pd.setMessage("正在查询图书信息...");
            }
            pd.show();
            try{
                DouBanTask task = new DouBanTask(url,isbnHan);
                HttpManager.startTask(task);
            }catch (Exception e) {
                Log.e("SellFragment","error in getBookInfo",e);
            }
            url = null;
        }else{
            Toast.makeText(getActivity(),"网络未连接",Toast.LENGTH_SHORT).show();
        }
    }

    private void upLoadPicture() {
        File f = new File(picPath);  //要上传的文件
        PutPolicy policy = new PutPolicy("shutuier",System.currentTimeMillis()+3600,1);
        String token = null;
        try {
            token = policy.token(null);
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("SellFragment","token is " + token );
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(f,null,token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        if(jsonObject != null) {
                            Log.d("uploadManager","JSONObject is " + jsonObject);
                            //解析Json获得hash值
                            if(jsonObject.has("hash")){
                                try{
                                    String hash = jsonObject.getString("hash");
                                    Log.d("hash is " ,hash);
                                    if(hash != null) {
                                        if(canCommit(Constants.PICLINK + hash)) {
                                            System.out.println(CommitBookData.getInstance().toString());
                                            commitBook();
                                        }else {
                                            Toast.makeText(getActivity(),"信息不完整！",Toast.LENGTH_SHORT).show();
                                            System.out.println(CommitBookData.getInstance().toString());
                                        }
                                    }
                                }catch (Exception e) {
                                    Log.e("SellFragment","error in get hash",e);
                                }
                            }
                        }else {
                            Toast.makeText(getActivity(),"发布失败！请稍后再试。",Toast.LENGTH_SHORT).show();
                        }
                    }
                },null);
    }

    //设置布局动画
    public void setLayoutAnimation() {
        if(layoutTransition == null) {
            layoutTransition = new LayoutTransition();
        }
        layoutTransition.setAnimator(LayoutTransition.APPEARING,layoutTransition.getAnimator(LayoutTransition.APPEARING));
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.DISAPPEARING));
        tableInput.setLayoutTransition(layoutTransition);
    }
}

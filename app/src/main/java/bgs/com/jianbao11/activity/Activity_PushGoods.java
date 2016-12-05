package bgs.com.jianbao11.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import bgs.com.jianbao11.R;
import bgs.com.jianbao11.utils.ProcessActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 醇色 on 2016/11/25.
 */

public class Activity_PushGoods extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private OkHttpClient ok;
    private String url = "http://192.168.4.188/Goods/app/item/issue.json";
    private MediaType MEDIA_TYPE_PNG = MediaType.parse("img/png");
    private File file;
    private Button publish_btn;//确定发布
    private TextView cancel_tv;//取消按钮
    private ImageView mImg_bargain, mImg_brand;// 支持侃价,全新商品
    private GridView gridView1;                 //网格显示缩略图
    private EditText qq_edtv, phone_edtv, content_edtv, title_edt, price_edtv;
    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int GET_DATA = 2;           //获取处理后图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathImage;                     //选择图片路径
    private Bitmap bmp;                             //导入临时图片
    private Uri imageUri;                            //拍照Uri
    private String pathTakePhoto;              //拍照路径
    private int count = 0;                           //计算上传图片个数 线程调用
    private int flagThread = 0;                    //线程循环标记变量 否则会上个线程没执行完就进行下面的
    private boolean flag = true;
    private boolean flag1 = true;
    //获取图片上传URL路径 文件夹名+时间命名图片
    //private String[] urlPicture;
    //存储Bmp图像
    private ArrayList<HashMap<String, Object>> list;
    //适配器
    private SimpleAdapter simpleAdapter;
    //插入PublishId通过Json解析
    private String publishIdByJson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
         * 防止键盘挡住输入框
         * 不希望遮挡设置activity属性 android:windowSoftInputMode="adjustPan"
         * 希望动态调整高度 android:windowSoftInputMode="adjustResize"
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //获取控件对象
        setContentView(R.layout.activity_push);
        initView();
    }

    private void initView() {
        cancel_tv = (TextView) findViewById(R.id.cancel_tv);
        mImg_bargain = (ImageView) findViewById(R.id.mImg_bargain);
        mImg_brand = (ImageView) findViewById(R.id.mImg_brand);
        publish_btn = (Button) findViewById(R.id.publish_btn);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        qq_edtv = (EditText) findViewById(R.id.qq_edtv);
        phone_edtv = (EditText) findViewById(R.id.phone_edtv);
        content_edtv = (EditText) findViewById(R.id.content_edtv);
        title_edt = (EditText) findViewById(R.id.title_edtv);
        price_edtv = (EditText) findViewById(R.id.price_edtv);

        cancel_tv.setOnClickListener(this);
        mImg_bargain.setOnClickListener(this);
        mImg_brand.setOnClickListener(this);
        publish_btn.setOnClickListener(this);
        gridView1.setOnItemClickListener(this);
         /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); //加号
        list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        map.put("pathImage", "add_pic");
        list.add(map);
        simpleAdapter = new SimpleAdapter(this, list, R.layout.griditem_addpic,
                new String[]{"itemImage"}, new int[]{R.id.imageView1});
        /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView1.setAdapter(simpleAdapter);
    }

    //点击事件
    @Override
    public void onClick(View view) {
        int ID = view.getId();
        //qq_edtv, phone_edtv, content_edtv, title_edt,price_edtv;
        String qq = qq_edtv.getText().toString().trim();
        String phone = phone_edtv.getText().toString().trim();
        String content = content_edtv.getText().toString().trim();
        String title = title_edt.getText().toString().trim();
        String price = price_edtv.getText().toString().trim();
        switch (ID) {
            case R.id.cancel_tv://取消按钮
                this.finish();
                break;
            case R.id.mImg_bargain:
                if (flag) {
                    mImg_bargain.setImageResource(R.drawable.publish_bargin_checked);
                } else {
                    mImg_bargain.setImageResource(R.drawable.publish_bargin_inactive);
                }
                flag = !flag;
                break;
            case R.id.mImg_brand:
                if (flag1) {
                    mImg_brand.setImageResource(R.drawable.publish_type_checked);
                } else {
                    mImg_brand.setImageResource(R.drawable.publish_type_inactive);
                }
                flag1 = !flag1;
                break;
            case R.id.publish_btn:
                /* 上传图片 进度条显示
                * String path = "/storage/emulated/0/DCIM/Camera/lennaFromSystem.jpg";
      		 * upload_SSP_Pic(path,"ranmei");
       		 * Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        		 */

                //判断是否添加图片
//                if (list.size() == 1) {
//                    Toast.makeText(Activity_PushGoods.this, "没有图片需要上传", Toast.LENGTH_SHORT).show();
//                }
                if (title==null||"".equals(title)){
                    Toast.makeText(Activity_PushGoods.this, "没有标题", Toast.LENGTH_SHORT).show();

                }
                if (content==null||"".equals(content)){
                    Toast.makeText(Activity_PushGoods.this, "没有描述内容", Toast.LENGTH_SHORT).show();
                }
                if (price==null||"".equals(price)){
                    Toast.makeText(Activity_PushGoods.this, "没有输入价格", Toast.LENGTH_SHORT).show();
                }
                if (phone==null||"".equals(phone)){
                    Toast.makeText(Activity_PushGoods.this, "没有手机号", Toast.LENGTH_SHORT).show();

                }
                if (title!=null&&!"".equals(title)&&content!=null&&!"".equals(content)
                        &&price!=null&&!"".equals(price)&&phone!=null&&!"".equals(phone)){
                ok = new OkHttpClient();
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                //创建文件参数请求
                for (int i = 1; i < list.size(); i++) {
                    HashMap<String, Object> imgMap = list.get(i);
                    String pathImage = (String) imgMap.get("pathImage");
                    file = new File(pathImage);
                    RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
                    builder.addFormDataPart("photo", file.getName(), fileBody);
                }
                builder.addFormDataPart("title", title);
                builder.addFormDataPart("description",content );
                builder.addFormDataPart("price", price);
                builder.addFormDataPart("mobile", phone);
                builder.addFormDataPart("qq",qq);
                MultipartBody mBody = builder.build();
                Request request = new Request.Builder().url(url).post(mBody).build();
                ok.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("", "请求失败" + e.getLocalizedMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Activity_PushGoods.this, "请求失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("", "请求成功" + response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Activity_PushGoods.this, "请求成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
                }
                break;
        }

    }


    /*
     * 监听GridView点击事件
     * 报错:该函数必须抽象方法 故需要手动导入import android.view.View;
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (list.size() == 6) { //第一张为默认图片
            Toast.makeText(Activity_PushGoods.this, "图片数量4张已满", Toast.LENGTH_SHORT).show();
        } else if (i == 0) { //点击图片位置为+ 0对应0张图片
            AddImageDialog();
        } else {
            DeleteDialog(i);
        }
    }

    //获取图片路径 响应startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if (resultCode == RESULT_OK && requestCode == IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                //向处理活动传递数据
                Intent intent = new Intent(this, ProcessActivity.class); //主活动->处理活动
                intent.putExtra("path", path);
                startActivityForResult(intent, GET_DATA);
            } else {
                Intent intent = new Intent(this, ProcessActivity.class); //主活动->处理活动
                intent.putExtra("path", uri.getPath());
                startActivityForResult(intent, GET_DATA);
            }
        }  //end if 打开图片

        //获取图片
        if (resultCode == RESULT_OK && requestCode == GET_DATA) {
            //获取传递的处理图片在onResume中显示
            pathImage = data.getStringExtra("pathProcess");
        }
        //拍照
        if (resultCode == RESULT_OK && requestCode == TAKE_PHOTO) {
            Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //广播刷新相册
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(imageUri);
            this.sendBroadcast(intentBc);
            //向处理活动传递数据
            Intent intentPut = new Intent(this, ProcessActivity.class); //主活动->处理活动
            intentPut.putExtra("path", pathTakePhoto);
            //startActivity(intent);
            startActivityForResult(intentPut, GET_DATA);
        }
    }


    /*
   * 添加图片 可通过本地添加、拍照添加
   */
    protected void AddImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PushGoods.this);
        //builder.setTitle("添加图片");
        //builder.setIcon(R.drawable.aa);
        builder.setCancelable(false); //不响应back按钮
        builder.setItems(new String[]{"本地相册", "拍照", "取消"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //本地相册
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_OPEN);
                                //通过onResume()刷新数据
                                break;
                            case 1: //手机相机
                                dialog.dismiss();
                                File outputImage = new File(Environment.getExternalStorageDirectory(), "suishoupai_image.jpg");
                                pathTakePhoto = outputImage.toString();
                                try {
                                    if (outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                imageUri = Uri.fromFile(outputImage);
                                Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE"); //拍照
                                intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intentPhoto, TAKE_PHOTO);
                                break;
                            case 2: //取消添加
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                });
        //显示对话框
        builder.create().show();
    }

    /*
       * Dialog对话框提示用户删除操作
       * position为删除图片位置
       */
    protected void DeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PushGoods.this);
        builder.setMessage("确认移除已添加图片吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                list.remove(position);
                simpleAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        //获取传递的处理图片在onResume中显示
        //Intent intent = getIntent();
        //pathImage = intent.getStringExtra("pathProcess");
        //适配器动态显示图片
        if (!TextUtils.isEmpty(pathImage)) {
            Bitmap addbmp = BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            map.put("pathImage", pathImage);
            list.add(map);
            simpleAdapter = new SimpleAdapter(this, list, R.layout.griditem_addpic,
                    new String[]{"itemImage"}, new int[]{R.id.imageView1});
            //接口载入图片
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView1.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }


}

package bgs.com.jianbao11.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bgs.com.jianbao11.R;
import bgs.com.jianbao11.activity.Activity_KeFu;
import bgs.com.jianbao11.activity.Activity_XinXi;
import bgs.com.jianbao11.activity.Activity_YiJian;
import bgs.com.jianbao11.bean.Info_xinxi;
import bgs.com.jianbao11.jianbao.MyAppalication;
import bgs.com.jianbao11.picture.ImageTools;
import bgs.com.jianbao11.utils.ImageLoader;
import bgs.com.jianbao11.utils.SharedUtils;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static bgs.com.jianbao11.R.id.mImg_head;
import static java.lang.String.valueOf;


/**
 * Created by 醇色 on 2016/11/25.
 * 我的
 */

public class MineFragment extends Fragment {
    private String url = "http://192.168.4.188/Goods/app/user/upload.json";

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int SCALE = 5;//照片缩小比例
    private Map map = new HashMap();
    private SharedUtils utils;
    @InjectView(mImg_head)
    ImageView mImgHead;
    @InjectView(R.id.mTv_login_no)
    TextView mTvLoginNo;
    @InjectView(R.id.mImg_selling)
    ImageView mImgSelling;
    @InjectView(R.id.mTv_selling)
    TextView mTvSelling;
    @InjectView(R.id.mLin_selling)
    LinearLayout mLinSelling;
    @InjectView(R.id.mImg_sold)
    ImageView mImgSold;
    @InjectView(R.id.mTv_sold)
    TextView mTvSold;
    @InjectView(R.id.mLin_sold)
    LinearLayout mLinSold;
    @InjectView(R.id.mImg_bought)
    ImageView mImgBought;
    @InjectView(R.id.mTv_bought)
    TextView mTvBought;
    @InjectView(R.id.mLin_bought)
    LinearLayout mLinBought;
    @InjectView(R.id.mImg_buying)
    ImageView mImgBuying;
    @InjectView(R.id.mTv_buying)
    TextView mTvBuying;
    @InjectView(R.id.mLin_buying)
    LinearLayout mLinBuying;
    @InjectView(R.id.mImg_favorite)
    ImageView mImgFavorite;
    @InjectView(R.id.mTv_favorite)
    TextView mTvFavorite;
    @InjectView(R.id.mLin_favorite)
    LinearLayout mLinFavorite;
    @InjectView(R.id.mLin_info)
    LinearLayout mLinInfo;
    @InjectView(R.id.mLin_renzheng)
    LinearLayout mLinRenzheng;
    @InjectView(R.id.mLin_yijian)
    LinearLayout mLinYijian;
    @InjectView(R.id.mLin_kefu)
    LinearLayout mLinKefu;
    private View v;
    private String xinxi_url = "http://192.168.4.188/Goods/app/user/info.json";
    private Info_xinxi info = new Info_xinxi();
    private ImageLoader loader;
    private File file;
    private String path;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = View.inflate(getActivity(), R.layout.fragment_mine, null);
        utils = ((MyAppalication) getActivity().getApplicationContext()).utils;
        loader = ((MyAppalication) getActivity().getApplicationContext()).imageLoader;
        ButterKnife.inject(this, v);
        initMap();
        post_headImg(xinxi_url,map);

        return v;
    }

    private void initMap() {
        String token = utils.getShared("token", getActivity());
        map.put("token", token);
        if (token != null) {
            mTvLoginNo.setText("已登录");
            mTvLoginNo.setTextColor(getResources().getColor(R.color.primary));
        }
    }

    private void clearImg() {
        mImgSelling.setImageResource(R.mipmap.userinfo_selling);
        mImgSold.setImageResource(R.mipmap.userinfo_sold);
        mImgBought.setImageResource(R.mipmap.userinfo_bought);
        mImgBuying.setImageResource(R.mipmap.userinfo_buying);
        mImgFavorite.setImageResource(R.mipmap.userinfo_favorite);
    }

    private void clearText() {
        mTvSelling.setTextColor(getResources().getColor(R.color.textcolor));
        mTvSold.setTextColor(getResources().getColor(R.color.textcolor));
        mTvBought.setTextColor(getResources().getColor(R.color.textcolor));
        mTvBuying.setTextColor(getResources().getColor(R.color.textcolor));
        mTvFavorite.setTextColor(getResources().getColor(R.color.textcolor));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                info = (Info_xinxi) msg.obj;
                String imgurl = info.getPhoto();
                if (imgurl.equals("http://192.168.4.188/Goods/uploads/")) {
                    mImgHead.setImageResource(R.mipmap.ic_launcher);
                } else {
                    loader.Load(imgurl, mImgHead, getActivity());
                }
            }
        }
    };
    protected void post_headImg(final String url, final Map<String, Object> map) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue()!=null&&!"".equals(entry.getValue()))
                {  requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));}
            }
        }
        MultipartBody build = requestBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(build)
                .build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Log.e("111111", response.message() + " , body " + str);
                     try {
                                JSONObject object = new JSONObject(str);
                                JSONObject data = object.getJSONObject("data");
                                if (data.has("photo")) {
                                    info.setPhoto("http://192.168.4.188/Goods/uploads/" + data.getString("photo"));
                                    Message msg = handler.obtainMessage();
                                    msg.obj = info;
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                } else {
                    Log.e("22222" ,response.message() + " error : body " + response.body().string());
                }
            }
        });

    }
    protected void post_photo(final String url, final Map<String, Object> map, File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() != null && !"".equals(entry.getValue())) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }
        }
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("photo", file.getName(), body);
            MultipartBody build = requestBody.build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(build)
                    .build();
            // readTimeout("请求超时时间" , 时间单位);
            client.newBuilder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() == 200) {
                                String str = response.body().string();
                                try {
                                    JSONObject object = new JSONObject(str);
                                    String jsonCode = object.getString("status");
                                    if (jsonCode.equals("200")) {
                                        Log.e("111111", response.message() + " , body " + str);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                Log.e("22222" ,response.message() + " error : body " + response.body().string());
                            }


                        }
                    });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({mImg_head, R.id.mLin_selling, R.id.mLin_sold, R.id.mLin_bought, R.id.mLin_buying, R.id.mLin_favorite, R.id.mLin_info, R.id.mLin_renzheng, R.id.mLin_yijian, R.id.mLin_kefu})
    public void onClick(View view) {
        clearText();
        clearImg();
        switch (view.getId()) {
            case R.id.mLin_selling:
                mImgSelling.setImageResource(R.mipmap.userinfo_selling_active);
                mTvSelling.setTextColor(Color.RED);
                break;
            case R.id.mLin_sold:
                mImgSold.setImageResource(R.mipmap.userinfo_sold_active);
                mTvSold.setTextColor(Color.RED);
                break;
            case R.id.mLin_bought:
                mImgBought.setImageResource(R.mipmap.userinfo_bought_active);
                mTvBought.setTextColor(Color.RED);
                break;
            case R.id.mLin_buying:
                mImgBuying.setImageResource(R.mipmap.userinfo_buying_active);
                mTvBuying.setTextColor(Color.RED);
                break;
            case R.id.mLin_favorite:
                mImgFavorite.setImageResource(R.mipmap.userinfo_favorite_active);
                mTvFavorite.setTextColor(Color.RED);
                break;
            case R.id.mLin_kefu:
                Intent intent = new Intent(getActivity(), Activity_KeFu.class);
                startActivity(intent);
                break;
            case R.id.mLin_yijian:
                Intent intent1 = new Intent(getActivity(), Activity_YiJian.class);
                startActivity(intent1);
                break;
            case mImg_head:
                showPicturePicker(getActivity());
                break;
            case R.id.mLin_info:
                Intent intent3 = new Intent(getActivity(), Activity_XinXi.class);
                startActivity(intent3);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    mImgHead.setImageBitmap(newBitmap);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), valueOf(System.currentTimeMillis()));
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getActivity().getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();
                            mImgHead.setImageBitmap(smallBitmap);
                            path = "";
                            int sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
                            if (sdkVersion > 19) {
                                path = originalUri.getPath();//5.0直接返回的是图片路径，5.0以下是一个和数据库有关的索引值
                            } else {
                                String[] proj = {MediaStore.Images.Media.DATA};
                                Cursor cursor = getActivity().getContentResolver().query(originalUri,
                                        proj, null, null, null);
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                cursor.moveToFirst();
                                path = cursor.getString(column_index);
                            }

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
            file = new File(path);
            String token = (String) map.get("token");
            Log.e("token",token);
            post_photo(url, map, file);
        }
    }

    public void showPicturePicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }
}

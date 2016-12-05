package bgs.com.jianbao11.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import bgs.com.jianbao11.picture.ImageTools;
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

import static bgs.com.jianbao11.R.id.mImg_idcard;
import static java.lang.String.valueOf;


/**
 * Created by 毛毛 on 2016/11/30.
 */

public class SignupActivity extends Activity {
    private static final String TAG = "SignupActivity";
    private ProgressDialog progressDialog;
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int SCALE = 5;//照片缩小比例
    @InjectView(R.id.mTil_QQ)
    TextInputLayout mTilQQ;
    @InjectView(R.id.mTil_wechat)
    TextInputLayout mTilWechat;
    @InjectView(R.id.mTil_email)
    TextInputLayout mTilEmail;
    private boolean isMore;
    @InjectView(R.id.input_code)
    EditText inputcode;
    @InjectView(R.id.input_name)
    EditText _nameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    @InjectView(R.id.input_phone1)
    EditText inputPhone;
    @InjectView(R.id.mRbtn_man)
    RadioButton mRbtnMan;
    @InjectView(R.id.mRbtn_woman)
    RadioButton mRbtnWoman;
    @InjectView(mImg_idcard)
    ImageView mImgIdcard;
    @InjectView(R.id.input_more)
    TextView inputMore;
    @InjectView(R.id.input_QQ)
    EditText inputQQ;
    @InjectView(R.id.input_wechat)
    EditText inputWechat;

    private String url = "http://192.168.4.188/Goods/app/common/register.json";
    private File file;
    private Map map = new HashMap<String, String>();
    private boolean valid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ButterKnife.inject(this);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                post_file(url, map, file);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void initData() {
        validate();
        if (!valid){
            Toast.makeText(this, "请检查您输入的信息", Toast.LENGTH_SHORT).show();
            return;
        }
        map.put("mobile", inputPhone.getText().toString());
        map.put("qq", inputQQ.getText().toString());
        map.put("wechat", inputWechat.getText().toString());
        map.put("name", _nameText.getText().toString());
        map.put("code", inputcode.getText().toString());
        map.put("password", _passwordText.getText().toString());
        if (mRbtnMan.isChecked()) {
            map.put("gender", "男");
        }
        if (mRbtnWoman.isChecked()) {
            map.put("gender", "女");
        }
    }

    protected void post_file(final String url, final Map<String, Object> map, File file) {

        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("card", file.getName(), body);
        }
        if (map != null&&!map.isEmpty()) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() != null && !"".equals(entry.getValue())) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
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
                    if (response.code() == 200) {
                        String str = response.body().string();
                        try {
                            JSONObject jo= new JSONObject(str);
                            String statusCode = (String) jo.get("status");
                            if(!"200".equals(statusCode)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, "邀请码无效", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        signup();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                _signupButton.setEnabled(false);

                                                progressDialog = new ProgressDialog(SignupActivity.this,
                                                        R.style.AppTheme_Dark_Dialog);
                                                progressDialog.setIndeterminate(true);
                                                progressDialog.setMessage("账号创建中...");
                                                progressDialog.show();
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignupActivity.this, "1212131212123", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "注册失败", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);

    }

    public boolean validate() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                valid = true;
                String code = inputcode.getText().toString().trim();
                String name = _nameText.getText().toString().trim();
                String password = _passwordText.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();


                if (code==null||"".equals(code)) {
                    inputcode.setError("输入的邀请码有误");
                    valid = false;
                } else {
                    inputcode.setError(null);
                }
                if (!isMobileNO(phone)) {
                    inputPhone.setError("输入的手机号有误");
                    valid = false;
                } else {
                    inputPhone.setError(null);
                }
                if (name.isEmpty() || name.length() > 6) {
                    _nameText.setError("名字长度不能大于三个字");
                    valid = false;
                } else {
                    _nameText.setError(null);
                }

                if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
                    _passwordText.setError("密码为4 - 20个字母数字字符");
                    valid = false;
                } else {
                    _passwordText.setError(null);
                }
            }
        });


        return valid;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
		/*
		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		*/
        String telRegex = "[1][358]\\d{9}";
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    @OnClick({mImg_idcard, R.id.input_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case mImg_idcard:
                showPicturePicker(this);
                break;
            case R.id.input_more:
                if (!isMore) {
                    mTilEmail.setVisibility(View.VISIBLE);
                    mTilQQ.setVisibility(View.VISIBLE);
                    mTilWechat.setVisibility(View.VISIBLE);
                    isMore = !isMore;
                } else {
                    mTilEmail.setVisibility(View.GONE);
                    mTilQQ.setVisibility(View.GONE);
                    mTilWechat.setVisibility(View.GONE);
                    isMore = !isMore;
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    mImgIdcard.setImageBitmap(newBitmap);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), valueOf(System.currentTimeMillis()));
                    break;
                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
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
                            mImgIdcard.setImageBitmap(smallBitmap);
                            String[] proj = {MediaStore.Images.Media.DATA};
                            //以下是拿到选择图片的路径
                            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                            //按我个人理解 这个是获得用户选择的图片的索引值
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            //将光标移至开头 ，这个很重要，不小心很容易引起越界
                            cursor.moveToFirst();
                            //最后根据索引值获取图片路径
                            String path = cursor.getString(column_index);
                            file = new File(path);
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

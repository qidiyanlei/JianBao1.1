package bgs.com.jianbao11.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bgs.com.jianbao11.R;
import bgs.com.jianbao11.bean.Info_xinxi;
import bgs.com.jianbao11.jianbao.MyAppalication;
import bgs.com.jianbao11.utils.SharedUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;


/**
 * Created by 毛毛 on 2016/11/29.
 */

public class Activity_XinXi extends Activity implements View.OnClickListener {
    private TextView mTv_name, mTv_phone, mTv_gender, mTv_QQ, mTv_wechat, mTv_email, mTv_exit, mTv_lasttime, mTv_isCode, mTv_code;
    private Map map = new HashMap();
    private SharedUtils utils;
    private String url = "http://192.168.4.188/Goods/app/user/info.json";
    private Info_xinxi info = new Info_xinxi();
    private String codeUrl = "http://192.168.4.188/Goods/app/user/invite.json";
    private Map map1 = new HashMap();
    private String token;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_xinxi);
        utils = ((MyAppalication) Activity_XinXi.this.getApplicationContext()).utils;
        initMap();
        initView();
    }

    private void initMap() {
        token = utils.getShared("token", Activity_XinXi.this);
        map.put("token", token);
    }

    private void initView() {
        mTv_exit = (TextView) findViewById(R.id.mTv_exit);
        mTv_exit.setOnClickListener(this);

        mTv_lasttime = (TextView) findViewById(R.id.mTv_lasttime);
        mTv_isCode = (TextView) findViewById(R.id.mTv_isCode);
        mTv_isCode.setOnClickListener(this);

        mTv_code = (TextView) findViewById(R.id.mTv_xinxi_code);

        mTv_gender = (TextView) findViewById(R.id.mTv_xinxi_gender);
        mTv_phone = (TextView) findViewById(R.id.mTv_xinxi_phone);
        mTv_QQ = (TextView) findViewById(R.id.mTv_xinxi_QQ);
        mTv_wechat = (TextView) findViewById(R.id.mTv_xinxi_wechat);
        mTv_email = (TextView) findViewById(R.id.mTv_xinxi_email);
        mTv_name = (TextView) findViewById(R.id.mTv_xinxi_name);


        post_file(url, map, null);
        post_file(codeUrl, map, null);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Info_xinxi info = (Info_xinxi) msg.obj;
                mTv_name.setText(info.getName());
                mTv_gender.setText(info.getGender());
                mTv_phone.setText(info.getMobile());
                mTv_email.setText(info.getEmile());
                mTv_wechat.setText(info.getWechat());
                mTv_QQ.setText(info.getQq());
                mTv_lasttime.setText(info.getTime());
            }
        }
    };

    protected void post_code(final String url, final Map<String, Object> map) {
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
                        String state = data.getString("state");
                        if (state.equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Activity_XinXi.this, "邀请码尚未被使用", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Activity_XinXi.this, "邀请码已经被使用", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("22222", response.message() + " error : body " + response.body().string());
                }
            }
        });

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
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() != null && !"".equals(entry.getValue())) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
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
                final String str = response.body().string();
                Log.e("111111", " , body " + str);
                if (url.equals("http://192.168.4.188/Goods/app/user/info.json")) {
                    try {
                        JSONObject object = new JSONObject(str);
                        JSONObject data = object.getJSONObject("data");
                        info.setMobile(data.getString("mobile"));
                        info.setName(data.getString("name"));
                        info.setGender(data.getString("gender"));
                        info.setTime(data.getString("last_time"));
                        if (data.has("qq")) {
                            info.setQq(data.getString("qq"));
                        }
                        if (data.has("wechat")) {
                            info.setWechat(data.getString("wechat"));
                        }
                        if (data.has("emile")) {
                            info.setEmile(data.getString("emile"));
                        }
                        Message msg = handler.obtainMessage();
                        msg.obj = info;
                        msg.what = 1;
                        handler.sendMessage(msg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (url.equals("http://192.168.4.188/Goods/app/user/invite.json")) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject object = null;
                                try {
                                    object = new JSONObject(str);
                                    JSONObject data = object.getJSONObject("data");
                                    mTv_code.setText(data.getString("code"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });


                }

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTv_exit:
                token = null;
                utils.saveShared("token", token, this);
                startActivity(new Intent(Activity_XinXi.this, LoginActivity.class));
                finish();
                break;
            case R.id.mTv_isCode:
                map1.put("token", token);
                post_code(codeUrl,map1);
                break;
        }

    }
}

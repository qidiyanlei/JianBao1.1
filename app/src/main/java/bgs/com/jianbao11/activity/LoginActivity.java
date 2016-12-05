package bgs.com.jianbao11.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bgs.com.jianbao11.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * Created by 毛毛 on 2016/11/30.
 */

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_phone)
    EditText _phoneText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;
    private String url = "http://192.168.4.188/Goods/app/common/login.json";
    private Map map = new HashMap();
    private boolean valid;
    private boolean valid1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initData();

                post_file(url, map);

            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void initData() {
        validate();
        if (!validate()) {
            Toast.makeText(this, "账号或密码不能为空。", Toast.LENGTH_SHORT).show();
            return;
        }
        map.put("username", _phoneText.getText().toString());
        map.put("password", _passwordText.getText().toString());


    }

    protected void post_file(final String url, final Map<String, Object> map) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (map != null && !map.isEmpty()) {
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
                            JSONObject josnStr = new JSONObject(str);
                            String statusCode = (String) josnStr.get("status");
                            if (! "200".equals(statusCode) ) {
                                Log.e("111111", response.message() + " , body " + str);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "账号密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                login();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else {
                        Log.e("22222", response.message() + " error : body " + response.body().string());
                       
                    }
                }
            });
        }

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            _loginButton.setEnabled(false);

            final ProgressDialog progressDialog =  new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("正在登陆，请稍等");
            progressDialog.show();


            // TODO: Implement your own authentication logic here.

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
    });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        // disable going back to the MainActivity
//        moveTaskToBack(true);
//    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登陆失败", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        valid1 = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                String phone = _phoneText.getText().toString();
                String password = _passwordText.getText().toString();
                if (phone.isEmpty() || phone.length() != 11) {
                    _phoneText.setError("请输入正确的手机号");
                    valid1 = false;
                } else {
                    _phoneText.setError(null);
                }

                if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
                    _passwordText.setError("4 - 20个字母数字字符");
                    valid1 = false;
                } else {
                    _passwordText.setError(null);
                }
            }

        });
        return valid1;
    }
}

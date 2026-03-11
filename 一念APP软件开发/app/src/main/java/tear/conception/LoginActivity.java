package tear.conception;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;
import tear.conception.util.MD5Util;
import tear.conception.util.SharedPreferencesUtil;

public class LoginActivity extends Activity {

    private EditText etQqNumber;
    private EditText etNickname;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    private SharedPreferencesUtil prefsUtil;
    private static final String DEVELOPER_QQ = "523341786";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefsUtil = SharedPreferencesUtil.getInstance(this);
        
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etQqNumber = findViewById(R.id.et_qq_number);
        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        etQqNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkDeveloperAccount();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void checkDeveloperAccount() {
        String qqNumber = etQqNumber.getText().toString().trim();
        if (DEVELOPER_QQ.equals(qqNumber)) {
            etPassword.setVisibility(View.VISIBLE);
            etNickname.setVisibility(View.GONE);
        } else {
            etPassword.setVisibility(View.GONE);
            etNickname.setVisibility(View.VISIBLE);
            etPassword.setText("");
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String qqNumber = etQqNumber.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (qqNumber.isEmpty()) {
            Toast.makeText(this, "请输入QQ号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (qqNumber.length() < 5) {
            Toast.makeText(this, "请输入正确的QQ号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (DEVELOPER_QQ.equals(qqNumber)) {
            if (password.isEmpty()) {
                Toast.makeText(this, "开发者账号请输入密码", Toast.LENGTH_SHORT).show();
                return;
            }
            String encryptedPassword = MD5Util.encrypt(password);
            if (!MD5Util.verifyDeveloperPassword(password)) {
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        showLoading(true);

        String passwordMd5 = DEVELOPER_QQ.equals(qqNumber) ? MD5Util.encrypt(password) : null;
        BlogApiService.login(qqNumber, nickname, passwordMd5, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.getJSONObject("data");
                        long userId = data.getLong("id");
                        String qqNum = data.getString("qqNumber");
                        String nick = data.optString("nickname", "一念用户");
                        String avatar = data.optString("avatar", "");
                        String signature = data.optString("signature", "");

                        prefsUtil.putLong("user_id", userId);
                        prefsUtil.putString("qq_number", qqNum);
                        prefsUtil.putString("nickname", nick);
                        prefsUtil.putString("avatar", avatar);
                        prefsUtil.putString("signature", signature);

                        tear.conception.service.SigninReminderService.start(LoginActivity.this);

                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    } else {
                        String message = response.optString("message", "登录失败");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "登录中..." : "登录 / 注册");
    }

    private void startMainActivity() {
        setResult(RESULT_OK);
        finish();
    }
}

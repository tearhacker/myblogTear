package tear.conception;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;
import tear.conception.util.SharedPreferencesUtil;

public class SplashActivity extends Activity {
    
    private static final int SPLASH_DURATION = 1000;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;
    private int currentVersionCode = 1;
    private String currentVersionName = "1.0.0";
    private boolean hasCheckedVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_splash);
        
        getCurrentVersion();
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNotificationPermission();
            }
        }, SPLASH_DURATION);
    }

    private void getCurrentVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionCode = packageInfo.versionCode;
            currentVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    new String[]{"android.permission.POST_NOTIFICATIONS"},
                    REQUEST_NOTIFICATION_PERMISSION
                );
            } else {
                checkVersionAndNavigate();
            }
        } else {
            checkVersionAndNavigate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "通知权限已开启，将为您推送签到提醒", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未开启通知权限，将无法收到签到提醒", Toast.LENGTH_LONG).show();
            }
            checkVersionAndNavigate();
        }
    }

    private void checkVersionAndNavigate() {
        if (hasCheckedVersion) return;
        hasCheckedVersion = true;
        
        BlogApiService.checkVersion(currentVersionCode, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            final String versionName = data.optString("versionName", "");
                            final String updateContent = data.optString("updateContent", "");
                            final String downloadUrl = data.optString("downloadUrl", "");
                            final int forceUpdate = data.optInt("forceUpdate", 0);
                            
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showUpdateDialog(versionName, updateContent, downloadUrl, forceUpdate == 1);
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                navigateToMain();
            }

            @Override
            public void onError(String error) {
                navigateToMain();
            }
        });
    }

    private void showUpdateDialog(String versionName, String updateContent, final String downloadUrl, final boolean forceUpdate) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_update_version);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(!forceUpdate);
        
        TextView tvVersionName = dialog.findViewById(R.id.tv_version_name);
        TextView tvUpdateContent = dialog.findViewById(R.id.tv_update_content);
        Button btnUpdate = dialog.findViewById(R.id.btn_update);
        Button btnLater = dialog.findViewById(R.id.btn_later);
        
        tvVersionName.setText("v" + versionName);
        tvUpdateContent.setText(updateContent);
        
        if (forceUpdate) {
            btnLater.setVisibility(View.GONE);
        } else {
            btnLater.setVisibility(View.VISIBLE);
        }
        
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDownloadUrl(downloadUrl);
                dialog.dismiss();
                if (!downloadUrl.isEmpty()) {
                    finish();
                }
            }
        });
        
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                navigateToMain();
            }
        });
        
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!forceUpdate) {
                    navigateToMain();
                }
            }
        });
        
        dialog.show();
    }

    private void openDownloadUrl(String downloadUrl) {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
    }
}

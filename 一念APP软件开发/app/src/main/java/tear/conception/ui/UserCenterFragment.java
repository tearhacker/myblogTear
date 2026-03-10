package tear.conception.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import tear.conception.AiChatActivity;
import tear.conception.LoginActivity;
import tear.conception.R;
import tear.conception.module.BlogApiService;
import tear.conception.ui.view.WaterDropProgressView;
import tear.conception.ui.view.StarParticleView;
import tear.conception.util.DateUtil;
import tear.conception.util.SharedPreferencesUtil;

public class UserCenterFragment extends Fragment {
    
    private WaterDropProgressView circleProgress;
    private TextView tvCheckinDays;
    private TextView tvSigninStatus;
    private TextView tvMilestone;
    private StarParticleView starParticleView;
    private LinearLayout llSupportDeveloper;
    private LinearLayout llAiAssistant;
    private LinearLayout llSettings;
    private LinearLayout llAbout;
    private LinearLayout llLogout;
    private LinearLayout llUserInfo;
    private TextView tvNickname;
    private TextView tvQqNumber;
    private ImageView ivAvatar;

    private SharedPreferencesUtil prefsUtil;
    
    private static final String KEY_LAST_SIGNIN_TIME = "last_signin_time";
    private static final String KEY_SIGNIN_DAYS = "signin_days";
    
    private boolean isSignedInToday = false;
    private int currentDays = 0;
    private long userId = 0;
    private String qqNumber = "";
    private String nickname = "";

    public static UserCenterFragment newInstance() {
        return new UserCenterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        prefsUtil = SharedPreferencesUtil.getInstance(getActivity());
        userId = prefsUtil.getLong("user_id", 0);
        qqNumber = prefsUtil.getString("qq_number", "");
        nickname = prefsUtil.getString("nickname", "");
        
        initViews(view);
        setupClickListeners();
        
        if (userId > 0) {
            showUserInfo();
            loadSigninFromServer();
        } else {
            showGuestView();
        }
    }

    private void showUserInfo() {
        if (llUserInfo != null) {
            llUserInfo.setVisibility(View.VISIBLE);
        }
        if (tvNickname != null && nickname != null && !nickname.isEmpty()) {
            tvNickname.setText(nickname);
        } else if (tvNickname != null) {
            tvNickname.setText("用户" + userId);
        }
        if (tvQqNumber != null) {
            tvQqNumber.setText("QQ: " + qqNumber);
        }
        if (ivAvatar != null && qqNumber != null && !qqNumber.isEmpty()) {
            String avatarUrl = "https://q1.qlogo.cn/g?b=qq&nk=" + qqNumber + "&s=100";
            new AvatarLoadTask(ivAvatar).execute(avatarUrl);
        }
    }

    private void hideUserInfo() {
        if (llUserInfo != null) {
            llUserInfo.setVisibility(View.GONE);
        }
    }

    private void showGuestView() {
        hideUserInfo();
        tvCheckinDays.setVisibility(View.GONE);
        circleProgress.setDays(0, 30);
        circleProgress.setSignedIn(false);
        tvSigninStatus.setVisibility(View.VISIBLE);
        tvSigninStatus.setText("点击登录以同步数据");
        tvMilestone.setVisibility(View.GONE);
    }

    private void initViews(View view) {
        circleProgress = view.findViewById(R.id.circle_progress);
        tvCheckinDays = view.findViewById(R.id.tv_checkin_days);
        tvSigninStatus = view.findViewById(R.id.tv_signin_status);
        tvMilestone = view.findViewById(R.id.tv_milestone);
        starParticleView = view.findViewById(R.id.star_particle_view);
        llSupportDeveloper = view.findViewById(R.id.ll_support_developer);
        llAiAssistant = view.findViewById(R.id.ll_ai_assistant);
        llSettings = view.findViewById(R.id.ll_settings);
        llAbout = view.findViewById(R.id.ll_about);
        llLogout = view.findViewById(R.id.ll_logout);
        llUserInfo = view.findViewById(R.id.ll_user_info);
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvQqNumber = view.findViewById(R.id.tv_qq_number);
        ivAvatar = view.findViewById(R.id.iv_avatar);
    }

    private void setupClickListeners() {
        circleProgress.setOnSigninClickListener(new WaterDropProgressView.OnSigninClickListener() {
            @Override
            public void onSigninClick() {
                onSigninClicked();
            }
        });

        llSupportDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportDeveloperClicked();
            }
        });

        llAiAssistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAiAssistantClicked();
            }
        });

        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingsClicked();
            }
        });

        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAboutClicked();
            }
        });

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClicked();
            }
        });
    }

    private void loadSigninFromServer() {
        BlogApiService.getSigninStats(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.getJSONObject("data");
                        currentDays = data.optInt("continuousDays", 0);
                        int totalDays = data.optInt("totalDays", 0);
                        
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvCheckinDays.setVisibility(View.VISIBLE);
                                tvCheckinDays.setText("已连续签到 " + currentDays + " 天");
                                circleProgress.setDays(currentDays, 30);
                                checkTodaySignin();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSigninStatus();
                    }
                });
            }
        });
    }

    private void checkTodaySignin() {
        BlogApiService.getTodaySignin(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        final JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            isSignedInToday = true;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    circleProgress.setSignedIn(true);
                                    tvSigninStatus.setVisibility(View.VISIBLE);
                                    String message = data.optString("signinMessage", "今日已签到");
                                    tvSigninStatus.setText(message);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void updateSigninStatus() {
        currentDays = prefsUtil.getInt(KEY_SIGNIN_DAYS, 0);
        long lastSigninTime = prefsUtil.getLong(KEY_LAST_SIGNIN_TIME, 0);
        
        tvCheckinDays.setVisibility(View.VISIBLE);
        tvCheckinDays.setText("已连续签到 " + currentDays + " 天");
        circleProgress.setDays(currentDays, 30);
        
        isSignedInToday = DateUtil.isToday(lastSigninTime);
        
        if (isSignedInToday) {
            circleProgress.setSignedIn(true);
            tvSigninStatus.setVisibility(View.VISIBLE);
            tvSigninStatus.setText("明日再来吧~");
        } else {
            circleProgress.setSignedIn(false);
            tvSigninStatus.setVisibility(View.GONE);
        }
        
        updateMilestone();
    }

    private void updateMilestone() {
        String milestoneText = null;
        
        if (currentDays >= 100) {
            milestoneText = "🎉 坚持签到100天！太棒了！";
        } else if (currentDays >= 30) {
            milestoneText = "🔥 坚持签到30天！继续加油！";
        } else if (currentDays >= 7) {
            milestoneText = "✨ 坚持签到7天！很棒！";
        } else if (currentDays > 0) {
            int nextMilestone = 7;
            if (currentDays < 7) nextMilestone = 7;
            else if (currentDays < 30) nextMilestone = 30;
            else nextMilestone = 100;
            milestoneText = "距离下一个里程碑还有 " + (nextMilestone - currentDays) + " 天";
        }
        
        if (milestoneText != null) {
            tvMilestone.setVisibility(View.VISIBLE);
            tvMilestone.setText(milestoneText);
        } else {
            tvMilestone.setVisibility(View.GONE);
        }
    }

    private void onSigninClicked() {
        if (userId > 0 && !qqNumber.isEmpty()) {
            doServerSignin();
        } else {
            goToLogin();
        }
    }

    private static final int REQUEST_LOGIN = 1001;
    
    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            userId = prefsUtil.getLong("user_id", 0);
            qqNumber = prefsUtil.getString("qq_number", "");
            nickname = prefsUtil.getString("nickname", "");
            if (userId > 0) {
                showUserInfo();
                loadSigninFromServer();
                updateSigninButton();
            }
        }
    }

    private void updateSigninButton() {
        circleProgress.setSignedIn(false);
        tvSigninStatus.setVisibility(View.GONE);
    }

    private void doServerSignin() {
        BlogApiService.doSignin(userId, qqNumber, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.getJSONObject("data");
                        final int newDays = data.getInt("continuousDays");
                        final String message = data.optString("signinMessage", "签到成功");
                        final boolean isMilestone = data.optBoolean("isMilestone", false);
                        
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playSigninAnimation(newDays, message, isMilestone);
                            }
                        });
                    } else {
                        final String msg = response.optString("message", "签到失败");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doLocalSignin();
                    }
                });
            }
        });
    }

    private void doLocalSignin() {
        long lastSigninTime = prefsUtil.getLong(KEY_LAST_SIGNIN_TIME, 0);
        int signinDays = prefsUtil.getInt(KEY_SIGNIN_DAYS, 0);

        if (DateUtil.isToday(lastSigninTime)) {
            Toast.makeText(getActivity(), "今日已签到", Toast.LENGTH_SHORT).show();
            return;
        }

        if (DateUtil.isSameDay(lastSigninTime, System.currentTimeMillis() - 24 * 60 * 60 * 1000)) {
            signinDays++;
        } else {
            signinDays = 1;
        }

        prefsUtil.putLong(KEY_LAST_SIGNIN_TIME, System.currentTimeMillis());
        prefsUtil.putInt(KEY_SIGNIN_DAYS, signinDays);
        
        playSigninAnimation(signinDays, "签到成功！", false);
    }

    private void playSigninAnimation(final int newDays, final String message, final boolean isMilestone) {
        starParticleView.startAnimation(20);
        
        animateDaysCounter(currentDays, newDays);
        
        circleProgress.animateProgress((float) newDays / 30);
        circleProgress.playSigninAnimation();
        circleProgress.setSignedIn(true);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSignedInToday = true;
                currentDays = newDays;
                tvSigninStatus.setVisibility(View.VISIBLE);
                tvSigninStatus.setText(message);
                updateMilestone();
                
                if (isMilestone) {
                    showMilestoneCelebration(newDays);
                }
            }
        }, 800);
    }

    private void animateDaysCounter(int from, int to) {
        tvCheckinDays.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(1000);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvCheckinDays.setText("已连续签到 " + value + " 天");
            }
        });
        animator.start();
    }

    private void showMilestoneCelebration(int days) {
        String msg = "";
        if (days == 7) {
            msg = "🎉 恭喜坚持签到7天！";
        } else if (days == 30) {
            msg = "🔥 太棒了！坚持签到30天！";
        } else if (days == 100) {
            msg = "🏆 传奇！坚持签到100天！";
        }
        
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        starParticleView.startAnimation(40);
    }

    private void onSupportDeveloperClicked() {
        showSupportDialog();
    }
    
    private void showSupportDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_support_developer);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        
        ImageView ivWechatPay = dialog.findViewById(R.id.iv_wechat_pay);
        ImageView ivAlipay = dialog.findViewById(R.id.iv_alipay);
        TextView btnClose = dialog.findViewById(R.id.btn_close);
        
        ivWechatPay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImageToGallery((ImageView) v, "wechat_pay_qrcode");
                return true;
            }
        });
        
        ivAlipay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImageToGallery((ImageView) v, "alipay_qrcode");
                return true;
            }
        });
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    private void saveImageToGallery(ImageView imageView, String fileName) {
        try {
            Drawable drawable = imageView.getDrawable();
            if (drawable == null) {
                Toast.makeText(getActivity(), "图片加载失败", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null) {
                Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
                return;
            }
            
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + "_" + System.currentTimeMillis() + ".png");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/一念APP");
            }
            
            android.net.Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri == null) {
                Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
            
            OutputStream outputStream = getActivity().getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
                return;
            }
            
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            
            Toast.makeText(getActivity(), "已保存到相册", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onAiAssistantClicked() {
        Intent intent = new Intent(getActivity(), AiChatActivity.class);
        startActivity(intent);
    }

    private void onSettingsClicked() {
        Toast.makeText(getActivity(), "设置", Toast.LENGTH_SHORT).show();
    }

    private void onAboutClicked() {
        showAboutDialog();
    }

    private void showAboutDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_app);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);
        
        final TextView tvVersion = dialog.findViewById(R.id.tv_version);
        final TextView tvUpdateContent = dialog.findViewById(R.id.tv_update_content);
        TextView btnClose = dialog.findViewById(R.id.btn_close);
        TextView tvGitee = dialog.findViewById(R.id.tv_gitee);
        
        try {
            String versionName = getActivity().getPackageManager()
                .getPackageInfo(getActivity().getPackageName(), 0).versionName;
            tvVersion.setText("版本：" + versionName);
        } catch (Exception e) {
            tvVersion.setText("版本：1.0");
        }
        
        BlogApiService.getLatestVersion(new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            final String updateContent = data.optString("updateContent", "暂无更新内容");
                            final String versionName = data.optString("versionName", "");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!versionName.isEmpty()) {
                                        tvVersion.setText("版本：" + versionName);
                                    }
                                    tvUpdateContent.setText(updateContent);
                                }
                            });
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvUpdateContent.setText("暂无更新内容");
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvUpdateContent.setText("暂无更新内容");
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvUpdateContent.setText("暂无更新内容");
                    }
                });
            }
        });
        
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        tvGitee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, 
                        android.net.Uri.parse("https://gitee.com/thoughtful123/one-thought-software"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "无法打开链接", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        dialog.show();
    }

    private void onLogoutClicked() {
        if (userId <= 0) {
            Toast.makeText(getActivity(), "您还未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new android.app.AlertDialog.Builder(getActivity())
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    doLogout();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void doLogout() {
        tear.conception.service.SigninReminderService.stop(getActivity());
        
        prefsUtil.putLong("user_id", 0);
        prefsUtil.putString("qq_number", "");
        prefsUtil.putString("nickname", "");
        prefsUtil.putString("avatar", "");
        prefsUtil.putString("signature", "");
        
        userId = 0;
        qqNumber = "";
        nickname = "";
        
        hideUserInfo();
        showGuestView();
        
        Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        starParticleView.stopAnimation();
    }

    private static class AvatarLoadTask extends AsyncTask<String, Void, Bitmap> {
        private final java.lang.ref.WeakReference<ImageView> imageViewRef;

        AvatarLoadTask(ImageView imageView) {
            this.imageViewRef = new java.lang.ref.WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL imageUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();
                InputStream input = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                if (bitmap != null) {
                    bitmap = getCircularBitmap(bitmap);
                }
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = imageViewRef.get();
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        private Bitmap getCircularBitmap(Bitmap bitmap) {
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(output);
            android.graphics.Paint paint = new android.graphics.Paint();
            android.graphics.Rect rect = new android.graphics.Rect(0, 0, size, size);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
            paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }
}

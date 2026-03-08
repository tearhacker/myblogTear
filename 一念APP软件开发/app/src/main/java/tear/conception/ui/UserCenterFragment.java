package tear.conception.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import tear.conception.AiChatActivity;
import tear.conception.LoginActivity;
import tear.conception.R;
import tear.conception.module.BlogApiService;
import tear.conception.ui.view.CircleProgressView;
import tear.conception.ui.view.RippleView;
import tear.conception.ui.view.StarParticleView;
import tear.conception.util.DateUtil;
import tear.conception.util.SharedPreferencesUtil;

public class UserCenterFragment extends Fragment {
    
    private CircleProgressView circleProgress;
    private TextView tvCheckinDays;
    private FrameLayout btnSigninContainer;
    private TextView tvSigninText;
    private TextView tvSigninStatus;
    private TextView tvMilestone;
    private RippleView rippleView;
    private StarParticleView starParticleView;
    private LinearLayout llMyPosts;
    private LinearLayout llMyComments;
    private LinearLayout llMyFavorites;
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
    
    private ObjectAnimator breathAnimator;
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
        startBreathAnimation();
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
    }

    private void hideUserInfo() {
        if (llUserInfo != null) {
            llUserInfo.setVisibility(View.GONE);
        }
    }

    private void showGuestView() {
        hideUserInfo();
        tvCheckinDays.setText("0");
        circleProgress.setDays(0, 30);
        btnSigninContainer.setEnabled(true);
        tvSigninText.setText("登录");
        tvSigninStatus.setVisibility(View.VISIBLE);
        tvSigninStatus.setText("点击登录以同步数据");
        tvMilestone.setVisibility(View.GONE);
    }

    private void initViews(View view) {
        circleProgress = view.findViewById(R.id.circle_progress);
        tvCheckinDays = view.findViewById(R.id.tv_checkin_days);
        btnSigninContainer = view.findViewById(R.id.btn_signin_container);
        tvSigninText = view.findViewById(R.id.tv_signin_text);
        tvSigninStatus = view.findViewById(R.id.tv_signin_status);
        tvMilestone = view.findViewById(R.id.tv_milestone);
        rippleView = view.findViewById(R.id.ripple_view);
        starParticleView = view.findViewById(R.id.star_particle_view);
        llMyPosts = view.findViewById(R.id.ll_my_posts);
        llMyComments = view.findViewById(R.id.ll_my_comments);
        llMyFavorites = view.findViewById(R.id.ll_my_favorites);
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
        btnSigninContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSigninClicked();
            }
        });

        llMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyPostsClicked();
            }
        });

        llMyComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyCommentsClicked();
            }
        });

        llMyFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyFavoritesClicked();
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
                                tvCheckinDays.setText(String.valueOf(currentDays));
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
                                    btnSigninContainer.setEnabled(false);
                                    tvSigninText.setText("已签到");
                                    tvSigninStatus.setVisibility(View.VISIBLE);
                                    String message = data.optString("signinMessage", "今日已签到");
                                    tvSigninStatus.setText(message);
                                    stopBreathAnimation();
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
        
        tvCheckinDays.setText(String.valueOf(currentDays));
        circleProgress.setDays(currentDays, 30);
        
        isSignedInToday = DateUtil.isToday(lastSigninTime);
        
        if (isSignedInToday) {
            btnSigninContainer.setEnabled(false);
            tvSigninText.setText("已签到");
            tvSigninStatus.setVisibility(View.VISIBLE);
            tvSigninStatus.setText("明日再来吧~");
            stopBreathAnimation();
        } else {
            btnSigninContainer.setEnabled(true);
            tvSigninText.setText("签到");
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

    private void startBreathAnimation() {
        if (isSignedInToday) return;
        
        breathAnimator = ObjectAnimator.ofFloat(btnSigninContainer, "alpha", 1f, 0.7f, 1f);
        breathAnimator.setDuration(1500);
        breathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathAnimator.start();
    }

    private void stopBreathAnimation() {
        if (breathAnimator != null) {
            breathAnimator.cancel();
            btnSigninContainer.setAlpha(1f);
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
        btnSigninContainer.setEnabled(true);
        tvSigninText.setText("签到");
        tvSigninStatus.setVisibility(View.GONE);
        startBreathAnimation();
    }

    private void doServerSignin() {
        btnSigninContainer.setEnabled(false);
        
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
                                btnSigninContainer.setEnabled(true);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSigninContainer.setEnabled(true);
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
        
        stopBreathAnimation();
        playSigninAnimation(signinDays, "签到成功！", false);
    }

    private void playSigninAnimation(final int newDays, final String message, final boolean isMilestone) {
        btnSigninContainer.setEnabled(false);
        
        rippleView.startRipple();
        starParticleView.startAnimation(20);
        
        animateDaysCounter(currentDays, newDays);
        
        circleProgress.animateProgress((float) newDays / 30);
        
        btnSigninContainer.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(150)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    btnSigninContainer.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                }
            })
            .start();
        
        tvSigninText.setText("已签到");
        
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
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(1000);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvCheckinDays.setText(String.valueOf(value));
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

    private void onMyPostsClicked() {
        Toast.makeText(getActivity(), "我的文章", Toast.LENGTH_SHORT).show();
    }

    private void onMyCommentsClicked() {
        Toast.makeText(getActivity(), "我的评论", Toast.LENGTH_SHORT).show();
    }

    private void onMyFavoritesClicked() {
        Toast.makeText(getActivity(), "我的收藏", Toast.LENGTH_SHORT).show();
    }

    private void onAiAssistantClicked() {
        Intent intent = new Intent(getActivity(), AiChatActivity.class);
        startActivity(intent);
    }

    private void onSettingsClicked() {
        Toast.makeText(getActivity(), "设置", Toast.LENGTH_SHORT).show();
    }

    private void onAboutClicked() {
        Toast.makeText(getActivity(), "关于一念", Toast.LENGTH_SHORT).show();
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
        stopBreathAnimation();
        starParticleView.stopAnimation();
        rippleView.stopRipple();
    }
}

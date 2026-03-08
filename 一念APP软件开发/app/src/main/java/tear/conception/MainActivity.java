package tear.conception;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tear.conception.ui.EducationFragment;
import tear.conception.ui.HomeFragment;
import tear.conception.ui.UserCenterFragment;
import tear.conception.ui.VideoFragment;
import tear.conception.util.DateUtil;
import tear.conception.util.SharedPreferencesUtil;

public class MainActivity extends Activity {

    private FrameLayout fragmentContainer;
    private LinearLayout bottomNavigation;
    private FrameLayout islandContainer;
    private LinearLayout dynamicIsland;
    private TextView islandText;

    private LinearLayout navHome;
    private LinearLayout navEducation;
    private LinearLayout navVideo;
    private LinearLayout navUser;
    private ImageView navHomeIcon;
    private ImageView navEducationIcon;
    private ImageView navVideoIcon;
    private ImageView navUserIcon;
    private TextView navHomeText;
    private TextView navEducationText;
    private TextView navVideoText;
    private TextView navUserText;

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private EducationFragment educationFragment;
    private VideoFragment videoFragment;
    private UserCenterFragment userCenterFragment;
    private Fragment currentFragment;

    private SharedPreferencesUtil prefsUtil;
    private int currentNavIndex = 0;
    
    private int secretTapCount = 0;
    private long lastSecretTapTime = 0;
    private static final int SECRET_TAP_THRESHOLD = 2;
    private static final long SECRET_TAP_INTERVAL = 800;

    private static final String KEY_LAST_SIGNIN_TIME = "last_signin_time";
    private static final String KEY_SIGNIN_DAYS = "signin_days";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        fragmentManager = getFragmentManager();

        initViews();
        setupBottomNavigation();
        showDefaultFragment();
        checkAndShowSignIn();
    }

    private void initViews() {
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        islandContainer = findViewById(R.id.island_container);
        dynamicIsland = findViewById(R.id.dynamic_island);
        islandText = findViewById(R.id.island_text);

        navHome = findViewById(R.id.nav_home);
        navEducation = findViewById(R.id.nav_education);
        navVideo = findViewById(R.id.nav_video);
        navUser = findViewById(R.id.nav_user);

        navHomeIcon = findViewById(R.id.nav_home_icon);
        navEducationIcon = findViewById(R.id.nav_education_icon);
        navVideoIcon = findViewById(R.id.nav_video_icon);
        navUserIcon = findViewById(R.id.nav_user_icon);

        navHomeText = findViewById(R.id.nav_home_text);
        navEducationText = findViewById(R.id.nav_education_text);
        navVideoText = findViewById(R.id.nav_video_text);
        navUserText = findViewById(R.id.nav_user_text);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSecretTap();
                switchTab(0);
            }
        });

        navEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        navVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });

        navUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(3);
            }
        });
    }

    private void checkSecretTap() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastSecretTapTime < SECRET_TAP_INTERVAL) {
            secretTapCount++;
            if (secretTapCount >= SECRET_TAP_THRESHOLD) {
                openLoveSignin();
                secretTapCount = 0;
            }
        } else {
            secretTapCount = 1;
        }
        
        lastSecretTapTime = currentTime;
    }

    private void openLoveSignin() {
        Intent intent = new Intent(this, LoveSigninActivity.class);
        startActivity(intent);
    }

    private void switchTab(int index) {
        if (currentNavIndex == index) {
            return;
        }

        updateNavIcons(index);
        
        switch (index) {
            case 0:
                switchFragment(getHomeFragment());
                break;
            case 1:
                switchFragment(getEducationFragment());
                break;
            case 2:
                switchFragment(getVideoFragment());
                break;
            case 3:
                switchFragment(getUserCenterFragment());
                break;
        }
        
        currentNavIndex = index;
    }

    private void updateNavIcons(int selectedIndex) {
        navHomeIcon.setImageResource(selectedIndex == 0 ? R.drawable.ic_home_selected : R.drawable.ic_home_normal);
        navEducationIcon.setImageResource(selectedIndex == 1 ? R.drawable.ic_education_selected : R.drawable.ic_education_normal);
        navVideoIcon.setImageResource(selectedIndex == 2 ? R.drawable.ic_video_selected : R.drawable.ic_video_normal);
        navUserIcon.setImageResource(selectedIndex == 3 ? R.drawable.ic_user_selected : R.drawable.ic_user_normal);

        navHomeText.setTextColor(getResources().getColor(selectedIndex == 0 ? R.color.primary : R.color.text_hint));
        navEducationText.setTextColor(getResources().getColor(selectedIndex == 1 ? R.color.primary : R.color.text_hint));
        navVideoText.setTextColor(getResources().getColor(selectedIndex == 2 ? R.color.primary : R.color.text_hint));
        navUserText.setTextColor(getResources().getColor(selectedIndex == 3 ? R.color.primary : R.color.text_hint));
    }

    private HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance();
        }
        return homeFragment;
    }

    private EducationFragment getEducationFragment() {
        if (educationFragment == null) {
            educationFragment = EducationFragment.newInstance();
        }
        return educationFragment;
    }

    private VideoFragment getVideoFragment() {
        if (videoFragment == null) {
            videoFragment = VideoFragment.newInstance();
        }
        return videoFragment;
    }

    private UserCenterFragment getUserCenterFragment() {
        if (userCenterFragment == null) {
            userCenterFragment = UserCenterFragment.newInstance();
        }
        return userCenterFragment;
    }

    private void showDefaultFragment() {
        currentFragment = getHomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, currentFragment);
        transaction.commit();
    }

    private void switchFragment(Fragment targetFragment) {
        if (currentFragment == targetFragment) {
            return;
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.hide(currentFragment).add(R.id.fragment_container, targetFragment);
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        transaction.commit();
        currentFragment = targetFragment;
    }

    private void checkAndShowSignIn() {
        long lastSigninTime = prefsUtil.getLong(KEY_LAST_SIGNIN_TIME, 0);
        if (!DateUtil.isToday(lastSigninTime)) {
            showDynamicIsland();
        }
    }

    private void showDynamicIsland() {
        islandContainer.setVisibility(View.VISIBLE);
        
        dynamicIsland.postDelayed(new Runnable() {
            @Override
            public void run() {
                expandIsland();
            }
        }, 500);
    }

    private void expandIsland() {
        int startWidth = dpToPx(100);
        int endWidth = dpToPx(300);
        int startHeight = dpToPx(36);
        int endHeight = dpToPx(60);

        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, endWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dynamicIsland.getLayoutParams();
                params.width = value;
                dynamicIsland.setLayoutParams(params);
            }
        });

        ValueAnimator heightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dynamicIsland.getLayoutParams();
                params.height = value;
                dynamicIsland.setLayoutParams(params);
            }
        });

        widthAnimator.setDuration(300);
        heightAnimator.setDuration(300);
        widthAnimator.start();
        heightAnimator.start();

        int signinDays = prefsUtil.getInt(KEY_SIGNIN_DAYS, 0) + 1;
        islandText.setText("连续 " + signinDays + " 天");

        dynamicIsland.postDelayed(new Runnable() {
            @Override
            public void run() {
                shrinkIsland();
            }
        }, 2000);
    }

    private void shrinkIsland() {
        int startWidth = dpToPx(300);
        int endWidth = dpToPx(100);
        int startHeight = dpToPx(60);
        int endHeight = dpToPx(36);

        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, endWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dynamicIsland.getLayoutParams();
                params.width = value;
                dynamicIsland.setLayoutParams(params);
            }
        });

        ValueAnimator heightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = dynamicIsland.getLayoutParams();
                params.height = value;
                dynamicIsland.setLayoutParams(params);
            }
        });

        widthAnimator.setDuration(300);
        heightAnimator.setDuration(300);
        widthAnimator.start();
        heightAnimator.start();

        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                islandContainer.setVisibility(View.GONE);
                performSignIn();
            }
        });
    }

    private void performSignIn() {
        long lastSigninTime = prefsUtil.getLong(KEY_LAST_SIGNIN_TIME, 0);
        int signinDays = prefsUtil.getInt(KEY_SIGNIN_DAYS, 0);

        if (DateUtil.isToday(lastSigninTime)) {
            return;
        }

        if (DateUtil.isSameDay(lastSigninTime, System.currentTimeMillis() - 24 * 60 * 60 * 1000)) {
            signinDays++;
        } else {
            signinDays = 1;
        }

        prefsUtil.putLong(KEY_LAST_SIGNIN_TIME, System.currentTimeMillis());
        prefsUtil.putInt(KEY_SIGNIN_DAYS, signinDays);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}

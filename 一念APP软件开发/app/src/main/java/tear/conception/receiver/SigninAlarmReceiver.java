package tear.conception.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;
import tear.conception.util.NotificationUtil;
import tear.conception.util.SharedPreferencesUtil;

/**
 * @Description: 签到提醒广播接收器
 * @Author: 泪心
 */
public class SigninAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "SigninAlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "收到签到提醒广播");
        
        final SharedPreferencesUtil prefsUtil = SharedPreferencesUtil.getInstance(context);
        final long userId = prefsUtil.getLong("user_id", 0);
        
        if (userId <= 0) {
            return;
        }
        
        BlogApiService.getTodaySignin(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        JSONObject data = response.optJSONObject("data");
                        if (data == null) {
                            NotificationUtil.showSigninReminder(context);
                            Log.d(TAG, "今日未签到，发送通知提醒");
                        } else {
                            Log.d(TAG, "今日已签到，不发送提醒");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "检查签到状态失败: " + error);
            }
        });
    }
}

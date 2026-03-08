package tear.conception.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import tear.conception.service.SigninReminderService;
import tear.conception.util.SharedPreferencesUtil;

/**
 * @Description: 开机启动广播接收器
 * @Author: 泪心
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferencesUtil prefsUtil = SharedPreferencesUtil.getInstance(context);
            long userId = prefsUtil.getLong("user_id", 0);
            
            if (userId > 0) {
                SigninReminderService.start(context);
            }
        }
    }
}

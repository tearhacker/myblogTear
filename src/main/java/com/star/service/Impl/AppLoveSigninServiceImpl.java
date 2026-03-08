package com.star.service.Impl;

import com.star.dao.AppLoveSigninDao;
import com.star.dao.AppUserStatsDao;
import com.star.entity.AppLoveSignin;
import com.star.entity.AppUserStats;
import com.star.service.AppLoveSigninService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

/**
 * @Description: 恋爱签到服务实现类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Service
public class AppLoveSigninServiceImpl implements AppLoveSigninService {

    @Autowired
    private AppLoveSigninDao appLoveSigninDao;

    @Autowired
    private AppUserStatsDao appUserStatsDao;

    private static final String[] LOVE_MESSAGES = {
        "暗恋是一场漫长的告白，每一天都在心里说爱你。",
        "欧阳颖，这个名字，是我青春最美的秘密。",
        "初中时的悸动，至今仍在心间。愿时光温柔以待。",
        "有些话藏在心里，却用签到记录每一天的思念。",
        "如果有一天能相见，我想告诉你：我一直都在。",
        "每一个签到，都是对你无声的思念。",
        "时光荏苒，唯有这份心意，始终如一。"
    };

    @Override
    @Transactional
    public AppLoveSignin signin(Long userId, String qqNumber, String targetName) {
        AppLoveSignin todaySignin = appLoveSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        if (todaySignin != null) {
            int totalDays = appLoveSigninDao.countByUserId(userId);
            todaySignin.setTotalDays(totalDays);
            return todaySignin;
        }

        AppLoveSignin lastSignin = appLoveSigninDao.findLatestByUserId(userId);
        int continuousDays = 1;
        
        if (lastSignin != null) {
            LocalDate lastDate = lastSignin.getSigninDate().toLocalDate();
            LocalDate today = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(lastDate, today);
            
            if (daysBetween == 1) {
                continuousDays = lastSignin.getContinuousDays() + 1;
            }
        }

        String loveMessage = generateLoveMessage(continuousDays, false);

        int loveLevel = Math.min(10, continuousDays / 7 + 1);

        if (targetName == null || targetName.trim().isEmpty()) {
            targetName = "欧阳颖";
        }

        AppLoveSignin signin = new AppLoveSignin();
        signin.setUserId(userId);
        signin.setQqNumber(qqNumber);
        signin.setTargetName(targetName);
        signin.setSigninDate(Date.valueOf(LocalDate.now()));
        signin.setSigninTime(new Timestamp(System.currentTimeMillis()));
        signin.setContinuousDays(continuousDays);
        signin.setLoveMessage(loveMessage);
        signin.setLoveLevel(loveLevel);
        signin.setAiGenerated(0);
        
        int totalDays = appLoveSigninDao.countByUserId(userId) + 1;
        signin.setTotalDays(totalDays);
        
        appLoveSigninDao.insert(signin);

        appUserStatsDao.incrementLoveSigninDays(userId);

        return signin;
    }

    @Override
    public AppLoveSignin getTodaySignin(Long userId) {
        AppLoveSignin todaySignin = appLoveSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        if (todaySignin != null) {
            int totalDays = appLoveSigninDao.countByUserId(userId);
            todaySignin.setTotalDays(totalDays);
        }
        return todaySignin;
    }

    @Override
    public List<AppLoveSignin> getLoveSigninHistory(Long userId) {
        return appLoveSigninDao.findByUserId(userId);
    }

    @Override
    public boolean hasSignedInToday(Long userId) {
        AppLoveSignin todaySignin = appLoveSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        return todaySignin != null;
    }

    @Override
    public String generateLoveMessage(int continuousDays, boolean useAI) {
        if (useAI) {
            return "AI生成的恋爱寄语（待对接大模型API）";
        }
        
        Random random = new Random();
        return LOVE_MESSAGES[random.nextInt(LOVE_MESSAGES.length)];
    }
}

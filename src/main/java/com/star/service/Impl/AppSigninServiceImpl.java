package com.star.service.Impl;

import com.star.dao.AppSigninDao;
import com.star.dao.AppUserStatsDao;
import com.star.entity.AppSignin;
import com.star.entity.AppUserStats;
import com.star.service.AppSigninService;
import com.star.service.AiChatService;
import com.star.vo.AppSigninVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class AppSigninServiceImpl implements AppSigninService {

    @Autowired
    private AppSigninDao appSigninDao;

    @Autowired
    private AppUserStatsDao appUserStatsDao;

    @Autowired
    private AiChatService aiChatService;

    private static final String[] DEFAULT_MESSAGES = {
        "一念放下，万般自在。今日的你，已比昨日更从容。",
        "心若向阳，无惧悲伤。愿你今日温暖如初。",
        "放下执念，回归本真。你正在找回最好的自己。",
        "每一次签到，都是与自己的约定。坚持，就是力量。",
        "人生路漫漫，愿你落子无悔，所愿皆所得。",
        "不再沉沦苦海，今日的你，已踏上归途。",
        "从泪心到本心，你正在书写自己的故事。"
    };

    @Override
    @Transactional
    public AppSigninVO signin(Long userId, String qqNumber) {
        AppSigninVO vo = new AppSigninVO();
        
        AppSignin todaySignin = appSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        if (todaySignin != null) {
            vo.setSuccess(false);
            vo.setAlreadySigned(true);
            vo.setMessage("今日已签到");
            vo.setContinuousDays(todaySignin.getContinuousDays());
            vo.setSigninMessage(todaySignin.getSigninMessage());
            return vo;
        }

        AppSignin lastSignin = appSigninDao.findLatestByUserId(userId);
        int continuousDays = 1;
        
        if (lastSignin != null) {
            LocalDate lastDate = lastSignin.getSigninDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            long daysBetween = ChronoUnit.DAYS.between(lastDate, LocalDate.now());
            
            if (daysBetween == 1) {
                continuousDays = lastSignin.getContinuousDays() + 1;
            }
        }

        int totalDays = appSigninDao.countByUserId(userId) + 1;

        String signinMessage = generateSigninMessage(continuousDays, totalDays, true);

        AppSignin signin = new AppSignin();
        signin.setUserId(userId);
        signin.setQqNumber(qqNumber);
        signin.setSigninDate(Date.valueOf(LocalDate.now()));
        signin.setSigninTime(new java.util.Date());
        signin.setContinuousDays(continuousDays);
        signin.setTotalDays(totalDays);
        signin.setSigninMessage(signinMessage);
        signin.setAiGenerated(1);
        appSigninDao.insert(signin);

        appUserStatsDao.incrementSigninDays(userId);

        vo.setSuccess(true);
        vo.setAlreadySigned(false);
        vo.setMessage("签到成功");
        vo.setContinuousDays(continuousDays);
        vo.setTotalDays(signin.getTotalDays());
        vo.setSigninMessage(signinMessage);
        vo.setIsMilestone(isMilestone(continuousDays));

        return vo;
    }

    @Override
    public AppSigninVO getTodaySignin(Long userId) {
        AppSignin todaySignin = appSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        
        if (todaySignin == null) {
            return null;
        }

        AppSigninVO vo = new AppSigninVO();
        vo.setSuccess(true);
        vo.setAlreadySigned(true);
        vo.setContinuousDays(todaySignin.getContinuousDays());
        vo.setTotalDays(todaySignin.getTotalDays());
        vo.setSigninMessage(todaySignin.getSigninMessage());
        
        return vo;
    }

    @Override
    public AppSigninVO getSigninStats(Long userId) {
        AppUserStats stats = appUserStatsDao.findByUserId(userId);
        AppSigninVO vo = new AppSigninVO();
        
        if (stats != null) {
            vo.setContinuousDays(stats.getContinuousSigninDays());
            vo.setTotalDays(stats.getTotalSigninDays());
            vo.setMaxContinuousDays(stats.getMaxContinuousDays());
        }
        
        return vo;
    }

    @Override
    public List<AppSignin> getSigninHistory(Long userId) {
        return appSigninDao.findByUserId(userId);
    }

    @Override
    public boolean hasSignedInToday(Long userId) {
        AppSignin todaySignin = appSigninDao.findByUserIdAndDate(userId, Date.valueOf(LocalDate.now()));
        return todaySignin != null;
    }

    @Override
    public String generateSigninMessage(int continuousDays, boolean useAI) {
        if (useAI) {
            return "AI生成的签到寄语（待对接大模型API）";
        }
        
        Random random = new Random();
        return DEFAULT_MESSAGES[random.nextInt(DEFAULT_MESSAGES.length)];
    }

    public String generateSigninMessage(int continuousDays, int totalDays, boolean useAI) {
        if (useAI) {
            try {
                String aiMessage = aiChatService.generateDailySigninMessage(continuousDays, totalDays);
                if (aiMessage != null && !aiMessage.isEmpty() && !aiMessage.startsWith("AI服务")) {
                    return aiMessage;
                }
            } catch (Exception e) {
            }
        }
        
        Random random = new Random();
        return DEFAULT_MESSAGES[random.nextInt(DEFAULT_MESSAGES.length)];
    }

    private boolean isMilestone(int days) {
        return days == 7 || days == 30 || days == 100 || days == 365;
    }
}

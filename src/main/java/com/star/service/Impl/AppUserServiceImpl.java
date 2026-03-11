package com.star.service.Impl;

import com.star.dao.AppUserDao;
import com.star.dao.AppUserStatsDao;
import com.star.entity.AppUser;
import com.star.entity.AppUserStats;
import com.star.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @Description: APP用户服务实现类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private static final String DEVELOPER_QQ = "523341786";
    private static final String DEVELOPER_PASSWORD_MD5 = "5797094e601ff1a8567320f304fb6642";

    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private AppUserStatsDao appUserStatsDao;

    private boolean verifyDeveloperPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return DEVELOPER_PASSWORD_MD5.equals(password);
    }

    @Override
    @Transactional
    public AppUser loginOrRegister(String qqNumber, String nickname, String password) {
        if (DEVELOPER_QQ.equals(qqNumber)) {
            if (!verifyDeveloperPassword(password)) {
                throw new RuntimeException("密码错误");
            }
        }
        AppUser user = appUserDao.findByQqNumber(qqNumber);
        
        if (user == null) {
            user = new AppUser();
            user.setQqNumber(qqNumber);
            user.setNickname(nickname != null ? nickname : "一念用户" + qqNumber.substring(qqNumber.length() - 4));
            user.setStatus(1);
            appUserDao.insert(user);
            
            AppUserStats stats = new AppUserStats();
            stats.setUserId(user.getId());
            stats.setQqNumber(qqNumber);
            stats.setTotalSigninDays(0);
            stats.setContinuousSigninDays(0);
            stats.setMaxContinuousDays(0);
            stats.setLoveSigninDays(0);
            stats.setLoveContinuousDays(0);
            appUserStatsDao.insert(stats);
        } else {
            appUserDao.updateLastLoginTime(user.getId());
        }
        
        return user;
    }

    @Override
    public AppUser getUserById(Long id) {
        return appUserDao.findById(id);
    }

    @Override
    public AppUser getUserByQqNumber(String qqNumber) {
        return appUserDao.findByQqNumber(qqNumber);
    }

    @Override
    public boolean updateNickname(Long id, String nickname) {
        return appUserDao.updateNickname(id, nickname) > 0;
    }

    @Override
    public boolean updateAvatar(Long id, String avatar) {
        return appUserDao.updateAvatar(id, avatar) > 0;
    }

    @Override
    public boolean updateSignature(Long id, String signature) {
        return appUserDao.updateSignature(id, signature) > 0;
    }

    @Override
    public AppUser updateUserInfo(AppUser appUser) {
        appUserDao.update(appUser);
        return appUserDao.findById(appUser.getId());
    }
}

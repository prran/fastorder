package com.kindlesstory.www.module;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserLog
{
    private static UserLog instance;
    final Logger logger;
    
    static {
        UserLog.instance = null;
    }
    
    private UserLog() {
        this.logger = LogManager.getLogger(UserLog.class);
    }
    
    public static UserLog getInstance() {
        if (UserLog.instance != null) {
            return UserLog.instance;
        }
        return UserLog.instance = new UserLog();
    }
    
    public void log(final String massage, final Level... level) {
        try {
            this.logger.log(level[0], massage);
        }
        catch (NullPointerException e) {
            this.logger.log(Level.DEBUG, massage);
        }
    }
}
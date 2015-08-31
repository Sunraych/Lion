package com.wonderful.lion.uitl;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class LogFile {

    public String logName;
    public String userId;
    public String logTime;
    public int isUpload = 0;
    public String logSize;
    public String createTime;
    public String logPath;
    public String logAppName;

    public String getLogAppName() {
        return logAppName;
    }

    public void setLogAppName(String logAppName) {
        this.logAppName = logAppName;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public String getLogSize() {
        return logSize;
    }

    public void setLogSize(String logSize) {
        this.logSize = logSize;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

}

package com.wonderful.lion.uitl;

import com.wonderful.lion.activity.DetailActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {

	private int PID = -1;
	private int PSS_MEM;
	private String PACKAGENAME;
	private String TRAFFIC_WIFI;
	private String TRAFFIC_3G;
	private int ShareDirty;
	private int PrivateDirty;
	private int FLAG = 0;// 0加载中，1正常，2未运行
	private boolean LOG_FLAG = true;
	private boolean FLOW_FLAG = true;
	private int Voltage;
	private boolean Charging;
	private String processCpuRatio = "0.00%";
	private String totalCpuRatio = "0.00%";
	private DetailActivity detailActivity = null;
	private String phoneModel;
	private String ReleaseVersion;
	private int LOGnum = 0;
	private String APPname;
	private String IMEI;
	private boolean unDoneLog = false;
	private HashMap<String, Integer> record;
	private boolean Offline = true;
	private ArrayList<HashMap<String, Object>> allProcess;

	private boolean serviceRunning = false;

	public boolean isServiceRunning() {
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}

	public ArrayList<HashMap<String, Object>> getAllProcess() {
		return allProcess;
	}

	public void setAllProcess(ArrayList<HashMap<String, Object>> allProcess) {
		this.allProcess = allProcess;
	}

	public boolean isOffline() {
		return Offline;
	}

	public void setOffline(boolean offline) {
		Offline = offline;
	}

	public void addRecord(String key) {
		if (record == null) {
			record = new HashMap<String, Integer>();
		}

		if (record.get(key) == null) {
			record.put(key, 0);
		} else {
			int temp = record.get(key);
			record.put(key, temp + 1);
		}
	}

	public HashMap<String, Integer> getRecord() {
		return record;
	}

	public boolean isUnDoneLog() {
		return unDoneLog;
	}

	public void setUnDoneLog(boolean unDoneLog) {
		this.unDoneLog = unDoneLog;
	}

	public String transSize(long originalSize) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		if (originalSize / 1024 == 0) {
			return originalSize + "KB";
		} else if (originalSize / 1048576 == 0) {
			return numberFormat.format((float) originalSize / 1024) + "MB";
		} else {
			return numberFormat.format((float) originalSize / 1048576) + "GB";
		}
	}

	public String transSpeed(long originalSize) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		if (originalSize / 1024 == 0) {
			return originalSize + "KB/S";
		} else if (originalSize / 1048576 == 0) {
			return numberFormat.format((float) originalSize / 1024) + "MB/S";
		} else {
			return numberFormat.format((float) originalSize / 1048576) + "GB/S";
		}
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public String getAPPname() {
		return APPname;
	}

	public void setAPPname(String aPPname) {
		APPname = aPPname;
	}

	public int getLOGnum() {
		return LOGnum;
	}

	public void setLOGnum(int lOGnum) {
		LOGnum = lOGnum;
	}

	private static Util util = null;

	public String getPhoneModel() {
		return phoneModel;
	}

	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}

	public String getReleaseVersion() {
		return ReleaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		ReleaseVersion = releaseVersion;
	}

	public static Util getUtil() {
		if (util == null) {
			util = new Util();
		}
		return util;
	}

	public DetailActivity getDetailActivity() {
		return detailActivity;
	}

	public void setDetailActivity(DetailActivity detailActivity) {
		this.detailActivity = detailActivity;
	}

	public int getPID() {

		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public String getPACKAGENAME() {
		return PACKAGENAME;
	}

	public void setPACKAGENAME(String pACKAGENAME) {
		PACKAGENAME = pACKAGENAME;
	}

	public int getFLAG() {
		return FLAG;
	}

	public void setFLAG(int fLAG) {
		FLAG = fLAG;
	}

	public boolean isLOG_FLAG() {
		return LOG_FLAG;
	}

	public void setLOG_FLAG(boolean lOG_FLAG) {
		LOG_FLAG = lOG_FLAG;
	}

	public boolean isFLOW_FLAG() {
		return FLOW_FLAG;
	}

	public void setFLOW_FLAG(boolean fLOW_FLAG) {
		FLOW_FLAG = fLOW_FLAG;
	}

	public int getVoltage() {
		return Voltage;
	}

	public void setVoltage(int voltage) {
		Voltage = voltage;
	}

	public boolean isCharging() {
		return Charging;
	}

	public void setCharging(boolean charging) {
		Charging = charging;
	}

	public String getProcessCpuRatio() {
		return processCpuRatio;
	}

	public void setProcessCpuRatio(String processCpuRatio) {
		this.processCpuRatio = processCpuRatio;
	}

	public String getTotalCpuRatio() {
		return totalCpuRatio;
	}

	public void setTotalCpuRatio(String totalCpuRatio) {
		this.totalCpuRatio = totalCpuRatio;
	}

	public int getPSS_MEM() {
		return PSS_MEM;
	}

	public void setPSS_MEM(int pSS_MEM) {
		PSS_MEM = pSS_MEM;
	}

	public String getTRAFFIC_WIFI() {
		return TRAFFIC_WIFI;
	}

	public void setTRAFFIC_WIFI(String tRAFFIC_WIFI) {
		TRAFFIC_WIFI = tRAFFIC_WIFI;
	}

	public String getTRAFFIC_3G() {
		return TRAFFIC_3G;
	}

	public void setTRAFFIC_3G(String tRAFFIC_3G) {
		TRAFFIC_3G = tRAFFIC_3G;
	}

	public int getShareDirty() {
		return ShareDirty;
	}

	public void setShareDirty(int shareDirty) {
		ShareDirty = shareDirty;
	}

	public int getPrivateDirty() {
		return PrivateDirty;
	}

	public void setPrivateDirty(int privateDirty) {
		PrivateDirty = privateDirty;
	}
}

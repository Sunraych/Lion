package com.wonderful.lion.service;

import com.wonderful.lion.uitl.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

/**
 * Created by Sun Ruichuan on 2015/8/31.
 */
public class GetCpu {

    private long processCpu1 = 0;
    private long idleCpu1 = 0;
    private long totalCpu1 = 0;
    private int pid = -1;
    private long processCpu2 = 0;
    private long idleCpu2 = 0;
    private long totalCpu2 = 0;
    private String processCpuRatio;
    private String totalCpuRatio;
    private DecimalFormat fomart;
    private Util util = Util.getUtil();

    public GetCpu() {
        fomart = new DecimalFormat();
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(2);
    }

    public void readCpuStat() {
        String processPid = Integer.toString(pid);
        String cpuStatPath = "/proc/" + processPid + "/stat";

        try {
            // monitor total and idle cpu stat of certain process
            RandomAccessFile cpuInfo = new RandomAccessFile("/proc/stat", "r");
            String[] toks = cpuInfo.readLine().split("\\s+");
            idleCpu1 = Long.parseLong(toks[4]);
            totalCpu1 = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
                    + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[7]);
            cpuInfo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // monitor cpu stat of certain process
            RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath,
                    "r");
            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            processCpu1 = Long.parseLong(tok[13]) + Long.parseLong(tok[14])
                    + Long.parseLong(tok[15]) + Long.parseLong(tok[16]);
            processCpuInfo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getCpuRatioInfo(int pid) {

        this.pid = pid;
        readCpuStat();

        if (dataValida()) {
            processCpuRatio = fomart
                    .format(100 * ((double) (processCpu1 - processCpu2) / (double) (totalCpu1 - totalCpu2)));
            totalCpuRatio = fomart
                    .format(Math
                            .abs(100 * ((double) ((totalCpu1 - idleCpu1) - (totalCpu2 - idleCpu2)) / (double) (totalCpu1 - totalCpu2))));
            util.setProcessCpuRatio(processCpuRatio + "%");
            util.setTotalCpuRatio(totalCpuRatio + "%");
            totalCpu2 = totalCpu1;
            processCpu2 = processCpu1;
            idleCpu2 = idleCpu1;
        }
    }

    public boolean dataValida() {
        boolean flag = true;
        if (processCpu2 == 0 || processCpu1 < processCpu2
                || totalCpu1 < totalCpu2) {
            flag = false;
            totalCpu2 = totalCpu1;
            processCpu2 = processCpu1;
            idleCpu2 = idleCpu1;
            util.setProcessCpuRatio("0.00%");
        } else if ((totalCpu1 - totalCpu2) < (processCpu1 - processCpu2)) {
            flag = false;
            totalCpu2 = totalCpu1;
            processCpu2 = processCpu1;
            idleCpu2 = idleCpu1;
            util.setProcessCpuRatio("100.00%");
        }
        return flag;
    }
}

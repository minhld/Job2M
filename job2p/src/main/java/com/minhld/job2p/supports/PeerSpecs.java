package com.minhld.job2p.supports;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by minhld on 12/7/2015.
 */
public class PeerSpecs {
    public long totalCPU;
    public long availableCPU;
    public long totalMemory;
    public long availableMemory;
    public long point;

    public static PeerSpecs getMySpecs() {
        return null;
    }

    public static String readMem() {

        RandomAccessFile reader;
        String line = "";
        float memTotal = 0, memFree = 0, memUsage = 0;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            String[] segs = null;
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();
                if (line.contains("memtotal:")) {
                    line = line.replaceAll("memtotal:", "").
                            replaceAll("kb","").trim();
                    segs = line.trim().split(" ");
                    memTotal = Float.parseFloat(line) / (float)(1024 * 1024);
                }
                if (line.contains("memfree:")) {
                    line = line.replaceAll("memfree:", "").
                            replaceAll("kb", "").trim();

                    memFree = Float.parseFloat(line) / (float)(1024 * 1024);
                    memUsage = (memTotal - memFree) / memTotal;
                }
            }
            return "mem-total: " + memTotal + ", mem-usage: " + memUsage;
        } catch (IOException e) {
            return "";
        }

    }

    private static String getCPU() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            String load = "";
            StringBuffer buff = new StringBuffer();
            while ((load = reader.readLine()) != null) {
                buff.append(load + "\n");
            }

            return "";//buff.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private static float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

}

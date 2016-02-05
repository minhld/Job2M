package com.minhld.job2p.supports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by minhld on 12/7/2015.
 */
public class PeerSpecs {
    public float cpuTotal;
    public float cpuCoreSpeed;
    public int cpuCoreNum;
    public float cpuUsage;

    public int memTotal;
    public float memUsage;

    public int batTotal;
    public float batUsage;

    /**
     * get current specs of the device
     * @return
     */
    public static PeerSpecs getMySpecs() {
        PeerSpecs ps = new PeerSpecs();

        // cpu
        ps.cpuTotal = getCpuTotal();
        ps.cpuUsage = readUsage();

        // memory
        float[] mems = readMem();
        ps.memTotal = (int) mems[0];
        ps.memUsage = mems[1];

        // battery


        return ps;
    }

    public static float[] readMem() {
        RandomAccessFile reader;
        String line = "";
        float memTotal = 0, memFree = 0, memUsage = 0;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();
                if (line.contains("memtotal:")) {
                    line = line.replaceAll("memtotal:", "").
                            replaceAll("kb","").trim();
                    memTotal = Float.parseFloat(line) / (float)(1024 * 1024);
                }
                if (line.contains("memfree:")) {
                    line = line.replaceAll("memfree:", "").
                            replaceAll("kb", "").trim();
                    memFree = Float.parseFloat(line) / (float)(1024 * 1024);
                    memUsage = (memTotal - memFree) / memTotal;
                }
            }
            reader.close();
            return new float[] { memTotal, memUsage };
        } catch (IOException e) {
            return new float[2];
        }

    }

    public static float getCpuTotal() {
        RandomAccessFile reader;
        String line = "";
        StringBuffer buffer = new StringBuffer();
        float cpuTotal = 0;
        try {
            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            while ((line = reader.readLine()) != null) {
                buffer.append(line.toLowerCase());
            }
            reader.close();
            return Float.parseFloat(buffer.toString()) / 1000000f;
        } catch (IOException e) {
            return 0;
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

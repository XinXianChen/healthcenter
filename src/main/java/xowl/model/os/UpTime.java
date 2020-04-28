package xowl.model.os;

import java.io.Serializable;

/**
 * @author xiluo
 * @ClassName
 * @description <pre>
 * /proc/uptime
 *      This  file  contains two numbers: the uptime of the system (seconds), and the amount of time spent in
 *      idle process (seconds).
 *
 *      系统启动到现在的时间 系统空闲的时间
 *
 * /proc/loadavg
 *               The first three fields in this file are load average figures giving the number of  jobs  in  the  run
 *               queue  (state  R) or waiting for disk I/O (state D) averaged over 1, 5, and 15 minutes.  They are the
 *               same as the load average numbers given by uptime(1) and other programs.  The fourth field consists of
 *               two  numbers separated by a slash (/).  The first of these is the number of currently runnable kernel
 *               scheduling entities (processes, threads).  The value after the slash is the number of kernel schedul‐
 *               ing  entities that currently exist on the system.  The fifth field is the PID of the process that was
 *               most recently created on the system.
 *
 *                  /proc/loadavg 各项数据的含义
 *                  /proc文件系统是一个虚拟的文件系统，不占用磁盘空间，它反映了当前操作系统在内存中的运行情况
 *                  ，查看/proc下的文件可以聊寄到系统的运行状态。查看系统平均负载使用“cat /proc/loadavg”命令，输出结果如下：
 *                  0.27 0.36 0.37 4/83 4828
 *                  前三个数字大家都知道，是1、5、15分钟内的平均进程数（有人认为是系统负荷的百分比，其实不然，有些时候可以看到200甚至更多）。
 *                  后面两个呢，一个的分子是正在运行的进程数，分母是进程总数；另一个是最近运行的进程ID号。
 *
 * /var/run/utmp
 *              Users
 *              https://blog.csdn.net/u011641885/article/details/46958613
 *
 *  eg:
 *  $ uptime
 *  20:41:30 up 8 days,  7:36,  7 users,  load average: 0.00, 0.07, 0.13
 * </pre>
 * @date 2020/3/26 20:14
 * @Version 1.0
 **/
public class UpTime implements Serializable {
    private static final long serialVersionUID = -3739814886042574800L;
    private long currentMs;
    private long uptimeSecs;

    /**
     * user number
     * 暂不读取，/var/run/utmp为一个二进制文件，而且用处也不大
     */
    private int userNbr;

    private double load1min;
    private double load5min;
    private double load15min;

    /**
     * The first of these is the number of currently runnable kernel scheduling entities (processes, threads).
     */
    private int runnableProcessNbr;

    /**
     * The value after the slash is the number of kernel scheduling  entities that currently exist on the system.
     */
    private int currentTotalProcessNbr;

    /**
     * The fifth field is the PID of the process that was most recently created on the system.
     */
    private int recentlyPid;

    public long getCurrentMs() {
        return currentMs;
    }

    public void setCurrentMs(long currentMs) {
        this.currentMs = currentMs;
    }

    public long getUptimeSecs() {
        return uptimeSecs;
    }

    public void setUptimeSecs(long uptimeSecs) {
        this.uptimeSecs = uptimeSecs;
    }

    public int getUserNbr() {
        return userNbr;
    }

    public void setUserNbr(int userNbr) {
        this.userNbr = userNbr;
    }

    public double getLoad1min() {
        return load1min;
    }

    public void setLoad1min(double load1min) {
        this.load1min = load1min;
    }

    public double getLoad5min() {
        return load5min;
    }

    public void setLoad5min(double load5min) {
        this.load5min = load5min;
    }

    public double getLoad15min() {
        return load15min;
    }

    public void setLoad15min(double load15min) {
        this.load15min = load15min;
    }

    public int getRunnableProcessNbr() {
        return runnableProcessNbr;
    }

    public void setRunnableProcessNbr(int runnableProcessNbr) {
        this.runnableProcessNbr = runnableProcessNbr;
    }

    public int getCurrentTotalProcessNbr() {
        return currentTotalProcessNbr;
    }

    public void setCurrentTotalProcessNbr(int currentTotalProcessNbr) {
        this.currentTotalProcessNbr = currentTotalProcessNbr;
    }

    public int getRecentlyPid() {
        return recentlyPid;
    }

    public void setRecentlyPid(int recentlyPid) {
        this.recentlyPid = recentlyPid;
    }

    @Override
    public String toString() {
        return "UpTime{" +
                "currentMs=" + currentMs +
                ", uptimeSecs=" + uptimeSecs +
                ", userNbr=" + userNbr +
                ", load1min=" + load1min +
                ", load5min=" + load5min +
                ", load15min=" + load15min +
                ", runnableProcessNbr=" + runnableProcessNbr +
                ", currentTotalProcessNbr=" + currentTotalProcessNbr +
                ", recentlyPid=" + recentlyPid +
                '}';
    }
}

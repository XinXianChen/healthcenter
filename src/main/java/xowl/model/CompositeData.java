package xowl.model;


import xowl.model.jvm.GcUsage;
import xowl.model.jvm.ProcessMemory;
import xowl.model.jvm.ProcessThreadInfo;
import xowl.model.os.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xinxian
 * @create 2020-04-24 10:55
 **/
public class CompositeData implements Serializable {
    private static final long serialVersionUID = -3952624965843092313L;
    private GcUsage gcUsage;
    private ProcessMemory processMemory;
    private ProcessThreadInfo processThreadInfo;
    private CpuStat cpuStat;
    private List<Disk> disks;
    private List<DiskState> diskStates;
    private Memory memory;
    private Os osInfo;
    private List<OsUser> users;
    private ProcessIo processIo;
    private UpTime upTime;

    public ProcessLimit getProcessLimit() {
        return processLimit;
    }

    public void setProcessLimit(ProcessLimit processLimit) {
        this.processLimit = processLimit;
    }

    private ProcessLimit processLimit;

    public GcUsage getGcUsage() {
        return gcUsage;
    }

    public void setGcUsage(GcUsage gcUsage) {
        this.gcUsage = gcUsage;
    }

    public ProcessMemory getProcessMemory() {
        return processMemory;
    }

    public void setProcessMemory(ProcessMemory processMemory) {
        this.processMemory = processMemory;
    }

    public ProcessThreadInfo getProcessThreadInfo() {
        return processThreadInfo;
    }

    public void setProcessThreadInfo(ProcessThreadInfo processThreadInfo) {
        this.processThreadInfo = processThreadInfo;
    }

    public CpuStat getCpuStat() {
        return cpuStat;
    }

    public void setCpuStat(CpuStat cpuStat) {
        this.cpuStat = cpuStat;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public void addDisks(Disk... items) {
        if (items != null && items.length > 0) {
            if (disks == null) {
                disks = new LinkedList<>();
            }

            disks.addAll(Arrays.asList(items));
        }
    }

    public List<DiskState> getDiskStates() {
        return diskStates;
    }

    public void addDiskStates(DiskState... items) {
        if (items != null && items.length > 0) {
            if (diskStates == null) {
                diskStates = new LinkedList<>();
            }

            diskStates.addAll(Arrays.asList(items));
        }
    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public Os getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(Os osInfo) {
        this.osInfo = osInfo;
    }

    public List<OsUser> getUsers() {
        return users;
    }

    public void addOsUsers(OsUser... items) {
        if (items != null && items.length > 0) {
            if (users == null) {
                users = new LinkedList<>();
            }

            users.addAll(Arrays.asList(items));
        }
    }

    public ProcessIo getProcessIo() {
        return processIo;
    }

    public void setProcessIo(ProcessIo processIo) {
        this.processIo = processIo;
    }

    public UpTime getUpTime() {
        return upTime;
    }

    public void setUpTime(UpTime upTime) {
        this.upTime = upTime;
    }
}

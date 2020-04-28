package xowl.service.impl;

import com.perfma.toolkit.common.util.linux.proc.CpuStat;
import com.perfma.toolkit.common.util.linux.proc.StatReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xowl.MetricModule;
import xowl.request.Cmd;
import xowl.service.MetricDataHandler;
import xowl.util.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;

/**
 * @author xinxian
 * @create 2020-04-27 16:58
 **/
public abstract class AbstractMetricDataHandler implements MetricDataHandler {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractMetricDataHandler.class);

    protected boolean isPerfDataSupport;

    protected Cmd cmd;

    protected MetricModule metricModule;

    protected PerfDataUtil perfDataUtil;



    /************************ jvm *******************************************/
    @Override
    public void collectGcUsage(DataOutputStream dataOutputStream) {
        if (!isPerfDataSupport) {
            logger.error("PerfData is not support!");
            return;
        }
        //写入实体类型
        try {
            dataOutputStream.writeInt(EntityType.JVM_GCUSAGE);
            MxBeanUtil.getJvmUptime(metricModule, cmd.getPid().toString(), dataOutputStream);
            perfDataUtil.getGcUsage(dataOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void collectProcessMemory(DataOutputStream dataOutputStream) {
        //写入实体类型
        try {
            dataOutputStream.writeInt(EntityType.JVM_PROCESS_MEMORY);
            //通过mxBean获取heap,noheap,memorypools
            MxBeanUtil.fillJvmMemoryMetrics(metricModule, String.valueOf(cmd.getPid()), dataOutputStream);
            OSMetricDataUtil.fillProcessMemory(String.valueOf(cmd.getPid()), dataOutputStream);
            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);
        } catch (Exception e) {
            logger.error("collect ProcessMemory error:{}", e);
        }
    }

    @Override
    public void collectThreadInfo(DataOutputStream dataOutputStream) {
        MxBeanUtil.getProcessThreadInfo(metricModule, String.valueOf(cmd.getPid()), dataOutputStream);
    }

    /************************ OS *******************************************/
    @Override
    public void collectCpuStat(DataOutputStream dataOutputStream) {
        try {
            CpuStat cpuStat = StatReader.readSystemCpuStat();
            dataOutputStream.writeInt(EntityType.OS_CPU_STAT);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getUser()
                    , MetricDataKey.CPU_STAT_USER, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getNice()
                    , MetricDataKey.CPU_STAT_NICE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getSystem()
                    , MetricDataKey.CPU_STAT_SYSTEM, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getIdle()
                    , MetricDataKey.CPU_STAT_IDLE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getIowait()
                    , MetricDataKey.CPU_STAT_IOWAIT, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getIrq()
                    , MetricDataKey.CPU_STAT_IRQ, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getSoftirq()
                    , MetricDataKey.CPU_STAT_SOFT_IRQ, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getSteal()
                    , MetricDataKey.CPU_STAT_STEAL, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getGuest()
                    , MetricDataKey.CPU_STAT_GUEST, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, cpuStat.getGuestNice()
                    , MetricDataKey.CPU_STAT_GUEST_NICE, dataOutputStream);
            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (IOException e) {
            logger.error("collectCpuStat error:{}", e);
        }
    }

    @Override
    public void collectDisk(DataOutputStream dataOutputStream) {
        try {
            Iterable<FileStore> fileStores = FileSystems.getDefault().getFileStores();
            for (FileStore fileStore : fileStores) {
                dataOutputStream.writeInt(EntityType.OS_DISK);
                DataWriteUtil.writeString(MetricDataType.STRING, fileStore.name()
                        , MetricDataKey.DISK_FILE_SYSTEM, dataOutputStream);
                Class<?> ufsClz = Class.forName("sun.nio.fs.UnixFileStore");
                if (ufsClz.isAssignableFrom(fileStore.getClass())) {
                    Method entry = ufsClz.getDeclaredMethod("entry");
                    entry.setAccessible(true);
                    Object eObj = entry.invoke(fileStore);
                    Method dir = eObj.getClass().getDeclaredMethod("dir");
                    dir.setAccessible(true);
                    byte[] dirBytes = (byte[]) dir.invoke(eObj);
                    if (dirBytes != null) {
                        dataOutputStream.writeInt(MetricDataType.STRING);
                        //针对string类型的数据需要写入长度
                        dataOutputStream.writeInt(dirBytes.length);
                        dataOutputStream.write(dirBytes);
                        dataOutputStream.writeInt(MetricDataKey.DISK_MOUNTED_ON);

                    }
                }
                DataWriteUtil.writeLong(MetricDataType.LONG, fileStore.getTotalSpace()
                        , MetricDataKey.DISK_SIZE, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, fileStore.getTotalSpace() - fileStore.getUsableSpace()
                        , MetricDataKey.DISK_USED, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, fileStore.getUsableSpace()
                        , MetricDataKey.DISK_AVAIL, dataOutputStream);

                DataWriteUtil.writeDouble(MetricDataType.DOUBLE, (fileStore.getUsableSpace() * 1.0) / fileStore.getTotalSpace()
                        , MetricDataKey.DISK_USE_PERCENT, dataOutputStream);

                //写入结束标识
                dataOutputStream.writeInt(MetricDataType.END);
            }

        } catch (Exception e) {
            logger.error("collect Disk infos error:{}", e);
        }
    }

    @Override
    public void collectDiskState(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readSystemDiskState(dataOutputStream);
    }

    @Override
    public void collectMemoryInfo(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readOsMemInfo(dataOutputStream);
    }

    @Override
    public void collectOs(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeInt(EntityType.OS);

            DataWriteUtil.writeString(MetricDataType.STRING, System.getProperty("os.version")
                    , MetricDataKey.OS_VERSION, dataOutputStream);

            DataWriteUtil.writeString(MetricDataType.STRING, System.getProperty("os.arch")
                    , MetricDataKey.OS_ARCH, dataOutputStream);

            DataWriteUtil.writeString(MetricDataType.STRING, System.getProperty("os.name")
                    , MetricDataKey.OS_TYPE, dataOutputStream);
            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (Exception e) {
            logger.error("collect Os Info error:{}", e);
        }

    }

    @Override
    public void collectOsUser(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readUserGroups();
        OSMetricDataUtil.readUsers(dataOutputStream);
    }

    @Override
    public void collectProcessIo(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readProcessIo(String.valueOf(cmd.getPid()), dataOutputStream);
    }

    @Override
    public void collectProcessLimit(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readProcessLimit(String.valueOf(cmd.getPid()), dataOutputStream);
    }

    @Override
    public void collectUpTime(DataOutputStream dataOutputStream) {
        OSMetricDataUtil.readUpTime(dataOutputStream);
    }
}

package xowl.service.impl;


import xowl.MetricModule;
import xowl.request.Cmd;
import xowl.util.MetricDataKey;
import xowl.util.MetricDataType;
import xowl.util.PerfDataUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 默认实现 操作系统为linux 内核大于等于{2.6.0} ,  jdk >= {hotspot 1.6}
 *
 * @author xinxian
 * @create 2020-04-27 17:05
 **/
public class DefaultMetricDataHandlerImpl extends AbstractMetricDataHandler {

    private ByteArrayOutputStream outputStream;

    private DataOutputStream dataOutputStream;

    public DefaultMetricDataHandlerImpl(Cmd cmd, MetricModule metricModule) {
        super.cmd = cmd;
        super.metricModule = metricModule;
        perfDataUtil = PerfDataUtil.connect(cmd.getPid());
        isPerfDataSupport = perfDataUtil != null;
        //字节数组流  用于保存数据
        outputStream = new ByteArrayOutputStream(512);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    @Override
    public byte[] collect() {
        final String metricDatas = cmd.getMetricDatas();
        final String[] split = metricDatas.split(";");
        try {
            //重置
            outputStream.reset();
            //将版本号写入
            dataOutputStream.writeDouble(MetricDataType.VERSION);
            for (String metricName : split) {
                collectDataByMetricName(metricName, dataOutputStream);
            }
            //写入完成标识
            dataOutputStream.writeInt(MetricDataType.COMPLETE);
        } catch (IOException e) {
            logger.error("collect hotspot jvm data error:{}", e);
        }
        return outputStream.toByteArray();
    }

    private void collectDataByMetricName(String metricName, DataOutputStream dataOutputStream) {
        try {
            switch (metricName) {
                case MetricDataKey.OS_CPUSTAT:
                    collectCpuStat(dataOutputStream);
                    break;
                case MetricDataKey.OS_DISKS:
                    collectDisk(dataOutputStream);
                    break;
                case MetricDataKey.OS_DISKSTATES:
                    collectDiskState(dataOutputStream);
                    break;
                case MetricDataKey.OS_INFO:
                    collectOs(dataOutputStream);
                    break;
                case MetricDataKey.OS_MEMORY:
                    collectMemoryInfo(dataOutputStream);
                    break;
                case MetricDataKey.OS_PROCESSIO:
                    collectProcessIo(dataOutputStream);
                    break;
                case MetricDataKey.OS_PROCESSlIMIT:
                    collectProcessLimit(dataOutputStream);
                    break;
                case MetricDataKey.OS_UPTIME:
                    collectUpTime(dataOutputStream);
                    break;
                case MetricDataKey.OS_USERS:
                    collectOsUser(dataOutputStream);
                    break;
                case MetricDataKey.JVM_GCUSAGE:
                    collectGcUsage(dataOutputStream);
                    break;
                case MetricDataKey.JVM_ProcessMemory:
                    collectProcessMemory(dataOutputStream);
                    break;
                case MetricDataKey.JVM_ProcessThreadInfo:
                    collectThreadInfo(dataOutputStream);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("collect Data By MetricName error:{}", e);
        }
    }
}

package xowl.util;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xowl.MetricModule;
import xowl.model.jvm.ProcessThreadInfo;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.*;
import java.util.*;

import static java.lang.management.ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

/**
 * @author xinxian
 * @create 2020-04-13 16:41
 **/
public class MxBeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(MxBeanUtil.class);

    private static String CONNECT_ADDRESS;

    private static JMXConnector connector;


    private static void getConnectAddress() {
        //暂时写死 连接1201进程
        try {
            VirtualMachine virtualMachine = VirtualMachine.attach("1201");
            String javaHome = virtualMachine.getSystemProperties().getProperty("java.home");
            String jmxAgent = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
            virtualMachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
            //获得连接地址
            Properties properties = virtualMachine.getAgentProperties();
            CONNECT_ADDRESS = (String) properties.get("com.sun.management.jmxremote.localConnectorAddress");

        } catch (Exception e) {
            logger.error("getConnectAddress error:{}", e);
        }
    }

    private static MemoryMXBean getMemoryMxbean() throws IOException {
        if (connector == null) {
            if (CONNECT_ADDRESS == null) {
                getConnectAddress();
            }
            JMXServiceURL url = new JMXServiceURL(CONNECT_ADDRESS);
            connector = JMXConnectorFactory.connect(url);
        }
        return ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    }

    public static Collection<MemoryPoolMXBean> getMemoryPoolMXBeans() throws IOException {
        return getPlatformMxBeans(MemoryPoolMXBean.class, MEMORY_POOL_MXBEAN_DOMAIN_TYPE);
    }

    public static <T> Collection<T> getPlatformMxBeans(Class<T> mxBeansClass, String mxBeansClassName) throws IOException {
        if (connector == null) {
            if (CONNECT_ADDRESS == null) {
                getConnectAddress();
            }
            JMXServiceURL url = new JMXServiceURL(CONNECT_ADDRESS);
            connector = JMXConnectorFactory.connect(url);
        }

        Set<T> collectorMXBeans = null;
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(mxBeansClassName + ",*");
        } catch (MalformedObjectNameException e) {
            // should not reach here
            assert (false);
        }

        if (objectName != null) {
            Set<ObjectName> mbeans = connector.getMBeanServerConnection().queryNames(objectName, null);
            if (mbeans != null && !mbeans.isEmpty()) {
                collectorMXBeans = new HashSet<>();
                Iterator<ObjectName> iterator = mbeans.iterator();
                while (iterator.hasNext()) {
                    ObjectName on = iterator.next();
                    String ObName = mxBeansClassName +
                            ",name=" + on.getKeyProperty("name");
                    T mBean = newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ObName,
                            mxBeansClass);
                    collectorMXBeans.add(mBean);
                }
            }
        }

        return collectorMXBeans;
    }

    public static void fillJvmMemoryMetrics(MetricModule metricModule, String pid, DataOutputStream dataOutputStream) throws Exception {
        MemoryMXBean memoryMXBean = getMemoryMxbean();
        //写入heap信息
        writeHeapMemoryUsage(dataOutputStream, memoryMXBean);
        //写入noHeap信息
        writeNoHeapMemoryUsage(dataOutputStream, memoryMXBean);
        //写入memoryPools
        writeMemoryPools(dataOutputStream, metricModule, pid);

    }

    /**
     * 写入内存池信息 heap、codeCache、bufferPool等
     * @param dataOutputStream
     * @param metricModule
     * @param pid
     */
    private static void writeMemoryPools(DataOutputStream dataOutputStream, MetricModule metricModule, String pid) {

        List<MemoryPoolMXBean> memoryPoolMXBeans = null;
        try {
            memoryPoolMXBeans = new ArrayList<>(getMemoryPoolMXBeans());
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                dataOutputStream.writeInt(EntityType.MEMORY_POOL);

                DataWriteUtil.writeLong(MetricDataType.LONG, memoryPoolMXBean.getUsage().getUsed()
                        , MetricDataKey.MEMORY_POOL_USED, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, memoryPoolMXBean.getUsage().getCommitted()
                        , MetricDataKey.MEMORY_POOL_COMMITTED, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, memoryPoolMXBean.getUsage().getMax()
                        , MetricDataKey.MEMORY_POOL_MAX, dataOutputStream);

                DataWriteUtil.writeString(MetricDataType.STRING, memoryPoolMXBean.getName()
                        , MetricDataKey.MEMORY_POOL_NAME, dataOutputStream);

                DataWriteUtil.writeString(MetricDataType.STRING, memoryPoolMXBean.getType().name()
                        , MetricDataKey.MEMORY_POOL_TYPE, dataOutputStream);

                //写入结束标识
                dataOutputStream.writeInt(MetricDataType.END);
            }
        } catch (Exception e) {
            logger.error("getMemoryPoolMXBeans exception. pid:{}", pid, e);
        }
    }

    /**
     * 采集非堆的使用情况并写入字节流返回
     * @param dataOutputStream
     * @param memoryMXBean
     * @throws Exception
     */
    private static void writeNoHeapMemoryUsage(DataOutputStream dataOutputStream, MemoryMXBean memoryMXBean) throws Exception {

        dataOutputStream.writeInt(EntityType.MEMORY_USAGE);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getNonHeapMemoryUsage().getInit()
                , MetricDataKey.MEMORY_USAGE_INIT, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getNonHeapMemoryUsage().getCommitted()
                , MetricDataKey.MEMORY_USAGE_COMMINTTED, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getNonHeapMemoryUsage().getMax()
                , MetricDataKey.MEMORY_USAGE_MAX, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getNonHeapMemoryUsage().getUsed()
                , MetricDataKey.MEMORY_USAGE_USED, dataOutputStream);

        DataWriteUtil.writeInt(MetricDataType.INT, 2
                , MetricDataKey.MEMORY_USAGE_TYPE, dataOutputStream);

        //写入结束标识
        dataOutputStream.writeInt(MetricDataType.END);

    }

    /**
     * 采集堆使用情况并写入字节流返回
     * @param dataOutputStream
     * @param memoryMXBean
     * @throws Exception
     */
    private static void writeHeapMemoryUsage(DataOutputStream dataOutputStream, MemoryMXBean memoryMXBean) throws Exception {
        dataOutputStream.writeInt(EntityType.MEMORY_USAGE);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getHeapMemoryUsage().getInit()
                , MetricDataKey.MEMORY_USAGE_INIT, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getHeapMemoryUsage().getCommitted()
                , MetricDataKey.MEMORY_USAGE_COMMINTTED, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getHeapMemoryUsage().getMax()
                , MetricDataKey.MEMORY_USAGE_MAX, dataOutputStream);

        DataWriteUtil.writeLong(MetricDataType.LONG, memoryMXBean.getHeapMemoryUsage().getUsed()
                , MetricDataKey.MEMORY_USAGE_USED, dataOutputStream);

        DataWriteUtil.writeInt(MetricDataType.INT, 1
                , MetricDataKey.MEMORY_USAGE_TYPE, dataOutputStream);

        //写入结束标识
        dataOutputStream.writeInt(MetricDataType.END);

    }

    /**
     * 采集线程信息并写入字节流返回
     * @param metricModule
     * @param pid
     * @param dataOutputStream
     */
    public static void getProcessThreadInfo(MetricModule metricModule, String pid, DataOutputStream dataOutputStream) {
        ThreadMXBean threadMXBean = null;
        try {
            threadMXBean = getThreadMXBean();

            dataOutputStream.writeInt(EntityType.JVM_PROCESS_THREADINFO);
            DataWriteUtil.writeLong(MetricDataType.LONG, threadMXBean.getThreadCount()
                    , MetricDataKey.THREAD_INFO_THREAD_COUNT, dataOutputStream);

            long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
            //写入字段类型 init
            dataOutputStream.writeInt(MetricDataType.LONG);
            if (deadlockedThreads.length > 0) {
                //写入字段值
                dataOutputStream.writeLong(1);
            } else {
                dataOutputStream.writeLong(0);
            }
            //写入字段名
            dataOutputStream.writeInt(MetricDataKey.THREAD_INFO_DEADLOCK_FLAG);
//            processThreadInfo.setDeadLocks(generateDeadLockInfos(deadlockedThreads, threadMXBean));

            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (Exception e) {
            logger.error("get ProcessThreadInfo exception. pid:{}", pid, e);
        }
    }


    /**
     * 得到死锁线程栈信息
     * @param deadlockedThreads
     * @param threadMXBean
     * @return
     */
    private static List<ProcessThreadInfo.DeadLockThread> generateDeadLockInfos(long[] deadlockedThreads, ThreadMXBean threadMXBean) {
        if (deadlockedThreads == null) return new LinkedList<>();
        List<ProcessThreadInfo.DeadLockThread>  deadLockThreads = new LinkedList<>();
        for (long threadId : deadlockedThreads) {
            ProcessThreadInfo.DeadLockThread deadLockThread = new ProcessThreadInfo.DeadLockThread();
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            deadLockThread.setTid(threadInfo.getThreadId());
            deadLockThread.setThreadName(threadInfo.getThreadName());
            List<String> stackTraceLines = new LinkedList<>();
            for (StackTraceElement stackTraceElement : threadInfo.getStackTrace()) {
                String stackTraceLine = stackTraceElement.getMethodName() + "("
                        + stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber() + ")";
                stackTraceLines.add(stackTraceLine);
            }
            deadLockThread.setStackTraceLines(stackTraceLines);
            deadLockThreads.add(deadLockThread);
        }
        return deadLockThreads;
    }

    public static void getJvmUptime(MetricModule metricModule, String pid, DataOutputStream dataOutputStream) throws IOException {
        RuntimeMXBean runtimeMXBean = getRuntimeMxBean();
        DataWriteUtil.writeLong(MetricDataType.LONG, runtimeMXBean.getUptime()
                , MetricDataKey.GC_USAGE_JVMUPDATE, dataOutputStream);
    }

    private static RuntimeMXBean getRuntimeMxBean() throws IOException {
        if (connector == null) {
            if (CONNECT_ADDRESS == null) {
                getConnectAddress();
            }
            JMXServiceURL url = new JMXServiceURL(CONNECT_ADDRESS);
            connector = JMXConnectorFactory.connect(url);
        }
        return ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
    }

    private static ThreadMXBean getThreadMXBean() throws IOException {
        if (connector == null) {
            if (CONNECT_ADDRESS == null) {
                getConnectAddress();
            }
            JMXServiceURL url = new JMXServiceURL(CONNECT_ADDRESS);
            connector = JMXConnectorFactory.connect(url);
        }
        return ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
    }

}

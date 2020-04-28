package xowl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xowl.model.os.UserGroup;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xinxian
 * @create 2020-04-13 20:46
 **/
public class OSMetricDataUtil {

    private static final Logger logger = LoggerFactory.getLogger(OSMetricDataUtil.class);

    public static final String DISK_STATS_FORMAT = "/proc/diskstats";
    public static final String MEM_INFO_FORMAT = "/proc/meminfo";
    public static final String UPTIME_FORMAT = "/proc/uptime";
    public static final String LOAD_AVG_FORMAT = "/proc/loadavg";

    public static final String USER_GROUP_FORMAT = "/etc/group";
    public static final String USER_INFO_FORMAT = "/etc/passwd";


    public static Map<String,String> memInfoCache = new HashMap<>();
    public static Map<String, UserGroup> userGroupCache = new HashMap<>();


    /**
     * 读取/proc/diskstats 相关的信息
     *
     * 252       0 vda 46867 39412 14629491 3381388 841397 126627 133697296 48966333 0 6443710 52352123
     * 252       1 vda1 1236 0 60003 2436 16 0 4177 4904 0 6604 7340
     *
     * @return 系统 CPU 的相关信息
     * @param dataOutputStream
     */
    public static void readSystemDiskState(DataOutputStream dataOutputStream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(DISK_STATS_FORMAT)));
            String stateLine = null;
            while ((stateLine = reader.readLine()) != null) {
                String[] props = stateLine.split(" ");
                final int length = props.length;
                dataOutputStream.writeInt(EntityType.OS_DISKSTATES);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 11].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_COMPLETED_READ_NBR, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 10].trim())
                        , MetricDataKey.DISK_STATE_READ_MERGED_NBR, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 9].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_READ_SECTORS_Nbr, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 8].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_READ_MS, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 7].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_COMPLETED_WRITE_NBR, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 6].trim())
                        , MetricDataKey.DISK_STATE_WRITE_MERGED_NBR, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 5].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_WRITE_SECTORS_Nbr, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 4].trim())
                        , MetricDataKey.DISK_STATE_TOTAL_WRITE_MS, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 3].trim())
                        , MetricDataKey.DISK_STATE_IO_QUEUE_NOT_EMPTY_MS, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 2].trim())
                        , MetricDataKey.DISK_STATE_IO_REQUEST_IN_PROGRESS, dataOutputStream);

                DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[length - 1].trim())
                        , MetricDataKey.DISK_STATE_IO_QUEUE_NOT_EMPTY_WEIGHT_MS, dataOutputStream);

                //写入结束标识
                dataOutputStream.writeInt(MetricDataType.END);
            }
        } catch (Exception e) {
            logger.error("read System DiskState error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *  /proc/meminfo
     *
     * MemTotal:       16266212 kB
     * MemFree:        13636144 kB
     * MemAvailable:   14513608 kB
     * Buffers:            2108 kB
     * Cached:          1108056 kB
     * SwapCached:            0 kB
     * Active:          1717940 kB
     * Inactive:         585376 kB
     * Active(anon):    1193548 kB
     * Inactive(anon):     8364 kB
     * Active(file):     524392 kB
     * Inactive(file):   577012 kB
     * Unevictable:           0 kB
     * Mlocked:               0 kB
     * SwapTotal:       8257532 kB
     * SwapFree:        8257532 kB
     * Dirty:                16 kB
     * Writeback:             0 kB
     * AnonPages:       1193272 kB
     * Mapped:           139052 kB
     * Shmem:              8760 kB
     * @param dataOutputStream
     */
    public static void readOsMemInfo(DataOutputStream dataOutputStream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(MEM_INFO_FORMAT)));
            String stateLine = null;
            while ((stateLine = reader.readLine()) != null) {
                String[] props = stateLine.split(":");
                String key = props[0];
                String val = props[1].trim().split(" ")[0].trim();
                memInfoCache.put(key,val);
                if (key.equals("Shmem")) {
                    break;
                }
            }
            dataOutputStream.writeInt(EntityType.OS_MEMORY);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("MemTotal"))
                    , MetricDataKey.OS_MEMORY_TOTAL_PHYSICAL_MEMORY_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("MemFree"))
                    , MetricDataKey.OS_MEMORY_FREE_PHYSICAL_MEMORY_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("MemAvailable"))
                    , MetricDataKey.OS_MEMORY_AVAILABLE_PHYSICAL_MEMORY_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("Buffers"))
                    , MetricDataKey.OS_MEMORY_BUFFER_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("Cached"))
                    , MetricDataKey.OS_MEMORY_CACHE_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("SwapTotal"))
                    , MetricDataKey.OS_MEMORY_TOTAL_SWAP_SPACE_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("SwapFree"))
                    , MetricDataKey.OS_MEMORY_FREE_SWAP_SPACE_SIZE, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("Dirty"))
                    , MetricDataKey.OS_MEMORY_DIRTY_SIZE_, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(memInfoCache.get("Shmem"))
                    , MetricDataKey.OS_MEMORY_SHARE_SIZE_, dataOutputStream);

            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (Exception e) {
            logger.error("read read Os MemInfo error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * /proc/uptime
     * 13661661.19 165386721.29
     * 系统启动到现在的时间 系统空闲的时间
     *
     * /proc/loadavg
     * 0.27 0.36 0.37 4/83 4828
     * 前三个数字大家都知道，是1、5、15分钟内的平均进程数（有人认为是系统负荷的百分比，其实不然，有些时候可以看到200甚至更多）。
     * 后面两个呢，一个的分子是正在运行的进程数，分母是进程总数；另一个是最近运行的进程ID号。
     *
     *
     * @param dataOutputStream
     */
    public static void readUpTime(DataOutputStream dataOutputStream) {
        BufferedReader uptimeInfoReader = null;
        BufferedReader loadAvgInfoReader = null;
        try {
            dataOutputStream.writeInt(EntityType.OS_UPTIME);

            uptimeInfoReader = new BufferedReader(new FileReader(new File(UPTIME_FORMAT)));
            String uptimeInfo = uptimeInfoReader.readLine();
            if (uptimeInfo != null) {
                //13661661.19 165386721.29
                String[] split = uptimeInfo.split(" ");
                String currentMsStr = split[0].trim();
                if (!currentMsStr.equals("")) {
                    //系统启动到现在的时间
                    DataWriteUtil.writeLong(MetricDataType.LONG, (long) Double.parseDouble(currentMsStr) * 1000
                            , MetricDataKey.OS_UPTIME_CURRENT_MS, dataOutputStream);
                }
                String uptimeSecsStr = split[1].trim();
                if (!uptimeSecsStr.equals("")) {
                    //系统空闲的时间
                    DataWriteUtil.writeLong(MetricDataType.LONG, (long) Double.parseDouble(currentMsStr) * 1000
                            , MetricDataKey.OS_UPTIME_UPTIME_SECS, dataOutputStream);
                }
            }

            loadAvgInfoReader = new BufferedReader(new FileReader(new File(LOAD_AVG_FORMAT)));
            String loadAvgInfo = loadAvgInfoReader.readLine();
            String[] split = loadAvgInfo.split(" ");

            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, Double.parseDouble(split[0].trim())
                    , MetricDataKey.OS_UPTIME_LOAD_1_MIN, dataOutputStream);

            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, Double.parseDouble(split[1].trim())
                    , MetricDataKey.OS_UPTIME_LOAD_5_MIN, dataOutputStream);

            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, Double.parseDouble(split[2].trim())
                    , MetricDataKey.OS_UPTIME_LOAD_15_MIN, dataOutputStream);

            String[] split1 = split[3].split("/");

            DataWriteUtil.writeInt(MetricDataType.INT, Integer.parseInt(split1[0].trim())
                    , MetricDataKey.OS_UPTIME_RUNNABLE_PROESS_NBR, dataOutputStream);

            DataWriteUtil.writeInt(MetricDataType.INT, Integer.parseInt(split1[1].trim())
                    , MetricDataKey.OS_UPTIME_CURRENT_TOTAL_PROESS_NBR, dataOutputStream);

            DataWriteUtil.writeInt(MetricDataType.INT, Integer.parseInt(split[4].trim())
                    , MetricDataKey.OS_UPTIME_RECENTLY_PID, dataOutputStream);

            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (Exception e) {
            logger.error("read read Os MemInfo error:{}",e);
        } finally {
            if (uptimeInfoReader != null) {
                try {
                    uptimeInfoReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (loadAvgInfoReader != null) {
                try {
                    loadAvgInfoReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * /proc/$pid/io
     *
     * rchar: 2151932403
     * wchar: 2757379007
     * syscr: 77811288
     * syscw: 45792360
     * read_bytes: 0
     * write_bytes: 130498560
     * cancelled_write_bytes: 3551232
     * @param pid
     * @param dataOutputStream
     */
    public static void readProcessIo(String pid, DataOutputStream dataOutputStream) {
        String path = "/proc/" + pid + "/io";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            dataOutputStream.writeInt(EntityType.OS_PROCESSIO);
            String info = null;
            while ((info = reader.readLine()) != null) {
                String[] props = info.split(":");
                switch (props[0]) {
                    case "rchar" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_R_CHAR, dataOutputStream);
                        break;
                    case "wchar" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_W_CHAR, dataOutputStream);
                        break;
                    case "syscr" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_SYS_CALL_READ_COUNT, dataOutputStream);
                        break;
                    case "syscw" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_SYS_CALL_WRITE_COUNT, dataOutputStream);
                        break;
                    case "read_bytes" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_READ_BYTES, dataOutputStream);
                        break;
                    case "write_bytes" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_WRITE_BYTES, dataOutputStream);
                        break;
                    case "cancelled_write_bytes" :
                        DataWriteUtil.writeLong(MetricDataType.LONG, Long.parseLong(props[1].trim())
                                , MetricDataKey.OS_PROCESS_IO_CAN_CELLED_WRITE_BYTES, dataOutputStream);
                        break;
                    default:
                        break;
                }
            }
            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);
        } catch (Exception e) {
            logger.error("read System DiskState error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     *
     * /proc/$pid/limits
     *
     * Limit                     Soft Limit           Hard Limit           Units
     * Max cpu time              unlimited            unlimited            seconds
     * Max file size             unlimited            unlimited            bytes
     * Max data size             unlimited            unlimited            bytes
     * Max stack size            8388608              unlimited            bytes
     * Max core file size        0                    unlimited            bytes
     * Max resident set          unlimited            unlimited            bytes
     * Max processes             4096                 15076                processes
     * Max open files            4096                 4096                 files
     * Max locked memory         65536                65536                bytes
     * Max address space         unlimited            unlimited            bytes
     * Max file locks            unlimited            unlimited            locks
     * Max pending signals       15076                15076                signals
     * Max msgqueue size         819200               819200               bytes
     * Max nice priority         0                    0
     * Max realtime priority     0                    0
     * Max realtime timeout      unlimited            unlimited            us
     * @param pid
     * @param dataOutputStream
     */
    public static void readProcessLimit(String pid, DataOutputStream dataOutputStream) {
        String path = "/proc/" + pid + "/limits";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String info = null;
            dataOutputStream.writeInt(EntityType.OS_PROCESS_LIMIT);
            while ((info = reader.readLine()) != null) {
                String[] props = info.split(" ");
                switch (props[1]) {
                    case "cpu" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxCpuTimeLimit"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "file" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        if (props[2].equals("locks")) {
                            DataWriteUtil.writeString(MetricDataType.STRING, "MaxFileLocks"
                                    , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        } else {
                            DataWriteUtil.writeString(MetricDataType.STRING, "MaxFileSize"
                                    , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        }
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "data" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxDataSize"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "stack" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxStackSize"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "core" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxCoreFileSize"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "resident" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxResidentSet"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "processes" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxProcesses"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "open" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxOpenFiles"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "locked" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxLockedMemory"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "address" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxAddressSpace"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "pending" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxPendingSignals"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "msgqueue" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxMsgQueueSize"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "nice" :
                        writeCommonLimitInfo(props, dataOutputStream);
                        DataWriteUtil.writeString(MetricDataType.STRING, "MaxNicePriority"
                                , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    case "realtime" :
                        dataOutputStream.writeInt(EntityType.LIMIT_ENTITY);

                        DataWriteUtil.writeString(MetricDataType.STRING, ""
                                , MetricDataKey.OS_LIMIT_ENTITY_UNIT, dataOutputStream);

                        DataWriteUtil.writeString(MetricDataType.STRING, props[props.length - 1]
                                , MetricDataKey.OS_LIMIT_ENTITY_HARD_LIMIT, dataOutputStream);

                        DataWriteUtil.writeString(MetricDataType.STRING, props[props.length - 2]
                                , MetricDataKey.OS_LIMIT_ENTITY_SOFT_LIMIT, dataOutputStream);
                        if (props[2].equals("priority")) {
                            DataWriteUtil.writeString(MetricDataType.STRING, "MaxRealTimePriority"
                                    , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        } else {
                            DataWriteUtil.writeString(MetricDataType.STRING, "MaxRealTimeTimeout"
                                    , MetricDataKey.OS_LIMIT_ENTITY_DESC, dataOutputStream);
                        }
                        //写入结束标识
                        dataOutputStream.writeInt(MetricDataType.END);
                        break;
                    default:
                        break;
                }
            }
            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);
        } catch (Exception e) {
            logger.error("read ProcessLimit error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeCommonLimitInfo(String[] props, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(EntityType.LIMIT_ENTITY);
        DataWriteUtil.writeString(MetricDataType.STRING, props[props.length - 1]
                , MetricDataKey.OS_LIMIT_ENTITY_UNIT, dataOutputStream);
        int hardLimitIndex = props.length - 2;
        for (int i = props.length - 2; i >= 0; i--) {
            if (!"".equals(props[i])) {
                hardLimitIndex = i;
                break;
            }
        }
        int softLimitIndex = hardLimitIndex - 1;
        for (int i = hardLimitIndex - 1; i >= 0; i--) {
            if (!"".equals(props[i])) {
                softLimitIndex = i;
                break;
            }
        }
        DataWriteUtil.writeString(MetricDataType.STRING, props[hardLimitIndex]
                , MetricDataKey.OS_LIMIT_ENTITY_HARD_LIMIT, dataOutputStream);

        DataWriteUtil.writeString(MetricDataType.STRING, props[softLimitIndex]
                , MetricDataKey.OS_LIMIT_ENTITY_SOFT_LIMIT, dataOutputStream);
    }


    /**
     * 读取/etc/group
     *如
     * root:x:0:
     * bin:x:1:
     * daemon:x:2:
     * sys:x:3:
     * adm:x:4:
     * tty:x:5:
     * disk:x:6:
     * lp:x:7:
     * mem:x:8:
     * kmem:x:9:
     * wheel:x:10:
     * cdrom:x:11:
     */
    public static void readUserGroups() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(USER_GROUP_FORMAT)));
            String info = null;
            while ((info = reader.readLine()) != null) {
                final String[] split = info.split(":");
                UserGroup userGroup = new UserGroup();
                userGroup.setName(split[0]);
                userGroup.setId(Integer.parseInt(split[2].trim()));
                userGroupCache.put(split[2].trim(),userGroup);
            }
        } catch (Exception e) {
            logger.error("read User Groups error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 读取 /etc/passwd
     * 如
     * root:x:0:0:root:/root:/bin/bash
     * bin:x:1:1:bin:/bin:/sbin/nologin
     * daemon:x:2:2:daemon:/sbin:/sbin/nologin
     * adm:x:3:4:adm:/var/adm:/sbin/nologin
     * lp:x:4:7:lp:/var/spool/lpd:/sbin/nologin
     * sync:x:5:0:sync:/sbin:/bin/sync
     * shutdown:x:6:0:shutdown:/sbin:/sbin/shutdown
     * halt:x:7:0:halt:/sbin:/sbin/halt
     * mail:x:8:12:mail:/var/spool/mail:/sbin/nologin
     *
     * @param dataOutputStream
     */
    public static void readUsers(DataOutputStream dataOutputStream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(USER_INFO_FORMAT)));
            String info = null;
            while ((info = reader.readLine()) != null) {
                final String[] split = info.split(":");
                dataOutputStream.writeInt(EntityType.OS_USERS);
                DataWriteUtil.writeInt(MetricDataType.INT, Integer.parseInt(split[2].trim())
                        , MetricDataKey.OS_USER_ID, dataOutputStream);

                DataWriteUtil.writeString(MetricDataType.STRING, split[0]
                        , MetricDataKey.OS_USER_NAME, dataOutputStream);

                final UserGroup userGroup = userGroupCache.get(split[3].trim());
                if (userGroup != null) {
                    dataOutputStream.writeInt(EntityType.USERGROUP);

                    DataWriteUtil.writeInt(MetricDataType.INT, userGroup.getId()
                            , MetricDataKey.OS_USER_GROUP_ID, dataOutputStream);

                    DataWriteUtil.writeString(MetricDataType.STRING, userGroup.getName()
                            , MetricDataKey.OS_USER_GROUP_NAME, dataOutputStream);
                    //写入结束标识
                    dataOutputStream.writeInt(MetricDataType.END);
                }
                //写入结束标识
                dataOutputStream.writeInt(MetricDataType.END);
            }
        } catch (Exception e) {
            logger.error("read Users error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取/proc/$pid/smaps文件 将结果相加
     *
     * 00400000-00401000 r-xp 00000000 fd:00 101762605                          /home/admin/perfma/xland-all-in-one/jre/bin/java
     * Size:                  4 kB
     * Rss:                   4 kB
     * Pss:                   4 kB
     * Shared_Clean:          0 kB
     * Shared_Dirty:          0 kB
     * Private_Clean:         4 kB
     * Private_Dirty:         0 kB
     * Referenced:            4 kB
     * Anonymous:             0 kB
     * AnonHugePages:         0 kB
     * Swap:                  0 kB
     * KernelPageSize:        4 kB
     * MMUPageSize:           4 kB
     * Locked:                0 kB
     * VmFlags: rd ex mr mw me dw sd
     * 00600000-00601000 rw-p 00000000 fd:00 101762605                          /home/admin/perfma/xland-all-in-one/jre/bin/java
     * Size:                  4 kB
     * Rss:                   4 kB
     * Pss:                   4 kB
     * Shared_Clean:          0 kB
     * Shared_Dirty:          0 kB
     * Private_Clean:         0 kB
     * Private_Dirty:         4 kB
     * Referenced:            4 kB
     * Anonymous:             4 kB
     * AnonHugePages:         0 kB
     * Swap:                  0 kB
     * KernelPageSize:        4 kB
     * MMUPageSize:           4 kB
     * Locked:                0 kB
     * VmFlags: rd wr mr mw me dw ac sd
     * @param pid
     * @param dataOutputStream
     */
    public static void fillProcessMemory(String pid, DataOutputStream dataOutputStream) {
        String path = "/proc/" + pid + "/smaps";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String info = null;
            Map<String,Long> maps = new HashMap<>();
            while ((info = reader.readLine()) != null) {
                if (info.contains("Rss")
                        || info.contains("Pss")
                        || info.contains("Private_Clean")
                        || info.contains("Private_Dirty")
                        || info.contains("Swap")) {
                    final String[] split = info.split(":");
                    Long val = maps.get(split[0].trim());
                    String[] split1 = split[1].split(" ");
                    if (val == null) {
                        maps.put(split[0].trim(),Long.parseLong(split1[split1.length - 2].trim()));
                    } else {
                        val += Long.parseLong(split1[split1.length - 2].trim());
                        maps.put(split[0].trim(),val);
                    }
                }
            }

            DataWriteUtil.writeLong(MetricDataType.LONG, maps.get("Pss")
                    , MetricDataKey.PROCESS_MEMORY_PSS, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, maps.get("Rss")
                    , MetricDataKey.PROCESS_MEMORY_RSS, dataOutputStream);

            DataWriteUtil.writeLong(MetricDataType.LONG, maps.get("Swap")
                    , MetricDataKey.PROCESS_MEMORY_VSS, dataOutputStream);

            long uss = maps.get("Private_Clean") + maps.get("Private_Dirty");
            DataWriteUtil.writeLong(MetricDataType.LONG, uss
                    , MetricDataKey.PROCESS_MEMORY_USS, dataOutputStream);

        } catch (Exception e) {
            logger.error("fill ProcessMemory error:{}",e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

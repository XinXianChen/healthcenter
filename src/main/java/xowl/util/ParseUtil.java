package xowl.util;


import xowl.model.CompositeData;
import xowl.model.jvm.*;
import xowl.model.os.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 数据协议
 * Data
 *    |- version
 *    |
 *    |- entity(可多个)
 *          |
 *          | - entityType
 *          |
 *          |- fieldInfo
 *          |        |- fieldType
 *          |        |- fieldLength（仅当数据类型是String是存在）
 *          |        |- value
 *          |        |- 字段编号
 *          |
 *          | - endFlag
 *
 * @author xinxian
 * @create 2020-04-22 17:12
 **/
public class ParseUtil {

    public static CompositeData parse(byte[] bytes) {
        CompositeData data = new CompositeData();
        //包装bytes
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        //首先 读取版本号
        final double version = buffer.getDouble();
        if (MetricDataType.VERSION == version) {
            while (buffer.hasRemaining()) {
                //读取实体类型
                int entityType = buffer.getInt();
                if (entityType == MetricDataType.COMPLETE) {
                    //说明已经读取完成
                    break;
                }
                switch (entityType) {
                    case EntityType.JVM_GCUSAGE :
                        data.setGcUsage(readGcUsage(buffer));
                        break;
                    case EntityType.JVM_PROCESS_MEMORY :
                        data.setProcessMemory(readProcessMemory(buffer));
                        break;
                    case EntityType.JVM_PROCESS_THREADINFO :
                        data.setProcessThreadInfo(readProcessThreadInfo(buffer));
                        break;
                    case EntityType.OS :
                        data.setOsInfo(readOsInfo(buffer));
                        break;
                    case EntityType.OS_DISKSTATES :
                        data.addDiskStates(readDiskStates(buffer));
                        break;
                    case EntityType.OS_MEMORY :
                        data.setMemory(readOsMemory(buffer));
                        break;
                    case EntityType.OS_USERS :
                        data.addOsUsers(readUser(buffer));
                        break;
                    case EntityType.OS_PROCESSIO :
                        data.setProcessIo(readProcessIo(buffer));
                        break;
                    case EntityType.OS_PROCESS_LIMIT :
                        data.setProcessLimit(readProcessLimit(buffer));
                        break;
                    case EntityType.OS_UPTIME :
                        data.setUpTime(readUptime(buffer));
                        break;
                    case EntityType.OS_CPU_STAT :
                        data.setCpuStat(readCpustat(buffer));
                        break;
                    case EntityType.OS_DISK :
                        data.addDisks(readDisk(buffer));
                        break;
                    default:
                        break;
                }
            }
        }
        return data;
    }

    private static Disk readDisk(ByteBuffer buffer) {
        Disk disk = new Disk();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    Long value = buffer.getLong();
                    fillDisk(buffer,disk,value);
                    break;
                case MetricDataType.DOUBLE:
                    Double doubleVal = buffer.getDouble();
                    fillDisk(buffer,disk,doubleVal);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    fillDisk(buffer,disk,new String(data,Charset.defaultCharset()));
                    break;    
                case MetricDataType.END:
                    return disk;
                default:
                    break;
            }
        }
        return disk;
    }

    private static void fillDisk(ByteBuffer buffer, Disk disk, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.DISK_FILE_SYSTEM:
                disk.setFileSystem((String) value);
                break;
            case MetricDataKey.DISK_SIZE:
                disk.setSize((Long) value);
                break;
            case MetricDataKey.DISK_USED:
                disk.setUsed((Long) value);
                break;
            case MetricDataKey.DISK_AVAIL:
                disk.setAvail((Long) value);
                break;
            case MetricDataKey.DISK_USE_PERCENT:
                disk.setUsePercent((Double) value);
                break;
            case MetricDataKey.DISK_MOUNTED_ON:
                disk.setMountedOn((String) value);
                break;
            default:
                break;
        }
        
    }

    private static CpuStat readCpustat(ByteBuffer buffer) {
        CpuStat cpuStat = new CpuStat();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    Long value = buffer.getLong();
                    fillCpuStat(buffer,cpuStat,value);
                    break;
                case MetricDataType.END:
                    return cpuStat;
                default:
                    break;
            }
        }
        return cpuStat;
    }

    private static void fillCpuStat(ByteBuffer buffer, CpuStat cpuStat, Long value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.CPU_STAT_USER:
                cpuStat.setUser(value);
                break;
            case MetricDataKey.CPU_STAT_NICE:
                cpuStat.setNice(value);
                break;
            case MetricDataKey.CPU_STAT_SYSTEM:
                cpuStat.setSystem(value);
                break;
            case MetricDataKey.CPU_STAT_IDLE:
                cpuStat.setIdle(value);
                break;
            case MetricDataKey.CPU_STAT_IOWAIT:
                cpuStat.setIowait(value);
                break;
            case MetricDataKey.CPU_STAT_IRQ:
                cpuStat.setIrq(value);
                break;
            case MetricDataKey.CPU_STAT_SOFT_IRQ:
                cpuStat.setSoftirq(value);
                break;
            case MetricDataKey.CPU_STAT_STEAL:
                cpuStat.setSteal(value);
                break;
            case MetricDataKey.CPU_STAT_GUEST:
                cpuStat.setGuest(value);
                break;
            case MetricDataKey.CPU_STAT_GUEST_NICE:
                cpuStat.setNice(value);
                break;
            default:
                break;
        }
    }

    private static UpTime readUptime(ByteBuffer buffer) {
        UpTime upTime = new UpTime();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    Long value = buffer.getLong();
                    fillUptime(buffer,upTime,value);
                    break;
                case MetricDataType.INT:
                    Integer intVal = buffer.getInt();
                    fillUptime(buffer,upTime,intVal);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    fillUptime(buffer,upTime,new String(data,Charset.defaultCharset()));
                    break;
                case MetricDataType.END:
                    return upTime;
                default:
                    break;
            }
        }
        return upTime;

    }

    private static void fillUptime(ByteBuffer buffer, UpTime upTime, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.OS_UPTIME_CURRENT_MS:
                upTime.setCurrentMs((Long) value);
                break;
            case MetricDataKey.OS_UPTIME_UPTIME_SECS:
                upTime.setUptimeSecs((Long) value);
                break;
            case MetricDataKey.OS_UPTIME_LOAD_1_MIN:
                upTime.setLoad1min((Double) value);
                break;
            case MetricDataKey.OS_UPTIME_LOAD_5_MIN:
                upTime.setLoad5min((Double) value);
                break;
            case MetricDataKey.OS_UPTIME_LOAD_15_MIN:
                upTime.setLoad15min((Double) value);
                break;
            case MetricDataKey.OS_UPTIME_RUNNABLE_PROESS_NBR:
                upTime.setRunnableProcessNbr((Integer) value);
                break;
            case MetricDataKey.OS_UPTIME_CURRENT_TOTAL_PROESS_NBR:
                upTime.setCurrentTotalProcessNbr((Integer) value);
                break;
            case MetricDataKey.OS_UPTIME_RECENTLY_PID:
                upTime.setRecentlyPid((Integer) value);
                break;
            default:
                break;
        }

    }

    private static ProcessLimit readProcessLimit(ByteBuffer buffer) {
        ProcessLimit processLimit = new ProcessLimit();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case EntityType.LIMIT_ENTITY:
                    final LimitEntity limitEntity = readLimitEntity(buffer);
                    fillProcessLimit(limitEntity,processLimit);
                    break;
                case MetricDataType.END:
                    return processLimit;
                default:
                    break;
            }
        }
        return processLimit;
    }

    private static void fillProcessLimit(LimitEntity limitEntity, ProcessLimit processLimit) {
        //获取limitEntity的描述
        final String desc = limitEntity.getDesc();
        switch (desc) {
            case "MaxCpuTimeLimit" :
                processLimit.setMaxCpuTimeLimit(limitEntity);
                break;
            case "MaxFileSize" :
                processLimit.setMaxFileSize(limitEntity);
                break;
            case "MaxDataSize" :
                processLimit.setMaxDataSize(limitEntity);
                break;
            case "MaxStackSize" :
                processLimit.setMaxStackSize(limitEntity);
                break;
            case "MaxCoreFileSize" :
                processLimit.setMaxCoreFileSize(limitEntity);
                break;
            case "MaxResidentSet" :
                processLimit.setMaxResidentSet(limitEntity);
                break;
            case "MaxProcesses" :
                processLimit.setMaxProcesses(limitEntity);
                break;
            case "MaxOpenFiles" :
                processLimit.setMaxOpenFiles(limitEntity);
                break;
            case "MaxLockedMemory" :
                processLimit.setMaxLockedMemory(limitEntity);
                break;
            case "MaxAddressSpace" :
                processLimit.setMaxAddressSpace(limitEntity);
                break;
            case "MaxFileLocks" :
                processLimit.setMaxFileLocks(limitEntity);
                break;
            case "MaxPendingSignals" :
                processLimit.setMaxPendingSignals(limitEntity);
                break;
            case "MaxMsgQueueSize" :
                processLimit.setMaxMsgQueueSize(limitEntity);
                break;
            case "MaxNicePriority" :
                processLimit.setMaxNicePriority(limitEntity);
                break;
            case "MaxRealTimePriority" :
                processLimit.setMaxRealTimePriority(limitEntity);
                break;
            case "MaxRealTimeTimeout" :
                processLimit.setMaxRealTimeTimeout(limitEntity);
                break;
            default:
                break;
        }

    }

    private static LimitEntity readLimitEntity(ByteBuffer buffer) {
        LimitEntity limitEntity = new LimitEntity();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    fillLimitEntity(buffer,limitEntity,new String(data,Charset.defaultCharset()));
                    break;
                case MetricDataType.END:
                    return limitEntity;
                default:
                    break;
            }
        }
        return limitEntity;
    }

    private static void fillLimitEntity(ByteBuffer buffer, LimitEntity limitEntity, String value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.OS_LIMIT_ENTITY_SOFT_LIMIT:
                limitEntity.setSoftLimit(value);
                break;
            case MetricDataKey.OS_LIMIT_ENTITY_HARD_LIMIT:
                limitEntity.setHardLimit(value);
                break;
            case MetricDataKey.OS_LIMIT_ENTITY_UNIT:
                limitEntity.setUnit(value);
                break;
            case MetricDataKey.OS_LIMIT_ENTITY_DESC:
                limitEntity.setDesc(value);
                break;
            default:
                break;
        }
    }

    private static ProcessIo readProcessIo(ByteBuffer buffer) {
        ProcessIo processIo = new ProcessIo();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    fillProcessIo(buffer,processIo,value);
                    break;
                case MetricDataType.END:
                    return processIo;
                default:
                    break;
            }
        }
        return processIo;
    }

    private static void fillProcessIo(ByteBuffer buffer, ProcessIo processIo, Long value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.OS_PROCESS_IO_R_CHAR:
                processIo.setrChar(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_W_CHAR:
                processIo.setwChar(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_SYS_CALL_READ_COUNT:
                processIo.setSysCallReadCount(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_SYS_CALL_WRITE_COUNT:
                processIo.setSysCallWriteCount(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_READ_BYTES:
                processIo.setReadBytes(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_WRITE_BYTES:
                processIo.setWriteBytes(value);
                break;
            case MetricDataKey.OS_PROCESS_IO_CAN_CELLED_WRITE_BYTES:
                processIo.setCancelledWriteBytes(value);
                break;
            default:
                break;
        }

    }

    private static OsUser readUser(ByteBuffer buffer) {
        OsUser user = new OsUser();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.INT:
                    //读取字段值
                    int id = buffer.getInt();
                    //完成必要的读取： 字段类型
                    buffer.getInt();
                    user.setId(id);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    String name = new String(data,Charset.defaultCharset());
                    //完成必要的读取： 字段类型
                    buffer.getInt();
                    user.setName(name);
                    break;
                case EntityType.USERGROUP:
                    final UserGroup userGroup = readUserGroup(buffer);
                    user.setMainGroup(userGroup);
                    break;
                case MetricDataType.END:
                    return user;
                default:
                    break;
            }
        }
        return user;

    }

    private static UserGroup readUserGroup(ByteBuffer buffer) {
        UserGroup userGroup = new UserGroup();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.INT:
                    //读取字段值
                    int id = buffer.getInt();
                    //完成必要的读取： 字段类型
                    buffer.getInt();
                    userGroup.setId(id);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    String name = new String(data,Charset.defaultCharset());
                    //完成必要的读取： 字段类型
                    buffer.getInt();
                    userGroup.setName(name);
                    break;
                case MetricDataType.END:
                    return userGroup;
                default:
                    break;
            }
        }
        return userGroup;

    }

    private static Memory readOsMemory(ByteBuffer buffer) {
        Memory memory = new Memory();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    fillMemory(buffer,memory,value);
                    break;
                case MetricDataType.END:
                    return memory;
                default:
                    break;
            }
        }
       return memory;
    }

    private static void fillMemory(ByteBuffer buffer, Memory memory, Long value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.OS_MEMORY_TOTAL_PHYSICAL_MEMORY_SIZE:
                memory.setTotalPhysicalMemorySize(value);
                break;
            case MetricDataKey.OS_MEMORY_FREE_PHYSICAL_MEMORY_SIZE:
                memory.setFreePhysicalMemorySize(value);
                break;
            case MetricDataKey.OS_MEMORY_AVAILABLE_PHYSICAL_MEMORY_SIZE:
                memory.setAvailablePhysicalMemorySize(value);
                break;
            case MetricDataKey.OS_MEMORY_BUFFER_SIZE:
                memory.setBufferSize(value);
                break;
            case MetricDataKey.OS_MEMORY_CACHE_SIZE:
                memory.setCacheSize(value);
                break;
            case MetricDataKey.OS_MEMORY_TOTAL_SWAP_SPACE_SIZE:
                memory.setTotalSwapSpaceSize(value);
                break;
            case MetricDataKey.OS_MEMORY_FREE_SWAP_SPACE_SIZE:
                memory.setFreeSwapSpaceSize(value);
                break;
            case MetricDataKey.OS_MEMORY_DIRTY_SIZE_:
                memory.setDirtySize(value);
                break;
            case MetricDataKey.OS_MEMORY_SHARE_SIZE_:
                memory.setShareSize(value);
                break;
            default:
                break;
        }
    }

    private static DiskState readDiskStates(ByteBuffer buffer) {
        DiskState diskState = new DiskState();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    fillDiskState(buffer,diskState,value);
                    break;
                case MetricDataType.END:
                    return diskState;
                default:
                    break;
            }
        }
        return diskState;
    }

    private static void fillDiskState(ByteBuffer buffer, DiskState diskState, Long value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.DISK_STATE_TOTAL_COMPLETED_READ_NBR:
                diskState.setTotalCompletedReadNbr(value);
                break;
            case MetricDataKey.DISK_STATE_TOTAL_COMPLETED_WRITE_NBR:
                diskState.setTotalCompletedWriteNbr(value);
                break;
            case MetricDataKey.DISK_STATE_READ_MERGED_NBR:
                diskState.setReadMergedNbr(value);
                break;
            case MetricDataKey.DISK_STATE_WRITE_MERGED_NBR:
                diskState.setWriteMergedNbr(value);
                break;
            case MetricDataKey.DISK_STATE_TOTAL_READ_SECTORS_Nbr:
                diskState.setTotalReadSectorsNbr(value);
                break;
            case MetricDataKey.DISK_STATE_TOTAL_WRITE_SECTORS_Nbr:
                diskState.setTotalWriteSectorsNbr(value);
                break;
            case MetricDataKey.DISK_STATE_TOTAL_READ_MS:
                diskState.setTotalReadMs(value);
                break;
            case MetricDataKey.DISK_STATE_TOTAL_WRITE_MS:
                diskState.setTotalWriteMs(value);
                break;
            case MetricDataKey.DISK_STATE_IO_REQUEST_IN_PROGRESS:
                diskState.setIoRequestInProgress(value);
                break;
            case MetricDataKey.DISK_STATE_IO_QUEUE_NOT_EMPTY_MS:
                diskState.setIoQueueNotEmptyMs(value);
                break;
            case MetricDataKey.DISK_STATE_IO_QUEUE_NOT_EMPTY_WEIGHT_MS:
                diskState.setIoQueueNotEmptyWeightMs(value);
                break;
            default:
                break;
        }
    }

    private static Os readOsInfo(ByteBuffer buffer) {
        Os os = new Os();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    String value = new String(data,Charset.defaultCharset());
                    //确定字段类型并设置
                    //读取字段类型
                    final int fieldType = buffer.getInt();
                    switch (fieldType) {
                        case MetricDataKey.OS_VERSION:
                            os.setKernelVersion(value);
                            break;
                        case MetricDataKey.OS_TYPE:
                            os.setOsType(value);
                            break;
                        case MetricDataKey.OS_ARCH:
                            os.setOsArch(value);
                            break;
                        default:
                            break;
                    }
                    break;
                case MetricDataType.END:
                    return os;
                default:
                    break;
            }
        }
        return os;
    }

    private static ProcessThreadInfo readProcessThreadInfo(ByteBuffer buffer) {
        ProcessThreadInfo processThreadInfo = new ProcessThreadInfo();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.INT:
                    //读取字段值
                    int value = buffer.getInt();
                    //确定字段类型并设置
                    //读取字段类型
                    final int fieldType = buffer.getInt();
                    switch (fieldType) {
                        case MetricDataKey.THREAD_INFO_THREAD_COUNT:
                            processThreadInfo.setThreadCount(value);
                            break;
                        case MetricDataKey.THREAD_INFO_DEADLOCK_FLAG:
                            processThreadInfo.setDeadLockFlag(value);
                            break;
                        default:
                            break;
                    }
                    break;
                case MetricDataType.END:
                    return processThreadInfo;
                default:
                    break;
            }
        }
        return processThreadInfo;
    }

    private static ProcessMemory readProcessMemory(ByteBuffer buffer) {
        ProcessMemory processMemory = new ProcessMemory();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    //确定字段类型并设置
                    fillJvmProcessMemory(buffer,processMemory,value);
                    break;
                case EntityType.MEMORY_USAGE:
                    final MemoryUsage memoryUsage = readMemoryUsage(buffer);
                    if (memoryUsage.getType() == 1) {
                        processMemory.setHeapUsage(memoryUsage);
                    } else {
                        processMemory.setNonHeapUsage(memoryUsage);
                    }
                    break;
                case EntityType.MEMORY_POOL:
                    final MemoryPool memoryPool = readMemoryPool(buffer);
                    processMemory.addMemoryPools(memoryPool);
                    break;
                case MetricDataType.END:
                    return processMemory;
                default:
                    break;
            }
        }
        return processMemory;
    }

    private static MemoryUsage readMemoryUsage(ByteBuffer buffer) {
        MemoryUsage memoryUsage = new MemoryUsage();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    //确定字段类型并设置
                    fillMemoryUsage(buffer,memoryUsage,value);
                    break;
                case MetricDataType.INT:
                    //读取字段值
                    Integer intVal = buffer.getInt();
                    //确定字段类型并设置
                    fillMemoryUsage(buffer,memoryUsage,intVal);
                    break;
                case MetricDataType.END:
                    return memoryUsage;
                default:
                    break;
            }
        }
        return memoryUsage;
    }

    private static MemoryPool readMemoryPool(ByteBuffer buffer) {
        MemoryPool memoryPool = new MemoryPool();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    //确定字段类型并设置
                    fillMemoryPool(buffer,memoryPool,value);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    //确定字段类型并设置
                    fillMemoryPool(buffer,memoryPool,new String(data,Charset.defaultCharset()));
                    break;
                case MetricDataType.END:
                    return memoryPool;
                default:
                    break;
            }
        }
        return memoryPool;
    }

    private static void fillMemoryUsage(ByteBuffer buffer, MemoryUsage memoryUsage, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.MEMORY_USAGE_INIT:
                memoryUsage.setInit((Long) value);
                break;
            case MetricDataKey.MEMORY_USAGE_COMMINTTED:
                memoryUsage.setCommitted((Long) value);
                break;
            case MetricDataKey.MEMORY_USAGE_USED:
                memoryUsage.setUsed((Long) value);
                break;
            case MetricDataKey.MEMORY_USAGE_MAX:
                memoryUsage.setMax((Long) value);
                break;
            case MetricDataKey.MEMORY_USAGE_TYPE:
                memoryUsage.setType((Integer) value);
                break;
            default:
                break;
        }

    }

    private static void fillMemoryPool(ByteBuffer buffer, MemoryPool memoryPool, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.MEMORY_POOL_NAME:
                memoryPool.setName((String) value);
                break;
            case MetricDataKey.MEMORY_POOL_TYPE:
                memoryPool.setType((String) value);
                break;
            case MetricDataKey.MEMORY_POOL_USED:
                memoryPool.setUsed((Long) value);
                break;
            case MetricDataKey.MEMORY_POOL_COMMITTED:
                memoryPool.setCommited((Long) value);
                break;
            case MetricDataKey.MEMORY_POOL_MAX:
                memoryPool.setMax((Long) value);
                break;
            default:
                break;
        }

    }

    private static void fillJvmProcessMemory(ByteBuffer buffer, ProcessMemory processMemory, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.PROCESS_MEMORY_VSS:
                processMemory.setVss((Long) value);
                break;
            case MetricDataKey.PROCESS_MEMORY_RSS:
                processMemory.setRss((Long) value);
                break;
            case MetricDataKey.PROCESS_MEMORY_PSS:
                processMemory.setPss((Long) value);
                break;
            case MetricDataKey.PROCESS_MEMORY_USS:
                processMemory.setUss((Long) value);
                break;
            default:
                break;
        }

    }

    private static GcUsage readGcUsage(ByteBuffer buffer) {
        GcUsage gcUsage = new GcUsage();
        while (buffer.hasRemaining()) {
            //读取字段数据类型标识
            int valueType = buffer.getInt();
            switch (valueType) {
                case MetricDataType.LONG:
                    //读取字段值
                    Long value = buffer.getLong();
                    //确定字段类型并设置
                    fillGcusage(buffer,gcUsage,value);
                    break;
                case MetricDataType.DOUBLE:
                    Double doubleValue = buffer.getDouble();
                    //确定字段类型并设置
                    fillGcusage(buffer,gcUsage,doubleValue);
                    break;
                case MetricDataType.STRING:
                    //读取String值的长度
                    final int length = buffer.getInt();
                    byte[] data = new byte[length];
                    buffer.get(data);
                    //确定字段类型并设置
                    fillGcusage(buffer,gcUsage,new String(data, Charset.defaultCharset()));
                    break;
                case MetricDataType.END:
                    return gcUsage;
                default:
                    break;
            }
        }
        return gcUsage;
    }

    private static void fillGcusage(ByteBuffer buffer, GcUsage gcUsage, Object value) {
        //读取字段类型
        final int fieldType = buffer.getInt();
        switch (fieldType) {
            case MetricDataKey.GC_USAGE_JVMUPDATE:
                gcUsage.setJvmUptime((Long) value);
                break;
            case MetricDataKey.GC_USAGE_S0CAPACITY:
                gcUsage.setS0Capacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_S1CAPACITY:
                gcUsage.setS1Capacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_S0USED:
                gcUsage.setS0Used((Long) value);
                break;
            case MetricDataKey.GC_USAGE_S1USED:
                gcUsage.setS1Used((Long) value);
                break;
            case MetricDataKey.GC_USAGE_EDENCAPACITY:
                gcUsage.setEdenCapacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_EDENUSED:
                gcUsage.setEdenUsed((Long) value);
                break;
            case MetricDataKey.GC_USAGE_OLDCAPACITY:
                gcUsage.setOldCapacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_OLDUSED:
                gcUsage.setOldUsed((Long) value);
                break;
            case MetricDataKey.GC_USAGE_METASPACECAPACITY:
                gcUsage.setMetaSpaceCapacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_METASPACEUSED:
                gcUsage.setMetaSpaceUsed((Long) value);
                break;
            case MetricDataKey.GC_USAGE_COMPRESS_CLASSESSPACE_CAPACITY:
                gcUsage.setCompressClassesSpaceCapacity((Long) value);
                break;
            case MetricDataKey.GC_USAGE_COMPRESS_CLASSESSPACE_USED:
                gcUsage.setCompressClassesSpaceUsed((Long) value);
                break;
            case MetricDataKey.GC_USAGE_YGC_COUNT:
                gcUsage.setyGcCount((Long) value);
                break;
            case MetricDataKey.GC_USAGE_YGC_TIME:
                gcUsage.setyGcTime((Double) value);
                break;
            case MetricDataKey.GC_USAGE_FGC_COUNT:
                gcUsage.setfGcCount((Long) value);
                break;
            case MetricDataKey.GC_USAGE_FGC_TIME:
                gcUsage.setfGcTime((Double) value);
                break;
            case MetricDataKey.GC_USAGE_TOTAL_GCTIME:
                gcUsage.setTotalGcTime((Double) value);
                break;
            case MetricDataKey.GC_USAGE_LAST_GCCAUSE:
                gcUsage.setLastGcCause((String) value);
                break;
            case MetricDataKey.GC_USAGE_CURRENTGCCAUSE:
                gcUsage.setCurrentGcCause((String) value);
                break;
            default:
                break;
        }

    }

}

package xowl.service;

import xowl.request.Cmd;

import java.io.DataOutputStream;

/**
 * @author xinxian
 * @create 2020-04-16 14:57
 **/
public interface MetricDataHandler {

    /**
     * 根据cmd来采集指标数据，并将结果写入一个byte数组返回
     * <p>
     * 数据协议
     * * Data
     * *    |- version
     * *    |
     * *    |- entity(可多个)
     * *          |
     * *          | - entityType
     * *          |
     * *          |- fieldInfo
     * *          |        |- fieldType
     * *          |        |- fieldLength（仅当数据类型是String是存在）
     * *          |        |- value
     * *          |        |- 字段编号
     * *          |
     * *          | - endFlag
     */
    byte[] collect();

    /************************ jvm *******************************************/
    void collectGcUsage(DataOutputStream dataOutputStream);

    void collectProcessMemory(DataOutputStream dataOutputStream);

    void collectThreadInfo(DataOutputStream dataOutputStream);

    /************************ OS *******************************************/
    void collectCpuStat(DataOutputStream dataOutputStream);

    void collectDisk(DataOutputStream dataOutputStream);

    void collectDiskState(DataOutputStream dataOutputStream);

    void collectMemoryInfo(DataOutputStream dataOutputStream);

    void collectOs(DataOutputStream dataOutputStream);

    void collectOsUser(DataOutputStream dataOutputStream);

    void collectProcessIo(DataOutputStream pid);

    void collectProcessLimit(DataOutputStream dataOutputStream);

    void collectUpTime(DataOutputStream dataOutputStream);


}

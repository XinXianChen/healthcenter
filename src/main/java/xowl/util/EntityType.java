package xowl.util;

/**
 * @author xinxian
 * @create 2020-04-17 11:42
 **/
public interface EntityType {

    int JVM_GCUSAGE = 10000;
    int JVM_PROCESS_MEMORY = 10001;
    int JVM_PROCESS_THREADINFO = 10002;
    int OS = 10003;
    int OS_DISKSTATES = 10004;
    int OS_MEMORY = 10005;
    int OS_USERS = 10006;
    int OS_PROCESSIO = 10007;
    int OS_PROCESS_LIMIT = 10008;
    int OS_UPTIME = 10009;
    int OS_CPU_STAT = 10010;
    int OS_DISK = 10011;
    //--------------------------------------作为字段的实体
    int LIMIT_ENTITY = 10020;
    int USERGROUP = 10021;
    int MEMORY_USAGE = 10022;
    int MEMORY_POOL = 10023;
}

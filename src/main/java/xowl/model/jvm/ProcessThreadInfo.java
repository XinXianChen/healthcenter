package xowl.model.jvm;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiluo
 * @date 2020/3/31 21:41
 * @Version 1.0
 **/
public class ProcessThreadInfo implements Serializable {
    private static final long serialVersionUID = -2372058742636505291L;
    /**
     * 线程数
     */
    private int threadCount;

    /**
     * 死锁标识
     * 1.true
     * 0:false
     */
    private int deadLockFlag;

    private List<DeadLockThread> deadLocks;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public List<DeadLockThread> getDeadLocks() {
        return deadLocks;
    }

    public void setDeadLocks(List<DeadLockThread> deadLocks) {
        this.deadLocks = deadLocks;
    }

    public int getDeadLockFlag() {
        return deadLockFlag;
    }

    public void setDeadLockFlag(int deadLockFlag) {
        this.deadLockFlag = deadLockFlag;
    }

    public static class DeadLockThread implements Serializable {
        private static final long serialVersionUID = -5658003474190864997L;
        private long tid;
        /**
         * native thread id
         */
        private long nTid;
        private String threadName;
        private List<String> stackTraceLines;

        public long getTid() {
            return tid;
        }

        public void setTid(long tid) {
            this.tid = tid;
        }

        public long getnTid() {
            return nTid;
        }

        public void setnTid(long nTid) {
            this.nTid = nTid;
        }

        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        public List<String> getStackTraceLines() {
            return stackTraceLines;
        }

        public void setStackTraceLines(List<String> stackTraceLines) {
            this.stackTraceLines = stackTraceLines;
        }

        @Override
        public String toString() {
            return "DeadLockThread{" +
                    "tid=" + tid +
                    ", nTid=" + nTid +
                    ", threadName='" + threadName + '\'' +
                    ", stackTraceLines=" + stackTraceLines +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProcessThreadInfo{" +
                "threadCount=" + threadCount +
                ", deadLocks=" + deadLocks +
                '}';
    }
}

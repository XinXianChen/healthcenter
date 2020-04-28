package xowl.model.os;

/**
 * @author xinxian
 * @create 2020-04-23 19:09
 **/
public class CpuStat {
    /**
     * user  (1) Time spent in user mode.
     */
    long user;

    /**
     * nice   (2) Time spent in user mode with low priority (nice).
     */
    long nice;

    /**
     * system (3) Time spent in system mode.
     */
    long system;

    /**
     * idle   (4) Time spent in the idle task.
     *     This value should be USER_HZ times the second entry in the
     *     /proc/uptime pseudo-file.
     */
    long idle;

    /**
     * iowait (since Linux 2.5.41)
     * (5) Time waiting for I/O to complete.  This
     *     value is not reliable, for the following rea‐
     *     sons:
     *
     *     1. The CPU will not wait for I/O to complete;
     *     iowait is the time that a task is waiting for
     *     I/O to complete.  When a CPU goes into idle
     *     state for outstanding task I/O, another task
     *     will be scheduled on this CPU.
     *
     *     2. On a multi-core CPU, the task waiting for I/O
     *     to complete is not running on any CPU, so the
     *     iowait of each CPU is difficult to calculate.
     *
     *     3. The value in this field may decrease in cer‐
     *     tain conditions.
     */
    long iowait;

    /**
     *  (since Linux 2.6.0-test4)
     *     (6) Time servicing interrupts.
     */
    long  irq;

    /**
     * softirq (since Linux 2.6.0-test4)
     *     (7) Time servicing softirqs.
     */
    long softirq;

    /**
     * steal (since Linux 2.6.11)
     *     (8) Stolen time, which is the time spent in
     *     other operating systems when running in a virtu‐alized environment
     */
    long steal;

    /**
     * guest (since Linux 2.6.24)
     *     (9) Time spent running a virtual CPU for guest
     *     operating systems under the control of the Linux kernel.
     */
    long guest;

    /**
     * guest_nice (since Linux 2.6.33)
     *     (10) Time spent running a niced guest (virtual
     *     CPU for guest operating systems under the con‐trol of the Linux kernel).
     */
    long guestNice;

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getNice() {
        return nice;
    }

    public void setNice(long nice) {
        this.nice = nice;
    }

    public long getSystem() {
        return system;
    }

    public void setSystem(long system) {
        this.system = system;
    }

    public long getIdle() {
        return idle;
    }

    public void setIdle(long idle) {
        this.idle = idle;
    }

    public long getIowait() {
        return iowait;
    }

    public void setIowait(long iowait) {
        this.iowait = iowait;
    }

    public long getIrq() {
        return irq;
    }

    public void setIrq(long irq) {
        this.irq = irq;
    }

    public long getSoftirq() {
        return softirq;
    }

    public void setSoftirq(long softirq) {
        this.softirq = softirq;
    }

    public long getSteal() {
        return steal;
    }

    public void setSteal(long steal) {
        this.steal = steal;
    }

    public long getGuest() {
        return guest;
    }

    public void setGuest(long guest) {
        this.guest = guest;
    }

    public long getGuestNice() {
        return guestNice;
    }

    public void setGuestNice(long guestNice) {
        this.guestNice = guestNice;
    }
}

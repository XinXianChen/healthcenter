package xowl.request;


import java.io.Serializable;

/**
 * @author xinxian
 * @create 2020-04-08 15:58
 **/
public class Cmd implements Serializable {


    private static final long serialVersionUID = 6749187332839177475L;
    /**
     * 调用action : collect
     */
    private String action;

    /**
     * 用于指明需要采集的数据域，如os/jvm
     */
    private String domain;

    /**
     * 需要采集的数据 用 ; 分隔
     *
     */
    private String metricDatas;

    /**
     * 进程PID
     */
    private Long pid;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 任务ID
     */
    private Long taskId;

    public Long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMetricDatas() {
        return metricDatas;
    }

    public void setMetricDatas(String metricDatas) {
        this.metricDatas = metricDatas;
    }

    @Override
    public String toString() {
        return "Cmd{" +
                "action='" + action + '\'' +
                ", domain='" + domain + '\'' +
                ", metricDatas='" + metricDatas + '\'' +
                ", pid=" + pid +
                ", machineId=" + machineId +
                ", taskId=" + taskId +
                '}';
    }
}

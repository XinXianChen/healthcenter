package xowl.model.os;

import java.io.Serializable;

/**
 * @author xiluo
 * @date 2020/3/25 22:44
 * @Version 1.0
 **/
public class LimitEntity implements Serializable {
    private static final long serialVersionUID = 3270501543655968667L;

    /**
     * unlimited or numerical
     *
     */
    private String softLimit;

    /**
     * unlimited or numerical
     */
    private String hardLimit;

    /**
     * SECONDS, BYTES, PROCESSES, FILES, LOCKS, SIGNALS, US
     */
    private String unit;

    private String desc;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSoftLimit() {
        return softLimit;
    }

    public void setSoftLimit(String softLimit) {
        this.softLimit = softLimit;
    }

    public String getHardLimit() {
        return hardLimit;
    }

    public void setHardLimit(String hardLimit) {
        this.hardLimit = hardLimit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "LimitEntity{" +
                "softLimit='" + softLimit + '\'' +
                ", hardLimit='" + hardLimit + '\'' +
                ", unit='" + unit + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

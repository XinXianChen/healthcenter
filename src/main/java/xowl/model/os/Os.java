package xowl.model.os;

import java.io.Serializable;

/**
 * @author xiluo
 * @date 2020/3/25 18:24
 * @Version 1.0
 **/
public class Os implements Serializable {
    private static final long serialVersionUID = -2016373909325178363L;
    private String kernelVersion;
    private String osType;
    private String osArch;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getKernelVersion() {
        return kernelVersion;
    }

    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    @Override
    public String toString() {
        return "Os{" +
                "kernelVersion='" + kernelVersion + '\'' +
                ", osType='" + osType + '\'' +
                ", osArch='" + osArch + '\'' +
                '}';
    }
}

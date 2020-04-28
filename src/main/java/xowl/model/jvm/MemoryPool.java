package xowl.model.jvm;

import java.io.Serializable;

/**
 * @author xiluo
 * @date 2020/3/27 10:48
 * @Version 1.0
 **/
public class MemoryPool implements Serializable {
    private static final long serialVersionUID = 7389086678504628755L;
    private String name;

    private String simpleName;

    private String type;

    private Long used;

    private Long commited;

    private Long max;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUsed() {
        return used;
    }

    public void setUsed(Long used) {
        this.used = used;
    }

    public Long getCommited() {
        return commited;
    }

    public void setCommited(Long commited) {
        this.commited = commited;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "MemoryPool{" +
                "name='" + name + '\'' +
                ", simpleName='" + simpleName + '\'' +
                ", type='" + type + '\'' +
                ", used=" + used +
                ", commited=" + commited +
                ", max=" + max +
                '}';
    }
}

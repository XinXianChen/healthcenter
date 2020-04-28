package xowl.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xinxian
 * @create 2020-04-08 20:36
 **/
public class ConfigurationUtil {

    public static Map<String,String> handlerMaps = new HashMap<>();

    static {
        //handlerMap
        handlerMaps.put("os","com.perfma.metric.xowl.service.impl.LinuxMetricDataHandlerImpl");
        handlerMaps.put("jvm","com.perfma.metric.xowl.service.impl.HotSpotMetricDataHandlerImpl");
    }

}

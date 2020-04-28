package xowl;

import com.alibaba.fastjson.JSONObject;
import com.perfma.module.AbstractModule;
import com.perfma.module.trigger.ITriggerAction;
import com.perfma.p2pcaller.model.Request;
import com.perfma.p2pcaller.model.Response;
import com.perfma.xowl.api.Command;
import com.perfma.xowl.api.Information;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xowl.request.Cmd;
import xowl.service.MetricDataHandler;
import xowl.util.ConfigurationUtil;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xinxian
 * @create 2020-04-08 15:42
 **/
@Information(id = "metric", version = "1.0.0", author = "心弦")
public class MetricModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MetricModule.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    @Command("collect")
    public void collect(Request request, Response response) {
        String cmdStr = request.getParams().get("data").toString();
        Cmd cmd = JSONObject.parseObject(cmdStr, Cmd.class);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                doMetricDataCollect(cmd);
            }
        });
    }

    private void doMetricDataCollect(Cmd cmd) {
        //todo 这里有问题 导致了某一次采集 只能采集os或者jvm中的一种数据信息
        MetricDataHandler metricDataHandler = getMetricHandler(cmd);
        if (metricDataHandler == null) {
            logger.error("doMetricDataCollect but metricDataHandler is null");
            return;
        }
        metricDataHandler.collect();
    }


    private MetricDataHandler getMetricHandler(Cmd cmd) {
        final String className = ConfigurationUtil.handlerMaps.get(cmd.getDomain());
        try {
            final Class<?> metricDataHandlerClass = Class.forName(className);
            final Constructor<?> constructor = metricDataHandlerClass.getConstructor(Cmd.class, MetricModule.class);
            return (MetricDataHandler) constructor.newInstance(cmd,this);
        } catch (Exception e) {
            logger.error("getMetricHandler error domain:{},exception:{}",cmd.getDomain(),e);
        }
        return null;
    }


    @Override
    public boolean executeTriggerAction(int i, ITriggerAction iTriggerAction) {
        return false;
    }
}

package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import xowl.MetricModule;
import xowl.model.CompositeData;
import xowl.request.Cmd;
import xowl.service.MetricDataHandler;
import xowl.service.impl.DefaultMetricDataHandlerImpl;
import xowl.util.ParseUtil;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author duqi
 * @createTime 2018/10/19 3:25 PM
 **/
@Component
public class StartTaskChecker implements DisposableBean {

    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private MetricDataHandler handler;


    @PostConstruct
    public void init() {
        String metricDatas = "JVM.ProcessMemory;JVM.GcUsage;JVM.ProcessThreadInfo;OS_CpuStat;OS.Disks;OS.DiskStates;OS.Memory;OS.Users;OS.Info;OS.ProcessIo;OS.ProcessLimit;OS.Uptime";
        Cmd cmd = new Cmd();
        cmd.setPid(23552);
        cmd.setMetricDatas(metricDatas);
        handler = new DefaultMetricDataHandlerImpl(cmd,new MetricModule());
        scheduledExecutorService.scheduleAtFixedRate(this::collect, 0,
                5, TimeUnit.SECONDS);
    }

    private void collect() {
        byte[] collect = handler.collect();
//        s = System.currentTimeMillis();
//        CompositeData parse = ParseUtil.parse(collect);
//        logger.info("parse cost: " + (System.currentTimeMillis() - s) + " ms");
    }



    @Override
    public void destroy() throws Exception {
        scheduledExecutorService.shutdown();
    }
}

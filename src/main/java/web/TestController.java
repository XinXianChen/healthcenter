package web;

import com.perfma.model.common.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xowl.MetricModule;
import xowl.model.CompositeData;
import xowl.request.Cmd;
import xowl.service.MetricDataHandler;
import xowl.service.impl.DefaultMetricDataHandlerImpl;
import xowl.util.ParseUtil;



/**
 * @author xinxian
 * @create 2020-04-24 14:47
 **/
@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(MetricModule.class);
    @GetMapping(value = "/metricData/{pid}/{metricDatas}")
    public ResultInfo<CompositeData> listAvailableModuleName(@PathVariable("pid") Long pid,@PathVariable("metricDatas") String metricDatas) {
        Cmd cmd = new Cmd();
        cmd.setPid(pid);
        cmd.setMetricDatas(metricDatas);
        MetricDataHandler handler = new DefaultMetricDataHandlerImpl(cmd,new MetricModule());
        long s = System.currentTimeMillis();
        byte[] collect = handler.collect();
        logger.info("colllect cost " + (System.currentTimeMillis() - s) +" ms");

        s = System.currentTimeMillis();
        CompositeData parse = ParseUtil.parse(collect);
        logger.info("parse cost: " + (System.currentTimeMillis() - s) + " ms");

        return ResultInfo.buildSuccess(parse);
    }


}

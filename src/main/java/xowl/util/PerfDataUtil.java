package xowl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.management.counter.Counter;
import sun.management.counter.LongCounter;
import sun.management.counter.StringCounter;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author xinxian
 * @create 2020-04-08 16:24
 **/
public class PerfDataUtil {

    private static final Logger logger = LoggerFactory.getLogger(PerfDataUtil.class);

    private PerfInstrumentation perfInstrumentation;

    public static PerfDataUtil connect(long pid) {
        try {
            return new PerfDataUtil((int) pid);
        } catch (Throwable t) {
            logger.warn("Perf is not supported", t);
        }
        return null;
    }

    private PerfDataUtil(int pid) throws IOException {
        ByteBuffer bb = Perf.getPerf().attach(pid, "r");
        perfInstrumentation = new PerfInstrumentation(bb);
    }

    public void getGcUsage(DataOutputStream dataOutputStream) {
        try {
            getLongVal("sun.gc.generation.0.space.1.capacity",dataOutputStream, MetricDataKey.GC_USAGE_S0CAPACITY);

            getLongVal("sun.gc.generation.0.space.2.capacity",dataOutputStream,MetricDataKey.GC_USAGE_S1CAPACITY);

            getLongVal("sun.gc.generation.0.space.1.used",dataOutputStream,MetricDataKey.GC_USAGE_S0USED);

            getLongVal("sun.gc.generation.0.space.2.used",dataOutputStream,MetricDataKey.GC_USAGE_S1USED);

            getLongVal("sun.gc.generation.0.space.0.capacity",dataOutputStream,MetricDataKey.GC_USAGE_EDENCAPACITY);

            getLongVal("sun.gc.generation.0.space.0.used",dataOutputStream,MetricDataKey.GC_USAGE_EDENUSED);

            getLongVal("sun.gc.generation.1.space.0.capacity",dataOutputStream,MetricDataKey.GC_USAGE_OLDCAPACITY);

            getLongVal("sun.gc.generation.1.space.0.used",dataOutputStream,MetricDataKey.GC_USAGE_OLDUSED);

            getLongVal("sun.gc.metaspace.capacity",dataOutputStream,MetricDataKey.GC_USAGE_METASPACECAPACITY);

            getLongVal("sun.gc.metaspace.used",dataOutputStream,MetricDataKey.GC_USAGE_METASPACEUSED);

            getLongVal("sun.gc.compressedclassspace.capacity",dataOutputStream,MetricDataKey.GC_USAGE_COMPRESS_CLASSESSPACE_CAPACITY);

            getLongVal("sun.gc.compressedclassspace.used",dataOutputStream,MetricDataKey.GC_USAGE_COMPRESS_CLASSESSPACE_USED);

            getLongVal("sun.gc.collector.0.invocations",dataOutputStream,MetricDataKey.GC_USAGE_YGC_COUNT);

            long yGcTotalTime = ((LongCounter) perfInstrumentation.findByPattern("sun.gc.collector.0.time").get(0)).longValue();
            long frequency = ((LongCounter) perfInstrumentation.findByPattern("sun.os.hrt.frequency").get(0)).longValue();
            double yGcTime = (yGcTotalTime * 1.0) / frequency;
            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, yGcTime, MetricDataKey.GC_USAGE_YGC_TIME, dataOutputStream);

            getLongVal("sun.gc.collector.1.invocations",dataOutputStream,MetricDataKey.GC_USAGE_FGC_COUNT);

            long fGcTotalTime = ((LongCounter) perfInstrumentation.findByPattern("sun.gc.collector.1.time").get(0)).longValue();
            double fGcTime = (fGcTotalTime * 1.0) / frequency;
            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, fGcTime, MetricDataKey.GC_USAGE_FGC_TIME, dataOutputStream);

            double totalGcTime = (yGcTotalTime + fGcTotalTime) / (frequency * 1.0);
            DataWriteUtil.writeDouble(MetricDataType.DOUBLE, totalGcTime, MetricDataKey.GC_USAGE_TOTAL_GCTIME, dataOutputStream);

            String lastGcCause = ((StringCounter) perfInstrumentation.findByPattern("sun.gc.lastCause").get(0)).stringValue();
            DataWriteUtil.writeString(MetricDataType.STRING, lastGcCause, MetricDataKey.GC_USAGE_LAST_GCCAUSE, dataOutputStream);

            //写入结束标识
            dataOutputStream.writeInt(MetricDataType.END);

        } catch (IOException e) {
            logger.error("get GcUsage error:{}",e);
        }
    }

    private void getLongVal(String key, DataOutputStream dataOutputStream, int fieldType) throws IOException {
        final List<Counter> counters = perfInstrumentation.findByPattern(key);
        if (counters != null && counters.size() > 0) {
            DataWriteUtil.writeLong(MetricDataType.LONG, ((LongCounter) counters.get(0)).longValue(), fieldType, dataOutputStream);
        } else {
            logger.error(" Value does not exist key:{}",key);
        }
    }
}

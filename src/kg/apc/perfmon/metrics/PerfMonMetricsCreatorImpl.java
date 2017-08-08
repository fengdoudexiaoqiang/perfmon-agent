package kg.apc.perfmon.metrics;

import kg.apc.perfmon.metrics.jmx.JMXConnectorHelper;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.hyperic.sigar.SigarProxy;

public class PerfMonMetricsCreatorImpl implements PerfMonMetricsCreator {

    private static final Logger log = LoggingManager.getLoggerForClass();

    public AbstractPerfMonMetric getMetricProvider(String metricType, MetricParamsSigar metricParams, SigarProxy sigarProxy, boolean isNoExec) {
        AbstractPerfMonMetric metric;
        if (isNoExec && (metricType.equalsIgnoreCase("exec") || metricType.equalsIgnoreCase("tail"))) {
            throw new RuntimeException("Agent started in safe mode, 'exec' and 'tail' metrics are not available");
        } else if (metricType.equalsIgnoreCase("exec")) {
            metric = new ExecMetric(metricParams);
        } else if (metricType.equalsIgnoreCase("tail")) {
            metric = new TailMetric(metricParams);
        } else if (metricType.equalsIgnoreCase("cpu")) {
            metric = AbstractCPUMetric.getMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("memory")) {
            metric = AbstractMemMetric.getMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("swap")) {
            metric = new SwapMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("disks")) {
            metric = new DiskIOMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("network")) {
            metric = new NetworkIOMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("tcp")) {
            metric = new TCPStatMetric(sigarProxy, metricParams);
        } else if (metricType.equalsIgnoreCase("jmx")) {
            metric = new JMXMetric(metricParams, new JMXConnectorHelper());
        } else {
            metric = new InvalidPerfMonMetric();
            throw new RuntimeException("No collector object for metric type " + metricType);
        }
        return metric;
    }
}

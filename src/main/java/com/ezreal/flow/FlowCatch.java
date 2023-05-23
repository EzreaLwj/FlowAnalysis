package com.ezreal.flow;

import com.ezreal.utils.FlowAnalysisUtils;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FlowCatch extends AbstractFlowCatch {

    private final static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,
            15,
            1000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(20),
            new ThreadPoolExecutor.CallerRunsPolicy());


    @Override
    public void beginFlowCatch() {
        System.out.println("开始抓包...");
        List<PcapIf> pcapIfs = FlowAnalysisUtils.getPcapIfs();
        for (PcapIf pcapIf : pcapIfs) {
            threadPoolExecutor.execute(() -> capturePcap(pcapIf));
        }
    }

    @Override
    public void endFlowCatch() {
        threadPoolExecutor.shutdownNow();
        System.out.println("结束抓包...");
    }

    public static void capturePcap(PcapIf device) {
        //截断此大小的数据包
        int snaplen = Pcap.DEFAULT_JPACKET_BUFFER_SIZE;

        int promiscous = Pcap.MODE_PROMISCUOUS;

        //以毫秒为单位
        int timeout = 60 * 1000;
        //如果发生错误，它将保存一个错误字符串。 错误打开 Live 将返回 null
        StringBuilder errbuf = new StringBuilder();

        Pcap pcap = Pcap.openLive(device.getName(), snaplen, promiscous, timeout, errbuf);
        if (pcap == null) {
            System.err.println("获取数据包失败：" + errbuf.toString());
        }

        CustomPcapHandler<Object> handler = new CustomPcapHandler<Object>();
        // 捕获数据包计数
        int cnt = 1;
        //我们要发送到处理程序的自定义对象
        PrintStream out = System.out;
        while (true) {
//            每个数据包将被分派到抓包处理器Handler
            pcap.loop(cnt, handler, out);
        }
//        pcap.close()
    }

}

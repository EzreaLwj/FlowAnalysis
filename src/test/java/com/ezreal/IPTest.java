package com.ezreal;

import com.ezreal.utils.FlowAnalysisUtils;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.junit.Test;
import org.openqa.selenium.support.ui.FluentWait;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPTest {

    @Test
    public void getLocalHost() throws UnknownHostException {
        PcapIf pcapIfByIp = FlowAnalysisUtils.getPcapIfByIp();
        System.out.println(pcapIfByIp);
    }

    @Test
    public void grap() {
        List<PcapIf> pcapIfs = FlowAnalysisUtils.getPcapIfs();
        for (PcapIf pcapIf : pcapIfs) {
            System.out.println(pcapIf);
        }
    }

    @Test
    public void flowCatch() {
        List<PcapIf> alldevs = new ArrayList<>();
        StringBuilder errbuf = new StringBuilder();

        // 获取本机网卡设备列表
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r != Pcap.OK) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }

        // 选择要抓包的网卡设备
        PcapIf device = alldevs.get(0);
        System.out.printf("Choosing '%s' on your behalf:\n",
                (device.getDescription() != null) ? device.getDescription()
                        : device.getName());

        // 打开选定的网卡设备
        int snaplen = 64 * 1024;
        int flags = Pcap.MODE_PROMISCUOUS;
        int timeout = 10 * 1000;
        Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        // 设置抓包过滤规则，只捕获 HTTP 请求
        PcapBpfProgram filter = new PcapBpfProgram();
        String expression = "tcp port 80 and (tcp[((tcp[12] & 0xf0) >> 2)] = 0x47455420)";
        int optimize = 0;
        int netmask = 0xFFFFFF00;
        if (pcap.compile(filter, expression, optimize, netmask) != Pcap.OK) {
            System.err.println(pcap.getErr());
            return;
        }
        if (pcap.setFilter(filter) != Pcap.OK) {
            System.err.println(pcap.getErr());
            return;
        }

        // 开始抓包
        System.out.println("Starting capture...");
        pcap.loop(Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {
            final Http http = new Http();
            final Ip4 ip = new Ip4();
            @Override
            public void nextPacket(JPacket packet, StringBuilder errbuf) {
                if (packet.hasHeader(http) && packet.hasHeader(ip)) {
                    String source = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
                    String destination = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
                    String uri = http.fieldValue(Http.Request.RequestUrl);
                    System.out.printf("%s -> %s : %s\n", source, destination, uri);
                }
            }
        }, errbuf);

        // 关闭抓包
        System.out.println("Capture stopped.");
        pcap.close();
    }
}

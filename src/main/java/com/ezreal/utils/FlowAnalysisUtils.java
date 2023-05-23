package com.ezreal.utils;

import cn.hutool.core.net.NetUtil;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class FlowAnalysisUtils {

    /**
     * 获取网卡接口
     *
     * @return 网卡信息
     */
    public static List<PcapIf> getPcapIfs() {
        List<PcapIf> pcapIfs = new ArrayList<>();
        StringBuilder errMsg = new StringBuilder();
        int devs = Pcap.findAllDevs(pcapIfs, errMsg);

        if (devs != Pcap.OK) {
            System.out.printf("获取网卡失败：%s", errMsg);
            return null;
        }
        return pcapIfs;
    }

    /**
     * 根据 ip 获取网卡
     *
     * @return
     */
    public static PcapIf getPcapIfByIp() {
        List<PcapIf> pcapIfs = getPcapIfs();
        String wifiAddress = getWifiAddress();
        PcapIf pcap = null;
        for (PcapIf pcapIf : pcapIfs) {
            String inet = pcapIf.getAddresses().get(0).getAddr().toString();
            String ip = inet.substring(1, inet.length() - 1).split(":")[1];
            if (wifiAddress.equals(ip)) {
                pcap = pcapIf;
                break;
            }
        }
        return pcap;
    }

    /**
     * 获取本机连接 WiFi 的地址
     *
     * @return ip 地址
     */
    public static String getWifiAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.isSiteLocalAddress()) {
                            ip = address.getHostAddress();
                            return ip;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 获取
     *
     * @return
     */
    public static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

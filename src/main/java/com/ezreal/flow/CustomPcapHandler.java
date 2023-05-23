package com.ezreal.flow;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;


public class CustomPcapHandler<Object> implements PcapPacketHandler<Object> {


    // todo 把打印的内容读入到文件中，再对文件进行处理
    @Override
    public void nextPacket(PcapPacket pcapPacket, Object object) {

        System.out.println(pcapPacket);

//        if (pcapPacket.hasHeader(Http.ID)) {
//            Http http = new Http();
//            pcapPacket.getHeader(http);
//            // 打印请求头
//            System.out.println(http);
//            // 打印请求体
//            System.out.println(new String(http.getPayload()));
//        }
    }
}

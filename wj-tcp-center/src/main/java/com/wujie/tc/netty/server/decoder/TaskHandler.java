package com.wujie.tc.netty.server.decoder;


import com.alibaba.fastjson.JSONObject;
import com.wujie.tc.netty.pojo.Device;
import com.wujie.tc.netty.protocol.WjProtocol;
import com.wujie.tc.netty.server.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.util.ObjectUtils;

public class TaskHandler {

    public void doProtocol(ChannelHandlerContext ctx, WjProtocol wjProtocol) {
        JSONObject objParam = null;
        String tx = "";

        if(WjProtocol.FORMAT_TX.equals(wjProtocol.getFormat())){
            if(wjProtocol.getUserdata() != null){

                String dataStr = new String(wjProtocol.getUserdata());
                tx = dataStr;
            }
        }else if(WjProtocol.FORMAT_JS.equals(wjProtocol.getFormat())){
            if(wjProtocol.getUserdata() != null){

                String jsonStr = new String(wjProtocol.getUserdata());
                objParam = JSONObject.parseObject(jsonStr);
            }
        }else if(WjProtocol.FORMAT_AT.equals(wjProtocol.getFormat())){
            if(wjProtocol.getUserdata() != null){

                tx = new String(wjProtocol.getUserdata());
            }
        }

        //======业务处理======
        if(wjProtocol.byte2shortSmall(wjProtocol.getMaincmd()) == 0 && wjProtocol.byte2shortSmall(wjProtocol.getSubcmd()) == 0x01){//终端→服务 登录
            this.nettyLogin(ctx,objParam);
        }
        if(wjProtocol.byte2shortSmall(wjProtocol.getMaincmd()) == 0 && wjProtocol.byte2shortSmall(wjProtocol.getSubcmd()) == 0){//终端→服务 心跳ping
            this.nettyIdle(ctx);
        }
    }

    private void nettyLogin(ChannelHandlerContext ctx, JSONObject objParam) {
        if(ObjectUtils.isEmpty(objParam)){
            ctx.channel().close();
        }
//        JSONObject jsonObject = JSONObject.parseObject(data.toString());
        Object userId1 = objParam.get("oid");
//        String oid = JSON.parseObject(userId1.toString(), String.class);
        String oid = (String) userId1;
//        UserInfoVo userInfoVo = appUserService.getUserInfoById(userId);
        Device device = new Device(oid,System.currentTimeMillis()+"");
        if (ChannelManager.deviceChannels.containsKey(device.getUniqueNo())){
            Channel channel = ChannelManager.deviceChannels.get(device.getUniqueNo());
            // TODO 需要定义返回的JSON格式，通知用户被挤下去了
            channel.close();
        }
        ctx.channel().attr(ChannelManager.deviceInfoVoAttr).set(device);
        ChannelManager.deviceChannels.put(device.getUniqueNo(), ctx.channel());
    }
    private void nettyIdle(ChannelHandlerContext ctx) {
            sendIdle(ctx);
    }

    private void sendIdle(ChannelHandlerContext ctx) {
        WjProtocol wjProtocol = new WjProtocol();
        wjProtocol.setPlat(new byte[]{0x20,0x00});
        wjProtocol.setMaincmd(new byte[]{0x00,0x00});
        wjProtocol.setSubcmd(new byte[]{0x00,0x00});
        wjProtocol.setFormat("TX");
        wjProtocol.setBack(new byte[]{0x00,0x00});

        int len = WjProtocol.MIN_DATA_LEN + 0;
        wjProtocol.setLen(wjProtocol.short2byte((short) len));

        ctx.write(wjProtocol);

        ctx.flush();
    }
}

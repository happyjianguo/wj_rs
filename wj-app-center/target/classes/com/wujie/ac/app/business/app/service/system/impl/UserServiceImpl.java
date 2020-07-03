package com.wujie.ac.app.business.app.service.system.impl;

import com.wujie.ac.app.business.app.service.system.UserService;
import com.wujie.ac.app.business.entity.Device;
import com.wujie.ac.app.business.entity.Node;
import com.wujie.ac.app.business.entity.NodeStandby;
import com.wujie.ac.app.business.entity.Wjuser;
import com.wujie.ac.app.business.repository.DeviceMapper;
import com.wujie.ac.app.business.repository.NodeMapper;
import com.wujie.ac.app.business.repository.NodeStandbyMapper;
import com.wujie.ac.app.business.repository.WjuserMapper;
import com.wujie.ac.app.business.util.MDA;
import com.wujie.ac.app.business.util.NumConvertUtil;
import com.wujie.ac.app.business.util.date.DateUtil;
import com.wujie.common.base.ApiResult;
import com.wujie.common.dto.DeviceVo;
import com.wujie.common.dto.NodeVo;
import com.wujie.common.dto.wj.NodeDto;
import com.wujie.common.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private DeviceMapper deviceMapper;
    private NodeMapper nodeMapper;
    private NodeStandbyMapper nodeStandbyMapper;
    private WjuserMapper wjuserMapper;

    @Autowired
    public UserServiceImpl(NodeStandbyMapper nodeStandbyMapper, NodeMapper nodeMapper, WjuserMapper wjuserMapper,   DeviceMapper deviceMapper) {
        this.nodeStandbyMapper = nodeStandbyMapper;
        this.nodeMapper = nodeMapper;
        this.deviceMapper = deviceMapper;
        this.wjuserMapper = wjuserMapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult deviceRegist(Long userId, String deviceSelected, String deviceName, String ip, String port, Long nodeId) {
        Wjuser wjuser = wjuserMapper.selectByPrimaryKey(userId);
        if (wjuser == null)
            return ApiResult.error(ErrorEnum.NOT_USER_ERR);

        Device device = new Device();
        device.setUserId(userId);
        device.setDeviceType(Integer.valueOf(deviceSelected));
        device.setDeviceName(deviceName);
        device.setIp(ip);
        device.setPort(port);
        device.setCreatTime(DateUtil.getDate());

        //生成泛在网编号
        String fzwNo = "";

        String country = "chn";
        String area = wjuser.getIdcard().substring(0, 6);
        String timeStr = DateUtil.getDateTime("yyyyMMdd");
        String seqno = "";//序列号表示该国家该地区当天注册的序号，以16进制字符串的形式表现，如FFFF表示65535号
        String catMaxFzwno = deviceMapper.findByFzwnoLikeCAT(country + area + timeStr);
        if (null == catMaxFzwno) {
            seqno = NumConvertUtil.IntToHexStringLimit4(1);
        } else {
            String limitPre = catMaxFzwno.substring(0, 21).substring(17);
            int seqInt = NumConvertUtil.HexStringToInt(limitPre);
            seqno = NumConvertUtil.IntToHexStringLimit4(seqInt + 1);//生成当前序号
            if (seqno == null)
                return ApiResult.error(ErrorEnum.ERR_SEQNO_MAX);
        }
        int userType = wjuser.getUserType();//1表示个人2团体、公司等

        fzwNo = country + area + timeStr + seqno + userId;

        device.setFzwno(fzwNo);
        deviceMapper.insertSelective(device);

        DeviceVo deviceVo = new DeviceVo();
        deviceVo.setIp("");
        deviceVo.setPort("");
        deviceVo.setFzwno(fzwNo);

        //增加下级节点
        if (MDA.numEnum.ZERO.ordinal() == Integer.valueOf(deviceSelected)) {
            Node preNode = nodeMapper.selectByPrimaryKey(nodeId);
            NodeStandby preNodeStandby = nodeStandbyMapper.findByNodeAndType(nodeId, MDA.numEnum.ZERO.ordinal());
            deviceVo.setIp(preNodeStandby.getIp());
            deviceVo.setPort(preNodeStandby.getPort());

            nodeMapper.updateRgt(preNode.getRgt());
            nodeMapper.updateLft(preNode.getRgt());

            Node currNode = new Node();
            currNode.setName(deviceName);
            currNode.setLft(preNode.getRgt());
            currNode.setRgt(preNode.getRgt() + 1);
            currNode.setCreatTime(DateUtil.getDate());
            nodeMapper.insertSelective(currNode);

            NodeStandby nodeStandby = new NodeStandby();
            nodeStandby.setNodeId(currNode.getId());
            nodeStandby.setDeviceId(device.getId());
            nodeStandby.setIp(ip);
            nodeStandby.setPort(port);
            nodeStandby.setType(MDA.numEnum.ZERO.ordinal());//默认设置为当前节点的主服务器
            nodeStandby.setCreatTime(DateUtil.getDate());
            nodeStandbyMapper.insertSelective(nodeStandby);
        }

        log.info("设备注册成功" + fzwNo);

        return ApiResult.success(deviceVo);
    }


    @Override
    public ApiResult getChildNodes(Long nodeId) {
        List<NodeDto> childDtos = new ArrayList<>();
        Node node = nodeMapper.selectByPrimaryKey(nodeId);
        List<Node> childs = nodeMapper.getChildNodes(node.getLft(), node.getRgt());
        for(Node n:childs){
            NodeDto d = new NodeDto();
            BeanUtils.copyProperties(n, d);
            childDtos.add(d);
        }

        return ApiResult.success(childDtos);
    }

    @Override
    public ApiResult getTreeData(Long nodeId) {
        NodeVo nodeVo = new NodeVo();
        List<NodeVo> list = nodeMapper.getAllChildNodeVosLayer();
        if (list.size() > 0) {
            Map<Integer, List<NodeVo>> map = list.stream().collect(Collectors.groupingBy(NodeVo::getLayer));
            int mapSize = map.size();
            if (mapSize > 0) {
                int firstLayer = 1;
                for (int i = firstLayer; i <= mapSize; i++) {
                    List<NodeVo> list0 = map.get(i);
                    int j = i + 1;
                    if (j > mapSize)
                        break;
                    List<NodeVo> list1 = map.get(j);
                    for (NodeVo parent : list0) {
                        if (!parent.getName().contains(":"))
                            parent.setName(parent.getName() + "(" + parent.getIp() + ":" + parent.getPort() + ")");
                        for (NodeVo child : list1) {
                            if (parent.getRgt() > child.getRgt() && parent.getLft() < child.getLft()) {
                                parent.getChildren().add(child);
                                if (!child.getName().contains(":"))
                                    child.setName(child.getName() + "(" + child.getIp() + ":" + child.getPort() + ")");
                            }
                        }
                    }
                }
                nodeVo = map.get(firstLayer).get(0);
                log.debug(nodeVo.toString());
            }
        }

        return ApiResult.success(nodeVo);
    }

}
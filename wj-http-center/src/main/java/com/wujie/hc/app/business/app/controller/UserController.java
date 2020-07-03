package com.wujie.hc.app.business.app.controller;

import com.wujie.common.base.ApiResult;
import com.wujie.common.dto.ResultVo;
import com.wujie.fclient.service.DispatchUserService;
import com.wujie.fclient.service.TcpUserService;
import com.wujie.hc.app.business.app.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserController
 * @Description: 用户控制层
 * @Date: 2020/4/21 14:27
 */
@RestController
public class UserController {

    private DispatchUserService dispatchUserService;
    private TcpUserService tcpUserService;
    private UserService userService;

    @Autowired
    public UserController(DispatchUserService dispatchUserService,UserService userService,TcpUserService tcpUserService) {
        this.dispatchUserService = dispatchUserService;
        this.tcpUserService = tcpUserService;
        this.userService = userService;
    }

    @PostMapping("/deviceRegist")
    public ApiResult deviceRegist(@RequestParam(value = "userId") Long userId,
                                  @RequestParam(value = "deviceSelected") String deviceSelected,
                                  @RequestParam(value = "deviceName") String deviceName,
                                  @RequestParam(value = "ip") String ip,
                                  @RequestParam(value = "port") String port,
                                  @RequestParam(value = "nodeId") Long nodeId) {
            return userService.deviceRegist(userId, deviceSelected, deviceName, ip, port, nodeId);
    }

    @PostMapping("/getChildNodes")
    public ApiResult getChildNodes(@RequestParam(value = "nodeId") Long nodeId) {
        return userService.getChildNodes(nodeId);
    }

    @PostMapping("/getTreeData")
    public ApiResult getTreeData(@RequestParam(value = "nodeId") Long nodeId) {
        return tcpUserService.getTreeData(nodeId);
    }

    @PostMapping("/userRegist")
    public ApiResult userRegist(@RequestParam(value = "username") String username,
                                       @RequestParam(value = "password") String password,
                                       @RequestParam(value = "idcard") String idcard,
                                       @RequestParam(value = "phone") String phone,
                                       @RequestParam(value = "userSelected") String userSelected) {
        return userService.userRegist(username, password, idcard, phone, userSelected);
    }

    @PostMapping("/userLogin")
    public ApiResult userLogin(@RequestParam(value = "username") String username,
                                      @RequestParam(value = "password") String password
    ) {
        return userService.userLogin(username, password);
    }
}
package org.example.shoppingserver.service;


import org.example.shoppingserver.model.dto.user.UserAddressDTO;
import org.example.shoppingserver.model.dto.user.UserDTO;

import org.example.shoppingserver.model.vo.user.UserAddressVO;
import org.springframework.stereotype.Service;
import org.example.shoppingserver.model.vo.user.UserVO;

/**
 * 用户Service接口
 */

public interface UserService {
    /**
     * 获取当前登录用户
     *
     * @return 用户信息
     */
    UserVO getCurrentUser();

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(String userId);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param userDTO 用户DTO
     * @return 用户信息
     */
    UserVO updateUser(String userId, UserDTO userDTO);


    /**
     * 获取用户收货地址列表
     *
     * @param userId 用户ID
     * @return 收货地址列表
     */
    java.util.List<UserAddressVO> getAddresses(String userId);

    /**
     * 获取收货地址详情
     *
     * @param addressId 地址ID
     * @return 收货地址
     */
    UserAddressVO getAddressById(Long addressId);

    /**
     * 添加收货地址
     *
     * @param userId     用户ID
     * @param addressDTO 地址DTO
     * @return 地址信息
     */
    UserAddressVO addAddress(String userId, UserAddressDTO addressDTO);

    /**
     * 更新收货地址
     *
     * @param userId     用户ID
     * @param addressId  地址ID
     * @param addressDTO 地址DTO
     * @return 地址信息
     */
    UserAddressVO updateAddress(String userId, Long addressId, UserAddressDTO addressDTO);

    /**
     * 删除收货地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 是否成功
     */
    boolean deleteAddress(String userId, Long addressId);

    /**
     * 设置默认收货地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 是否成功
     */
    boolean setDefaultAddress(String userId, Long addressId);

}

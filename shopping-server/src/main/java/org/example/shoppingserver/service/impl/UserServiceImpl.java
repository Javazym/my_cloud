package org.example.shoppingserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.user.UserAddressDTO;
import org.example.shoppingserver.model.dto.user.UserDTO;

import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.model.entity.UserAddress;
import org.example.shoppingserver.model.vo.user.UserAddressVO;
import org.example.shoppingserver.repository.UserAddressRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.shoppingserver.model.vo.user.UserVO;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;

    @Override
    @Cacheable(value = "currentUser", key = "T(org.example.shoppingserver.common.UserHolder).getCurrentUserId()", unless = "#result == null")
    public UserVO getCurrentUser() {
        String userId = UserHolder.getCurrentUserId();
        log.info("获取当前用户信息, userId: {}", userId);
        return toVO(Objects.requireNonNull(userRepository.findById(userId).orElse(null)));
    }

    private UserVO toVO(User user) {
        log.info("user: {}", user.getBalance());
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setBirthday(user.getBirthday());
        vo.setGender(user.getGender());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setStatus(user.getStatus());
        vo.setBalance(user.getBalance());
        vo.setNickname(user.getNickname());
        log.info(vo.toString());
        return vo;
    }

    private UserAddressVO toAddressVO(UserAddress userAddress) {
        UserAddressVO vo = new UserAddressVO();
        vo.setId(userAddress.getId());
        vo.setDetailAddress(userAddress.getDetailAddress());
        vo.setFullAddress(userAddress.getFullAddress());
        vo.setCity(userAddress.getCity());
        vo.setDistrict(userAddress.getDistrict());
        vo.setReceiverName(userAddress.getReceiverName());
        vo.setReceiverPhone(userAddress.getReceiverPhone());
        vo.setProvince(userAddress.getProvince());
        vo.setIsDefault(userAddress.getIsDefault());
        return vo;
    }

    private UserAddress toEntity(UserAddressDTO userAddressDTO, String userId) {
        UserAddress address = new UserAddress();
        address.setUser(userRepository.findById(userId).orElse(null));
        address.setDetailAddress(userAddressDTO.getDetailAddress());
        address.setCity(userAddressDTO.getCity());
        address.setDistrict(userAddressDTO.getDistrict());
        address.setReceiverName(userAddressDTO.getReceiverName());
        address.setReceiverPhone(userAddressDTO.getReceiverPhone());
        address.setProvince(userAddressDTO.getProvince());
        address.setIsDefault(userAddressDTO.getIsDefault());
        return address;
    }

    @Override
    @Cacheable(value = "user", key = "#userId", unless = "#result == null")
    public UserVO getUserById(String userId) {
        return toVO(Objects.requireNonNull(userRepository.findById(userId).orElse(null)));
    }

    @Override
    @CacheEvict(value = {"user", "currentUser"}, allEntries = true)
    public UserVO updateUser(String userId, UserDTO userDTO) {
        User user = userRepository.findById(userId).orElse(null);
        user.setEmail(userDTO.getEmail());
        user.setAvatar(userDTO.getAvatar());
        user.setBirthday(userDTO.getBirthday());
        user.setGender(userDTO.getGender());
        user.setStatus(userDTO.getStatus());
        user.setBalance(userDTO.getBalance());
        user.setNickname(userDTO.getNickname());
        userRepository.save(user);
        return null;
    }

    @Override
    @Cacheable(value = "userAddresses", key = "#userId", unless = "#result == null || #result.isEmpty()")
    public List<UserAddressVO> getAddresses(String userId) {
        return userAddressRepository.findByUserId(userId).stream().map(
                address -> toAddressVO(address)
        ).collect(Collectors.toList());
    }

    @Override
    public UserAddressVO getAddressById(Long addressId) {
        return null;
    }

    @Transactional
    @Override
    @CacheEvict(value = "userAddresses", key = "#userId")
    public UserAddressVO addAddress(String userId, UserAddressDTO addressDTO) {
        userAddressRepository.save(toEntity(addressDTO, userId));
        if (addressDTO.getIsDefault() == 1) {
            setDefaultAddress(userId, addressDTO.getId());
        }
        return null;
    }
    @Transactional
    @Override
    @CacheEvict(value = "userAddresses", key = "#userId")
    public UserAddressVO updateAddress(String userId, Long addressId, UserAddressDTO addressDTO) {
        UserAddress userAddress = userAddressRepository.findByUserIdAndId(userId, addressId);
        userAddress.setDetailAddress(addressDTO.getDetailAddress());
        userAddress.setCity(addressDTO.getCity());
        userAddress.setDistrict(addressDTO.getDistrict());
        userAddress.setReceiverName(addressDTO.getReceiverName());
        userAddress.setReceiverPhone(addressDTO.getReceiverPhone());
        userAddress.setProvince(addressDTO.getProvince());
        userAddress.setIsDefault(addressDTO.getIsDefault());
        if (addressDTO.getIsDefault() == 1) {
            setDefaultAddress(userId, addressId);
        }
        userAddressRepository.save(userAddress);
        return null;
    }

    @Override
    @CacheEvict(value = "userAddresses", key = "#userId")
    public boolean deleteAddress(String userId, Long addressId) {
        userAddressRepository.deleteById(addressId);
        return false;
    }
    @Transactional
    @Override
    @CacheEvict(value = "userAddresses", allEntries = true)
    public boolean setDefaultAddress(String userId, Long addressId) {
        userAddressRepository.clearDefaultAddress(userId);
        userAddressRepository.setDefaultAddress(addressId);
        return false;
    }
}
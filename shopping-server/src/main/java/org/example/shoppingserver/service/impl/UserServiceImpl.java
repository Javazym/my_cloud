package org.example.shoppingserver.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.model.dto.UserAddressDTO;
import org.example.shoppingserver.model.dto.UserDTO;
import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.model.entity.UserAddress;
import org.example.shoppingserver.repository.UserAddressRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Cacheable(value = "currentUser", unless = "#result == null")
    public UserDTO getCurrentUser() {
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        return toDTO(Objects.requireNonNull(userRepository.findById(SecurityContextHolder
                .getContext().getAuthentication().getName()).orElse(null)));
    }

    private UserDTO toDTO(User user) {
        log.info("user: {}", user.getBalance());
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setBirthday(user.getBirthday());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setStatus(user.getStatus());
        dto.setBalance(user.getBalance());
        dto.setNickname(user.getNickname());
        log.info(dto.toString());
        return dto;
    }

    private UserAddressDTO toAddressDTO(UserAddress userAddress) {
        UserAddressDTO dto = new UserAddressDTO();
        dto.setId(userAddress.getId());
        dto.setDetailAddress(userAddress.getDetailAddress());
        dto.setFullAddress(userAddress.getFullAddress());
        dto.setCity(userAddress.getCity());
        dto.setDistrict(userAddress.getDistrict());
        dto.setReceiverName(userAddress.getReceiverName());
        dto.setReceiverPhone(userAddress.getReceiverPhone());
        dto.setProvince(userAddress.getProvince());
        dto.setIsDefault(userAddress.getIsDefault());
        return dto;
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
    public UserDTO getUserById(String userId) {
        return toDTO(Objects.requireNonNull(userRepository.findById(userId).orElse(null)));
    }

    @Override
    @CacheEvict(value = {"user", "currentUser"}, allEntries = true)
    public UserDTO updateUser(String userId, UserDTO userDTO) {
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
    public List<UserAddressDTO> getAddresses(String userId) {
        return userAddressRepository.findByUserId(userId).stream().map(
                address -> toAddressDTO(address)
        ).collect(Collectors.toList());
    }

    @Override
    public UserAddressDTO getAddressById(Long addressId) {
        return null;
    }

    @Transactional
    @Override
    @CacheEvict(value = "userAddresses", key = "#userId")
    public UserAddressDTO addAddress(String userId, UserAddressDTO addressDTO) {
        userAddressRepository.save(toEntity(addressDTO, userId));
        if (addressDTO.getIsDefault() == 1) {
            setDefaultAddress(userId, addressDTO.getId());
        }
        return null;
    }
    @Transactional
    @Override
    @CacheEvict(value = "userAddresses", key = "#userId")
    public UserAddressDTO updateAddress(String userId, Long addressId, UserAddressDTO addressDTO) {
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
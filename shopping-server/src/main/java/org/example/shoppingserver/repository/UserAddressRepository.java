package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.user.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收货地址Repository
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    /**
     * 根据用户ID查询收货地址列表
     */
    List<UserAddress> findByUserId(String userId);

    /**
     * 根据用户ID查询默认收货地址
     */
    Optional<UserAddress> findByUserIdAndIsDefault(String userId, Integer isDefault);

    /**
     * 取消用户的默认地址
     */
    @Modifying
    @Query("UPDATE UserAddress u SET u.isDefault = 0 WHERE u.user.id = :userId")
    void clearDefaultAddress(@Param("userId") String userId);

    /**
     * 统计用户收货地址数量
     */
    long countByUserId(String userId);

    UserAddress findByUserIdAndId(String userId, Long id);

    @Modifying
    @Query("UPDATE UserAddress u SET u.isDefault = 1 WHERE u.id = :id")
    void setDefaultAddress(Long id);
}

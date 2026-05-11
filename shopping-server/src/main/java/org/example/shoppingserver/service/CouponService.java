package org.example.shoppingserver.service;


import org.example.shoppingserver.model.dto.coupon.CouponQueryDTO;
import org.example.shoppingserver.model.vo.coupon.UserCouponVO;
import org.example.shoppingserver.model.vo.coupon.CouponVO;
import org.example.shoppingserver.model.dto.coupon.ValidateResultDTO;
import org.example.shoppingserver.model.vo.coupon.ValidateResultVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 优惠券Service接口
 */
public interface CouponService {

    /**
     * 获取优惠券列表
     *
     * @param queryDTO 查询DTO
     * @return 优惠券列表
     */
    List<CouponVO> getCoupons(CouponQueryDTO queryDTO);

    /**
     * 获取可用优惠券列表
     *
     * @param userId   用户ID
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    List<CouponVO> getAvailableCoupons(String userId, java.math.BigDecimal orderAmount);

    /**
     * 获取优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    CouponVO getCouponDetail(Long couponId);

    /**
     * 领取优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean receiveCoupon(String userId, Long couponId);

    /**
     * 获取用户已领取的优惠券
     *
     * @param userId  用户ID
     * @param status  状态：0-未使用，1-已使用，2-已过期
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户优惠券分页结果
     */
    Page<UserCouponVO> getUserCoupons(String userId, Integer status, int pageNum, int pageSize);

    /**
     * 验证优惠券
     *
     * @param userId    用户ID
     * @param couponId  优惠券ID
     * @param orderAmount 订单金额
     * @return 验证结果
     */
    ValidateResultVO validateCoupon(String userId, Long couponId, java.math.BigDecimal orderAmount);

    /**
     * 使用优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @param orderId  订单ID
     * @return 是否成功
     */
    boolean useCoupon(String userId, Long couponId, Long orderId);

    /**
     * 清理过期优惠券
     * 将已过期的未使用优惠券状态更新为已过期
     *
     * @return 更新的优惠券数量
     */
    int cleanExpiredCoupons();

    /**
     * 获取指定商品的可用优惠券列表
     *
     * @param productId 商品ID
     * @param merchantId 商家ID
     * @return 可用优惠券列表
     */
    List<CouponVO> getAvailableCouponsForProduct(Long productId, Long merchantId);

    /**
     * 获取用户可用于指定商品的优惠券列表
     * 从用户已领取的未使用优惠券中筛选出适用于该商品的优惠券
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param merchantId 商家ID
     * @return 可用用户优惠券列表
     */
    List<UserCouponVO> getUserAvailableCouponsForProduct(String userId, Long productId, Long merchantId);

}
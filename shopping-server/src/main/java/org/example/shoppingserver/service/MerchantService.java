package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.merchant.ApplySettleDTO;
import org.example.shoppingserver.model.dto.merchant.MerchantDTO;
import org.example.shoppingserver.model.vo.merchant.MerchantVO;
import org.example.shoppingserver.model.vo.merchant.MerchantStatisticsVO;
import org.example.shoppingserver.model.vo.merchant.MerchantApplicationVO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 商家Service接口
 */

public interface MerchantService {

    /**
     * 申请入驻
     *
     * @param userId    用户ID
     * @param applyDTO  申请DTO
     * @return 申请结果
     */
    boolean applySettle(String userId, ApplySettleDTO applyDTO);

    /**
     * 获取商家信息
     *
     * @param userId 用户ID
     * @return 商家信息
     */
    MerchantVO getMerchantInfo(String userId);

    /**
     * 获取商家信息（根据用户ID）
     *
     * @param userId 用户ID
     * @return 商家信息
     */
    MerchantVO getMerchantByUserId(String userId);

    /**
     * 更新商家信息
     *
     * @param userId 用户ID
     * @param merchantDTO 商家DTO
     * @return 商家信息
     */
    MerchantVO updateMerchantInfo(String userId, MerchantDTO merchantDTO);

    /**
     * 获取商家统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    MerchantStatisticsVO getStatistics(String userId);

    MerchantApplicationVO getMerchantApplication(String userId);
}

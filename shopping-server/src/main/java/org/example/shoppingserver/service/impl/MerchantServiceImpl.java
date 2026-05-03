package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.dto.MerchantApplicationDTO;
import org.example.shoppingserver.model.dto.MerchantDTO;
import org.example.shoppingserver.model.entity.AuditStatus;
import org.example.shoppingserver.model.entity.Merchant;
import org.example.shoppingserver.model.entity.MerchantApplication;
import org.example.shoppingserver.model.entity.User;
import org.example.shoppingserver.repository.MerchantApplicationRepository;
import org.example.shoppingserver.repository.MerchantRepository;
import org.example.shoppingserver.service.MerchantService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantApplicationRepository merchantApplicationRepository;

    // ====================== 1. 申请入驻 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applySettle(String userId, ApplySettleDTO applyDTO) {
        // 检查是否已存在待审核申请
        boolean exists = merchantApplicationRepository.existsByUserIdAndStatus(userId, AuditStatus.PENDING);
        if (exists) {
            return false;
        }

        // 检查店铺名称是否重复
        Optional<Merchant> existMerchant = merchantRepository.findByStoreName(applyDTO.getStoreName());
        if (existMerchant.isPresent()) {
            return false;
        }

        User user = new User();
        user.setId(userId);

        MerchantApplication application = new MerchantApplication();
        application.setUser(user);
        application.setStoreName(applyDTO.getStoreName());
        application.setStoreType(applyDTO.getStoreType());
        application.setCategory(applyDTO.getCategory());
        application.setContactName(applyDTO.getContactName());
        application.setContactPhone(applyDTO.getContactPhone());
        application.setContactEmail(applyDTO.getContactEmail());
        application.setBusinessLicense(applyDTO.getBusinessLicense());
        application.setIdCardFront(applyDTO.getIdCardFront());
        application.setIdCardBack(applyDTO.getIdCardBack());
        application.setStatus(AuditStatus.PENDING);
        application.setApplyTime(LocalDateTime.now());
        merchantApplicationRepository.save(application);
        return true;
    }

    @Override
    public MerchantApplicationDTO getMerchantApplication(String userId) {
        return convertToApplicationDTO(merchantApplicationRepository.findByUserId(userId));
    }

    private MerchantApplicationDTO convertToApplicationDTO(MerchantApplication application) {
        MerchantApplicationDTO dto = new MerchantApplicationDTO();
        dto.setId(application.getId());
        dto.setUserId(application.getUser().getId());
        dto.setUsername(application.getUser().getUsername());
        dto.setStoreName(application.getStoreName());
        dto.setStoreType(application.getStoreType());
        dto.setCategory(application.getCategory());
        dto.setContactName(application.getContactName());
        dto.setContactPhone(application.getContactPhone());
        dto.setContactEmail(application.getContactEmail());
        dto.setBusinessLicense(application.getBusinessLicense());
        dto.setIdCardFront(application.getIdCardFront());
        dto.setIdCardBack(application.getIdCardBack());
        dto.setStatus(application.getStatus().getCode());
        dto.setStatusDescription(application.getStatus().getDescription());
        dto.setRemark(application.getRemark());
        dto.setApplyTime(application.getApplyTime());
        dto.setAuditTime(application.getAuditTime());
        return dto;
    }

    // ====================== 2. 根据商家ID获取信息 ======================
    @Override
    @Cacheable(value = "merchantInfo", key = "#userId", unless = "#result == null")
    public MerchantDTO getMerchantInfo(String userId) {
        Merchant merchant = merchantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        return convert(merchant);
    }

    // ====================== 3. 根据用户ID获取商家 ======================
    @Override
    @Cacheable(value = "merchantByUserId", key = "#userId", unless = "#result == null")
    public MerchantDTO getMerchantByUserId(String userId) {
        Merchant merchant = merchantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        return convert(merchant);
    }

    // ====================== 4. 更新商家信息 ======================
    @Override
    @Transactional
    @CacheEvict(value = {"merchantInfo", "merchantByUserId"}, allEntries = true)
    public MerchantDTO updateMerchantInfo(String userId, MerchantDTO merchantDTO) {
        Merchant merchant = merchantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        merchant.setStoreName(merchantDTO.getStoreName());
        merchant.setStoreLogo(merchantDTO.getStoreLogo());
        merchant.setStoreBanner(merchantDTO.getStoreBanner());
        merchant.setStoreDescription(merchantDTO.getStoreDescription());
        merchant.setStoreType(merchantDTO.getStoreType());
        merchant.setCategory(merchantDTO.getCategory());
        merchant.setContactName(merchantDTO.getContactName());
        merchant.setContactPhone(merchantDTO.getContactPhone());
        merchant.setContactEmail(merchantDTO.getContactEmail());

        Merchant updated = merchantRepository.save(merchant);
        return convert(updated);
    }

    // ====================== 5. 商家统计数据 ======================
    @Override
    public MerchantStatisticsDTO getStatistics(String userId) {
        MerchantStatisticsDTO dto = new MerchantStatisticsDTO();
        dto.setTodaySales(0L);
        dto.setTodayOrders(0);
        dto.setTodayVisitors(0);
        dto.setPendingOrders(0);
        dto.setShippingOrders(0);
        dto.setTotalProducts(0);
        dto.setTotalIncome(0L);
        return dto;
    }

    // ====================== 🔴 最终转换器：完全匹配你的 DTO ======================
    private MerchantDTO convert(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setUserId(merchant.getUserId()); // 你实体没有userId，暂时用merchantId代替
        dto.setStoreName(merchant.getStoreName());
        dto.setStoreLogo(merchant.getStoreLogo());
        dto.setStoreBanner(merchant.getStoreBanner());
        dto.setStoreDescription(merchant.getStoreDescription());
        dto.setStoreType(merchant.getStoreType());
        dto.setCategory(merchant.getCategory());
        dto.setContactName(merchant.getContactName());
        dto.setContactPhone(merchant.getContactPhone());
        dto.setContactEmail(merchant.getContactEmail());
        dto.setRating(merchant.getRating());
        dto.setSales(merchant.getSales());
        dto.setFollowers(merchant.getFollowers());
        dto.setStatus(merchant.getStatus());
        dto.setAuditStatus(merchant.getAuditStatus().ordinal()); // 转 Integer
        return dto;
    }
}
package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.vo.marketing.AnnouncementVO;
import org.example.shoppingserver.model.vo.marketing.BannerVO;
import org.example.shoppingserver.model.vo.marketing.SeckillActivityVO;
import org.example.shoppingserver.model.vo.marketing.DiscountActivityVO;
import org.example.shoppingserver.model.entity.marketing.Announcement;
import org.example.shoppingserver.model.entity.marketing.Banner;
import org.example.shoppingserver.model.entity.marketing.SeckillActivity;
import org.example.shoppingserver.model.entity.marketing.DiscountActivity;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.repository.AnnouncementRepository;
import org.example.shoppingserver.repository.BannerRepository;
import org.example.shoppingserver.repository.SeckillActivityRepository;
import org.example.shoppingserver.repository.DiscountActivityRepository;
import org.example.shoppingserver.repository.ProductRepository;
import org.example.shoppingserver.service.MarketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements MarketingService {

    private static final Logger log = LoggerFactory.getLogger(MarketingServiceImpl.class);
    private final BannerRepository bannerRepository;
    private final AnnouncementRepository announcementRepository;
    private final SeckillActivityRepository seckillActivityRepository;
    private final DiscountActivityRepository discountActivityRepository;
    private final ProductRepository productRepository;

    // ====================== 1. 获取轮播图 ======================
    @Override
    @Cacheable(value = "banners", key = "#position != null ? #position : 'all'", unless = "#result == null || #result.isEmpty()")
    public List<BannerVO> getBanners(Integer position) {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerRepository.findActiveBanners(position, now);

        return banners.stream()
                .map(this::convertBannerVO)
                .collect(Collectors.toList());
    }

    // ====================== 2. 获取公告列表 ======================
    @Override
    @Cacheable(value = "announcements", key = "#type != null ? #type + ':' + #limit : 'all:' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<AnnouncementVO> getAnnouncements(Integer type, int limit) {
        List<Announcement> list;

        if (type != null) {
            list = announcementRepository.findByType(type);
        } else {
            list = announcementRepository.findByStatus(1);
        }

        // 限制返回数量
        if (list.size() > limit) {
            list = list.subList(0, limit);
        }

        return list.stream()
                .map(this::convertAnnouncementVO)
                .collect(Collectors.toList());
    }

    // ====================== 3. 公告详情 ======================
    @Override
    @Cacheable(value = "announcement", key = "#announcementId", unless = "#result == null")
    public AnnouncementVO getAnnouncementById(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        return convertAnnouncementVO(announcement);
    }

    // ====================== 4. 轮播图点击 +1 ======================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementBannerClick(Long bannerId) {
        if (!bannerRepository.existsById(bannerId)) {
            return false;
        }
        bannerRepository.incrementClickCount(bannerId);
        return true;
    }

    // ====================== 工具：Banner → VO ======================
    private BannerVO convertBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        vo.setId(banner.getId());
        vo.setTitle(banner.getTitle());
        vo.setImage(banner.getImage());
        vo.setLink(banner.getLink());
        vo.setLinkType(banner.getLinkType());
        vo.setPosition(banner.getPosition());
        vo.setSort(banner.getSort());
        vo.setStartTime(banner.getStartTime());
        vo.setEndTime(banner.getEndTime());
        return vo;
    }

    // ====================== 工具：Announcement → VO ======================
    private AnnouncementVO convertAnnouncementVO(Announcement announcement) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.getId());
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setType(announcement.getType());
        vo.setStatus(announcement.getStatus());
        vo.setCreatedAt(announcement.getCreatedAt());
        return vo;
    }

    // ====================== 用户端秒杀活动API ======================

    /**
     * 获取进行中的秒杀活动列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 秒杀活动分页列表
     */
    @Override
    public Page<SeckillActivityVO> getActiveSeckillActivities(int pageNum, int pageSize) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        log.info("查询秒杀活动 - 当前时间: {}, 页码: {}, 每页数量: {}", now, pageNum, pageSize);
        
        // 先查询所有数据进行调试
        List<SeckillActivity> allActivities = seckillActivityRepository.findAll();
        log.info("数据库中秒杀活动总数: {}", allActivities.size());
        allActivities.forEach(activity -> 
            log.info("活动ID: {}, 名称: {}, 状态: {}, 开始时间: {}, 结束时间: {}", 
                activity.getId(), activity.getName(), activity.getStatus(), 
                activity.getStartTime(), activity.getEndTime())
        );
        
        // 查询进行中的秒杀活动
        // 策略：返回状态为1（进行中）的活动，包括：
        // 1. 已经开始但未结束的活动
        // 2. 即将开始的活动（可选，如果需要预热展示）
        Page<SeckillActivity> page = seckillActivityRepository.findAll((root, query, cb) -> {
            // 只查询状态为1的活动，让业务层判断是否有效
            return cb.equal(root.get("status"), 1);
        }, pageable);
        
        log.info("符合条件的秒杀活动数量: {}", page.getTotalElements());
        return page.map(this::convertToSeckillVO);
    }

    /**
     * 获取秒杀活动详情
     *
     * @param activityId 活动ID
     * @return 秒杀活动详情
     */
    @Override
    @Cacheable(value = "seckillActivity", key = "#activityId", unless = "#result == null")
    public SeckillActivityVO getSeckillActivityDetail(Long activityId) {
        SeckillActivity activity = seckillActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("秒杀活动不存在"));
        
        // 检查活动是否有效
        if (!activity.isValid()) {
            throw new RuntimeException("秒杀活动已结束或未开始");
        }
        
        return convertToSeckillVO(activity);
    }

    // ====================== 用户端满减活动API ======================

    /**
     * 获取进行中的满减活动列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 满减活动分页列表
     */
    @Override
    public Page<DiscountActivityVO> getActiveDiscountActivities(int pageNum, int pageSize) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        log.info("查询满减活动 - 当前时间: {}, 页码: {}, 每页数量: {}", now, pageNum, pageSize);
        
        // 先查询所有数据进行调试
        List<DiscountActivity> allActivities = discountActivityRepository.findAll();
        log.info("数据库中满减活动总数: {}", allActivities.size());
        allActivities.forEach(activity -> 
            log.info("活动ID: {}, 名称: {}, 状态: {}, 开始时间: {}, 结束时间: {}", 
                activity.getId(), activity.getName(), activity.getStatus(), 
                activity.getStartTime(), activity.getEndTime())
        );
        
        // 查询进行中的满减活动
        Page<DiscountActivity> page = discountActivityRepository.findAll((root, query, cb) -> {
            // 只查询状态为1的活动
            return cb.equal(root.get("status"), 1);
        }, pageable);
        
        log.info("符合条件的满减活动数量: {}", page.getTotalElements());
        return page.map(this::convertToDiscountVO);
    }

    /**
     * 获取满减活动详情
     *
     * @param activityId 活动ID
     * @return 满减活动详情
     */
    @Override
    @Cacheable(value = "discountActivity", key = "#activityId", unless = "#result == null")
    public DiscountActivityVO getDiscountActivityDetail(Long activityId) {
        DiscountActivity activity = discountActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("满减活动不存在"));
        
        // 检查活动是否有效
        if (!activity.isValid()) {
            throw new RuntimeException("满减活动已结束或未开始");
        }
        
        return convertToDiscountVO(activity);
    }

    // ====================== 转换方法 ======================

    /**
     * 转换 SeckillActivity 到 SeckillActivityVO
     */
    private SeckillActivityVO convertToSeckillVO(SeckillActivity activity) {
        SeckillActivityVO vo = new SeckillActivityVO();
        vo.setId(activity.getId());
        vo.setMerchantId(activity.getMerchant().getId());
        vo.setName(activity.getName());
        vo.setProductId(activity.getProductId());
        vo.setSkuId(activity.getSkuId());
        vo.setSeckillPrice(activity.getSeckillPrice());
        vo.setOriginalPrice(activity.getOriginalPrice());
        vo.setStock(activity.getStock());
        vo.setSoldCount(activity.getSoldCount());
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setSort(activity.getSort());
        
        // 填充商品基本信息（通过关联关系获取）
        try {
            if (activity.getProduct() != null) {
                Product product = activity.getProduct();
                vo.setProductName(product.getName());
                vo.setProductSubName(product.getSubName());
                vo.setProductImage(product.getImage());
                vo.setProductPrice(product.getPrice());
                vo.setProductStock(product.getStock());
                vo.setProductSoldCount(product.getSoldCount());
                vo.setProductRating(product.getRating());
            }
        } catch (Exception e) {
            log.warn("获取商品信息失败，activityId: {}", activity.getId(), e);
        }
        
        return vo;
    }

    /**
     * 转换 DiscountActivity 到 DiscountActivityVO
     */
    private DiscountActivityVO convertToDiscountVO(DiscountActivity activity) {
        DiscountActivityVO vo = new DiscountActivityVO();
        vo.setId(activity.getId());
        vo.setMerchantId(activity.getMerchant().getId());
        vo.setName(activity.getName());
        vo.setDescription(activity.getDescription());
        vo.setDiscountType(activity.getDiscountType());
        vo.setConditionValue(activity.getConditionValue());
        vo.setDiscountAmount(activity.getDiscountAmount());
        vo.setMaxDiscount(activity.getMaxDiscount());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setScopeType(activity.getScopeType());
        
        // 将商品ID列表转换为JSON字符串（用于返回给前端）
        if (activity.getProducts() != null && !activity.getProducts().isEmpty()) {
            List<Long> productIds = activity.getProducts().stream()
                .map(Product::getId)
                .collect(java.util.stream.Collectors.toList());
            vo.setScopeIds(productIds.toString());
        }
        
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setUsedCount(activity.getUsedCount());
        vo.setSort(activity.getSort());
        return vo;
    }
}
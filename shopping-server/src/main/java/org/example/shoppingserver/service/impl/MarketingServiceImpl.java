package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.vo.marketing.AnnouncementVO;
import org.example.shoppingserver.model.vo.marketing.BannerVO;
import org.example.shoppingserver.model.entity.marketing.Announcement;
import org.example.shoppingserver.model.entity.marketing.Banner;
import org.example.shoppingserver.repository.AnnouncementRepository;
import org.example.shoppingserver.repository.BannerRepository;
import org.example.shoppingserver.service.MarketingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements MarketingService {

    private final BannerRepository bannerRepository;
    private final AnnouncementRepository announcementRepository;

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
}
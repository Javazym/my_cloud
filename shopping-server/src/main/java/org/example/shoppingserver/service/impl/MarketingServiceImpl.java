package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.dto.AnnouncementDTO;
import org.example.shoppingserver.model.dto.BannerDTO;
import org.example.shoppingserver.model.entity.Announcement;
import org.example.shoppingserver.model.entity.Banner;
import org.example.shoppingserver.repository.AnnouncementRepository;
import org.example.shoppingserver.repository.BannerRepository;
import org.example.shoppingserver.service.MarketingService;
import org.springframework.cache.annotation.CacheEvict;
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
    public List<BannerDTO> getBanners(Integer position) {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = bannerRepository.findActiveBanners(position, now);

        return banners.stream()
                .map(this::convertBannerDTO)
                .collect(Collectors.toList());
    }

    // ====================== 2. 获取公告列表 ======================
    @Override
    @Cacheable(value = "announcements", key = "#type != null ? #type + ':' + #limit : 'all:' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<AnnouncementDTO> getAnnouncements(Integer type, int limit) {
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
                .map(this::convertAnnouncementDTO)
                .collect(Collectors.toList());
    }

    // ====================== 3. 公告详情 ======================
    @Override
    @Cacheable(value = "announcement", key = "#announcementId", unless = "#result == null")
    public AnnouncementDTO getAnnouncementById(Long announcementId) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        return convertAnnouncementDTO(announcement);
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

    // ====================== 工具：Banner → DTO ======================
    private BannerDTO convertBannerDTO(Banner banner) {
        BannerDTO dto = new BannerDTO();
        dto.setId(banner.getId());
        dto.setTitle(banner.getTitle());
        dto.setImage(banner.getImage());
        dto.setLink(banner.getLink());
        dto.setLinkType(banner.getLinkType());
        dto.setPosition(banner.getPosition());
        dto.setSort(banner.getSort());
        dto.setStartTime(banner.getStartTime());
        dto.setEndTime(banner.getEndTime());
        return dto;
    }

    // ====================== 工具：Announcement → DTO ======================
    private AnnouncementDTO convertAnnouncementDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setType(announcement.getType());
        dto.setStatus(announcement.getStatus());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }
}
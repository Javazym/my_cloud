package org.example.shoppingserver.service;

import org.example.shoppingserver.model.dto.BannerDTO;
import org.example.shoppingserver.model.dto.AnnouncementDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 运营Service接口
 */

public interface MarketingService {

    /**
     * 获取轮播图列表
     *
     * @param position 位置：0-首页，1-分类页
     * @return 轮播图列表
     */
    List<BannerDTO> getBanners(Integer position);

    /**
     * 获取公告列表
     *
     * @param type   类型：0-系统，1-活动
     * @param limit  数量限制
     * @return 公告列表
     */
    List<AnnouncementDTO> getAnnouncements(Integer type, int limit);

    /**
     * 获取公告详情
     *
     * @param announcementId 公告ID
     * @return 公告详情
     */
    AnnouncementDTO getAnnouncementById(Long announcementId);

    /**
     * 增加轮播图点击次数
     *
     * @param bannerId 轮播图ID
     * @return 是否成功
     */
    boolean incrementBannerClick(Long bannerId);
}

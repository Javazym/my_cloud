package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.AnnouncementDTO;
import org.example.shoppingserver.model.dto.BannerDTO;
import org.example.shoppingserver.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运营控制器
 */
@RestController
@RequestMapping("/marketing")
@RequiredArgsConstructor
public class MarketingController {


 private final MarketingService marketingService;

 /**
 * 获取轮播图列表
 */
 @GetMapping("/banners")
 public ResponseResult<List<BannerDTO>> getBanners( @RequestParam(defaultValue = "0") Integer position) {
 List<BannerDTO> banners = marketingService.getBanners(position);
 return ResponseResult.success(banners);
 }

 /**
 * 获取公告列表
 */
 @GetMapping("/announcements")
 public ResponseResult<List<AnnouncementDTO>> getAnnouncements( @RequestParam(required = false) Integer type, @RequestParam(defaultValue = "5") int limit) {
 List<AnnouncementDTO> announcements = marketingService.getAnnouncements(type, limit);
 return ResponseResult.success(announcements);
 }

 /**
 * 获取公告详情
 */
 @GetMapping("/announcements/{announcementId}")
 public ResponseResult<AnnouncementDTO> getAnnouncementById( @PathVariable Long announcementId) {
 AnnouncementDTO announcement = marketingService.getAnnouncementById(announcementId);
 return ResponseResult.success(announcement);
 }

 /**
 * 增加轮播图点击次数
 */
 @PostMapping("/banners/{bannerId}/click")
 public ResponseResult<Boolean> incrementBannerClick( @PathVariable Long bannerId) {
 boolean result = marketingService.incrementBannerClick(bannerId);
 return ResponseResult.success(result);
 }
}

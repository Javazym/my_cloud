package org.example.shoppingserver.controller;

import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.result.ResponseResult;

import org.example.shoppingserver.model.vo.marketing.AnnouncementVO;
import org.example.shoppingserver.model.vo.marketing.BannerVO;
import org.example.shoppingserver.model.vo.marketing.SeckillActivityVO;
import org.example.shoppingserver.model.vo.marketing.DiscountActivityVO;
import org.example.shoppingserver.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 public ResponseResult<List<BannerVO>> getBanners(@RequestParam(defaultValue = "0") Integer position) {
  List<BannerVO> banners = marketingService.getBanners(position);
  return ResponseResult.success(banners);
 }

 /**
 * 获取公告列表
 */
 @GetMapping("/announcements")
 public ResponseResult<List<AnnouncementVO>> getAnnouncements(@RequestParam(required = false) Integer type, @RequestParam(defaultValue = "5") int limit) {
  List<AnnouncementVO> announcements = marketingService.getAnnouncements(type, limit);
  return ResponseResult.success(announcements);
 }

 /**
 * 获取公告详情
 */
 @GetMapping("/announcements/{announcementId}")
 public ResponseResult<AnnouncementVO> getAnnouncementById( @PathVariable Long announcementId) {
 AnnouncementVO announcement = marketingService.getAnnouncementById(announcementId);
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

 // ==================== 用户端秒杀活动API ====================

 /**
  * 获取进行中的秒杀活动列表
  *
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 秒杀活动分页列表
  */
 @GetMapping("/seckill/active")
 public ResponseResult<Page<SeckillActivityVO>> getActiveSeckillActivities(
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<SeckillActivityVO> page = marketingService.getActiveSeckillActivities(pageNum, pageSize);
     return ResponseResult.success(page);
 }

 /**
  * 获取秒杀活动详情
  *
  * @param activityId 活动ID
  * @return 秒杀活动详情
  */
 @GetMapping("/seckill/{activityId}")
 public ResponseResult<SeckillActivityVO> getSeckillActivityDetail(@PathVariable Long activityId) {
     SeckillActivityVO activity = marketingService.getSeckillActivityDetail(activityId);
     return ResponseResult.success(activity);
 }

 // ==================== 用户端满减活动API ====================

 /**
  * 获取进行中的满减活动列表
  *
  * @param pageNum 页码，默认1
  * @param pageSize 每页数量，默认10
  * @return 满减活动分页列表
  */
 @GetMapping("/discount/active")
 public ResponseResult<Page<DiscountActivityVO>> getActiveDiscountActivities(
         @RequestParam(defaultValue = "1") int pageNum,
         @RequestParam(defaultValue = "10") int pageSize) {
     Page<DiscountActivityVO> page = marketingService.getActiveDiscountActivities(pageNum, pageSize);
     return ResponseResult.success(page);
 }

 /**
  * 获取满减活动详情
  *
  * @param activityId 活动ID
  * @return 满减活动详情
  */
 @GetMapping("/discount/{activityId}")
 public ResponseResult<DiscountActivityVO> getDiscountActivityDetail(@PathVariable Long activityId) {
     DiscountActivityVO activity = marketingService.getDiscountActivityDetail(activityId);
     return ResponseResult.success(activity);
 }
}

package org.example.shoppingserver.controller;


import lombok.RequiredArgsConstructor;

import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.common.result.ResponseResult;
import org.example.shoppingserver.model.dto.FavoriteDTO;
import org.example.shoppingserver.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 获取用户收藏列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 收藏分页结果
     */
    @GetMapping
    public ResponseResult<Page<FavoriteDTO>> getFavorites(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserHolder.getCurrentUserId();
        Page<FavoriteDTO> page = favoriteService.getFavorites(userId, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 添加收藏
     *
     * @param productId 商品ID
     * @return 操作结果
     */
    @PostMapping
    public ResponseResult<Boolean> addFavorite(@RequestParam Long productId) {
        String userId = UserHolder.getCurrentUserId();
        boolean result = favoriteService.addFavorite(userId, productId);
        return ResponseResult.success(result);
    }

    /**
     * 取消收藏
     *
     * @param productId 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/{productId}")
    public ResponseResult<Boolean> removeFavorite(@PathVariable Long productId) {
        String userId = UserHolder.getCurrentUserId();
        boolean result = favoriteService.removeFavorite(userId, productId);
        return ResponseResult.success(result);
    }

    /**
     * 检查是否已收藏
     *
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @GetMapping("/check")
    public ResponseResult<Boolean> checkFavorite(@RequestParam Long productId) {
        String userId = UserHolder.getCurrentUserId();
        boolean result = favoriteService.checkFavorite(userId, productId);
        return ResponseResult.success(result);
    }

    /**
     * 获取收藏数量
     *
     * @return 收藏数量
     */
    @GetMapping("/count")
    public ResponseResult<Integer> getFavoriteCount() {
        String userId = UserHolder.getCurrentUserId();
        Integer count = favoriteService.getFavoriteCount(userId);
        return ResponseResult.success(count);
    }

    /**
     * 清空收藏夹
     *
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public ResponseResult<Boolean> clearFavorites() {
        String userId = UserHolder.getCurrentUserId();
        boolean result = favoriteService.clearFavorites(userId);
        return ResponseResult.success(result);
    }

    /**
     * 批量删除收藏
     *
     * @param request 包含favoriteIds的请求体
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public ResponseResult<Boolean> batchRemove(@RequestBody Map<String, List<Long>> request) {
        String userId = UserHolder.getCurrentUserId();
        List<Long> favoriteIds = request.get("favoriteIds");
        boolean result = favoriteService.batchRemove(userId, favoriteIds);
        return ResponseResult.success(result);
    }
}

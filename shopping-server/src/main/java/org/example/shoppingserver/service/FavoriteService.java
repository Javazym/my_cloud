package org.example.shoppingserver.service;


import org.example.shoppingserver.model.vo.favorite.FavoriteVO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 收藏Service接口
 */

public interface FavoriteService {

    /**
     * 获取用户收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 收藏分页结果
     */
    Page<FavoriteVO> getFavorites(String userId, int pageNum, int pageSize);

    /**
     * 添加收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean addFavorite(String userId, Long productId);

    /**
     * 取消收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    boolean removeFavorite(String userId, Long productId);

    /**
     * 检查是否已收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    boolean checkFavorite(String userId, Long productId);

    /**
     * 获取收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    Integer getFavoriteCount(String userId);

    /**
     * 清空收藏夹
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearFavorites(String userId);

    /**
     * 批量删除收藏
     *
     * @param userId      用户ID
     * @param favoriteIds 收藏ID列表
     * @return 是否成功
     */
    boolean batchRemove(String userId, java.util.List<Long> favoriteIds);
}

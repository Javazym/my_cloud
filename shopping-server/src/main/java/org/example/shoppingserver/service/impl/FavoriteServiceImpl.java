package org.example.shoppingserver.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.model.vo.favorite.FavoriteVO;
import org.example.shoppingserver.model.entity.favorite.Favorite;
import org.example.shoppingserver.model.entity.product.Product;
import org.example.shoppingserver.model.entity.user.User;
import org.example.shoppingserver.repository.FavoriteRepository;
import org.example.shoppingserver.repository.ProductRepository;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.FavoriteService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收藏服务实现类
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 获取用户收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 收藏分页结果
     */
    @Override
    public Page<FavoriteVO> getFavorites(String userId, int pageNum, int pageSize) {
        // 修正页码：从0开始
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        return favoriteRepository.findByUserId(userId, pageable).map(favorite -> {
            FavoriteVO vo = new FavoriteVO();
            vo.setId(favorite.getId());
            vo.setCreatedAt(favorite.getCreatedAt());
            
            // 添加空值检查
            if (favorite.getProduct() != null) {
                Product product = favorite.getProduct();
                vo.setProductId(product.getId());
                vo.setProductName(product.getName());
                vo.setProductImage(product.getImage());
                vo.setProductPrice(product.getPrice());
            }
            
            return vo;
        });
    }

    /**
     * 添加收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    @Override
    @Transactional
    @CacheEvict(value = "favorites", allEntries = true)
    public boolean addFavorite(String userId, Long productId) {
        // 检查是否已收藏
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return false; // 已经收藏过了
        }

        // 验证用户和商品是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        
        return true;
    }

    /**
     * 取消收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否成功
     */
    @Override
    @Transactional
    @CacheEvict(value = "favorites", allEntries = true)
    public boolean removeFavorite(String userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return true;
    }

    /**
     * 检查是否已收藏
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 是否已收藏
     */
    @Override
    @Cacheable(value = "favoriteCheck", key = "#userId + ':' + #productId", unless = "#result == null")
    public boolean checkFavorite(String userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Override
    @Cacheable(value = "favoriteCount", key = "#userId", unless = "#result == null")
    public Integer getFavoriteCount(String userId) {
        return (int) favoriteRepository.countByUserId(userId);
    }

    /**
     * 清空收藏夹
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    @Transactional
    @CacheEvict(value = {"favorites", "favoriteCount"}, allEntries = true)
    public boolean clearFavorites(String userId) {
        favoriteRepository.deleteByUserId(userId);
        return true;
    }

    /**
     * 批量删除收藏
     *
     * @param userId     用户ID
     * @param favoriteIds 收藏ID列表
     * @return 是否成功
     */
    @Override
    @Transactional
    @CacheEvict(value = {"favorites", "favoriteCount"}, allEntries = true)
    public boolean batchRemove(String userId, List<Long> favoriteIds) {
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return false;
        }
        
        // 验证这些收藏是否属于该用户
        List<Favorite> favorites = favoriteRepository.findAllById(favoriteIds);
        for (Favorite favorite : favorites) {
            if (!favorite.getUser().getId().equals(userId)) {
                throw new RuntimeException("无权删除此收藏");
            }
        }
        
        favoriteRepository.deleteAllById(favoriteIds);
        return true;
    }
}

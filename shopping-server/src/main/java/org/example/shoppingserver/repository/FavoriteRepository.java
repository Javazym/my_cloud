package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏Repository
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {

    /**
     * 根据用户ID查询收藏列表
     */
    Page<Favorite> findByUserId(String userId, Pageable pageable);

    /**
     * 根据用户ID和商品ID查询收藏
     */
    Optional<Favorite> findByUserIdAndProductId(String userId, Long productId);

    /**
     * 检查是否已收藏
     */
    boolean existsByUserIdAndProductId(String userId, Long productId);

    /**
     * 统计用户收藏数量
     */
    long countByUserId(String userId);

    /**
     * 统计商品被收藏数量
     */
    long countByProductId(Long productId);

    /**
     * 删除收藏
     *
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId AND f.product.id = :productId")
    int deleteByUserIdAndProductId(@Param("userId") String userId, @Param("productId") Long productId);

    /**
     * 清空用户收藏
     */
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.user.id = :userId")
    void deleteByUserId(@Param("userId") String userId);

    /**
     * 查询热门收藏商品
     */
    @Query("SELECT f.product.id, COUNT(f) FROM Favorite f GROUP BY f.product.id ORDER BY COUNT(f) DESC")
    List<Object[]> findHotFavorites(Pageable pageable);
}

package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轮播图Repository
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * 根据位置查询轮播图列表
     */
    List<Banner> findByPosition(Integer position);

    /**
     * 根据位置和状态查询轮播图列表（按排序）
     */
    @Query("SELECT b FROM Banner b WHERE b.position = :position AND b.status = 1 AND (b.startTime IS NULL OR b.startTime <= :now) AND (b.endTime IS NULL OR b.endTime >= :now) ORDER BY b.sort ASC")
    List<Banner> findActiveBanners(@Param("position") Integer position, @Param("now") LocalDateTime now);

    /**
     * 根据状态查询轮播图列表
     */
    Page<Banner> findByStatus(Integer status, Pageable pageable);

    /**
     * 增加点击次数
     */
    @Modifying
    @Query("UPDATE Banner b SET b.clickCount = b.clickCount + 1 WHERE b.id = :id")
    void incrementClickCount(@Param("id") Long id);

    /**
     * 统计位置轮播图数量
     */
    long countByPosition(Integer position);
}

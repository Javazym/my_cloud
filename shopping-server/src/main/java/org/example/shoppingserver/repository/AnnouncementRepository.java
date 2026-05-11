package org.example.shoppingserver.repository;


import org.example.shoppingserver.model.entity.marketing.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告Repository
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * 根据状态查询公告列表
     */
    List<Announcement> findByStatus(Integer status);

    /**
     * 根据类型查询公告列表
     */
    List<Announcement> findByType(Integer type);

    /**
     * 根据状态分页查询公告
     */
    Page<Announcement> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);

    /**
     * 统计公告数量
     */
    long countByStatus(Integer status);
}

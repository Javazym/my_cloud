package org.example.shoppingserver.model.entity.marketing;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.shoppingserver.model.entity.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "announcements")
public class Announcement extends BaseEntity {

    /**
     * 标题
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 类型：0-系统，1-活动
     */
    @Column(name = "type")
    private Integer type = 0;

    /**
     * 状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    private LocalDateTime publishTime;
}

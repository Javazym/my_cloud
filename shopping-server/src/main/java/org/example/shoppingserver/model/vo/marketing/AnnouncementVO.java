package org.example.shoppingserver.model.vo.marketing;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告VO
 */
@Data
public class AnnouncementVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型：0-系统，1-活动
     */
    private Integer type;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

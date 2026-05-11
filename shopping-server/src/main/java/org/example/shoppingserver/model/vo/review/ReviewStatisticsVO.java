package org.example.shoppingserver.model.vo.review;

import lombok.Data;

import java.io.Serializable;

/**
 * 评价统计VO
 */
@Data
public class ReviewStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总评价数
     */
    private Long totalCount;

    /**
     * 平均评分
     */
    private Double averageRating;

    /**
     * 5星数量
     */
    private Integer fiveStarCount;

    /**
     * 4星数量
     */
    private Integer fourStarCount;

    /**
     * 3星数量
     */
    private Integer threeStarCount;

    /**
     * 2星数量
     */
    private Integer twoStarCount;

    /**
     * 1星数量
     */
    private Integer oneStarCount;

    /**
     * 好评率
     */
    private Double positiveRate;

    /**
     * 有图评价数
     */
    private Integer withImageCount;
}

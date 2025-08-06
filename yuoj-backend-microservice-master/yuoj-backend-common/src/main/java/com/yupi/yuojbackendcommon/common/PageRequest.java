package com.yupi.yuojbackendcommon.common;
import lombok.Data;
import com.yupi.yuojbackendcommon.constant.CommonConstant;
/**
 * Pagination request
 *
 */
@Data
public class PageRequest {
    /**
     * Current page number
     */
    private long current = 1;
    /**
     * Page size
     */
    private long pageSize = 10;
    /**
     * Sort field
     */
    private String sortField;
    /**
     * Sort order (default ascending)
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}

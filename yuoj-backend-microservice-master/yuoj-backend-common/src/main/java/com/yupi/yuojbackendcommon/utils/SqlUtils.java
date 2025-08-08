package com.yupi.yuojbackendcommon.utils;
import org.apache.commons.lang3.StringUtils;
/**
 * SQL utilities
 *
 */
public class SqlUtils {
    /**
     * Validate if sort field is legal (prevent SQL injection)
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}

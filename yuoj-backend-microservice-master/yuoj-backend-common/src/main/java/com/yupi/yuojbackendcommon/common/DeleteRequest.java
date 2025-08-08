package com.yupi.yuojbackendcommon.common;
import lombok.Data;
import java.io.Serializable;
/**
 * Delete request
 *
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    private static final long serialVersionUID = 1L;
}
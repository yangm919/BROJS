package com.yupi.yuojcodesandbox;


import com.yupi.yuojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.yuojcodesandbox.model.ExecuteCodeResponse;

/**
 * Code sandbox interface definition
 */
public interface CodeSandbox {

    /**
     * Execute code
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

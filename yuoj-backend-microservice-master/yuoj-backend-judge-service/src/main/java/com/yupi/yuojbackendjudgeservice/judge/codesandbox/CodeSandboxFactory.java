package com.yupi.yuojbackendjudgeservice.judge.codesandbox;

import com.yupi.yuojbackendjudgeservice.config.Judge0Config;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.Main;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
@Component
public class CodeSandboxFactory {
    
    @Autowired
    private Judge0Config judge0Config;
    
    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return
     */
    public CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox(judge0Config);
            case "judge0":
                return new ThirdPartyCodeSandbox(judge0Config);
            default:
                return new ExampleCodeSandbox();
        }
    }
}

-- =====================================================
-- 在线判题系统数据库初始化脚本
-- 数据库名: yuoj
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `yuoj` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `yuoj`;

-- =====================================================
-- 1. 用户表 (user)
-- =====================================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    `userAccount` VARCHAR(256) NOT NULL COMMENT '用户账号',
    `userPassword` VARCHAR(512) NOT NULL COMMENT '用户密码',
    `unionId` VARCHAR(256) NULL COMMENT '微信开放平台ID',
    `mpOpenId` VARCHAR(256) NULL COMMENT '公众号openId',
    `userName` VARCHAR(256) NULL COMMENT '用户昵称',
    `userAvatar` VARCHAR(1024) NULL COMMENT '用户头像',
    `userProfile` VARCHAR(512) NULL COMMENT '用户简介',
    `userRole` VARCHAR(256) DEFAULT 'user' NOT NULL COMMENT '用户角色：user/admin/ban',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX `idx_unionId` (`unionId`),
    INDEX `idx_userAccount` (`userAccount`),
    INDEX `idx_userRole` (`userRole`)
) COMMENT '用户表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 2. 题目表 (question)
-- =====================================================
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT COMMENT '题目ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT '题目标题',
    `content` TEXT NULL COMMENT '题目内容',
    `tags` VARCHAR(1024) NULL COMMENT '标签列表（json 数组）',
    `answer` TEXT NULL COMMENT '题目答案',
    `submitNum` INT DEFAULT 0 NOT NULL COMMENT '题目提交数',
    `acceptedNum` INT DEFAULT 0 NOT NULL COMMENT '题目通过数',
    `judgeCase` TEXT NULL COMMENT '判题用例（json 数组）',
    `judgeConfig` TEXT NULL COMMENT '判题配置（json 对象）',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT '收藏数',
    `userId` BIGINT NOT NULL COMMENT '创建用户ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT '题目表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 3. 题目提交表 (question_submit)
-- =====================================================
CREATE TABLE IF NOT EXISTS `question_submit` (
    `id` BIGINT AUTO_INCREMENT COMMENT '提交ID' PRIMARY KEY,
    `language` VARCHAR(128) NOT NULL COMMENT '编程语言',
    `code` TEXT NOT NULL COMMENT '用户代码',
    `judgeInfo` TEXT NULL COMMENT '判题信息（json 对象）',
    `status` INT DEFAULT 0 NOT NULL COMMENT '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    `questionId` BIGINT NOT NULL COMMENT '题目ID',
    `userId` BIGINT NOT NULL COMMENT '创建用户ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX `idx_questionId` (`questionId`),
    INDEX `idx_userId` (`userId`),
    INDEX `idx_status` (`status`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT '题目提交表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 4. 帖子表 (post)
-- =====================================================
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT AUTO_INCREMENT COMMENT '帖子ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT '帖子标题',
    `content` TEXT NULL COMMENT '帖子内容',
    `tags` VARCHAR(1024) NULL COMMENT '标签列表（json 数组）',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT '收藏数',
    `userId` BIGINT NOT NULL COMMENT '创建用户ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT '帖子表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 5. 帖子点赞表 (post_thumb)
-- =====================================================
CREATE TABLE IF NOT EXISTS `post_thumb` (
    `id` BIGINT AUTO_INCREMENT COMMENT '点赞ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT '帖子ID',
    `userId` BIGINT NOT NULL COMMENT '创建用户ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT '帖子点赞表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 6. 帖子收藏表 (post_favour)
-- =====================================================
CREATE TABLE IF NOT EXISTS `post_favour` (
    `id` BIGINT AUTO_INCREMENT COMMENT '收藏ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT '帖子ID',
    `userId` BIGINT NOT NULL COMMENT '创建用户ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT '帖子收藏表' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 初始数据插入
-- =====================================================

-- 插入管理员用户
INSERT INTO `user` (`userAccount`, `userPassword`, `userName`, `userRole`, `userProfile`) VALUES
('admin', '03caebb36670995fc5e097d0d8a6f908', '管理员', 'admin', '系统管理员'),
('test', '03caebb36670995fc5e097d0d8a6f908', '测试用户', 'user', '测试用户账号');

-- 插入示例题目
INSERT INTO `question` (`title`, `content`, `tags`, `answer`, `judgeCase`, `judgeConfig`, `userId`) VALUES
('两数之和', 
'给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值 target 的那两个整数，并返回它们的数组下标。

你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。

你可以按任意顺序返回答案。

示例 1：
输入：nums = [2,7,11,15], target = 9
输出：[0,1]
解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。

示例 2：
输入：nums = [3,2,4], target = 6
输出：[1,2]

示例 3：
输入：nums = [3,3], target = 6
输出：[0,1]

提示：
2 <= nums.length <= 104
-109 <= nums[i] <= 109
-109 <= target <= 109
只会存在一个有效答案', 
'["数组", "哈希表"]', 
'```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}
```', 
'[{"input": "[2,7,11,15]\n9", "output": "[0,1]"}, {"input": "[3,2,4]\n6", "output": "[1,2]"}, {"input": "[3,3]\n6", "output": "[0,1]"}]', 
'{"timeLimit": 1000, "memoryLimit": 256000}', 
1),

('斐波那契数列', 
'斐波那契数列是一个经典的数学问题，定义如下：
F(0) = 0, F(1) = 1
F(n) = F(n-1) + F(n-2), 其中 n > 1

给定一个整数 n，请计算 F(n)。

示例 1：
输入：n = 2
输出：1
解释：F(2) = F(1) + F(0) = 1 + 0 = 1

示例 2：
输入：n = 3
输出：2
解释：F(3) = F(2) + F(1) = 1 + 1 = 2

示例 3：
输入：n = 4
输出：3
解释：F(4) = F(3) + F(2) = 2 + 1 = 3

提示：
0 <= n <= 30', 
'["动态规划", "数学"]', 
'```java
class Solution {
    public int fib(int n) {
        if (n <= 1) return n;
        int prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            int temp = curr;
            curr = prev + curr;
            prev = temp;
        }
        return curr;
    }
}
```', 
'[{"input": "2", "output": "1"}, {"input": "3", "output": "2"}, {"input": "4", "output": "3"}]', 
'{"timeLimit": 1000, "memoryLimit": 256000}', 
1),

('反转字符串', 
'编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 char[] 的形式给出。

不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间来解决这一问题。

你可以假设数组中的所有字符都是 ASCII 码表中的可打印字符。

示例 1：
输入：["h","e","l","l","o"]
输出：["o","l","l","e","h"]

示例 2：
输入：["H","a","n","n","a","h"]
输出：["h","a","n","n","a","H"]', 
'["字符串", "双指针"]', 
'```java
class Solution {
    public void reverseString(char[] s) {
        int left = 0, right = s.length - 1;
        while (left < right) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            left++;
            right--;
        }
    }
}
```', 
'[{"input": "[\"h\",\"e\",\"l\",\"l\",\"o\"]", "output": "[\"o\",\"l\",\"l\",\"e\",\"h\"]"}, {"input": "[\"H\",\"a\",\"n\",\"n\",\"a\",\"h\"]", "output": "[\"h\",\"a\",\"n\",\"n\",\"a\",\"H\"]"}]', 
'{"timeLimit": 1000, "memoryLimit": 256000}', 
1);

-- 插入示例帖子
INSERT INTO `post` (`title`, `content`, `tags`, `userId`) VALUES
('欢迎来到在线判题系统', 
'欢迎使用我们的在线判题系统！这是一个功能强大的编程练习平台，支持多种编程语言。

主要功能：
- 题目练习：提供各种难度的编程题目
- 在线判题：实时编译和运行代码
- 社区交流：分享解题思路和经验
- 个人中心：查看做题记录和统计

开始你的编程之旅吧！', 
'["欢迎", "介绍"]', 
1),

('Java编程技巧分享', 
'今天想和大家分享一些Java编程中的实用技巧：

1. 使用StringBuilder进行字符串拼接
2. 合理使用集合框架
3. 注意内存管理和垃圾回收
4. 掌握多线程编程基础
5. 学习设计模式

这些技巧能帮助你写出更高效、更优雅的代码。', 
'["Java", "编程技巧"]', 
1);

-- =====================================================
-- 创建视图和存储过程（可选）
-- =====================================================

-- 创建用户统计视图
CREATE OR REPLACE VIEW `user_statistics` AS
SELECT 
    u.id,
    u.userName,
    u.userAccount,
    COUNT(DISTINCT qs.id) as submitCount,
    COUNT(DISTINCT CASE WHEN qs.status = 2 THEN qs.id END) as acceptedCount,
    COUNT(DISTINCT q.id) as questionCount
FROM `user` u
LEFT JOIN `question_submit` qs ON u.id = qs.userId AND qs.isDelete = 0
LEFT JOIN `question` q ON u.id = q.userId AND q.isDelete = 0
WHERE u.isDelete = 0
GROUP BY u.id, u.userName, u.userAccount;

-- 创建题目统计视图
CREATE OR REPLACE VIEW `question_statistics` AS
SELECT 
    q.id,
    q.title,
    q.submitNum,
    q.acceptedNum,
    CASE 
        WHEN q.submitNum = 0 THEN 0
        ELSE ROUND(q.acceptedNum * 100.0 / q.submitNum, 2)
    END as acceptanceRate
FROM `question` q
WHERE q.isDelete = 0;

-- =====================================================
-- 数据库权限设置（可选）
-- =====================================================

-- 创建应用用户（如果需要）
-- CREATE USER IF NOT EXISTS 'yuoj_user'@'%' IDENTIFIED BY 'yuoj_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON yuoj.* TO 'yuoj_user'@'%';
-- FLUSH PRIVILEGES;

-- =====================================================
-- 初始化完成提示
-- =====================================================
SELECT '数据库初始化完成！' as message;
SELECT '已创建的表：' as info;
SHOW TABLES;
SELECT '已插入的初始数据：' as info;
SELECT COUNT(*) as user_count FROM `user`;
SELECT COUNT(*) as question_count FROM `question`;
SELECT COUNT(*) as post_count FROM `post`; 
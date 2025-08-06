-- =====================================================
-- Online Judge System Database Initialization Script (Docker Version)
-- Database name: yuoj
-- Character set: utf8mb4
-- Collation: utf8mb4_unicode_ci
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS `yuoj` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use database
USE `yuoj`;

-- =====================================================
-- 1. User table (user)
-- =====================================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'User ID' PRIMARY KEY,
    `userAccount` VARCHAR(256) NOT NULL COMMENT 'User account',
    `userPassword` VARCHAR(512) NOT NULL COMMENT 'User password',
    `unionId` VARCHAR(256) NULL COMMENT 'WeChat Open Platform ID',
    `mpOpenId` VARCHAR(256) NULL COMMENT 'WeChat Official Account openId',
    `userName` VARCHAR(256) NULL COMMENT 'User nickname',
    `userAvatar` VARCHAR(1024) NULL COMMENT 'User avatar',
    `userProfile` VARCHAR(512) NULL COMMENT 'User profile',
    `userRole` VARCHAR(256) DEFAULT 'user' NOT NULL COMMENT 'User role: user/admin/ban',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_unionId` (`unionId`),
    INDEX `idx_userAccount` (`userAccount`),
    INDEX `idx_userRole` (`userRole`)
) COMMENT 'User table' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 2. Question table (question)
-- =====================================================
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Question ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Question title',
    `content` TEXT NULL COMMENT 'Question content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tag list (json array)',
    `answer` TEXT NULL COMMENT 'Question answer',
    `submitNum` INT DEFAULT 0 NOT NULL COMMENT 'Question submission count',
    `acceptedNum` INT DEFAULT 0 NOT NULL COMMENT 'Question accepted count',
    `judgeCase` TEXT NULL COMMENT 'Judge cases (json array)',
    `judgeConfig` TEXT NULL COMMENT 'Judge configuration (json object)',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT 'Thumb count',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT 'Favour count',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Question table' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 3. Question submission table (question_submit)
-- =====================================================
CREATE TABLE IF NOT EXISTS `question_submit` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Submission ID' PRIMARY KEY,
    `language` VARCHAR(128) NOT NULL COMMENT 'Programming language',
    `code` TEXT NOT NULL COMMENT 'User code',
    `judgeInfo` TEXT NULL COMMENT 'Judge information (json object)',
    `status` INT DEFAULT 0 NOT NULL COMMENT 'Judge status (0 - waiting, 1 - judging, 2 - success, 3 - failed)',
    `questionId` BIGINT NOT NULL COMMENT 'Question ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_questionId` (`questionId`),
    INDEX `idx_userId` (`userId`),
    INDEX `idx_status` (`status`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Question submission table' COLLATE = utf8mb4_unicode_ci;

-- =====================================================
-- 4. Post table (post)
-- =====================================================
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Post ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Post title',
    `content` TEXT NULL COMMENT 'Post content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tag list (json array)',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT 'Thumb count',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT 'Favour count',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Post table' COLLATE = utf8mb4_unicode_ci;

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

-- 插入管理员用户 (密码: 12345678)
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
1); 
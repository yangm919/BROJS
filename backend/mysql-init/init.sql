CREATE DATABASE IF NOT EXISTS `yuoj` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `yuoj`;

-- User table
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

-- Question table
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Question ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Question title',
    `content` TEXT NULL COMMENT 'Question content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tags list (json array)',
    `answer` TEXT NULL COMMENT 'Question answer',
    `submitNum` INT DEFAULT 0 NOT NULL COMMENT 'Submit count',
    `acceptedNum` INT DEFAULT 0 NOT NULL COMMENT 'Accepted count',
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

-- Question submit table
CREATE TABLE IF NOT EXISTS `question_submit` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Submit ID' PRIMARY KEY,
    `language` VARCHAR(128) NOT NULL COMMENT 'Programming language',
    `code` TEXT NOT NULL COMMENT 'User code',
    `judgeInfo` TEXT NULL COMMENT 'Judge information (json object)',
    `status` INT DEFAULT 0 NOT NULL COMMENT 'Judge status (0 - pending, 1 - judging, 2 - success, 3 - failed)',
    `questionId` BIGINT NOT NULL COMMENT 'Question ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_questionId` (`questionId`),
    INDEX `idx_userId` (`userId`),
    INDEX `idx_status` (`status`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Question submit table' COLLATE = utf8mb4_unicode_ci;

-- Post table
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Post ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Post title',
    `content` TEXT NULL COMMENT 'Post content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tags list (json array)',
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

-- Post thumb table
CREATE TABLE IF NOT EXISTS `post_thumb` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Thumb ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT 'Post ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT 'Post thumb table' COLLATE = utf8mb4_unicode_ci;

-- Post favour table
CREATE TABLE IF NOT EXISTS `post_favour` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Favour ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT 'Post ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT 'Post favour table' COLLATE = utf8mb4_unicode_ci;

-- Insert initial data
INSERT INTO `user` (`userAccount`, `userPassword`, `userName`, `userRole`, `userProfile`) VALUES
('admin', '03caebb36670995fc5e097d0d8a6f908', 'Administrator', 'admin', 'System Administrator'),
('test', '03caebb36670995fc5e097d0d8a6f908', 'Test User', 'user', 'Test user account');

-- Insert sample question
INSERT INTO `question` (`title`, `content`, `tags`, `answer`, `judgeCase`, `judgeConfig`, `userId`) VALUES
('Two Sum', 
'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

You can return the answer in any order.

Example 1:
Input: nums = [2,7,11,15], target = 9
Output: [0,1]
Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].

Example 2:
Input: nums = [3,2,4], target = 6
Output: [1,2]

Example 3:
Input: nums = [3,3], target = 6
Output: [0,1]

Constraints:
2 <= nums.length <= 104
-109 <= nums[i] <= 109
-109 <= target <= 109
Only one valid answer exists', 
'["Array", "Hash Table"]', 
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

-- Insert sample post
INSERT INTO `post` (`title`, `content`, `tags`, `userId`) VALUES
('Welcome to Online Judge System', 
'Welcome to our Online Judge System! This is a powerful programming practice platform that supports multiple programming languages.

Main features:
- Question practice: Provides programming questions of various difficulties
- Online judging: Real-time code compilation and execution
- Community communication: Share problem-solving ideas and experiences
- Personal center: View problem-solving records and statistics

Start your programming journey now!', 
'["Welcome", "Introduction"]', 
1);
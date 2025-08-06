package com.yupi.yuojbackenduserservice.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuojbackendmodel.model.dto.user.UserQueryRequest;
import com.yupi.yuojbackendmodel.model.entity.User;
import com.yupi.yuojbackendmodel.model.vo.LoginUserVO;
import com.yupi.yuojbackendmodel.model.vo.UserStatisticsVO;
import com.yupi.yuojbackendmodel.model.vo.UserVO;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
/**
 * User service
 *
 */
public interface UserService extends IService<User> {
    /**
     * User registration
     *
     * @param userAccount   User account
     * @param userPassword  User password
     * @param checkPassword Check password
     * @return New user id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
    /**
     * User login
     *
     * @param userAccount   User account
     * @param userPassword  User password
     * @param request
     * @return Desensitized user information
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * Get current logged in user
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * Get current logged in user (allow not logged in)
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);
    /**
     * Whether is admin
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);
    /**
     * Whether is admin
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);
    /**
     * User logout
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
    /**
     * Get desensitized logged in user information
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);
    /**
     * Get user statistics
     *
     * @param userId User ID
     * @return User statistics
     */
    UserStatisticsVO getUserStatistics(Long userId);
    /**
     * Get query wrapper
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    /**
     * Get user wrapper
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);
    /**
     * Get user wrapper
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);
}

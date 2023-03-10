package ee.valiit.back_toytrade.trade.user;

import ee.valiit.back_toytrade.domain.user.User;
import ee.valiit.back_toytrade.trade.dto.NewUserRequest;
import ee.valiit.back_toytrade.trade.dto.UserInfo;
import ee.valiit.back_toytrade.domain.user.UserMapper;
import ee.valiit.back_toytrade.domain.user.UserService;
import ee.valiit.back_toytrade.domain.user.role.Role;
import ee.valiit.back_toytrade.domain.user.role.RoleService;
import ee.valiit.back_toytrade.trade.dto.UserRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import static ee.valiit.back_toytrade.trade.Status.DEACTIVATED;

@Service
public class UsersService {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserMapper userMapper;

    public List<UserInfo> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return userMapper.toInfos(allUsers);
    }

    public UserRequest getUserInfo(Integer userId) {
        User user = userService.findUser(userId);
        return userMapper.getUserInfo(user);
    }

    public void editUser(Integer userId, UserRequest userRequest) {
        User user = userService.findUser(userId);
        userMapper.updateUser(userRequest, user);
        updateRoleIfChanged(userRequest.getRoleId(), user);
        userService.saveUser(user);
    }

    public void deleteUser(Integer userId) {
        User user = userService.findUser(userId);
        addTimestampToUsername(user);
        user.setStatus(DEACTIVATED);
        userService.saveUser(user);
    }

    public void editMyUser(Integer userId, NewUserRequest userInfo) {
        User user = userService.findUser(userId);
        updateUserInfo(userInfo, user);
        userService.saveUser(user);
    }

    public String getUsername(Integer userId) {
        return userService.findUser(userId).getUsername();
    }

    public Integer getMyPoints(Integer userId) {
        return userService.findUser(userId).getPoints();
    }

    public List<Role> getAllRoles() {
        return userService.getAllRoles();
    }

    public UserInfo getUser(Integer userId) {
        return userMapper.toInfo(userService.getUser(userId));

    }

    private static void updateUserInfo(NewUserRequest userInfo, User user) {
        user.setMobile(userInfo.getMobile());
        user.setPassword(userInfo.getPassword());
        user.setUsername(userInfo.getUsername());
    }

    private void updateRoleIfChanged(Integer roleId, User user) {
        if (!roleId.equals(user.getRole().getId())) {
            Role role = roleService.findRole(roleId);
            user.setRole(role);
        }
    }

    private static void addTimestampToUsername(User user) {
        String currentUsername = user.getUsername();
        String newUsername = currentUsername + " (deactivated: " + LocalDateTime.now() + ")";
        user.setUsername(newUsername);
    }
}

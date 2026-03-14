package com.edumark.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.enums.ResultCode;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysUserDTO;
import com.edumark.system.dto.SysUserQueryDTO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.entity.SysUserRole;
import com.edumark.system.mapper.SysUserMapper;
import com.edumark.system.mapper.SysUserRoleMapper;
import com.edumark.system.service.SysUserService;
import com.edumark.system.vo.SysUserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现
 *
 * @author EduMark
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public PageResult<SysUserVO> getUserPage(SysUserQueryDTO query) {
        Page<SysUserVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SysUserVO> resultPage = baseMapper.selectUserPage(page, query);

        for (SysUserVO userVO : resultPage.getRecords()) {
            List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userVO.getId());
            userVO.setRoleIds(roleIds);
        }

        return PageResult.of(resultPage);
    }

    @Override
    public SysUserVO getUserById(Long userId) {
        SysUserVO userVO = baseMapper.selectUserById(userId);
        if (userVO == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userId);
        userVO.setRoleIds(roleIds);
        return userVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(SysUserDTO dto) {
        SysUser existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setUserType(dto.getUserType());
        user.setSchoolId(dto.getSchoolId());
        user.setStatus(1);
        user.setRemark(dto.getRemark());
        save(user);

        saveUserRoles(user.getId(), dto.getRoleIds());

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUserDTO dto) {
        SysUser existUser = getById(dto.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        SysUser usernameUser = getByUsername(dto.getUsername());
        if (usernameUser != null && !usernameUser.getId().equals(dto.getId())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setUserType(dto.getUserType());
        user.setSchoolId(dto.getSchoolId());
        user.setStatus(dto.getStatus());
        user.setRemark(dto.getRemark());
        updateById(user);

        userRoleMapper.deleteByUserId(dto.getId());
        saveUserRoles(dto.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        removeById(userId);
        userRoleMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        for (Long userId : userIds) {
            deleteUser(userId);
        }
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateById(updateUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateById(updateUser);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public List<String> getPermissionCodes(Long userId) {
        return baseMapper.selectPermissionCodesByUserId(userId);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }
}

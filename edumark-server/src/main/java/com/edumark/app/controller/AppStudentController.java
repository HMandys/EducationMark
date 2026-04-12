package com.edumark.app.controller;

import com.edumark.app.dto.BindStudentDTO;
import com.edumark.app.vo.AppStudentVO;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.Result;
import com.edumark.security.service.LoginUserDetails;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.school.entity.Parent;
import com.edumark.school.entity.ParentStudentBind;
import com.edumark.school.entity.Student;
import com.edumark.school.mapper.ParentMapper;
import com.edumark.school.mapper.ParentStudentBindMapper;
import com.edumark.school.mapper.StudentMapper;
import com.edumark.school.vo.ParentVO;
import com.edumark.school.vo.StudentVO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * App学生管理控制器
 *
 * @author EduMark
 */
@Tag(name = "App-学生管理")
@RestController
@RequestMapping("/app/student")
public class AppStudentController {

    @Resource
    private ParentStudentBindMapper parentStudentBindMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private ParentMapper parentMapper;

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "获取已绑定学生列表（家长）")
    @GetMapping("/bind-list")
    public Result<List<AppStudentVO>> getBindStudents() {
        SysUser user = requireCurrentUser();
        Long parentId = user.getParentId();
        if (parentId == null) {
            return Result.success(List.of());
        }

        List<ParentVO.StudentBindVO> binds = parentStudentBindMapper.selectStudentsByParentId(parentId);
        List<AppStudentVO> students = binds.stream()
                .map(bind -> {
                    StudentVO student = studentMapper.selectVOById(bind.getStudentId());
                    return student == null ? null : toAppStudentVO(student, null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Result.success(students);
    }

    @Operation(summary = "绑定学生（家长）")
    @PostMapping("/bind")
    public Result<AppStudentVO> bindStudent(@Valid @RequestBody BindStudentDTO dto) {
        SysUser user = requireCurrentUser();
        Long parentId = ensureParentProfile(user);

        Student student = studentMapper.selectByNumberAndName(dto.getStudentNumber(), dto.getStudentName());
        if (student == null) {
            throw new BusinessException("未找到匹配的学生信息");
        }
        if (student.getBindCode() == null || student.getBindCode().isBlank()) {
            throw new BusinessException("该学生暂未生成绑定码");
        }
        if (!student.getBindCode().equalsIgnoreCase(dto.getBindCode())) {
            throw new BusinessException("绑定码错误");
        }
        ParentStudentBind existing = parentStudentBindMapper.selectByParentAndStudent(parentId, student.getId());
        if (existing != null) {
            throw new BusinessException("该学生已绑定");
        }

        ParentStudentBind bind = new ParentStudentBind();
        bind.setParentId(parentId);
        bind.setStudentId(student.getId());
        bind.setRelation(9);
        bind.setIsPrimary(0);
        parentStudentBindMapper.insert(bind);

        StudentVO studentVO = studentMapper.selectVOById(student.getId());
        if (studentVO == null) {
            throw new BusinessException("学生不存在");
        }
        return Result.success(toAppStudentVO(studentVO, bind.getCreateTime()));
    }

    @Operation(summary = "解绑学生（家长）")
    @DeleteMapping("/unbind/{studentId}")
    public Result<Void> unbindStudent(@PathVariable Long studentId) {
        SysUser user = requireCurrentUser();
        if (user.getParentId() == null) {
            throw new BusinessException("当前账号未绑定家长信息");
        }
        ParentStudentBind bind = parentStudentBindMapper.selectByParentAndStudent(user.getParentId(), studentId);
        if (bind == null) {
            throw new BusinessException("未找到绑定关系");
        }
        parentStudentBindMapper.deleteById(bind.getId());
        return Result.success();
    }

    @Operation(summary = "获取学生信息（学生本人）")
    @GetMapping("/info")
    public Result<AppStudentVO> getStudentInfo() {
        SysUser user = requireCurrentUser();
        if (user.getStudentId() == null) {
            throw new BusinessException("当前账号未绑定学生信息");
        }

        StudentVO student = studentMapper.selectVOById(user.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return Result.success(toAppStudentVO(student, null));
    }

    private SysUser requireCurrentUser() {
        LoginUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUser() == null) {
            throw new BusinessException("未登录");
        }
        return currentUser.getUser();
    }

    private Long ensureParentProfile(SysUser user) {
        if (user.getParentId() != null) {
            return user.getParentId();
        }

        Parent parent = new Parent();
        parent.setName(user.getRealName() != null && !user.getRealName().isBlank() ? user.getRealName() : user.getUsername());
        parent.setPhone(user.getPhone());
        parent.setStatus(1);
        parentMapper.insert(parent);

        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setParentId(parent.getId());
        sysUserService.updateById(updateUser);
        user.setParentId(parent.getId());
        return parent.getId();
    }

    private AppStudentVO toAppStudentVO(StudentVO student, java.time.LocalDateTime bindTime) {
        AppStudentVO vo = new AppStudentVO();
        vo.setId(student.getId());
        vo.setName(student.getName());
        vo.setStudentNumber(student.getStudentNumber());
        vo.setClassId(student.getClassId());
        vo.setGradeId(student.getGradeId());
        vo.setClassName(student.getClassName());
        vo.setGradeName(student.getGradeName());
        vo.setSchoolId(student.getSchoolId());
        vo.setSchoolName(student.getSchoolName());
        vo.setBindTime(bindTime);
        return vo;
    }
}

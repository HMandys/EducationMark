package com.edumark.app.controller;

import com.edumark.app.dto.BindStudentDTO;
import com.edumark.app.vo.AppStudentVO;
import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * App学生管理控制器
 *
 * @author EduMark
 */
@Tag(name = "App-学生管理")
@RestController
@RequestMapping("/app/student")
public class AppStudentController {

    @Operation(summary = "获取已绑定学生列表（家长）")
    @GetMapping("/bind-list")
    public Result<List<AppStudentVO>> getBindStudents() {
        // TODO: 从SecurityContext获取当前家长ID
        // 查询 parent_student_bind 表获取绑定的学生列表
        List<AppStudentVO> list = new ArrayList<>();

        // Mock数据
        AppStudentVO vo = new AppStudentVO();
        vo.setId(1L);
        vo.setName("张三");
        vo.setStudentNumber("2024001");
        vo.setClassName("三年级1班");
        vo.setGradeName("三年级");
        vo.setSchoolName("示范小学");
        list.add(vo);

        return Result.success(list);
    }

    @Operation(summary = "绑定学生（家长）")
    @PostMapping("/bind")
    public Result<AppStudentVO> bindStudent(@Valid @RequestBody BindStudentDTO dto) {
        // TODO: 实现绑定逻辑
        // 1. 根据姓名+学号+绑定码查询学生
        // 2. 验证绑定码是否正确
        // 3. 创建绑定关系
        // 4. 返回学生信息

        AppStudentVO vo = new AppStudentVO();
        vo.setId(1L);
        vo.setName(dto.getStudentName());
        vo.setStudentNumber(dto.getStudentNumber());
        vo.setClassName("三年级1班");
        vo.setGradeName("三年级");
        vo.setSchoolName("示范小学");

        return Result.success(vo);
    }

    @Operation(summary = "解绑学生（家长）")
    @DeleteMapping("/unbind/{studentId}")
    public Result<Void> unbindStudent(@PathVariable Long studentId) {
        // TODO: 删除绑定关系
        return Result.success();
    }

    @Operation(summary = "获取学生信息（学生本人）")
    @GetMapping("/info")
    public Result<AppStudentVO> getStudentInfo() {
        // TODO: 从SecurityContext获取当前学生ID
        // 查询学生信息

        AppStudentVO vo = new AppStudentVO();
        vo.setId(1L);
        vo.setName("张三");
        vo.setStudentNumber("2024001");
        vo.setClassName("三年级1班");
        vo.setGradeName("三年级");
        vo.setSchoolName("示范小学");

        return Result.success(vo);
    }
}

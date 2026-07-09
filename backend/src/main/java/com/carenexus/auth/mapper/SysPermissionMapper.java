package com.carenexus.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carenexus.auth.entity.SysPermission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    @Select("SELECT p.permission_code FROM sys_permission p "
            + "JOIN sys_role_permission rp ON rp.permission_id = p.id "
            + "WHERE rp.role_id = #{roleId} AND p.is_deleted = 0")
    List<String> selectPermissionCodesByRoleId(@Param("roleId") Long roleId);
}

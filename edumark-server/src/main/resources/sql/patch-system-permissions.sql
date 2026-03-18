USE edumark;

UPDATE sys_permission
SET permission_name = '菜单管理',
    permission_code = 'system:menu:list',
    path = 'menu',
    component = 'system/menu/index',
    icon = 'Menu'
WHERE id = 103;

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1021, 102, '角色查询', 'system:role:query', 3, NULL, NULL, NULL, 1, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1021);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1022, 102, '角色新增', 'system:role:add', 3, NULL, NULL, NULL, 2, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1022);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1023, 102, '角色修改', 'system:role:edit', 3, NULL, NULL, NULL, 3, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1023);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1024, 102, '角色删除', 'system:role:delete', 3, NULL, NULL, NULL, 4, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1024);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1031, 103, '菜单查询', 'system:menu:query', 3, NULL, NULL, NULL, 1, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1031);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1032, 103, '菜单新增', 'system:menu:add', 3, NULL, NULL, NULL, 2, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1032);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1033, 103, '菜单修改', 'system:menu:edit', 3, NULL, NULL, NULL, 3, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1033);

INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted)
SELECT 1034, 103, '菜单删除', 'system:menu:delete', 3, NULL, NULL, NULL, 4, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id = 1034);

INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT (
         SELECT COALESCE(MAX(id), 0) FROM sys_role_permission
       ) + seq.row_num,
       1,
       seq.permission_id
FROM (
       SELECT 1 AS row_num, 1021 AS permission_id
       UNION ALL SELECT 2, 1022
       UNION ALL SELECT 3, 1023
       UNION ALL SELECT 4, 1024
       UNION ALL SELECT 5, 1031
       UNION ALL SELECT 6, 1032
       UNION ALL SELECT 7, 1033
       UNION ALL SELECT 8, 1034
     ) seq
WHERE NOT EXISTS (
  SELECT 1
  FROM sys_role_permission rp
  WHERE rp.role_id = 1
    AND rp.permission_id = seq.permission_id
);

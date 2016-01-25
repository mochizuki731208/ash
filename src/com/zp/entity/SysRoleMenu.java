package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysRoleMenu extends Model<SysRoleMenu>{

    private static final long serialVersionUID = 1L;
    public static final SysRoleMenu dao = new SysRoleMenu();

    public static String TABLE = "sys_role_menu";

	/** 主键 **/
    public static final String ID = "id";

	/** 角色id **/
    public static final String ROLE_ID = "role_id";

	/** 功能id **/
    public static final String MENU_ID = "menu_id";
 }
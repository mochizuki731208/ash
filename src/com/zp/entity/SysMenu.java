package com.zp.entity;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class SysMenu extends Model<SysMenu>{

    private static final long serialVersionUID = 1L;
    public static final SysMenu dao = new SysMenu();

    public static String TABLE = "sys_menu";

	/** 主键 **/
    public static final String ID = "id";

	/** 功能菜单名称 **/
    public static final String MC = "mc";

	/** 类型 **/
    public static final String TYPEID = "typeid";

	/** 地址 **/
    public static final String MENU_URL = "menu_url";

	/** 上级id **/
    public static final String P_ID = "p_id";

	/** 顺序号 **/
    public static final String SEQ_NUM = "seq_num";

	/** 扩展A **/
    public static final String EXTA = "exta";

	/** 扩展B **/
    public static final String EXTB = "extb";

	/** css样式 **/
    public static final String CSS = "css";

	/** 备注 **/
    public static final String REMARK = "remark";
 }
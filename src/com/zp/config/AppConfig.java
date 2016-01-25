package com.zp.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.render.ViewType;
import com.zp.dir.CmsInterceptor;
import com.zp.handler.AccessAbHandler;
import com.zp.handler.BaseHandler;
import com.zp.interceptor.ContextInterceptor;
import com.zp.interceptor.DirectiveInterceptor;
import com.zp.tools.MyKit;

import freemarker.template.TemplateModelException;

public class AppConfig extends JFinalConfig {
	public void configConstant(Constants me) {
		me.setEncoding("utf-8");
//		me.setViewType(ViewType.FREE_MARKER);
		me.setMaxPostSize(1024 * 1024 * 1024);
		me.setFreeMarkerTemplateUpdateDelay(0);
		// loadPropertyFile("/classes/jdbc.property");
		me.setDevMode(false);
	}

	public void configRoute(Routes me) {
		// AutoControllerRegist.regist(me);
		me.add(new Route());
	}

	public void configPlugin(Plugins me) {

//		DruidPlugin dp = new DruidPlugin(
//				"jdbc:mysql://localhost:3306/ash?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull",
//				"root", "ailk12#$");
		loadPropertyFile("config.properties");
//		DruidPlugin dp = new DruidPlugin(
//				"jdbc:mysql://127.0.0.1:3306/ash?characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull",
//				"root", "123456");
		DruidPlugin dp = new DruidPlugin(getProperty("jdbcUrl"),getProperty("user"),getProperty("password"));
		// System.out.println(getProperty("jdbcUrl"));
		// System.out.println(getProperty("user"));
		// System.out.println(getProperty("password"));

		/**
		 * String driver="oracle.jdbc.driver.OracleDriver"; String
		 * url="jdbc:oracle:thin:@127.0.0.1:1521:orcl "; String
		 * username="jfinal"; String password="jfinal"; DruidPlugin dp=new
		 * DruidPlugin(url, username, password, driver);
		 */
//		dp.addFilter(new StatFilter());
//		WallFilter wall = new WallFilter();
//		wall.setDbType("mysql");
//		dp.addFilter(wall);
		me.add(dp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setShowSql(true);

		// oracle数据库专用配置
		// arp.setDialect(new OracleDialect());
		// arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		//

		me.add(com.zp.config.MapTable.getArp(arp));

		// 添加缓存，增加并发处理能力
		me.add(new EhCachePlugin());

		//配置定时处理
		QuartzPlugin quartzPlugin = new QuartzPlugin("job.properties");
		me.add(quartzPlugin);
	}

	public void configInterceptor(Interceptors me) {
		me.add(new ContextInterceptor());
		me.add(new DirectiveInterceptor());
		me.add(new CmsInterceptor());
		
		me.add(new TxByRegex(".*save.*"));
		// me.add(new TxByActionMethods("save","update"));
	}

	public void configHandler(Handlers me) {
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid");
		me.add(dvh);
		me.add(new BaseHandler());
		me.add(new AccessAbHandler());
	}

	public void afterJFinalStart() {
		super.afterJFinalStart();
		try {
			FreeMarkerRender.getConfiguration().setSharedVariable("mykit",
					new MyKit());
			FreeMarkerRender.getConfiguration().setDefaultEncoding("utf-8");
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		com.jfinal.core.JFinal.start("WebRoot", 80, "/ash", 5);
	}
}
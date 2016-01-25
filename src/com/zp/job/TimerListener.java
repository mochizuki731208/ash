package com.zp.job;

import java.util.Calendar;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class TimerListener implements ServletContextListener {
	private Timer timer;
	private AnnleeTimerTask task;
	
	public void contextDestroyed(ServletContextEvent sce) {
		timer.cancel();
		System.out.println("定时器已销毁");
	}

	public void contextInitialized(ServletContextEvent sce) {
		timer = new java.util.Timer(true); 
		task = new AnnleeTimerTask(sce.getServletContext());
		System.out.println("定时器已启动");
		timer.schedule(task, Calendar.getInstance ().getTime(), 60 * 1000);
		System.out.println("已经添加任务调度表");
	}

}

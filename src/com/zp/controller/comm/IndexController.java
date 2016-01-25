package com.zp.controller.comm;

import com.jfinal.core.Controller;

public class IndexController extends Controller {
	public void index(){
		redirect("/ab");
	}
}

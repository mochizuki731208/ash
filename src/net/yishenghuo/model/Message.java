package net.yishenghuo.model;

public class Message extends BaseBean{
     private String id;
     private String user_id;
     private String role_id;
     private String mes_title;
     private String mes_type;
     private String isread;
     private String text;
     private String send_date;
     
     public Message(){
    	 
     }
	public Message(String id, String user_id, String role_id) {
		this.id = id;
		this.user_id = user_id;
		this.role_id = role_id;
	}
	
	public Message(String id, String user_id, String role_id, String mes_type, String isread, String text,
			String send_date) {
		this.id = id;
		this.user_id = user_id;
		this.role_id = role_id;
		this.mes_type = mes_type;
		this.isread = isread;
		this.text = text;
		this.send_date = send_date;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	public String getMes_type() {
		return mes_type;
	}
	public void setMes_type(String mes_type) {
		this.mes_type = mes_type;
	}
	public String getIsread() {
		return isread;
	}
	public void setIsread(String isread) {
		this.isread = isread;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}
	public String getMes_title() {
		return mes_title;
	}
	public void setMes_title(String mes_title) {
		this.mes_title = mes_title;
	}
     
}

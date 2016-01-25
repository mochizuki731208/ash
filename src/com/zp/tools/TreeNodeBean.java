package com.zp.tools;
public class TreeNodeBean {
	
	private String parentID;
	private String curtreeID;
	private String showText;
	private boolean haveChecked;
	private boolean isChecked;
	private StringBuffer str_Script= null;
	public TreeNodeBean(String treeName) {
		//str_Script = "<script type=text/javascript>\n"+treeName;
		str_Script =new StringBuffer("");
	}
	public TreeNodeBean(String parentID,String curtreeID,String showText,boolean haveChecked,boolean isChecked) {
		this.parentID = parentID;
		this.curtreeID = curtreeID;
		this.showText = showText;
		this.haveChecked = haveChecked;
		this.isChecked = isChecked;
	}
	/**
	 * @日期：2009年12月9日16:36:47
	 * @作者：李金伟
	 * @描述：构建树根节点。
	 * @param rootID   根节点ID号
	 * @param rootText 根节点显示值
	 * @param haveChecked  根节点是否是CheckBox;
	 * @param isChecked    根节点是否Checked（选中）
	 */
	public void addRootNode(String rootID,String rootText,boolean haveChecked,boolean isChecked){
		StringBuffer stringBuffer = new StringBuffer(this.str_Script);
		stringBuffer.append("([\n{'pid':'-1','text':'"+rootText+"','id':'"+rootID+"',");
		if(haveChecked==true){
			stringBuffer.append("'checked':");
		}else{
			stringBuffer.append("'unchecked':");
		}
		if(isChecked == true){
			stringBuffer.append("1}");
		}else{
			stringBuffer.append("0}");
		}
		this.str_Script = stringBuffer;
	}
	
	/**
	 * 日期：2009年12月9日16:36:47
	 * 作者：李金伟
	 * 描述：构建树节点。
	 * @param parentID  上级节点ID
	 * @param currentID 当前节点ID
	 * @param showText  显示值
	 * @param haveChecked 是否是CheckBox;
	 * @param isChecked   是否Checked（选中）
	 */
	public void addTreeNode(String parentID,String currentID,String showText,boolean haveChecked,boolean isChecked){
		this.str_Script.append(",\n{'pid':'"+parentID+"','text':'"+showText+"','id':'"+currentID+"'");
		if(haveChecked==true){
			this.str_Script.append(",'checked':");
		}else{
			this.str_Script.append(",'unchecked':");
		}
		if(isChecked==true){
			this.str_Script.append("1}");
		}else{
			this.str_Script.append("0}");
		}
	}
	
	public String addTreeTail(String treePostion){
		this.str_Script.append("]);");//\n var otree = new OTree({\n panel: document.getElementById('"
			//+treePostion+"'),\n data:TreeData\n});\notree.paint();\n otree.addListener('click',ClickEvent);\n</script>";
		return this.str_Script.toString();
	}
	/**
	 * 日期：2009年12月9日16:36:47
	 * 作者：李金伟
	 * 描述：构建树节点。
	 * @param treeNodeBean  可以是传入的TreeNode对象
	 */
	public void addTreeNode(TreeNodeBean treeNodeBean){
		this.str_Script.append(",{'pid':'"+treeNodeBean.getParentID()+"','text':'"+treeNodeBean.getShowText()+"','id':'"+treeNodeBean.getCurtreeID()+"'");
		if(treeNodeBean.isHaveChecked()==true){
			this.str_Script.append(",'checked':");
		}else{
			this.str_Script.append(",'unchecked':");
		}
		if(treeNodeBean.isChecked()==true){
			this.str_Script.append("1}");
		}else{
			this.str_Script.append("0}");
		}
	}
	
	
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getCurtreeID() {
		return curtreeID;
	}
	public void setCurtreeID(String curtreeID) {
		this.curtreeID = curtreeID;
	}
	public String getShowText() {
		return showText;
	}
	public void setShowText(String showText) {
		this.showText = showText;
	}
	public boolean isHaveChecked() {
		return haveChecked;
	}
	public void setHaveChecked(boolean haveChecked) {
		this.haveChecked = haveChecked;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public StringBuffer getStr_Script() {
		return str_Script;
	}
	public void setStr_Script(StringBuffer str_Script) {
		this.str_Script = str_Script;
	}
	
	
}

package com.zp.tools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 映射数据库，自动生成Entity
 * @author WWF
 */
public class CreateJfinalEntityUtil {
	
	private static Connection conn = null;
	private static final String URL;
	private static final String JDBC_DRIVER;
	private static final String USER_NAME;
	private static final String PASSWORD;
	private static final String DATABASENAME;
	private static final String PACKAGENAME;
	static {
		DATABASENAME = "ash";
		URL = "jdbc:mysql://localhost:3306/"+DATABASENAME+"?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull";
		JDBC_DRIVER = "com.mysql.jdbc.Driver";
		USER_NAME = "root";
		PASSWORD = "root";
		PACKAGENAME = "com.zp.entity";
	}
	
	/**
	 * 获得连接
	 * @return
	 */
	public static Connection getConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 关闭数据库
	 */
	public static void closeConnection(){
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成字段静态声明
	 * @param connection
	 * @param tableName
	 * @return
	 */
	private static String CreateEntityString(String tableName){
		String result  = "package "+PACKAGENAME+";\n\n";
		result += "import com.jfinal.plugin.activerecord.Model; \n\n";
		result += "public class "+StringUtil.getClassName(tableName)+" extends Model<"+StringUtil.getClassName(tableName)+">{\n\n";
		result += "    private static final long serialVersionUID = 1L;\n";
		result += "    public static final "+StringUtil.getClassName(tableName)+" dao = new "+StringUtil.getClassName(tableName)+"();\n\n";
		
		result += "    public static String TABLE = \""+tableName+"\";" ;
		String sql = "SELECT column_name, column_comment  FROM   information_schema.columns  WHERE table_name = '"+tableName+"'	AND table_schema = '"+DATABASENAME+"'";
		System.out.println(sql);
		
		try {
			Statement statement = conn.createStatement();
			ResultSet resultSet =  statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1)!=null&&resultSet.getString(1).length() > 0) {
					String string = resultSet.getString(1);
					
					String row = "    public static final String "+ string.toUpperCase(Locale.CHINA) +" = \""+string+"\";";
					String note = "\t/** "+resultSet.getString(2)+" **/";
					result += "\n\n"+note + "\n" +row;
				}
			}
			resultSet.close();
			statement.close();
			result+="\n }";
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获得数据库的所有表名
	 * @return
	 */
	public static List<String> getAllTables(String schema){
		String sql = "SELECT table_name FROM information_schema.TABLES WHERE table_schema = '"+schema+"'";;
		try {
			List<String> result = new ArrayList<String>();
			Statement statement = conn.createStatement();
			ResultSet resultSet =  statement.executeQuery(sql);
			while (resultSet.next()) {
				if (resultSet.getString(1)!=null&&resultSet.getString(1).length()>0){
					result.add(resultSet.getString(1));
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * 生成Entity
	 */
	public static void CreateEntity(){
		try {
			getConnection();
			List<String> tables = getAllTables(DATABASENAME);
			for (int i = 0; i < tables.size(); i++) {
				File createFolder = new File(System.getProperty("user.dir")+"/src/"+PACKAGENAME.replace(".", "/"));
				//路径不存在，生成文件夹
				if (!createFolder.exists()) {
					createFolder.mkdirs();
				}
				String entityString = CreateEntityString(tables.get(i).trim());//写入内容
				String str_java = createFolder.getAbsolutePath()+"/"+StringUtil.getClassName(tables.get(i))+".java";//写入路径
				//如果已经存在，则将其删除。
				File file = new File("str_java");
				if(file.exists()){
					file.delete();
				}
				//写入文件 
				BufferedWriter out  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(str_java),"UTF-8"));
				out.write(entityString);
	            out.close();
	            out = null;
				
			}
			
			String result  = "package com.zp.config;\n\n";
			result += "import com.jfinal.plugin.IPlugin; \n";
			result += "import com.jfinal.plugin.activerecord.ActiveRecordPlugin; \n\n";
			result += "public class MapTable{\n\n";
			result += "    public static IPlugin getArp(ActiveRecordPlugin arp) {\n";
			
			for (int i = 0; i < tables.size(); i++) {
				result+="\n\t\tarp.addMapping(\""+tables.get(i)+"\", "+PACKAGENAME+"."+StringUtil.getClassName(tables.get(i))+".class);";
			}
			result+="\n\n\t\treturn arp;";
			result+="\n\t }";
			result+="\n }";
			
			File createFolder = new File(System.getProperty("user.dir")+"/src/com/zp/config");
			//路径不存在，生成文件夹
			if (!createFolder.exists()) {
				createFolder.mkdirs();
			}
			
			String str_java = createFolder.getAbsolutePath()+"/MapTable.java";//写入路径
			//如果已经存在，则将其删除。
			File file = new File("str_java");
			if(file.exists()){
				file.delete();
			}
			//写入文件 
			BufferedWriter out  = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(str_java),"UTF-8"));
			out.write(result);
            out.close();
            out = null;
			
			
			closeConnection();
			System.out.println("生成成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		CreateEntity();
	}

	

}
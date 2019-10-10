package com.yp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
/**
 * 自己制作的ORM框架，从数据库到对象的映射
 * @author 追
 *
 */
public class ReverseProject {

	public static void main(String[] args) throws Exception {
		BlueGenerate blueGenerate = new BlueGenerate();
		blueGenerate.init();
		blueGenerate.execGenerate();
	}

}

class BlueGenerate {
	private Connection connection = null;
	private DatabaseMetaData metaData = null;
	ResultSet tables = null;

	public void init() {
		// 加载驱动
		try {
			Class.forName(Utils.getDriver());
			// 建立连接
			connection = DriverManager.getConnection(Utils.getUrl(),
					Utils.getUserName(), Utils.getPassWord());
			// System.out.println(connection);
			metaData = connection.getMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void execGenerate() {
		try {
			tables = metaData.getTables(Utils.getDataBase(),
					Utils.getDataBase(), null, null);
			// 生成包名
			String packageName = Utils.getPackage();
			String dirPath = Utils.creatDir(packageName);
			while (tables.next()) {
				String tableName = tables.getString(3);// 得到表名
				// System.out.println(tableName);
				// 这里就可以进行类的创建了

				ResultSet columns = metaData.getColumns(null, null, tableName,
						null);
				Map<String, String> colAndTypeMap = new HashMap<String, String>();
				Map<String, String> remarkAndOtherMap = new HashMap<String, String>();

				// 线程不安全，但效率高
				StringBuilder content = new StringBuilder();
				while (columns.next()) {
					String columnsName = columns.getString("COLUMN_NAME");
					String typeName = columns.getString("TYPE_NAME");
					String remarks = columns.getString("REMARKS"); // 表备注
					// 把所需要的东西放入集合，后面直接取出。如果在这个循环里面导包，构建类，后面会发生混乱，会重复输出一些东西
					colAndTypeMap.put(columnsName, typeName);
					remarkAndOtherMap.put(columnsName, remarks);

					// System.out.println("列名:"+columnsName+"  类型:"+typeName+"  注释:"+remarks);
				}
				// content.append(Utils.IMPORT_PACK_MAP.get(typeName));
				/**
				 * 创建类 1.指明输出地点 2.构建类名 3.写入属性 4.封装属性 5.创建包 6.创建 JAVA BEAN
				 */
				// 導入類名
				content.append("package " + packageName + ";\n\n");

				// 导包
				Set<Entry<String, String>> entrySet = colAndTypeMap.entrySet();// 线程安全的，实现由hashmap,一般在Map遍历的时候，才会用到
				for (Entry<String, String> entry : entrySet) {
					// append()里面不得为空，所以判断下
					String nessaryImport = Utils.IMPORT_PACK_MAP.get(entry
							.getValue());
					if (nessaryImport != null)
						content.append(nessaryImport + "\n");
				}
				String generateClassName = Utils.generateClassName(tableName);
				// 创建类
				content.append("public class " + generateClassName + " {"
						+ "\n");

				// 创建属性
				for (Entry<String, String> entry : entrySet) {
					content.append("\t" + "private "
							+ Utils.SQL_TYPE2JAVA_TYPE.get(entry.getValue())
							+ " " + Utils.generateFileName(entry.getKey())
							+ "; //" + remarkAndOtherMap.get(entry.getKey())
							+ "\n\n");// 添加注釋，
				}

				// 生成get set方法
				for (Entry<String, String> entry : entrySet) {
					String natureName = Utils.generateFileName(entry.getKey());
					// 首字母大寫
					String propertyName = Utils.generateClassName(natureName);
					// get
					content.append("\t" + "public "
							+ Utils.SQL_TYPE2JAVA_TYPE.get(entry.getValue())
							+ " get" + propertyName + "()" + " {\n\t\t"
							+ "return " + natureName + ";\n\t}\n\n");
					// set
					content.append("\t" + "public " + "void" + " set"
							+ propertyName + "("
							+ Utils.SQL_TYPE2JAVA_TYPE.get(entry.getValue())
							+ " " + natureName + ")" + " {\n\t\t" + "this."
							+ natureName + " = " + natureName + ";\n\t}\n\n");
				}

				content.append("}");

				// 寫入內容到文件
				Utils.writeClazz(dirPath + File.separator + generateClassName
						+ ".java", content.toString());

				// System.out.println(content.toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Utils.closeStream(connection, null, tables);
		}
		System.out.println("success");
	}

	/**
	 * 工具类 内部类
	 * 
	 * @author 追
	 *
	 */
	static class Utils {
		// sql映射到java类型
		public static final Map<String, String> SQL_TYPE2JAVA_TYPE = new HashMap<>();
		// 类型对应的导入包
		public final static Map<String, String> IMPORT_PACK_MAP = new HashMap<>();
		// 读取配置文件
		private final static Properties JDBC_PROPERTIES = new Properties();
		/**
		 * 初始化时加载进去 Ctrl + Shift + Y 将大写改为小写
		 */
		static {
			SQL_TYPE2JAVA_TYPE.put("int unsigned", "Integer");
			SQL_TYPE2JAVA_TYPE.put("varchar", "String");
			SQL_TYPE2JAVA_TYPE.put("timestamp", "Date");
			SQL_TYPE2JAVA_TYPE.put("int", "Integer");
			SQL_TYPE2JAVA_TYPE.put("tinyint", "Byte");
			SQL_TYPE2JAVA_TYPE.put("datetime", "Date");
			SQL_TYPE2JAVA_TYPE.put("char", "String");
			SQL_TYPE2JAVA_TYPE.put("text", "String");
			SQL_TYPE2JAVA_TYPE.put("bigint", "long");
			SQL_TYPE2JAVA_TYPE.put("tinyint", "long");
			SQL_TYPE2JAVA_TYPE.put("BIT", "boolean");
			SQL_TYPE2JAVA_TYPE.put("smallint", "Integer");
			SQL_TYPE2JAVA_TYPE.put("mediumint", "Integer");
			SQL_TYPE2JAVA_TYPE.put("decimal", "double");
			SQL_TYPE2JAVA_TYPE.put("double", "double");
			SQL_TYPE2JAVA_TYPE.put("float", "float");

			IMPORT_PACK_MAP.put("datetime", "import java.util.Date;");
			IMPORT_PACK_MAP.put("timestamp", "import java.util.Date;");

			InputStream resourceAsStream = Utils.class.getClassLoader()
					.getResourceAsStream("jdbc_path.properties");
			try {
				JDBC_PROPERTIES.load(resourceAsStream);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);// 加载出现错误直接退出
			}
		}

		public static String getDriver() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Driver");
			if (property == null) {
				throw new RuntimeException("驱动加载失败");
			}
			return property;
		}

		public static String getUserName() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.userName");
			if (property == null) {
				throw new RuntimeException("用户名加载失败");
			}
			return property;
		}

		public static String getPassWord() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.passWord");
			if (property == null) {
				throw new RuntimeException("密码加载失败");
			}
			return property;
		}

		public static String getUrl() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Url");
			if (property == null) {
				throw new RuntimeException("连接加载失败");
			}
			return property;
		}

		public static String getDataBase() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Url");
			if (property == null) {
				throw new RuntimeException("连接加载失败");
			}
			String urlProperty = property.substring(
					property.lastIndexOf("/") + 1, property.length());
			if (urlProperty == null) {
				throw new RuntimeException("数据库加载失败");
			}
			return urlProperty;
		}

		public static String getPackage() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.packPath");
			if (property == null) {
				throw new RuntimeException("包路径加载失败");
			}
			return property;
		}

		/**
		 * 
		 * @param tableName
		 *            数据库表名，对应java中的类名
		 * @return 类名
		 * 
		 */
		public static String generateClassName(String tableName) {
			String clazzName = "";
			StringBuilder classContent = new StringBuilder();
			String[] split = tableName.split("_");
			for (int i = 0; i < split.length; i++) {
				clazzName = classContent
						.append(split[i].substring(0, 1).toUpperCase())
						.append(split[i].substring(1, split[i].length()))
						.toString();
			}
			return clazzName;
		}

		/**
		 * 
		 * @param columnsName
		 *            数据库列名，java属性名
		 * @return 属性名
		 * 
		 *         属性名首字母小写
		 */
		public static String generateFileName(String columnsName) {
			String fileName = "";
			StringBuilder fileContent = new StringBuilder();
			String[] split = columnsName.split("_");
			for (int i = 0; i < split.length; i++) {
				if (0 == i) {
					fileName = fileContent
							.append(split[i].substring(0, 1).toLowerCase())
							.append(split[i].substring(1, split[i].length()))
							.toString();
				} else {
					fileName = fileContent
							.append(split[i].substring(0, 1).toUpperCase())
							.append(split[i].substring(1, split[i].length()))
							.toString();
				}
			}

			return fileName;
		}

		/**
		 * 
		 * @param packageName
		 *            包名 xxx.xxx.xx
		 * @return 包路徑
		 */
		public static String creatDir(String packageName) {
			String dirPath = null;
			try {
				String rootName = new File("").getCanonicalPath()
						+ File.separator + "src";
				String[] split = packageName.split("\\.");
				StringBuilder dirBuilder = new StringBuilder();
				dirBuilder.append(rootName);
				for (String string : split) {
					dirBuilder.append(File.separator + string);
				}
				File file = new File(dirBuilder.toString());
				file.mkdirs();
				dirPath = file.getCanonicalPath();
				System.out.println("生成路径成功");
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("生成失败");
			}

			return dirPath;
		}

		/**
		 * 
		 * @param dirPath
		 *            磁盤的路徑，
		 * @param clazzContent
		 *            寫入java文件的內容
		 */
		public static void writeClazz(String dirPath, String clazzContent) {
			FileOutputStream fileOutputStream = null;
			File file = new File(dirPath);// 輸出到磁盤的路徑
			try {
				fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(clazzContent.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						fileOutputStream = null;
					}
				}
			}
		}

		/**
		 * 
		 * @param connection
		 * @param statement
		 * @param resultSet
		 *            关闭流
		 */
		public static void closeStream(Connection connection,
				Statement statement, ResultSet resultSet) {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

}

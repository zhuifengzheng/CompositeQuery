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
 * �Լ�������ORM��ܣ������ݿ⵽�����ӳ��
 * @author ׷
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
		// ��������
		try {
			Class.forName(Utils.getDriver());
			// ��������
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
			// ���ɰ���
			String packageName = Utils.getPackage();
			String dirPath = Utils.creatDir(packageName);
			while (tables.next()) {
				String tableName = tables.getString(3);// �õ�����
				// System.out.println(tableName);
				// ����Ϳ��Խ�����Ĵ�����

				ResultSet columns = metaData.getColumns(null, null, tableName,
						null);
				Map<String, String> colAndTypeMap = new HashMap<String, String>();
				Map<String, String> remarkAndOtherMap = new HashMap<String, String>();

				// �̲߳���ȫ����Ч�ʸ�
				StringBuilder content = new StringBuilder();
				while (columns.next()) {
					String columnsName = columns.getString("COLUMN_NAME");
					String typeName = columns.getString("TYPE_NAME");
					String remarks = columns.getString("REMARKS"); // ��ע
					// ������Ҫ�Ķ������뼯�ϣ�����ֱ��ȡ������������ѭ�����浼���������࣬����ᷢ�����ң����ظ����һЩ����
					colAndTypeMap.put(columnsName, typeName);
					remarkAndOtherMap.put(columnsName, remarks);

					// System.out.println("����:"+columnsName+"  ����:"+typeName+"  ע��:"+remarks);
				}
				// content.append(Utils.IMPORT_PACK_MAP.get(typeName));
				/**
				 * ������ 1.ָ������ص� 2.�������� 3.д������ 4.��װ���� 5.������ 6.���� JAVA BEAN
				 */
				// �������
				content.append("package " + packageName + ";\n\n");

				// ����
				Set<Entry<String, String>> entrySet = colAndTypeMap.entrySet();// �̰߳�ȫ�ģ�ʵ����hashmap,һ����Map������ʱ�򣬲Ż��õ�
				for (Entry<String, String> entry : entrySet) {
					// append()���治��Ϊ�գ������ж���
					String nessaryImport = Utils.IMPORT_PACK_MAP.get(entry
							.getValue());
					if (nessaryImport != null)
						content.append(nessaryImport + "\n");
				}
				String generateClassName = Utils.generateClassName(tableName);
				// ������
				content.append("public class " + generateClassName + " {"
						+ "\n");

				// ��������
				for (Entry<String, String> entry : entrySet) {
					content.append("\t" + "private "
							+ Utils.SQL_TYPE2JAVA_TYPE.get(entry.getValue())
							+ " " + Utils.generateFileName(entry.getKey())
							+ "; //" + remarkAndOtherMap.get(entry.getKey())
							+ "\n\n");// ���עጣ�
				}

				// ����get set����
				for (Entry<String, String> entry : entrySet) {
					String natureName = Utils.generateFileName(entry.getKey());
					// ����ĸ��
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

				// ������ݵ��ļ�
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
	 * ������ �ڲ���
	 * 
	 * @author ׷
	 *
	 */
	static class Utils {
		// sqlӳ�䵽java����
		public static final Map<String, String> SQL_TYPE2JAVA_TYPE = new HashMap<>();
		// ���Ͷ�Ӧ�ĵ����
		public final static Map<String, String> IMPORT_PACK_MAP = new HashMap<>();
		// ��ȡ�����ļ�
		private final static Properties JDBC_PROPERTIES = new Properties();
		/**
		 * ��ʼ��ʱ���ؽ�ȥ Ctrl + Shift + Y ����д��ΪСд
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
				System.exit(-1);// ���س��ִ���ֱ���˳�
			}
		}

		public static String getDriver() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Driver");
			if (property == null) {
				throw new RuntimeException("��������ʧ��");
			}
			return property;
		}

		public static String getUserName() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.userName");
			if (property == null) {
				throw new RuntimeException("�û�������ʧ��");
			}
			return property;
		}

		public static String getPassWord() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.passWord");
			if (property == null) {
				throw new RuntimeException("�������ʧ��");
			}
			return property;
		}

		public static String getUrl() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Url");
			if (property == null) {
				throw new RuntimeException("���Ӽ���ʧ��");
			}
			return property;
		}

		public static String getDataBase() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.Url");
			if (property == null) {
				throw new RuntimeException("���Ӽ���ʧ��");
			}
			String urlProperty = property.substring(
					property.lastIndexOf("/") + 1, property.length());
			if (urlProperty == null) {
				throw new RuntimeException("���ݿ����ʧ��");
			}
			return urlProperty;
		}

		public static String getPackage() {
			String property = JDBC_PROPERTIES.getProperty("jdbc.packPath");
			if (property == null) {
				throw new RuntimeException("��·������ʧ��");
			}
			return property;
		}

		/**
		 * 
		 * @param tableName
		 *            ���ݿ��������Ӧjava�е�����
		 * @return ����
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
		 *            ���ݿ�������java������
		 * @return ������
		 * 
		 *         ����������ĸСд
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
		 *            ���� xxx.xxx.xx
		 * @return ��·��
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
				System.out.println("����·���ɹ�");
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("����ʧ��");
			}

			return dirPath;
		}

		/**
		 * 
		 * @param dirPath
		 *            �űP��·����
		 * @param clazzContent
		 *            ����java�ļ��ă���
		 */
		public static void writeClazz(String dirPath, String clazzContent) {
			FileOutputStream fileOutputStream = null;
			File file = new File(dirPath);// ݔ�����űP��·��
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
		 *            �ر���
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

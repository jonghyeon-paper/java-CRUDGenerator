import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CRUDGenerator {
	
	private Class<?> targetClass;

	public CRUDGenerator(Class<?> clazz) {
		this.targetClass = clazz;
	}	
	
	public String generateSelectQuery(Object data) {
		StringBuffer columns = new StringBuffer();
		StringBuffer conditions = new StringBuffer();
		for (Method column : targetClass.getDeclaredMethods()) {
			if (!column.getName().startsWith("get")) {
				continue;
			}
			String columnName = this.camelCaseToUnderScore(column.getName().replaceAll("get", ""));
			columns.append("     , ").append(columnName).append("\n");
			
			Object columnValue = null;
			try {
				Method dataMethod = data.getClass().getDeclaredMethod(column.getName());
				columnValue = dataMethod.invoke(data);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//e.printStackTrace();
			}
			String stringValue = "";
			if (columnValue == null) {
				continue;
			} else if (Number.class.isAssignableFrom(columnValue.getClass())) {
				stringValue = columnValue.toString();
			} else {
				stringValue = "'" + columnValue.toString() + "'";
			}
			conditions.append("   AND ").append(columnName).append(" = ").append(stringValue).append("\n");
		}
		
		String tableName = this.camelCaseToUnderScore(targetClass.getSimpleName());
		return new StringBuilder()
				.append("SELECT\n")
				.append("       ").append(columns.substring(7))
				.append("  FORM ").append(tableName).append("\n")
				.append(" WHERE ").append(conditions.length() > 0 ? conditions.substring(7) : "")
				.toString();
	}
	
	public String generateInsertQuery(Object data) {
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		for (Method column : targetClass.getDeclaredMethods()) {
			if (!column.getName().startsWith("get")) {
				continue;
			}
			String columnName = this.camelCaseToUnderScore(column.getName().replaceAll("get", ""));
			columns.append("    ").append(columnName).append(",\n");
			
			Object columnValue = null;
			try {
				Method dataMethod = data.getClass().getDeclaredMethod(column.getName());
				columnValue = dataMethod.invoke(data);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//e.printStackTrace();
			}
			String stringValue = "";
			if (columnValue == null) {
				stringValue = "null";
			} else if (Number.class.isAssignableFrom(columnValue.getClass())) {
				stringValue = columnValue.toString();
			} else {
				stringValue = "'" + columnValue.toString() + "'";
			}
			values.append("    ").append(stringValue).append(",\n");
		}
		String tableName = this.camelCaseToUnderScore(targetClass.getSimpleName());
		return new StringBuilder()
				.append("INSERT INTO ").append(tableName).append("(\n")
				.append(columns.substring(0, columns.length() - 2)).append("\n")
				.append(")VALUES(\n")
				.append(values.substring(0, values.length() - 2)).append("\n")
				.append(")")
				.toString();
	}
	
	public String generateUpdateQuery(Object data) {
		StringBuilder columns = new StringBuilder();
		StringBuilder conditions = new StringBuilder();
		for (Method column : targetClass.getDeclaredMethods()) {
			if (!column.getName().startsWith("get")) {
				continue;
			}
			
			String columnName = this.camelCaseToUnderScore(column.getName().replaceAll("get", ""));
			
			Object columnValue = null;
			try {
				Method dataMethod = data.getClass().getDeclaredMethod(column.getName());
				columnValue = dataMethod.invoke(data);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//e.printStackTrace();
			}
			String stringValue = null;
			if (columnValue == null) {
				stringValue = "null";
			} else if (Number.class.isAssignableFrom(columnValue.getClass())) {
				stringValue = columnValue.toString();
			} else {
				stringValue = "'" + columnValue.toString() + "'";
			}
			
			if (column.isAnnotationPresent(Primarykey.class)) {
				conditions.append(columnName).append(" = ").append(stringValue);
			} else {
				columns.append("     , ").append(columnName).append(" = ").append(stringValue).append("\n");
			}
		}
		
		String tableName = this.camelCaseToUnderScore(targetClass.getSimpleName());
		return new StringBuilder()
				.append("UPDATE ").append(tableName).append("\n")
				.append("   SET\n")
				.append("       ").append(columns.substring(7))
				.append(" WHERE ").append(conditions)
				.toString();
	}
	
	public String generateDeleteQuery(Object data) {
		StringBuilder conditions = new StringBuilder();
		for (Method column : targetClass.getDeclaredMethods()) {
			if (!column.getName().startsWith("get")) {
				continue;
			}
			if (!column.isAnnotationPresent(Primarykey.class)) {
				continue;
			}
			String columnName = this.camelCaseToUnderScore(column.getName().replaceAll("get", ""));
			
			Object columnValue = null;
			try {
				Method dataMethod = data.getClass().getDeclaredMethod(column.getName());
				columnValue = dataMethod.invoke(data);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				//e.printStackTrace();
			}
			String stringValue = null;
			if (columnValue == null) {
				stringValue = "null";
			} else if (Number.class.isAssignableFrom(columnValue.getClass())) {
				stringValue = columnValue.toString();
			} else {
				stringValue = "'" + columnValue.toString() + "'";
			}
			conditions.append(columnName).append(" = ").append(stringValue);
			break;
		}
		
		String tableName = this.camelCaseToUnderScore(targetClass.getSimpleName());
		return new StringBuilder()
				.append("DELETE FROM ").append(tableName).append("\n")
				.append(" WHERE ").append(conditions)
				.toString();
	}
	
	private String camelCaseToUnderScore(String targetString) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < targetString.length(); i++) {
			char c = targetString.charAt(i);
			if (i == 0) {
				sb.append(String.valueOf(c).toUpperCase());
			} else if (c >= 65 && c <= 90) {
				sb.append("_");
				sb.append(String.valueOf(c));
			} else {
				sb.append(String.valueOf(c).toUpperCase());
			}
		}
		return sb.toString();
	}
}

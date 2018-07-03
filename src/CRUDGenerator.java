import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CRUDGenerator {
	
	private Class<?> targetClass;

	public CRUDGenerator(Class<?> clazz) {
		this.targetClass = clazz;
	}	
	
	public String createSelectQuery(Object condition) {
		StringBuffer sb = new StringBuffer();
		for (Method column : targetClass.getDeclaredMethods()) {
			if (!column.getName().startsWith("get")) {
				continue;
			}
			if (sb.length() == 0) {
				sb.append("SELECT ");
			} else {
				sb.append("     , ");
			}
			String columnName = column.getName().replaceAll("get", "");
			for (int i = 0; i < columnName.length(); i++) {
				char c = columnName.charAt(i);
				if (i == 0) {
					sb.append(String.valueOf(c).toUpperCase());
				} else if (c >= 65 && c <= 90) {
					sb.append("_");
					sb.append(String.valueOf(c));
				} else {
					sb.append(String.valueOf(c).toUpperCase());
				}
			}
			sb.append("\n");
		}
		
		String className = targetClass.getSimpleName();
		for (int i = 0; i < className.length(); i++) {
			char c = className.charAt(i);
			if (i == 0) {
				sb.append("  FORM ");
				sb.append(String.valueOf(c).toUpperCase());
			} else if (c >= 65 && c <= 90) {
				sb.append("_");
				sb.append(String.valueOf(c));
			} else {
				sb.append(String.valueOf(c).toUpperCase());
			}
		}
		sb.append("\n");
		
		boolean isFirst = true;
		for (Method conditionItem : condition.getClass().getDeclaredMethods()) {
			if (!conditionItem.getName().startsWith("get")) {
				continue;
			}
			try {
				targetClass.getDeclaredMethod(conditionItem.getName());
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			
			try {
				Object conditionValue = conditionItem.invoke(condition);
				if (conditionValue == null) {
					continue;
				}
				if (isFirst) {
					isFirst = false;
					sb.append(" WHERE ");
				} else {
					sb.append("   AND ");
				}
				
				String columnName = conditionItem.getName().replaceAll("get", "");
				for (int i = 0; i < columnName.length(); i++) {
					char c = columnName.charAt(i);
					if (i == 0) {
						sb.append(String.valueOf(c).toUpperCase());
					} else if (c >= 65 && c <= 90) {
						sb.append("_");
						sb.append(String.valueOf(c));
					} else {
						sb.append(String.valueOf(c).toUpperCase());
					}
				}
				sb.append(" = ");
				sb.append(conditionValue);
				sb.append("\n");
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException("Extraction error occurred!");
			}
		}
		
		return sb.toString();
	}
	
	public String createInsertQuery(Object data) {
		
		return "";
	}
}

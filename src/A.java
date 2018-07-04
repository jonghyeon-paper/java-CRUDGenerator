
public class A {

	public static void main(String[] args) {
		SampleTable parameter = new SampleTable();
		parameter.setAge(15);
		parameter.setGender("M");
		parameter.setExtraData("99999");
		
		CRUDGenerator test = new CRUDGenerator(SampleTable.class);
		System.out.println(test.generateSelectQuery(parameter));
		System.out.println("\n");
		System.out.println(test.generateInsertQuery(parameter));
		System.out.println("\n");
		System.out.println(test.generateUpdateQuery(parameter));
		System.out.println("\n");
		System.out.println(test.generateDeleteQuery(parameter));
	}

}

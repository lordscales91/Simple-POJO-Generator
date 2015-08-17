package model;

import java.util.ArrayList;
import java.util.List;

public class PojoObject {
	
	private String packageName;
	private String pojoName;
	private List<String> imports=new ArrayList<>();
	private List<String> fields=new ArrayList<>();
	private List<String> types=new ArrayList<>();
	
	
	/**
	 * Constructs a POJO Object in memory with the specified name and package
	 * @param packageName the package
	 * @param pojoName the name of the Class
	 */
	public PojoObject(String packageName, String pojoName) {
		this.packageName = packageName;
		this.pojoName = pojoName;
	}
	/**
	 * Constructs a POJO Object in memory with the specified name in the default 
	 * package (NOT RECOMMENDED)
	 * @param pojoName the name of the Class
	 */
	public PojoObject(String pojoName) {
		this.packageName = null;
		this.pojoName = pojoName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageStr) {
		this.packageName = packageStr;
	}

	public String getPojoName() {
		return pojoName;
	}

	public void setPojoName(String pojoName) {
		this.pojoName = pojoName;
	}
	
	/**
	 * Adds a field to the fields List and its corresponding type to the types List
	 * @param type the type of the field
	 * @param name the name of the field
	 */
	public void addField(String type, String name) {
		this.types.add(type);
		this.fields.add(name);
	}
	/**
	 * Adds an import to the imports List
	 * @param importStr
	 */
	public void addImport(String importStr) {
		this.imports.add(importStr);
	}
	
	public List<String> getImports() {
		return imports;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	/**
	 * Finds the position of the specified field and calls {@link #getTypeOfField(int) 
	 * getTypeOfField(int)} or throws an IllegalArgumentException if can't be found
	 * @param fieldName the name of the field
	 * @return The type
	 */
	public String getTypeOfField(String fieldName) {
		int i=fields.indexOf(fieldName);
		if(i!=-1) {
			return getTypeOfField(i);
		} else {
			throw new IllegalArgumentException("The field "+fieldName+" can't be found");
		}
	}
	
	/**
	 * Retrieve the type of the field in that position
	 * @param pos
	 * @return The type
	 */

	public String getTypeOfField(int pos) {		
		return types.get(pos);
	}
	

}

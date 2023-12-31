/*
 * Copyright 2020 The MDSL Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mdsl.generator.model;

import io.mdsl.exception.MDSLException;

/**
 * Represents a field/attribute of an MDSL data type.
 *
 */
public class DataTypeField {

	private static final String ANONYMOUS_KEY = "akey";
	private String name;
	private MDSLType type;
	private boolean list = false;
	private boolean nullable = false;
	
	private String defaultValue;

	/**
	 * Creates a new data type field.
	 * 
	 * @param name the name of the data type field
	 */
	public DataTypeField(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the represented data type field.
	 * 
	 * @return the name of the represented data type field
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the represented data type field.
	 * 
	 * @return the type of the represented data type field
	 */
	public MDSLType getType() {
		return type;
	}

	/**
	 * Returns the type of the represented data type field as String.
	 * 
	 * @return the type of the represented data type field as String.
	 */
	public String getTypeAsString() {
		return type.getName();
	}

	/**
	 * Indicates whether the field is a list (zero, one, or multiple values) or not.
	 * 
	 * @return true, if the field is a list, false otherwise
	 */
	public boolean isList() {
		return list;
	}

	/**
	 * Indicates whether the field is nullable (can be null; does not necessarily
	 * have a value) or not.
	 * 
	 * @return true, if the field can be null, false otherwise
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Sets the type of the represented data type field.
	 * 
	 * @param type the type of the represented data type field
	 */
	public void setType(MDSLType type) {
		this.type = type;
	}

	/**
	 * Defines whether the field is a list or not.
	 * 
	 * @param list true, if the field shall be a list, false otherwise
	 */
	public void isList(boolean list) {
		this.list = list;
	}

	/**
	 * Defines whether the field shall be nullable or not.
	 * 
	 * @param nullable true, if the field shall be nullable, false otherwise
	 */
	public void isNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Sample data
	 * 
	 * @param levelOfDetail amount of data to be included
	 */
	public String sampleJSON(int levelOfDetail) {	
		
		String sampleValue;
		
		if(defaultValue!=null&&!defaultValue.equals("")) {
			sampleValue = "\"" + defaultValue + "\"";
		}
		else {
			sampleValue = type.sampleJSON(levelOfDetail);
		}
		
		switch (levelOfDetail) {
		case 0 : 
			// how about nullable lists? 
			if(list) {
				return ", \"" + name + "\": []";
			}
			if(nullable) {
				return ", \"" + name + "\": {}";
			}
		case 1:
			if(list) {
				return ", \"" + name + "\": ["+ sampleValue +"]";
			}
			else {
				return ", \"" + name + "\":" + sampleValue;
			}
		case 2:
			if(list) {
				return ", \"" + name + "\": ["+ sampleValue  
					+ ", "+ sampleValue + " ]";
			}
			else {
				return ", \"" + name + "\":" + sampleValue;
			}
		}
		throw new MDSLException("Unsupported level of detail, try 0 or 1 or 2.");
	}
}

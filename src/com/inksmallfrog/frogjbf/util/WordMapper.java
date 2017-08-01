package com.inksmallfrog.frogjbf.util;

public class WordMapper {
	public static String classNameToCamelName(String className){
		if(null == className){
			return "";
		}else if(className.contains(".")){
			className = className.substring(className.lastIndexOf(".") + 1);
		}
		int firstCharacter = className.charAt(0);
		if(firstCharacter >= 'a' && firstCharacter <= 'z'){
			return className;
		}else{
			StringBuilder camelNameBuilder = new StringBuilder();
			camelNameBuilder.append((char)(className.charAt(0) + 32));
			camelNameBuilder.append(className.substring(1));
			
			return camelNameBuilder.toString();
		}
	}
}

package com.inksmallfrog.frogjbf.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

public class PackageLoader {
	public static void main(String[] args){
		List<String> classes = PackageLoader.getAllClassNamesFromPackage("com.inksmallfrog.frogjbf.test");
		for(String name : classes){
			System.out.println(WordMapper.classNameToCamelName(name));
		}
		System.out.println("---END---");
	}
	
	public static List<String> getAllClassNamesFromPackage(String packages){
		if(null == packages){
			return null;
		}
		List<String> classNameList = new LinkedList<String>();
		String path = ClassLoader.getSystemResource("").getPath() 
				+ packages.replace('.', '/');
		try {
			path = URLDecoder.decode(path,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File packageFile = new File(path);
		if(packageFile.exists() && packageFile.isDirectory()){
			classNameList.addAll(getAllClassNamesFromDirectory(packageFile));
		}
		return classNameList;
	}
	
	private static List<String> getAllClassNamesFromDirectory(File dir){
		List<String> classNameList = new LinkedList<String>();
		File[] files = dir.listFiles();
		for(File file : files){
			if(file.isDirectory()){
				classNameList.addAll(getAllClassNamesFromDirectory(file));
			}else if(file.getPath().endsWith("class")){
				String path = file.getAbsolutePath();
				String packagePath = path.substring(path.indexOf("\\classes") + 9, path.lastIndexOf("."));
				classNameList.add(packagePath.replace('\\', '.'));
			}
		}
		return classNameList;
	}
}

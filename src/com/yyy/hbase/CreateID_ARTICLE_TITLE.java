package com.yyy.hbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CreateID_ARTICLE_TITLE {

	static String path = "D:\\mahout-work-ubuntu\\reuters-out-matrix\\";
	static String[] inputArgs = { "-i", path + "docIndex", "-o", path + "docIndex.txt" };

	public static String PATH = path + File.separator + "docIndex.txt";
	public static String[] family = { "article" };
	public static String[] qualifer = { "alias", "title" };
	public static String tableName = "ARTICLE_ALIAS_TITLE";

	static String prefixTitleFile = "D:\\mahout-work-ubuntu\\reuters-out";

	public static void main(String[] args) throws Exception {
		// Seqdumper.run(inputArgs);
		HBaseDAO.createTable(tableName, family);
		//
		CreateID_ARTICLE_TITLE w = new CreateID_ARTICLE_TITLE();
		w.readTxtAndImport(PATH);

	}

	private void readTxtAndImport(String filePath) {
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				for (int i = 0; i < 2; i++) {
					bufferedReader.readLine();
				}
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					/**
					 * ex Key, 0, Value, /reuters-out/reut2-000.sgm-0.txt
					 */
					String[] strs = lineTxt.split(":");
					if (strs.length == 4) {
						String title = readTitle(prefixTitleFile + strs[3].trim());
						if (title != null) {
							System.out.println(strs[3].trim());
							HBaseDAO.put(tableName, strs[1].trim(), family[0], qualifer[0], strs[3].trim());
							HBaseDAO.put(tableName, strs[1].trim(), family[0], qualifer[1], title);
						}
					}
				}
				read.close();
			} else {
				System.out.println("file not exist");
			}
		} catch (Exception e) {
			System.out.println("content error");
			e.printStackTrace();
		}
	}

	private static String readTitle(String filePath) {
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				for (int i = 0; i < 2; i++) {
					bufferedReader.readLine();
				}
				String title = bufferedReader.readLine();
				System.out.println(title);
				read.close();
				return title;
			} else {
				System.out.println("file not exist");
			}
		} catch (

		Exception e) {
			System.out.println("content error");
			e.printStackTrace();
		}
		return null;
	}

}

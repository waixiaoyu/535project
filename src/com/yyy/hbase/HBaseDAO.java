package com.yyy.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;

public class HBaseDAO {
	private static HBaseAdmin hBaseAdmin = null;
	static {
		try {
			hBaseAdmin = (HBaseAdmin) HBaseUtils.getHConnection().getAdmin();
			if (hBaseAdmin == null) {
				throw new Exception("get connection fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		Result r = HBaseDAO.get("ARTICLE_TOPIC20", "1");
		NavigableMap<byte[], byte[]> maps = r.getFamilyMap("topic".getBytes());
		for (Entry<byte[], byte[]> e : maps.entrySet()) {
			System.out.println(new String(e.getKey()) + "-" + new String(e.getValue()));
		}
	}

	public static void createTable(String tableName, String[] strColumn) {
		System.out.println("start create table ......");
		try {
			if (hBaseAdmin.tableExists(tableName)) {// �������Ҫ�����ı���ô��ɾ�����ٴ���
				// hBaseAdmin.disableTable(tableName);
				// hBaseAdmin.deleteTable(tableName);
				System.out.println(tableName + " is exist....");
				return;
			}
			HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
			for (String string : strColumn) {
				tableDescriptor.addFamily(new HColumnDescriptor(string));
			}
			hBaseAdmin.createTable(tableDescriptor);
			hBaseAdmin.close();
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end create table ......");
	}

	public static void deleteTable(String tableName) throws IOException {
		System.out.println("start deleting " + tableName);
		if (hBaseAdmin.tableExists(tableName)) {// �������Ҫ�����ı���ô��ɾ�����ٴ���
			hBaseAdmin.disableTable(tableName);
			hBaseAdmin.deleteTable(tableName);
			System.out.println(tableName + " is detele....");
		} else {
			System.out.println(tableName + " does not exist....");
		}
	}

	/**
	 *
	 * get
	 */
	public static Result get(String tableName, String rowKey) throws IOException {
		Get get = new Get(rowKey.getBytes());
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);// ��ȡ��
		Result result = table.get(get);
		/**
		 * you can use the following sentence to get each value.
		 * System.out.println(new String(result.getValue("content".getBytes(),
		 * "count".getBytes()))); return new
		 * String(result.getValue("content".getBytes(), "count".getBytes()));
		 */
		return result;
	}

	/**
	 * scan operation of different type of filters
	 */
	public static List<Result> scanRowKeyByFilter(String tableName, Filter filter) throws IOException {
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);
		Scan scan = new Scan();
		scan.setFilter(filter);
		ResultScanner resultScanner = table.getScanner(scan);
		List<Result> lResults = new ArrayList<>();
		for (Result rs : resultScanner) {
			lResults.add(rs);
		}
		return lResults;
	}

	public static List<Result> scanRowKeyByPrefix(String tableName, String prefix) throws IOException {
		return scanRowKeyByFilter(tableName, new PrefixFilter(prefix.getBytes()));
	}

	public static List<Result> scanRowKeyByRegexString(String tableName, String regex) throws IOException {
		return scanRowKeyByFilter(tableName, new RowFilter(CompareOp.EQUAL, new RegexStringComparator(regex)));
	}

	public static List<Result> scanRowKeyBySubString(String tableName, String regex) throws IOException {
		// just like String.contain()
		return scanRowKeyByFilter(tableName, new RowFilter(CompareOp.EQUAL, new SubstringComparator(regex)));
	}

	public static List<Result> scanColumnByFilter(String tableName, String family, String qualifier, Filter filter)
			throws IOException {
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);
		Scan scan = new Scan();
		scan.addColumn(family.getBytes(), qualifier.getBytes());
		scan.setFilter(filter);
		ResultScanner resultScanner = table.getScanner(scan);
		List<Result> lResults = new ArrayList<>();
		for (Result rs : resultScanner) {
			lResults.add(rs);
		}
		return lResults;
	}

	public static List<Result> scanColumnBySubString(String tableName, String family, String qualifier,
			String substring) throws IOException {
		// just like String.contain()
		return scanColumnByFilter(tableName, family, qualifier,
				new ValueFilter(CompareOp.EQUAL, new SubstringComparator(substring)));
	}

	public static List<Result> scanColumnByRegexString(String tableName, String family, String qualifier, String regex)
			throws IOException {
		// just like String.contain()
		return scanColumnByFilter(tableName, family, qualifier,
				new ValueFilter(CompareOp.EQUAL, new RegexStringComparator(regex)));
	}

	/**
	 * put
	 * 
	 */
	public static void put(String tableName, String rowKey, String family, String qualifier, String value)
			throws IOException {
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);// ��ȡ��
		Put put = new Put(rowKey.getBytes());
		put.addColumn(family.getBytes(), qualifier.getBytes(), value.getBytes());
		table.put(put);
	}

	public static void put(String tableName, String rowKey, String family, String value) throws IOException {
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);// ��ȡ��
		Put put = new Put(rowKey.getBytes());
		put.addColumn(family.getBytes(), null, value.getBytes());
		table.put(put);
	}

	public static void putAll(String tableName, List<Put> puts) throws IOException {
		HTable table = new HTable(HBaseUtils.getConfiguration(), tableName);// ��ȡ��
		table.put(puts);
	}
}
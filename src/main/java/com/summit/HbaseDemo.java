package com.summit;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseDemo {
	// ������̬����
	static Configuration conf = null;
	static {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", "localhost");
	}

	/*
	 * ������
	 * 
	 * @tableName ����
	 * 
	 * @family �����б�
	 */
	public static void creatTable(String tableName, String[] family) throws Exception {

		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor desc = new HTableDescriptor(tableName);
		for (int i = 0; i < family.length; i++) {
			desc.addFamily(new HColumnDescriptor(family[i]));
		}
		if (admin.tableExists(tableName)) {
			System.out.println("table Exists!");
			System.exit(0);
		} else {
			admin.createTable(desc);
			System.out.println("create table Success!");
		}

		admin.close();
	}

	/*
	 * Ϊ�������ݣ��ʺ�֪���ж�������Ĺ̶��?
	 * 
	 * @rowKey rowKey
	 * 
	 * @tableName ����
	 * 
	 * @column1 ��һ�������б�
	 * 
	 * @value1 ��һ���е�ֵ���б�
	 * 
	 * @column2 �ڶ��������б�
	 * 
	 * @value2 �ڶ����е�ֵ���б�
	 */
	public static void addData(String rowKey, String tableName, String[] column1, String[] value1, String[] column2, String[] value2) throws IOException {
		Put put = new Put(Bytes.toBytes(rowKey));// ����rowkey
		HTable table = new HTable(conf, Bytes.toBytes(tableName));// HTabel������¼��صĲ�������ɾ�Ĳ��//
																	// ��ȡ��
		HColumnDescriptor[] columnFamilies = table.getTableDescriptor() // ��ȡ���е�����
				.getColumnFamilies();

		for (int i = 0; i < columnFamilies.length; i++) {
			String familyName = columnFamilies[i].getNameAsString(); // ��ȡ������
			if (familyName.equals("article")) { // article����put���
				for (int j = 0; j < column1.length; j++) {
					put.add(Bytes.toBytes(familyName), Bytes.toBytes(column1[j]), Bytes.toBytes(value1[j]));
				}
			}
			if (familyName.equals("author")) { // author����put���
				for (int j = 0; j < column2.length; j++) {
					put.add(Bytes.toBytes(familyName), Bytes.toBytes(column2[j]), Bytes.toBytes(value2[j]));
				}
			}
		}
		table.put(put);
		System.out.println("add data Success!");
	}

	/*
	 * ���rwokey��ѯ
	 * 
	 * @rowKey rowKey
	 * 
	 * @tableName ����
	 */
	public static Result getResult(String tableName, String rowKey) throws IOException {
		Get get = new Get(Bytes.toBytes(rowKey));
		HTable table = new HTable(conf, Bytes.toBytes(tableName));// ��ȡ��
		Result result = table.get(get);
		for (KeyValue kv : result.list()) {
			System.out.println("family:" + Bytes.toString(kv.getFamily()));
			System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
			System.out.println("value:" + Bytes.toString(kv.getValue()));
			System.out.println("Timestamp:" + kv.getTimestamp());
			System.out.println("-------------------------------------------");
		}
		return result;
	}

	/*
	 * �����ѯhbase��
	 * 
	 * @tableName ����
	 */
	public static void getResultScann(String tableName) throws IOException {
		Scan scan = new Scan();
		ResultScanner rs = null;
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		try {
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					System.out.println("row:" + Bytes.toString(kv.getRow()));
					System.out.println("family:" + Bytes.toString(kv.getFamily()));
					System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
					System.out.println("value:" + Bytes.toString(kv.getValue()));
					System.out.println("timestamp:" + kv.getTimestamp());
					System.out.println("-------------------------------------------");
				}
			}
		} finally {
			rs.close();
		}
	}

	/*
	 * �����ѯhbase��
	 * 
	 * @tableName ����
	 */
	public static void getResultScann(String tableName, String start_rowkey, String stop_rowkey) throws IOException {
		Scan scan = new Scan();
		scan.setStartRow(Bytes.toBytes(start_rowkey));
		scan.setStopRow(Bytes.toBytes(stop_rowkey));
		ResultScanner rs = null;
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		try {
			rs = table.getScanner(scan);
			for (Result r : rs) {
				for (KeyValue kv : r.list()) {
					System.out.println("row:" + Bytes.toString(kv.getRow()));
					System.out.println("family:" + Bytes.toString(kv.getFamily()));
					System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
					System.out.println("value:" + Bytes.toString(kv.getValue()));
					System.out.println("timestamp:" + kv.getTimestamp());
					System.out.println("-------------------------------------------");
				}
			}
		} finally {
			rs.close();
		}
	}

	/*
	 * ��ѯ���е�ĳһ��
	 * 
	 * @tableName ����
	 * 
	 * @rowKey rowKey
	 */
	public static void getResultByColumn(String tableName, String rowKey, String familyName, String columnName) throws IOException {
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName)); // ��ȡָ������������η��Ӧ����
		Result result = table.get(get);
		for (KeyValue kv : result.list()) {
			System.out.println("family:" + Bytes.toString(kv.getFamily()));
			System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
			System.out.println("value:" + Bytes.toString(kv.getValue()));
			System.out.println("Timestamp:" + kv.getTimestamp());
			System.out.println("-------------------------------------------");
		}
	}

	/*
	 * ���±��е�ĳһ��
	 * 
	 * @tableName ����
	 * 
	 * @rowKey rowKey
	 * 
	 * @familyName ������
	 * 
	 * @columnName ����
	 * 
	 * @value ���º��ֵ
	 */
	public static void updateTable(String tableName, String rowKey, String familyName, String columnName, String value) throws IOException {
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
		put.add(Bytes.toBytes(familyName), Bytes.toBytes(columnName), Bytes.toBytes(value));
		table.put(put);
		System.out.println("update table Success!");
	}

	/*
	 * ��ѯĳ����ݵĶ���汾
	 * 
	 * @tableName ����
	 * 
	 * @rowKey rowKey
	 * 
	 * @familyName ������
	 * 
	 * @columnName ����
	 */
	public static void getResultByVersion(String tableName, String rowKey, String familyName, String columnName) throws IOException {
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(columnName));
		get.setMaxVersions(5);
		Result result = table.get(get);
		for (KeyValue kv : result.list()) {
			System.out.println("family:" + Bytes.toString(kv.getFamily()));
			System.out.println("qualifier:" + Bytes.toString(kv.getQualifier()));
			System.out.println("value:" + Bytes.toString(kv.getValue()));
			System.out.println("Timestamp:" + kv.getTimestamp());
			System.out.println("-------------------------------------------");
		}
		/*
		 * List<?> results = table.get(get).list(); Iterator<?> it = results.iterator(); while (it.hasNext()) { System.out.println(it.next().toString()); }
		 */
	}

	/*
	 * ɾ��ָ������
	 * 
	 * @tableName ����
	 * 
	 * @rowKey rowKey
	 * 
	 * @familyName ������
	 * 
	 * @columnName ����
	 */
	public static void deleteColumn(String tableName, String rowKey, String falilyName, String columnName) throws IOException {
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		Delete deleteColumn = new Delete(Bytes.toBytes(rowKey));
		deleteColumn.deleteColumns(Bytes.toBytes(falilyName), Bytes.toBytes(columnName));
		table.delete(deleteColumn);
		System.out.println(falilyName + ":" + columnName + "is deleted!");
	}

	/*
	 * ɾ��ָ������
	 * 
	 * @tableName ����
	 * 
	 * @rowKey rowKey
	 */
	public static void deleteAllColumn(String tableName, String rowKey) throws IOException {
		HTable table = new HTable(conf, Bytes.toBytes(tableName));
		Delete deleteAll = new Delete(Bytes.toBytes(rowKey));
		table.delete(deleteAll);
		System.out.println("all columns are deleted!");
	}

	/*
	 * ɾ���
	 * 
	 * @tableName ����
	 */
	public static void deleteTable(String tableName) throws IOException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		admin.disableTable(tableName);
		admin.deleteTable(tableName);
		System.out.println(tableName + "is deleted!");
	}

	public static void main(String[] args) throws Exception {

		// ������
		String tableName = "blog2";
		String[] family = { "article", "author" };
		// creatTable(tableName, family);

		// Ϊ��������

		String[] column1 = { "title", "content", "tag" };
		String[] value1 = { "Head First HBase", "HBase is the Hadoop database. Use it when you need random, realtime read/write access to your Big Data.", "Hadoop,HBase,NoSQL" };
		String[] column2 = { "name", "nickname" };
		String[] value2 = { "nicholas", "lee" };
		addData("rowkey1", "blog2", column1, value1, column2, value2);
		addData("rowkey2", "blog2", column1, value1, column2, value2);
		addData("rowkey3", "blog2", column1, value1, column2, value2);

		// �����ѯ
		getResultScann("blog2", "rowkey4", "rowkey5");
		// ���row key��Χ�����ѯ
		getResultScann("blog2", "rowkey4", "rowkey5");

		// ��ѯ
		getResult("blog2", "rowkey1");

		// ��ѯĳһ�е�ֵ
		getResultByColumn("blog2", "rowkey1", "author", "name");

		// ������
		updateTable("blog2", "rowkey1", "author", "name", "bin");

		// ��ѯĳһ�е�ֵ
		getResultByColumn("blog2", "rowkey1", "author", "name");

		// ��ѯĳ�еĶ�汾
		getResultByVersion("blog2", "rowkey1", "author", "name");

		// ɾ��һ��
		deleteColumn("blog2", "rowkey1", "author", "nickname");

		// ɾ��������
		deleteAllColumn("blog2", "rowkey1");

		// ɾ���
		deleteTable("blog2");

	}
}
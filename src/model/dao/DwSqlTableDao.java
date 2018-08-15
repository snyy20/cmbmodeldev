package model.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.taskdefs.SQLExec;

import java.util.StringTokenizer;

import model.bean.DwSqlTableEntity;
import model.bean.TgtTableEtlRulesEntity;
import model.bean.SrcTableListEntity;

public class DwSqlTableDao extends SQLExec {
	

	
    /**
     * Database connection
     */
    private Connection conn = null;

    /**
     * SQL statement
     */
    private Statement statement = null;

    /**
     * SQL input file
     */
    private File srcFile = null;

    /**
     * SQL input command
     */
    private String sqlCommand = "";

    /**
     * SQL Statement delimiter
     */
    private String delimiter = ";";


    /**
     * Action to perform if an error is found
     * value1: continue 
     * value2: abort
     **/
    private String onError = "continue";

    /**
     * Keep the format of a sql block?
     */
    private boolean keepformat = false;

    /**
     * Argument to Statement.setEscapeProcessing
     *
     * @since Ant 1.6
     */
    private boolean escapeProcessing = true;

    /**
     * should properties be expanded in text?
     * false for backwards compatibility
     *
     * @since Ant 1.7
     */
    private boolean expandProperties = false;
	
	public static void insertIntoTgtTableEtlRulesTbl(String phyName,List<TgtTableEtlRulesEntity> tRulesList,Connection conn) throws SQLException {
		PreparedStatement statement= null;
		conn.setAutoCommit(false);
		statement = conn.prepareStatement("delete from MODEL_TGT_TABLE_ETL_RULES WHERE PHY_NAME=\'"+phyName+"\'");

        statement.execute();
        statement.close();
        //System.out.println("TGT_TABLE_ETL_RULES size="+ tRulesList.size());
		statement = conn.prepareStatement("insert into MODEL_TGT_TABLE_ETL_RULES (TGT_ENTITY,TGT_DB_SCHEMA,PHY_NAME,COLUMN_ID,COLUMN_NAME_EN,COLUMN_NAME_CH,DATA_TYPE,IS_PK,IS_PARTITION_KEY,LOAD_BATCH,GROUP_ID,SRC_TABLE_NAME,SRC_TABLE_NAME_CN,SRC_COLUMN_NAME,SRC_COLUMN_NAME_CN,SRC_COLUMN_TYPE,COMPUTE_EXPRESSION,EXPRESSION_COMMENTS) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		for(TgtTableEtlRulesEntity tRules:tRulesList) {
			statement.setString(1, tRules.getTgtEntity());
			statement.setString(2, tRules.getTgtDbSchema());
			statement.setString(3, tRules.getPhyName());
			statement.setString(4, tRules.getColumnId());
			statement.setString(5, tRules.getColumnNameEn());
			statement.setString(6, tRules.getColumnNameCh());
			statement.setString(7, tRules.getDataType());
			statement.setString(8, tRules.getIsPk());
			statement.setString(9, tRules.getIsPartitionKey());
			statement.setString(10, tRules.getLoadBatch());
			statement.setString(11, tRules.getGroupId());
			statement.setString(12, tRules.getSrcTableName());
			statement.setString(13, tRules.getSrcTableNameCn());
			statement.setString(14, tRules.getSrcColumnName());
			statement.setString(15, tRules.getSrcColumnNameCn());
			statement.setString(16, tRules.getSrcColumnType());
			statement.setString(17, tRules.getComputeExpression());
			statement.setString(18, tRules.getExpressionComments());
			statement.addBatch();
		}
        statement.executeBatch();
        conn.commit();   
        statement.close();		
	}
	
	
	/**
	 * 
	 * @param reader
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	 protected DwSqlTableEntity readSqlStatements(File f,Reader reader)
			throws Exception, IOException {

		 DwSqlTableEntity dwSqlTable= new DwSqlTableEntity();
		 
		 //字段行集合  字段内容+“｜”+行数
//		ArrayList<DwSqlTableEntity>SqlFiledList = new ArrayList<DwSqlTableEntity>(0);
		StringBuffer sql = new StringBuffer();
		String line = "";
		int filedNum=0;

		BufferedReader in = new BufferedReader(reader);

		while ((line = in.readLine()) != null) {
			filedNum++;//行数从1开始。
			
			if (!keepformat) {
				line = line.trim();
			}
			line = getProject().replaceProperties(line);
			if (!keepformat) {
				if (line.startsWith("//")) {
					continue;
				}
				if (line.startsWith("--")) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(line);
				if (st.hasMoreTokens()) {
					String token = st.nextToken();
					if ("REM".equalsIgnoreCase(token)) {
						continue;
					}
				}
			}

			// SQL defines "--" as a comment to EOL
			// and in Oracle it may contain a hint
			// so we cannot just remove it, instead we must end it
			if (!keepformat) {
				if (line.indexOf("--") >= 0) {
					sql.append("\n");
				}

			}
			
			//字段行集合  字段内容+“｜”+行数
			if (sql.length() > 0) {
//				SqlFiledList.add(sql.append("|").append(filedNum).toString());
				dwSqlTable.setLoad_num(filedNum);
				dwSqlTable.setTable_name(f.getName());
				dwSqlTable.setLoad_date(new Date());
				dwSqlTable.setFiled_value(sql.toString());
			}
			

		}
		// Catch any statements not followed by ;		
		in.close();
		reader.close();
		
		return dwSqlTable;
	}
	 
	 
	/**
	 *读取SQL文件到数据库中
	 * @param fileList
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public void readSqlToDbFromSrc(List<File> fileList) throws FileNotFoundException, IOException, Exception{
		//表级的数据集 key-value  sql文件名-字段行集合
		Map<String, List> TableSqlmap= new HashMap();
		//字段行集合  字段内容+“｜”+行数
		ArrayList<DwSqlTableEntity>SqlFiledList = new ArrayList<DwSqlTableEntity>(0);
		
		//循环所有文件列表，将每个文件字段行数信息存入MAP中
		for(File f: fileList) {
			SqlFiledList.add(readSqlStatements(f,new FileReader(f.toString())));
		}
		
	}
	
	
	
	

}

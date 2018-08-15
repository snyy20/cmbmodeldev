package model.entry;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import model.constant.Constant;
import model.dao.TgtTableListDao;
import model.dao.metaDbConn;
import model.bean.TgtTableEtlRulesEntity;
import model.bean.SrcTableListEntity;
import model.handle.ReadingEtlTaskExcel;
import model.utils.MyPathUtil;
import model.utils.PropertiesUtil;

public class LoadSqlToDb {
	
	
	public static void main(String[] args) {
		Connection conn = null;
			//获取前台传来的参数,数据源SQL文件路径;
			String workPath = args[0];
			if(!workPath.endsWith(File.separator))
				workPath=workPath+File.separator;
			try {
				//获取数据配置文件
				String locpath=MyPathUtil.getProjectPath();
				String dbpropfile = locpath+File.separator+Constant.dbPropertiesfile;
				conn = metaDbConn.connectMetaDB(dbpropfile);
				//开启事务
				conn.setAutoCommit(false);
				//将参数路径里的SQL写入数据库表中.
				int ret = ImportSqlToDb(workPath,conn);
			conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	public static int ImportSqlToDb(String filePath,Connection conn)
	{
		//判断filePath目录下SQL目录是否存在
		File srcFilePath = new File(filePath+"SQL");
        if(!srcFilePath.exists()){
        	System.out.println("文件夹"+srcFilePath +"不存在！");
        	return -1;
        }
        //将SQL目录下的所有文件读入LIST集合中
		List<File> fileList = getFileList(filePath+"SQL");
	    Map<String, List> map= new HashMap();
		List<SrcTableListEntity> srcTLsList = new ArrayList();
		List<TgtTableEtlRulesEntity> tRulesList = new ArrayList();
		
		try {
			
			
			
			
			map = ReadingEtlTaskExcel.readXls(fileList);
			srcTLsList = map.get("srcTLsList");
			tRulesList = map.get("tRulesList");
			TgtTableListDao.insertIntoSrcTableList(srcTLsList, conn);
			TgtTableListDao.insertIntoTgtTableEtlRules(tRulesList, conn);
			conn.commit();
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;

	}
	
	/**
	 * 获取SQL脚本文件到List集合
	 * @param strPath
	 * @return
	 */
	public static List<File> getFileList(String strPath) {
		List<File> fileList = new ArrayList<>(); 
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileName.endsWith("SQL")) { // 判断文件名是否以.SQL结尾
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    fileList.add(files[i]);
                } else {
                    continue;
                }
            }

        }
        return fileList;
    }
}
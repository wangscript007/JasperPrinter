package org.ltd3000.jasperprinter.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.ltd3000.jasperprinter.utils.ConfigUtil;

public  class DBUtil {
	public static boolean initDB() {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
			//DROP TABLE IF EXISTS testtable
			helper.executeUpdate("DROP TABLE IF EXISTS authorize");
			helper.executeUpdate("DROP TABLE IF EXISTS cogfig");
			helper.executeUpdate("DROP TABLE IF EXISTS jaspercount");
			helper.executeUpdate("create table authorize(id INTEGER PRIMARY KEY AUTOINCREMENT  ,item varchar(20),value varchar(30))");
			helper.executeUpdate("create table cogfig(id INTEGER PRIMARY KEY AUTOINCREMENT,item varchar(20),value varchar(30))");
			helper.executeUpdate("create table jaspercount(id INTEGER PRIMARY KEY AUTOINCREMENT,item varchar(20),value DOUBLE)");
			int count=ConfigUtil.getProperty("count").equals("")?0:Integer.parseInt(ConfigUtil.getProperty("count"));
			helper.executeUpdate("INSERT OR REPLACE INTO jaspercount(item,value) values('ok',"+count+")");
			//setSlaveDate();
			helper.destroyed();	
			ConfigUtil.removeProperty("count");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}
	public static boolean setProperties(String name,String value) {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
			helper.executeUpdate("INSERT OR REPLACE INTO cogfig(item,value) values('"+name+"','"+value+"')");		
			helper.destroyed();	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;		
	}
	public static boolean addJasperCount() {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
			helper.executeUpdate("update jaspercount set value=value+1 where item='ok'");		
			helper.destroyed();	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	public static int  getJasperCount() {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
            List<Integer> dlist = helper.executeQuery("select value from jaspercount where item='ok'", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int index)
                        throws SQLException {
                    return rs.getInt("value");
                }
            });
            helper.destroyed();	
            if(dlist==null||dlist.isEmpty()) {
            	return 0;
            }else {
            	return dlist.get(0);
            }
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}
	private static  boolean setSlaveDate() {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
            helper.executeUpdate("INSERT OR REPLACE INTO authorize(item,value) values('slavedate','"+ConfigUtil.getProperty("slavedate")+"')");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;		
	}
	public static boolean checkPermission() {
		 try {
			SqliteHelper helper = new SqliteHelper("jasper.db");
            List<String> dlist = helper.executeQuery("select value from authorize where item='slavedate'", new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int index)
                        throws SQLException {
                    return rs.getString("value");
                }
            });
            if(!dlist.isEmpty()) {
            	String sDate=dlist.get(0);
            	Calendar cNow=Calendar.getInstance();
            	Calendar cSlave=Calendar.getInstance();
            	cSlave.set(Calendar.YEAR,Integer.parseInt(sDate.substring(0, 4)) );
            	cSlave.set(Calendar.MONTH, Integer.parseInt(sDate.substring(5, 7))-1);
            	cSlave.set(Calendar.DATE, Integer.parseInt(sDate.substring(8, 10)));

            	if(cNow.before(cSlave)) {
            		return true;
            	}
            }
			helper.destroyed();	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;		
	}
	public static void main(String[] args) {
		
	}

}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DB {
	Connection c;

	public DB() {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bitirme", "postgres", "12345");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean gucForKoordinant(int x, int y, int columnNum, int guc, int oda, int yon, int time) {
		Statement stmt = null;
		int ret = 0;
		try {

			stmt = c.createStatement();
			String sql = "UPDATE yon" + yon + "oda" + oda + " set " + "rout" + columnNum + " = " + guc + " ,time= "
					+ time + " where x=" + x + " and y=" + y + ";";
			ret = stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			// //c.close();
			System.out.println("Records created successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}
		return ret != 0;
	}

	public void delete(int maxOdaNum) {
		Statement stmt;
		try {
			stmt = c.createStatement();
			for (int i = 1; i < 1 + maxOdaNum; i++) {
				for (int j = 1; j < 5; j++) {
					try {
						stmt.execute("delete from yon" + j + "oda" + i);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			stmt.execute("delete from router_mac");

			c.commit();

			stmt.close();
			// c.close();
		} catch (SQLException e1) {
		}

	}

	public int column_num_from_mac(String mac, int yon, int oda) {
		Statement stmt = null;
		int column = 0;
		try {

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT column_num FROM router_mac WHERE mac ='" + mac + "' and yon=" + yon
					+ "and oda=" + oda + ";");
			while (rs.next()) {
				column = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return column;
	}

	public int insert_db(String yon_oda, double guc, String router_num, double x, double y, int time) {
		Statement stmt = null;
		try {

			stmt = c.createStatement();
			String sql = "INSERT INTO " + yon_oda + " (" + router_num + ",x,y,time) " + "VALUES (" + guc + ", " + x
					+ ", " + y + ", " + time + " );";
			stmt.executeUpdate(sql);

			stmt.close();
			c.commit();
			// //c.close();
			System.out.println("Records created successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			try {
				stmt.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return -1;
		}
		return 0;
	}

	public HashMap<String, Integer> macColumn(int oda, int yon) {
		HashMap<String, Integer> map = new HashMap<>();
		try {

			Statement stmt = c.createStatement();
			String sql = "select column_num, mac from router_mac where yon=" + yon + " and oda=" + oda + ";";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				map.put(rs.getString("mac"), rs.getInt("column_num"));
			}
			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return map;

	}

	public HashMap<Integer, Integer> macColumn(int oda, int yon, int x, int y) {
		HashMap<Integer, Integer> map = new HashMap<>();
		try {

			Statement stmt = c.createStatement();
			String sql = "select  * from yon" + yon + "oda" + oda + " where x=" + x + " and y=" + y + ";";
			ResultSet rs = stmt.executeQuery(sql);

			rs.next();
			map.put(1, rs.getInt("rout1"));
			map.put(2, rs.getInt("rout2"));
			map.put(3, rs.getInt("rout3"));
			map.put(4, rs.getInt("rout4"));
			map.put(5, rs.getInt("rout5"));
			map.put(6, rs.getInt("rout6"));

			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return map;

	}

	public ResultSet yon_oda_bilgisi(String yon_oda) {
		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + yon_oda + ";");
			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return rs;
	}

	public void update_coordinant(HashMap<Integer, Integer> map, int x, int y, String update_edilecek_yon_oda) {
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			String sql = "UPDATE " + update_edilecek_yon_oda + " set rout1=" + map.get(1) + ", rout2=" + map.get(2)
					+ ", rout3=" + map.get(3) + ", rout4=" + map.get(4) + ", rout5=" + map.get(5) 
					+ ", rout6=" + map.get(6) +", time="+(int) (System.currentTimeMillis() / 60000)+ " where x=" + x + " and y=" + y+";";
			stmt.executeUpdate(sql);
			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public Statement kordinat_bilgisi(int oda, int yon) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM yon" + yon + "oda" + oda + ";");
			// stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return stmt;
	}

	public void creatTable(int oda, int yon) {
		String sql = "CREATE TABLE public.yon" + yon + "oda" + oda
				+ " (x double precision,y double precision,rout1 double precision, " + " rout2 double precision, "
				+ "rout3 double precision, "
				+ "rout4 double precision, rout5 double precision, rout6 double precision, time integer) "
				+ " WITH (OIDS=FALSE); " + "ALTER TABLE public.yon" + yon + "oda" + oda + " OWNER TO postgres;";
		execute(sql);
	}

	public void update_db(double guc, String router_num, double x, double y, String update_edilecek_yon_oda) {
		Statement stmt = null;
		try {
			stmt = c.createStatement();
			String sql = "UPDATE " + update_edilecek_yon_oda + " set " + router_num + " = data where x=" + x + "y=" + y;
			stmt.executeUpdate(sql);
			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}
	
	public void execute(String sql) {
		try {

			Statement stmt = c.createStatement();
			stmt.execute(sql);
			c.commit();

			stmt.close();
			//// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public int kullanilmayanSutun(int yon, int oda) {
		int ret = 0;
		try {

			Statement stmt = c.createStatement();
			String sql = "select column_num from router_mac where yon=" + yon + " and oda=" + oda + ";";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				ret = rs.getInt(1) + 1;
			}
			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return ret;
	}

	public void insert_router_mac(String mac, int column_num, int yon, int oda) {
		Statement stmt = null;
		try {

			stmt = c.createStatement();
			String sql = "insert into router_mac (mac, column_num,yon,oda) values ('" + mac + "'," + column_num + ","
					+ yon + " , " + oda + ");";
			stmt.executeUpdate(sql);
			c.commit();

			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public ArrayList<arrayYerine> mac_bilgi(String mac) {
		Statement stmt = null;
		ArrayList<arrayYerine> ret = new ArrayList<>();
		int column, yon, oda;
		try {

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT column_num,yon,oda FROM router_mac WHERE mac ='" + mac + "';");
			while (rs.next()) {
				column = (rs.getInt("column_num"));
				yon = (rs.getInt("yon"));
				oda = (rs.getInt("oda"));
				ret.add(new arrayYerine(yon, oda, mac, column));
			}
			rs.close();
			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

		return ret;
	}

	public int maxRoom() {
		int i = 0;
		try {

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("select max(oda) from router_mac;");
			rs.next();
			i = rs.getInt(1);
			rs.close();
			stmt.close();
			// //c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return i;

	}
	/*
	 * public void delete_db(String data, String yon_oda, double x, double y) {
	 * Statement stmt = null; try {
	 * 
	 * stmt = c.createStatement(); String sql =
	 * "DELETE from yon_oda where ...;"; stmt.executeUpdate(sql); c.commit();
	 * 
	 * stmt.close(); // //c.close(); } catch (Exception e) {
	 * System.err.println(e.getClass().getName() + ": " + e.getMessage());
	 * System.exit(0); } }
	 */

}
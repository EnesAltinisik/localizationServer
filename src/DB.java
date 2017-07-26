import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
			// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		

		return column;
	}

	public int insert_db(String yon_oda, double guc, String router_num, double x, double y) {
		Statement stmt = null;
		try {

			stmt = c.createStatement();
			String sql = "INSERT INTO " + yon_oda + " (" + router_num + ",x,y) " + "VALUES (" + guc + ", " + x + ", "
					+ y + " );";
			stmt.executeUpdate(sql);

			stmt.close();
			// c.commit();
			// c.close();
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
		System.out.println("Records created successfully");
		return 0;
	}

	public ResultSet yon_oda_bilgisi(String yon_oda) {
		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + yon_oda + ";");
			stmt.close();
			// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		return rs;
	}

	public Statement kordinat_bilgisi(int oda,int yon) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM yon" + yon +"oda"+oda+";");
			//stmt.close();
			// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		return stmt;
	}

	public void creatTable(int oda, int yon) {
		String sql = "CREATE TABLE public.yon" + yon + "oda" + oda
				+ " (x double precision,y double precision,rout1 double precision, " + " rout2 double precision, "
				+ "rout3 double precision, " + "rout4 double precision) " + " WITH (OIDS=FALSE); "
				+ "ALTER TABLE public.yon" + yon + "oda" + oda + " OWNER TO postgres;";
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
			// c.close();
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
			// c.close();
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
			// c.close();
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
			// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
	}

	public ArrayList<arrayYerine>  mac_bilgi(String mac){
		Statement stmt = null;
		ArrayList<arrayYerine> ret = new ArrayList<>();
		int column,yon,oda;
		try {

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT column_num,yon,oda FROM router_mac WHERE mac ='" + mac + "';");
			while (rs.next()) {
				column=(rs.getInt("column_num"));
				yon=(rs.getInt("yon"));
				oda=(rs.getInt("oda"));
				ret.add(new arrayYerine(yon, oda, mac, column));
			}
			rs.close();
			stmt.close();
			// c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		

		return ret;
	}
	public int maxRoom() {
		int i=0;
		try {

			Statement stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("select max(oda) from router_mac;");
			rs.next();
			i= rs.getInt(1);
			rs.close();
			stmt.close();
			// c.close();
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
	 * stmt.close(); // c.close(); } catch (Exception e) {
	 * System.err.println(e.getClass().getName() + ": " + e.getMessage());
	 * System.exit(0); }  }
	 */
}
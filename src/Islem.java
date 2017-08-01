import java.awt.EventQueue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Islem {
	static DB db = server.db;
	static ModifiedCells mdfcell;
	static String temp = "";
	static int x2ret = -1, y2ret = -1, oda2ret = -1, dogruTime = 0, xEski = 0, yEski = 0, yon2ret = -1, routCount2ret = 0;
	static String maxX2ret = "1",maxY2ret = "1";
	static ArrayList<Integer> column = new ArrayList<>();
	static ArrayList<Double> guc = new ArrayList<>();
	static ArrayList<Double> katsayi = new ArrayList<>();
	static ArrayList<Double> eksikGuc = new ArrayList<>();
	static ArrayList<Double> eksiKatsayi = new ArrayList<>();

	public static String isle(String veri) {
		if(mdfcell==null){
			mdfcell=new ModifiedCells();
		}
		if (veri.contains("sending data:"))
			return veri2finger(veri);
		return veri2konum(veri);
	}

	private static String veri2konum(String veri) {
		String[] mac = new String[6];
		String[] arr = veri.split("\t");
		double[] gucArr = new double[6];
		double[] katsayiArr = new double[6];
		ArrayList<ArrayList<arrayYerine>> temel = new ArrayList<>();
		double toplam = 0;
		for (int i = 0; i < arr.length && i < 6; i++) {
			String s = arr[i];
			String[] arr1 = s.split(" ");
			mac[i] = arr1[0];
			gucArr[i] = new Integer(arr1[2]);
			toplam += (1.0 / gucArr[i]);
			temel.add(db.mac_bilgi(mac[i]));
		}
		for (int i = 0; i < katsayiArr.length; i++) {
			katsayiArr[i] = (1.0 / gucArr[i]) / toplam;
		}
		ArrayList<arrayYerine> arrayYerineList = new ArrayList<arrayYerine>();
		int max = db.maxRoom();
		boolean kont = false;
		boolean kont2column = false;
		int count = 0;
		double minEcl = 100000000;
		for (int i = 1; i < max + 1; i++) {
			for (int j = 1; j < 5; j++) {
				for (Iterator iterator = temel.iterator(); iterator.hasNext();) {
					arrayYerineList = (ArrayList<arrayYerine>) iterator.next();
					for (Iterator iterator2 = arrayYerineList.iterator(); iterator2.hasNext();) {
						arrayYerine arrayYerine = (arrayYerine) iterator2.next();
						if (arrayYerine.getOda() == i) {
							if (arrayYerine.getYon() == j) {
								column.add(arrayYerine.getColumn());
								guc.add(gucArr[count]);
								katsayi.add(katsayiArr[count]);
								kont = true;
								kont2column = true;
								break;
							}
						}
					}
					if (!kont2column) {
						eksikGuc.add(gucArr[count]);
						eksiKatsayi.add(katsayiArr[count]);

					}
					kont2column = false;
					count++;
				}
				count = 0;
				if (kont) {
					minEcl = hesaplaEclud(i, j, minEcl);
					kont = false;
				}
			}
			column.removeAll(column);
			guc.removeAll(guc);
			katsayi.removeAll(katsayi);
			eksiKatsayi.removeAll(eksiKatsayi);
			eksikGuc.removeAll(eksikGuc);

		}
		if (!temp.equals("oda:" + oda2ret + " x: " + x2ret + " y:" + y2ret)) {
			temp = "oda:" + oda2ret + " x: " + x2ret + " y:" + y2ret;
		}
		updateVeri(veri, minEcl, mac, gucArr);
		System.out.println("oda:" + oda2ret + " x:" + x2ret + " y:" + y2ret + " routCount:" + routCount2ret );
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				mdfcell.display(x2ret,y2ret,routCount2ret,oda2ret);
			}
		});
		return "oda:" + oda2ret + " x:" + x2ret + " y:" + y2ret + " routCount:" + routCount2ret ;
	}

	private static void updateVeri(String veri, double minEcl, String[] mac, double[] gucArr) {
		if (xEski != x2ret || yEski != y2ret || yEski == -1) {
			xEski = x2ret;
			yEski = y2ret;
			return;
		}
		xEski = -1;
		yEski = -1;
		int value;
		int time = (int) (System.currentTimeMillis() / 60000);
		if ((time - dogruTime) < 10 || minEcl > 50)
			return;
		if (minEcl < 4)
			minEcl = 4;
		double carpan = 0.8;
		if ((time - dogruTime) > 10)
			carpan += (Math.pow((Math.log(minEcl) / Math.log(50)), (Math.log(time - dogruTime)))) / 5;
		HashMap<String, Integer> map4column = db.macColumn(oda2ret, yon2ret);
		HashMap<Integer, Integer> map4power = db.macColumn(oda2ret, yon2ret, x2ret, y2ret);
		for (int i = 0; i < mac.length; i++) {
			if (map4column.containsKey(mac[i])) {
				if (map4power.get(map4column.get(mac[i])) == 0)
					map4power.put(map4column.get(mac[i]), (int) gucArr[i]);
				value = (int) (map4power.get(map4column.get(mac[i])) * carpan + (1 - carpan) * gucArr[i]);
				map4power.put(map4column.get(mac[i]), value);
			}
		}
		db.update_coordinant(map4power, x2ret, y2ret, "yon" + yon2ret + "oda" + oda2ret);
	}

	private static double hesaplaEclud(int oda, int yon, double minEcl) {
		int count = 0;
		double distance = 0, gucTemp, katSayi;
		Statement stmt = db.kordinat_bilgisi(oda, yon);
		try {
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				for (Iterator iterator = column.iterator(); iterator.hasNext();) {
					Integer column = (Integer) iterator.next();
					gucTemp = rs.getInt("rout" + column);
					if (gucTemp == 0)
						gucTemp = -99;
					distance += Math.pow(gucTemp - guc.get(count), 2) * katsayi.get(count);
					count++;
				}
				count = 0;
				for (Iterator iterator = eksiKatsayi.iterator(); iterator.hasNext();) {
					katSayi = (Double) iterator.next();
					distance += Math.pow((-99) - eksikGuc.get(count), 2) * katSayi;
					count++;
				}
				for (int i = 1; i < 7; i++) {
					if (!column.contains(i)) {
						gucTemp = rs.getInt("rout" + i);
						if (gucTemp != 0) {
							distance = distance * 0.9 + Math.pow(gucTemp + 99, 2) * 0.1;
						}
					}
				}
				if (distance < minEcl) {
					minEcl = distance;
					dogruTime = rs.getInt("time");
					x2ret = rs.getInt("x");
					y2ret = rs.getInt("y");
					oda2ret = oda;
					yon2ret = yon;
					routCount2ret = column.size();
				}
				distance = 0;
				count = 0;
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return minEcl;

	}

	private static String veri2finger(String veri) {
		int index = veri.indexOf(":");
		veri = veri.substring(index + 1);
		index = veri.indexOf(" ");
		int x = new Integer(veri.substring(0, index));

		veri = veri.substring(index + 1);
		index = veri.indexOf(" ");
		int y = new Integer(veri.substring(0, index));

		veri = veri.substring(index + 1);
		index = veri.indexOf(" ");
		int room = new Integer(veri.substring(0, index));

		veri = veri.substring(index + 1);
		index = veri.indexOf("\t");
		int yon = new Integer(veri.substring(0, index));

		veri = veri.substring(index + 1);
		String[] arr = veri.split("\t");
		for (int i = 0; i < arr.length; i++) {
			veriEkle(arr[i], x, y, room, yon);
		}

		return "succesfuly adding data";
	}

	public static void veriEkle(String veri, int x, int y, int oda, int yon) {
		String[] arr = veri.split(" ");
		String mac = arr[0];
		boolean macMevcutDegil = true;
		int guc = new Integer(arr[2]);
		int column = db.column_num_from_mac(mac, yon, oda);
		if (column == 0) {
			column = db.kullanilmayanSutun(yon, oda);
		} else {
			macMevcutDegil = false;
		}
		if (column == 0) {
			column = 1;
		}
		if (column != 7) {
			if (macMevcutDegil)
				db.insert_router_mac(mac, column, yon, oda);
			int time = (int) (System.currentTimeMillis() / 60000);
			boolean dontNeedInsert = db.gucForKoordinant(x, y, column, guc, oda, yon, time);
			if (!dontNeedInsert) {
				int error = db.insert_db("yon" + yon + "oda" + oda, guc, "rout" + column, x, y, time);
				if (error == -1) {
					server.db = new DB();
					db = server.db;
					db.creatTable(oda, yon);

					db.insert_db("yon" + yon + "oda" + oda, guc, "rout" + column, x, y, time);
				}
			}
		}

	}
}

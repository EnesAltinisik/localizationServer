import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class Islem {
	static DB db = server.db;
	static int x2ret=-1,y2ret=-1,oda2ret=-1;
	static ArrayList<Integer> column=new ArrayList<>();
	static ArrayList<Double> guc=new ArrayList<>();
	static ArrayList<Double> katsayi=new ArrayList<>();

	public static String isle(String veri) {
		if (veri.contains("sending data:"))
			return veri2finger(veri);
		return veri2konum(veri);
	}

	private static String veri2konum(String veri) {
		String[] mac = new String[4];
		String[] arr = veri.split("\t");
		 double[] gucArr = new double[4];
		 double[] katsayiArr = new double[4];
		 column.removeAll(column);
		 guc.removeAll(guc);
		 katsayi.removeAll(katsayi);

		ArrayList<ArrayList<arrayYerine> > temel = new ArrayList<>();
		double toplam=0;
		for (int i = 0; i < arr.length && i < 4; i++) {
			String s = arr[i];
			String[] arr1 = s.split(" ");
			mac[i] = arr1[0];
			gucArr[i] = new Integer(arr1[2]);
			toplam+=(1.0/gucArr[i] );
			temel.add(db.mac_bilgi(mac[i]));
		}
		for (int i = 0; i < katsayiArr.length; i++) {
			katsayiArr[i] = (1.0/gucArr[i])/ toplam;
		}
		ArrayList<arrayYerine> arrayYerineList = new ArrayList<arrayYerine>();
		int max = db.maxRoom();
		boolean kont=false;
		int count=0;
		double minEcl=100000000;
		for (int i = 1; i < max+1; i++) {
			for (int j = 1; j < 5; j++) {
				for (Iterator iterator = temel.iterator(); iterator.hasNext();) {
					arrayYerineList = (ArrayList<arrayYerine>) iterator.next();
					for (Iterator iterator2 = arrayYerineList.iterator(); iterator2.hasNext();) {
						arrayYerine arrayYerine = (arrayYerine) iterator2.next();
						if(arrayYerine.getOda()==i){
							if(arrayYerine.getYon()==j){
								column.add(arrayYerine.getColumn());
								guc.add(gucArr[count]);
								katsayi.add(katsayiArr[count]);
								kont=true;
								break;
							}
						}
					}

					count++;
				}
				count=0;
				if(kont){
					minEcl=hesaplaEclud(i,j,minEcl);
					kont=false;
				}
			}
		}

		return "oda:"+oda2ret+" x: "+x2ret+" y:"+y2ret;
	}

	private static double hesaplaEclud(int oda, int yon,double minEcl) {
		int count=0;double distance=0,gucTemp;
		Statement stmt=db.kordinat_bilgisi(oda, yon);
		double katsayiToplami=0;
		try {
			ResultSet rs=stmt.getResultSet();
			while (rs.next()) {
				katsayiToplami=0;
				System.out.println(rs.getInt("x")+" "+rs.getInt("y"));
				for (Iterator iterator = column.iterator(); iterator.hasNext();) {
					katsayiToplami+=katsayi.get(count);
					Integer column = (Integer) iterator.next();
					gucTemp=rs.getInt("rout"+column);
					if(gucTemp==0)
						gucTemp=-99;
					System.out.println(gucTemp+" "+guc.get(count)+" "+katsayi.get(count));
					distance+=Math.pow(gucTemp-guc.get(count),2)*katsayi.get(count);
					count++;
				}
				distance/=katsayiToplami;
				if(distance<minEcl){
					minEcl=distance;
					x2ret=rs.getInt("x");
					y2ret=rs.getInt("y");
					System.out.println("x:"+x2ret+" y:"+y2ret+"minEcl:"+minEcl);
					oda2ret=oda;
				}
				distance=0;
				count=0;
			}
			System.out.println(minEcl);
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
		int guc = new Integer(arr[2]);
		int column = db.column_num_from_mac(mac, yon, oda);
		if (column == 0) {
			column = db.kullanilmayanSutun(yon, oda);
		}
		if (column == 0) {
			column = 1;
		}
		if (column != 5) {
			db.insert_router_mac(mac, column, yon, oda);
			int error = db.insert_db("yon" + yon + "oda" + oda, guc, "rout" + column, x, y);
			if (error == -1) {
				server.db = new DB();
				db = server.db;
				db.creatTable(oda, yon);

				db.insert_db("yon" + yon + "oda" + oda, guc, "rout" + column, x, y);
			}
		}

	}
}

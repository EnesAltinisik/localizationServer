
public class arrayYerine {
	int yon,oda,column;
	public int getYon() {
		return yon;
	}
	public void setYon(int yon) {
		this.yon = yon;
	}
	public int getOda() {
		return oda;
	}
	public void setOda(int oda) {
		this.oda = oda;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	String mac;
	public arrayYerine(int yon,int oda,String mac,int column) {
		this.yon=yon;
		this.oda=oda;
		this.column=column;
		this.mac=mac;
	}
}

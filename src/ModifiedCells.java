import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * @see https://stackoverflow.com/a/12352838/230513
 */
public class ModifiedCells extends JPanel {
	static int x = 0;
	static int y = 0;
	static int routCount = 0;
	static JFrame f = new JFrame("Wifi Localization");

	public ModifiedCells() {
		final MyModel model = new MyModel();
		JTable table = new JTable(model);
		table.setDefaultRenderer(String.class, new MyRenderer());
		table.setDefaultEditor(String.class, new MyEditor(table));
		this.add(table);
	}

	public static void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 15; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			if (width > 300)
				width = 300;
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private static class MyRenderer extends DefaultTableCellRenderer {

		Color backgroundColor = getBackground();

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			MyModel model = (MyModel) table.getModel();
			if (model.getState(row, column) == 0) {
				c.setBackground(Color.BLACK);
			} else if (model.getState(row, column) == 1) {
				if (routCount == 1)
					c.setBackground(Color.RED);
				else if (routCount == 2)
					c.setBackground(new Color(150, 150, 0));
				else if (routCount == 3)
					c.setBackground(Color.YELLOW);
				else
					c.setBackground(Color.GREEN);
			} else {
				c.setBackground(backgroundColor);
			}
			return c;
		}
	}

	private static class MyEditor extends DefaultCellEditor {

		private MyModel model;
		JTable table;

		public MyEditor(JTable table) {
			super(new JTextField());
			this.table = table;
			this.model = (MyModel) table.getModel();
			resizeColumnWidth(table);
		}

		@Override
		public boolean stopCellEditing() {
			model.setState(table.getEditingRow(), true);
			return super.stopCellEditing();
		}
	}

	private static class MyModel extends AbstractTableModel {

		private final List<Row> list = new ArrayList<Row>();

		public MyModel() {
			for (int i = 0; i < 22; i++) {
				if (i == 5)
					list.add(new Row("", true));
				else
					list.add(new Row("", false));
			}
		}

		public int getState(int row, int column) {
			if ((row > 5 && column < 18) || (row < 3 && column > 11) || (row > 5 && column > 20)
					|| (row > 13 && column > 19)) {
				return 0;
			}
			if (row == y && column == x) {
				return 1;
			}
			return 2;
		}

		public void setState(int row, boolean state) {
			list.get(row).state = state;
		}

		@Override
		public int getRowCount() {
			return list.size();
		}

		@Override
		public int getColumnCount() {
			return 22;
		}

		@Override
		public Object getValueAt(int row, int col) {
			return list.get(row).name;
		}

		@Override
		public void setValueAt(Object aValue, int row, int col) {
			list.get(row).name = (String) aValue;
			fireTableCellUpdated(row, col);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		private static class Row {

			private String name;
			private Boolean state;

			public Row(String name, Boolean state) {
				this.name = name;
				this.state = state;
			}
		}
	}

	void display(int x1, int y1, int rout, int OdaNum) {
		x = x1;
		y = y1;
		if (OdaNum == 2) {
			x = x1 + 5;
		}
		if (OdaNum == 3) {
			y = y1 + 2;
			x = x1 + 11;
		}
		if (OdaNum == 4) {
			y = y1 + 5;
			x = x1 + 17;
		}
		if (OdaNum == 5) {
			y = y1 + 13;
			x = x1 + 17;
		}
		routCount = rout;
		f.invalidate();
		f.validate();
		f.repaint();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(this);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

}

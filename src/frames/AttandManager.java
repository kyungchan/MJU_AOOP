package frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableCellRenderer;

import classes.ClassTableModel;
import queries.AttandsQueries;
import queries.CoursesQueries;


public class AttandManager extends JDialog implements ActionListener {
	private AttandTablePanel atp;
	private AttandsQueries attandsQueries;
	private CoursesQueries coursesQueries;
	private ClassTableModel attandsModel;
	private ClassTableModel coursesModel;

	public AttandManager(ClassTableModel tableModel, ClassTableModel coursesModel, AttandsQueries attandsQueries,
			CoursesQueries coursesQueries, String studentNum) {
		this.attandsModel = tableModel;
		this.coursesModel = coursesModel;
		this.attandsQueries = attandsQueries;
		this.coursesQueries = coursesQueries;
		atp = new AttandTablePanel(tableModel);
		JToolBar toolBar = new JToolBar();
		JButton toolBarBtn;
		toolBar.setFloatable(false);
		toolBarBtn = new JButton(new ImageIcon("images/icon_check.png"));
		toolBarBtn.setActionCommand("check");
		toolBarBtn.addActionListener(this);
		toolBar.add(toolBarBtn);

		toolBarBtn = new JButton(new ImageIcon("images/icon_late.png"));
		toolBarBtn.addActionListener(this);
		toolBarBtn.setActionCommand("late");
		toolBar.add(toolBarBtn);

		toolBarBtn = new JButton(new ImageIcon("images/icon_miss.png"));
		toolBarBtn.addActionListener(this);
		toolBarBtn.setActionCommand("miss");
		toolBar.add(toolBarBtn);

		add(toolBar, BorderLayout.NORTH);
		add(atp, BorderLayout.CENTER);
		if (studentNum != null) {
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				if (atp.getAttandModel().getValueAt(i, 0).equals(studentNum)) {
					atp.getScoreTable().requestFocus();
					atp.getScoreTable().changeSelection(i, 0, false, false);
					break;
				}
			}
		}
		this.setTitle("免搬包府");
		this.pack();
		this.setModal(true);
		this.setVisible(true);
	}

	public void calTotal(int row) {
		String stuNum = (String) atp.scoreTable.getValueAt(row, 0);
		int late = (int) atp.scoreTable.getValueAt(row, 4);
		int miss = (int) atp.scoreTable.getValueAt(row, 5);
		int total;
		miss += late / 3;
		late %= 3;

		total = 100 - miss * 10 - late * 3;
		attandsModel.setValueAt(total, row, 3);

		if (coursesQueries.updateScore(stuNum, 0, total) != 1) {
			JOptionPane.showMessageDialog(null, "DB坷幅惯积", "己利包府", JOptionPane.ERROR_MESSAGE);
		}
		System.out.println(attandsModel.getValueAt(row, 3));
		coursesModel.setValueAt(total, row, 3);
	}

	public void warningRow() {
		int late;
		int miss;
		int total = 0;

		for (int i = 0; i < attandsModel.getRowCount(); i++) {
			late = (int) atp.scoreTable.getValueAt(i, 4);
			miss = (int) atp.scoreTable.getValueAt(i, 5);

			miss += late / 3;
			late %= 3;
			total = 100 - miss * 10 - late * 3;
			if (total < 70) {
				DefaultTableCellRenderer warining = new DefaultTableCellRenderer();
				atp.scoreTable.getComponentAt(i, 0).setBackground(Color.RED);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedCols[] = atp.getScoreTable().getSelectedColumns();
		int selectedRows[] = atp.getScoreTable().getSelectedRows();

		if (selectedCols.length != 0) {
			switch (e.getActionCommand()) {
			case "check":
				for (int row : selectedRows)
					for (int col : selectedCols)
						if (col > 5)
							if (attandsQueries.updateAttand((String) atp.getScoreTable().getValueAt(row, 0), col - 5,
									2) == 0)
								JOptionPane.showMessageDialog(null, "DB坷幅惯积", "己利包府", JOptionPane.ERROR_MESSAGE);
							else {
								atp.getScoreTable().setValueAt(2, row, col);
								calTotal(row);
							}
				break;
			case "late":
				for (int row : selectedRows)
					for (int col : selectedCols)
						if (col > 5)
							if (attandsQueries.updateAttand((String) atp.getScoreTable().getValueAt(row, 0), col - 5,
									1) == 0)
								JOptionPane.showMessageDialog(null, "DB坷幅惯积", "己利包府", JOptionPane.ERROR_MESSAGE);
							else {
								atp.getScoreTable().setValueAt(1, row, col);
								calTotal(row);
							}
				break;
			case "miss":
				for (int row : selectedRows)
					for (int col : selectedCols)
						if (col > 5)
							if (attandsQueries.updateAttand((String) atp.getScoreTable().getValueAt(row, 0), col - 5,
									0) == 0)
								JOptionPane.showMessageDialog(null, "DB坷幅惯积", "己利包府", JOptionPane.ERROR_MESSAGE);
							else {
								atp.getScoreTable().setValueAt(0, row, col);
								calTotal(row);
							}
				break;
			}
		} else

		{
			JOptionPane.showMessageDialog(null, "函版且 亲格阑 急琶窍瘤 臼疽嚼聪促.", "己利包府", JOptionPane.WARNING_MESSAGE);
		}
	}
}

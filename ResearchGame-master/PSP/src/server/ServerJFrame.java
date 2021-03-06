package server;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

public class ServerJFrame extends JFrame {
	private PropertyChangeSupport PCS;
	private JPanel contentPane;
	private JLabel roundLabel;
	private JButton startGame;
	private JTable table;
	private JScrollPane scrollPane;
	private String[] columnNames = new String[]{"Player #", "Remove"};
	private Object[][] data = new Object[0][2];

	public ServerJFrame(final PropertyChangeSupport PCS) {
		this.PCS = PCS;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow,right][50px,grow,fill][50px,grow,fill][50px,grow,fill][50px,grow,fill][50px,grow,fill][grow,left]", "[grow,top][][][][fill][fill][fill][fill][grow,fill][grow][][grow,bottom]"));

		roundLabel = new JLabel("Game not started yet");
		contentPane.add(roundLabel, "cell 3 1");
		
		startGame = new JButton("Start Game");
		startGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PCS.firePropertyChange("Start Game", null, true);
			}
		});
		contentPane.add(startGame, "cell 1 7");
		
		addScrollPane();
	}
	
	public void addScrollPane() {
		DefaultTableModel table1Model = new DefaultTableModel(data, columnNames);
		table = new JTable(table1Model);
		table.getColumn("Remove").setCellRenderer(new ButtonRenderer());
		table.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox()));
		Dimension d = new Dimension(5, 5);
		table.setPreferredScrollableViewportSize(d);
		scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, "cell 1 8 5 1,grow");
		
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	public void addRowToPlayers(int playerNumber) {
		contentPane.remove(scrollPane);
		Object[][] newData = new Object[data.length+1][2];
		for (int i=0; i<data.length; i++) {
			newData[i] = data[i];
		}
		newData[newData.length-1] = new Object[]{playerNumber, "Remove"};
		this.data = newData;
		addScrollPane();
	}
	
	public void setRound(String round) {
		roundLabel.setText("Round: " + round);
	}
	
	public void updateGUI() {
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * @version 1.0 11/09/98
	 */
	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;

		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selectedRow = table.getSelectedRow();
					int playerNumber = (Integer) table.getModel().getValueAt(selectedRow, 0);
					PCS.firePropertyChange("Remove Player", null, playerNumber);
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (isPushed) {
						int selectedRow = table.getSelectedRow();
						((DefaultTableModel) table.getModel()).removeRow(selectedRow);
						table.revalidate();
						table.repaint();
						isPushed = false;
					}
				}
			});
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {
			super.fireEditingStopped();
		}
	}
}

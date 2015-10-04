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
	private JTextField mean1Field;
	private JTextField stdDev1Field;
	private JTextField mean2Field;
	private JTextField stdDev2Field;
	private JButton submitGraph;
	private JTextField candidateField;
	private JButton submitCandidate;
	private JTextField budgetField;
	private JButton submitBudget;
	private JLabel roundLabel;
	private JButton startGame;
	private JTextField fileNameField;
	private JButton submitFileName;
	private JTable table;
	private JScrollPane scrollPane;
	private String[] columnNames = new String[]{"Player #", "Remove"};
	private Object[][] data = new Object[0][2];

	/**
	 * Needs:
	 * Round
	 * Start game
	 * Save data to filename+submit
	 * 
	 * Show the players + their actions?
	 * Eventually option to delete player from the game
	 */

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
		
		mean1Field = new JTextField();
		mean1Field.setText("Mean 1");
		mean1Field.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (mean1Field.getText().equals("Mean 1")) {
					mean1Field.setText("");
				} 
			}
		});
		contentPane.add(mean1Field, "cell 1 2,growx");
		mean1Field.setColumns(10);

		stdDev1Field = new JTextField();
		stdDev1Field.setText("Std Dev 1");
		stdDev1Field.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (stdDev1Field.getText().equals("Std Dev 1")) {
					stdDev1Field.setText("");
				} 
			}
		});
		contentPane.add(stdDev1Field, "cell 2 2,growx");
		stdDev1Field.setColumns(10);

		mean2Field = new JTextField();
		mean2Field.setText("Mean 2");
		mean2Field.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (mean2Field.getText().equals("Mean 2")) {
					mean2Field.setText("");
				} 
			}
		});
		contentPane.add(mean2Field, "cell 3 2,growx");
		mean2Field.setColumns(10);

		stdDev2Field = new JTextField();
		stdDev2Field.setText("Std Dev 2");
		stdDev2Field.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (stdDev2Field.getText().equals("Std Dev 2")) {
					stdDev2Field.setText("");
				} 
			}
		});
		contentPane.add(stdDev2Field, "cell 4 2,growx");
		stdDev2Field.setColumns(10);
		
		submitGraph = new JButton("Submit");
		submitGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// FIXME add check that data in numeric
				int[] graphData = new int[4];
				graphData[0] = Integer.parseInt(mean1Field.getText());
				graphData[1] = Integer.parseInt(stdDev1Field.getText());
				graphData[2] = Integer.parseInt(mean2Field.getText());
				graphData[3] = Integer.parseInt(stdDev2Field.getText());
				PCS.firePropertyChange("Set Graph Data", null, graphData);
			}
		});
		contentPane.add(submitGraph, "cell 5 2");
		
		candidateField = new JTextField();
		candidateField.setText("Candidate Ideal Pt");
		candidateField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (candidateField.getText().equals("Candidate Ideal Pt")) {
					candidateField.setText("");
				} 
			}
		});
		contentPane.add(candidateField, "cell 1 4,growx");
		candidateField.setColumns(10);
		
		submitCandidate = new JButton("Submit");
		submitCandidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int idealPt = Integer.parseInt(candidateField.getText());
				PCS.firePropertyChange("Add Candidate", null, idealPt);
			}
		});
		contentPane.add(submitCandidate, "cell 2 4");
		
		budgetField = new JTextField();
		budgetField.setText("Budget");
		budgetField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (budgetField.getText().equals("Budget")) {
					budgetField.setText("");
				} 
			}
		});
		contentPane.add(budgetField, "cell 3 4,growx");
		budgetField.setColumns(10);
		
		submitBudget = new JButton("Submit");
		submitBudget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int budget = Integer.parseInt(budgetField.getText());
				PCS.firePropertyChange("Set Budget", null, budget);
			}
		});
		contentPane.add(submitBudget, "cell 4 4");
		
		fileNameField = new JTextField();
		fileNameField.setText("File Name");
		fileNameField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (fileNameField.getText().equals("File Name")) {
					fileNameField.setText("");
				} 
			}
		});
		contentPane.add(fileNameField, "cell 1 6,growx");
		fileNameField.setColumns(10);
		
		submitFileName = new JButton("Submit");
		submitFileName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String fileName = fileNameField.getText();
				PCS.firePropertyChange("Set File Name", null, fileName);
			}
		});
		contentPane.add(submitFileName, "cell 2 6");
		
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

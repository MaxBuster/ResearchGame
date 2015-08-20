package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ServerJFrame extends JFrame {
	private PropertyChangeSupport PCS;
	private JPanel contentPane;
	private JTextField mean1Field;
	private JTextField stdDev1Field;
	private JTextField mean2Field;
	private JTextField stdDev2Field;
	private JButton submitGraph;
	private JTextField numCandidatesField;
	private JButton submitNumCandidates;
	private JTextField budgetField;
	private JButton submitBudget;
	private JLabel roundLabel;
	private JButton startGame;
	private JTextField fileNameField;
	private JButton submitFileName;

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
		// FIXME add method to update this label
		
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
		
		numCandidatesField = new JTextField();
		numCandidatesField.setText("# Candidates");
		numCandidatesField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
				if (numCandidatesField.getText().equals("# Candidates")) {
					numCandidatesField.setText("");
				} 
			}
		});
		contentPane.add(numCandidatesField, "cell 1 4,growx");
		numCandidatesField.setColumns(10);
		
		submitNumCandidates = new JButton("Submit");
		submitNumCandidates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int numCandidates = Integer.parseInt(numCandidatesField.getText());
				PCS.firePropertyChange("Set Num Candidates", null, numCandidates);
			}
		});
		contentPane.add(submitNumCandidates, "cell 2 4");
		
		budgetField = new JTextField();
		budgetField.setText("Budget");
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
	}
	
	public void updateGUI() {
		contentPane.revalidate();
		contentPane.repaint();
	}
}
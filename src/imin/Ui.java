package imin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Ui extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton btnNewButton;
	JCheckBox chckbxNewCheckBox, chckbxNewCheckBox_1, chckbxNewCheckBox_2;
	JLabel lblNewLabel, lblNewLabel_1, lblNewLabel_2;
	private String content = "";
	private String[] metaContent = null;

	public Ui() {
		super("☆JAVA CLASS☆");

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel_3 = new JLabel("★6조 출석 확인★");
		panel.add(lblNewLabel_3);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(4, 0, 0, 0));

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3);

		chckbxNewCheckBox = new JCheckBox("출석");
		panel_3.add(chckbxNewCheckBox);

		lblNewLabel = new JLabel("백승은");
		panel_3.add(lblNewLabel);

		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4);

		chckbxNewCheckBox_1 = new JCheckBox("출석");
		panel_4.add(chckbxNewCheckBox_1);

		lblNewLabel_1 = new JLabel("김성우");
		panel_4.add(lblNewLabel_1);

		JPanel panel_5 = new JPanel();
		panel_1.add(panel_5);

		chckbxNewCheckBox_2 = new JCheckBox("출석");
		panel_5.add(chckbxNewCheckBox_2);

		lblNewLabel_2 = new JLabel("이달호");
		panel_5.add(lblNewLabel_2);

		JPanel panel_7 = new JPanel();
		panel_1.add(panel_7);

		btnNewButton = new JButton("SEND");
		btnNewButton.addActionListener(this);
		panel_7.add(btnNewButton);

		JPanel panel_6 = new JPanel();
		getContentPane().add(panel_6, BorderLayout.SOUTH);

		setSize(250, 300);
		setVisible(true);
	}
	
	public Ui(String[] metaContent) {
		this();
		this.metaContent = metaContent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String str1 = null;
		if (e.getSource() == btnNewButton) {
			if (chckbxNewCheckBox.isSelected()
					&& chckbxNewCheckBox_1.isSelected()
					&& chckbxNewCheckBox_2.isSelected())
				str1 = lblNewLabel.getText() + "," + lblNewLabel_1.getText()
						+ "," + lblNewLabel_2.getText() + " 출석";

			else if (chckbxNewCheckBox.isSelected()
					&& chckbxNewCheckBox_1.isSelected())
				str1 = lblNewLabel.getText() + "," + lblNewLabel_1.getText()
						+ " 출석";

			else if (chckbxNewCheckBox.isSelected()
					&& chckbxNewCheckBox_2.isSelected())
				str1 = lblNewLabel.getText() + "," + lblNewLabel_2.getText()
						+ " 출석";

			else if (chckbxNewCheckBox_1.isSelected()
					&& chckbxNewCheckBox_2.isSelected())
				str1 = lblNewLabel_1.getText() + "," + lblNewLabel_2.getText()
						+ " 출석";

			else if (chckbxNewCheckBox.isSelected())
				str1 = lblNewLabel.getText() + " 출석";

			else if (chckbxNewCheckBox_1.isSelected())
				str1 = lblNewLabel_1.getText() + " 출석";

			else if (chckbxNewCheckBox_2.isSelected())
				str1 = lblNewLabel_2.getText() + " 출석";

			else
				str1 = " ";
		}
		System.out.println(str1);
		
		HelperClass.smtpsend(metaContent, str1);
		setContent(str1);
	}

	private void setContent(String str) {
		this.content  = str;
	}
	
	public String getContent() {
		return this.content;
	}

//	public static void main(String[] args) {
//		new Ui();
//	}// main
}// class

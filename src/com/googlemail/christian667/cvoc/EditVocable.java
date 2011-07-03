package com.googlemail.christian667.cvoc;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class EditVocable extends javax.swing.JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JTextField pinyinField;
	private JLabel translationL;
	private JTextField translationField;
	private JLabel pinyinL;
	private JSeparator jSeparator3;
	private JButton cancelButton;
	private JLabel topicL;
	private JTextField chineseField;
	private JLabel chineseL;
	private JButton saveButton;
	private SqliteHandler sqlite;
	private Vocable voc;
	private CVoc callback;

	public static void editVocable(Vocable voc, SqliteHandler sqlite,
			CVoc callback) {

		EditVocable inst = new EditVocable(voc, sqlite, callback);
		SwingUtilities.invokeLater(inst);
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public EditVocable(Vocable voc, SqliteHandler sqlite, CVoc callback) {
		super();
		this.voc = voc;
		this.sqlite = sqlite;
		this.callback = callback;
		initGUI();
	}

	private void initGUI() {
		try {
			getContentPane().setLayout(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setResizable(false);
			this.setUndecorated(true);
			this.setTitle("cVoc");
			this.setIconImage(CVoc.logoImage);
			{
				chineseL = new JLabel();
				getContentPane().add(
						chineseL,
						new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				chineseL.setText("chinese:");
				chineseL.setBounds(13, 76, 97, 15);
			}
			{
				chineseField = new JTextField();
				getContentPane().add(
						chineseField,
						new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				chineseField.setBounds(122, 42, 254, 85);
				chineseField.setFont(CVoc.getFont72plain());
			}
			{
				pinyinL = new JLabel();
				getContentPane().add(
						pinyinL,
						new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				pinyinL.setText("pinyin:");
				pinyinL.setBounds(13, 159, 95, 15);
			}
			{
				translationField = new JTextField();
				getContentPane().add(
						translationField,
						new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				translationField.setBounds(122, 216, 254, 30);
				translationField.setFont(CVoc.getFont18plain());
			}
			{
				translationL = new JLabel();
				getContentPane().add(
						translationL,
						new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
								GridBagConstraints.WEST,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				translationL.setText("translation:");
				translationL.setBounds(12, 223, 98, 15);
			}
			{
				pinyinField = new JTextField();
				getContentPane().add(
						pinyinField,
						new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				pinyinField.setBounds(122, 139, 254, 56);
				pinyinField.setFont(CVoc.getFont36plain());
			}
			{
				jSeparator3 = new JSeparator();
				getContentPane().add(
						jSeparator3,
						new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
								GridBagConstraints.NORTH,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				jSeparator3.setBounds(12, 258, 372, 3);
			}
			{
				topicL = new JLabel();
				getContentPane().add(topicL);
				topicL.setText("editing vocable...");
				topicL.setBounds(54, 6, 321, 30);
				topicL.setFont(CVoc.getFont20italic());
			}
			{
				saveButton = new JButton();
				getContentPane().add(saveButton);
				saveButton.setText("save");
				saveButton.setBounds(204, 271, 165, 35);
				saveButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						saveButtonMouseClicked(evt);
					}
				});
			}
			{
				cancelButton = new JButton();
				getContentPane().add(cancelButton);
				cancelButton.setText("cancel");
				cancelButton.setBounds(31, 273, 162, 32);
				cancelButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						cancelButtonMouseClicked(evt);
					}
				});
			}
			this.chineseField.setText(this.voc.getChinese());
			this.pinyinField.setText(this.voc.getPinyin());
			this.translationField.setText(this.voc.gettranslation());
			pack();
			this.setSize(408, 320);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private void saveButtonMouseClicked(MouseEvent evt) {
		this.voc.setChinese(this.chineseField.getText());
		this.voc.setPinyin(this.pinyinField.getText());
		this.voc.settranslation(this.translationField.getText()
				.replace("ö", "oe").replace("ü", "ue").replace("ä", "ae"));
		this.sqlite.editVocable(this.voc);
		this.dispose();
		this.callback.updateAll();
	}

	private void cancelButtonMouseClicked(MouseEvent evt) {
		this.dispose();
		this.callback.updateAll();
	}

}

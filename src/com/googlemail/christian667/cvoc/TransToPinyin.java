package com.googlemail.christian667.cvoc;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.SwingUtilities;

public class TransToPinyin extends javax.swing.JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private JLabel topicLabel;
	private JLabel resultLabel;
	private JButton finishTestButton;
	private JButton okButton;
	private JTextField pinyinField;
	private JLabel toTranslateLabel;
	private SqliteHandler sqlite;
	private Vocable currentVoc;
	private Iterator<Vocable> vocIt;
	private int currentVocNumber = 0;
	private int numberOfVocs = 0;

	public static void startTest(SqliteHandler sqlite, ArrayList<Vocable> vocs) {
		TransToPinyin inst = new TransToPinyin(sqlite, vocs);
		SwingUtilities.invokeLater(inst);
	}

	public TransToPinyin(SqliteHandler sqlite, ArrayList<Vocable> vocs) {
		super();
		this.sqlite = sqlite;
		this.vocIt = vocs.iterator();
		this.numberOfVocs = vocs.size();
		initGUI();
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		if (!this.displayNextVocable())
			dispose();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			this.setTitle("cVoc pinyin test");
			this.setUndecorated(true);
			{
				topicLabel = new JLabel();
				getContentPane().add(topicLabel);
				topicLabel.setText("do you know pinyin of:");
				this.setIconImage(CVoc.logoImage);
				topicLabel.setFont(CVoc.getFont20italic());
				topicLabel.setBounds(12, 12, 417, 41);
			}
			{
				toTranslateLabel = new JLabel();
				getContentPane().add(toTranslateLabel);
				toTranslateLabel.setText("??");
				toTranslateLabel.setBounds(12, 65, 469, 163);
				toTranslateLabel.setFont(CVoc.getFont36plain());
				toTranslateLabel.setHorizontalAlignment(JLabel.CENTER);
				toTranslateLabel.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
			}
			{
				pinyinField = new JTextField();
				getContentPane().add(pinyinField);
				pinyinField.setBounds(12, 240, 469, 31);
				pinyinField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent evt) {
						if (evt.getKeyCode() == 10)
							okButtonMouseClicked(null);
					}
				});
			}
			{
				okButton = new JButton();
				getContentPane().add(okButton);
				okButton.setText("ok and next");
				okButton.setBounds(282, 364, 164, 35);
				okButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						okButtonMouseClicked(evt);
					}
				});
			}
			{
				finishTestButton = new JButton();
				getContentPane().add(finishTestButton);
				finishTestButton.setText("finish test");
				finishTestButton.setBounds(51, 366, 164, 32);
				finishTestButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						finishTestButtonMouseClicked(evt);
					}
				});
			}
			{
				resultLabel = new JLabel();
				getContentPane().add(resultLabel);
				resultLabel.setText("result:");
				resultLabel.setBounds(12, 292, 469, 35);
			}
			pack();
			this.setSize(495, 410);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private void okButtonMouseClicked(MouseEvent evt) {
		this.check();
		if (!this.displayNextVocable())
			this.finishTestButtonMouseClicked(null);
	}

	private void check() {
		// Check result, save edited vocable, display next
		if (this.pinyinField.getText().length() > 1
				&& this.currentVoc.getPinyin().contains(
						this.pinyinField.getText())
				|| (this.pinyinField.getText().length() > 1
						&& CVoc.easyPinyinEnter && CVoc.simplifyPinyin(
						this.currentVoc.getPinyin()).contains(
						this.pinyinField.getText()))) {
			// Correct
			this.resultLabel.setForeground(Color.GREEN);
			this.resultLabel.setText("Correct!!!");
			this.store(true);
		} else {
			// Wrong
			this.resultLabel.setForeground(Color.RED);
			this.resultLabel.setText("WRONG! Correct answer: "
					+ this.currentVoc.getPinyin());
			this.store(false);
		}
	}

	private boolean displayNextVocable() {
		// Display next if available
		if (this.vocIt.hasNext()) {
			this.currentVoc = this.vocIt.next();
			this.toTranslateLabel.setText(this.currentVoc.gettranslation());
			this.pinyinField.setText("");
			this.currentVocNumber++;
			this.topicLabel.setText("[" + this.currentVocNumber + "/"
					+ this.numberOfVocs + "]" + " - do you know pinyin of:");
			return true;
		} else
			return false;
	}

	private void store(boolean correct) {
		this.currentVoc.setLearningattempts(this.currentVoc
				.getLearningattempts() + 1);
		this.currentVoc.setLastlearned(System.currentTimeMillis());
		if (correct) {
			this.currentVoc.setSkill(this.currentVoc.getSkill() + 1);
			if (this.currentVoc.getAttemptsUntilLearned() == 0)
				this.currentVoc.setAttemptsUntilLearned(this.currentVoc
						.getLearningattempts());
			if (this.currentVoc.getKnownsince() == 0
					&& this.currentVoc.getSkill() > this.sqlite
							.getSkillToKnown()) // Known since now
				this.currentVoc.setKnownsince(System.currentTimeMillis());
		} else {
			this.currentVoc.setSkill(this.currentVoc.getSkill() - 1);
		}
		this.sqlite.editVocable(currentVoc);
	}

	private void finishTestButtonMouseClicked(MouseEvent evt) {
		this.dispose();
	}

}

package com.googlemail.christian667.cvoc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.SwingUtilities;

public class ChineseFlashCardTest extends javax.swing.JFrame implements
		Runnable {

	private static final long serialVersionUID = 1L;
	private JLabel topicLabel;
	private JButton noButton;
	private JButton finishTestButton;
	private JButton yesButton;
	private JLabel chineseLabel;
	private SqliteHandler sqlite;
	private Vocable currentVoc;
	private Iterator<Vocable> vocIt;
	private int currentVocNumber = 0;
	private int numberOfVocs = 0;

	public static void startTest(SqliteHandler sqlite, ArrayList<Vocable> vocs) {
		ChineseFlashCardTest inst = new ChineseFlashCardTest(sqlite, vocs);
		SwingUtilities.invokeLater(inst);
	}

	public ChineseFlashCardTest(SqliteHandler sqlite, ArrayList<Vocable> vocs) {
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
			this.setTitle("cVoc flash card test");
			this.setIconImage(CVoc.logoImage);
			this.setUndecorated(true);
			{
				topicLabel = new JLabel();
				getContentPane().add(topicLabel);
				topicLabel.setText("Do you know:");
				topicLabel.setFont(CVoc.getFont20italic());
				topicLabel.setBounds(12, 12, 417, 41);
			}
			{
				chineseLabel = new JLabel();
				getContentPane().add(chineseLabel);
				chineseLabel.setText("??");
				chineseLabel.setBounds(12, 65, 467, 259);
				chineseLabel.setFont(CVoc.getFont85plain());
				chineseLabel.setHorizontalAlignment(JLabel.CENTER);
				chineseLabel.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
			}
			{
				yesButton = new JButton();
				getContentPane().add(yesButton);
				yesButton.setText("show");
				yesButton.setBounds(315, 347, 164, 35);
				yesButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						yesClicked(evt);
					}
				});
			}
			{
				finishTestButton = new JButton();
				getContentPane().add(finishTestButton);
				finishTestButton.setText("finish");
				finishTestButton.setBounds(19, 349, 102, 32);
				finishTestButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						finishTestButtonMouseClicked(evt);
					}
				});
			}
			{
				noButton = new JButton();
				getContentPane().add(noButton);
				noButton.setText("wrong");
				noButton.setVisible(false);
				noButton.setBounds(134, 349, 164, 32);
				noButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						noButtonMouseClicked(evt);
					}
				});
			}
			pack();
			this.setSize(495, 410);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private void yesClicked(MouseEvent evt) {
		if (!this.topicLabel.isVisible())
			this.store(true);
		if (!this.displayResult())
			this.finishTestButtonMouseClicked(null);
	}

	private void noButtonMouseClicked(MouseEvent evt) {
		this.store(false);
		if (!this.displayResult())
			this.finishTestButtonMouseClicked(null);
	}

	private boolean displayResult() {
		if (this.topicLabel.isVisible()) {
			this.topicLabel.setVisible(false);
			this.noButton.setVisible(true);
			this.yesButton.setText("correct");
			chineseLabel.setFont(CVoc.getFont25plain());
			this.chineseLabel.setText(this.currentVoc.getChinese() + ": "
					+ this.currentVoc.getPinyin() + " ["
					+ this.currentVoc.getTranslation() + "]");
			return true;
		} else
			return this.displayNextVocable();
	}

	private boolean displayNextVocable() {
		// Display next if available
		if (this.vocIt.hasNext()) {
			this.currentVoc = this.vocIt.next();
			this.topicLabel.setVisible(true);
			this.noButton.setVisible(false);
			this.yesButton.setText("show");
			chineseLabel.setFont(CVoc.getFont85plain());
			this.chineseLabel.setText(this.currentVoc.getChinese());
			this.currentVocNumber++;
			this.topicLabel.setText("[" + this.currentVocNumber + "/"
					+ this.numberOfVocs + "]" + " - Do you know:");
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

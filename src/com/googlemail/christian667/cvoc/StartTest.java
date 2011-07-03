package com.googlemail.christian667.cvoc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class StartTest extends javax.swing.JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JLabel topic;
	private JButton cancelButton;
	private JSeparator jSeparator3;
	private JCheckBox simplyAllBox;
	private JTextField lastLearnedField;
	private JLabel lastLearnedLabel;
	private JLabel lectureLabel;
	private JLabel id2;
	private JLabel id1;
	private JTextField toField;
	private JTextField fromField;
	private JComboBox lectureBox;
	private JButton startTestButton;
	private JLabel topic3;
	private JSeparator jSeparator1;
	private JRadioButton magicTestButton;
	private JRadioButton mostRareLearnedTestButton;
	private JTextField maxVocsField;
	private JLabel upToLabel;
	private JCheckBox onlyUnknown;
	private JRadioButton flashCardButton;
	private JRadioButton transToPinyinButton;
	private JRadioButton chineseToTransButton;
	private JRadioButton transToChineseButton;
	private ButtonGroup directionGroup;
	private JLabel directionLabel;
	private JSeparator jSeparator4;
	private JRadioButton mostLastLearnedButton;
	private JRadioButton mostUnknownButton;
	private JRadioButton randomTestButton;
	private JRadioButton linearTestButton;
	private ButtonGroup testGroup;
	private JLabel topic2;
	private JSeparator seperator1;
	private TestGenerator testGen;
	private SqliteHandler sqlite;
	private JRadioButton transToPinyinFlashButton;
	private JCheckBox easyPinyin;

	public static void startTest(SqliteHandler sqlite) {
		StartTest inst = new StartTest(sqlite);
		SwingUtilities.invokeLater(inst);
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public StartTest(SqliteHandler sqlite) {
		super();
		this.sqlite = sqlite;
		this.testGen = new TestGenerator(this.sqlite);
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			this.setTitle("cVoc start new test");
			this.setIconImage(CVoc.logoImage);
			this.setUndecorated(true);
			{
				topic = new JLabel();
				getContentPane().add(topic);
				topic.setText("Choose your test settings...");
				topic.setFont(CVoc.getFont20italic());
				topic.setBounds(65, 14, 327, 36);
			}
			{
				seperator1 = new JSeparator();
				getContentPane().add(seperator1);
				seperator1.setBounds(7, 71, 531, 18);
			}
			{
				topic2 = new JLabel();
				getContentPane().add(topic2);
				topic2.setText("way of testing:");
				topic2.setBounds(19, 81, 116, 21);
			}
			{
				linearTestButton = new JRadioButton();
				getContentPane().add(linearTestButton);
				linearTestButton.setText("linear test");
				linearTestButton.setBounds(147, 84, 191, 22);
			}
			{
				randomTestButton = new JRadioButton();
				getContentPane().add(randomTestButton);
				randomTestButton.setText("random test");
				randomTestButton.setBounds(147, 111, 169, 21);
			}
			{
				mostUnknownButton = new JRadioButton();
				getContentPane().add(mostUnknownButton);
				mostUnknownButton.setText("most unknown test");
				mostUnknownButton.setBounds(147, 139, 185, 19);
			}
			{
				getContentPane().add(this.getEasyPinyin());
				mostLastLearnedButton = new JRadioButton();
				getContentPane().add(mostLastLearnedButton);
				getContentPane().add(getMostRareLearnedTestButton());
				getContentPane().add(getMagicTestButton());
				getContentPane().add(getJSeparator1());
				getContentPane().add(getTopic3());
				getContentPane().add(getFromField());
				getContentPane().add(getToField());
				getContentPane().add(getId1());
				getContentPane().add(getId2());
				getContentPane().add(getLectureLabel());
				getContentPane().add(getLastLearnedLabel());
				getContentPane().add(getLastLearnedField());
				getContentPane().add(getSimplyAllBox());
				getContentPane().add(getJSeparator3());
				getContentPane().add(getCancelButton());
				getContentPane().add(getStartTestButton());
				getContentPane().add(getJComboBox1());
				getContentPane().add(getJSeparator4());
				getContentPane().add(getDirectionLabel());
				getContentPane().add(getTransToChineseButton());
				getContentPane().add(getChineseToTransButton());
				getContentPane().add(getTransToPinyinButton());
				getContentPane().add(getFlashCardButton());
				getContentPane().add(getAllUnknownBox());
				getContentPane().add(getUpToLabel());
				getContentPane().add(getMaxVocsField());
				getContentPane().add(getTransToPinyinFlashButton());
				getContentPane().add(getEasyPinyin());
				mostLastLearnedButton.setText("most last learned test");
				mostLastLearnedButton.setBounds(343, 80, 188, 24);
				getTestGroup().add(linearTestButton);
				getTestGroup().add(magicTestButton);
				getTestGroup().add(mostLastLearnedButton);
				getTestGroup().add(mostUnknownButton);
				getTestGroup().add(mostRareLearnedTestButton);
				getTestGroup().add(randomTestButton);
				getDirectionGroup().add(chineseToTransButton);
				getDirectionGroup().add(flashCardButton);
				getDirectionGroup().add(transToChineseButton);
				getDirectionGroup().add(transToPinyinButton);
				getDirectionGroup().add(transToPinyinFlashButton);
			}
			pack();
			this.setResizable(false);
			this.setSize(552, 488);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private ButtonGroup getTestGroup() {
		if (testGroup == null) {
			testGroup = new ButtonGroup();
		}
		return testGroup;
	}

	private JRadioButton getMostRareLearnedTestButton() {
		if (mostRareLearnedTestButton == null) {
			mostRareLearnedTestButton = new JRadioButton();
			mostRareLearnedTestButton.setText("most rare learned test");
			mostRareLearnedTestButton.setBounds(343, 112, 189, 19);
		}
		return mostRareLearnedTestButton;
	}

	private JRadioButton getMagicTestButton() {
		if (magicTestButton == null) {
			magicTestButton = new JRadioButton();
			magicTestButton.setText("magic test");
			magicTestButton.setBounds(343, 139, 178, 19);
			magicTestButton.setSelected(true);
		}
		return magicTestButton;
	}

	private JSeparator getJSeparator1() {
		if (jSeparator1 == null) {
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(4, 188, 532, 18);
		}
		return jSeparator1;
	}

	private JLabel getTopic3() {
		if (topic3 == null) {
			topic3 = new JLabel();
			topic3.setText("test interval:");
			topic3.setBounds(19, 197, 116, 15);
		}
		return topic3;
	}

	private JTextField getFromField() {
		if (fromField == null) {
			fromField = new JTextField();
			fromField.setText("0");
			fromField.setBounds(255, 230, 53, 22);
		}
		return fromField;
	}

	private JTextField getToField() {
		if (toField == null) {
			toField = new JTextField();
			toField.setText("0");
			toField.setBounds(401, 230, 49, 22);
		}
		return toField;
	}

	private JLabel getId1() {
		if (id1 == null) {
			id1 = new JLabel();
			id1.setText("id from:");
			id1.setBounds(147, 229, 89, 22);
		}
		return id1;
	}

	private JLabel getId2() {
		if (id2 == null) {
			id2 = new JLabel();
			id2.setText("to:");
			id2.setBounds(343, 233, 59, 15);
		}
		return id2;
	}

	private JLabel getLectureLabel() {
		if (lectureLabel == null) {
			lectureLabel = new JLabel();
			lectureLabel.setText("lecture:");
			lectureLabel.setBounds(147, 202, 85, 15);
		}
		return lectureLabel;
	}

	private JLabel getLastLearnedLabel() {
		if (lastLearnedLabel == null) {
			lastLearnedLabel = new JLabel();
			lastLearnedLabel.setText("last learned before:");
			lastLearnedLabel.setBounds(147, 263, 147, 18);
		}
		return lastLearnedLabel;
	}

	private JTextField getLastLearnedField() {
		if (lastLearnedField == null) {
			lastLearnedField = new JTextField();
			lastLearnedField.setText("mm:dd:yyyy");
			lastLearnedField.setBounds(306, 262, 95, 22);
		}
		return lastLearnedField;
	}

	private JCheckBox getSimplyAllBox() {
		if (simplyAllBox == null) {
			simplyAllBox = new JCheckBox();
			simplyAllBox.setText("simply all");
			simplyAllBox.setBounds(12, 294, 123, 23);
			simplyAllBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					simplyAllBoxActionPerformed(evt);
				}
			});
		}
		return simplyAllBox;
	}

	private JSeparator getJSeparator3() {
		if (jSeparator3 == null) {
			jSeparator3 = new JSeparator();
			jSeparator3.setBounds(3, 406, 535, 16);
		}
		return jSeparator3;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("cancel");
			cancelButton.setBounds(100, 415, 144, 32);
			cancelButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					cancelButtonMouseClicked(evt);
				}
			});
		}
		return cancelButton;
	}

	private JButton getStartTestButton() {
		if (startTestButton == null) {
			startTestButton = new JButton();
			startTestButton.setText("start test");
			startTestButton.setBounds(303, 417, 147, 32);
			startTestButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					startTestButtonMouseClicked(evt);
				}
			});
		}
		return startTestButton;
	}

	private JComboBox getJComboBox1() {
		if (lectureBox == null) {
			// get number of lectures => SHIFT + 1!
			String[] lectures = new String[this.sqlite.getNumberOfLectures() + 1];
			lectures[0] = "";
			for (int i = 1; i < lectures.length; i++)
				lectures[i] = "Lecture " + i;
			ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(lectures);
			lectureBox = new JComboBox();
			lectureBox.setModel(jComboBox1Model);
			lectureBox.setBounds(255, 197, 195, 22);
		}
		return lectureBox;
	}

	private JSeparator getJSeparator4() {
		if (jSeparator4 == null) {
			jSeparator4 = new JSeparator();
			jSeparator4.setBounds(4, 326, 535, 16);
		}
		return jSeparator4;
	}

	private JLabel getDirectionLabel() {
		if (directionLabel == null) {
			directionLabel = new JLabel();
			directionLabel.setText("direction:");
			directionLabel.setBounds(51, 345, 76, 15);
		}
		return directionLabel;
	}

	private ButtonGroup getDirectionGroup() {
		if (directionGroup == null) {
			directionGroup = new ButtonGroup();
		}
		return directionGroup;
	}

	private JRadioButton getTransToChineseButton() {
		if (transToChineseButton == null) {
			transToChineseButton = new JRadioButton();
			transToChineseButton.setText("translation \u2192 chinese");
			transToChineseButton.setBounds(190, 342, 169, 20);
			transToChineseButton.setFont(CVoc.getFont12plain());
		}
		return transToChineseButton;
	}

	private JRadioButton getTransToPinyinFlashButton() {
		if (transToPinyinFlashButton == null) {
			transToPinyinFlashButton = new JRadioButton();
			transToPinyinFlashButton.setText("translation \u2192 flash card");
			transToPinyinFlashButton.setBounds(5, 374, 178, 19);
			transToPinyinFlashButton.setSelected(true);
			transToPinyinFlashButton.setFont(CVoc.getFont12plain());
		}
		return transToPinyinFlashButton;
	}

	private JRadioButton getChineseToTransButton() {
		if (chineseToTransButton == null) {
			chineseToTransButton = new JRadioButton();
			chineseToTransButton.setText("chinese \u2192 translation");
			chineseToTransButton.setBounds(188, 374, 164, 19);
			chineseToTransButton.setFont(CVoc.getFont12plain());
		}
		return chineseToTransButton;
	}

	private JRadioButton getTransToPinyinButton() {
		if (transToPinyinButton == null) {
			transToPinyinButton = new JRadioButton();
			transToPinyinButton.setText("translation \u2192 pinyin");
			transToPinyinButton.setBounds(364, 343, 177, 19);
			transToPinyinButton.setFont(CVoc.getFont12plain());
		}
		return transToPinyinButton;
	}

	private JRadioButton getFlashCardButton() {
		if (flashCardButton == null) {
			flashCardButton = new JRadioButton();
			flashCardButton.setText("chinese \u2192 flash card");
			flashCardButton.setBounds(364, 374, 163, 19);
			flashCardButton.setFont(CVoc.getFont12plain());
		}
		return flashCardButton;
	}

	private void startTestButtonMouseClicked(MouseEvent evt) {

		// Set easy pinyin or not
		if (this.easyPinyin.isSelected())
			CVoc.easyPinyinEnter = true;
		else
			CVoc.easyPinyinEnter = false;

		// Set the test generator
		this.testGen.setIdFrom(Integer.valueOf(this.fromField.getText()));
		this.testGen.setIdTo(Integer.valueOf(this.toField.getText()));
		this.testGen.setOnlyUnknown(this.onlyUnknown.isSelected());
		this.testGen
				.setMaximumVocs(Integer.valueOf(this.maxVocsField.getText()));
		if (!this.lastLearnedField.getText().contains("mm:dd:yyyy")) {
			Calendar cal = Calendar.getInstance();
			cal.set(Integer
					.valueOf(this.lastLearnedField.getText().split(":")[2]),
					Integer.valueOf(this.lastLearnedField.getText().split(":")[0]) - 1,
					Integer.valueOf(this.lastLearnedField.getText().split(":")[1]));
			this.testGen.setLastLearnedBefore(cal.getTimeInMillis());
		}
		if (this.lectureBox.getSelectedIndex() > 0)
			this.testGen.setLecture(this.lectureBox.getSelectedIndex());
		// Get generated vocs
		ArrayList<Vocable> vocs = null;
		if (this.linearTestButton.isSelected())
			vocs = this.testGen.linearTest();
		else if (this.randomTestButton.isSelected())
			vocs = this.testGen.randomTest(null);
		else if (this.mostUnknownButton.isSelected())
			vocs = this.testGen.mostUnknownTest();
		else if (this.mostLastLearnedButton.isSelected())
			vocs = this.testGen.mostLastLearnedTest();
		else if (this.mostRareLearnedTestButton.isSelected())
			vocs = this.testGen.mostRareLearnedTest();
		else if (this.magicTestButton.isSelected())
			vocs = this.testGen.magicTest();

		// Start test window
		if (this.chineseToTransButton.isSelected())
			ChineseToTrans.startTest(this.sqlite, vocs);
		else if (this.transToChineseButton.isSelected())
			TransToChinese.startTest(this.sqlite, vocs);
		else if (this.transToPinyinButton.isSelected())
			TransToPinyin.startTest(this.sqlite, vocs);
		else if (this.flashCardButton.isSelected())
			ChineseFlashCardTest.startTest(this.sqlite, vocs);
		else if (this.transToPinyinFlashButton.isSelected())
			TransToPinyinFlashCardTest.startTest(this.sqlite, vocs);
		this.dispose();
	}

	private void cancelButtonMouseClicked(MouseEvent evt) {
		this.dispose();
	}

	private void simplyAllBoxActionPerformed(ActionEvent evt) {
		this.toggleInterval();
	}

	private void toggleInterval() {
		if (this.lectureLabel.isVisible()) {
			// Make everything invisible
			this.lectureLabel.setVisible(false);
			this.id1.setVisible(false);
			this.id2.setVisible(false);
			this.lastLearnedLabel.setVisible(false);
			this.lastLearnedField.setVisible(false);
			this.lectureBox.setVisible(false);
			this.fromField.setVisible(false);
			this.toField.setVisible(false);
			this.upToLabel.setVisible(false);
			this.maxVocsField.setVisible(false);
		} else {
			// Make other options visible
			this.lectureLabel.setVisible(true);
			this.id1.setVisible(true);
			this.id2.setVisible(true);
			this.lastLearnedLabel.setVisible(true);
			this.lastLearnedField.setVisible(true);
			this.lectureBox.setVisible(true);
			this.fromField.setVisible(true);
			this.toField.setVisible(true);
			this.upToLabel.setVisible(true);
			this.maxVocsField.setVisible(true);
		}
	}

	private JCheckBox getAllUnknownBox() {
		if (onlyUnknown == null) {
			onlyUnknown = new JCheckBox();
			onlyUnknown.setText("only unknown");
			onlyUnknown.setBounds(343, 296, 164, 19);
		}
		return onlyUnknown;
	}

	private JLabel getUpToLabel() {
		if (upToLabel == null) {
			upToLabel = new JLabel();
			upToLabel.setText("up to:");
			upToLabel.setBounds(146, 298, 78, 15);
		}
		return upToLabel;
	}

	private JTextField getMaxVocsField() {
		if (maxVocsField == null) {
			maxVocsField = new JTextField();
			maxVocsField.setText("0");
			maxVocsField.setBounds(242, 295, 61, 22);
		}
		return maxVocsField;
	}

	private JCheckBox getEasyPinyin() {
		if (easyPinyin == null) {
			easyPinyin = new JCheckBox();
			easyPinyin.setText("easy pinyin");
			easyPinyin.setBounds(343, 45, 153, 18);
			easyPinyin.setSelected(true);
		}
		return easyPinyin;
	}
}

package com.googlemail.christian667.cvoc;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.pinyin4j.PinyinHelper;

import com.google.api.GoogleAPI;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class AddVocable extends javax.swing.JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JButton addLast;
	private JButton addNext;
	private JTextField pinyinField;
	private JLabel translationL;
	private JTextField translationField;
	private JLabel pinyinL;
	private JSeparator jSeparator3;
	private JLabel topicL;
	private JButton cancelVocable;
	private JTextField chineseField;
	private JLabel chineseL;
	private JTextField lectureField;
	private JLabel lectureL;
	private SqliteHandler sqlite;
	private CVoc callback;

	public static void addVocable(SqliteHandler sqlite, CVoc callback) {
		AddVocable inst = new AddVocable(sqlite, callback);
		SwingUtilities.invokeLater(inst);
	}

	public AddVocable(SqliteHandler sqlite, CVoc callback) {
		super();
		this.sqlite = sqlite;
		this.callback = callback;
		GoogleAPI.setHttpReferrer("www.google.de");
		GoogleAPI.setKey("AIzaSyARmCo5lPqilmfxa5itQBms7V4qI3Yay_k");
		initGUI();
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
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
				chineseL.setBounds(13, 76, 94, 15);
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
				chineseField.addInputMethodListener(new InputMethodListener() {
					public void inputMethodTextChanged(InputMethodEvent evt) {
						chineseFieldKeyReleased(null);
					}

					public void caretPositionChanged(InputMethodEvent evt) {
					}
				});
				chineseField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent evt) {
						chineseFieldKeyReleased(evt);
					}
				});
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
				translationField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent evt) {
						translationFieldKeyReleased(evt);
					}
				});
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
				translationL.setBounds(12, 223, 102, 15);
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
				pinyinField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent evt) {
						pinyinFieldKeyReleased(evt);
					}
				});
			}
			{
				jSeparator3 = new JSeparator();
				getContentPane().add(
						jSeparator3,
						new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
								GridBagConstraints.NORTH,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				jSeparator3.setBounds(12, 298, 372, 3);
			}
			{
				addNext = new JButton();
				cancelVocable = new JButton();
				getContentPane().add(addNext);
				addNext.setText("add and goto next");
				addNext.setBounds(12, 313, 174, 46);
				addNext.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						addNextMouseClicked(evt);
					}
				});
			}
			{
				addLast = new JButton();
				getContentPane().add(addLast);
				addLast.setText("add and finish");
				addLast.setBounds(216, 313, 181, 46);
				addLast.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						addLastMouseClicked(evt);
					}
				});
			}
			{
				getContentPane().add(cancelVocable);
				cancelVocable.setText("cancel");
				cancelVocable.setBounds(0, 370, 408, 31);
				cancelVocable.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						cancelVocableMouseClicked(evt);
					}
				});
			}
			{
				lectureL = new JLabel();
				getContentPane().add(lectureL);
				lectureL.setText("lecture:");
				lectureL.setBounds(107, 267, 79, 15);
			}
			{
				lectureField = new JTextField();
				getContentPane().add(lectureField);
				lectureField.setBounds(204, 264, 43, 22);
				lectureField.addKeyListener(new KeyAdapter() {
					public void keyReleased(KeyEvent evt) {
						lectureFieldKeyReleased(evt);
					}
				});
			}
			{
				topicL = new JLabel();
				getContentPane().add(topicL);
				topicL.setText("entering new vocable...");
				topicL.setBounds(54, 6, 321, 30);
				topicL.setFont(CVoc.getFont20italic());
			}
			pack();
			this.setSize(408, 407);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private boolean store() {
		String chinese = this.chineseField.getText();
		String pinyin = this.pinyinField.getText();
		String translation = this.translationField.getText().replace("ö", "oe")
				.replace("ü", "ue").replace("ä", "ae");
		if (chinese.length() > 0 && pinyin.length() > 0
				&& translation.length() > 0
				&& this.lectureField.getText().length() > 0) {
			long lecture = Integer.valueOf(this.lectureField.getText());
			Vocable tmpVoc = new Vocable(chinese, pinyin, translation, lecture);
			if (this.sqlite.addVocable(tmpVoc)) {
				this.callback.updateAll();
				return true;
			} else
				return false;
		} else
			return false;
	}

	private void clearFields() {
		this.chineseField.setText("");
		this.pinyinField.setText("");
		this.translationField.setText("");
		this.chineseField.requestFocusInWindow();
	}

	private void chineseFieldKeyReleased(KeyEvent evt) {
		if (evt != null) {
			if (evt.getKeyCode() == 10) {
				this.store();
				this.clearFields();
			} else if (evt.getKeyCode() == 27) {
				this.dispose();
				this.callback.updateAll();
			}
		}

		// Get pinyin and set to field
		StringBuilder tmpPinyin = new StringBuilder();
		for (int i = 0; i < this.chineseField.getText().length(); i++) {
			String[] tmpStringBuffer = null;
			tmpStringBuffer = PinyinHelper
					.toHanyuPinyinStringArray(this.chineseField.getText()
							.charAt(i));
			if (tmpStringBuffer != null) {
				tmpPinyin.append(this.convertPinyin(tmpStringBuffer[0]));
				if (tmpStringBuffer.length > 1) {
					// Alternatives
					tmpPinyin.append("(");
					for (int k = 1; k < tmpStringBuffer.length; k++) {
						tmpPinyin
								.append(this.convertPinyin(tmpStringBuffer[k]));
						if (k + 1 < tmpStringBuffer.length)
							tmpPinyin.append("|");
					}
					tmpPinyin.append(")");
				}
			}
		}
		if (tmpPinyin.length() > 0)
			this.pinyinField.setText(tmpPinyin.toString());
		else
			this.pinyinField.setText("");
		// Get translation and set to translationField (if possible)
		if (this.chineseField.getText().length() > 0) {
			try {
				this.translationField.setText(Translate.DEFAULT.execute(
						this.chineseField.getText(), Language.CHINESE,
						Language.GERMAN));
			} catch (Exception e) {
			}
		} else
			this.translationField.setText("");
	}

	private void translationFieldKeyReleased(KeyEvent evt) {
		if (evt.getKeyCode() == 10) {
			this.store();
			this.clearFields();
		}
	}

	private void pinyinFieldKeyReleased(KeyEvent evt) {
		if (evt.getKeyCode() == 10) {
			this.store();
			this.clearFields();
		}
	}

	private void addNextMouseClicked(MouseEvent evt) {
		this.store();
		this.clearFields();
	}

	private void addLastMouseClicked(MouseEvent evt) {
		this.dispose();
		this.store();
	}

	private void cancelVocableMouseClicked(MouseEvent evt) {
		this.dispose();
		this.callback.updateAll();
	}

	private void lectureFieldKeyReleased(KeyEvent evt) {
		if (evt.getKeyCode() == 10) {
			this.store();
			this.clearFields();
		}
	}

	private String convertPinyin(String pinyin) {
		// Has tone number, convert it
		// ā á ǎ à | ē é ě è | ī í ǐ ì | ō ó ǒ ò | ū ú ǔ ù |
		// a, e, o, u, i
		if (pinyin.contains("a")) {
			if (pinyin.contains("1")) {
				pinyin = pinyin.replace("a", "ā");
			} else if (pinyin.contains("2")) {
				pinyin = pinyin.replace("a", "á");
			} else if (pinyin.contains("3")) {
				pinyin = pinyin.replace("a", "ǎ");
			} else if (pinyin.contains("4")) {
				pinyin = pinyin.replace("a", "à");
			}
		} else if (pinyin.contains("e")) {
			if (pinyin.contains("1")) {
				pinyin = pinyin.replace("e", "ē");
			} else if (pinyin.contains("2")) {
				pinyin = pinyin.replace("e", "é");
			} else if (pinyin.contains("3")) {
				pinyin = pinyin.replace("e", "ě");
			} else if (pinyin.contains("4")) {
				pinyin = pinyin.replace("e", "è");
			}
		} else if (pinyin.contains("o")) {
			if (pinyin.contains("1")) {
				pinyin = pinyin.replace("o", "ō");
			} else if (pinyin.contains("2")) {
				pinyin = pinyin.replace("o", "ó");
			} else if (pinyin.contains("3")) {
				pinyin = pinyin.replace("o", "ǒ");
			} else if (pinyin.contains("4")) {
				pinyin = pinyin.replace("o", "ò");
			}
		} else if (pinyin.contains("u")) {
			if (pinyin.contains("1")) {
				pinyin = pinyin.replace("u", "ū");
			} else if (pinyin.contains("2")) {
				pinyin = pinyin.replace("u", "ú");
			} else if (pinyin.contains("3")) {
				pinyin = pinyin.replace("u", "ǔ");
			} else if (pinyin.contains("4")) {
				pinyin = pinyin.replace("u", "ù");
			}
		} else if (pinyin.contains("i")) {
			if (pinyin.contains("1")) {
				pinyin = pinyin.replace("i", "ī");
			} else if (pinyin.contains("2")) {
				pinyin = pinyin.replace("i", "í");
			} else if (pinyin.contains("3")) {
				pinyin = pinyin.replace("i", "ǐ");
			} else if (pinyin.contains("4")) {
				pinyin = pinyin.replace("i", "ì");
			}
		}
		pinyin = pinyin.replace("1", "");
		pinyin = pinyin.replace("2", "");
		pinyin = pinyin.replace("3", "");
		pinyin = pinyin.replace("4", "");
		pinyin = pinyin.replace("5", "");
		return pinyin;
	}
}

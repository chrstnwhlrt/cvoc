package com.googlemail.christian667.cvoc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class InfoDialog extends javax.swing.JFrame implements Runnable {
	private static final long serialVersionUID = 4937830746897509883L;
	private JTextPane infoArea;
	private JButton closeButton;
	private String message;
	private boolean closeAble = true;

	public static InfoDialog show(String message, boolean closeAble) {
		InfoDialog inst = new InfoDialog(message, closeAble);
		SwingUtilities.invokeLater(inst);
		return inst;
	}

	public InfoDialog(String message, boolean closeAble) {
		super();
		this.message = message;
		this.closeAble = closeAble;
		initGUI();
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void close() {
		this.dispose();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			this.setTitle("cVoc info");
			this.setIconImage(CVoc.logoImage);
			this.setResizable(false);
			this.setUndecorated(true);
			this.setAlwaysOnTop(true);
			{
				infoArea = new JTextPane();
				getContentPane().add(infoArea);
				infoArea.setEditable(false);
				infoArea.setText(this.message);
				infoArea.setBounds(12, 12, 330, 143);
				infoArea.setBorder(BorderFactory
						.createBevelBorder(BevelBorder.LOWERED));
				StyledDocument doc = infoArea.getStyledDocument();
				SimpleAttributeSet center = new SimpleAttributeSet();
				StyleConstants
						.setAlignment(center, StyleConstants.ALIGN_CENTER);
				doc.setParagraphAttributes(0, doc.getLength(), center, false);
			}
			{
				closeButton = new JButton();
				getContentPane().add(closeButton);
				closeButton.setText("close");
				closeButton.setToolTipText("close info");
				closeButton.setBounds(93, 160, 155, 29);
				closeButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						if (closeAble)
							dispose();
					}
				});
			}
			pack();
			this.setSize(368, 200);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

}

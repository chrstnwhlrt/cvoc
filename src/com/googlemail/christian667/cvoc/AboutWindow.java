package com.googlemail.christian667.cvoc;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class AboutWindow extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel image;
	private JLabel versionLabel;
	private JButton closeButton;
	private JTextArea comArea;

	public static void showAboutWindow() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AboutWindow inst = new AboutWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public AboutWindow() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);
			this.setTitle("cVoc - about cVoc");
			this.setIconImage(CVoc.logoImage);
			this.setUndecorated(true);
			this.setResizable(false);
			this.setPreferredSize(new java.awt.Dimension(308, 260));
			{
				image = new JLabel(new ImageIcon(CVoc.logoImage)) {
					private static final long serialVersionUID = 1L;

					@Override
					public void paintComponent(Graphics g) {
						g.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0,
								getWidth(), getHeight(), null);
					}
				};
				image.setBounds(120, 10, 60, 60);
				getContentPane().add(image);
			}
			{
				versionLabel = new JLabel();
				getContentPane().add(versionLabel);
				versionLabel.setText("version " + CVoc.VERSION);
				versionLabel.setBounds(90, 72, 116, 15);
				versionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
				versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				comArea = new JTextArea();
				getContentPane().add(comArea);
				comArea.setText("cVoc is created by christian wohlert \n [christian667@googlemail.com] \n License: GPL v3 \n [http://www.gnu.org/licenses/gpl.html] \n All rights reserved");
				comArea.setBounds(12, 93, 282, 91);
				comArea.setLineWrap(true);
				comArea.setEditable(false);
				comArea.setBorder(BorderFactory
						.createBevelBorder(BevelBorder.LOWERED));
				comArea.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						if (Desktop.isDesktopSupported())
							try {
								Desktop.getDesktop()
										.mail(new URI(
												"mailto:christian667@googlemail.com"));
							} catch (IOException e) {
							} catch (URISyntaxException e) {
							}
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}

				});
			}
			{
				closeButton = new JButton();
				getContentPane().add(closeButton);
				closeButton.setText("close");
				closeButton.setToolTipText("close about");
				closeButton.setBounds(114, 196, 77, 22);
				closeButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						dispose();
					}
				});
			}
			pack();
			this.setSize(308, 220);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}
}

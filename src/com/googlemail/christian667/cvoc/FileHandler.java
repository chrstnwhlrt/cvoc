package com.googlemail.christian667.cvoc;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;

import javax.swing.SwingUtilities;

public class FileHandler extends javax.swing.JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private JFileChooser fileChooser;
	private File file;
	private SqliteHandler sqlite;
	private int mode;
	public static final int FIRSTSTART = 0;
	public static final int OPENTOSYNC = 1;
	public static final int EXPORTTOFILE = 2;
	public static final int CREATENEW = 3;
	public static final int OPENTOIMPORTNEW = 4;
	public static final int EXPORTWITHOUTUSAGEDATA = 5;

	public static FileHandler getInstance(int mode, SqliteHandler sqlite) {
		FileHandler fchoose = new FileHandler(mode, sqlite);
		SwingUtilities.invokeLater(fchoose);
		return fchoose;
	}

	public File getFileAndWait() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// this.dispose(); TODO strange error
		return this.file;
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public FileHandler(int mode, SqliteHandler sqlite) {
		super();
		this.sqlite = sqlite;
		this.mode = mode;
		initGUI();
	}

	private void initGUI() {
		try {
			fileChooser = new JFileChooser();
			if (CVoc.logoImage != null)
				this.setIconImage(CVoc.logoImage);
			else
				this.setIconImage(Toolkit.getDefaultToolkit().getImage(
						this.getClass().getResource("/img/logo.png")));
			this.setTitle("please choose a file");
			getContentPane().add(fileChooser, BorderLayout.CENTER);
			fileChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					fileChooserActionPerformed(evt);
				}
			});
			this.setSize(538, 354);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fileChooserActionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().contains("CancelSelection")) {
			this.dispose();
			if (this.mode == FIRSTSTART)
				System.exit(0);
		} else if (this.fileChooser.getSelectedFile() != null) {
			// Set the file
			this.file = this.fileChooser.getSelectedFile();
			if (this.mode == EXPORTTOFILE)
				this.sqlite.exportDatabaseTo(this.file.getAbsolutePath());
			if (this.mode == CREATENEW)
				this.sqlite.newDatabase(this.file);
			if (this.mode == OPENTOSYNC)
				this.sqlite.sync(this.file, false);
			if (this.mode == OPENTOIMPORTNEW)
				this.sqlite.sync(this.file, true);
			if (this.mode == EXPORTWITHOUTUSAGEDATA)
				this.sqlite.exportWithoutUsageData(this.file.getAbsolutePath());
			if (this.mode == FIRSTSTART)
				synchronized (this) {
					this.notifyAll();
				}
		}
		this.dispose();
	}

}

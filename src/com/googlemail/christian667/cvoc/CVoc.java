package com.googlemail.christian667.cvoc;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.googlemail.christian667.autoupdater.AutoUpdater;
import com.googlemail.christian667.autoupdater.UpdateAble;

/**
 * @author Christian Wohlert [christian667@googlemail.com]
 * 
 */
public class CVoc extends javax.swing.JFrame implements Runnable, UpdateAble {
	public static boolean easyPinyinEnter = true;

	// Update and version informations
	public static final int VERSION = 4;
	public static final String VERSIONURLSTRING = "http://www.tu-harburg.de/~sicw1071/cvoc/version";

	/**
	 * Fetch name of the launched file, to find the correct update file if
	 * available.. (*.exe or *.jar)
	 * 
	 * @return The filename or if fetching failed "cvoc.jar"
	 */
	public static String getFileName() {
		String fileName = null;
		try {
			fileName = new File(CVoc.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI()).getName();
		} catch (URISyntaxException e) {
			// If fetching the file name failed
			return "cvoc.jar";
		}
		return fileName;
	}

	public static final String FILEURLSTRING = "http://www.tu-harburg.de/~sicw1071/cvoc/"
			+ CVoc.getFileName();

	// The private members
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JPanel searchPanel;
	private JMenuItem settingsItem;
	private JMenuItem aboutItem;
	private JMenuItem printItem;
	private JMenuItem syncItem;
	private JMenuItem exitItem;
	private JLabel infoL;
	private JButton removeButton;
	private JMenuItem helpItem;
	private JMenuItem statisticsItem;
	private JButton editButton;
	private JPanel infoPanel;
	private JMenuItem exportItem;
	private JMenuItem exportWithoutUsageItem;
	private JMenuItem createNewItem;
	private JMenuItem openToSyncItem;
	private JMenuItem openToImportWithoutSync;
	private JMenu extrasMenu;
	private JButton testButton;
	private JButton newVocable;
	private JPanel bottomPanel;
	private JTable resultTable;
	private JMenu helpMenu;
	private JMenu fileMenu;
	private JRadioButton chineseSearch;
	private JScrollPane resultScrollPane;
	private JRadioButton translationSearch;
	private JRadioButton pinyinSearch;
	private ButtonGroup searchGroup;
	private JTextField searchField;
	private JLabel jLabel1;
	private SqliteHandler sqlite;
	private int port = 3141;
	private HashMap<String, String> logins;
	private Font font;
	private static Font font12plain;
	private static Font font20italic;
	private static Font font72plain;
	private static Font font18plain;
	private static Font font36plain;
	private static Font font85plain;
	private static Font font25plain;
	private static Font font15plain;

	public static Image logoImage;
	public static InfoDialog dialog;

	public static void main(String[] args) {
		if (args.length == 1)
			System.out.println("cVoc version: " + VERSION);
		else {
			if (AutoUpdater.update(new UpdateAble() {

				@Override
				public void updateInProgress() {
					CVoc.dialog = InfoDialog.show(
							"Update in progress, please wait!", false);
				}

			}, VERSIONURLSTRING, FILEURLSTRING, VERSION)) {
				CVoc.dialog.close();
				InfoDialog.show("Update done - please restart cVoc!", true);
			} else {
				FileHandler fchoose = FileHandler.getInstance(
						FileHandler.FIRSTSTART, null);
				SqliteHandler sqlite = new SqliteHandler(
						fchoose.getFileAndWait());
				sqlite.openDatabase();
				CVoc inst = new CVoc(sqlite);
				SwingUtilities.invokeLater(inst);
			}
		}
	}

	public CVoc(SqliteHandler sqlite) {
		super();
		this.sqlite = sqlite;
		this.logins = new HashMap<String, String>();
		this.logins.put("cVoc", "cvoc*secure*standard*password");
		this.buildFontSet();
		initGUI();
	}

	public void run() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void buildFontSet() {
		InputStream fontStream = CVoc.class
				.getResourceAsStream("DroidSansFallback.ttf");
		this.font = null;
		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		font12plain = this.font.deriveFont(Font.PLAIN, 12);
		font15plain = this.font.deriveFont(Font.PLAIN, 15);
		font18plain = this.font.deriveFont(Font.PLAIN, 18);
		font25plain = this.font.deriveFont(Font.PLAIN, 25);
		font36plain = this.font.deriveFont(Font.PLAIN, 36);
		font72plain = this.font.deriveFont(Font.PLAIN, 72);
		font85plain = this.font.deriveFont(Font.PLAIN, 85);
		font20italic = this.font.deriveFont(Font.ITALIC, 20);
	}

	public static Font getFont25plain() {
		return font25plain;
	}

	public static Font getFont85plain() {
		return font85plain;
	}

	public static Font getFont36plain() {
		return font36plain;
	}

	public static Font getFont18plain() {
		return font18plain;
	}

	public static Font getFont72plain() {
		return font72plain;
	}

	public static Font getFont12plain() {
		return font12plain;
	}

	public static Font getFont20italic() {
		return font20italic;
	}

	private void initGUI() {
		try {
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] { 0.0, 0.1, 0.0, 0.1, 0.1 };
			thisLayout.rowHeights = new int[] { 28, 7, 212, 7, 20 };
			thisLayout.columnWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
			thisLayout.columnWidths = new int[] { 7, 7, 7, 7 };
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			logoImage = Toolkit.getDefaultToolkit().getImage(
					this.getClass().getResource("/img/logo.png"));
			this.setIconImage(logoImage);
			this.setTitle("cVoc");
			this.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent evt) {
					thisWindowClosing(evt);
				}
			});
			{
				searchPanel = new JPanel();
				BoxLayout jPanel1Layout = new BoxLayout(searchPanel,
						javax.swing.BoxLayout.X_AXIS);
				searchPanel.setLayout(jPanel1Layout);
				getContentPane().add(
						searchPanel,
						new GridBagConstraints(0, 0, 4, 1, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				searchPanel.setBorder(BorderFactory
						.createEtchedBorder(BevelBorder.LOWERED));
				searchPanel.setPreferredSize(new Dimension(200, 20));
				{
					jLabel1 = new JLabel();
					searchPanel.add(jLabel1);
					jLabel1.setText("search:");
				}
				{
					searchField = new JTextField();
					searchField.setFont(font12plain);
					searchPanel.add(searchField);
					searchField.addKeyListener(new KeyAdapter() {
						public void keyReleased(KeyEvent evt) {
							searchFieldKeyReleased(evt);
						}
					});
					searchField
							.addInputMethodListener(new InputMethodListener() {
								public void inputMethodTextChanged(
										InputMethodEvent evt) {
									searchFieldKeyReleased(null);
								}

								public void caretPositionChanged(
										InputMethodEvent evt) {
								}
							});
				}
				{
					chineseSearch = new JRadioButton();
					searchPanel.add(chineseSearch);
					chineseSearch.setText("chinese");
					chineseSearch.setSelected(true);
					this.getSearchGroup().add(chineseSearch);
				}
				{
					pinyinSearch = new JRadioButton();
					searchPanel.add(pinyinSearch);
					pinyinSearch.setText("pinyin");
					this.getSearchGroup().add(pinyinSearch);

				}
				{
					translationSearch = new JRadioButton();
					searchPanel.add(translationSearch);
					translationSearch.setText("translation");
					this.getSearchGroup().add(translationSearch);

				}
			}
			{
				resultScrollPane = new JScrollPane();
				getContentPane().add(
						resultScrollPane,
						new GridBagConstraints(0, 1, 4, 2, 0.0, 0.0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				getContentPane().add(
						getBottomPanel(),
						new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0,
								GridBagConstraints.SOUTH,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				getContentPane().add(
						getInfoPanel(),
						new GridBagConstraints(0, 4, 4, 1, 0.0, 0.0,
								GridBagConstraints.SOUTH,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0,
										0, 0), 0, 0));
				resultScrollPane.setBorder(BorderFactory
						.createBevelBorder(BevelBorder.LOWERED));
				resultScrollPane
						.setBackground(new java.awt.Color(255, 255, 255));
				resultScrollPane.setAlignmentX(1.0f);
				resultScrollPane.setAlignmentY(1.0f);
				resultScrollPane.setViewportView(getResultTable());
			}
			{
				menuBar = new JMenuBar();
				setJMenuBar(menuBar);
				menuBar.add(getFileMenu());
				// menuBar.add(getExtrasMenu()); TODO: IMPLEMENT
				menuBar.add(getHelpMenu());
			}
			ToolTipManager.sharedInstance().setDismissDelay(30000);
			pack();
			this.setSize(568, 361);
		} catch (Exception e) {
			// add your error handling code here
			e.printStackTrace();
		}
	}

	private void updateResults(ArrayList<Vocable> results) {
		String[][] resultRows = new String[results.size()][5];
		for (int i = 0; i < results.size(); i++) {
			resultRows[i][0] = results.get(i).getChinese();
			resultRows[i][1] = results.get(i).getPinyin();
			resultRows[i][2] = results.get(i).gettranslation();
			resultRows[i][3] = String.valueOf(results.get(i).getLecture());
			resultRows[i][4] = String.valueOf(results.get(i).getId());
		}
		TableModel resultTableModel = new DefaultTableModel(resultRows,
				new String[] { "chinese", "pinyin", "translation", "lecture",
						"id" });
		resultTable.setModel(resultTableModel);
		this.infoL.setText("info: " + resultRows.length + " results");
	}

	private ButtonGroup getSearchGroup() {
		if (searchGroup == null) {
			searchGroup = new ButtonGroup();
		}
		return searchGroup;
	}

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("file");
			fileMenu.add(getCreateNewItem());
			fileMenu.add(getOpenToSyncItem());
			fileMenu.add(getOpenToImportWithoutSync());
			fileMenu.add(getExportItem());
			fileMenu.add(getExportWithoutUsageItem());
			fileMenu.add(getExitItem());
		}
		return fileMenu;
	}

	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("help");
			helpMenu.add(getHelpItem());
			helpMenu.add(getAboutItem());
		}
		return helpMenu;
	}

	private JTable getResultTable() {
		if (resultTable == null) {
			resultTable = new JTable() {
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int rowIndex, int colIndex) {
					return false; // Disallow the editing of any cell
				}
			};
			resultTable.setShowVerticalLines(false);
			resultTable.setFont(font15plain);
			this.updateResults(this.sqlite.getAll(false));
			resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return resultTable;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.add(getNewVocable());
			bottomPanel.add(getEditButton());
			bottomPanel.add(getRemoveButton());
			bottomPanel.add(getTestButton());
		}
		return bottomPanel;
	}

	private JButton getNewVocable() {
		if (newVocable == null) {
			newVocable = new JButton();
			newVocable.setText("add new vocable");
			newVocable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					newVocableMouseClicked(evt);
				}
			});
		}
		return newVocable;
	}

	private JButton getTestButton() {
		if (testButton == null) {
			testButton = new JButton();
			testButton.setText("start  test");
			testButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					testButtonMouseClicked(evt);
				}
			});
		}
		return testButton;
	}

	@SuppressWarnings("unused")
	// TODO add extras content
	private JMenu getExtrasMenu() {
		if (extrasMenu == null) {
			extrasMenu = new JMenu();
			extrasMenu.setText("extras");
			extrasMenu.add(getSyncItem());
			extrasMenu.add(getPrintItem());
			extrasMenu.add(getStatisticsItem());
			extrasMenu.add(getSettingsItem());
		}
		return extrasMenu;
	}

	private JMenuItem getExportItem() {
		if (exportItem == null) {
			exportItem = new JMenuItem();
			exportItem
					.setToolTipText("<html>Export database with all vocs and your <br>learning progress data (→ just copy it)</html>");
			exportItem.setText("export");
			exportItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FileHandler.getInstance(FileHandler.EXPORTTOFILE, sqlite);
				}
			});
		}
		return exportItem;
	}

	private JMenuItem getExportWithoutUsageItem() {
		if (exportWithoutUsageItem == null) {
			exportWithoutUsageItem = new JMenuItem();
			exportWithoutUsageItem
					.setToolTipText("<html>Export database with all vocs WITHOUT your learning progress,<br>→ send your new entered vocs to a friend who imports it \"without sync\"</html>");
			exportWithoutUsageItem.setText("export WITHOUT usage data");
			exportWithoutUsageItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FileHandler.getInstance(FileHandler.EXPORTWITHOUTUSAGEDATA,
							sqlite);
				}
			});
		}
		return exportWithoutUsageItem;
	}

	private JMenuItem getCreateNewItem() {
		if (createNewItem == null) {
			createNewItem = new JMenuItem();
			createNewItem
					.setToolTipText("<html>Creates a new BLANK database on a given file and opens it,<br>→ same as entering a new filename while start of cVoc</html>");
			createNewItem.setText("new database");
			createNewItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FileHandler.getInstance(FileHandler.CREATENEW, sqlite);
				}
			});
		}
		return createNewItem;
	}

	private JMenuItem getOpenToSyncItem() {
		if (openToSyncItem == null) {
			openToSyncItem = new JMenuItem();
			openToSyncItem
					.setToolTipText("<html>Open another database and synchronize the vocs and PERSONAL LEARNING PROGRESS,<br>→ learn at work, have two databases, which you want to sync sometimes</html>");
			openToSyncItem.setText("import and sync");
			openToSyncItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FileHandler.getInstance(FileHandler.OPENTOSYNC, sqlite);
				}
			});
		}
		return openToSyncItem;
	}

	private JMenuItem getOpenToImportWithoutSync() {
		if (openToImportWithoutSync == null) {
			openToImportWithoutSync = new JMenuItem();
			openToImportWithoutSync.setText("import (only new) vocabulary");
			openToImportWithoutSync
					.setToolTipText("<html>Just import the new vocabulary without sync,<br>→ get new entered vocabulary by a friend and import the new one</html>");
			openToImportWithoutSync.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					FileHandler
							.getInstance(FileHandler.OPENTOIMPORTNEW, sqlite);
				}
			});
		}
		return openToImportWithoutSync;
	}

	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem();
			exitItem.setText("exit");
			exitItem.setToolTipText("close and exit cVoc");
			exitItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dispose();
				}
			});
		}
		return exitItem;
	}

	private JMenuItem getSyncItem() {
		if (syncItem == null) {
			syncItem = new JMenuItem();
			syncItem.setText("start synchronization");
			syncItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					startSync();
				}
			});
		}
		return syncItem;
	}

	private void startSync() {
		Socket socket = null;
		boolean authenticationSuccess = false;
		InfoDialog tmpDialog = InfoDialog.show(
				"ready for incomming syncronisation connections.. please wait",
				false);
		try {
			// Wait blocking for the connection
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("A");
			socket = serverSocket.accept();
			tmpDialog.close();
			authenticationSuccess = HamsterToolkit.serverAuthentication(socket,
					null, this.logins);
		} catch (IOException e) {
			System.out.println("B");
			tmpDialog.close();

		}
		tmpDialog = InfoDialog.show("synchronisation running, please wait",
				false);

		if (authenticationSuccess) {
			try {
				// Receive database to sync
				BufferedInputStream bufIn = new BufferedInputStream(
						socket.getInputStream());
				BufferedOutputStream bufOut = new BufferedOutputStream(
						socket.getOutputStream());

				File tmpFile = File.createTempFile(".databaseToSync", ".db");
				FileOutputStream fOut = new FileOutputStream(tmpFile);

				tmpDialog.close();
				tmpDialog = InfoDialog.show("loading database to sync", false);

				byte[] buffer = new byte[1];
				while (bufIn.read(buffer) != -1)
					fOut.write(buffer);

				fOut.flush();
				fOut.close();

				tmpDialog.close();
				tmpDialog = InfoDialog.show("synchronizing databases", false);

				// sync and delete file
				this.sqlite.sync(tmpFile, false);
				tmpFile.delete();

				// export to tmpFile
				this.sqlite.exportDatabaseTo(tmpFile.getAbsolutePath());
				FileInputStream fIn = new FileInputStream(
						tmpFile.getAbsolutePath());

				tmpDialog.close();
				tmpDialog = InfoDialog.show("sending synced database", false);

				// And send it
				while (fIn.read(buffer) != -1)
					bufOut.write(buffer);

				// close connection
				socket.close();
			} catch (IOException e) {
				tmpDialog.close();
			}
			tmpDialog.close();
			tmpDialog = InfoDialog.show(
					"finished synchronization successfully", true);
		} else {
			// close connection
			InfoDialog.show("authentication failed", true);
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private JMenuItem getPrintItem() {
		if (printItem == null) {
			printItem = new JMenuItem();
			printItem.setText("print database");
		}
		return printItem;
	}

	private JMenuItem getAboutItem() {
		if (aboutItem == null) {
			aboutItem = new JMenuItem();
			aboutItem.setText("about");
			aboutItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					AboutWindow.showAboutWindow();
				}
			});
		}
		return aboutItem;
	}

	private JMenuItem getSettingsItem() {
		if (settingsItem == null) {
			settingsItem = new JMenuItem();
			settingsItem.setText("settings");
		}
		return settingsItem;
	}

	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			BoxLayout infoPanelLayout = new BoxLayout(infoPanel,
					javax.swing.BoxLayout.X_AXIS);
			infoPanel.setLayout(infoPanelLayout);
			infoPanel.setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			infoPanel.add(getInfoL());
		}
		return infoPanel;
	}

	private JLabel getInfoL() {
		if (infoL == null) {
			infoL = new JLabel();
			infoL.setText("info");
		}
		return infoL;
	}

	private void thisWindowClosing(WindowEvent evt) {
		this.sqlite.closeDatabase();
	}

	private void searchFieldKeyReleased(KeyEvent evt) {
		if (evt != null)
			if (evt.getKeyCode() == 27)
				this.dispose();
		if (this.searchField.getText().length() > 0) {
			if (evt != null)
				this.searchField.setText(this.searchField.getText()
						.replace("ö", "oe").replace("ü", "ue")
						.replace("ä", "ae"));
			if (this.chineseSearch.isSelected())
				this.updateResults(this.sqlite.searchChinese(this.searchField
						.getText()));
			else if (this.pinyinSearch.isSelected())
				this.updateResults(this.sqlite.searchPinyin(this.searchField
						.getText()));
			else if (this.translationSearch.isSelected())
				this.updateResults(this.sqlite
						.searchtranslation(this.searchField.getText()));
		} else
			this.updateResults(this.sqlite.getAll(false));
	}

	private void newVocableMouseClicked(MouseEvent evt) {
		AddVocable.addVocable(this.sqlite, this);
	}

	private void testButtonMouseClicked(MouseEvent evt) {
		StartTest.startTest(this.sqlite);
	}

	private JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setText("edit");
			editButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					editButtonMouseClicked(evt);
				}
			});
		}
		return editButton;
	}

	private void editButtonMouseClicked(MouseEvent evt) {
		Vocable selectedVoc = this.sqlite.getLecture(
				Integer.valueOf((String) this.resultTable.getValueAt(
						this.resultTable.getSelectedRow(), 3)), false).get(
				Integer.valueOf((String) this.resultTable.getValueAt(
						this.resultTable.getSelectedRow(), 4)) - 1);
		EditVocable.editVocable(selectedVoc, this.sqlite, this);
		this.updateResults(this.sqlite.getAll(false));
	}

	private JMenuItem getStatisticsItem() {
		if (statisticsItem == null) {
			statisticsItem = new JMenuItem();
			statisticsItem.setText("statistics");
		}
		return statisticsItem;
	}

	private JMenuItem getHelpItem() {
		if (helpItem == null) {
			helpItem = new JMenuItem();
			helpItem.setText("report issue | request feature");
			helpItem.setToolTipText("<html>Report and issue to the developer <br>or just request a new feature at github</html>");
			if (Desktop.isDesktopSupported())
				helpItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						try {
							Desktop.getDesktop()
									.browse(new URI(
											"https://github.com/christian667/cvoc"));
						} catch (IOException e) {
						} catch (URISyntaxException e) {
						}
					}
				});
		}
		return helpItem;
	}

	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("remove");
			removeButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					removeButtonMouseClicked(evt);
				}
			});
		}
		return removeButton;
	}

	private void removeButtonMouseClicked(MouseEvent evt) {
		this.sqlite.rmVocable(this.sqlite.getLecture(
				Integer.valueOf((String) this.resultTable.getValueAt(
						this.resultTable.getSelectedRow(), 3)), false).get(
				Integer.valueOf((String) this.resultTable.getValueAt(
						this.resultTable.getSelectedRow(), 4)) - 1));
		this.updateAll();
	}

	public void updateAll() {
		this.updateResults(this.sqlite.getAll(false));
		this.searchField.requestFocusInWindow();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static String simplifyPinyin(String pinyin) {
		pinyin = pinyin.replace("ā", "a");
		pinyin = pinyin.replace("á", "a");
		pinyin = pinyin.replace("ǎ", "a");
		pinyin = pinyin.replace("à", "a");
		pinyin = pinyin.replace("ē", "e");
		pinyin = pinyin.replace("é", "e");
		pinyin = pinyin.replace("ě", "e");
		pinyin = pinyin.replace("è", "e");
		pinyin = pinyin.replace("ō", "o");
		pinyin = pinyin.replace("ó", "o");
		pinyin = pinyin.replace("ǒ", "o");
		pinyin = pinyin.replace("ò", "o");
		pinyin = pinyin.replace("ū", "u");
		pinyin = pinyin.replace("ú", "u");
		pinyin = pinyin.replace("ǔ", "u");
		pinyin = pinyin.replace("ù", "u");
		pinyin = pinyin.replace("ī", "i");
		pinyin = pinyin.replace("í", "i");
		pinyin = pinyin.replace("ǐ", "i");
		pinyin = pinyin.replace("ì", "i");
		return pinyin;
	}

	@Override
	public void updateInProgress() {
		// TODO Auto-generated method stub

	}
}

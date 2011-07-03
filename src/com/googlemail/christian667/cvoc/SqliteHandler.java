package com.googlemail.christian667.cvoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class SqliteHandler {
	private File databaseFile;
	private SqlJetDb database;
	private ArrayList<Integer> createdLectures;
	private int skillToKnown = 5;

	public SqliteHandler(File database) {
		this.databaseFile = database;
	}

	public boolean openDatabase() {
		if (this.databaseFile != null) {
			try {
				debug("Creating database from file.. loading");
				database = SqlJetDb.open(this.databaseFile, true);
				database.runTransaction(new ISqlJetTransaction() {
					public Object run(SqlJetDb database) throws SqlJetException {
						database.getOptions().setUserVersion(1);
						return true;
					}
				}, SqlJetTransactionMode.WRITE);

			} catch (SqlJetException e) {
				debug(e.getMessage());
				return false;
			}
			debug("..loading the index table..");
			// Load index table
			try {
				this.database.beginTransaction(SqlJetTransactionMode.WRITE);
			} catch (SqlJetException e2) {
				debug(e2.getMessage());
				return false;
			}
			try {
				this.database.getTable("index");
			} catch (SqlJetException e) {
				String createTableQuery = "CREATE TABLE " + "index" + " ("
						+ "id" + " INTEGER NOT NULL PRIMARY KEY)";
				try {
					// Or create it
					debug("index table not found: creating new one");
					this.database.createTable(createTableQuery);
				} catch (SqlJetException e1) {
					debug(e1.getMessage());
					return false;
				}
			}
			// And commit
			try {
				this.database.commit();
			} catch (SqlJetException e1) {
				debug(e1.getMessage());
				return false;
			}
			debug("loading created lecture table ids");
			this.loadCreatedLectures();
			return true;
		} else
			return false;
	}

	public void newDatabase(File databaseFile) {
		this.closeDatabase();
		this.databaseFile = databaseFile;
		this.openDatabase();
	}

	private boolean loadCreatedLectures() {
		this.createdLectures = new ArrayList<Integer>();
		try {
			this.database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = this.database.getTable("index");
			ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
			if (!cursor.eof()) {
				do {
					this.createdLectures.add((int) cursor.getInteger("id"));
				} while (cursor.next());
			}
			cursor.close();
			this.database.commit();
		} catch (SqlJetException e) {
			debug(e.getMessage());
			return false;
		}
		debug(this.createdLectures.size() + " lecture tables found");
		return true;
	}

	public boolean closeDatabase() {
		try {
			debug("closing database");
			this.database.close();
		} catch (SqlJetException e) {
			debug(e.getMessage());
			return false;
		}
		return true;
	}

	public void sync(File databaseToSync, boolean onlyImportNewVocs) {
		SqliteHandler dbToSync = new SqliteHandler(databaseToSync);
		dbToSync.openDatabase();
		ArrayList<Vocable> vocsToSync = dbToSync.getAll(false);
		dbToSync.closeDatabase();
		HashMap<String, Vocable> ownVocsIndexed = this.getAllIndexed(false);
		for (int i = 0; i < vocsToSync.size(); i++) {
			if (ownVocsIndexed.containsKey(vocsToSync.get(i).getChinese())) {
				// Only sync if not to import just the new one
				if (!onlyImportNewVocs) {
					// Need to sync
					Vocable tmpVocToSync = vocsToSync.get(i);
					Vocable ownVoc = ownVocsIndexed.get(tmpVocToSync
							.getChinese());
					// now sync them
					// The pinyin
					if (!ownVoc.getPinyin().contains(tmpVocToSync.getPinyin()))
						ownVoc.setPinyin(ownVoc.getPinyin() + ","
								+ tmpVocToSync.getPinyin());
					// The translation
					if (!ownVoc.gettranslation().contains(
							tmpVocToSync.gettranslation()))
						ownVoc.settranslation(ownVoc.gettranslation() + ","
								+ tmpVocToSync.gettranslation());
					// Last learned
					if (ownVoc.getLastlearned() < tmpVocToSync.getLastlearned())
						ownVoc.setLastlearned(tmpVocToSync.getLastlearned());
					// Known since
					if (ownVoc.getKnownsince() > tmpVocToSync.getKnownsince())
						ownVoc.setKnownsince(tmpVocToSync.getKnownsince());
					// Skill avg
					ownVoc.setSkill((ownVoc.getSkill() + tmpVocToSync
							.getSkill()) / 2);
					// Attemtps until learned => +
					ownVoc.setAttemptsUntilLearned(ownVoc
							.getAttemptsUntilLearned()
							+ tmpVocToSync.getAttemptsUntilLearned());
					// learning attemtps => +
					ownVoc.setLearningattempts(ownVoc.getLearningattempts()
							+ tmpVocToSync.getLearningattempts());
					// Set to edited
					this.editVocable(ownVoc);
					// And correct in comparison list, may come a second time
					ownVocsIndexed.remove(ownVoc.getChinese());
					ownVocsIndexed.put(ownVoc.getChinese(), ownVoc);
				}
			} else {
				// New voc, just add it
				this.addVocable(vocsToSync.get(i));
				// And add to comparison list
				ownVocsIndexed.put(vocsToSync.get(i).getChinese(),
						vocsToSync.get(i));
			}
		}
	}

	public void exportDatabaseTo(String path) {
		this.closeDatabase();
		copyfile(this.databaseFile.getAbsolutePath(), path);
		this.openDatabase();
		debug("Database exported");
	}

	public void exportWithoutUsageData(String path) {
		this.closeDatabase();
		copyfile(this.databaseFile.getAbsolutePath(), path);
		this.openDatabase();
		SqliteHandler exportDB = new SqliteHandler(new File(path));
		exportDB.openDatabase();
		exportDB.resetUsageData();
		exportDB.closeDatabase();
	}

	public void resetUsageData() {
		try {
			this.database.beginTransaction(SqlJetTransactionMode.WRITE);
		} catch (SqlJetException e1) {
			debug(e1.getMessage());
		}

		ISqlJetCursor updateCursor;

		try {
			for (int i = 1; i < this.createdLectures.size() + 1; i++) {
				updateCursor = this.database.getTable(
						"lecture" + String.valueOf(i)).open();
				do {
					updateCursor
							.update(updateCursor.getValue("id"),
									updateCursor.getValue("chinese"),
									updateCursor.getValue("pinyin"),
									updateCursor.getValue("translation"), 0, 0,
									0, 0, 0);
				} while (updateCursor.next());
				updateCursor.close();
			}

			// Finally commit update
			this.database.commit();
		} catch (SqlJetException e) {
			debug(e.getMessage());
		}
	}

	private static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean addVocable(Vocable voc) {
		if (this.database != null) {
			boolean newLecture = false;
			try {
				// Check for lectureTable, if not available, create it and write
				// vocable
				this.database.beginTransaction(SqlJetTransactionMode.WRITE);
			} catch (SqlJetException e1) {
				debug(e1.getMessage());
				return false;
			}
			debug("adding new vocable");
			String tableName = "lecture" + String.valueOf(voc.getLecture());
			ISqlJetTable table = null;
			try {
				table = this.database.getTable(tableName);
				debug("lecture table found");
			} catch (SqlJetException e) {
				debug("creating new lecture table");
				String createTableQuery = "CREATE TABLE " + tableName + " ("
						+ "id" + " INTEGER NOT NULL PRIMARY KEY , " + "chinese"
						+ " TEXT NOT NULL , " + "pinyin" + " TEXT NOT NULL , "
						+ "translation" + " TEXT NOT NULL , " + "lastlearned"
						+ " INTEGER , " + "knownsince" + " INTEGER , "
						+ "skill" + " INTEGER , " + "attemptsuntillearned"
						+ " INTEGER NOT NULL , " + "learningattempts"
						+ " INTEGER)";
				try {
					// Table not found, create new for this lecture
					this.database.createTable(createTableQuery);
					table = this.database.getTable(tableName);
					// And set voc's id to the first one = 1
					voc.setId(1);
					this.database.getTable("index").insert(voc.getLecture());
					newLecture = true;
				} catch (SqlJetException e1) {
					debug(e.getMessage());
					return false;
				}
			}
			// Set the id
			if (voc.getId() == 0)
				try {
					voc.setId(table.order(table.getPrimaryKeyIndexName())
							.getRowCount() + 1);
				} catch (SqlJetException e1) {
					debug(e1.getMessage());
					return false;
				}
			boolean insertError = false;
			do {
				// Write to table
				try {
					table.insert(voc.getId(), voc.getChinese(),
							voc.getPinyin(), voc.gettranslation(),
							voc.getLastlearned(), voc.getKnownsince(),
							voc.getSkill(), voc.getAttemptsUntilLearned(),
							voc.getLearningattempts());
					insertError = false;
				} catch (SqlJetException e) {
					insertError = true;
					// increase the id
					voc.setId(voc.getId() + 1);
				}
			} while (insertError);

			// And commit
			try {
				this.database.commit();
			} catch (SqlJetException e) {
				debug(e.getMessage());
				return false;
			}

			// Load new lectures
			if (newLecture)
				this.loadCreatedLectures();

			return true;
		}
		// Database not opened yet
		return false;
	}

	public void rmVocable(Vocable voc) {
		try {
			this.database.beginTransaction(SqlJetTransactionMode.WRITE);
			ISqlJetTable table = database
					.getTable("lecture" + voc.getLecture());
			ISqlJetCursor cursor = table.open();
			if (!cursor.eof()) {
				do {
					if (cursor.getInteger("id") == voc.getId()) {
						cursor.delete();
						debug(voc.getChinese() + "(" + voc.getId() + ")"
								+ "deleted");
					}
				} while (cursor.next());
			}
			this.database.commit();
		} catch (SqlJetException e) {
			debug(e.getMessage());
		}

	}

	public void editVocable(Vocable voc) {
		this.rmVocable(voc);
		this.addVocable(voc);
	}

	public ArrayList<Vocable> getAll(boolean onlyUnknown) {
		if (this.database == null)
			return null;
		ArrayList<Vocable> vocs = new ArrayList<Vocable>();
		for (int i = 0; i < this.createdLectures.size(); i++) {
			vocs.addAll(this.getLecture(this.createdLectures.get(i),
					onlyUnknown));
		}
		return vocs;
	}

	public HashMap<String, Vocable> getAllIndexed(boolean onlyUnknown) {
		ArrayList<Vocable> vocs = this.getAll(onlyUnknown);
		HashMap<String, Vocable> vocsIndexed = new HashMap<String, Vocable>();
		for (int i = 0; i < vocs.size(); i++)
			vocsIndexed.put(vocs.get(i).getChinese(), vocs.get(i));
		return vocsIndexed;
	}

	public ArrayList<Vocable> getLecture(int lecture, boolean onlyUnknown) {
		if (this.database == null)
			return null;
		ArrayList<Vocable> vocs = new ArrayList<Vocable>();
		try {
			this.database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = this.database.getTable("lecture" + lecture);
			ISqlJetCursor cursor = table.open();
			if (!cursor.eof()) {
				do {
					Vocable tmpVoc = new Vocable(cursor.getString("chinese"),
							cursor.getString("pinyin"),
							cursor.getString("translation"), lecture);
					tmpVoc.setLastlearned(cursor.getInteger("lastlearned"));
					tmpVoc.setKnownsince(cursor.getInteger("knownsince"));
					tmpVoc.setSkill(cursor.getInteger("skill"));
					tmpVoc.setAttemptsUntilLearned(cursor
							.getInteger("attemptsuntillearned"));
					tmpVoc.setLearningattempts(cursor
							.getInteger("learningattempts"));
					tmpVoc.setId(cursor.getInteger(("id")));
					if (tmpVoc.getKnownsince() == 0 || !onlyUnknown)
						vocs.add(tmpVoc);
				} while (cursor.next());
			}

			this.database.commit();
			return vocs;
		} catch (SqlJetException e) {
			debug(e.getMessage());
			try {
				this.database.commit();
			} catch (SqlJetException e1) {
			}
			return null;
		}
	}

	public ArrayList<Vocable> searchChinese(String chinese) {
		ArrayList<Vocable> vocs = new ArrayList<Vocable>();
		try {
			this.database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			for (int i = 0; i < this.createdLectures.size(); i++) {
				ISqlJetTable table = this.database.getTable("lecture"
						+ this.createdLectures.get(i));
				ISqlJetCursor cursor = table.open();
				if (!cursor.eof()) {
					do {
						if (cursor.getString("chinese").contains(chinese)) {
							Vocable tmpVoc = new Vocable(
									cursor.getString("chinese"),
									cursor.getString("pinyin"),
									cursor.getString("translation"),
									this.createdLectures.get(i));
							tmpVoc.setLastlearned(cursor
									.getInteger("lastlearned"));
							tmpVoc.setKnownsince(cursor
									.getInteger("knownsince"));
							tmpVoc.setSkill(cursor.getInteger("skill"));
							tmpVoc.setAttemptsUntilLearned(cursor
									.getInteger("attemptsuntillearned"));
							tmpVoc.setLearningattempts(cursor
									.getInteger("learningattempts"));
							tmpVoc.setId(cursor.getInteger(("id")));
							vocs.add(tmpVoc);
						}
					} while (cursor.next());
				}
			}
			this.database.commit();

		} catch (SqlJetException e) {
			debug(e.getMessage());
		}
		return vocs;
	}

	public ArrayList<Vocable> searchtranslation(String translation) {
		ArrayList<Vocable> vocs = new ArrayList<Vocable>();
		try {
			this.database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			for (int i = 0; i < this.createdLectures.size(); i++) {
				ISqlJetTable table = this.database.getTable("lecture"
						+ this.createdLectures.get(i));
				ISqlJetCursor cursor = table.open();
				if (!cursor.eof()) {
					do {
						if (cursor.getString("translation").toLowerCase()
								.contains(translation.toLowerCase())) {
							Vocable tmpVoc = new Vocable(
									cursor.getString("chinese"),
									cursor.getString("pinyin"),
									cursor.getString("translation"),
									this.createdLectures.get(i));
							tmpVoc.setLastlearned(cursor
									.getInteger("lastlearned"));
							tmpVoc.setKnownsince(cursor
									.getInteger("knownsince"));
							tmpVoc.setSkill(cursor.getInteger("skill"));
							tmpVoc.setAttemptsUntilLearned(cursor
									.getInteger("attemptsuntillearned"));
							tmpVoc.setLearningattempts(cursor
									.getInteger("learningattempts"));
							tmpVoc.setId(cursor.getInteger(("id")));
							vocs.add(tmpVoc);
						}
					} while (cursor.next());
				}
			}
			this.database.commit();
		} catch (SqlJetException e) {
			debug(e.getMessage());
		}

		return vocs;
	}

	public ArrayList<Vocable> searchPinyin(String pinyin) {
		ArrayList<Vocable> vocs = new ArrayList<Vocable>();
		try {
			this.database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			for (int i = 0; i < this.createdLectures.size(); i++) {
				ISqlJetTable table = this.database.getTable("lecture"
						+ this.createdLectures.get(i));
				ISqlJetCursor cursor = table.open();
				if (!cursor.eof()) {
					do {
						if (CVoc.simplifyPinyin(
								cursor.getString("pinyin").toLowerCase())
								.contains(pinyin.toLowerCase())) {
							Vocable tmpVoc = new Vocable(
									cursor.getString("chinese"),
									cursor.getString("pinyin"),
									cursor.getString("translation"),
									this.createdLectures.get(i));
							tmpVoc.setLastlearned(cursor
									.getInteger("lastlearned"));
							tmpVoc.setKnownsince(cursor
									.getInteger("knownsince"));
							tmpVoc.setSkill(cursor.getInteger("skill"));
							tmpVoc.setAttemptsUntilLearned(cursor
									.getInteger("attemptsuntillearned"));
							tmpVoc.setLearningattempts(cursor
									.getInteger("learningattempts"));
							tmpVoc.setId(cursor.getInteger(("id")));
							vocs.add(tmpVoc);
						}
					} while (cursor.next());
				}
			}
			this.database.commit();
		} catch (SqlJetException e) {
			debug(e.getMessage());
		}

		return vocs;
	}

	public void debug(String debugString) {
		// System.out.println(debugString);
	}

	public int getSkillToKnown() {
		return skillToKnown;
	}

	public void setSkillToKnown(int skillToKnown) {
		this.skillToKnown = skillToKnown;
	}

	public int getNumberOfLectures() {
		return this.createdLectures.size();
	}
}

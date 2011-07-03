package com.googlemail.christian667.cvoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestGenerator {
	private SqliteHandler sqlite;
	private int lecture = 0;
	private long lastLearnedBefore = 0;
	private long idFrom = 0;
	private long idTo = 0;
	private boolean onlyUnknown = false;
	private int maximumVocs = 0;

	public TestGenerator(SqliteHandler sqlite) {
		this.sqlite = sqlite;
	}

	private ArrayList<Vocable> filtered() {
		ArrayList<Vocable> vocs = this.sqlite.getAll(this.onlyUnknown);
		if (this.lecture == 0 && this.lastLearnedBefore == 0
				&& this.idFrom == 0 && this.idTo == 0) {
			// Simply all
			return vocs;
		} else {
			// Range the lecture
			if (this.lecture > 0)
				vocs = this.sqlite.getLecture(this.lecture, this.onlyUnknown);
			// Range the id
			if (this.idTo > 0 && this.idTo > this.idFrom) {
				ArrayList<Vocable> idVocs = new ArrayList<Vocable>();
				for (int i = 0; i < vocs.size(); i++)
					if (vocs.get(i).getId() >= this.idFrom
							&& vocs.get(i).getId() <= this.idTo)
						idVocs.add(vocs.get(i));
				vocs = idVocs;
			}
			// Range the time
			if (this.lastLearnedBefore > 0) {
				ArrayList<Vocable> timeVocs = new ArrayList<Vocable>();
				for (int i = 0; i < vocs.size(); i++)
					if (vocs.get(i).getLastlearned() < this.lastLearnedBefore)
						timeVocs.add(vocs.get(i));
				vocs = timeVocs;
			}
			return vocs;
		}
	}

	private ArrayList<Vocable> cutMaximum(ArrayList<Vocable> vocs) {
		if (this.maximumVocs > vocs.size() || this.maximumVocs == 0)
			return vocs;
		else {
			ArrayList<Vocable> cutVocs = new ArrayList<Vocable>();
			for (int i = 0; i < this.maximumVocs; i++)
				cutVocs.add(vocs.get(i));
			return cutVocs;
		}
	}

	public ArrayList<Vocable> randomTest(ArrayList<Vocable> inVocs) {
		ArrayList<Vocable> vocs = this.filtered();
		if (inVocs != null)
			vocs = inVocs;
		ArrayList<Vocable> randomVocs = new ArrayList<Vocable>();
		for (int i = 0; i < vocs.size(); i++) {
			int random = getPositivRandomIntIn(0, vocs.size());
			if (!randomVocs.contains(vocs.get(random)))
				randomVocs.add(vocs.get(random));
			else
				i--;
		}
		return this.cutMaximum(randomVocs);
	}

	public ArrayList<Vocable> linearTest() {
		return this.cutMaximum(this.filtered());
	}

	public ArrayList<Vocable> mostUnknownTest() {
		ArrayList<Vocable> vocs = this.filtered();
		Collections.sort(vocs, new VocableUnknownComparator());
		return this.cutMaximum(vocs);
	}

	public ArrayList<Vocable> mostLastLearnedTest() {
		ArrayList<Vocable> vocs = this.filtered();
		Collections.sort(vocs, new VocableLastLearnedComparator());
		return this.cutMaximum(vocs);
	}

	public ArrayList<Vocable> magicTest() {
		ArrayList<Vocable> mUnkVocs = this.cutMaximum(this.mostUnknownTest());
		ArrayList<Vocable> mLlVocs = this
				.cutMaximum(this.mostLastLearnedTest());
		ArrayList<Vocable> mVocs = new ArrayList<Vocable>();
		for (int i = 0; i < mUnkVocs.size(); i++) {
			mVocs.add(mUnkVocs.get(i));
			mVocs.add(mLlVocs.get(i));
		}
		return this.randomTest(mVocs);
	}

	public ArrayList<Vocable> mostRareLearnedTest() {
		ArrayList<Vocable> vocs = this.filtered();
		Collections.sort(vocs, new VocableRareLearnedComparator());
		return this.cutMaximum(vocs);
	}

	// Getters and setters

	public void setLecture(int lecture) {
		this.lecture = lecture;
	}

	public void setLastLearnedBefore(long lastLearnedBefore) {
		this.lastLearnedBefore = lastLearnedBefore;
	}

	public void setIdFrom(long idFrom) {
		this.idFrom = idFrom;
	}

	public void setIdTo(long idTo) {
		this.idTo = idTo;
	}

	public long getLecture() {
		return lecture;
	}

	public long getLastLearnedBefore() {
		return lastLearnedBefore;
	}

	public long getIdFrom() {
		return idFrom;
	}

	public long getIdTo() {
		return idTo;
	}

	public boolean isOnlyUnknown() {
		return onlyUnknown;
	}

	public void setOnlyUnknown(boolean onlyUnknown) {
		this.onlyUnknown = onlyUnknown;
	}

	public int getMaximumVocs() {
		return maximumVocs;
	}

	public void setMaximumVocs(int maximumVocs) {
		this.maximumVocs = maximumVocs;
	}

	private static int getPositivRandomIntIn(int intervalBegin, int intervalEnd) {
		return intervalBegin
				+ (int) (Math.random() * (intervalEnd - intervalBegin));
	}

	class VocableUnknownComparator implements Comparator<Vocable> {
		public int compare(Vocable first, Vocable second) {
			int unknownComp = -1;
			if (first.getSkill() >= second.getSkill())
				unknownComp = 1;
			return (unknownComp);
		}
	}

	class VocableLastLearnedComparator implements Comparator<Vocable> {
		public int compare(Vocable first, Vocable second) {
			int unknownComp = -1;
			if (first.getLastlearned() >= second.getLastlearned())
				unknownComp = 1;
			return (unknownComp);
		}
	}

	class VocableRareLearnedComparator implements Comparator<Vocable> {
		public int compare(Vocable first, Vocable second) {
			int unknownComp = -1;
			if (first.getLearningattempts() >= second.getLearningattempts())
				unknownComp = 1;
			return (unknownComp);
		}
	}
}

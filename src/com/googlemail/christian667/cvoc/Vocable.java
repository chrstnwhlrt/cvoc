package com.googlemail.christian667.cvoc;

public class Vocable {
	// Set on creation
	private long id = 0;
	private String chinese = "";
	private String pinyin = "";
	private String translation;
	private long lecture = 0;
	// Variable
	private long lastlearned = 0;
	private long knownsince = 0;
	private long skill = 0;
	private long attemptsuntillearned = 0;
	private long learningattempts = 0;

	public Vocable(String chinese, String pinyin, String translation,
			long lecture) {
		this.chinese = chinese;
		this.pinyin = pinyin;
		this.translation = translation;
		this.lecture = lecture;
	}

	public Vocable resetUsageData() {
		this.lastlearned = 0;
		this.knownsince = 0;
		this.skill = 0;
		this.attemptsuntillearned = 0;
		this.learningattempts = 0;
		return this;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChinese() {
		return chinese;
	}

	public void setChinese(String chinese) {
		this.chinese = chinese;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String gettranslation() {
		return translation;
	}

	public void settranslation(String translation) {
		this.translation = translation;
	}

	public long getLastlearned() {
		return lastlearned;
	}

	public void setLastlearned(long lastlearned) {
		this.lastlearned = lastlearned;
	}

	public long getKnownsince() {
		return knownsince;
	}

	public void setKnownsince(long knownsince) {
		this.knownsince = knownsince;
	}

	public long getSkill() {
		return skill;
	}

	public void setSkill(long skill) {
		this.skill = skill;
	}

	public long getLecture() {
		return lecture;
	}

	public void setLecture(long lecture) {
		this.lecture = lecture;
	}

	public long getAttemptsUntilLearned() {
		return attemptsuntillearned;
	}

	public void setAttemptsUntilLearned(long attemptsUntilLearned) {
		this.attemptsuntillearned = attemptsUntilLearned;
	}

	public String[] toDisplayStringArray() {
		String[] stringArray = new String[] { this.getChinese(),
				this.getPinyin(), this.gettranslation(),
				String.valueOf(this.getLecture()), String.valueOf(this.getId()) };
		return stringArray;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public long getLearningattempts() {
		return learningattempts;
	}

	public void setLearningattempts(long learningattempts) {
		this.learningattempts = learningattempts;
	}
}

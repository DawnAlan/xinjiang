package com.cj.model.func.modular.FloodPredict.entity;
/**
 * 数据的时间处理
 * 
 * @author leileilei
 *
 */
public class DateIndex implements Comparable<DateIndex> {
	private int year;
	private int index;
	private int time;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof DateIndex)) {
			throw new ClassCastException("类型错误"); // 输入类型错误
		}

		DateIndex p = (DateIndex) obj;// 强制转换

		return (this.year == p.getYear() && this.index == p.getIndex() && this.time == p.getTime());
	}

	public int hashCode() {
		return year * 100000 + index * 100 + time;
	}

	public DateIndex getLastDateIndex(int maxIndex) {
		DateIndex lastDateIndex = new DateIndex();
		if (this.getTime() == 0) {
			if (this.index == 1) {

				lastDateIndex.setYear(this.year - 1);
				if (maxIndex == 36) {
					lastDateIndex.setIndex(36);
				} else {
					if (lastDateIndex.getYear() % 4 == 0 && lastDateIndex.getYear() % 100 != 0
							|| lastDateIndex.getYear() % 400 == 0) {
						lastDateIndex.setIndex(366);
					} else {
						lastDateIndex.setIndex(365);
					}
				}

			} else {
				lastDateIndex.setYear(this.year);
				lastDateIndex.setIndex(this.index - 1);
			}
		} else {
			if (this.getTime() == 2) {
				if (this.getIndex() == 1) {
					lastDateIndex.setYear(this.year - 1);
					if (lastDateIndex.getYear() % 4 == 0 && lastDateIndex.getYear() % 100 != 0
							|| lastDateIndex.getYear() % 400 == 0) {
						lastDateIndex.setIndex(366);
					} else {
						lastDateIndex.setIndex(365);
					}
				} else {
					lastDateIndex.setYear(this.year);
					lastDateIndex.setIndex(this.index - 1);
				}
				lastDateIndex.setTime(20);
			} else {
				lastDateIndex.setYear(this.year);
				lastDateIndex.setIndex(this.index);
				lastDateIndex.setTime(this.time - 6);
			}
		}
		return lastDateIndex;
	}

	public DateIndex getNextDateIndex(int maxIndex) {
		DateIndex nextDateIndex = new DateIndex();
		if (this.getTime() == 0) {
			if (maxIndex == 36) {
				if (this.index == maxIndex) {
					nextDateIndex.setIndex(1);
					nextDateIndex.setYear(this.year + 1);
				} else {
					nextDateIndex.setIndex(this.index + 1);
					nextDateIndex.setYear(this.year);
				}
			} else {
				if (this.year % 4 == 0 && this.year % 100 != 0 || this.year % 400 == 0) {
					if (this.index == 366) {
						nextDateIndex.setIndex(1);
						nextDateIndex.setYear(this.year + 1);
					} else {
						nextDateIndex.setIndex(this.index + 1);
						nextDateIndex.setYear(this.year);
					}
				} else {
					if (this.index == 365) {
						nextDateIndex.setIndex(1);
						nextDateIndex.setYear(this.year + 1);
					} else {
						nextDateIndex.setIndex(this.index + 1);
						nextDateIndex.setYear(this.year);
					}
				}
			}
		} else {
			if (this.getTime() == 20) {
				if (this.year % 4 == 0 && this.year % 100 != 0 || this.year % 400 == 0) {
					if (this.index == 366) {
						nextDateIndex.setIndex(1);
						nextDateIndex.setYear(this.year + 1);
						nextDateIndex.setTime(2);
					} else {
						nextDateIndex.setIndex(this.index + 1);
						nextDateIndex.setYear(this.year);
						nextDateIndex.setTime(2);
					}
				} else {
					if (this.index == 365) {
						nextDateIndex.setIndex(1);
						nextDateIndex.setYear(this.year + 1);
						nextDateIndex.setTime(2);
					} else {
						nextDateIndex.setIndex(this.index + 1);
						nextDateIndex.setYear(this.year);
						nextDateIndex.setTime(2);
					}
				}

			} else {
				nextDateIndex.setIndex(this.index);
				nextDateIndex.setYear(this.year);
				nextDateIndex.setTime(this.time + 6);
			}
		}
		return nextDateIndex;
	}

	/**
	 * 返回特定时段
	 * 
	 * @param preIndexLenth
	 * @param maxIndex
	 * @return
	 */
	public DateIndex getCertainDateIndex(int preIndexLenth, int maxIndex) {
		DateIndex dateIndex = new DateIndex();
		int index = this.index + preIndexLenth;
		int year = this.year;
		if (index < 0) {
			year = year - 1;
			index = maxIndex + index;
		} else if (index > maxIndex) {
			year = year + 1;
			index = index - maxIndex;
		}
		dateIndex.setIndex(index);
		dateIndex.setYear(year);
		return dateIndex;

	}

	public int compareTo(DateIndex anothorIndex) {

		return this.index - anothorIndex.getIndex();

	}

}

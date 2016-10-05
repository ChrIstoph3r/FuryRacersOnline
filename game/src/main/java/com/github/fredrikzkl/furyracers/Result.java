package com.github.fredrikzkl.furyracers;

import java.util.concurrent.TimeUnit;

public class Result implements Comparable<Result>{
	
	int id;
	int totalTenthsOfSeconds;
	String date;
	String username;
	String carModel;
	String formattedTime;
	
	
	public String getFormattedTime() {
		
		long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(totalTenthsOfSeconds*100);
		String formattedTime = totalMinutes + ":";
		
		long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(totalTenthsOfSeconds*100);
		
		long seconds = totalSeconds - totalMinutes*60;
		if(seconds < 10)  formattedTime += "0";
		formattedTime += seconds + ":";
		
 		long tenthsOfSeconds = totalTenthsOfSeconds - totalSeconds*10;
		formattedTime +=  tenthsOfSeconds;
		
		return formattedTime;
	}

	public void setFormattedTime(String formattedTime) {
		this.formattedTime = formattedTime;
	}
	
	public String getCarModel() {
		return carModel;
	}

	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTime() {
		return totalTenthsOfSeconds;
	}

	public void setTime(int totalTenthsOfSeconds) {
		this.totalTenthsOfSeconds = totalTenthsOfSeconds;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int compareTimes(int time){
		
		return Integer.compare( time, getTime());
	}
	
	@Override
	public int compareTo(Result result) {
		return Integer.compare( result.getTime(), getTime());
	}
	


}

package com.gtunes

class Album {
		
	String title
	
	static hasMany = [songs : Song]

	public String toString() {
		title
	}
    static constraints = {
    }
}

package com.gtunes

class Song {

	String title
	String artist
	Integer duration	
	Album album
	
	public String toString() {
		title	
	}
    static constraints = {
		title blank:false
		artist blank:false
		duration min: 1
    }
}

package demo.app.example1.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Hospital {

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Id
	private int id;
	private String name;
	private String city;
	private double rating;

	public Hospital(int id, String name, String city, double rating) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.rating = rating;
	}

	public Hospital() {
		super();
	}

	@Override
	public String toString() {
		return "Hospital [id=" + id + ", name=" + name + ", city=" + city + ", rating=" + rating + "]";
	}

	
}

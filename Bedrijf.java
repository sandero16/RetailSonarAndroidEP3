package data;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the bedrijven database table.
 * 
 */
@Entity
@Table(name="bedrijven")
@NamedQuery(name="Bedrijven.findAll", query="SELECT b FROM Bedrijf b")
public class Bedrijf implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int bedrijfId;

	private String gemeente;

	private String naam;

	private int nummer;

	private int postcode;

	private String straat;

	//bi-directional many-to-one association to Filiaal
	@OneToMany(mappedBy="bedrijven")
	private List<FiliaalGegevens> filiaals;

	//bi-directional many-to-one association to User
	@OneToMany(mappedBy="bedrijf")
	@JoinColumn(name="user_Id", nullable=false)
	private List<User> users;

	public Bedrijf() {
	}

	public int getBedrijfId() {
		return this.bedrijfId;
	}

	public void setBedrijfId(int bedrijfId) {
		this.bedrijfId = bedrijfId;
	}

	public String getGemeente() {
		return this.gemeente;
	}

	public void setGemeente(String gemeente) {
		this.gemeente = gemeente;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public int getNummer() {
		return this.nummer;
	}

	public void setNummer(int nummer) {
		this.nummer = nummer;
	}

	public int getPostcode() {
		return this.postcode;
	}

	public void setPostcode(int postcode) {
		this.postcode = postcode;
	}

	public String getStraat() {
		return this.straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	public List<FiliaalGegevens> getFiliaals() {
		return this.filiaals;
	}

	public void setFiliaals(List<FiliaalGegevens> filiaals) {
		this.filiaals = filiaals;
	}
	public String getAdres(){
		return (this.gemeente+" "+this.postcode+", "+this.straat+" "+this.nummer);
	}

	public FiliaalGegevens addFiliaal(FiliaalGegevens filiaal) {
		getFiliaals().add(filiaal);
		filiaal.setBedrijf(this);

		return filiaal;
	}

	public FiliaalGegevens removeFiliaal(FiliaalGegevens filiaal) {
		getFiliaals().remove(filiaal);
		filiaal.setBedrijf(null);

		return filiaal;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User addUser(User user) {
		getUsers().add(user);
		user.setBedrijf(this);

		return user;
	}

	public User removeUser(User user) {
		getUsers().remove(user);
		user.setBedrijf(null);

		return user;
	}

}
package data;

import java.io.Serializable;
import javax.persistence.*;

import org.eclipse.persistence.annotations.ReadOnly;

import java.util.List;
import java.util.ArrayList;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private int userId;
	@JoinColumn(name="bedrijfId", nullable=false)
	private int bedrijfId;
	@Lob
	private String password;

	private String username;
	

	//bi-directional many-to-many association to Group
	@ManyToMany()
	@JoinTable(
		name="user_groups"
		, joinColumns={
			@JoinColumn(name="user_id", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="group_id", nullable=false)
			}
		)
	private List<Group> groups;

	//bi-directional many-to-one association to Bedrijven
	@ManyToOne
	@JoinColumn(name="bedrijfId", updatable=false, insertable=false)
	private Bedrijf bedrijf;

	public User() {
	}
	
	public User(Bedrijf b){
		this.bedrijf=b;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getBedrijfId(){
		return this.bedrijfId;
	}
	
	public void setBedrijfId(int id){
		this.bedrijfId = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public void addGroup(Group g){
		if(this.groups==null){
			this.groups = new ArrayList<Group>(1);
		}
		this.groups.add(g);
	}

	public Bedrijf getBedrijf() {
		return this.bedrijf;
	}
	public String getBedrijfsNaam(){
		return this.bedrijf.getNaam();
	}
	
	public String getBedrijfsAndres(){
		return this.bedrijf.getAdres();
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijfId = bedrijf.getBedrijfId();
		this.bedrijf = bedrijf;
	}
///////////////////////////HENRI///////////////////////////////////
	
	@Column(name="group_id")
	private int groupId;
	
	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getGroupName(){
		if(groupId == 1){
			return "RegioManager";
		}else if(groupId == 3){
			return "ExpansieManager";
		}else{
			return "Not Working";
		}
	}
	
///////////////////////////HENRI///////////////////////////////////
	

}
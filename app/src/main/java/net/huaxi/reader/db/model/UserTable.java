package net.huaxi.reader.db.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * 用户表
 * @author ZMW
 *
 */
@DatabaseTable(tableName="user_table")
public class UserTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2943846901743949685L;
	@DatabaseField(columnName ="u_id", dataType = DataType.INTEGER,unique=true,canBeNull=false,generatedId=true)
	private int id;
	@DatabaseField(columnName="u_sid",dataType=DataType.STRING,canBeNull=false)
	private String uid;
	@DatabaseField(columnName="u_nikename",dataType=DataType.STRING)
	private String nikename;
	@DatabaseField(columnName="u_iconimg_id",dataType=DataType.STRING)
	private String iconImgId; 
	@DatabaseField(columnName="u_gender",dataType=DataType.INTEGER)
	private int gender;
	@DatabaseField(columnName="u_phone",dataType=DataType.STRING)
	private String phoneNumber;
	@DatabaseField(columnName="u_email",dataType=DataType.STRING)
	private String email;
	@DatabaseField(columnName="u_is_monthly",dataType=DataType.INTEGER)
	private int isMonthLy;//是否包月 0包月，1不包月
	@DatabaseField(columnName="u_is_tourist",dataType=DataType.INTEGER)
	private int isTourist;//游客模式
	@DatabaseField(columnName="u_booklist_update_time",dataType=DataType.LONG)
	private long updateTime ;//书籍列表更新时间。用此值与服务器上对应的值相比较，以实现书籍列表的同步。
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getNikename() {
		return nikename;
	}
	public void setNikename(String nikename) {
		this.nikename = nikename;
	}
	public String getIconImgId() {
		return iconImgId;
	}
	public void setIconImgId(String iconImgId) {
		this.iconImgId = iconImgId;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getIsMonthLy() {
		return isMonthLy;
	}
	public void setIsMonthLy(int isMonthLy) {
		this.isMonthLy = isMonthLy;
	}
	public int getIsTourist() {
		return isTourist;
	}
	public void setIsTourist(int isTourist) {
		this.isTourist = isTourist;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
	
		
	
	
	
	
}

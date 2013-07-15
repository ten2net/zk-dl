package cz.datalite.zk.components.list;

import java.util.Comparator;

public class DLListboxProfileImpl implements DLListboxProfile {

	/** profile id - usually generated by persistent storage */
	private Long id;
	
	/** identifier (e.q. session name) of listbox */ 
	private String dlListboxId;

	/** profile name - shown in lovbox */
	private String name;

	/** profile will be visible only for owner and invisible for other users if set to true */
	private boolean publicProfile = true;

	/** default profile for listbox */
	private boolean defaultProfile = false;

	/** hidden profile - invisible for user */
	private boolean hidden = false;
	
	/** profile can be overwritten using save button in profile manager */
	private boolean editable = true;

	/** profile ownew */
	private String user;

	/** column model settings serialized to json */
	private String columnModelJsonData;

	/** filter model settings serialized to json */
	private String filterModelJsonData;

	/** hash code of listbox columns in time of saving profile */
	private Integer columnsHashCode;
	
	public DLListboxProfileImpl() {
		
	}

	public DLListboxProfileImpl(Long id) {		
		this.id = id;
	}
	
	public DLListboxProfileImpl(Long id, String dlListboxId, String name) {	
		this.id = id;
		this.dlListboxId = dlListboxId;
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getDlListboxId() {
		return dlListboxId;
	}

	@Override
	public void setDlListboxId(String dlListboxId) {
		this.dlListboxId = dlListboxId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isPublicProfile() {
		return publicProfile;
	}

	@Override
	public void setPublicProfile(boolean publicProfile) {
		this.publicProfile = publicProfile;
	}
	
	@Override
	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	@Override
	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String getColumnModelJsonData() {
		return columnModelJsonData;
	}

	@Override
	public void setColumnModelJsonData(String columnModelJsonData) {
		this.columnModelJsonData = columnModelJsonData;
	}

	@Override
	public String getFilterModelJsonData() {
		return filterModelJsonData;
	}

	@Override
	public void setFilterModelJsonData(String filterModelJsonData) {
		this.filterModelJsonData = filterModelJsonData;
	}

	@Override
	public Integer getColumnsHashCode() {
		return columnsHashCode;
	}

	@Override
	public void setColumnsHashCode(Integer columnsHashCode) {
		this.columnsHashCode = columnsHashCode;
	}
	
	@Override
	public String toString() {
		return "DLListboxProfile [id=" + id + ", dlListboxId=" + dlListboxId + ", name=" + name + ", publicProfile="
				+ publicProfile + ", defaultProfile=" + defaultProfile + ", hidden=" + hidden + ", user=" + user
				+ ", columnModelJsonData=" + columnModelJsonData + ", filterModelJsonData=" + filterModelJsonData
				+ ", columnsHashCode=" + columnsHashCode + "]";
	}	
	
	public class NameComparator implements Comparator<DLListboxProfile> {
		@Override
		public int compare(DLListboxProfile p1, DLListboxProfile p2) {
			if (p1.getName() != null && p2.getName() != null) {
				return p1.getName().compareTo(p2.getName());
			} else {
				return 0;
			}
		}
	}
}

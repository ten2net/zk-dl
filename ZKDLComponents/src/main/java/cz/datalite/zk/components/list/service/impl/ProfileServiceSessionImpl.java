package cz.datalite.zk.components.list.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;

import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.components.list.service.ProfileService;

public class ProfileServiceSessionImpl implements ProfileService {
	
	public static final String PROFILE_SESS_ATTR = "__dlListbox__profile__";
	
	@Override
	public List<DLListboxProfile> findAll() {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public List<DLListboxProfile> findAll(String dlListboxId) {
		DLListboxProfile profile = (DLListboxProfile) Sessions.getCurrent().getAttribute(PROFILE_SESS_ATTR + dlListboxId);
		
		ArrayList<DLListboxProfile> profiles = new ArrayList<DLListboxProfile>(1);
		
		if (profile != null) {
			profiles.add(profile);
		}
		
		return profiles;
	}

	@Override
	public DLListboxProfile findById(Long id) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}

	@Override
	public DLListboxProfile save(DLListboxProfile dlListboxProfile) {
		Sessions.getCurrent().setAttribute(PROFILE_SESS_ATTR + dlListboxProfile.getDlListboxId(), dlListboxProfile);
		return dlListboxProfile;
	}

	@Override
	public void delete(DLListboxProfile dlListboxProfile) {
		throw new IllegalAccessError("Method not valid for session implementation.");		
	}

	@Override
	public DLListboxProfile getByDefault(String dlListboxId) {
		throw new IllegalAccessError("Method not valid for session implementation.");
	}	
}

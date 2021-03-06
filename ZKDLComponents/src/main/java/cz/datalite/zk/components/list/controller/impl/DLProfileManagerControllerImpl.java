package cz.datalite.zk.components.list.controller.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

import cz.datalite.helpers.StringHelper;
import cz.datalite.helpers.ZKDLResourceResolver;
import cz.datalite.zk.components.list.DLListboxFilterController;
import cz.datalite.zk.components.list.DLListboxGeneralController;
import cz.datalite.zk.components.list.DLListboxProfile;
import cz.datalite.zk.components.list.DLListboxProfileImpl;
import cz.datalite.zk.components.list.controller.DLListboxExtController;
import cz.datalite.zk.components.list.controller.DLProfileManagerController;
import cz.datalite.zk.components.list.service.ProfileService;
import cz.datalite.zk.components.list.service.ProfileServiceFactory;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.components.profile.DLProfileManager;

/**
 * Implementation of the controller for the Listbox profile manager which
 * provides extended tools.
 */
public class DLProfileManagerControllerImpl<T> implements DLProfileManagerController<T> {

	// master controller
	private final DLListboxExtController<T> masterController;

	// view
	private final DLProfileManager<T> dlProfileManagerComponent;

	// lovbox controller
	private final DLLovboxGeneralController<DLListboxProfile> profilesCtl;

	/** profile service used to load/store profiles from/to persistent storage */
	private final ProfileService profileService;

	@SuppressWarnings("unchecked")
	public DLProfileManagerControllerImpl(final DLListboxExtController<T> masterController, final DLProfileManager<T> dlProfileComponent, ProfileService service) {
		this.masterController = masterController;
		this.dlProfileManagerComponent = dlProfileComponent;
		this.dlProfileManagerComponent.setController(this);

		if (service != null) {
			this.profileService = service;
		} else {        
			String profileServiceFactoryClass = Library.getProperty("zk-dl.listbox.profile.provider");
			if (!StringHelper.isNull(profileServiceFactoryClass)) {
				try {
					Object profileServiceFactory = Class.forName(profileServiceFactoryClass).newInstance();

					if (ProfileServiceFactory.class.isAssignableFrom(profileServiceFactory.getClass())) {
						this.profileService = ((ProfileServiceFactory) profileServiceFactory).create(Sessions.getCurrent().getNativeSession());
					} else {
						throw new IllegalArgumentException();
					}
				} catch (IllegalArgumentException e) {
					throw new IllegalStateException("ProfileServiceFactory class is not assignable from '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
				} catch (InstantiationException e) {
					throw new IllegalStateException("Unable to instantiate class '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("Unable to instantiate class '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("Class not found: '" + profileServiceFactoryClass + "'. Check zk.xml parameter zk-dl.listbox.profile.provider.", e);
				}
			} else {
				throw new IllegalStateException("Unable to instantiate ProfileServiceFactory class. File zk.xml must contain parameter zk-dl.listbox.profile.provider to use DLProfileManager.");
			}
		}

		this.profilesCtl = new DLLovboxGeneralController<DLListboxProfile>(
				new DLListboxFilterController<DLListboxProfile>(this.getClass().getName() + "ProfilesLovbox") {
					@Override
					protected List<DLListboxProfile> loadData() {
						List<DLListboxProfile> profiles = (List<DLListboxProfile>) profileService.findAll(masterController.getSessionName()); 
						Collections.sort(profiles, new DLListboxProfileImpl().new NameComparator());;
						return profiles;
					}
				});

		this.dlProfileManagerComponent.setProfilesLovboxController(this.profilesCtl);
	}

	@Override
	public boolean selectDefaultProfile(boolean onCreate) {
		if (onCreate && !this.dlProfileManagerComponent.isApplyDefaultProfile()) {
			return false;
		}

		DLListboxProfile defaultProfile = this.profileService.getByDefault(masterController.getSessionName());

		if (defaultProfile != null) {
			this.profilesCtl.getLovBox().setSelectedItem(defaultProfile);
			return true;
		}

		return false;
	}

	@Override
	public void onLoadProfile() {
		DLListboxProfile selectedProfile = this.profilesCtl.getSelectedItem();

		if (selectedProfile != null) {			
			((DLListboxGeneralController<T>) this.masterController).applyProfile(selectedProfile);
		}
	}

	@Override
	public void onEditProfile(Long idProfile) {
		final DLListboxProfile editProfile;
		
		if (idProfile == null) {
			editProfile = new DLListboxProfileImpl();
		} else {
			editProfile = this.profileService.findById(idProfile);
		}
		
		final Map<String, Object> args = new HashMap<String, Object>();
		args.put("profile", editProfile);

		final Window win = (org.zkoss.zul.Window) ZKDLResourceResolver.resolveAndCreateComponents("listboxProfileEditWindow.zul", null, args);
		
		final EventListener<Event> saveListener = new EventListener<Event>() {
			public void onEvent(final Event event) {
				onEditProfileOk(editProfile, Boolean.valueOf(event.getData().toString()));
			}
		};        
		win.addEventListener("onSave", saveListener);
		
		final EventListener<Event> deleteListener = new EventListener<Event>() {
			public void onEvent(final Event event) {
				onDeleteProfile(editProfile.getId());
			}
		};        
		win.addEventListener("onDelete", deleteListener);
        
		win.doHighlighted();
	}	
	
	@Override
	public void onEditProfileOk(DLListboxProfile profile, boolean saveAgendaSettings) {
		if (saveAgendaSettings || profile.getId() == null) {
			DLListboxProfile actualAgendaSettingProfile = ((DLListboxGeneralController<T>) this.masterController).createProfile();
			profile.setColumnModelJsonData(actualAgendaSettingProfile.getColumnModelJsonData());
			profile.setFilterModelJsonData(actualAgendaSettingProfile.getFilterModelJsonData());
			profile.setColumnsHashCode(actualAgendaSettingProfile.getColumnsHashCode());
			profile.setDlListboxId(actualAgendaSettingProfile.getDlListboxId());
		}
		
		this.profileService.save(profile);
		
		this.profilesCtl.getListboxController().refreshDataModel();
		
		if (saveAgendaSettings) {
			this.profilesCtl.setSelectedItem(profile);
		}
	}

	@Override
	public void onDeleteProfile(Long idProfile) {				
		this.profileService.delete(new DLListboxProfileImpl(idProfile));
		this.profilesCtl.getListboxExtController().setSelected(null);
		this.profilesCtl.getListboxController().refreshDataModel();
	}

	@Override
	public void fireChanges() {		
		this.profilesCtl.getListboxExtController().setSelected(null);
	}
	
}

package iot.mike.malayans.rmimanager.register;

import java.util.Set;

public class ModuleStatus {
	private Set<ModuleStatusEntry>	moduleStatusEntries		= null;

	public Set<ModuleStatusEntry> getModuleStatusEntries() {
		return moduleStatusEntries;
	}

	public void setModuleStatusEntries(Set<ModuleStatusEntry> moduleStatusEntries) {
		this.moduleStatusEntries = moduleStatusEntries;
	}
}

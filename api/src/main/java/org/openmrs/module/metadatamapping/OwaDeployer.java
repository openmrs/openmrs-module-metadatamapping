package org.openmrs.module.metadatamapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ModuleUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class OwaDeployer {
	
	public static final String OWA_DIR_KEY = "owa.appFolderPath";
	
	public static final String METADATAMAPPING = "metadatamapping";
	
	public void deployOwa() {
		Module owa = ModuleFactory.getStartedModuleById("owa");
		boolean owaModuleStarted = owa != null;
		
		if (owaModuleStarted && checkIfOwaNotDeployed()) {
			URL owaZip = getOwaZip();
			File owaDestination = new File(getOwaPath(), METADATAMAPPING + ".zip");
			try {
				FileUtils.copyURLToFile(owaZip, owaDestination);
			}
			catch (IOException e) {
				throw new RuntimeException("Failed to copy metadatamapping-owa to OWA module base directory", e);
			}
		}
	}
	
	public boolean checkIfOwaNotDeployed() {
		boolean owaNotDeployed = true;
		File manifest = new File(getOwaPath(), METADATAMAPPING + File.separator + "manifest.webapp");
		if (manifest.exists()) {
			//read version value from manifest.webapp
			ObjectMapper mapper = new ObjectMapper();
			FileInputStream manifestStream = null;
			try {
				manifestStream = new FileInputStream(manifest);
				ModelMap manifestModel = mapper.readValue(manifestStream, ModelMap.class);
				
				String currentVersion = (String) manifestModel.get("version");
				String newVersion = getModuleVersion();
				int compare = ModuleUtil.compareVersion(currentVersion, newVersion);
				//if versions are matching and snapshot, deploy owa, useful for development
				if (compare == 0 && notSnapshot(currentVersion) && notSnapshot(newVersion)) {
					owaNotDeployed = false;
				}
			}
			catch (IOException e) {
				//file not found/corrupted means that installed OWA is corrupted, need to deploy
				return true;
			}
		}
		return owaNotDeployed;
	}
	
	private boolean notSnapshot(String version) {
		return !version.contains("SNAPSHOT");
	}
	
	private String getOwaPath() {
		String owaPath = Context.getAdministrationService().getGlobalProperty(OWA_DIR_KEY);
		//if global property is not set, at first run OWA module will use default owa directory
		if (owaPath == null) {
			owaPath = OpenmrsUtil.getApplicationDataDirectory() + "owa";
		}
		return owaPath;
	}
	
	private URL getOwaZip() {
		String version = getModuleVersion();
		String filename = METADATAMAPPING + "-owa-" + version + ".zip";
		return getClass().getClassLoader().getResource("lib" + File.separator + filename);
	}
	
	private String getModuleVersion() {
		return ModuleFactory.getModuleById(METADATAMAPPING).getVersion();
	}
}

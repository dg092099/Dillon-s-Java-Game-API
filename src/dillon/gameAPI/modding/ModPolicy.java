package dillon.gameAPI.modding;

import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.logging.Logger;

/**
 * A security policy for mods.
 * 
 * @author Dillon - Github https://dg092099.github.io/
 *
 */
public class ModPolicy extends Policy {
	public PermissionCollection getPermissions(ProtectionDomain d) {
		if (d.getClassLoader() instanceof SandboxedLoader) {
			SandboxedLoader s = (SandboxedLoader) d.getClassLoader();
			String modname = ModdingCore.getNameFromLoader(s);
			Logger.getLogger("ModSecurity").severe("Mod: \"" + modname + "\" attempted a security check and failed.");
			return new Permissions();
		} else {
			Permissions p = new Permissions();
			p.add(new AllPermission());
			return p;
		}
	}
}

package de.brettspielwelt.shared.tools;

public class JavaVersionChecker {

	public enum OSType {
		WINDOWS,
		LINUX,
		MAC_OS,
		OTHER
	}

	public static class JavaVersion {
		private final int[] version;
		private final String versionUpdateSub;

		private final String osName;

		private final String osVersion;

		private final String osArch;

		private final OSType osType;

		public JavaVersion(int[] versionArray, String versionSub, String osName, String osVersion, String osArch,
		                   OSType osType) {
			this.version = versionArray;
			this.versionUpdateSub = versionSub;
			this.osName = osName;
			this.osVersion = osVersion;
			this.osArch = osArch;
			this.osType = osType;
		}


		/**
		 * Überprüft, ob die verwendete Javaversion größer oder gleich der angegebenen ist.
		 *
		 * @param main
		 *            Hauptversion (<b>1</b>.4.2 bzw. <b>1</b>.6.0_02)
		 * @param sub
		 *            Unterversion (1.<b>4</b>.2 bzw. 1.<b>6</b>.0_02)
		 * @param update
		 *            Update Hauptversion (1.4.<b>2</b> bzw. 1.6.<b>0</b>_02)
		 *
		 * @return true, wenn die verwendete Version >= der angegebenen ist, ansonsten false.
		 */
		public boolean isPossible(int main, int sub, int update, String updateSub) {

			if (version[0] != main) {
				return (version[0] > main);
			}

			if (version[1] != sub) {
				return (version[1] > sub);
			}

			if (version[2] != update) {
				return (version[2] > update);
			}

			return (versionUpdateSub.compareToIgnoreCase((updateSub != null) ? updateSub : "") >= 0);
		}

		public boolean isPossible(int main, int sub, int update)
		{
			return isPossible(main, sub, update, "");
		}

		public boolean isPossible(int main, int sub)
		{
			return isPossible(main, sub, 0);
		}

		public int getMajor () {
			return version[0];
		}

		public int getMinor() {
			return version[1];
		}

		public int getUpdate() {
			return version[2];
		}

		public String getUpdateSub() {
			return versionUpdateSub;
		}

		public String getOsName() {
			return osName;
		}

		public String getOsVersion() {
			return osVersion;
		}

		public String getOsArch() {
			return osArch;
		}

		public OSType getOsType() {
			return osType;
		}

		public boolean isMac() {
			return getOsType() == OSType.MAC_OS;
		}

	}

	private static JavaVersion localVersion;

	public static JavaVersion jvmVersion() {
		if (localVersion != null) {
			// Java-Version bereits ermittelt!
			return localVersion;
		}
		localVersion = parseVersion(
				System.getProperty("java.version", ""),
				System.getProperty("os.name", ""),
				System.getProperty("os.version", ""),
				System.getProperty("os.arch", "")
		);
		System.out.println("Using java version { "+localVersion.getMajor()+", "+
				localVersion.getMinor() +", "+localVersion.getUpdate()+", \""+localVersion.getUpdateSub()+"\" } on " +
				localVersion.getOsName() + " | " + localVersion.getOsVersion() + " | " + localVersion.getOsArch() +
				" (classified as " + localVersion.getOsType()+")");
		return localVersion;
	}

	public static JavaVersion parseVersion(String versionString, String osNameString, String osVersionString,
	                                        String osArchString) {

		String[] sVersion = versionString.split("\\.");
		int[] version = new int[3];
		String versionUpdateSub;

		int pos = -1;

		if (sVersion.length == 0) {
			sVersion = new String[] { "1", "1" }; // zur Sicherheit 1.1 als default.
		}

		if (sVersion.length > 2) {
			pos = sVersion[2].indexOf('_');

			// Alternativer Trennstrich...
			int pos2 = sVersion[2].indexOf('-');
			if ((pos2 > -1) && (pos2 < pos)) {
				pos = pos2;
			}
		}

		if (pos > -1) {
			versionUpdateSub = sVersion[2].substring(pos + 1);
			sVersion[2] = sVersion[2].substring(0, pos);
		} else {
			versionUpdateSub = "";
		}

		for (int i = 0; i < 3; i++) {
			if (i < sVersion.length) {
				try {
					version[i] = Integer.parseInt(sVersion[i]);
				}
				catch (NumberFormatException ignored) {
				}
			}
		}

		OSType osType = OSType.OTHER;
		if (osNameString.startsWith("Windows")) {
			osType = OSType.WINDOWS;
		} else if (osNameString.startsWith("Linux")) {
			osType = OSType.LINUX;
		} else if (osNameString.startsWith("Mac")) {
			osType = OSType.MAC_OS;
		}
		return new JavaVersion(version, versionUpdateSub, osNameString, osVersionString, osArchString,
				osType);
	}
}

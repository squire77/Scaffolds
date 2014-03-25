package scaffold.console;


public class LicenseValidator {
    private static String HARDCODED_KEY = "D968E3D0-38D6-4C5C-AB7A-71BB78D0396B";
    
    public static boolean isValidLicenseKey(String licenseKey) {
        if (licenseKey != null && licenseKey.equals(HARDCODED_KEY)) {
            return true;
        }
        
        return false;
    }
}

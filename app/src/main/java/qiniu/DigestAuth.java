package qiniu;

public class DigestAuth {

    private static final String SK = "frWQI8QPogc9sJSWLtjiYU5m1ER7ogy5-3nZWsox";
    private static final String AK = "gTh9TiM54BbogCWEWKsdBy9Ub2A0K1lTpSoLRl_Y";

	public static String sign(Mac mac, byte[] data) throws AuthException {
		if (mac == null) {
			mac = new Mac(AK , SK );
		}
		return mac.sign(data);
	}
	
	
	public static String signWithData(Mac mac, byte[] data) throws AuthException {
		if (mac == null) {
			mac = new Mac(AK, SK);
		}
		return mac.signWithData(data);
	}
	
}

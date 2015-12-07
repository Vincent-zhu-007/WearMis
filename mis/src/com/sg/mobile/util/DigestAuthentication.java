package com.sg.mobile.util;

public class DigestAuthentication {
	/** Costructs a new DigestAuthentication. */
	/*public DigestAuthentication(String method, String uri,
			WwwAuthenticateHeader ah, String qop, String body, String username,
			String passwd) {
		init(method, ah, body, passwd);
		this.uri = uri;
		this.qop = qop;
		if (qop != null && cnonce == null) {
			this.cnonce = Random.nextHexString(16);
			
			 * A new cnonce is generated for every new instance so 
			 * no need to keep track of the nc value.
			 
			this.nc = "00000001";
		}
		this.username = username;
	}*/

	/**
	 * Calculates the digest-response.
	 * <p>
	 * If the "qop" value is "auth" or "auth-int": <br>
	 * KD ( H(A1), unq(nonce) ":" nc ":" unq(cnonce) ":" unq(qop) ":" H(A2) )
	 * 
	 * <p>
	 * If the "qop" directive is not present: <br>
	 * KD ( H(A1), unq(nonce) ":" H(A2) )
	 */
	public static String getSafeCode(String username, String realm, String passwd, String algorithm, String nonce, String cnonce, String method, String uri, String qop, String body, String nc) {
		
		if (qop != null && cnonce == null) {
			cnonce = Random.nextHexString(16);
			nc = "00000001";
		}
		
		//A1 username, realm, passwd, algorithm, nonce, cnonce
		//A2 method, uri, qop, body
		
		String secret = HEX(MD5(A1(username, realm, passwd, algorithm, nonce, cnonce)));
		StringBuffer sb = new StringBuffer();
		if (nonce != null)
			sb.append(nonce);
		sb.append(":");
		if (qop != null) {
			if (nc != null)
				sb.append(nc);
			sb.append(":");
			if (cnonce != null)
				sb.append(cnonce);
			sb.append(":");
			sb.append(qop);
			sb.append(":");
		}
		sb.append(HEX(MD5(A2(method, uri, qop, body))));
		String data = sb.toString();
		return HEX(KD(secret, data));
	}

	/**
	 * Calculates KD() value.
	 * <p>
	 * KD(secret, data) = H(concat(secret, ":", data))
	 */
	private static byte[] KD(String secret, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(secret).append(":").append(data);
		return MD5(sb.toString());
	}

	/**
	 * Calculates A1 value.
	 * <p>
	 * If the "algorithm" directive's value is "MD5" or is unspecified: <br>
	 * A1 = unq(username) ":" unq(realm) ":" passwd
	 * 
	 * <p>
	 * If the "algorithm" directive's value is "MD5-sess": <br>
	 * A1 = H( unq(username) ":" unq(realm) ":" passwd ) ":" unq(nonce) ":"
	 * unq(cnonce)
	 */
	private static byte[] A1(String username, String realm, String passwd, String algorithm, String nonce, String cnonce) {
		StringBuffer sb = new StringBuffer();
		if (username != null)
			sb.append(username);
		sb.append(":");
		if (realm != null)
			sb.append(realm);
		sb.append(":");
		if (passwd != null)
			sb.append(passwd);

		if (algorithm == null || !algorithm.equalsIgnoreCase("MD5-sess")) {
			return sb.toString().getBytes();
		} else {
			StringBuffer sb2 = new StringBuffer();
			sb2.append(":");
			if (nonce != null)
				sb2.append(nonce);
			sb2.append(":");
			if (cnonce != null)
				sb2.append(cnonce);
			return cat(MD5(sb.toString()), sb2.toString().getBytes());
		}
	}

	/**
	 * Calculates A2 value.
	 * <p>
	 * If the "qop" directive's value is "auth" or is unspecified: <br>
	 * A2 = Method ":" digest-uri
	 * 
	 * <p>
	 * If the "qop" value is "auth-int": <br>
	 * A2 = Method ":" digest-uri ":" H(entity-body)
	 */
	private static String A2(String method, String uri, String qop, String body) {
		StringBuffer sb = new StringBuffer();
		sb.append(method);
		sb.append(":");
		if (uri != null)
			sb.append(uri);

		if (qop != null && qop.equalsIgnoreCase("auth-int")) {
			sb.append(":");
			if (body == null)
				sb.append(HEX(MD5("")));
			else
				sb.append(HEX(MD5(body)));
		}
		return sb.toString();
	}

	/** Concatenates two arrays of bytes. */
	private static byte[] cat(byte[] a, byte[] b) {
		int len = a.length + b.length;
		byte[] c = new byte[len];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i];
		for (int i = 0; i < b.length; i++)
			c[i + a.length] = b[i];
		return c;
	}

	/** Calculates the MD5 of a String. */
	private static byte[] MD5(String str) {
		return MD5.digest(str);
	}

	/** Calculates the MD5 of an array of bytes. */
	private static byte[] MD5(byte[] bb) {
		return MD5.digest(bb);
	}

	/** Calculates the HEX of an array of bytes. */
	private static String HEX(byte[] bb) {
		return MD5.asHex(bb);
	}
}

package qiniu;

import org.json.JSONException;
import org.json.JSONStringer;

/**
 * The PutPolicy class used to generate a upload token. To upload a file, you
 * should obtain upload authorization from Qiniu cloud strage platform. By a
 * pair of valid accesskey and secretkey, we generate a upload token. When
 * upload a file, the upload token is transmissed as a part of the file stream,
 * or as an accessory part of the HTTP Headers.
 */

public class PutPolicy {
	/** 必选。可以是 bucketName 或者 bucketName:key */
	public String scope;

	public long expires;
    //授权过期时间
	public long deadline;
    //启动callbackFetchKeym模式(1)
    public int callbackFetchKey;

	public PutPolicy(String scope,long deadline,int callbackFetchKey) {
		this.scope = scope;
        this.deadline = deadline;
        this.callbackFetchKey = callbackFetchKey;
	}

	private String marshal() throws JSONException {
		JSONStringer stringer = new JSONStringer();
		stringer.object();
		stringer.key("scope").value(this.scope);
        stringer.key("deadline").value(this.deadline);
        stringer.key("callbackFetchKey").value(this.callbackFetchKey);
		stringer.endObject();

		return stringer.toString();
	}


	/**
	 * makes an upload token.
	 * @param mac
	 * @return
	 * @throws org.json.JSONException
	 */

    //生成token
	public String token(Mac mac) throws AuthException, JSONException {
		if (this.expires == 0) {
			this.expires = 3600; // 3600s, default.
		}
		this.deadline = System.currentTimeMillis() / 1000 + this.expires;
		byte[] data = EncodeUtils.toByte(this.marshal());
		return DigestAuth.signWithData(mac, data);
	}

}

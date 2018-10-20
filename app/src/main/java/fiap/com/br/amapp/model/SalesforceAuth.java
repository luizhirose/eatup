package fiap.com.br.amapp.model;

public class SalesforceAuth {

    private volatile static SalesforceAuth _salesforceAuth;

    private String accessToken;
    private String instanceUrl;
    private String id;
    private String tokenType;
    private String issuedAt;
    private String signature;

    public static SalesforceAuth getInstance() {
        if (_salesforceAuth == null) {
            synchronized (SalesforceAuth.class) {
                if (_salesforceAuth == null) _salesforceAuth = new SalesforceAuth();
            }
        }
        return _salesforceAuth;
    }

    public SalesforceAuth() {
    }

    public SalesforceAuth(String accessToken, String instanceUrl, String id, String tokenType, String issuedAt, String signature) {
        this.accessToken = accessToken;
        this.instanceUrl = instanceUrl;
        this.id = id;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.signature = signature;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

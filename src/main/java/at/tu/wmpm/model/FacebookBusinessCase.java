package at.tu.wmpm.model;

public class FacebookBusinessCase extends BusinessCase {

    private String facebookUserId;
    private String facebookPostId;

    public FacebookBusinessCase(String facebookUserId, String facebookPostId) {
        super();
        this.facebookUserId = facebookUserId;
        this.facebookPostId = facebookPostId;
    }

    public FacebookBusinessCase() {
        super();
    }

    public String getFacebookPostId() {
        return facebookPostId;
    }

    public void setFacebookPostId(String facebookPostId) {
        this.facebookPostId = facebookPostId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }
}
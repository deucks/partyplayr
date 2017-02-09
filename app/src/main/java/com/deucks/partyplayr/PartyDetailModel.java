package com.deucks.partyplayr;

/**
 * Class for holding the Model for PartyDetail
 */
public class PartyDetailModel {

    //Declare variables
    private String mPartyName;
    private String mPartyId;
    private String mPartyUrl;
    private String mPartyHostId;

    /**
     * Get The party name
     * @return
     */
    public String getmPartyName() {
        return mPartyName;
    }

    /**
     * Set the party name
     * @param mPartyName
     */
    public void setmPartyName(String mPartyName) {
        this.mPartyName = mPartyName;
    }

    /**
     * Get the party id
     * @return
     */
    public String getmPartyId() {
        return mPartyId;
    }

    /**
     * Set the party id
     * @param mPartyId
     */
    public void setmPartyId(String mPartyId) {
        this.mPartyId = mPartyId;
    }

    /**
     * Get the party url
     * @return
     */
    public String getmPartyUrl() {
        return mPartyUrl;
    }

    /**
     * Set the party url
     * @param mPartyUrl
     */
    public void setmPartyUrl(String mPartyUrl) {
        this.mPartyUrl = mPartyUrl;
    }

    /**
     * Get the party host id
     * @return
     */
    public String getmPartyHostId() {
        return mPartyHostId;
    }

    /**
     * Set the party host id
     * @param mPartyHostId
     */
    public void setmPartyHostId(String mPartyHostId) {
        this.mPartyHostId = mPartyHostId;
    }
}

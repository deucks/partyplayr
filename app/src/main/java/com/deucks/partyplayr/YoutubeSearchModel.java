package com.deucks.partyplayr;

/**
 * Model for containing the Youtube Search Results
 */
public class YoutubeSearchModel {

    //Decalre variables
    private String mVideoName;
    private String mVideoUrl;
    private String mVideoThumnbnailUrl;

    /**
     * Constructor for Youtube Search model
     * @param videoName
     * @param videoUrl
     * @param thumbnailUrl
     */
    public YoutubeSearchModel(String videoName, String videoUrl, String thumbnailUrl)
    {
        mVideoName = videoName;
        mVideoUrl = videoUrl;
        mVideoThumnbnailUrl = thumbnailUrl;
    }

    /**
     * Get the video name
     * @return
     */
    public String getmVideoName() {
        return mVideoName;
    }

    /**
     * Method for setting the video name
     * @param mVideoName
     */
    public void setmVideoName(String mVideoName) {
        this.mVideoName = mVideoName;
    }

    /**
     * Get the Video url
     * @return
     */
    public String getmVideoUrl() {
        return mVideoUrl;
    }

    /**
     * Set the video url
     * @param mVideoUrl
     */
    public void setmVideoUrl(String mVideoUrl) {
        this.mVideoUrl = mVideoUrl;
    }

    /**
     * Get the video thumbnail url
     * @return
     */
    public String getmVideoThumnbnailUrl() {
        return mVideoThumnbnailUrl;
    }

    /**
     * Set the video thumbnail url
     * @param mVideoThumnbnailUrl
     */
    public void setmVideoThumnbnailUrl(String mVideoThumnbnailUrl) {
        this.mVideoThumnbnailUrl = mVideoThumnbnailUrl;
    }
}

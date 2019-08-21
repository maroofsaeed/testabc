package maroof.oldhindisongs.utils;

/**
 * Created by Maroof on 6/19/2017.
 */

public class YoutubeResults {
    private String video;
    private String title;
    private String thumbnail;

    public YoutubeResults(String video, String title, String thumbnail) {
        this.video = video;
        this.title = title;
        this.thumbnail = thumbnail;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}

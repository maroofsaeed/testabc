package maroof.oldhindisongs;

import java.io.Serializable;

/**
 * Created by Maroof on 6/18/2017.
 */

public class Categories implements Serializable {
    private int id;
    private int packageId;
    private String title;
    private String keyword;
    private boolean isPlaylist;
    private String PlaylistCode;
    private boolean isRedirection;
    private String RedirectionApp;
    private int SortOrder;
    private String image;
    private boolean isLimit;
    private int LimitCount;
    private String description;
    private boolean status;
    private String date;
    private int PackageAddlimit;
    private boolean PackageIsAddActive;
    private int MainCatID;
    private String MainCatName;
    private String MainCatDescription;
    private String MainCatImage;
    private String type;
    private boolean PackageIsAddmob;
    private boolean PackageIsStartapp;


    public Categories(int id, int packageId, String title, String keyword, boolean isPlaylist, String playlistCode, boolean isRedirection, String redirectionApp, int sortOrder, String image, boolean isLimit, int limitCount, String description, boolean status, String date, int packageAddlimit, boolean packageIsAddActive, int mainCatID, String mainCatName, String mainCatDescription, String mainCatImage, String type, boolean isadmob, boolean isstartapp) {
        this.id = id;
        this.packageId = packageId;
        this.title = title;
        this.keyword = keyword;
        this.isPlaylist = isPlaylist;
        PlaylistCode = playlistCode;
        this.isRedirection = isRedirection;
        RedirectionApp = redirectionApp;
        SortOrder = sortOrder;
        this.image = image;
        this.isLimit = isLimit;
        LimitCount = limitCount;
        this.description = description;
        this.status = status;
        this.date = date;
        this.PackageAddlimit = packageAddlimit;
        this.PackageIsAddActive = packageIsAddActive;
        MainCatID = mainCatID;
        MainCatName = mainCatName;
        MainCatDescription = mainCatDescription;
        MainCatImage = mainCatImage;
        this.type = type;
        this.PackageIsAddmob = isadmob;
        this.PackageIsStartapp = isstartapp;
    }

    public boolean isPackageIsAddmob() {
        return PackageIsAddmob;
    }

    public void setPackageIsAddmob(boolean packageIsAddmob) {
        PackageIsAddmob = packageIsAddmob;
    }

    public boolean isPackageIsStartapp() {
        return PackageIsStartapp;
    }

    public void setPackageIsStartapp(boolean packageIsStartapp) {
        PackageIsStartapp = packageIsStartapp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMainCatID() {
        return MainCatID;
    }

    public void setMainCatID(int mainCatID) {
        MainCatID = mainCatID;
    }

    public String getMainCatName() {
        return MainCatName;
    }

    public void setMainCatName(String mainCatName) {
        MainCatName = mainCatName;
    }

    public String getMainCatDescription() {
        return MainCatDescription;
    }

    public void setMainCatDescription(String mainCatDescription) {
        MainCatDescription = mainCatDescription;
    }

    public String getMainCatImage() {
        return MainCatImage;
    }

    public void setMainCatImage(String mainCatImage) {
        MainCatImage = mainCatImage;
    }

    public boolean isPackageIsAddActive() {
        return PackageIsAddActive;
    }

    public void setPackageIsAddActive(boolean packageIsAddActive) {
        PackageIsAddActive = packageIsAddActive;
    }

    public int getId() {
        return id;
    }

    public int getPackageAddlimit() {
        return PackageAddlimit;
    }

    public void setPackageAddlimit(int packageAddlimit) {
        PackageAddlimit = packageAddlimit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isPlaylist() {
        return isPlaylist;
    }

    public void setPlaylist(boolean playlist) {
        isPlaylist = playlist;
    }

    public String getPlaylistCode() {
        return PlaylistCode;
    }

    public void setPlaylistCode(String playlistCode) {
        PlaylistCode = playlistCode;
    }

    public boolean isRedirection() {
        return isRedirection;
    }

    public void setRedirection(boolean redirection) {
        isRedirection = redirection;
    }

    public String getRedirectionApp() {
        return RedirectionApp;
    }

    public void setRedirectionApp(String redirectionApp) {
        RedirectionApp = redirectionApp;
    }

    public int getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(int sortOrder) {
        SortOrder = sortOrder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isLimit() {
        return isLimit;
    }

    public void setLimit(boolean limit) {
        isLimit = limit;
    }

    public int getLimitCount() {
        return LimitCount;
    }

    public void setLimitCount(int limitCount) {
        LimitCount = limitCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

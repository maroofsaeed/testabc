package maroof.oldhindisongs;

/**
 * Created by Maroof on 7/25/2017.
 */

public class News {
    private int id;
    private String title;
    private String description;
    private String image;
    private int sortorder;
    private String date;
    private int AdminId;

    public News(int id, String title, String description, String image, int sortorder, String date, int adminId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.sortorder = sortorder;
        this.date = date;
        AdminId = adminId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSortorder() {
        return sortorder;
    }

    public void setSortorder(int sortorder) {
        this.sortorder = sortorder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAdminId() {
        return AdminId;
    }

    public void setAdminId(int adminId) {
        AdminId = adminId;
    }
}

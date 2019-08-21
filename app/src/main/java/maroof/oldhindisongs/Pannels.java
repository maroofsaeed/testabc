package maroof.oldhindisongs;

/**
 * Created by Maroof on 7/23/2017.
 */

public class Pannels {
    private int id;
    private int mid;
    private int pid;
    private int sortorder;
    private boolean status;
    private String pannelname;
    private String panneldescription;

    public Pannels(int id, int mid, int pid, int sortorder, boolean status, String pannelname, String panneldescription) {
        this.id = id;
        this.mid = mid;
        this.pid = pid;
        this.sortorder = sortorder;
        this.status = status;
        this.pannelname = pannelname;
        this.panneldescription = panneldescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getSortorder() {
        return sortorder;
    }

    public void setSortorder(int sortorder) {
        this.sortorder = sortorder;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPannelname() {
        return pannelname;
    }

    public void setPannelname(String pannelname) {
        this.pannelname = pannelname;
    }

    public String getPanneldescription() {
        return panneldescription;
    }

    public void setPanneldescription(String panneldescription) {
        this.panneldescription = panneldescription;
    }
}

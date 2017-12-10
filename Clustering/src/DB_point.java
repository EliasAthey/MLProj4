import java.util.ArrayList;

/**
 * Datapoints in db-scan
 */
public class DB_point{
    private final Double[] value;
    private final int dbLabel;
    private int cluster;

    public DB_point(Double[] value, int dbLabel){
        this.value = value;
        this.dbLabel = dbLabel;
        this.cluster = -1;
    }

    public Double[] getValue() {
        return value;
    }

    public int getCluster() {
        return cluster;
    }

    public int getDbLabel() {
        return dbLabel;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}

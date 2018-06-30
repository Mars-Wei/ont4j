package indi.ylins.ont4j.util;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public enum PathEnum {

    ONT_PATH("C:/anti_terrorism_zh.owl"),
    DB_DIR("C:/Users/Yue/.Neo4jDesktop/neo4jDatabases/database-1af19124-e4c6-4d33-b73d-ae9ae4a89528/installation-3.4.0/data/databases/graph.db");

    private String path;

    PathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

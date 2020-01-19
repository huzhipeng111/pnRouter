package com.stratagile.pnrouter.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOCAL_FILE_MENU".
*/
public class LocalFileMenuDao extends AbstractDao<LocalFileMenu, Long> {

    public static final String TABLENAME = "LOCAL_FILE_MENU";

    /**
     * Properties of entity LocalFileMenu.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CreatTime = new Property(1, Long.class, "creatTime", false, "CREAT_TIME");
        public final static Property FileName = new Property(2, String.class, "fileName", false, "FILE_NAME");
        public final static Property Path = new Property(3, String.class, "path", false, "PATH");
        public final static Property FileNum = new Property(4, Long.class, "fileNum", false, "FILE_NUM");
        public final static Property Type = new Property(5, String.class, "type", false, "TYPE");
        public final static Property NodeId = new Property(6, Integer.class, "nodeId", false, "NODE_ID");
        public final static Property Size = new Property(7, Long.class, "size", false, "SIZE");
        public final static Property LastModify = new Property(8, Long.class, "LastModify", false, "LAST_MODIFY");
        public final static Property IsChoose = new Property(9, Boolean.class, "isChoose", false, "IS_CHOOSE");
    }


    public LocalFileMenuDao(DaoConfig config) {
        super(config);
    }
    
    public LocalFileMenuDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCAL_FILE_MENU\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CREAT_TIME\" INTEGER," + // 1: creatTime
                "\"FILE_NAME\" TEXT," + // 2: fileName
                "\"PATH\" TEXT," + // 3: path
                "\"FILE_NUM\" INTEGER," + // 4: fileNum
                "\"TYPE\" TEXT," + // 5: type
                "\"NODE_ID\" INTEGER," + // 6: nodeId
                "\"SIZE\" INTEGER," + // 7: size
                "\"LAST_MODIFY\" INTEGER," + // 8: LastModify
                "\"IS_CHOOSE\" INTEGER);"); // 9: isChoose
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCAL_FILE_MENU\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LocalFileMenu entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long creatTime = entity.getCreatTime();
        if (creatTime != null) {
            stmt.bindLong(2, creatTime);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(3, fileName);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        Long fileNum = entity.getFileNum();
        if (fileNum != null) {
            stmt.bindLong(5, fileNum);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        Integer nodeId = entity.getNodeId();
        if (nodeId != null) {
            stmt.bindLong(7, nodeId);
        }
 
        Long size = entity.getSize();
        if (size != null) {
            stmt.bindLong(8, size);
        }
 
        Long LastModify = entity.getLastModify();
        if (LastModify != null) {
            stmt.bindLong(9, LastModify);
        }
 
        Boolean isChoose = entity.getIsChoose();
        if (isChoose != null) {
            stmt.bindLong(10, isChoose ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LocalFileMenu entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long creatTime = entity.getCreatTime();
        if (creatTime != null) {
            stmt.bindLong(2, creatTime);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(3, fileName);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        Long fileNum = entity.getFileNum();
        if (fileNum != null) {
            stmt.bindLong(5, fileNum);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        Integer nodeId = entity.getNodeId();
        if (nodeId != null) {
            stmt.bindLong(7, nodeId);
        }
 
        Long size = entity.getSize();
        if (size != null) {
            stmt.bindLong(8, size);
        }
 
        Long LastModify = entity.getLastModify();
        if (LastModify != null) {
            stmt.bindLong(9, LastModify);
        }
 
        Boolean isChoose = entity.getIsChoose();
        if (isChoose != null) {
            stmt.bindLong(10, isChoose ? 1L: 0L);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LocalFileMenu readEntity(Cursor cursor, int offset) {
        LocalFileMenu entity = new LocalFileMenu( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // creatTime
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fileName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // path
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // fileNum
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // type
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // nodeId
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // size
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // LastModify
            cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0 // isChoose
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LocalFileMenu entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCreatTime(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setFileName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFileNum(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNodeId(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setSize(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setLastModify(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setIsChoose(cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LocalFileMenu entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LocalFileMenu entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LocalFileMenu entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

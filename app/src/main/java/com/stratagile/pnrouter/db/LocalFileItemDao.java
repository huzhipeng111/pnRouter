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
 * DAO for table "LOCAL_FILE_ITEM".
*/
public class LocalFileItemDao extends AbstractDao<LocalFileItem, Long> {

    public static final String TABLENAME = "LOCAL_FILE_ITEM";

    /**
     * Properties of entity LocalFileItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FileName = new Property(1, String.class, "fileName", false, "FILE_NAME");
        public final static Property FilePath = new Property(2, String.class, "filePath", false, "FILE_PATH");
        public final static Property FileLocalPath = new Property(3, String.class, "fileLocalPath", false, "FILE_LOCAL_PATH");
        public final static Property FileSize = new Property(4, Long.class, "fileSize", false, "FILE_SIZE");
        public final static Property CreatTime = new Property(5, Long.class, "creatTime", false, "CREAT_TIME");
        public final static Property FileType = new Property(6, Integer.class, "fileType", false, "FILE_TYPE");
        public final static Property FileFrom = new Property(7, Integer.class, "fileFrom", false, "FILE_FROM");
        public final static Property Autor = new Property(8, String.class, "autor", false, "AUTOR");
        public final static Property FileId = new Property(9, Long.class, "fileId", false, "FILE_ID");
        public final static Property SrcKey = new Property(10, String.class, "srcKey", false, "SRC_KEY");
        public final static Property FileMD5 = new Property(11, String.class, "fileMD5", false, "FILE_MD5");
        public final static Property FileInfo = new Property(12, String.class, "fileInfo", false, "FILE_INFO");
        public final static Property IsUpLoad = new Property(13, Boolean.class, "isUpLoad", false, "IS_UP_LOAD");
        public final static Property NodeId = new Property(14, Integer.class, "nodeId", false, "NODE_ID");
    }


    public LocalFileItemDao(DaoConfig config) {
        super(config);
    }
    
    public LocalFileItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCAL_FILE_ITEM\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"FILE_NAME\" TEXT," + // 1: fileName
                "\"FILE_PATH\" TEXT," + // 2: filePath
                "\"FILE_LOCAL_PATH\" TEXT," + // 3: fileLocalPath
                "\"FILE_SIZE\" INTEGER," + // 4: fileSize
                "\"CREAT_TIME\" INTEGER," + // 5: creatTime
                "\"FILE_TYPE\" INTEGER," + // 6: fileType
                "\"FILE_FROM\" INTEGER," + // 7: fileFrom
                "\"AUTOR\" TEXT," + // 8: autor
                "\"FILE_ID\" INTEGER," + // 9: fileId
                "\"SRC_KEY\" TEXT," + // 10: srcKey
                "\"FILE_MD5\" TEXT," + // 11: fileMD5
                "\"FILE_INFO\" TEXT," + // 12: fileInfo
                "\"IS_UP_LOAD\" INTEGER," + // 13: isUpLoad
                "\"NODE_ID\" INTEGER);"); // 14: nodeId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCAL_FILE_ITEM\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LocalFileItem entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
 
        String fileLocalPath = entity.getFileLocalPath();
        if (fileLocalPath != null) {
            stmt.bindString(4, fileLocalPath);
        }
 
        Long fileSize = entity.getFileSize();
        if (fileSize != null) {
            stmt.bindLong(5, fileSize);
        }
 
        Long creatTime = entity.getCreatTime();
        if (creatTime != null) {
            stmt.bindLong(6, creatTime);
        }
 
        Integer fileType = entity.getFileType();
        if (fileType != null) {
            stmt.bindLong(7, fileType);
        }
 
        Integer fileFrom = entity.getFileFrom();
        if (fileFrom != null) {
            stmt.bindLong(8, fileFrom);
        }
 
        String autor = entity.getAutor();
        if (autor != null) {
            stmt.bindString(9, autor);
        }
 
        Long fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(10, fileId);
        }
 
        String srcKey = entity.getSrcKey();
        if (srcKey != null) {
            stmt.bindString(11, srcKey);
        }
 
        String fileMD5 = entity.getFileMD5();
        if (fileMD5 != null) {
            stmt.bindString(12, fileMD5);
        }
 
        String fileInfo = entity.getFileInfo();
        if (fileInfo != null) {
            stmt.bindString(13, fileInfo);
        }
 
        Boolean isUpLoad = entity.getIsUpLoad();
        if (isUpLoad != null) {
            stmt.bindLong(14, isUpLoad ? 1L: 0L);
        }
 
        Integer nodeId = entity.getNodeId();
        if (nodeId != null) {
            stmt.bindLong(15, nodeId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LocalFileItem entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(2, fileName);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(3, filePath);
        }
 
        String fileLocalPath = entity.getFileLocalPath();
        if (fileLocalPath != null) {
            stmt.bindString(4, fileLocalPath);
        }
 
        Long fileSize = entity.getFileSize();
        if (fileSize != null) {
            stmt.bindLong(5, fileSize);
        }
 
        Long creatTime = entity.getCreatTime();
        if (creatTime != null) {
            stmt.bindLong(6, creatTime);
        }
 
        Integer fileType = entity.getFileType();
        if (fileType != null) {
            stmt.bindLong(7, fileType);
        }
 
        Integer fileFrom = entity.getFileFrom();
        if (fileFrom != null) {
            stmt.bindLong(8, fileFrom);
        }
 
        String autor = entity.getAutor();
        if (autor != null) {
            stmt.bindString(9, autor);
        }
 
        Long fileId = entity.getFileId();
        if (fileId != null) {
            stmt.bindLong(10, fileId);
        }
 
        String srcKey = entity.getSrcKey();
        if (srcKey != null) {
            stmt.bindString(11, srcKey);
        }
 
        String fileMD5 = entity.getFileMD5();
        if (fileMD5 != null) {
            stmt.bindString(12, fileMD5);
        }
 
        String fileInfo = entity.getFileInfo();
        if (fileInfo != null) {
            stmt.bindString(13, fileInfo);
        }
 
        Boolean isUpLoad = entity.getIsUpLoad();
        if (isUpLoad != null) {
            stmt.bindLong(14, isUpLoad ? 1L: 0L);
        }
 
        Integer nodeId = entity.getNodeId();
        if (nodeId != null) {
            stmt.bindLong(15, nodeId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LocalFileItem readEntity(Cursor cursor, int offset) {
        LocalFileItem entity = new LocalFileItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // fileName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // filePath
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // fileLocalPath
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // fileSize
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // creatTime
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // fileType
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // fileFrom
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // autor
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // fileId
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // srcKey
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // fileMD5
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // fileInfo
            cursor.isNull(offset + 13) ? null : cursor.getShort(offset + 13) != 0, // isUpLoad
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14) // nodeId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LocalFileItem entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFileName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFilePath(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFileLocalPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFileSize(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setCreatTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setFileType(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setFileFrom(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setAutor(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFileId(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setSrcKey(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setFileMD5(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setFileInfo(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setIsUpLoad(cursor.isNull(offset + 13) ? null : cursor.getShort(offset + 13) != 0);
        entity.setNodeId(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LocalFileItem entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LocalFileItem entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LocalFileItem entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

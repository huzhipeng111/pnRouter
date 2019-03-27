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
 * DAO for table "GROUP_ENTITY".
*/
public class GroupEntityDao extends AbstractDao<GroupEntity, Long> {

    public static final String TABLENAME = "GROUP_ENTITY";

    /**
     * Properties of entity GroupEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property GId = new Property(1, String.class, "GId", false, "GID");
        public final static Property GName = new Property(2, String.class, "GName", false, "GNAME");
        public final static Property GAdmin = new Property(3, String.class, "GAdmin", false, "GADMIN");
        public final static Property Remark = new Property(4, String.class, "Remark", false, "REMARK");
        public final static Property UserKey = new Property(5, String.class, "UserKey", false, "USER_KEY");
        public final static Property Verify = new Property(6, int.class, "Verify", false, "VERIFY");
        public final static Property RouterId = new Property(7, String.class, "routerId", false, "ROUTER_ID");
        public final static Property NickSouceName = new Property(8, String.class, "nickSouceName", false, "NICK_SOUCE_NAME");
    }


    public GroupEntityDao(DaoConfig config) {
        super(config);
    }
    
    public GroupEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"GID\" TEXT," + // 1: GId
                "\"GNAME\" TEXT," + // 2: GName
                "\"GADMIN\" TEXT," + // 3: GAdmin
                "\"REMARK\" TEXT," + // 4: Remark
                "\"USER_KEY\" TEXT," + // 5: UserKey
                "\"VERIFY\" INTEGER NOT NULL ," + // 6: Verify
                "\"ROUTER_ID\" TEXT," + // 7: routerId
                "\"NICK_SOUCE_NAME\" TEXT);"); // 8: nickSouceName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GroupEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String GId = entity.getGId();
        if (GId != null) {
            stmt.bindString(2, GId);
        }
 
        String GName = entity.getGName();
        if (GName != null) {
            stmt.bindString(3, GName);
        }
 
        String GAdmin = entity.getGAdmin();
        if (GAdmin != null) {
            stmt.bindString(4, GAdmin);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(5, Remark);
        }
 
        String UserKey = entity.getUserKey();
        if (UserKey != null) {
            stmt.bindString(6, UserKey);
        }
        stmt.bindLong(7, entity.getVerify());
 
        String routerId = entity.getRouterId();
        if (routerId != null) {
            stmt.bindString(8, routerId);
        }
 
        String nickSouceName = entity.getNickSouceName();
        if (nickSouceName != null) {
            stmt.bindString(9, nickSouceName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GroupEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String GId = entity.getGId();
        if (GId != null) {
            stmt.bindString(2, GId);
        }
 
        String GName = entity.getGName();
        if (GName != null) {
            stmt.bindString(3, GName);
        }
 
        String GAdmin = entity.getGAdmin();
        if (GAdmin != null) {
            stmt.bindString(4, GAdmin);
        }
 
        String Remark = entity.getRemark();
        if (Remark != null) {
            stmt.bindString(5, Remark);
        }
 
        String UserKey = entity.getUserKey();
        if (UserKey != null) {
            stmt.bindString(6, UserKey);
        }
        stmt.bindLong(7, entity.getVerify());
 
        String routerId = entity.getRouterId();
        if (routerId != null) {
            stmt.bindString(8, routerId);
        }
 
        String nickSouceName = entity.getNickSouceName();
        if (nickSouceName != null) {
            stmt.bindString(9, nickSouceName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public GroupEntity readEntity(Cursor cursor, int offset) {
        GroupEntity entity = new GroupEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // GId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // GName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // GAdmin
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // Remark
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // UserKey
            cursor.getInt(offset + 6), // Verify
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // routerId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // nickSouceName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GroupEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setGName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGAdmin(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRemark(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUserKey(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setVerify(cursor.getInt(offset + 6));
        entity.setRouterId(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setNickSouceName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(GroupEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(GroupEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(GroupEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

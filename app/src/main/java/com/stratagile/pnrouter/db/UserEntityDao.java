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
 * DAO for table "USER_ENTITY".
*/
public class UserEntityDao extends AbstractDao<UserEntity, Long> {

    public static final String TABLENAME = "USER_ENTITY";

    /**
     * Properties of entity UserEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property RouterUserId = new Property(2, String.class, "routerUserId", false, "ROUTER_USER_ID");
        public final static Property PublicKey = new Property(3, String.class, "publicKey", false, "PUBLIC_KEY");
        public final static Property NickName = new Property(4, String.class, "nickName", false, "NICK_NAME");
        public final static Property Avatar = new Property(5, String.class, "avatar", false, "AVATAR");
        public final static Property NoteName = new Property(6, String.class, "noteName", false, "NOTE_NAME");
        public final static Property FriendStatus = new Property(7, int.class, "friendStatus", false, "FRIEND_STATUS");
        public final static Property AddFromMe = new Property(8, boolean.class, "addFromMe", false, "ADD_FROM_ME");
        public final static Property Timestamp = new Property(9, long.class, "timestamp", false, "TIMESTAMP");
    }


    public UserEntityDao(DaoConfig config) {
        super(config);
    }
    
    public UserEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"ROUTER_USER_ID\" TEXT," + // 2: routerUserId
                "\"PUBLIC_KEY\" TEXT," + // 3: publicKey
                "\"NICK_NAME\" TEXT," + // 4: nickName
                "\"AVATAR\" TEXT," + // 5: avatar
                "\"NOTE_NAME\" TEXT," + // 6: noteName
                "\"FRIEND_STATUS\" INTEGER NOT NULL ," + // 7: friendStatus
                "\"ADD_FROM_ME\" INTEGER NOT NULL ," + // 8: addFromMe
                "\"TIMESTAMP\" INTEGER NOT NULL );"); // 9: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String routerUserId = entity.getRouterUserId();
        if (routerUserId != null) {
            stmt.bindString(3, routerUserId);
        }
 
        String publicKey = entity.getPublicKey();
        if (publicKey != null) {
            stmt.bindString(4, publicKey);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(5, nickName);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String noteName = entity.getNoteName();
        if (noteName != null) {
            stmt.bindString(7, noteName);
        }
        stmt.bindLong(8, entity.getFriendStatus());
        stmt.bindLong(9, entity.getAddFromMe() ? 1L: 0L);
        stmt.bindLong(10, entity.getTimestamp());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String routerUserId = entity.getRouterUserId();
        if (routerUserId != null) {
            stmt.bindString(3, routerUserId);
        }
 
        String publicKey = entity.getPublicKey();
        if (publicKey != null) {
            stmt.bindString(4, publicKey);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(5, nickName);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(6, avatar);
        }
 
        String noteName = entity.getNoteName();
        if (noteName != null) {
            stmt.bindString(7, noteName);
        }
        stmt.bindLong(8, entity.getFriendStatus());
        stmt.bindLong(9, entity.getAddFromMe() ? 1L: 0L);
        stmt.bindLong(10, entity.getTimestamp());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserEntity readEntity(Cursor cursor, int offset) {
        UserEntity entity = new UserEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // routerUserId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // publicKey
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nickName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // avatar
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // noteName
            cursor.getInt(offset + 7), // friendStatus
            cursor.getShort(offset + 8) != 0, // addFromMe
            cursor.getLong(offset + 9) // timestamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRouterUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPublicKey(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNickName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAvatar(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNoteName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFriendStatus(cursor.getInt(offset + 7));
        entity.setAddFromMe(cursor.getShort(offset + 8) != 0);
        entity.setTimestamp(cursor.getLong(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

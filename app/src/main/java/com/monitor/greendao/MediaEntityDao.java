package com.monitor.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.monitor.greendao.MediaEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEDIA_ENTITY".
*/
public class MediaEntityDao extends AbstractDao<MediaEntity, String> {

    public static final String TABLENAME = "MEDIA_ENTITY";

    /**
     * Properties of entity MediaEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ModifyTime = new Property(0, Long.class, "modifyTime", false, "MODIFY_TIME");
        public final static Property Name = new Property(1, String.class, "name", true, "NAME");
    };


    public MediaEntityDao(DaoConfig config) {
        super(config);
    }
    
    public MediaEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEDIA_ENTITY\" (" + //
                "\"MODIFY_TIME\" INTEGER," + // 0: modifyTime
                "\"NAME\" TEXT PRIMARY KEY NOT NULL UNIQUE );"); // 1: name
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEDIA_ENTITY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MediaEntity entity) {
        stmt.clearBindings();
 
        Long modifyTime = entity.getModifyTime();
        if (modifyTime != null) {
            stmt.bindLong(1, modifyTime);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
    }    

    /** @inheritdoc */
    @Override
    public MediaEntity readEntity(Cursor cursor, int offset) {
        MediaEntity entity = new MediaEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // modifyTime
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1) // name
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MediaEntity entity, int offset) {
        entity.setModifyTime(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(MediaEntity entity, long rowId) {
        return entity.getName();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(MediaEntity entity) {
        if(entity != null) {
            return entity.getName();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
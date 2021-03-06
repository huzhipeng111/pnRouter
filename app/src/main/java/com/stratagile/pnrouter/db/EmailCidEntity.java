package com.stratagile.pnrouter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EmailCidEntity implements Parcelable{


    @Id(autoincrement = true)
    private Long id;
    private String msgId;
    private String account;
    private String name;
    private String localPath;
    private boolean canDelete;
    private boolean hasData;
    private byte[] data;
    private String cid;

    public EmailCidEntity() {

    }

    protected EmailCidEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        msgId = in.readString();
        account = in.readString();
        name = in.readString();
        localPath = in.readString();
        canDelete = in.readByte() != 0;
        hasData = in.readByte() != 0;
        data = in.createByteArray();
    }

    @Generated(hash = 1005614747)
    public EmailCidEntity(Long id, String msgId, String account, String name,
            String localPath, boolean canDelete, boolean hasData, byte[] data, String cid) {
        this.id = id;
        this.msgId = msgId;
        this.account = account;
        this.name = name;
        this.localPath = localPath;
        this.canDelete = canDelete;
        this.hasData = hasData;
        this.data = data;
        this.cid = cid;
    }


    public static final Creator<EmailCidEntity> CREATOR = new Creator<EmailCidEntity>() {
        @Override
        public EmailCidEntity createFromParcel(Parcel in) {
            return new EmailCidEntity(in);
        }

        @Override
        public EmailCidEntity[] newArray(int size) {
            return new EmailCidEntity[size];
        }
    };

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getCanDelete() {
        return this.canDelete;
    }

    public boolean getHasData() {
        return this.hasData;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(msgId);
        dest.writeString(account);
        dest.writeString(name);
        dest.writeString(localPath);
        dest.writeByte((byte) (canDelete ? 1 : 0));
        dest.writeByte((byte) (hasData ? 1 : 0));
        dest.writeByteArray(data);
        dest.writeString(cid);
    }
}

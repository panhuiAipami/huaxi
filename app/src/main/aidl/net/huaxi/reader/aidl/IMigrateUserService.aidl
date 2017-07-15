package net.huaxi.reader.aidl;

import net.huaxi.reader.aidl.XSMigrateStore;

interface IMigrateUserService{
	List<XSMigrateStore> getXSMigrateStore();
	int deletefile(in String uid);
}
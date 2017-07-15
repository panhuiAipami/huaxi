package net.huaxi.reader.db.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.OrmDataBaseHelper;
import net.huaxi.reader.db.model.UserTable;

import com.j256.ormlite.dao.Dao;
import com.tools.commonlibs.tools.StringUtils;


public class UserDao {
    private static UserDao userdao;
    private static Dao<UserTable, Integer> mDao;

    public static UserDao getInstance() {
        if (null == userdao) {
            userdao = new UserDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance()).getDao(UserTable.class);
        return userdao;
    }

    public static UserDao getInstance(String uid) {
        if (null == userdao) {
            userdao = new UserDao();
        }
        mDao = OrmDataBaseHelper.getDataBaseHelper(AppContext.getInstance(), uid).getDao(UserTable.class);
        return userdao;
    }

    public int addUser(UserTable user) {
        if (null == user || user.getUid() == null) {
            return -1;
        }
        int result = -1;
        int i = hasKey(user.getUid());
        if (i == 1) {
            result = updateUser(user);
        } else if (i == 0) {
            try {
                result = mDao.create(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public int hasKey(String userid) {
        if (userid == null) {
            return -1;
        }
        int b = 0;
        UserTable user = findUser(userid);
        if (user != null) {
            b = 1;
        }
        return b;
    }

    public UserTable findUser(String userid) {
        UserTable user = null;
        try {
            List<UserTable> tables = mDao.queryBuilder().where().eq("u_sid", userid).query();
            if (tables != null && tables.size() > 0) {
                for (Iterator<UserTable> iterator = tables.iterator(); iterator.hasNext(); ) {
                    UserTable userTable = iterator.next();
                    if (userTable != null && !StringUtils.isBlank(userTable.getUid())) {
                        user = userTable;
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public int updateUser(UserTable user) {
        if (user == null || user.getUid() == null) return -1;
        int result = -1;
        try {
            if (user.getId() == 0) {
                UserTable ut = findUser(user.getUid());
                if (ut == null) return -1;
                int id = ut.getId();
                user.setId(id);
            }
            result = mDao.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}

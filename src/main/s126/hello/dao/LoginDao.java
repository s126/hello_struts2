package s126.hello.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import s126.hello.bean.Account;
import s126.hello.util.DBUtil;


public class LoginDao {

	/**
	 * 
	 */
	public boolean checkEname(String username) {
		String sql = "select username from account where username = ?";
		Connection conn = DBUtil.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();
			
			if (rs.next())
				return true;


		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;

		} finally {
			DBUtil.closeAll(rs, ps, conn);
		}
		
		
		return false;
	}
	
	
	
	
	/**
	 * 通过账号、密码查询账号，返回 account，查不到返回 null.
	 */
	public Account checkLogin(String name, String pwd) {
		Connection conn = DBUtil.getConn();
		String sql = "select username, acctype, lastlogin from account where username=? and password=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Account account = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, pwd);
			rs = ps.executeQuery();
			if (rs.next()) {
				account = new Account();
				account.setUsername(rs.getString(1));
				account.setAcctype(rs.getInt(2));
				account.setLastLogin(rs.getTimestamp(3));
			}

		} catch (Exception e) {
		}

		return account;
	}

	/**
	 * 增加一个新的账号.
	 */
	public boolean addAccount(Account account) {
		Connection conn = DBUtil.getConn();
		String sql = "insert into account (username, password, acctype, birthday, email, phone, sex) values (?, ?, ? , ? , ? , ?, ? )";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// 事务处理
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, account.getUsername());
			ps.setString(2, account.getPassword());
			ps.setInt(3, account.getAcctype() == 0 ? 1 : account.getAcctype());
			ps.setDate(4, new Date(account.getBirthday().getTime()));
			ps.setString(5, account.getEmail());
			ps.setString(6, account.getPhone());
			ps.setInt(7, account.getSex());
			ps.execute();

			conn.commit();

			return true;

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;

		} finally {
			DBUtil.closeAll(rs, ps, conn);
		}
	}

	
	
	/**
	 * 获取所有的身份类型，学生、老师等
	 */
	public Map<Integer, String> getAccTypes() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		String sql = "select tid, tname from types where 1=1";
		Connection conn = DBUtil.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				map.put(rs.getInt(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeAll(rs, ps, conn);
		}
		return map;
	}
	
	

	public static void main(String[] args) {
		LoginDao loginDao = new LoginDao();
		System.out.println(loginDao.checkLogin("zhouqiang", "1234567"));
	}

}
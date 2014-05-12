package edu.ycp.cs.cs496.TGOH.pesist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Notification;
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.RegistrationStatus;
import edu.ycp.cs.cs496.TGOH.temp.User;
import edu.ycp.cs.cs496.TGOH.temp.UserType;

public class RealDatabase implements IDatabase{
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load sqlite driver");
		}
	}
	
	private interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;
	
	/**
	 * Done
	 */
	@Override
	public void addUser(final User user) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;
				
				try {
					stmt = conn.prepareStatement(
							"insert into users (username, firstname, lastname, password, type) values (?, ?, ?, ?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS
					);
					
					storeUserNoId(user, stmt, 1);

					// Attempt to insert the item
					stmt.executeUpdate();

					// Determine the auto-generated id
					generatedKeys = stmt.getGeneratedKeys();
					if (!generatedKeys.next()) {
						throw new SQLException("Could not get auto-generated key for inserted Users");
					}
					
					user.setId(generatedKeys.getInt(1));
					System.out.println("New User has id " + user.getId());
					
					return true;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override 
	public boolean deleteUser(final String user) {
		return executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("Delete from users where users.username = ?");
					stmt.setString(1, user);
					
					int numRowsAffected = stmt.executeUpdate();
					
					return numRowsAffected != 0;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public User getUser(final String username) {
		return executeTransaction(new Transaction<User>() {
			@Override
			public User execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select users.* from users where users.username = ?");
					stmt.setString(1, username);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					User user = new User();
					loadUser(user, resultSet, 1);
					return user;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	@Override
	public void changeStatus(final String username) {
		executeTransaction(new Transaction<User>() {
				@Override
				public User execute(Connection conn) throws SQLException {
					PreparedStatement stmt = null;
					ResultSet keys = null;

					try {					
						stmt = conn.prepareStatement("update users set users.type = ? where users.username = ? "
								);

						stmt.setInt(1, UserType.ACCEPTEDTEACHER.ordinal());
						stmt.setString(2,  username);

						stmt.executeUpdate();
						
						User user = new User();
						
						return user;
					} finally {
						DBUtil.closeQuietly(stmt);
						DBUtil.closeQuietly(keys);
					}
				}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public Courses getCourse(final int coursename) {
		return executeTransaction(new Transaction<Courses>() {
			@Override
			public Courses execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select courses.* from courses where courses.id = ?");
					stmt.setInt(1, coursename);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					Courses course = new Courses();
					loadCourse(course, resultSet, 1);
					return course;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public void deleteCourse(final int Coursename) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				//ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("Delete from courses where courses.id = ?");
					stmt.setInt(1, Coursename);
					
					int numRowsAffected = stmt.executeUpdate();
					
					return numRowsAffected != 0;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public List<Courses> getCoursefromUser(final int user) {
		return executeTransaction(new Transaction<List<Courses>>() {
			@Override
			public List<Courses> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.userid = ? and registrations.type = ?");
					stmt.setInt(1, user);
					stmt.setInt(2, RegistrationStatus.APPROVED.ordinal());
					resultSet = stmt.executeQuery();

					List<Courses> result = new ArrayList<Courses>();
					while (resultSet.next()) {
						Courses course = new Courses();
						course = getCourse(resultSet.getInt(3));
						result.add(course);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public List<Courses> getCoursefromTeacher(final int user) {
		return executeTransaction(new Transaction<List<Courses>>() {
			@Override
			public List<Courses> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.userid = ? and registrations.type = ?");
					stmt.setInt(1, user);
					stmt.setInt(2, RegistrationStatus.TEACHER.ordinal());
					resultSet = stmt.executeQuery();

					List<Courses> result = new ArrayList<Courses>();
					while (resultSet.next()) {
						Courses course = new Courses();
						course = getCourse(resultSet.getInt(3));
						result.add(course);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public Courses getCourseByName(final String coursename) {
		return executeTransaction(new Transaction<Courses>() {
			@Override
			public Courses execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select courses.* from courses where courses.coursename = ?");
					stmt.setString(1, coursename);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					Courses course = new Courses();
					loadCourse(course, resultSet, 1);
					return course;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public void addCourse(final Courses course) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;
				
				try {
					stmt = conn.prepareStatement(
							"insert into courses (coursename, teachername) values (?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS
					);
					
					storeCourseNoId(course, stmt, 1);

					// Attempt to insert the item
					stmt.executeUpdate();

					// Determine the auto-generated id
					generatedKeys = stmt.getGeneratedKeys();
					if (!generatedKeys.next()) {
						throw new SQLException("Could not get auto-generated key for inserted Item");
					}
					
					course.setId(generatedKeys.getInt(1));
					System.out.println("New Course has id " + course.getId());
					return true;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public List<Courses> getAllCourse() {
		return executeTransaction(new Transaction<List<Courses>>() {
			@Override
			public List<Courses> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select courses.* from courses");
					
					resultSet = stmt.executeQuery();

					List<Courses> result = new ArrayList<Courses>();
					while (resultSet.next()) {
						Courses course = new Courses();
						course.setId(resultSet.getInt(1));
						course.setCourse(resultSet.getString(2));
						result.add(course);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public void registerUserForCourse(final Registration reg) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;
				
				try {
					stmt = conn.prepareStatement(
							"insert into registrations (userid, courseid, type) values (?, ?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS
					);
					
					storeRegistrationNoId(reg, stmt, 1);

					// Attempt to insert the item
					stmt.executeUpdate();

					// Determine the auto-generated id
					generatedKeys = stmt.getGeneratedKeys();
					if (!generatedKeys.next()) {
						throw new SQLException("Could not get auto-generated key for inserted Users");
					}
					
					reg.setId(generatedKeys.getInt(1));
					System.out.println("New Registration has id " + reg.getId());
					
					return true;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public void RemovingUserFromCourse(final int user, final int course) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("Delete from registrations where registrations.userid = ? and registrations.courseid = ?");
					stmt.setInt(1, user);
					stmt.setInt(2, course);
					
					int numRowsAffected = stmt.executeUpdate();

					return numRowsAffected != 0;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public User getUserfromRegistration(final int Username) {
		return executeTransaction(new Transaction<User>() {
			@Override
			public User execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select users.* from users where users.id = ?");
					stmt.setInt(1, Username);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					User user = new User();
					loadUser(user, resultSet, 1);
					return user;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public Registration findreg(final int id) {
		return executeTransaction(new Transaction<Registration>() {
			@Override
			public Registration execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.id = ?");
					stmt.setInt(1, id);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					Registration reg = new Registration();
					loadRegistration(reg, resultSet, 1);
					return reg;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public Registration findUserForCourse(final User user, final Courses course) {
		return executeTransaction(new Transaction<Registration>() {
			@Override
			public Registration execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.userid = ? and registrations.courseid = ?");
					stmt.setInt(1, user.getId());
					stmt.setInt(2, course.getId());
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					Registration reg = new Registration();
					loadRegistration(reg, resultSet, 1);
					return reg;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public void AcceptingUserforCourse(final User user, final Courses course) {
		executeTransaction(new Transaction<Registration>() {
			@Override
			public Registration execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet keys = null;

				try {					
					stmt = conn.prepareStatement("update registrations set registrations.type = ? where registrations.userid = ? and registrations.courseid = ?"
							);
					stmt.setInt(1, RegistrationStatus.APPROVED.ordinal());
					stmt.setInt(2, user.getId());
					stmt.setInt(3,  course.getId());

					stmt.executeUpdate();
				} finally {
					DBUtil.closeQuietly(stmt);
					DBUtil.closeQuietly(keys);
				}
				
				Registration reg = new Registration();
				return reg;
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public List<User> getPendingUserforCourse(final int course) {
		return executeTransaction(new Transaction<List<User>>() {
			@Override
			public List<User> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.courseid = ? and registrations.type = ?");
					stmt.setInt(1, course);
					stmt.setInt(2, RegistrationStatus.PENDING.ordinal());
					resultSet = stmt.executeQuery();

					List<User> result = new ArrayList<User>();
					while (resultSet.next()) {
						User user = new User();
						user = getUserfromRegistration(resultSet.getInt(2));
						result.add(user);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public List<User> getPendingTeacher() {
		return executeTransaction(new Transaction<List<User>>() {
			@Override
			public List<User> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select users.* from users where users.type = ?");
					stmt.setInt(1, UserType.PENDINGTEACHER.ordinal());
					resultSet = stmt.executeQuery();

					List<User> result = new ArrayList<User>();
					while (resultSet.next()) {
						User user = new User();
						user.setId(resultSet.getInt(1));
						user.setUserName(resultSet.getString(2));
						user.setFirstName(resultSet.getString(3));
						user.setLastName(resultSet.getString(4));
						user.setPassword(resultSet.getString(5));
						UserType[] statusValues = UserType.values();
						UserType status = statusValues[resultSet.getInt(6)];
						user.setType(status);
						result.add(user);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public void removeNotification(final int id) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				//ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("Delete from notifications where notifications.id = ?");
					stmt.setInt(1, id);
					
					int numRowsAffected = stmt.executeUpdate();
					
					return numRowsAffected != 0;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public Notification addNotification(final Notification not) {
		return executeTransaction(new Transaction<Notification>() {
			@Override
			public Notification execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;

				try {
					stmt = conn.prepareStatement(
							"insert into notifications (courseid, text) values (?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS
					);
					storeNotNoId(not, stmt, 1);

					// Attempt to insert the item
					stmt.executeUpdate();

					// Determine the auto-generated id
					generatedKeys = stmt.getGeneratedKeys();
					if (!generatedKeys.next()) {
						throw new SQLException("Could not get auto-generated key for inserted Item");
					}

					not.setId(generatedKeys.getInt(1));
					System.out.println("New item has id " + not.getId());

					return not;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	/*
	 * Done
	 */
	@Override
	public List<Notification> getNotificationForCourse(final int courseId) {
		return executeTransaction(new Transaction<List<Notification>>() {
			@Override
			public List<Notification> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select notifications.* from notifications where notifications.courseid = ?");
					stmt.setInt(1, courseId);
					
					resultSet = stmt.executeQuery();

					List<Notification> result = new ArrayList<Notification>();
					while (resultSet.next()) {
						Notification not = new Notification();
						not.setId(resultSet.getInt(1));
						not.setCourseId(resultSet.getInt(2));
						not.setText(resultSet.getString(3));
						result.add(not);
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	/*
	 * Done
	 */
	@Override
	public Notification getNotification(final int id) {
		return executeTransaction(new Transaction<Notification>() {
			@Override
			public Notification execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					stmt = conn.prepareStatement("select notifications.* from notifications where notifications.id = ?");
					stmt.setInt(1, id);
					
					resultSet = stmt.executeQuery();
					
					if (!resultSet.next()) {
						// No such item
						return null;
					}
					
					Notification not = new Notification();
					loadNot(not, resultSet, 1);
					return not;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	/*
	 * Done
	 */
	@Override
	public void changePass(final String username, final String password) {
		executeTransaction(new Transaction<User>() {
				@Override
				public User execute(Connection conn) throws SQLException {
					PreparedStatement stmt = null;
					ResultSet keys = null;

					try {					
						stmt = conn.prepareStatement("update users set users.password = ? where users.username = ? "
								);

						stmt.setString(1, password);
						stmt.setString(2,  username);

						stmt.executeUpdate();
						
						User user = new User();
						
						return user;
					} finally {
						DBUtil.closeQuietly(stmt);
						DBUtil.closeQuietly(keys);
					}
				}
		});
	}
	
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}

	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();
		
		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		// Set autocommit to false to allow multiple the execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}

	public void createUserTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					// Note that the 'id' column is an autoincrement primary key,
					stmt = conn.prepareStatement(
							"create table users (" +
							"  id integer primary key not null generated always as identity," +
							"  username varchar(30)," +
							"  firstname varchar(30)," +
							"  lastname varchar(30)," +
							"  password varchar(30)," +
							"  type integer not null default 0" +
							")"

					);
					
					stmt.executeUpdate();
							
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}	
	
	public void createCourseTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					// Note that the 'id' column is an autoincrement primary key,				
					stmt = conn.prepareStatement(
							"create table courses (" +
							"  id integer primary key not null generated always as identity," +
							"  coursename varchar(10) unique," +
							"  teachername varchar(10)" +
							")"
					);
					
					stmt.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}	
	
	public void createNotificationTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					// Note that the 'id' column is an autoincrement primary key,			
					stmt = conn.prepareStatement(
							"create table notifications (" +
							"  id integer primary key not null generated always as identity," +
							"  courseid integer," +
							"  text varchar(100)" +
							")"
					);
					
					stmt.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}	
	
	public void createRegistrationTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					// Note that the 'id' column is an autoincrement primary key,
					// so SQLite will automatically assign an id when rows
					// are inserted.				
					stmt = conn.prepareStatement(
							"create table registrations (" +
							"  id integer primary key not null generated always as identity," +
							"  userid integer," +
							"  courseid integer, " +
							"  type integer not null default 0" +
							")"
					);
					
					stmt.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}	
	
	public void loadInitialUserData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("insert into users (username, firstname, lastname, password, type) values (?, ?, ?, ?, ?)");
					storeUserNoId(new User("Master","Master","Master","Master", UserType.MASTER), stmt, 1);
					stmt.addBatch();
					storeUserNoId(new User("Sam","Sam","Sam","Sam",UserType.STUDENT), stmt, 1);
					stmt.addBatch();
					storeUserNoId(new User("Jason","Jason","Jason","Jason",UserType.ACCEPTEDTEACHER), stmt, 1);
					stmt.addBatch();
					storeUserNoId(new User("Babcock","Babcock","Babcock","Babcock",UserType.ACCEPTEDTEACHER), stmt, 1);
					stmt.addBatch();
					
					stmt.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public void loadCourseInitialUserData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("insert into courses (coursename, teachername) values (?, ?)");
					
					storeCourseNoId(new Courses("CS360", "Babcock"), stmt, 1);
					stmt.addBatch();
					storeCourseNoId(new Courses("CS101", "Babcock"), stmt, 1);
					stmt.addBatch();
					storeCourseNoId(new Courses("CS201", "Jason"), stmt, 1);
					stmt.addBatch();
					
					stmt.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public void loadRegInitialUserData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("insert into registrations (userid, courseid, type) values (?,?,?)");
					Registration reg3 = new Registration(4,1);
					reg3.setStatus(RegistrationStatus.TEACHER);
					storeRegistrationNoId(reg3, stmt, 1);
					stmt.addBatch();
					Registration reg4 = new Registration(4,2);
					reg4.setStatus(RegistrationStatus.TEACHER);
					storeRegistrationNoId(reg4, stmt, 1);
					stmt.addBatch();
					Registration reg = new Registration(2,1);
					reg.setStatus(RegistrationStatus.PENDING);
					storeRegistrationNoId(reg, stmt, 1);
					stmt.addBatch();
					Registration regg = new Registration(3,3);
					regg.setStatus(RegistrationStatus.TEACHER);
					storeRegistrationNoId(regg, stmt, 1);
					
					stmt.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public void loadNoteInitialUserData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("insert into notifications (courseid, text) values (?,?)");
					storeNotNoId(new Notification(1, "Welcome"), stmt, 1);
					stmt.addBatch();
					storeNotNoId(new Notification(2, "Welcome"), stmt, 1);
					stmt.addBatch();
					storeNotNoId(new Notification(3, "Welcome"), stmt, 1);
					stmt.addBatch();
					
					stmt.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	protected void storeUserNoId(User user, PreparedStatement stmt, int index) throws SQLException {
		stmt.setString(index++, user.getUserName());
		stmt.setString(index++, user.getFirstName());
		stmt.setString(index++, user.getLastName());
		stmt.setString(index++, user.getPassword());
		stmt.setInt(index++, user.getType().ordinal());
	}
	
	protected void storeCourseNoId(Courses course, PreparedStatement stmt, int index) throws SQLException {
		stmt.setString(index++, course.getCourse());
		stmt.setString(index++, course.getTeacher());
	}
	
	protected void storeRegistrationNoId(Registration reg, PreparedStatement stmt, int index) throws SQLException {
		stmt.setInt(index++, reg.getUserId());
		stmt.setInt(index++, reg.getCourseId());
		stmt.setInt(index++, reg.getStatus().ordinal());
	}
	
	protected void storeNotNoId(Notification not, PreparedStatement stmt, int index) throws SQLException {
		stmt.setInt(index++, not.getCourseId());
		stmt.setString(index++, not.getText());
	}
	
	protected void loadUser(User user, ResultSet resultSet, int index) throws SQLException {
		user.setId(resultSet.getInt(index++));
		user.setUserName(resultSet.getString(index++));
		user.setFirstName(resultSet.getString(index++));
		user.setLastName(resultSet.getString(index++));
		user.setPassword(resultSet.getString(index++));
		UserType[] statusValues = UserType.values();
		UserType status = statusValues[resultSet.getInt(index++)];
		user.setType(status);
	}
	
	protected void loadRegistration(Registration reg, ResultSet resultSet, int index) throws SQLException {
		reg.setId(resultSet.getInt(index++));
		reg.setUserId(resultSet.getInt(index++));
		reg.setCourseId(resultSet.getInt(index++));
		RegistrationStatus[] statusValues = RegistrationStatus.values();
		RegistrationStatus status = statusValues[resultSet.getInt(index++)];
		reg.setStatus(status);
	}
	
	protected void loadCourse(Courses course, ResultSet resultSet, int index) throws SQLException {
		course.setId(resultSet.getInt(index++));
		course.setCourse(resultSet.getString(index++));
		course.setTeacher(resultSet.getString(index++));
	}
	
	protected void loadNot(Notification not, ResultSet resultSet, int index) throws SQLException {
		not.setId(resultSet.getInt(index++));
		not.setCourseId(resultSet.getInt(index++));
		not.setText(resultSet.getString(index++));
	}
	
	public static void main(String[] args) {
		RealDatabase db = new RealDatabase();
		System.out.println("Creating tables...");
		db.createCourseTables();
		db.createNotificationTables();
		db.createRegistrationTables();
		db.createUserTables();
		System.out.println("Loading initial data...");
		db.loadInitialUserData();
		db.loadCourseInitialUserData();
		db.loadRegInitialUserData();
		db.loadNoteInitialUserData();
		System.out.println("Done!");
	}

}

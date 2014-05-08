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
					System.out.println("New item has id " + user.getId());
					
					return true;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	// fixing
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

	@Override
	public List<Courses> getCoursefromUser(final int user) {
		return executeTransaction(new Transaction<List<Courses>>() {
			@Override
			public List<Courses> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.userid = ?");
					stmt.setInt(1, user);
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

	@Override
	public void addCourse(final Courses course) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;
				
				try {
					stmt = conn.prepareStatement(
							"insert into courses (coursename) values (?)",
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
					System.out.println("New item has id " + course.getId());
					
					return true;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

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

	@Override
	public Registration registerUserForCourse(final int user, final int course) {
		return executeTransaction(new Transaction<Registration>() {
			@Override
			public Registration execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet generatedKeys = null;
				
				try {
					stmt = conn.prepareStatement(
							"insert into registrations (userid, courseid) values (?, ?)",
							PreparedStatement.RETURN_GENERATED_KEYS
					);
					Registration reg = new Registration(user,course);
					storeRegistrationNoId(reg, stmt, 1);

					// Attempt to insert the item
					stmt.executeUpdate();

					// Determine the auto-generated id
					generatedKeys = stmt.getGeneratedKeys();
					if (!generatedKeys.next()) {
						throw new SQLException("Could not get auto-generated key for inserted Users");
					}
					
					reg.setId(generatedKeys.getInt(1));
					System.out.println("New item has id " + reg.getId());
					
					return reg;
				} finally {
					DBUtil.closeQuietly(generatedKeys);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}

	//fixme
	@Override
	public void RemovingUserFromCourse(final User user, final Courses course) {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement("Delete from registrations where registrations.userid = ? and registrations.courseid = ?");
					stmt.setInt(1, user.getId());
					stmt.setInt(2, course.getId());
					
					int numRowsAffected = stmt.executeUpdate();
					
					return numRowsAffected != 0;
				} finally {
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
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

	// updating
	@Override
	public Registration AcceptingUserforCourse(User user, Courses course) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getPendingUserforCourse(final int course) {
		return executeTransaction(new Transaction<List<User>>() {
			@Override
			public List<User> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// Note: no 'where' clause, so all items will be returned
					stmt = conn.prepareStatement("select registrations.* from registrations where registrations.courseid = ?");
					stmt.setInt(1, course);
					resultSet = stmt.executeQuery();

					List<User> result = new ArrayList<User>();
					while (resultSet.next()) {
						User user = new User();
						user = getUserfromRegistration(resultSet.getInt(1));
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

	@Override
	public Notification addNotification(final int courseId, final String text) {
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
					Notification not = new Notification();
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
	
	@Override

	public void changePass(final String username, final String password) {
		executeTransaction(new Transaction<User>() {
				@Override
				public User execute(Connection conn) throws SQLException {
					PreparedStatement stmt = null;
					ResultSet keys = null;

					try {					
						stmt = conn.prepareStatement("update users set users.password = ? where users.username = ? "  // FIXME:+  security issue    // only update score if new score is higher
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
					// so SQLite will automatically assign an id when rows
					// are inserted.
					stmt = conn.prepareStatement(
							"create table users (" +
							"  id integer primary key not null generated always as identity," +
							"  username varchar(30) unique," +
							"  firstname varchar(30) unique," +
							"  lastname varchar(30) unique," +
							"  password varchar(30) unique," +
							"  type boolean not null " +
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
					// so SQLite will automatically assign an id when rows
					// are inserted.				
					stmt = conn.prepareStatement(
							"create table courses (" +
							"  id integer primary key not null generated always as identity," +
							"  coursename varchar(10) unique" +
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
					// so SQLite will automatically assign an id when rows
					// are inserted.				
					stmt = conn.prepareStatement(
							"create table notifications (" +
							"  id integer primary key not null generated always as identity," +
							"  courseid integer unique," +
							"  text varchar(100) unique" +
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
							"  userid integer unique," +
							"  courseid integer unique, " +
							//"  type enum('PENDING', 'ACCEPTED') not null default 'PENDING" +  // ask for help for this one.
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
					storeUserNoId(new User("Apples","Apples","Apples","Apples", true), stmt, 1);
					stmt.addBatch();
					storeUserNoId(new User("Oranges","Oranges","Oranges","Oranges",false), stmt, 1);
					stmt.addBatch();
					storeUserNoId(new User("Pomegranates","Pomegranates","Pomegranates","Pomegranates",true), stmt, 1);
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
					stmt = conn.prepareStatement("insert into courses (coursename) values (?)");
					storeCourseNoId(new Courses("CS360"), stmt, 1);
					stmt.addBatch();
					storeCourseNoId(new Courses("CS101"), stmt, 1);
					stmt.addBatch();
					storeCourseNoId(new Courses("CS201"), stmt, 1);
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
					storeRegistrationNoId(new Registration(1,1), stmt, 1);
					stmt.addBatch();
					
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
					storeNotNoId(new Notification(), stmt, 1);
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
		// Note that we are assuming that the Item does not have a valid id,
		// and so are not attempting to store the (invalid) id.
		// This is the preferred approach when inserting a new row into
		// a table in which a unique id is automatically generated.
		stmt.setString(index++, user.getUserName());
		stmt.setString(index++, user.getFirstName());
		stmt.setString(index++, user.getLastName());
		stmt.setString(index++, user.getPassword());
		stmt.setBoolean(index++, user.getType());
	}
	
	protected void storeCourseNoId(Courses course, PreparedStatement stmt, int index) throws SQLException {
		// Note that we are assuming that the Item does not have a valid id,
		// and so are not attempting to store the (invalid) id.
		// This is the preferred approach when inserting a new row into
		// a table in which a unique id is automatically generated.
		stmt.setString(index++, course.getCourse());
	}
	
	protected void storeRegistrationNoId(Registration reg, PreparedStatement stmt, int index) throws SQLException {
		// Note that we are assuming that the Item does not have a valid id,
		// and so are not attempting to store the (invalid) id.
		// This is the preferred approach when inserting a new row into
		// a table in which a unique id is automatically generated.
		stmt.setInt(index++, reg.getUserId());
		stmt.setInt(index++, reg.getCourseId());
		stmt.setInt(index++, reg.getStatus().ordinal());
	}
	
	protected void storeNotNoId(Notification not, PreparedStatement stmt, int index) throws SQLException {
		// Note that we are assuming that the Item does not have a valid id,
		// and so are not attempting to store the (invalid) id.
		// This is the preferred approach when inserting a new row into
		// a table in which a unique id is automatically generated.
		stmt.setInt(index++, not.getCourseId());
		stmt.setString(index++, not.getText());
	}
	
	protected void loadUser(User item, ResultSet resultSet, int index) throws SQLException {
		item.setId(resultSet.getInt(index++));
		item.setUserName(resultSet.getString(index++));
		item.setFirstName(resultSet.getString(index++));
		item.setLastName(resultSet.getString(index++));
		item.setPassword(resultSet.getString(index++));
		item.setType(resultSet.getBoolean(index++));
	}
	
	protected void loadRegistration(Registration reg, ResultSet resultSet, int index) throws SQLException {
		reg.setId(resultSet.getInt(index++));
		reg.setUserId(resultSet.getInt(index++));
		reg.setCourseId(resultSet.getInt(index++));
		//reg.setStatus(resultSet.get(index++));
		RegistrationStatus[] statusValues = RegistrationStatus.values();
		RegistrationStatus status = statusValues[resultSet.getInt(index++)];
		reg.setStatus(status);
	}
	
	
	protected void loadCourse(Courses item, ResultSet resultSet, int index) throws SQLException {
		item.setId(resultSet.getInt(index++));
		item.setCourse(resultSet.getString(index++));
	}
	
	protected void loadNot(Notification item, ResultSet resultSet, int index) throws SQLException {
		item.setId(resultSet.getInt(index++));
		item.setCourseId(resultSet.getInt(index++));
		item.setText(resultSet.getString(index++));
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

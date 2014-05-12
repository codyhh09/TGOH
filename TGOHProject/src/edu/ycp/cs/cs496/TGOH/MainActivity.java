package edu.ycp.cs.cs496.TGOH;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.ycp.cs.cs496.TGOH.controller.AcceptUser4Course;
import edu.ycp.cs.cs496.TGOH.controller.AddAnnouncement;
import edu.ycp.cs.cs496.TGOH.controller.AddCourse;
import edu.ycp.cs.cs496.TGOH.controller.DeleteCourse;
import edu.ycp.cs.cs496.TGOH.controller.DeleteUser;
import edu.ycp.cs.cs496.TGOH.controller.DeletingARegistration;
import edu.ycp.cs.cs496.TGOH.controller.GetAnnouncements;
import edu.ycp.cs.cs496.TGOH.controller.GetCourseByName;
import edu.ycp.cs.cs496.TGOH.controller.GetCoursesfromTeacher;
import edu.ycp.cs.cs496.TGOH.controller.GetCoursesfromUser;
import edu.ycp.cs.cs496.TGOH.controller.GetPendingUsersforCourse;
import edu.ycp.cs.cs496.TGOH.controller.GetUser;
import edu.ycp.cs.cs496.TGOH.controller.PutPassword;
import edu.ycp.cs.cs496.TGOH.controller.RegisterForCourse;
import edu.ycp.cs.cs496.TGOH.controller.RemovingAUserFromCourse;
import edu.ycp.cs.cs496.TGOH.controller.RemovingAnAnnouncement;
import edu.ycp.cs.cs496.TGOH.controller.ReplaceStatus;
import edu.ycp.cs.cs496.TGOH.controller.adduser;
import edu.ycp.cs.cs496.TGOH.controller.getRegforCourse;
import edu.ycp.cs.cs496.TGOH.controller.gettingPendingTeachers;
import edu.ycp.cs.cs496.TGOH.temp.Courses;
import edu.ycp.cs.cs496.TGOH.temp.Notification;
import edu.ycp.cs.cs496.TGOH.temp.Registration;
import edu.ycp.cs.cs496.TGOH.temp.RegistrationStatus;
import edu.ycp.cs.cs496.TGOH.temp.User;
import edu.ycp.cs.cs496.TGOH.temp.UserType;

public class MainActivity extends Activity {
	protected static final View CurrentUser = null;
	public User Currentuser = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDefaultView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /**DONE(FOR NOW)
     * This will take us to the sign in page
     */
	public void setDefaultView(){
		setContentView(R.layout.activity_main);
		getResources().getColor(R.color.red);
		Currentuser = null;
		Button Signin = (Button) findViewById(R.id.btnSignIn);
		Button Signup = (Button) findViewById(R.id.btnSignUp);
		
		Signup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// setting a new account to the Database.
				setSignupPage();
			}
		});
		
		Signin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText Username = (EditText) findViewById(R.id.txtUserName);
				EditText Password = (EditText) findViewById(R.id.txtPassword);
				
				String userName = Username.getText().toString();
				String passWord = Password.getText().toString();
				GetUser controller = new GetUser();
        			//get a user object from the database
					try {	
						Currentuser = controller.getUser(userName);
						
						if(Currentuser.getPassword().equals(passWord)){
							if(Currentuser.getType().equals(UserType.MASTER))
								setMaster_Notification_Page();
							else if(Currentuser.getType().equals(UserType.ACCEPTEDTEACHER))
								setTeacher_Selection_Page();
							else if(Currentuser.getType().equals(UserType.STUDENT))
								setClass_Selection_Page();														//user is student, go to student page
							else
								Toast.makeText(MainActivity.this, "Still waiting for the Master to accept you as a teacher.", Toast.LENGTH_SHORT).show();
						}else{
							//check to make sure the userName and passWord for the user are both correct
							Toast.makeText(MainActivity.this, "Invalid Username/Password.", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
					
						
						Toast.makeText(MainActivity.this, "Username does not exsist.", Toast.LENGTH_SHORT).show();
					}
			}
		});
	}
	
	/**DONE(FOR NOW)
	 *Display the Sign up page 
	 *User enters firstname/lastname/username/passowrd/usertype
	 **/
	public void setSignupPage(){
		setContentView(R.layout.signuppage);
		
		Button Signin = (Button) findViewById(R.id.btnSignUp);
		Button Back = (Button) findViewById(R.id.back);
		//pull information from text boxes and add the new user to the database
		//also error checking
        final EditText Username = (EditText) findViewById(R.id.UserNameSignup);
        final EditText Password = (EditText) findViewById(R.id.PassSignUp);
        final EditText FirstName = (EditText) findViewById(R.id.FirstNameSignup);
        final EditText LastName = (EditText) findViewById(R.id.LastNameSignup);
        final EditText Passwordcheck = (EditText) findViewById(R.id.editText1);
        final RadioButton isStudent = (RadioButton) findViewById(R.id.studentradio);
		
        Signin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Password.getText().toString().equals(Passwordcheck.getText().toString())){   //check to see if passwords entered are equal
					adduser controller = new adduser();
					GetUser con = new GetUser();
					UserType type;
					if(isStudent.isChecked()){
						 type = UserType.STUDENT;
					}else{
						type = UserType.PENDINGTEACHER;
					}
						
					try {
						if(con.getUser(Username.getText().toString()) == null && controller.postUser(Username.getText().toString(), Password.getText().toString(),FirstName.getText().toString(), LastName.getText().toString(), type)){
							// toast box: right
							if(type.equals(UserType.STUDENT)){
								Toast.makeText(MainActivity.this, "Welcome to TGOH. Please log in.", Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(MainActivity.this, "You have requested to be a teacher. Your request is pending...", Toast.LENGTH_SHORT).show();
							}
							setDefaultView();
						}else{
							Toast.makeText(MainActivity.this, "Please select another username.", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "Invalid Request." , Toast.LENGTH_SHORT).show();
					}	
				}else{//Inform users that their passwords do not match each other
					Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//take user back to original sign in page
				setDefaultView();
			}
		});
	}
	
	/**DONE(FOR NOW)
	 * This is for students to select a class and view their schedule
	 */
	public void setClass_Selection_Page(){
	
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.class_selection_page);
				
			Button viewSchedule = (Button) findViewById(R.id.btnback);
			Button Req = (Button) findViewById(R.id.btnRequestClass);
			ListView lview = (ListView) findViewById(R.id.listView1);
			Button LogOut = (Button) findViewById(R.id.button1);
			Button Settings = (Button) findViewById(R.id.button2);
			
			//pull the list of user courses from the database
			GetCoursesfromUser con = new GetCoursesfromUser(); 
			List<String> classes = new ArrayList<String>();
			Courses[] courses = null;


			try {
				 courses = con.getCourses(Currentuser.getId());
				for(int i = 0; i < courses.length; i++){
					classes.add(courses[i].getCourse());
				}
			} catch (Exception e) {
				e.printStackTrace();
				//Toast.makeText(MainActivity.this, "User does not have any courses." , Toast.LENGTH_SHORT).show();
			}
			
			final Courses[] courses2 = courses;
			ArrayAdapter<String> la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classes);
			lview.setAdapter(la);      
			lview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					//pull item from listview - go to homepage associated with the class

					Courses course = new Courses();
					course = courses2[arg2]; 
	
					setStudent_Home_Page(course);
				}
			});

			viewSchedule.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					setSchedule_Page(courses2);
				}
			});
			
			Req.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setRequest_Page();
				}}
			);
			
			LogOut.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Logs the user out and brings back to sign-in page.
					setDefaultView();
				}
			});
			
			Settings.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Logs the user out and brings back to sign-in page.
					setSettings_Page();
				}
			});
		}
	}

	/**DONE(FOR NOW)
	 * Displays a student's course homepage 
	 * @param course name of the course that homepage is displayed for
	 */
	public void setStudent_Home_Page(Courses course)
	{
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.studenthomepage);
			
			TextView classlbl = (TextView) findViewById(R.id.classlbl);
			classlbl.setText("for: " + course.getCourse()); 
			
			Button LogOut = (Button) findViewById(R.id.button1);
			Button back = (Button) findViewById(R.id.button2);
			
			LogOut.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Logs the user out and brings back to sign-in page.
					setDefaultView();
				}
			});
			
			back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setClass_Selection_Page();
				}
			});
				//view holding the announcements
				ListView lview = (ListView) findViewById(R.id.listView1);
				
				List<String> announcements = new ArrayList<String>();
				List<String> announcements2 = new ArrayList<String>();
				//controller for getting the Announcements
				GetAnnouncements con = new GetAnnouncements();
				Notification[]  announce = null;
				try {
					announce = con.getAnnouncements(course.getId());
					//adding the announcements to the list
					for(int j = 0; j < announce.length; j++){
						announcements.add(announce[j].getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				for(int j = announcements.size()-1; j >= 0; j--){
					announcements2.add(announcements.get(j));
				}
				//add strings to a list adapter to be displayed
				ArrayAdapter<String> la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, announcements2);
				lview.setAdapter(la);
		}
	}

	/**done (for now)
	 * Page students use to request a new class
	 */
	public void setRequest_Page() {
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.request_page); 
			
			Button LogOut = (Button) findViewById(R.id.button1);
			Button Back = (Button) findViewById(R.id.button2);
			Button submit = (Button) findViewById(R.id.submitbtn);
			final EditText TeacherName = (EditText) findViewById(R.id.txtteachername);	
			

			
			LogOut.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) {
					// Logs the user out and brings back to sign-in page.
					setDefaultView();
				}
			});

			Back.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) {
					setClass_Selection_Page();
				}
			});
			
			submit.setOnClickListener(new View.OnClickListener() 
			{
				Courses[] courses = null;
				@Override
				public void onClick(View v) {
					ListView lview = (ListView) findViewById(R.id.listView1);
					String Teachername = TeacherName.getText().toString();
					
					//pull the list of user courses from the database
					GetCoursesfromTeacher con = new GetCoursesfromTeacher(); 
					GetUser user = new GetUser();
					List<String> classes = new ArrayList<String>();

					try {
						courses = con.getCourses(user.getUser(Teachername).getId());
						
						for(int i = 0; i< courses.length; i++){
							classes.add(courses[i].getCourse());
						}
					} catch (Exception e) {
						e.printStackTrace();
						//Toast.makeText(MainActivity.this, "User does not have any courses." , Toast.LENGTH_SHORT).show();
					} 
					
					ArrayAdapter<String> la = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, classes);
					lview.setAdapter(la);  
					
					lview.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
									CharSequence msg = null;
									// fix: adding it once.

									RegisterForCourse con = new RegisterForCourse();
									getRegforCourse getting = new getRegforCourse();
									try {	
										if(getting.getregforCourse(Currentuser.getId(), courses[arg2].getId()) != null){
											msg = "You are already waiting on the instructer for this course.";
										}else{
											Registration reg = new Registration();
											reg.setCourseId(courses[arg2].getId());
											reg.setUserId(Currentuser.getId());
											reg.setStatus(RegistrationStatus.PENDING);
											con.postRegisterRequest(reg);
											msg = "You have selected " + ((TextView) view).getText() + ". Please wait for approval.";
										}
									} catch (Exception e) {
										e.printStackTrace();
									} 
									Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
								}
							});
					

					
				}
			});		
					
		}
	}

	/**DONE(FOR NOW)
	 * Displays all of the student's announcements
	 * @param courses 
	 */
	public void setSchedule_Page(Courses[] courses){
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.schedule_page);
			
			Button Back = (Button) findViewById(R.id.btnBack);
			Button LogOut = (Button) findViewById(R.id.button1);
			
			Back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setClass_Selection_Page();
				}
			});
			
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v)
				{
					setDefaultView();
				}
			});
	
			ListView lview = (ListView) findViewById(R.id.listView1);
			
			List<String> announcements = new ArrayList<String>();
			List<String> announcements2 = new ArrayList<String>();
			//controller for getting the Announcements
			GetAnnouncements con = new GetAnnouncements();
			Notification[]  announce = null;
			
			for(int i = 0; i <courses.length; i++){
				//list of announcements
				try {
					announce = con.getAnnouncements(courses[i].getId());
					//adding the announcements to the list
					for(int j = 0; j < announce.length; j++){
						announcements.add(announce[j].getText());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				for(int j = announcements.size()-1; j >= 0; j--){
					announcements2.add(announcements.get(j));
				}
			}
				//add strings to a list adapter to be displayed
				ArrayAdapter<String> la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, announcements2);
				lview.setAdapter(la);
		}
	}
	
	/**DONE(FOR NOW)
	 * Teacher's homepage
	 * @param course
	 */
	public void setTeacher_Main_Page(final Courses course){
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.teacher_main_page);
			
			Button notify = (Button) findViewById(R.id.btnNotify);
			Button LogOut = (Button) findViewById(R.id.logoutbtn);
			Button back = (Button) findViewById(R.id.backbtn);
			Button delete = (Button) findViewById(R.id.button2);
			Button add = (Button) findViewById(R.id.button1);
			final EditText announcmentText = (EditText) findViewById(R.id.editText1);
		
			ListView lview = (ListView) findViewById(R.id.listView2);

			//list of announcements
			List<String> announcements = new ArrayList<String>();
			List<String> announcements2 = new ArrayList<String>();
			//controller for getting the Announcements
			GetAnnouncements con = new GetAnnouncements();
			Notification[]  announce = null;
			
			try {
				announce = con.getAnnouncements(course.getId());
				//adding the announcements to the list
				for(int j = 0; j < announce.length; j++){
					announcements.add(announce[j].getText());
				}


			} catch (Exception e) {
				e.printStackTrace();
			} 
			for(int j = announcements.size()-1; j > 0; j--){
				announcements2.add(announcements.get(j));
			}
			//add strings to a list adapter to be displayed
			final Notification[] not = announce;
			ArrayAdapter<String> la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, announcements2);
			lview.setAdapter(la);
			
			lview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3){
					//pull item from listview - delete announcement
					RemovingAnAnnouncement removeCon = new RemovingAnAnnouncement();
					try {
						if(removeCon.deleteAnnouncment(not[not.length-1 - arg2].getId())){
							setTeacher_Main_Page(course);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "No announcements to show." , Toast.LENGTH_SHORT).show();
					}	
				}
			});
			
			// Add onClickListener
			notify.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					setTeacher_Notification_Page(course);
				}
			});
			
			back.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					setTeacher_Selection_Page();
				}
			});
			
			delete.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					//delete course
					DeleteCourse con = new DeleteCourse(); 
					RemovingAUserFromCourse cont = new RemovingAUserFromCourse();
					try {
						con.deleteCourse(course.getId());
						cont.deleteRegristration(Currentuser.getId(), course.getId());
						Toast.makeText(MainActivity.this, "Course Deleted!" , Toast.LENGTH_SHORT).show();
						setTeacher_Selection_Page();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v) 
				{
					setDefaultView();
				}
			});
			
			// Add onClickListener
			add.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					//pull message from text box
					Notification newNot = new Notification();
					newNot.setText(announcmentText.getText().toString());
					newNot.setCourseId(course.getId());
					
					//add to database through Add Announcement controller
					AddAnnouncement controller = new AddAnnouncement();
					try {
						if(controller.postAnnouncement(newNot)){
							Toast.makeText(MainActivity.this, "Announcement Added" , Toast.LENGTH_SHORT).show();
						}	
						setTeacher_Main_Page(course);
						
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this, "Internal Error." , Toast.LENGTH_SHORT).show();
					} 
					
				}
			});
		}
	}
	
	/**done (for now)
	 * Displays the students who are pending for
	 * a given course the teacher teaches
	 */
	public void setTeacher_Notification_Page(final Courses course)
	{
		
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.teacher_notification_page);
			
			Button acceptButton = (Button) findViewById(R.id.btnaccept);
			Button denyButton = (Button) findViewById(R.id.btndeny);
			Button logOutButton = (Button) findViewById(R.id.btnlogout);
			Button backButton = (Button) findViewById(R.id.btnback);
			
			// pull notifications from database
			
			List<String> list = new ArrayList<String>();
			GetPendingUsersforCourse con = new GetPendingUsersforCourse();
			User[] user = null;
			
			try {
				user = con.getUser(course.getId());	
				for(int i = 0; i <= user.length; i++){
					list.add(user[i].getUserName());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		
			ArrayList<CheckBox> checks = new ArrayList<CheckBox>();
			
			// Access Linear layout for ScrollView
			LinearLayout layout4Checks = (LinearLayout) findViewById(R.id.linearLayout1);
			
			//Add Check Box to go next to requests' names
			for (String students : list)
			{
				CheckBox check = new CheckBox(this);
				check.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				check.setText(students);
				checks.add(check);
				//check.setButtonDrawable(R.drawable.check);
				
				// Add check to layout
				layout4Checks.addView(check);
			}
			
			final ArrayList<CheckBox> checkL2 = checks;
			
			//Accept Button
			// Add accept button onClickListener
			acceptButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					for (CheckBox students : checkL2)
					{
						if(students.isChecked())
						{
							GetUser UConn = new GetUser();
							AcceptUser4Course au = new AcceptUser4Course();

							try {
								User user = UConn.getUser(students.getText().toString());
								au.putUser(user.getUserName(), course.getId());
								Toast.makeText(MainActivity.this, "You just added "+ user.getUserName() +" to the course." , Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					setTeacher_Notification_Page(course);
				}
			});
			
			//Add Deny Button
			// Add Deny button onClickListener
			denyButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					//TODO: Removes user from list.  Sends sad message to user.
					for (CheckBox students : checkL2)
					{
						if(students.isChecked())
						{
							GetUser UConn = new GetUser();
							DeletingARegistration con = new DeletingARegistration();

							try {
								User user = UConn.getUser(students.getText().toString());
								con.deleteRegistration(user.getId(), course.getId());
								Toast.makeText(MainActivity.this, "You just denied "+ user.getUserName() +" from the course." , Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					setTeacher_Notification_Page(course);
				}
			});
			
			//Log Off Button
			// Add log-off button onClickListener
			logOutButton.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					// Logs the user out and brings back to sign-in page.
					setDefaultView();
				}
			});		
		
			//Add Back Button
			// Add back button onClickListener
			backButton.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					setTeacher_Main_Page(course);
				}
			});
		}
	}
	
	/**DONE(FOR NOW)
	 * Page which displays classes the teacher is teaching
	 */
	public void setTeacher_Selection_Page()
	{
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.teacher_selection_page);
			
			Button LogOut = (Button) findViewById(R.id.button1);
			Button Create = (Button) findViewById(R.id.button2);
			Button Settings = (Button) findViewById(R.id.button3);
			ListView lview = (ListView) findViewById(R.id.listView1);
			
			//pull the list of user courses from the database
			GetCoursesfromTeacher con = new GetCoursesfromTeacher(); 
			List<String> classes = new ArrayList<String>();
			Courses[] courses = null;

			try {
				courses = con.getCourses(Currentuser.getId());
				
				for(int i = 0; i< courses.length; i++){
					classes.add(courses[i].getCourse());
				}
			} catch (Exception e) {
				e.printStackTrace();
				//Toast.makeText(MainActivity.this, "User does not have any courses." , Toast.LENGTH_SHORT).show();
			} 
			
			final Courses[] courses2 = courses;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classes);
			lview.setAdapter(adapter);
			
			lview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					Courses course = new Courses();
					course = courses2[arg2]; 
	
					setTeacher_Main_Page(course);

				}
			});
		
			
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v) 
				{
					setDefaultView();
				}
			});
			
			Create.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					setCreate_Course();
				}
			});
			
			Settings.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// Logs the user out and brings back to sign-in page.
					setSettings_Page();
				}
			});
		}
	}
	
	/**DONE(FOR NOW)
	 * method to create a new course
	 */
	public void setCreate_Course()
	{
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.create_course);
			
			Button LogOut = (Button) findViewById(R.id.button1);
			Button back = (Button) findViewById(R.id.button2);
			Button submit = (Button) findViewById(R.id.button3);
			final EditText newCourse = (EditText) findViewById(R.id.txtCourse);
			
			
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v) 
				{
					setDefaultView();
				}
			});
			
			back.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					setTeacher_Selection_Page();
				}
			});
			
			submit.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					//add the course to the courses database
					GetCourseByName cont = new GetCourseByName();
					try {
						if(cont.getCourse(newCourse.getText().toString()) != null){
							Toast.makeText(MainActivity.this, "This course already exists.", Toast.LENGTH_SHORT).show();
						}else{
							AddCourse con = new AddCourse(); 
							Courses course = new Courses();
							course.setCourse(newCourse.getText().toString());
							course.setTeacher(Currentuser.getUserName());
							GetUser get = new GetUser();
							RegisterForCourse reging = new RegisterForCourse();
							Registration reg = new Registration();
							GetCourseByName ce = new GetCourseByName();
							User user;
						
							if(con.postCourse(course)){
								Toast.makeText(MainActivity.this, "Added: " + newCourse.getText().toString(), Toast.LENGTH_SHORT).show();
								user = get.getUser(Currentuser.getUserName());	
								reg.setCourseId(ce.getCourse(newCourse.getText().toString()).getId());
								reg.setUserId(user.getId());
								reg.setStatus(RegistrationStatus.TEACHER);
								if(reging.postRegisterRequest(reg))
									Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(MainActivity.this, "Error" + newCourse.getText().toString(), Toast.LENGTH_SHORT).show();	
							}else
								Toast.makeText(MainActivity.this, "Didn't added: " + newCourse.getText().toString(), Toast.LENGTH_SHORT).show();
							setTeacher_Selection_Page();	

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			
				}


			});
			
		}
	}

	/**Finished notifications
	 * the master's notification/homepage
	 */
	public void setMaster_Notification_Page(){
		if(Currentuser == null){
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}else{
			setContentView(R.layout.master_notifications_page);
			Button LogOut = (Button) findViewById(R.id.button1);
			Button Accept =(Button) findViewById(R.id.backbtn);
			Button Deny = (Button) findViewById(R.id.submitbtn);
			

			List<String> list = new ArrayList<String>();
			gettingPendingTeachers con = new gettingPendingTeachers();
			User[] user = null;
			
			try {
				user = con.getPT();	
				for(int i = 0; i < user.length; i++){
					list.add(user[i].getUserName());
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} 	
			
			ArrayList<CheckBox> checks = new ArrayList<CheckBox>();
			
			// Access Linear layout for ScrollView
			LinearLayout layout4Checks = (LinearLayout) findViewById(R.id.linearLayout2);

			//Add Check Box to go next to requests' names
			for (String students : list){
				CheckBox check = new CheckBox(this);
				check.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				check.setText(students);
				checks.add(check);
				check.setButtonDrawable(R.drawable.check);
				
				// Add check to layout
				layout4Checks.addView(check);
			}
			
			
			final ArrayList<CheckBox> checkL2 = checks;
					
			//Accept Button
			// Add accept button onClickListener
			Accept.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					for (CheckBox students : checkL2)
					{
						if(students.isChecked())
						{
							
							try {
								ReplaceStatus con = new ReplaceStatus();
								GetUser cont = new GetUser();
								con.putReplace(cont.getUser(students.getText().toString()));
								Toast.makeText(MainActivity.this, "You just added "+ students.getText().toString() +" as a teacher", Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					setMaster_Notification_Page();
				}
			});
			
			//Add Deny Button
			// Add Deny button onClickListener
			Deny.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					//TODO: Removes user from list.  Sends sad message to user.
					for (CheckBox students : checkL2)
					{
						if(students.isChecked())
						{
							DeleteUser duser = new DeleteUser();
							try {
								duser.deleteUser(students.getText().toString());
								Toast.makeText(MainActivity.this, "You just deleted "+ students.getText().toString() , Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					setMaster_Notification_Page();
				}
			});
			
			//Log Off Button
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v) 
				{
					setDefaultView();
				}
			});
		}
	}
	/**Finished
	 * Allows User to change some of their options
	 */
	public void setSettings_Page()
	{
		if(Currentuser == null)
		{
			Toast.makeText(MainActivity.this, "No one is logged in!" , Toast.LENGTH_SHORT).show();
			setDefaultView();
		}
		else
		{
			setContentView(R.layout.settings);
			
			Button Submit = (Button) findViewById(R.id.button1);	
			Button LogOut = (Button) findViewById(R.id.button2);
			Button Delete = (Button) findViewById(R.id.button3);			
			Button Back = (Button) findViewById(R.id.button4);
			
	        final EditText Password = (EditText) findViewById(R.id.editText1);
	        final EditText Passwordcheck = (EditText) findViewById(R.id.editText2);
	        
			Submit.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Changes the user's password
				public void onClick(View v) 
				{
					//TODO: Change the user's password
					if(Password.getText().toString().equals(Passwordcheck.getText().toString())) 				//check to see if passwords entered are equal
					{
						PutPassword newPass = new PutPassword();
						try {
							newPass.putPassword(Currentuser, Password.getText().toString());
							Toast.makeText(MainActivity.this, "You have just changed your password.", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
					else																						//Inform users that their passwords do not match each other
					{
						Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
					}
				}
			});
	        
			LogOut.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Logs the user out and brings back to sign-in page.
				public void onClick(View v) 
				{
					setDefaultView();
				}
			});
			
			Delete.setOnClickListener(new View.OnClickListener()
			{
				@Override
				// Deletes the user's account FOREVER!!!
				public void onClick(View v) 
				{
					// Delete the user's account FOREVER!!!
					DeleteUser delU = new DeleteUser();
					try {
						Toast.makeText(MainActivity.this, "You just deleted your account. Goodbye," + Currentuser.getUserName() + ".", Toast.LENGTH_SHORT).show();
						delU.deleteUser(Currentuser.getUserName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					// goes back to homepage and resets the username
					setDefaultView();
				}
			});
			
			Back.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(Currentuser.getType().equals(UserType.STUDENT))
					{
						//user is student, go to student page
						setClass_Selection_Page();
					}
					else
					{
						//user is teacher, go to teacher page
						setTeacher_Selection_Page();
					}
				}
			});
		}
	}
}

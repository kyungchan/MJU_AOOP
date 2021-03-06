package queries;

import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import classes.Attand;

public class AttandsQueries {
	private Connection connection = null;
	private PreparedStatement selectAllAttands = null;
	private PreparedStatement insertStudent = null;
	private PreparedStatement insertCourse = null;
	private PreparedStatement insertAttand = null;
	private PreparedStatement updateAttandByStuNumber = null;
	private String colName[] = { "StudentNum", "Week1_1", "Week1_2", "Week2_1", "Week2_2", "Week3_1", "Week3_2",
			"Week4_1", "Week4_2", "Week5_1", "Week5_2", "Week6_1", "Week6_2", "Week7_1", "Week7_2", "Week8_1",
			"Week8_2", "Week9_1", "Week9_2", "Week10_1", "Week10_2", "Week11_1", "Week11_2", "Week12_1", "Week12_2",
			"Week13_1", "Week13_2", "Week14_1", "Week14_2", "Week15_1", "Week15_2", "Week16_1", "Week16_2" };

	public AttandsQueries(String DATABASE_URL, String USERNAME, String PASSWORD) {
		try {
			connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			selectAllAttands = connection.prepareStatement("SELECT * FROM students NATURAL JOIN attands;");
			insertStudent = connection.prepareStatement("INSERT INTO students VALUES(?, ?, ?);");
			insertCourse = connection.prepareStatement("INSERT INTO courses VALUES(?,?,?,?,?,?,?,?,?);");
			insertAttand = connection.prepareStatement(
					"INSERT INTO attands VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			System.exit(1);
		}
	}

	public void insertAll(String stuName, String stuNumber, int stuGrade, int scoreAttand, int scoreMid, int scoreFinal,
			int scoreHomework, int scoreQuiz, int scorePPT, int scoreReport, int scoreEtc, String serialAttand) {
		try {
			insertStudent.setString(1, stuNumber);
			insertStudent.setString(2, stuName);
			insertStudent.setInt(3, stuGrade);
			insertStudent.execute();

			insertCourse.setString(1, stuNumber);
			insertCourse.setInt(2, scoreAttand);
			insertCourse.setInt(3, scoreMid);
			insertCourse.setInt(4, scoreFinal);
			insertCourse.setInt(5, scoreHomework);
			insertCourse.setInt(6, scoreQuiz);
			insertCourse.setInt(7, scorePPT);
			insertCourse.setInt(8, scoreReport);
			insertCourse.setInt(9, scoreEtc);
			insertCourse.execute();
			
			insertAttand.setString(1, stuNumber);
			for(int i = 0; i < 32; i++) {
				insertAttand.setInt(i+2, serialAttand.charAt(i) - 48);
			}
			insertAttand.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			close();
		}
	}

	public void deleteAll() {
		try {
			connection.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0;");
			connection.createStatement().execute("TRUNCATE attands");
			connection.createStatement().execute("TRUNCATE students");
			connection.createStatement().execute("TRUNCATE courses");
			connection.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1;");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			close();
		}
	}

	public int updateAttand(String studentNumber, int col, int value) {
		int result = 0;
		try {
			updateAttandByStuNumber = connection
					.prepareStatement("UPDATE attands SET " + colName[col] + " = ? WHERE studentNum = ?;");
			updateAttandByStuNumber.setInt(1, value);
			updateAttandByStuNumber.setString(2, studentNumber);
			result = updateAttandByStuNumber.executeUpdate();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			close();
		}
		return result;
	}

	public List<Attand> getAllAttands() {
		List<Attand> results = null;
		ResultSet resultSet = null;
		String serialize;
		try {
			resultSet = selectAllAttands.executeQuery();
			results = new ArrayList<Attand>();

			while (resultSet.next()) {
				serialize = "";
				for (int i = 1; i <= 16; i++) {
					for (int j = 1; j <= 2; j++) {
						serialize += (Integer.toString(resultSet.getInt("Week" + i + "_" + j)));
					}
				}
				results.add(new Attand(resultSet.getString("StudentName"), resultSet.getString("StudentNum"),
						resultSet.getInt("StudentGrade"), serialize));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				close();
			}
		}

		return results;
	}

	void close() {
		try {
			connection.close();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Momo.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class QuestionDAL extends ConnecttoXampp {

    public QuestionDAL() {
        super();
        this.connectDB();
    }

    public int addQuest(QuestionModel us) throws SQLException {
        String query = "INSERT INTO quest (idquest, question, answerA, answerB, answerC, answerD, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setInt(1, us.getIdquest());
        p.setString(2, us.getQuestion());
        p.setString(3, us.getAnswerA());
        p.setString(4, us.getAnswerB());
        p.setString(5, us.getAnswerC());
        p.setString(6, us.getAnswerD());
        p.setBoolean(7, us.isStatus());
        int result = p.executeUpdate();
        return result;
    }

    public int updateUser(QuestionModel us) throws SQLException {
        String query = "UPDATE quest SET idquest = ?,question = ?, answerA = ?, answerB = ?, answerC = ?, answerD = ?, status = ? WHERE idquest = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setInt(1, us.getIdquest());
        p.setString(2, us.getQuestion());
        p.setString(3, us.getAnswerA());
        p.setString(4, us.getAnswerB());
        p.setString(5, us.getAnswerC());
        p.setString(6, us.getAnswerD());
        p.setBoolean(7, us.isStatus());
        int result = p.executeUpdate();
        return result;
    }

    public ArrayList loadQuestion() throws SQLException {
        String query = "SELECT * FROM User";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<QuestionModel> questionList = new ArrayList();
        if (rs != null) {
            while (rs.next()) {
                QuestionModel us = new QuestionModel();
                us.setIdquest(rs.getInt("Question ID"));
                us.setQuestion(rs.getString("Question :"));
                us.setAnswerA(rs.getString("Answer A"));
                us.setAnswerB(rs.getString("Answer B"));
                us.setAnswerC(rs.getString("Answer C"));
                us.setAnswerD(rs.getString("Answer D"));
                questionList.add(us);
            }
        }
        return questionList;
    }
    public Boolean checkDuplicate(String question) throws SQLException {
        String query = "SELECT * FROM quest WHERE question = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setString(1, question);
        ResultSet rs = p.executeQuery();
        return rs.next();
    }
    public int setOnlOff(int idquest, boolean status) throws SQLException{
        String query = "UPDATE user SET status = ? Where UserID = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setBoolean(1, status);
        p.setInt(2, idquest);
        int rs = p.executeUpdate();
        return rs;
    }

    private void connectDB() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   
}

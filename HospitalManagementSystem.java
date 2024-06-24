package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

import static java.lang.Class.forName;

public class HospitalManagementSystem {
    private static final String url ="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="sql@jdbc123";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);

            while(true){
                System.out.println("-- HOSPITAL MANAGEMENT SYSTEM --");
                System.out.println("1.Add Patient");
                System.out.println("2.View Patient");
                System.out.println("3.View Doctor");
                System.out.println("4.Book Appointment");
                System.out.println("5.Exit");
                System.out.println("Enter Choice Number--> ");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        //Add Patient
                       patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view Patient
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //View Doctor
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appointment
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter Valid Choice ??? ");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

    }
        public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner){
            System.out.println("Enter Patient ID:  ");
            int patientId =scanner.nextInt();
            System.out.println("Enter Doctor ID:  ");
            int doctorId =scanner.nextInt();
            System.out.println("Enter Appointment Date in Format(YYYY-MM-DD):  ");
            String appointmentDate=scanner.next();
            if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
                if(checkDoctorAvailability(doctorId,appointmentDate,connection)){
                    String appointmentquery="INSERT INTO appointments(patient_id,docter_id,appointment_date) VALUES(?, ?, ?)";
                    try{
                        PreparedStatement preparedStatement=connection.prepareStatement(appointmentquery);
                        preparedStatement.setInt(1,patientId);
                        preparedStatement.setInt(2,doctorId);
                        preparedStatement.setString(3,appointmentDate);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if(rowsAffected>0){
                            System.out.println("Appointment Booked");
                        }else{
                            System.out.println("Failed to Book Appointment");
                        }
                    }catch(SQLException e){
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("Doctor is Not Available on this Date!!!");
                }

            }else{
                System.out.println("Either Doctor Or Patient Doesn't Exists ???");
            }
        }

    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE docter_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setInt(1,doctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
            return false;
    }
}

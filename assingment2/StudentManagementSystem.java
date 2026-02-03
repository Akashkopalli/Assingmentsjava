// A2
interface SportsParticipant {
    // Q6: Create an interface SportsParticipant
    void playSport();
}

interface CulturalParticipant {
    // Q6: Create another interface CulturalParticipant
    void performActivity();
}

abstract class Student {
    // Q1: Encapsulation - Make all data members private
    private int rollNumber;
    private String name;
    private int semester;

    // Q3: Usage of static Keyword - Add a static variable
    static String universityName;

    // Q4: Usage of final Keyword - Add a final variable
    final int MAX_SEMESTER = 8;

    // Q3: Initialize static variable using a static block
    static {
        universityName = "Global Tech University";
    }

    public Student(int rollNumber, String name, int semester) {
        this.rollNumber = rollNumber;
        this.name = name;
        setSemester(semester);
    }

    // Q1: Provide public getter and setter methods
    public int getRollNumber() { return rollNumber; }
    public void setRollNumber(int rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSemester() { return semester; }
    
    // Q1: Add validation inside setters
    public void setSemester(int semester) {
        if (semester > 0 && semester <= MAX_SEMESTER) {
            this.semester = semester;
        } else {
            System.out.println("Error: Semester must be between 1 and " + MAX_SEMESTER);
        }
    }

    // Q2: Abstraction - Declare an abstract method
    abstract double calculateGrade();

    // Q2: Create a concrete method to print student information
    public void displayDetails() {
        System.out.println("Roll No: " + rollNumber);
        System.out.println("Name: " + name);
        System.out.println("Semester: " + semester);
        System.out.println("University: " + universityName);
    }

    // Q3: Add a static method
    static void displayUniversityName() {
        System.out.println("University Name: " + universityName);
    }

    // Q4: Create a final method
    final void showRules() {
        System.out.println("Rule: Minimum 75% attendance is required.");
    }
}

// Q5: Inheritance - Create a class EngineeringStudent that extends Student
class EngineeringStudent extends Student {
    private double internalMarks;
    private double externalMarks;

    public EngineeringStudent(int rollNumber, String name, int semester, double internalMarks, double externalMarks) {
        super(rollNumber, name, semester);
        this.internalMarks = internalMarks;
        this.externalMarks = externalMarks;
    }

    // Q5: Override calculateGrade()
    @Override
    double calculateGrade() {
        return (internalMarks + externalMarks) / 2.0;
    }
}

// Q5: Create another class MedicalStudent that extends Student
class MedicalStudent extends Student {
    private double theoryMarks;
    private double practicalMarks;

    public MedicalStudent(int rollNumber, String name, int semester, double theoryMarks, double practicalMarks) {
        super(rollNumber, name, semester);
        this.theoryMarks = theoryMarks;
        this.practicalMarks = practicalMarks;
    }

    // Q5: Override calculateGrade() with a different grading logic
    @Override
    double calculateGrade() {
        return (theoryMarks * 0.4) + (practicalMarks * 0.6);
    }
}

// Q7: Multiple Inheritance - Create a class AllRounderStudent that extends Student and implements interfaces
class AllRounderStudent extends Student implements SportsParticipant, CulturalParticipant {
    
    private double gpa;

    public AllRounderStudent(int rollNumber, String name, int semester, double gpa) {
        super(rollNumber, name, semester);
        this.gpa = gpa;
    }

    // Q7: Implement calculateGrade()
    @Override
    double calculateGrade() {
        return gpa;
    }

    // Q7: Implement playSport()
    @Override
    public void playSport() {
        System.out.println(getName() + " is playing Basketball.");
    }

    // Q7: Implement performActivity()
    @Override
    public void performActivity() {
        System.out.println(getName() + " is performing in a Drama.");
    }
}

public class StudentManagementSystem {
    public static void main(String[] args) {
        // Q3: Show that static members belong to the class
        Student.displayUniversityName();

        // Q8: Polymorphism - Create a reference of type Student
        Student s1, s2, s3;

        // Q8: Assign it objects of EngineeringStudent, MedicalStudent, AllRounderStudent
        s1 = new EngineeringStudent(101, "Alice", 2, 85, 90);
        s2 = new MedicalStudent(102, "Bob", 3, 80, 95);
        s3 = new AllRounderStudent(103, "Charlie", 4, 9.2);

        // Q8: Call calculateGrade() and observe late binding
        System.out.println("Engineering Grade: " + s1.calculateGrade());
        System.out.println("Medical Grade: " + s2.calculateGrade());
        System.out.println("All Rounder Grade: " + s3.calculateGrade());

        // Q4: Usage of final method
        s1.showRules();

        // Q9: Interface Reference - Create a reference of type SportsParticipant
        SportsParticipant sp = new AllRounderStudent(104, "David", 2, 8.5);
        
        // Q9: Call playSport()
        sp.playSport();
    }
}
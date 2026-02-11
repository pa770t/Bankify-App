package bankify;

public class Agent {
    private int employeeId;
    private String fullName;
    private String role;
    private String gender;
    private String email;
    private String address;
    private String password;
    private String status;
    private java.sql.Timestamp createdAt;


    // Getters and setters
    public int getAgentId() { return employeeId; }
    public void setAgentId(int AgentId) { this.employeeId = AgentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}

    public String getGender() {return gender;}
    public void setGender(String gender) {this.gender = gender;}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    private String phoneNumber;
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
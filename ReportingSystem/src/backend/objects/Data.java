package backend.objects;

import backend.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {
    
    public static class UserRole {
        private int roleId;
        private String roleName;
        private String description;
        
        public UserRole() {}
        
        public UserRole(int roleId, String roleName, String description) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.description = description;
        }
        
        // Getters and Setters
        public int getRoleId() { return roleId; }
        public void setRoleId(int roleId) { this.roleId = roleId; }
        
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        // Database Functions
        public static List<UserRole> getAllRoles() {
            List<UserRole> roles = new ArrayList<>();
            String query = "SELECT * FROM User_Role_tb ORDER BY role_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    UserRole role = new UserRole(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("description")
                    );
                    roles.add(role);
                }
            } catch (SQLException e) {
                System.err.println("Error getting roles: " + e.getMessage());
            }
            return roles;
        }
    }
    
    public static class Sex {
        private int sexId;
        private String sexCode;
        private String sexName;
        
        public Sex() {}
        
        public Sex(int sexId, String sexCode, String sexName) {
            this.sexId = sexId;
            this.sexCode = sexCode;
            this.sexName = sexName;
        }
        
        // Getters and Setters
        public int getSexId() { return sexId; }
        public void setSexId(int sexId) { this.sexId = sexId; }
        
        public String getSexCode() { return sexCode; }
        public void setSexCode(String sexCode) { this.sexCode = sexCode; }
        
        public String getSexName() { return sexName; }
        public void setSexName(String sexName) { this.sexName = sexName; }
        
        // Database Functions
        public static List<Sex> getAllSex() {
            List<Sex> sexes = new ArrayList<>();
            String query = "SELECT * FROM Sex_tb ORDER BY sex_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Sex sex = new Sex(
                        rs.getInt("sex_id"),
                        rs.getString("sex_code"),
                        rs.getString("sex_name")
                    );
                    sexes.add(sex);
                }
            } catch (SQLException e) {
                System.err.println("Error getting sexes: " + e.getMessage());
            }
            return sexes;
        }
    }
    
    public static class VerificationReason {
        private int reasonId;
        private String reasonName;
        
        public VerificationReason() {}
        
        public VerificationReason(int reasonId, String reasonName) {
            this.reasonId = reasonId;
            this.reasonName = reasonName;
        }
        
        // Getters and Setters
        public int getReasonId() { return reasonId; }
        public void setReasonId(int reasonId) { this.reasonId = reasonId; }
        
        public String getReasonName() { return reasonName; }
        public void setReasonName(String reasonName) { this.reasonName = reasonName; }
        
        // Database Functions
        public static List<VerificationReason> getAllReasons() {
            List<VerificationReason> reasons = new ArrayList<>();
            String query = "SELECT * FROM Verification_Reason_tb ORDER BY reason_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    VerificationReason reason = new VerificationReason(
                        rs.getInt("reason_id"),
                        rs.getString("reason_name")
                    );
                    reasons.add(reason);
                }
            } catch (SQLException e) {
                System.err.println("Error getting reasons: " + e.getMessage());
            }
            return reasons;
        }
    }
    
    public static class RequestStatus {
        private int statusId;
        private String statusName;
        
        public RequestStatus() {}
        
        public RequestStatus(int statusId, String statusName) {
            this.statusId = statusId;
            this.statusName = statusName;
        }
        
        // Getters and Setters
        public int getStatusId() { return statusId; }
        public void setStatusId(int statusId) { this.statusId = statusId; }
        
        public String getStatusName() { return statusName; }
        public void setStatusName(String statusName) { this.statusName = statusName; }
        
        // Database Functions
        public static List<RequestStatus> getAllStatus() {
            List<RequestStatus> statuses = new ArrayList<>();
            String query = "SELECT * FROM Request_Status_tb ORDER BY status_id";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    RequestStatus status = new RequestStatus(
                        rs.getInt("status_id"),
                        rs.getString("status_name")
                    );
                    statuses.add(status);
                }
            } catch (SQLException e) {
                System.err.println("Error getting statuses: " + e.getMessage());
            }
            return statuses;
        }
    }
    
    public static class User {
        private int userId;
        private String firstName;
        private String lastName;
        private String username;
        private String passwordHash;
        private int roleId;
        private boolean isActive;
        private Timestamp createdAt;
        
        public User() {}
        
        public User(int userId, String firstName, String lastName, String username, 
                   String passwordHash, int roleId, boolean isActive, Timestamp createdAt) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.passwordHash = passwordHash;
            this.roleId = roleId;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }
        
        // Getters and Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        
        public int getRoleId() { return roleId; }
        public void setRoleId(int roleId) { this.roleId = roleId; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public String getFullName() { return firstName + " " + lastName; }
        
        // Database Functions
        public static User authenticate(String username, String password) {
            String query = "SELECT * FROM User_tb WHERE username = ? AND password_hash = ? AND is_active = true";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getInt("role_id"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error authenticating user: " + e.getMessage());
            }
            return null;
        }
        
        public static List<User> getAllUsers() {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM User_tb ORDER BY first_name, last_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getInt("role_id"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at")
                    );
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users: " + e.getMessage());
            }
            return users;
        }
        
        public static boolean addUser(User user) {
            String query = "INSERT INTO User_tb (first_name, last_name, username, password_hash, role_id) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getUsername());
                stmt.setString(4, user.getPasswordHash());
                stmt.setInt(5, user.getRoleId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateUser(User user) {
            String query = "UPDATE User_tb SET first_name = ?, last_name = ?, username = ?, " +
                          "role_id = ?, is_active = ? WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getFirstName());
                stmt.setString(2, user.getLastName());
                stmt.setString(3, user.getUsername());
                stmt.setInt(4, user.getRoleId());
                stmt.setBoolean(5, user.isActive());
                stmt.setInt(6, user.getUserId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating user: " + e.getMessage());
                return false;
            }
        }
        
        public static User getUserById(int userId) {
            String query = "SELECT * FROM User_tb WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getInt("role_id"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting user: " + e.getMessage());
            }
            return null;
        }
    }
    
    public static class Citizen {
        private int citizenId;
        private String firstName;
        private String lastName;
        private Date dateOfBirth;
        private String placeOfBirth;
        private int sexId;
        private String registryNumber;
        private Timestamp createdAt;
        
        public Citizen() {}
        
        public Citizen(int citizenId, String firstName, String lastName, Date dateOfBirth,
                      String placeOfBirth, int sexId, String registryNumber, Timestamp createdAt) {
            this.citizenId = citizenId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
            this.placeOfBirth = placeOfBirth;
            this.sexId = sexId;
            this.registryNumber = registryNumber;
            this.createdAt = createdAt;
        }
        
        // Getters and Setters
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Date getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public String getPlaceOfBirth() { return placeOfBirth; }
        public void setPlaceOfBirth(String placeOfBirth) { this.placeOfBirth = placeOfBirth; }
        
        public int getSexId() { return sexId; }
        public void setSexId(int sexId) { this.sexId = sexId; }
        
        public String getRegistryNumber() { return registryNumber; }
        public void setRegistryNumber(String registryNumber) { this.registryNumber = registryNumber; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public String getFullName() { return firstName + " " + lastName; }
        
        // Database Functions
        public static List<Citizen> getAllCitizens() {
            List<Citizen> citizens = new ArrayList<>();
            String query = "SELECT * FROM Citizen_tb ORDER BY last_name, first_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Citizen citizen = new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth"),
                        rs.getString("place_of_birth"),
                        rs.getInt("sex_id"),
                        rs.getString("registry_number"),
                        rs.getTimestamp("created_at")
                    );
                    citizens.add(citizen);
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizens: " + e.getMessage());
            }
            return citizens;
        }
        
        public static List<Citizen> searchCitizens(String searchTerm) {
            List<Citizen> citizens = new ArrayList<>();
            String query = "SELECT * FROM Citizen_tb " +
                          "WHERE first_name LIKE ? OR last_name LIKE ? OR registry_number LIKE ? " +
                          "ORDER BY last_name, first_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Citizen citizen = new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth"),
                        rs.getString("place_of_birth"),
                        rs.getInt("sex_id"),
                        rs.getString("registry_number"),
                        rs.getTimestamp("created_at")
                    );
                    citizens.add(citizen);
                }
            } catch (SQLException e) {
                System.err.println("Error searching citizens: " + e.getMessage());
            }
            return citizens;
        }
        
        public static boolean addCitizen(Citizen citizen) {
            String query = "INSERT INTO Citizen_tb (first_name, last_name, date_of_birth, " +
                          "place_of_birth, sex_id, registry_number) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, citizen.getFirstName());
                stmt.setString(2, citizen.getLastName());
                stmt.setDate(3, citizen.getDateOfBirth());
                stmt.setString(4, citizen.getPlaceOfBirth());
                stmt.setInt(5, citizen.getSexId());
                stmt.setString(6, citizen.getRegistryNumber());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding citizen: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateCitizen(Citizen citizen) {
            String query = "UPDATE Citizen_tb SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                          "place_of_birth = ?, sex_id = ?, registry_number = ? WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, citizen.getFirstName());
                stmt.setString(2, citizen.getLastName());
                stmt.setDate(3, citizen.getDateOfBirth());
                stmt.setString(4, citizen.getPlaceOfBirth());
                stmt.setInt(5, citizen.getSexId());
                stmt.setString(6, citizen.getRegistryNumber());
                stmt.setInt(7, citizen.getCitizenId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating citizen: " + e.getMessage());
                return false;
            }
        }
        
        public static Citizen getCitizenById(int citizenId) {
            String query = "SELECT * FROM Citizen_tb WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth"),
                        rs.getString("place_of_birth"),
                        rs.getInt("sex_id"),
                        rs.getString("registry_number"),
                        rs.getTimestamp("created_at")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizen: " + e.getMessage());
            }
            return null;
        }
        
        public static int getTotalCitizens() {
            String query = "SELECT COUNT(*) as total FROM Citizen_tb";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting total citizens: " + e.getMessage());
            }
            return 0;
        }
    }
    
    public static class VerificationRequest {
        private int requestId;
        private int citizenId;
        private int requesterId;
        private int reasonId;
        private int statusId;
        private Timestamp requestDatetime;
        private String responseDetails;
        
        public VerificationRequest() {}
        
        public VerificationRequest(int requestId, int citizenId, int requesterId, int reasonId,
                                 int statusId, Timestamp requestDatetime, String responseDetails) {
            this.requestId = requestId;
            this.citizenId = citizenId;
            this.requesterId = requesterId;
            this.reasonId = reasonId;
            this.statusId = statusId;
            this.requestDatetime = requestDatetime;
            this.responseDetails = responseDetails;
        }
        
        // Getters and Setters
        public int getRequestId() { return requestId; }
        public void setRequestId(int requestId) { this.requestId = requestId; }
        
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public int getRequesterId() { return requesterId; }
        public void setRequesterId(int requesterId) { this.requesterId = requesterId; }
        
        public int getReasonId() { return reasonId; }
        public void setReasonId(int reasonId) { this.reasonId = reasonId; }
        
        public int getStatusId() { return statusId; }
        public void setStatusId(int statusId) { this.statusId = statusId; }
        
        public Timestamp getRequestDatetime() { return requestDatetime; }
        public void setRequestDatetime(Timestamp requestDatetime) { this.requestDatetime = requestDatetime; }
        
        public String getResponseDetails() { return responseDetails; }
        public void setResponseDetails(String responseDetails) { this.responseDetails = responseDetails; }
        
        // Database Functions
        public static List<VerificationRequest> getAllRequests() {
            List<VerificationRequest> requests = new ArrayList<>();
            String query = "SELECT * FROM Verification_Request_tb ORDER BY request_datetime DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    VerificationRequest request = new VerificationRequest(
                        rs.getInt("request_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("requester_id"),
                        rs.getInt("reason_id"),
                        rs.getInt("status_id"),
                        rs.getTimestamp("request_datetime"),
                        rs.getString("response_details")
                    );
                    requests.add(request);
                }
            } catch (SQLException e) {
                System.err.println("Error getting verification requests: " + e.getMessage());
            }
            return requests;
        }
        
        public static List<VerificationRequest> getRecentRequests(int limit) {
            List<VerificationRequest> requests = new ArrayList<>();
            String query = "SELECT * FROM Verification_Request_tb ORDER BY request_datetime DESC LIMIT ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    VerificationRequest request = new VerificationRequest(
                        rs.getInt("request_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("requester_id"),
                        rs.getInt("reason_id"),
                        rs.getInt("status_id"),
                        rs.getTimestamp("request_datetime"),
                        rs.getString("response_details")
                    );
                    requests.add(request);
                }
            } catch (SQLException e) {
                System.err.println("Error getting recent requests: " + e.getMessage());
            }
            return requests;
        }
        
        public static int getPendingRequestsCount() {
            String query = "SELECT COUNT(*) as pending_count FROM Verification_Request_tb WHERE status_id = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt("pending_count");
                }
            } catch (SQLException e) {
                System.err.println("Error getting pending requests count: " + e.getMessage());
            }
            return 0;
        }
        
        public static boolean addRequest(VerificationRequest request) {
            String query = "INSERT INTO Verification_Request_tb (citizen_id, requester_id, reason_id) " +
                          "VALUES (?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, request.getCitizenId());
                stmt.setInt(2, request.getRequesterId());
                stmt.setInt(3, request.getReasonId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding verification request: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateRequestStatus(int requestId, int statusId, String responseDetails) {
            String query = "UPDATE Verification_Request_tb SET status_id = ?, response_details = ? " +
                          "WHERE request_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, statusId);
                stmt.setString(2, responseDetails);
                stmt.setInt(3, requestId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating request status: " + e.getMessage());
                return false;
            }
        }
        
        public static VerificationRequest getRequestById(int requestId) {
            String query = "SELECT * FROM Verification_Request_tb WHERE request_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, requestId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new VerificationRequest(
                        rs.getInt("request_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("requester_id"),
                        rs.getInt("reason_id"),
                        rs.getInt("status_id"),
                        rs.getTimestamp("request_datetime"),
                        rs.getString("response_details")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting request: " + e.getMessage());
            }
            return null;
        }
    }
    
    public static class ActivityLog {
        private int logId;
        private int userId;
        private String action;
        private Timestamp actionDatetime;
        
        public ActivityLog() {}
        
        public ActivityLog(int logId, int userId, String action, Timestamp actionDatetime) {
            this.logId = logId;
            this.userId = userId;
            this.action = action;
            this.actionDatetime = actionDatetime;
        }
        
        // Getters and Setters
        public int getLogId() { return logId; }
        public void setLogId(int logId) { this.logId = logId; }
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Timestamp getActionDatetime() { return actionDatetime; }
        public void setActionDatetime(Timestamp actionDatetime) { this.actionDatetime = actionDatetime; }
        
        // Database Functions
        public static List<ActivityLog> getAllActivityLogs() {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM Activity_Log_tb ORDER BY action_datetime DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getTimestamp("action_datetime")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting activity logs: " + e.getMessage());
            }
            return logs;
        }
        
        public static boolean logActivity(int userId, String action) {
            String query = "INSERT INTO Activity_Log_tb (user_id, action) VALUES (?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                stmt.setString(2, action);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error logging activity: " + e.getMessage());
                return false;
            }
        }
        
        public static List<ActivityLog> getRecentActivities(int limit) {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM Activity_Log_tb ORDER BY action_datetime DESC LIMIT ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getTimestamp("action_datetime")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting recent activities: " + e.getMessage());
            }
            return logs;
        }
    }
}
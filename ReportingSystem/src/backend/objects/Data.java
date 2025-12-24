package backend.objects;

import backend.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {
    
    public static class Role {
        private int roleId;
        private String roleName;
        private String roleCode;
        private String description;
        private Date createdDate;
        private boolean isActive;
        
        public Role() {}
        
        public Role(int roleId, String roleName, String roleCode, String description, 
                   Date createdDate, boolean isActive) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.roleCode = roleCode;
            this.description = description;
            this.createdDate = createdDate;
            this.isActive = isActive;
        }
        
        // Getters and Setters
        public int getRoleId() { return roleId; }
        public void setRoleId(int roleId) { this.roleId = roleId; }
        
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        
        public String getRoleCode() { return roleCode; }
        public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        // Database Functions
        public static List<Role> getAllRoles() {
            List<Role> roles = new ArrayList<>();
            String query = "SELECT * FROM roles WHERE is_active = 1 ORDER BY role_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code"),
                        rs.getString("description"),
                        rs.getDate("created_date"),
                        rs.getBoolean("is_active")
                    );
                    roles.add(role);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all roles: " + e.getMessage());
            }
            return roles;
        }
        
        public static Role getRoleById(int roleId) {
            String query = "SELECT * FROM roles WHERE role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, roleId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code"),
                        rs.getString("description"),
                        rs.getDate("created_date"),
                        rs.getBoolean("is_active")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting role: " + e.getMessage());
            }
            return null;
        }
        
        public static Role getRoleByCode(String roleCode) {
            String query = "SELECT * FROM roles WHERE role_code = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, roleCode);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code"),
                        rs.getString("description"),
                        rs.getDate("created_date"),
                        rs.getBoolean("is_active")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting role by code: " + e.getMessage());
            }
            return null;
        }
        
        public static boolean addRole(Role role) {
            String query = "INSERT INTO roles (role_name, role_code, description, created_date, is_active) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, role.getRoleName());
                stmt.setString(2, role.getRoleCode());
                stmt.setString(3, role.getDescription());
                stmt.setDate(4, role.getCreatedDate());
                stmt.setBoolean(5, role.isActive());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding role: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateRole(Role role) {
            String query = "UPDATE roles SET role_name = ?, role_code = ?, description = ?, " +
                          "created_date = ?, is_active = ? WHERE role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, role.getRoleName());
                stmt.setString(2, role.getRoleCode());
                stmt.setString(3, role.getDescription());
                stmt.setDate(4, role.getCreatedDate());
                stmt.setBoolean(5, role.isActive());
                stmt.setInt(6, role.getRoleId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating role: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean deleteRole(int roleId) {
            String query = "UPDATE roles SET is_active = 0 WHERE role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, roleId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting role: " + e.getMessage());
                return false;
            }
        }
        
        public static List<Role> searchRoles(String searchTerm) {
            List<Role> roles = new ArrayList<>();
            String query = "SELECT * FROM roles WHERE role_name LIKE ? OR role_code LIKE ? OR description LIKE ? " +
                          "ORDER BY role_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_name"),
                        rs.getString("role_code"),
                        rs.getString("description"),
                        rs.getDate("created_date"),
                        rs.getBoolean("is_active")
                    );
                    roles.add(role);
                }
            } catch (SQLException e) {
                System.err.println("Error searching roles: " + e.getMessage());
            }
            return roles;
        }
    }
    
    public static class UserRole {
        private int userRoleId;
        private int userId;
        private int roleId;
        private Date assignedDate;
        
        private Role role; // Reference to role details
        private User user; // Reference to user details
        
        public UserRole() {}
        
        public UserRole(int userRoleId, int userId, int roleId, Date assignedDate) {
            this.userRoleId = userRoleId;
            this.userId = userId;
            this.roleId = roleId;
            this.assignedDate = assignedDate;
        }
        
        // Getters and Setters
        public int getUserRoleId() { return userRoleId; }
        public void setUserRoleId(int userRoleId) { this.userRoleId = userRoleId; }
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public int getRoleId() { return roleId; }
        public void setRoleId(int roleId) { this.roleId = roleId; }
        
        public Date getAssignedDate() { return assignedDate; }
        public void setAssignedDate(Date assignedDate) { this.assignedDate = assignedDate; }
        
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        
        // Database Functions
        public static List<UserRole> getUserRolesByUserId(int userId) {
            List<UserRole> userRoles = new ArrayList<>();
            String query = "SELECT ur.*, r.role_name, r.role_code FROM user_roles ur " +
                          "JOIN roles r ON ur.role_id = r.role_id " +
                          "WHERE ur.user_id = ? AND r.is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    UserRole userRole = new UserRole(
                        rs.getInt("user_role_id"),
                        rs.getInt("user_id"),
                        rs.getInt("role_id"),
                        rs.getDate("assigned_date")
                    );
                    
                    // Create role object
                    Role role = new Role();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    role.setRoleCode(rs.getString("role_code"));
                    userRole.setRole(role);
                    
                    userRoles.add(userRole);
                }
            } catch (SQLException e) {
                System.err.println("Error getting user roles: " + e.getMessage());
            }
            return userRoles;
        }
        
        public static List<UserRole> getUsersByRoleId(int roleId) {
            List<UserRole> userRoles = new ArrayList<>();
            String query = "SELECT ur.*, u.username, u.fname, u.lname FROM user_roles ur " +
                          "JOIN users u ON ur.user_id = u.user_id " +
                          "WHERE ur.role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, roleId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    UserRole userRole = new UserRole(
                        rs.getInt("user_role_id"),
                        rs.getInt("user_id"),
                        rs.getInt("role_id"),
                        rs.getDate("assigned_date")
                    );
                    
                    // Create user object
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFname(rs.getString("fname"));
                    user.setLname(rs.getString("lname"));
                    userRole.setUser(user);
                    
                    userRoles.add(userRole);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users by role: " + e.getMessage());
            }
            return userRoles;
        }
        
        public static boolean assignRoleToUser(int userId, int roleId) {
            // Check if role already assigned
            String checkQuery = "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, roleId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    // Role already assigned
                    return true;
                }
                
                // Assign role
                String insertQuery = "INSERT INTO user_roles (user_id, role_id, assigned_date) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, roleId);
                    insertStmt.setDate(3, new Date(System.currentTimeMillis()));
                    
                    return insertStmt.executeUpdate() > 0;
                }
            } catch (SQLException e) {
                System.err.println("Error assigning role to user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean removeRoleFromUser(int userId, int roleId) {
            String query = "DELETE FROM user_roles WHERE user_id = ? AND role_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                stmt.setInt(2, roleId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error removing role from user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateUserRoles(int userId, List<Integer> roleIds) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                // Remove existing roles
                String deleteQuery = "DELETE FROM user_roles WHERE user_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.executeUpdate();
                }
                
                // Add new roles
                if (roleIds != null && !roleIds.isEmpty()) {
                    String insertQuery = "INSERT INTO user_roles (user_id, role_id, assigned_date) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        Date currentDate = new Date(System.currentTimeMillis());
                        for (int roleId : roleIds) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, roleId);
                            insertStmt.setDate(3, currentDate);
                            insertStmt.addBatch();
                        }
                        insertStmt.executeBatch();
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                System.err.println("Error updating user roles: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean hasRole(int userId, String roleCode) {
            String query = "SELECT COUNT(*) FROM user_roles ur " +
                          "JOIN roles r ON ur.role_id = r.role_id " +
                          "WHERE ur.user_id = ? AND r.role_code = ? AND r.is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                stmt.setString(2, roleCode);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                System.err.println("Error checking user role: " + e.getMessage());
            }
            return false;
        }
        
        public static List<String> getUserRoleNames(int userId) {
            List<String> roleNames = new ArrayList<>();
            String query = "SELECT r.role_name FROM user_roles ur " +
                          "JOIN roles r ON ur.role_id = r.role_id " +
                          "WHERE ur.user_id = ? AND r.is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    roleNames.add(rs.getString("role_name"));
                }
            } catch (SQLException e) {
                System.err.println("Error getting user role names: " + e.getMessage());
            }
            return roleNames;
        }
        
        public static List<String> getUserRoleCodes(int userId) {
            List<String> roleCodes = new ArrayList<>();
            String query = "SELECT r.role_code FROM user_roles ur " +
                          "JOIN roles r ON ur.role_id = r.role_id " +
                          "WHERE ur.user_id = ? AND r.is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    roleCodes.add(rs.getString("role_code"));
                }
            } catch (SQLException e) {
                System.err.println("Error getting user role codes: " + e.getMessage());
            }
            return roleCodes;
        }
    }
    
    // User class for users table (unchanged)
    public static class User {
        private int userId;
        private String fname;
        private String mname;
        private String lname;
        private String username;
        private String password; // Changed to String for hashed passwords
        private String phone;
        private String email;
        private Date createdDate;
        private Date lastLogin;
        private boolean isActive;
        
        // For backward compatibility - stores primary role name
        private String role;
        private List<String> roles; // NEW: List of all role names
        private List<String> roleCodes; // NEW: List of all role codes
        
        public User() {}
        
        public User(int userId, String fname, String mname, String lname, String username, String password, 
                   String phone, String email, Date createdDate, Date lastLogin, boolean isActive) {
            this.userId = userId;
            this.fname = fname;
            this.mname = mname;
            this.lname = lname;
            this.username = username;
            this.password = password;
            this.phone = phone;
            this.email = email;
            this.createdDate = createdDate;
            this.lastLogin = lastLogin;
            this.isActive = isActive;
        }
        
        // Getters and Setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getFname() { return fname; }
        public void setFname(String fname) { this.fname = fname; }
        
        public String getMname() { return mname; }
        public void setMname(String mname) { this.mname = mname; }
        
        public String getLname() { return lname; }
        public void setLname(String lname) { this.lname = lname; }
        
        public String getFullName() { 
            return (fname + " " + (mname != null && !mname.isEmpty() ? mname + " " : "") + lname).trim();
        }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        // For backward compatibility
        public String getRole() { 
            if (role == null && roles != null && !roles.isEmpty()) {
                return roles.get(0); // Return first role as primary
            }
            return role; 
        }
        public void setRole(String role) { this.role = role; }
        
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        
        public List<String> getRoleCodes() { return roleCodes; }
        public void setRoleCodes(List<String> roleCodes) { this.roleCodes = roleCodes; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
        
        public Date getLastLogin() { return lastLogin; }
        public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }
        
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        // Database Functions (UPDATED)
        public static User authenticate(String username, String password) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                stmt.setString(2, password); // In production, use password hashing
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date"),
                        rs.getDate("last_login"),
                        rs.getBoolean("is_active")
                    );
                    
                    // Load user roles
                    loadUserRoles(user);
                    
                    return user;
                }
            } catch (SQLException e) {
                System.err.println("Error authenticating user: " + e.getMessage());
            }
            return null;
        }
        
        // Helper method to load user roles
        private static void loadUserRoles(User user) {
            if (user == null) return;
            
            List<String> roleNames = UserRole.getUserRoleNames(user.getUserId());
            List<String> roleCodes = UserRole.getUserRoleCodes(user.getUserId());
            
            user.setRoles(roleNames);
            user.setRoleCodes(roleCodes);
            
            // Set primary role for backward compatibility
            if (!roleNames.isEmpty()) {
                user.setRole(roleNames.get(0));
            }
        }
        
        public static List<User> getAllUsers() {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date"),
                        rs.getDate("last_login"),
                        rs.getBoolean("is_active")
                    );
                    
                    // Load user roles
                    loadUserRoles(user);
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users: " + e.getMessage());
            }
            return users;
        }
        
        public static boolean addUser(User user) {
            String query = "INSERT INTO users (fname, mname, lname, username, password, phone, email, " +
                          "created_date, last_login, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, user.getFname());
                stmt.setString(2, user.getMname());
                stmt.setString(3, user.getLname());
                stmt.setString(4, user.getUsername());
                stmt.setString(5, user.getPassword()); // Should be hashed
                stmt.setString(6, user.getPhone());
                stmt.setString(7, user.getEmail());
                stmt.setDate(8, user.getCreatedDate());
                stmt.setDate(9, user.getLastLogin());
                stmt.setBoolean(10, user.isActive());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // Get the generated user ID
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            
                            // If role was specified in old way, assign default role
                            if (user.getRole() != null && !user.getRole().isEmpty()) {
                                Role role = Role.getRoleByCode(user.getRole());
                                if (role != null) {
                                    UserRole.assignRoleToUser(userId, role.getRoleId());
                                }
                            }
                            return true;
                        }
                    }
                }
                return false;
            } catch (SQLException e) {
                System.err.println("Error adding user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateUser(User user) {
            String query = "UPDATE users SET fname = ?, mname = ?, lname = ?, username = ?, password = ?, " +
                          "phone = ?, email = ?, last_login = ?, is_active = ? WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getFname());
                stmt.setString(2, user.getMname());
                stmt.setString(3, user.getLname());
                stmt.setString(4, user.getUsername());
                stmt.setString(5, user.getPassword());
                stmt.setString(6, user.getPhone());
                stmt.setString(7, user.getEmail());
                stmt.setDate(8, user.getLastLogin());
                stmt.setBoolean(9, user.isActive());
                stmt.setInt(10, user.getUserId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating user: " + e.getMessage());
                return false;
            }
        }
        
        public static User getUserById(int userId) {
            String query = "SELECT * FROM users WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date"),
                        rs.getDate("last_login"),
                        rs.getBoolean("is_active")
                    );
                    
                    // Load user roles
                    loadUserRoles(user);
                    return user;
                }
            } catch (SQLException e) {
                System.err.println("Error getting user: " + e.getMessage());
            }
            return null;
        }
        
        public static int getTotalUsers() {
            String query = "SELECT COUNT(*) as total FROM users WHERE is_active = 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting total users: " + e.getMessage());
            }
            return 0;
        }
        
        // Updated to use roles system
        public static List<User> getUsersByRole(String roleName) {
            List<User> users = new ArrayList<>();
            String query = "SELECT u.* FROM users u " +
                          "JOIN user_roles ur ON u.user_id = ur.user_id " +
                          "JOIN roles r ON ur.role_id = r.role_id " +
                          "WHERE r.role_name = ? AND u.is_active = 1 AND r.is_active = 1 " +
                          "ORDER BY u.lname, u.fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, roleName);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date"),
                        rs.getDate("last_login"),
                        rs.getBoolean("is_active")
                    );
                    
                    // Load user roles
                    loadUserRoles(user);
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users by role: " + e.getMessage());
            }
            return users;
        }
        
        public static List<User> searchUsers(String searchTerm) {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users WHERE (fname LIKE ? OR mname LIKE ? OR lname LIKE ? " +
                          "OR username LIKE ? OR email LIKE ? OR phone LIKE ?) AND is_active = 1 " +
                          "ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                stmt.setString(4, likeTerm);
                stmt.setString(5, likeTerm);
                stmt.setString(6, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date"),
                        rs.getDate("last_login"),
                        rs.getBoolean("is_active")
                    );
                    
                    // Load user roles
                    loadUserRoles(user);
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error searching users: " + e.getMessage());
            }
            return users;
        }
        
        public static boolean updateLastLogin(int userId) {
            String query = "UPDATE users SET last_login = ? WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setDate(1, new Date(System.currentTimeMillis()));
                stmt.setInt(2, userId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating last login: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean deactivateUser(int userId) {
            String query = "UPDATE users SET is_active = 0 WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deactivating user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean activateUser(int userId) {
            String query = "UPDATE users SET is_active = 1 WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error activating user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean checkUsernameExists(String username, Integer excludeUserId) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            if (excludeUserId != null) {
                query += " AND user_id != ?";
            }
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                if (excludeUserId != null) {
                    stmt.setInt(2, excludeUserId);
                }
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                System.err.println("Error checking username: " + e.getMessage());
            }
            return false;
        }
        
        public static boolean checkEmailExists(String email, Integer excludeUserId) {
            String query = "SELECT COUNT(*) FROM users WHERE email = ?";
            if (excludeUserId != null) {
                query += " AND user_id != ?";
            }
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, email);
                if (excludeUserId != null) {
                    stmt.setInt(2, excludeUserId);
                }
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                System.err.println("Error checking email: " + e.getMessage());
            }
            return false;
        }
    }
    
    // Address class for addresses table (UPDATED with barangay and address_line)
    public static class Address {
        private int addressId;
        private int citizenId;
        private String streetAddress;
        private String barangay; // NEW FIELD
        private String addressLine; // NEW FIELD
        private String city;
        private String stateProvince;
        private String zipPostalCode;
        private String country;

        public Address() {}

        public Address(int addressId, int citizenId, String streetAddress, String barangay, 
                      String addressLine, String city, String stateProvince, 
                      String zipPostalCode, String country) {
            this.addressId = addressId;
            this.citizenId = citizenId;
            this.streetAddress = streetAddress;
            this.barangay = barangay;
            this.addressLine = addressLine;
            this.city = city;
            this.stateProvince = stateProvince;
            this.zipPostalCode = zipPostalCode;
            this.country = country;
        }

        // Getters and Setters
        public int getAddressId() { return addressId; }
        public void setAddressId(int addressId) { this.addressId = addressId; }

        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }

        public String getStreetAddress() { return streetAddress; }
        public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

        public String getBarangay() { return barangay; } // NEW GETTER
        public void setBarangay(String barangay) { this.barangay = barangay; } // NEW SETTER

        public String getAddressLine() { return addressLine; } // NEW GETTER
        public void setAddressLine(String addressLine) { this.addressLine = addressLine; } // NEW SETTER

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getStateProvince() { return stateProvince; }
        public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }

        public String getZipPostalCode() { return zipPostalCode; }
        public void setZipPostalCode(String zipPostalCode) { this.zipPostalCode = zipPostalCode; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getFullAddress() {
            StringBuilder address = new StringBuilder();

            if (streetAddress != null && !streetAddress.isEmpty()) {
                address.append(streetAddress);
            }

            if (addressLine != null && !addressLine.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(addressLine);
            }

            if (barangay != null && !barangay.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append("Brgy. ").append(barangay);
            }

            if (city != null && !city.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(city);
            }

            if (stateProvince != null && !stateProvince.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(stateProvince);
            }

            if (zipPostalCode != null && !zipPostalCode.isEmpty()) {
                if (address.length() > 0) address.append(" ");
                address.append(zipPostalCode);
            }

            if (country != null && !country.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(country);
            }

            return address.toString();
        }

        public String getConciseAddress() {
            // Returns a shorter version of the address
            StringBuilder address = new StringBuilder();

            if (barangay != null && !barangay.isEmpty()) {
                address.append("Brgy. ").append(barangay);
            }

            if (city != null && !city.isEmpty()) {
                if (address.length() > 0) address.append(", ");
                address.append(city);
            }

            return address.toString();
        }

        // Database Functions (UPDATED)
        public static Address getAddressByCitizenId(int citizenId) {
            String query = "SELECT * FROM addresses WHERE citizen_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new Address(
                        rs.getInt("address_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("street_address"),
                        rs.getString("barangay"), // NEW FIELD
                        rs.getString("address_line"), // NEW FIELD
                        rs.getString("city"),
                        rs.getString("state_province"),
                        rs.getString("zip_postal_code"),
                        rs.getString("country")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting address by citizen ID: " + e.getMessage());
            }
            return null;
        }

        public static Address getAddressById(int addressId) {
            String query = "SELECT * FROM addresses WHERE address_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, addressId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new Address(
                        rs.getInt("address_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("street_address"),
                        rs.getString("barangay"), // NEW FIELD
                        rs.getString("address_line"), // NEW FIELD
                        rs.getString("city"),
                        rs.getString("state_province"),
                        rs.getString("zip_postal_code"),
                        rs.getString("country")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting address: " + e.getMessage());
            }
            return null;
        }

        public static boolean addAddress(Address address) {
            String query = "INSERT INTO addresses (citizen_id, street_address, barangay, address_line, " +
                          "city, state_province, zip_postal_code, country) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, address.getCitizenId());
                stmt.setString(2, address.getStreetAddress());
                stmt.setString(3, address.getBarangay()); // NEW FIELD
                stmt.setString(4, address.getAddressLine()); // NEW FIELD
                stmt.setString(5, address.getCity());
                stmt.setString(6, address.getStateProvince());
                stmt.setString(7, address.getZipPostalCode());
                stmt.setString(8, address.getCountry());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding address: " + e.getMessage());
                return false;
            }
        }

        public static boolean updateAddress(Address address) {
            String query = "UPDATE addresses SET street_address = ?, barangay = ?, address_line = ?, " +
                          "city = ?, state_province = ?, zip_postal_code = ?, country = ? WHERE address_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, address.getStreetAddress());
                stmt.setString(2, address.getBarangay()); // NEW FIELD
                stmt.setString(3, address.getAddressLine()); // NEW FIELD
                stmt.setString(4, address.getCity());
                stmt.setString(5, address.getStateProvince());
                stmt.setString(6, address.getZipPostalCode());
                stmt.setString(7, address.getCountry());
                stmt.setInt(8, address.getAddressId());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating address: " + e.getMessage());
                return false;
            }
        }

        public static boolean updateAddressByCitizenId(int citizenId, Address address) {
            String query = "UPDATE addresses SET street_address = ?, barangay = ?, address_line = ?, " +
                          "city = ?, state_province = ?, zip_postal_code = ?, country = ? WHERE citizen_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, address.getStreetAddress());
                stmt.setString(2, address.getBarangay()); // NEW FIELD
                stmt.setString(3, address.getAddressLine()); // NEW FIELD
                stmt.setString(4, address.getCity());
                stmt.setString(5, address.getStateProvince());
                stmt.setString(6, address.getZipPostalCode());
                stmt.setString(7, address.getCountry());
                stmt.setInt(8, citizenId);

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating address by citizen ID: " + e.getMessage());
                return false;
            }
        }

        public static boolean deleteAddress(int addressId) {
            String query = "DELETE FROM addresses WHERE address_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, addressId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting address: " + e.getMessage());
                return false;
            }
        }

        public static boolean deleteAddressByCitizenId(int citizenId) {
            String query = "DELETE FROM addresses WHERE citizen_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting address by citizen ID: " + e.getMessage());
                return false;
            }
        }

        // NEW: Search addresses by barangay
        public static List<Address> getAddressesByBarangay(String barangay) {
            List<Address> addresses = new ArrayList<>();
            String query = "SELECT * FROM addresses WHERE barangay LIKE ? ORDER BY city, street_address";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, "%" + barangay + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Address address = new Address(
                        rs.getInt("address_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("street_address"),
                        rs.getString("barangay"),
                        rs.getString("address_line"),
                        rs.getString("city"),
                        rs.getString("state_province"),
                        rs.getString("zip_postal_code"),
                        rs.getString("country")
                    );
                    addresses.add(address);
                }
            } catch (SQLException e) {
                System.err.println("Error getting addresses by barangay: " + e.getMessage());
            }
            return addresses;
        }

        // NEW: Search addresses by city
        public static List<Address> getAddressesByCity(String city) {
            List<Address> addresses = new ArrayList<>();
            String query = "SELECT * FROM addresses WHERE city LIKE ? ORDER BY barangay, street_address";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, "%" + city + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Address address = new Address(
                        rs.getInt("address_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("street_address"),
                        rs.getString("barangay"),
                        rs.getString("address_line"),
                        rs.getString("city"),
                        rs.getString("state_province"),
                        rs.getString("zip_postal_code"),
                        rs.getString("country")
                    );
                    addresses.add(address);
                }
            } catch (SQLException e) {
                System.err.println("Error getting addresses by city: " + e.getMessage());
            }
            return addresses;
        }
    }
    
    // Citizen class for citizens table (UPDATED with gender field, removed address)
    public static class Citizen {
        private int citizenId;
        private Integer userId;
        private String fname;
        private String mname;
        private String lname;
        private String nationalId;
        private Date birthDate;
        private String gender; // NEW FIELD
        private String phone;
        private String email;
        private Date applicationDate;
        
        public Citizen() {}
        
        public Citizen(int citizenId, Integer userId, String fname, String mname, String lname, String nationalId,
                      Date birthDate, String gender, String phone, String email, Date applicationDate) {
            this.citizenId = citizenId;
            this.userId = userId;
            this.fname = fname;
            this.mname = mname;
            this.lname = lname;
            this.nationalId = nationalId;
            this.birthDate = birthDate;
            this.gender = gender;
            this.phone = phone;
            this.email = email;
            this.applicationDate = applicationDate;
        }
        
        // Getters and Setters
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public String getFname() { return fname; }
        public void setFname(String fname) { this.fname = fname; }
        
        public String getMname() { return mname; }
        public void setMname(String mname) { this.mname = mname; }
        
        public String getLname() { return lname; }
        public void setLname(String lname) { this.lname = lname; }
        
        public String getFullName() { 
            return (fname + " " + (mname != null && !mname.isEmpty() ? mname + " " : "") + lname).trim();
        }
        
        public String getNationalId() { return nationalId; }
        public void setNationalId(String nationalId) { this.nationalId = nationalId; }
        
        public Date getBirthDate() { return birthDate; }
        public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
        
        public String getGender() { return gender; } // NEW GETTER
        public void setGender(String gender) { this.gender = gender; } // NEW SETTER
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Date getApplicationDate() { return applicationDate; }
        public void setApplicationDate(Date applicationDate) { this.applicationDate = applicationDate; }
        
        // Database Functions (UPDATED to include gender field)
        public static List<Citizen> getAllCitizens() {
            List<Citizen> citizens = new ArrayList<>();
            String query = "SELECT * FROM citizens ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Citizen citizen = new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                    citizens.add(citizen);
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizens: " + e.getMessage());
            }
            return citizens;
        }
        
        public static Citizen getCitizenByNationalId(String nationalId) {
            String query = "SELECT * FROM citizens WHERE national_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, nationalId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizen by national ID: " + e.getMessage());
            }
            return null;
        }
        
        public static Citizen getCitizenById(int citizenId) {
            String query = "SELECT * FROM citizens WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizen: " + e.getMessage());
            }
            return null;
        }
        
        public static Citizen getCitizenByUserId(int userId) {
            String query = "SELECT * FROM citizens WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizen by user ID: " + e.getMessage());
            }
            return null;
        }
        
        public static boolean addCitizen(Citizen citizen) {
            return addCitizenAndGetId(citizen) > 0;
        }

        public static int addCitizenAndGetId(Citizen citizen) {
            String query = "INSERT INTO citizens (user_id, fname, mname, lname, national_id, birth_date, gender, phone, email, application_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                if (citizen.getUserId() != null) {
                    stmt.setInt(1, citizen.getUserId());
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setString(2, citizen.getFname());
                stmt.setString(3, citizen.getMname());
                stmt.setString(4, citizen.getLname());
                stmt.setString(5, citizen.getNationalId());
                stmt.setDate(6, citizen.getBirthDate());
                stmt.setString(7, citizen.getGender());
                stmt.setString(8, citizen.getPhone());
                stmt.setString(9, citizen.getEmail());
                stmt.setDate(10, citizen.getApplicationDate());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
                return -1;
            } catch (SQLException e) {
                System.err.println("Error adding citizen: " + e.getMessage());
                return -1;
            }
        }

        public static boolean updateCitizen(Citizen citizen) {
            String query = "UPDATE citizens SET user_id = ?, fname = ?, mname = ?, lname = ?, national_id = ?, " +
                          "birth_date = ?, gender = ?, phone = ?, email = ?, application_date = ? WHERE citizen_id = ?"; // UPDATED with gender
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                if (citizen.getUserId() != null) {
                    stmt.setInt(1, citizen.getUserId());
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setString(2, citizen.getFname());
                stmt.setString(3, citizen.getMname());
                stmt.setString(4, citizen.getLname());
                stmt.setString(5, citizen.getNationalId());
                stmt.setDate(6, citizen.getBirthDate());
                stmt.setString(7, citizen.getGender()); // NEW FIELD
                stmt.setString(8, citizen.getPhone());
                stmt.setString(9, citizen.getEmail());
                stmt.setDate(10, citizen.getApplicationDate());
                stmt.setInt(11, citizen.getCitizenId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating citizen: " + e.getMessage());
                return false;
            }
        }
        
        public static List<Citizen> searchCitizens(String searchTerm) {
            List<Citizen> citizens = new ArrayList<>();
            String query = "SELECT * FROM citizens WHERE " +
                          "fname LIKE ? OR mname LIKE ? OR lname LIKE ? OR national_id LIKE ? OR phone LIKE ? OR email LIKE ? " +
                          "ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                stmt.setString(4, likeTerm);
                stmt.setString(5, likeTerm);
                stmt.setString(6, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Citizen citizen = new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                    citizens.add(citizen);
                }
            } catch (SQLException e) {
                System.err.println("Error searching citizens: " + e.getMessage());
            }
            return citizens;
        }
        
        public static int getTotalCitizens() {
            String query = "SELECT COUNT(*) as total FROM citizens";
            
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
        
        public static List<Citizen> getCitizensWithNoUser() {
            List<Citizen> citizens = new ArrayList<>();
            String query = "SELECT * FROM citizens WHERE user_id IS NULL ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Citizen citizen = new Citizen(
                        rs.getInt("citizen_id"),
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("national_id"),
                        rs.getDate("birth_date"),
                        rs.getString("gender"), // NEW FIELD
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("application_date")
                    );
                    citizens.add(citizen);
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizens with no user: " + e.getMessage());
            }
            return citizens;
        }
        
        public static int getCitizenCountByGender(String gender) {
            String query = "SELECT COUNT(*) as total FROM citizens WHERE gender = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, gender);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting citizen count by gender: " + e.getMessage());
            }
            return 0;
        }
    }
    
    // IDStatus class for id_status table (UNCHANGED)
    public static class IDStatus {
        private int statusId;
        private String transactionId;
        private int citizenId;
        private int statusNameId; // CHANGED: from status to statusNameId
        private String status; // ADDED: For backward compatibility
        private Date updateDate;
        private String notes;

        // Reference to status_names table
        private StatusName statusName; // NEW: Reference to status details

        public IDStatus() {}

        public IDStatus(int statusId, String transactionId, int citizenId, int statusNameId, 
                        Date updateDate, String notes) {
            this.statusId = statusId;
            this.transactionId = transactionId;
            this.citizenId = citizenId;
            this.statusNameId = statusNameId;
            this.updateDate = updateDate;
            this.notes = notes;
        }

        // Getters and Setters
        public int getStatusId() { return statusId; }
        public void setStatusId(int statusId) { this.statusId = statusId; }

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }

        public int getStatusNameId() { return statusNameId; }
        public void setStatusNameId(int statusNameId) { this.statusNameId = statusNameId; }

        // For backward compatibility
        public String getStatus() { 
            if (statusName != null) {
                return statusName.getStatusName();
            }
            return status;
        }

        public void setStatus(String status) { 
            this.status = status;
        }

        public Date getUpdateDate() { return updateDate; }
        public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public StatusName getStatusName() { return statusName; } // NEW
        public void setStatusName(StatusName statusName) { this.statusName = statusName; } // NEW

        // Database Functions (UPDATED)
        public static IDStatus getStatusByCitizenId(int citizenId) {
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "WHERE ist.citizen_id = ? " +
                          "ORDER BY ist.update_date DESC, ist.status_id DESC LIMIT 1";


            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),  // This should get the transaction_id
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    idStatus.setStatus(rs.getString("status_name"));
                    return idStatus;
                }
            } catch (SQLException e) {
                System.err.println("Error getting status by citizen ID: " + e.getMessage());
            }
            return null;
        }
        
        public static String formatTransactionId(String rawTransactionId) {
            if (rawTransactionId == null || rawTransactionId.isEmpty()) {
                return "TXN-Not-Assigned";
            }

            // If it's already in the correct format, return as is
            if (rawTransactionId.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}-\\d{4}-\\d{4}-\\d{2}")) {
                return rawTransactionId;
            }

            // If it's in the old TXN2024001 format, convert it
            if (rawTransactionId.startsWith("TXN")) {
                try {
                    String numbers = rawTransactionId.replace("TXN", "").trim();
                    // Pad with zeros to get 26 digits total
                    String padded = String.format("%026d", Long.parseLong(numbers));

                    // Format: 1234-5678-9012-3456-7890-1234-56
                    return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                        "$1-$2-$3-$4-$5-$6-$7");
                } catch (NumberFormatException e) {
                    return rawTransactionId; // Return as is if can't convert
                }
            }

            // For any other format, try to extract numbers and format
            String numbersOnly = rawTransactionId.replaceAll("[^0-9]", "");
            if (numbersOnly.length() >= 26) {
                String padded = numbersOnly.substring(0, 26);
                return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                    "$1-$2-$3-$4-$5-$6-$7");
            }

            // Pad with zeros if too short
            String padded = String.format("%-26s", numbersOnly).replace(' ', '0');
            return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                "$1-$2-$3-$4-$5-$6-$7");
        }
        
        public static IDStatus getStatusByTransactionId(String transactionId) {
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "WHERE ist.transaction_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, transactionId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    idStatus.setStatus(rs.getString("status_name"));
                    return idStatus;
                }
            } catch (SQLException e) {
                System.err.println("Error getting status by transaction ID: " + e.getMessage());
            }
            return null;
        }
        
        public static java.util.List<IDStatus> getAllStatuses() {
            java.util.List<IDStatus> statuses = new java.util.ArrayList<>();
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "LIMIT 10";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    IDStatus status = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    status.setStatus(rs.getString("status_name"));
                    statuses.add(status);
                }

            } catch (SQLException e) {
                System.err.println("Error getting all statuses: " + e.getMessage());
            }
            return statuses;
        }

        public static IDStatus getStatusById(int statusId) {
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "WHERE ist.status_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, statusId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting status: " + e.getMessage());
            }
            return null;
        }
        
        public static List<IDStatus> getAllStatus() {
            List<IDStatus> statuses = new ArrayList<>();
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "ORDER BY ist.update_date DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    idStatus.setStatus(rs.getString("status_name"));
                    statuses.add(idStatus);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all status: " + e.getMessage());
            }
            return statuses;
        }

        public static List<IDStatus> getStatusByStatus(String statusName) {
            List<IDStatus> statuses = new ArrayList<>();
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "WHERE sn.status_name = ? ORDER BY ist.update_date DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, statusName);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    idStatus.setStatus(rs.getString("status_name"));
                    statuses.add(idStatus);
                }
            } catch (SQLException e) {
                System.err.println("Error getting status by status: " + e.getMessage());
            }
            return statuses;
        }
        
        public static List<IDStatus> getStatusHistoryByCitizenId(int citizenId) {
            List<IDStatus> statuses = new ArrayList<>();
            String query = "SELECT ist.*, sn.status_name FROM id_status ist " +
                          "LEFT JOIN status_names sn ON ist.status_name_id = sn.status_name_id " +
                          "WHERE ist.citizen_id = ? ORDER BY ist.update_date ASC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("status_name_id"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );

                    // Set status from joined table
                    idStatus.setStatus(rs.getString("status_name"));
                    statuses.add(idStatus);
                }
            } catch (SQLException e) {
                System.err.println("Error getting status history by citizen ID: " + e.getMessage());
            }
            return statuses;
        }
        
        public static boolean addStatus(IDStatus idStatus) {
            String query = "INSERT INTO id_status (transaction_id, citizen_id, status_name_id, update_date, notes) " +
                          "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, idStatus.getTransactionId());
                stmt.setInt(2, idStatus.getCitizenId());
                stmt.setInt(3, idStatus.getStatusNameId());
                stmt.setDate(4, idStatus.getUpdateDate());
                stmt.setString(5, idStatus.getNotes());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding status: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateStatus(IDStatus idStatus) {
            String query = "UPDATE id_status SET transaction_id = ?, status_name_id = ?, update_date = ?, notes = ? " +
                          "WHERE status_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, idStatus.getTransactionId());
                stmt.setInt(2, idStatus.getStatusNameId());  // Update status_name_id instead of status
                stmt.setDate(3, idStatus.getUpdateDate());
                stmt.setString(4, idStatus.getNotes());
                stmt.setInt(5, idStatus.getStatusId());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating status: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateStatusByCitizenId(int citizenId, String status, String notes) {
            String query = "UPDATE id_status SET status = ?, update_date = ?, notes = ? " +
                          "WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                stmt.setDate(2, new Date(System.currentTimeMillis()));
                stmt.setString(3, notes);
                stmt.setInt(4, citizenId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating status by citizen ID: " + e.getMessage());
                return false;
            }
        }
        
        public static int getStatusCount(String status) {
            String query = "SELECT COUNT(*) as total FROM id_status WHERE status = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting status count: " + e.getMessage());
            }
            return 0;
        }
        
        public static String generateTransactionId(int citizenId) {
            // Generate a 30-digit transaction ID in format: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX-XX
            java.util.Random random = new java.util.Random();
            
            // Generate random numbers for each segment
            int segment1 = 1000 + random.nextInt(9000); // 4 digits
            int segment2 = 1000 + random.nextInt(9000); // 4 digits
            int segment3 = 1000 + random.nextInt(9000); // 4 digits
            int segment4 = 1000 + random.nextInt(9000); // 4 digits
            int segment5 = 1000 + random.nextInt(9000); // 4 digits
            int segment6 = 1000 + random.nextInt(9000); // 4 digits
            int segment7 = 10 + random.nextInt(90);     // 2 digits

            // Format: 1234-5678-9012-3456-7890-1234-56
            return String.format("%04d-%04d-%04d-%04d-%04d-%04d-%02d", 
                segment1, segment2, segment3, segment4, segment5, segment6, segment7);
        }
    }
    
    public static class StatusName {
        private int statusNameId;
        private String statusName;
        private String statusCode;
        private String description;
        private int stepOrder;
        private boolean isActive;

        public StatusName() {}

        public StatusName(int statusNameId, String statusName, String statusCode, 
                         String description, int stepOrder, boolean isActive) {
            this.statusNameId = statusNameId;
            this.statusName = statusName;
            this.statusCode = statusCode;
            this.description = description;
            this.stepOrder = stepOrder;
            this.isActive = isActive;
        }

        // Getters and Setters
        public int getStatusNameId() { return statusNameId; }
        public void setStatusNameId(int statusNameId) { this.statusNameId = statusNameId; }

        public String getStatusName() { return statusName; }
        public void setStatusName(String statusName) { this.statusName = statusName; }

        public String getStatusCode() { return statusCode; }
        public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getStepOrder() { return stepOrder; }
        public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        // Database Functions
        public static List<StatusName> getAllStatusNames() {
            List<StatusName> statuses = new ArrayList<>();
            String query = "SELECT * FROM status_names WHERE is_active = 1 ORDER BY step_order";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    StatusName status = new StatusName(
                        rs.getInt("status_name_id"),
                        rs.getString("status_name"),
                        rs.getString("status_code"),
                        rs.getString("description"),
                        rs.getInt("step_order"),
                        rs.getBoolean("is_active")
                    );
                    statuses.add(status);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all status names: " + e.getMessage());
            }
            return statuses;
        }

        public static StatusName getStatusNameById(int statusNameId) {
            String query = "SELECT * FROM status_names WHERE status_name_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, statusNameId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new StatusName(
                        rs.getInt("status_name_id"),
                        rs.getString("status_name"),
                        rs.getString("status_code"),
                        rs.getString("description"),
                        rs.getInt("step_order"),
                        rs.getBoolean("is_active")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting status name: " + e.getMessage());
            }
            return null;
        }

        public static StatusName getStatusNameByCode(String statusCode) {
            String query = "SELECT * FROM status_names WHERE status_code = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, statusCode);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new StatusName(
                        rs.getInt("status_name_id"),
                        rs.getString("status_name"),
                        rs.getString("status_code"),
                        rs.getString("description"),
                        rs.getInt("step_order"),
                        rs.getBoolean("is_active")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting status name by code: " + e.getMessage());
            }
            return null;
        }

        public static StatusName getNextStatus(int currentStepOrder) {
            String query = "SELECT * FROM status_names WHERE step_order > ? AND is_active = 1 ORDER BY step_order LIMIT 1";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, currentStepOrder);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new StatusName(
                        rs.getInt("status_name_id"),
                        rs.getString("status_name"),
                        rs.getString("status_code"),
                        rs.getString("description"),
                        rs.getInt("step_order"),
                        rs.getBoolean("is_active")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting next status: " + e.getMessage());
            }
            return null;
        }
    }
    
    // Appointment class for appointments table (UNCHANGED)
    public static class Appointment {
        // ... (all Appointment code remains exactly the same as in the original file)
        // No changes needed for this class
        private int appointmentId;
        private int citizenId;
        private Date appDate;
        private String appTime;
        private String status;
        private Date createdDate;
        
        public Appointment() {}
        
        public Appointment(int appointmentId, int citizenId, Date appDate, 
                          String appTime, String status, Date createdDate) {
            this.appointmentId = appointmentId;
            this.citizenId = citizenId;
            this.appDate = appDate;
            this.appTime = appTime;
            this.status = status;
            this.createdDate = createdDate;
        }
        
        // Getters and Setters
        public int getAppointmentId() { return appointmentId; }
        public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
        
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public Date getAppDate() { return appDate; }
        public void setAppDate(Date appDate) { this.appDate = appDate; }
        
        public String getAppTime() { return appTime; }
        public void setAppTime(String appTime) { this.appTime = appTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
        
        // Database Functions
        public static Appointment getAppointmentByCitizenId(int citizenId) {
            String query = "SELECT * FROM appointments WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting appointment by citizen ID: " + e.getMessage());
            }
            return null;
        }
        
        public static Appointment getAppointmentById(int appointmentId) {
            String query = "SELECT * FROM appointments WHERE appointment_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, appointmentId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting appointment: " + e.getMessage());
            }
            return null;
        }
        
        public static List<Appointment> getAllAppointments() {
            List<Appointment> appointments = new ArrayList<>();
            String query = "SELECT * FROM appointments ORDER BY app_date, app_time";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                    appointments.add(appointment);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all appointments: " + e.getMessage());
            }
            return appointments;
        }
        
        public static List<Appointment> getAppointmentsByDate(Date date) {
            List<Appointment> appointments = new ArrayList<>();
            String query = "SELECT * FROM appointments WHERE app_date = ? ORDER BY app_time";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setDate(1, date);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                    appointments.add(appointment);
                }
            } catch (SQLException e) {
                System.err.println("Error getting appointments by date: " + e.getMessage());
            }
            return appointments;
        }
        
        public static List<Appointment> getAppointmentsByStatus(String status) {
            List<Appointment> appointments = new ArrayList<>();
            String query = "SELECT * FROM appointments WHERE status = ? ORDER BY app_date, app_time";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                    appointments.add(appointment);
                }
            } catch (SQLException e) {
                System.err.println("Error getting appointments by status: " + e.getMessage());
            }
            return appointments;
        }
        
        public static boolean addAppointment(Appointment appointment) {
            String query = "INSERT INTO appointments (citizen_id, app_date, app_time, status, created_date) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, appointment.getCitizenId());
                stmt.setDate(2, appointment.getAppDate());
                stmt.setString(3, appointment.getAppTime());
                stmt.setString(4, appointment.getStatus());
                stmt.setDate(5, appointment.getCreatedDate());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding appointment: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateAppointment(Appointment appointment) {
            String query = "UPDATE appointments SET citizen_id = ?, app_date = ?, app_time = ?, " +
                          "status = ?, created_date = ? WHERE appointment_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, appointment.getCitizenId());
                stmt.setDate(2, appointment.getAppDate());
                stmt.setString(3, appointment.getAppTime());
                stmt.setString(4, appointment.getStatus());
                stmt.setDate(5, appointment.getCreatedDate());
                stmt.setInt(6, appointment.getAppointmentId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating appointment: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateAppointmentStatus(int appointmentId, String status) {
            String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                stmt.setInt(2, appointmentId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating appointment status: " + e.getMessage());
                return false;
            }
        }
        
        public static int getAppointmentCountByStatus(String status) {
            String query = "SELECT COUNT(*) as total FROM appointments WHERE status = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting appointment count: " + e.getMessage());
            }
            return 0;
        }
        
        public static List<Appointment> searchAppointments(String searchTerm) {
            List<Appointment> appointments = new ArrayList<>();
            String query = "SELECT a.* FROM appointments a JOIN citizens c ON a.citizen_id = c.citizen_id " +
                          "WHERE c.fname LIKE ? OR c.mname LIKE ? OR c.lname LIKE ? OR c.national_id LIKE ? " +
                          "ORDER BY a.app_date, a.app_time";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                stmt.setString(4, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Appointment appointment = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("app_date"),
                        rs.getString("app_time"),
                        rs.getString("status"),
                        rs.getDate("created_date")
                    );
                    appointments.add(appointment);
                }
            } catch (SQLException e) {
                System.err.println("Error searching appointments: " + e.getMessage());
            }
            return appointments;
        }
    }
    
    // ActivityLog class for activity_log table (UNCHANGED)
    public static class ActivityLog {
        // ... (all ActivityLog code remains exactly the same as in the original file)
        // No changes needed for this class
        private int logId;
        private Integer userId;
        private String action;
        private Date actionDate;
        private String actionTime;
        
        public ActivityLog() {}
        
        public ActivityLog(int logId, Integer userId, String action, Date actionDate, String actionTime) {
            this.logId = logId;
            this.userId = userId;
            this.action = action;
            this.actionDate = actionDate;
            this.actionTime = actionTime;
        }
        
        // Getters and Setters (unchanged)
        public int getLogId() { return logId; }
        public void setLogId(int logId) { this.logId = logId; }
        
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Date getActionDate() { return actionDate; }
        public void setActionDate(Date actionDate) { this.actionDate = actionDate; }
        
        public String getActionTime() { return actionTime; }
        public void setActionTime(String actionTime) { this.actionTime = actionTime; }
        
        // Database Functions (unchanged)
        public static List<ActivityLog> getAllActivityLogs() {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM activity_log ORDER BY action_date DESC, action_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getDate("action_date"),
                        rs.getString("action_time")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all activity logs: " + e.getMessage());
            }
            return logs;
        }
        
        public static List<ActivityLog> getActivityLogsByUser(int userId) {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM activity_log WHERE user_id = ? ORDER BY action_date DESC, action_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getDate("action_date"),
                        rs.getString("action_time")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting activity logs by user: " + e.getMessage());
            }
            return logs;
        }
        
        public static List<ActivityLog> getRecentActivityLogs(int limit) {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM activity_log ORDER BY action_date DESC, action_time DESC LIMIT ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getDate("action_date"),
                        rs.getString("action_time")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting recent activity logs: " + e.getMessage());
            }
            return logs;
        }
        
        public static boolean logActivity(int userId, String action) {
            String query = "INSERT INTO activity_log (user_id, action, action_date, action_time) " +
                          "VALUES (?, ?, CURDATE(), TIME_FORMAT(NOW(), '%h:%i %p'))";
            
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
        
        public static boolean logActivity(int userId, String action, Date actionDate, String actionTime) {
            String query = "INSERT INTO activity_log (user_id, action, action_date, action_time) " +
                          "VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, userId);
                stmt.setString(2, action);
                stmt.setDate(3, actionDate);
                stmt.setString(4, actionTime);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error logging activity: " + e.getMessage());
                return false;
            }
        }
        
        public static int getTotalActivityLogs() {
            String query = "SELECT COUNT(*) as total FROM activity_log";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting total activity logs: " + e.getMessage());
            }
            return 0;
        }
        
        public static List<ActivityLog> getActivityLogsByCitizenId(int citizenId) {
            List<ActivityLog> logs = new ArrayList<>();
            String query = "SELECT * FROM activity_log WHERE user_id IN (SELECT user_id FROM citizens WHERE citizen_id = ?) ORDER BY action_date DESC, action_time DESC";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    ActivityLog log = new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getDate("action_date"),
                        rs.getString("action_time")
                    );
                    logs.add(log);
                }
            } catch (SQLException e) {
                System.err.println("Error getting activity logs by citizen ID: " + e.getMessage());
            }
            return logs;
        }
    }
    
    // Helper class to get combined citizen information (UPDATED to include Address)
    public static class CitizenInfo {
        private Citizen citizen;
        private IDStatus status;
        private Appointment appointment;
        private User user;
        private List<Document> documents;
        private Address address; // NEW FIELD
        
        public CitizenInfo() {}
        
        public CitizenInfo(Citizen citizen, IDStatus status, Appointment appointment, User user, 
                          List<Document> documents, Address address) {
            this.citizen = citizen;
            this.status = status;
            this.appointment = appointment;
            this.user = user;
            this.documents = documents;
            this.address = address;
        }
        
        // Getters and Setters
        public Citizen getCitizen() { return citizen; }
        public void setCitizen(Citizen citizen) { this.citizen = citizen; }
        
        public IDStatus getStatus() { return status; }
        public void setStatus(IDStatus status) { this.status = status; }
        
        public Appointment getAppointment() { return appointment; }
        public void setAppointment(Appointment appointment) { this.appointment = appointment; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        
        public List<Document> getDocuments() { return documents; }
        public void setDocuments(List<Document> documents) { this.documents = documents; }
        
        public Address getAddress() { return address; } // NEW GETTER
        public void setAddress(Address address) { this.address = address; } // NEW SETTER
        
        public static CitizenInfo getCitizenInfoByNationalId(String nationalId) {
            Citizen citizen = Citizen.getCitizenByNationalId(nationalId);
            if (citizen == null) return null;
            
            IDStatus status = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            Appointment appointment = Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            User user = null;
            if (citizen.getUserId() != null) {
                user = User.getUserById(citizen.getUserId());
            }
            List<Document> documents = Document.getDocumentsByCitizenId(citizen.getCitizenId());
            Address address = Address.getAddressByCitizenId(citizen.getCitizenId()); // NEW
            
            return new CitizenInfo(citizen, status, appointment, user, documents, address);
        }
        
        public static CitizenInfo getCitizenInfoByCitizenId(int citizenId) {
            Citizen citizen = Citizen.getCitizenById(citizenId);
            if (citizen == null) return null;
            
            IDStatus status = IDStatus.getStatusByCitizenId(citizen.getCitizenId());
            Appointment appointment = Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            User user = null;
            if (citizen.getUserId() != null) {
                user = User.getUserById(citizen.getUserId());
            }
            List<Document> documents = Document.getDocumentsByCitizenId(citizen.getCitizenId());
            Address address = Address.getAddressByCitizenId(citizen.getCitizenId()); // NEW
            
            return new CitizenInfo(citizen, status, appointment, user, documents, address);
        }
        
        public static CitizenInfo getCitizenInfoByTransactionId(String transactionId) {
            IDStatus status = IDStatus.getStatusByTransactionId(transactionId);
            if (status == null) return null;
            
            Citizen citizen = Citizen.getCitizenById(status.getCitizenId());
            if (citizen == null) return null;
            
            Appointment appointment = Appointment.getAppointmentByCitizenId(citizen.getCitizenId());
            User user = null;
            if (citizen.getUserId() != null) {
                user = User.getUserById(citizen.getUserId());
            }
            List<Document> documents = Document.getDocumentsByCitizenId(citizen.getCitizenId());
            Address address = Address.getAddressByCitizenId(citizen.getCitizenId()); // NEW
            
            return new CitizenInfo(citizen, status, appointment, user, documents, address);
        }
    }
    
    // Document class for documents table (UNCHANGED)
    public static class Document {
        private int documentId;
        private int citizenId;
        private int formId; // CHANGED: from documentName to formId
        private String documentName; // ADDED: For backward compatibility
        private String status;
        private String submitted;
        private String requiredBy;
        private String filePath;
        private Date uploadDate;
        
        private DocForm docForm;
        
        public Document() {}
        
        public Document(int documentId, int citizenId, int formId, String status,
                           String submitted, String requiredBy, String filePath, Date uploadDate) {
                this.documentId = documentId;
                this.citizenId = citizenId;
                this.formId = formId;
                this.status = status;
                this.submitted = submitted;
                this.requiredBy = requiredBy;
                this.filePath = filePath;
                this.uploadDate = uploadDate;
            }
        
        // Getters and Setters
        public int getDocumentId() { return documentId; }
        public void setDocumentId(int documentId) { this.documentId = documentId; }

        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }

        public int getFormId() { return formId; }
        public void setFormId(int formId) { this.formId = formId; }

        // For backward compatibility
        public String getDocumentName() { 
            if (docForm != null) {
                return docForm.getFormName();
            }
            return documentName;
        }

        public void setDocumentName(String documentName) { 
            this.documentName = documentName;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getSubmitted() { return submitted; }
        public void setSubmitted(String submitted) { this.submitted = submitted; }

        public String getRequiredBy() { return requiredBy; }
        public void setRequiredBy(String requiredBy) { this.requiredBy = requiredBy; }

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }

        public Date getUploadDate() { return uploadDate; }
        public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }

        public DocForm getDocForm() { return docForm; } // NEW
        public void setDocForm(DocForm docForm) { this.docForm = docForm; } // NEW

        // Database Functions (UPDATED)
        public static List<Document> getDocumentsByCitizenId(int citizenId) {
            List<Document> documents = new ArrayList<>();
            String query = "SELECT d.*, df.form_name FROM documents d " +
                          "LEFT JOIN doc_forms df ON d.form_id = df.form_id " +
                          "WHERE d.citizen_id = ? ORDER BY d.required_by, df.form_name";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Document document = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("form_id"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );

                    // Set document name from joined table
                    document.setDocumentName(rs.getString("form_name"));
                    documents.add(document);
                }
            } catch (SQLException e) {
                System.err.println("Error getting documents by citizen ID: " + e.getMessage());
            }
            return documents;
        }
        
        public static Document getDocumentById(int documentId) {
            String query = "SELECT * FROM documents WHERE document_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, documentId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("form_id"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting document: " + e.getMessage());
            }
            return null;
        }
        
        public static List<Document> getAllDocuments() {
            List<Document> documents = new ArrayList<>();
            String query = "SELECT * FROM documents ORDER BY citizen_id, required_by";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Document document = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("form_id"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );
                    documents.add(document);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all documents: " + e.getMessage());
            }
            return documents;
        }
        
        public static List<Document> getDocumentsByStatus(String status) {
            List<Document> documents = new ArrayList<>();
            String query = "SELECT * FROM documents WHERE status = ? ORDER BY citizen_id, document_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Document document = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("form_id"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );
                    documents.add(document);
                }
            } catch (SQLException e) {
                System.err.println("Error getting documents by status: " + e.getMessage());
            }
            return documents;
        }
        
        public static boolean addDocument(Document document) {
            String query = "INSERT INTO documents (citizen_id, form_id, status, submitted, required_by, file_path, upload_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, document.getCitizenId());
                stmt.setInt(2, document.getFormId()); // Changed from document_name to form_id
                stmt.setString(3, document.getStatus());
                stmt.setString(4, document.getSubmitted());
                stmt.setString(5, document.getRequiredBy());
                stmt.setString(6, document.getFilePath());
                stmt.setDate(7, document.getUploadDate());

                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding document: " + e.getMessage());
                return false;
            }
        }

        public static boolean updateDocument(Document document) {
            String query = "UPDATE documents SET citizen_id = ?, document_name = ?, status = ?, " +
                          "submitted = ?, required_by = ?, file_path = ?, upload_date = ? WHERE document_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, document.getCitizenId());
                stmt.setString(2, document.getDocumentName());
                stmt.setString(3, document.getStatus());
                stmt.setString(4, document.getSubmitted());
                stmt.setString(5, document.getRequiredBy());
                stmt.setString(6, document.getFilePath());
                stmt.setDate(7, document.getUploadDate());
                stmt.setInt(8, document.getDocumentId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating document: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateDocumentStatus(int documentId, String status, String submitted) {
            String query = "UPDATE documents SET status = ?, submitted = ?, upload_date = ? WHERE document_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                stmt.setString(2, submitted);
                stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                stmt.setInt(4, documentId);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating document status: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean deleteDocument(int documentId) {
            String query = "DELETE FROM documents WHERE document_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, documentId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting document: " + e.getMessage());
                return false;
            }
        }
        
        public static int getDocumentCountByStatus(String status) {
            String query = "SELECT COUNT(*) as total FROM documents WHERE status = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting document count: " + e.getMessage());
            }
            return 0;
        }
        
        public static List<Document> searchDocuments(String searchTerm) {
            List<Document> documents = new ArrayList<>();
            String query = "SELECT * FROM documents WHERE document_name LIKE ? OR status LIKE ? OR required_by LIKE ? " +
                          "ORDER BY citizen_id, document_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                stmt.setString(3, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Document document = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getInt("form_id"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );
                    documents.add(document);
                }
            } catch (SQLException e) {
                System.err.println("Error searching documents: " + e.getMessage());
            }
            return documents;
        }
    }
    
    public static class DocForm {
        private int formId;
        private String formName;
        private String formCode;
        private String description;
        private boolean isRequired;
        private String status;

        public DocForm() {}

        public DocForm(int formId, String formName, String formCode, String description, 
                       boolean isRequired, String status) {
            this.formId = formId;
            this.formName = formName;
            this.formCode = formCode;
            this.description = description;
            this.isRequired = isRequired;
            this.status = status;
        }

        // Getters and Setters
        public int getFormId() { return formId; }
        public void setFormId(int formId) { this.formId = formId; }

        public String getFormName() { return formName; }
        public void setFormName(String formName) { this.formName = formName; }

        public String getFormCode() { return formCode; }
        public void setFormCode(String formCode) { this.formCode = formCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public boolean isRequired() { return isRequired; }
        public void setRequired(boolean required) { isRequired = required; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        // Database Functions
        public static List<DocForm> getAllDocForms() {
            List<DocForm> forms = new ArrayList<>();
            String query = "SELECT * FROM doc_forms WHERE status = 'Active' ORDER BY form_name";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    DocForm form = new DocForm(
                        rs.getInt("form_id"),
                        rs.getString("form_name"),
                        rs.getString("form_code"),
                        rs.getString("description"),
                        rs.getBoolean("is_required"),
                        rs.getString("status")
                    );
                    forms.add(form);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all doc forms: " + e.getMessage());
            }
            return forms;
        }

        public static DocForm getDocFormById(int formId) {
            String query = "SELECT * FROM doc_forms WHERE form_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, formId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new DocForm(
                        rs.getInt("form_id"),
                        rs.getString("form_name"),
                        rs.getString("form_code"),
                        rs.getString("description"),
                        rs.getBoolean("is_required"),
                        rs.getString("status")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting doc form: " + e.getMessage());
            }
            return null;
        }

        public static DocForm getDocFormByCode(String formCode) {
            String query = "SELECT * FROM doc_forms WHERE form_code = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, formCode);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new DocForm(
                        rs.getInt("form_id"),
                        rs.getString("form_name"),
                        rs.getString("form_code"),
                        rs.getString("description"),
                        rs.getBoolean("is_required"),
                        rs.getString("status")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting doc form by code: " + e.getMessage());
            }
            return null;
        }
    }
    
    // Notification class for notifications table (UNCHANGED)
    public static class Notification {
        // ... (all Notification code remains exactly the same as in the original file)
        // No changes needed for this class
        private int notificationId;
        private int citizenId;
        private Date notificationDate;
        private String notificationTime;
        private String message;
        private String type;
        private String readStatus;
        
        public Notification() {}
        
        public Notification(int notificationId, int citizenId, Date notificationDate, String notificationTime,
                           String message, String type, String readStatus) {
            this.notificationId = notificationId;
            this.citizenId = citizenId;
            this.notificationDate = notificationDate;
            this.notificationTime = notificationTime;
            this.message = message;
            this.type = type;
            this.readStatus = readStatus;
        }
        
        // Getters and Setters (unchanged)
        public int getNotificationId() { return notificationId; }
        public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
        
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public Date getNotificationDate() { return notificationDate; }
        public void setNotificationDate(Date notificationDate) { this.notificationDate = notificationDate; }
        
        public String getNotificationTime() { return notificationTime; }
        public void setNotificationTime(String notificationTime) { this.notificationTime = notificationTime; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getReadStatus() { return readStatus; }
        public void setReadStatus(String readStatus) { this.readStatus = readStatus; }
        
        // Database Functions (unchanged)
        public static List<Notification> getNotificationsByCitizenId(int citizenId) {
            List<Notification> notifications = new ArrayList<>();
            String query = "SELECT * FROM notifications WHERE citizen_id = ? ORDER BY notification_date DESC, notification_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("notification_date"),
                        rs.getString("notification_time"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("read_status")
                    );
                    notifications.add(notification);
                }
            } catch (SQLException e) {
                System.err.println("Error getting notifications by citizen ID: " + e.getMessage());
            }
            return notifications;
        }
        
        public static List<Notification> getUnreadNotifications(int citizenId) {
            List<Notification> notifications = new ArrayList<>();
            String query = "SELECT * FROM notifications WHERE citizen_id = ? AND read_status = 'Unread' " +
                          "ORDER BY notification_date DESC, notification_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("notification_date"),
                        rs.getString("notification_time"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("read_status")
                    );
                    notifications.add(notification);
                }
            } catch (SQLException e) {
                System.err.println("Error getting unread notifications: " + e.getMessage());
            }
            return notifications;
        }
        
        public static Notification getNotificationById(int notificationId) {
            String query = "SELECT * FROM notifications WHERE notification_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, notificationId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("notification_date"),
                        rs.getString("notification_time"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("read_status")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting notification: " + e.getMessage());
            }
            return null;
        }
        
        public static List<Notification> getAllNotifications() {
            List<Notification> notifications = new ArrayList<>();
            String query = "SELECT * FROM notifications ORDER BY notification_date DESC, notification_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("notification_date"),
                        rs.getString("notification_time"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("read_status")
                    );
                    notifications.add(notification);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all notifications: " + e.getMessage());
            }
            return notifications;
        }
        
        public static boolean addNotification(Notification notification) {
            String query = "INSERT INTO notifications (citizen_id, notification_date, notification_time, message, type, read_status) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, notification.getCitizenId());
                stmt.setDate(2, notification.getNotificationDate());
                stmt.setString(3, notification.getNotificationTime());
                stmt.setString(4, notification.getMessage());
                stmt.setString(5, notification.getType());
                stmt.setString(6, notification.getReadStatus());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding notification: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean addNotification(int citizenId, String message, String type) {
            String query = "INSERT INTO notifications (citizen_id, notification_date, notification_time, message, type, read_status) " +
                          "VALUES (?, CURDATE(), TIME_FORMAT(NOW(), '%h:%i %p'), ?, ?, 'Unread')";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                stmt.setString(2, message);
                stmt.setString(3, type);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding notification: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateNotification(Notification notification) {
            String query = "UPDATE notifications SET citizen_id = ?, notification_date = ?, notification_time = ?, " +
                          "message = ?, type = ?, read_status = ? WHERE notification_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, notification.getCitizenId());
                stmt.setDate(2, notification.getNotificationDate());
                stmt.setString(3, notification.getNotificationTime());
                stmt.setString(4, notification.getMessage());
                stmt.setString(5, notification.getType());
                stmt.setString(6, notification.getReadStatus());
                stmt.setInt(7, notification.getNotificationId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating notification: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean markAsRead(int notificationId) {
            String query = "UPDATE notifications SET read_status = 'Read' WHERE notification_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, notificationId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error marking notification as read: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean markAllAsRead(int citizenId) {
            String query = "UPDATE notifications SET read_status = 'Read' WHERE citizen_id = ? AND read_status = 'Unread'";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error marking all notifications as read: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean deleteNotification(int notificationId) {
            String query = "DELETE FROM notifications WHERE notification_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, notificationId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting notification: " + e.getMessage());
                return false;
            }
        }
        
        public static int getUnreadCount(int citizenId) {
            String query = "SELECT COUNT(*) as total FROM notifications WHERE citizen_id = ? AND read_status = 'Unread'";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Error getting unread count: " + e.getMessage());
            }
            return 0;
        }
        
        public static List<Notification> searchNotifications(String searchTerm) {
            List<Notification> notifications = new ArrayList<>();
            String query = "SELECT * FROM notifications WHERE message LIKE ? OR type LIKE ? " +
                          "ORDER BY notification_date DESC, notification_time DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                String likeTerm = "%" + searchTerm + "%";
                stmt.setString(1, likeTerm);
                stmt.setString(2, likeTerm);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Notification notification = new Notification(
                        rs.getInt("notification_id"),
                        rs.getInt("citizen_id"),
                        rs.getDate("notification_date"),
                        rs.getString("notification_time"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("read_status")
                    );
                    notifications.add(notification);
                }
            } catch (SQLException e) {
                System.err.println("Error searching notifications: " + e.getMessage());
            }
            return notifications;
        }
    }
}
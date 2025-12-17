package backend.objects;

import backend.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Data {
    
    // User class for users table (unchanged)
    public static class User {
        private int userId;
        private String fname;
        private String mname;
        private String lname;
        private String username;
        private String password;
        private String role;
        private String phone;
        private String email;
        private Date createdDate;
        
        public User() {}
        
        public User(int userId, String fname, String mname, String lname, String username, String password, 
                   String role, String phone, String email, Date createdDate) {
            this.userId = userId;
            this.fname = fname;
            this.mname = mname;
            this.lname = lname;
            this.username = username;
            this.password = password;
            this.role = role;
            this.phone = phone;
            this.email = email;
            this.createdDate = createdDate;
        }
        
        // Getters and Setters (unchanged)
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
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Date getCreatedDate() { return createdDate; }
        public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
        
        // Database Functions (unchanged)
        public static User authenticate(String username, String password) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error authenticating user: " + e.getMessage());
            }
            return null;
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
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date")
                    );
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users: " + e.getMessage());
            }
            return users;
        }
        
        public static boolean addUser(User user) {
            String query = "INSERT INTO users (fname, mname, lname, username, password, role, phone, email, created_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getFname());
                stmt.setString(2, user.getMname());
                stmt.setString(3, user.getLname());
                stmt.setString(4, user.getUsername());
                stmt.setString(5, user.getPassword());
                stmt.setString(6, user.getRole());
                stmt.setString(7, user.getPhone());
                stmt.setString(8, user.getEmail());
                stmt.setDate(9, user.getCreatedDate());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding user: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateUser(User user) {
            String query = "UPDATE users SET fname = ?, mname = ?, lname = ?, username = ?, password = ?, " +
                          "role = ?, phone = ?, email = ? WHERE user_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getFname());
                stmt.setString(2, user.getMname());
                stmt.setString(3, user.getLname());
                stmt.setString(4, user.getUsername());
                stmt.setString(5, user.getPassword());
                stmt.setString(6, user.getRole());
                stmt.setString(7, user.getPhone());
                stmt.setString(8, user.getEmail());
                stmt.setInt(9, user.getUserId());
                
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
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting user: " + e.getMessage());
            }
            return null;
        }
        
        public static int getTotalUsers() {
            String query = "SELECT COUNT(*) as total FROM users";
            
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
        
        public static List<User> getUsersByRole(String role) {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users WHERE role = ? ORDER BY lname, fname";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, role);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("fname"),
                        rs.getString("mname"),
                        rs.getString("lname"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date")
                    );
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error getting users by role: " + e.getMessage());
            }
            return users;
        }
        
        public static List<User> searchUsers(String searchTerm) {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users WHERE fname LIKE ? OR mname LIKE ? OR lname LIKE ? " +
                          "OR username LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY lname, fname";
            
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
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDate("created_date")
                    );
                    users.add(user);
                }
            } catch (SQLException e) {
                System.err.println("Error searching users: " + e.getMessage());
            }
            return users;
        }
    }
    
    // Address class for addresses table (NEW)
    public static class Address {
        private int addressId;
        private int citizenId;
        private String streetAddress;
        private String city;
        private String stateProvince;
        private String zipPostalCode;
        private String country;
        
        public Address() {}
        
        public Address(int addressId, int citizenId, String streetAddress, String city, 
                      String stateProvince, String zipPostalCode, String country) {
            this.addressId = addressId;
            this.citizenId = citizenId;
            this.streetAddress = streetAddress;
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
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getStateProvince() { return stateProvince; }
        public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
        
        public String getZipPostalCode() { return zipPostalCode; }
        public void setZipPostalCode(String zipPostalCode) { this.zipPostalCode = zipPostalCode; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getFullAddress() {
            return String.format("%s, %s, %s %s, %s", 
                streetAddress, city, stateProvince, zipPostalCode, country);
        }
        
        // Database Functions
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
            String query = "INSERT INTO addresses (citizen_id, street_address, city, state_province, zip_postal_code, country) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, address.getCitizenId());
                stmt.setString(2, address.getStreetAddress());
                stmt.setString(3, address.getCity());
                stmt.setString(4, address.getStateProvince());
                stmt.setString(5, address.getZipPostalCode());
                stmt.setString(6, address.getCountry());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding address: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateAddress(Address address) {
            String query = "UPDATE addresses SET street_address = ?, city = ?, state_province = ?, " +
                          "zip_postal_code = ?, country = ? WHERE address_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, address.getStreetAddress());
                stmt.setString(2, address.getCity());
                stmt.setString(3, address.getStateProvince());
                stmt.setString(4, address.getZipPostalCode());
                stmt.setString(5, address.getCountry());
                stmt.setInt(6, address.getAddressId());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating address: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateAddressByCitizenId(int citizenId, Address address) {
            String query = "UPDATE addresses SET street_address = ?, city = ?, state_province = ?, " +
                          "zip_postal_code = ?, country = ? WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, address.getStreetAddress());
                stmt.setString(2, address.getCity());
                stmt.setString(3, address.getStateProvince());
                stmt.setString(4, address.getZipPostalCode());
                stmt.setString(5, address.getCountry());
                stmt.setInt(6, citizenId);
                
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
            String query = "INSERT INTO citizens (user_id, fname, mname, lname, national_id, birth_date, gender, phone, email, application_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // UPDATED with gender
            
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
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding citizen: " + e.getMessage());
                return false;
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
        // ... (all IDStatus code remains exactly the same as in the original file)
        // No changes needed for this class
        private int statusId;
        private String transactionId;
        private int citizenId;
        private String status;
        private Date updateDate;
        private String notes;
        
        public IDStatus() {}
        
        public IDStatus(int statusId, String transactionId, int citizenId, String status, Date updateDate, String notes) {
            this.statusId = statusId;
            this.transactionId = transactionId;
            this.citizenId = citizenId;
            this.status = status;
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
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Date getUpdateDate() { return updateDate; }
        public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        // Database Functions (unchanged)
        public static IDStatus getStatusByCitizenId(int citizenId) {
            String query = "SELECT * FROM id_status WHERE citizen_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("status"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );
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
                    // Pad with zeros to get 28 digits total
                    String padded = String.format("%028d", Long.parseLong(numbers));

                    // Format: 1234-5678-9012-3456-7890-1234-56
                    return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                        "$1-$2-$3-$4-$5-$6-$7");
                } catch (NumberFormatException e) {
                    return rawTransactionId; // Return as is if can't convert
                }
            }

            // For any other format, try to extract numbers and format
            String numbersOnly = rawTransactionId.replaceAll("[^0-9]", "");
            if (numbersOnly.length() >= 28) {
                String padded = numbersOnly.substring(0, 28);
                return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                    "$1-$2-$3-$4-$5-$6-$7");
            }

            // Pad with zeros if too short
            String padded = String.format("%-28s", numbersOnly).replace(' ', '0');
            return padded.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{4})(\\d{2})", 
                "$1-$2-$3-$4-$5-$6-$7");
        }
        
        public static IDStatus getStatusByTransactionId(String transactionId) {
            String query = "SELECT * FROM id_status WHERE transaction_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, transactionId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("status"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error getting status by transaction ID: " + e.getMessage());
            }
            return null;
        }
        
        public static IDStatus getStatusById(int statusId) {
            String query = "SELECT * FROM id_status WHERE status_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, statusId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("status"),
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
            String query = "SELECT * FROM id_status ORDER BY update_date DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("status"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );
                    statuses.add(idStatus);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all status: " + e.getMessage());
            }
            return statuses;
        }
        
        public static List<IDStatus> getStatusByStatus(String status) {
            List<IDStatus> statuses = new ArrayList<>();
            String query = "SELECT * FROM id_status WHERE status = ? ORDER BY update_date DESC";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    IDStatus idStatus = new IDStatus(
                        rs.getInt("status_id"),
                        rs.getString("transaction_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("status"),
                        rs.getDate("update_date"),
                        rs.getString("notes")
                    );
                    statuses.add(idStatus);
                }
            } catch (SQLException e) {
                System.err.println("Error getting status by status: " + e.getMessage());
            }
            return statuses;
        }
        
        public static boolean addStatus(IDStatus idStatus) {
            String query = "INSERT INTO id_status (transaction_id, citizen_id, status, update_date, notes) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, idStatus.getTransactionId());
                stmt.setInt(2, idStatus.getCitizenId());
                stmt.setString(3, idStatus.getStatus());
                stmt.setDate(4, idStatus.getUpdateDate());
                stmt.setString(5, idStatus.getNotes());
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding status: " + e.getMessage());
                return false;
            }
        }
        
        public static boolean updateStatus(IDStatus idStatus) {
            String query = "UPDATE id_status SET transaction_id = ?, citizen_id = ?, status = ?, update_date = ?, notes = ? " +
                          "WHERE status_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, idStatus.getTransactionId());
                stmt.setInt(2, idStatus.getCitizenId());
                stmt.setString(3, idStatus.getStatus());
                stmt.setDate(4, idStatus.getUpdateDate());
                stmt.setString(5, idStatus.getNotes());
                stmt.setInt(6, idStatus.getStatusId());
                
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
        // ... (all Document code remains exactly the same as in the original file)
        // No changes needed for this class
        private int documentId;
        private int citizenId;
        private String documentName;
        private String status;
        private String submitted;
        private String requiredBy;
        private String filePath;
        private Date uploadDate;
        
        public Document() {}
        
        public Document(int documentId, int citizenId, String documentName, String status,
                       String submitted, String requiredBy, String filePath, Date uploadDate) {
            this.documentId = documentId;
            this.citizenId = citizenId;
            this.documentName = documentName;
            this.status = status;
            this.submitted = submitted;
            this.requiredBy = requiredBy;
            this.filePath = filePath;
            this.uploadDate = uploadDate;
        }
        
        // Getters and Setters (unchanged)
        public int getDocumentId() { return documentId; }
        public void setDocumentId(int documentId) { this.documentId = documentId; }
        
        public int getCitizenId() { return citizenId; }
        public void setCitizenId(int citizenId) { this.citizenId = citizenId; }
        
        public String getDocumentName() { return documentName; }
        public void setDocumentName(String documentName) { this.documentName = documentName; }
        
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
        
        // Database Functions (unchanged)
        public static List<Document> getDocumentsByCitizenId(int citizenId) {
            List<Document> documents = new ArrayList<>();
            String query = "SELECT * FROM documents WHERE citizen_id = ? ORDER BY required_by, document_name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, citizenId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Document document = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("citizen_id"),
                        rs.getString("document_name"),
                        rs.getString("status"),
                        rs.getString("submitted"),
                        rs.getString("required_by"),
                        rs.getString("file_path"),
                        rs.getDate("upload_date")
                    );
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
                        rs.getString("document_name"),
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
                        rs.getString("document_name"),
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
                        rs.getString("document_name"),
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
            String query = "INSERT INTO documents (citizen_id, document_name, status, submitted, required_by, file_path, upload_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, document.getCitizenId());
                stmt.setString(2, document.getDocumentName());
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
                        rs.getString("document_name"),
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
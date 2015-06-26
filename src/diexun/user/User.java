/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diexun.user;

/**
 *
 * @author luoyuankang
 */
public class User {

    private String username;
    private String password;
    private String role;
    private String website;
    private String push_website;
    private String push_sort;
    private String push_column;
    private String push_column_type;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website the website to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @return the push_website
     */
    public String getPush_website() {
        return push_website;
    }

    /**
     * @param push_website the push_website to set
     */
    public void setPush_website(String push_website) {
        this.push_website = push_website;
    }

    /**
     * @return the push_sort
     */
    public String getPush_sort() {
        return push_sort;
    }

    /**
     * @param push_sort the push_sort to set
     */
    public void setPush_sort(String push_sort) {
        this.push_sort = push_sort;
    }

    /**
     * @return the push_column
     */
    public String getPush_column() {
        return push_column;
    }

    /**
     * @param push_column the push_column to set
     */
    public void setPush_column(String push_column) {
        this.push_column = push_column;
    }

    /**
     * @return the push_column_type
     */
    public String getPush_column_type() {
        return push_column_type;
    }

    /**
     * @param push_column_type the push_column_type to set
     */
    public void setPush_column_type(String push_column_type) {
        this.push_column_type = push_column_type;
    }

}

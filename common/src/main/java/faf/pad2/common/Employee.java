/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.common;

import java.io.Serializable;
import org.json.JSONObject;

/**
 *
 * @author andrew
 */
public class Employee implements Serializable {
    public Employee(String fn, String ln, String dep, double s) {
        this.firstName = fn;
        this.lastName = ln;
        this.department = dep;
        this.salary = s;
    }


    public Employee(JSONObject json) {
        this.firstName = json.getString("first_name");
        this.lastName = json.getString("last_name");
        this.department = json.getString("department");
        this.salary = json.getDouble("salary");
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%-10s ", firstName));
        s.append(String.format("%-10s @ ", lastName));
        s.append(department);
        s.append(" -> ");
        s.append(salary);
        return s.toString();
    }

    public String firstName;
    public String lastName;
    public String department;
    public double salary;
}

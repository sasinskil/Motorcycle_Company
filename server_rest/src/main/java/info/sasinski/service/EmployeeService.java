package info.sasinski.service;

import info.sasinski.entity.Employee;
import info.sasinski.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor

@Service
public class EmployeeService {

    final EmployeeRepository _employeeRepository;

    public long countEmployees() { return _employeeRepository.count(); }

    public Employee findEmployeeById(long id) {
        return _employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> findAll() {
        return _employeeRepository.findAll();
    }

    public void removeEmployee(long id) {
        _employeeRepository.deleteById(id);
    }

    public void saveEmployee(Employee employee) {

        if(employee.getDateOfTermination() != null) {
            if(employee.getDateOfEmployment().isBefore(employee.getDateOfTermination())) {
                _employeeRepository.save(employee);
            }
        } else {
            _employeeRepository.save(employee);
        }
    }
}

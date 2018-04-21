package ru.savimar.payroll.service;


import ru.savimar.payroll.model.AbstractEmployee;
import ru.savimar.payroll.model.EmployeeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class EmployeeService implements IAbstractEmployeeService {
    private static final BigDecimal EMPLOYEE_PERCENT = new BigDecimal(0.03).setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal EMPLOYEE_MAX = new BigDecimal(0.3).setScale(2, RoundingMode.HALF_UP);

    private final BigDecimal MANAGER_PERCENT = new BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal MANAGER_MAX = new BigDecimal(0.4).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal MANAGER_PERCENT_SUBORDINATES = new BigDecimal(0.005).setScale(3, RoundingMode.HALF_EVEN);

    private final BigDecimal SALES_PERCENT = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal SALES_MAX = new BigDecimal(0.35).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal SALES_PERCENT_SUBORDINATES = new BigDecimal(0.003).setScale(3, RoundingMode.HALF_EVEN);


    private BigDecimal salarySub = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);


    private BigDecimal calculateSalaryWithPercent(AbstractEmployee employee, LocalDate date) {

        BigDecimal percent = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;

        if (employee.getType().equals(EmployeeEnum.EMPLOYEE)) {
            percent = EMPLOYEE_PERCENT;
            max = EMPLOYEE_MAX;
        } else if (employee.getType().equals(EmployeeEnum.MANAGER)) {
            percent = MANAGER_PERCENT;
            max = MANAGER_MAX;

        } else if (employee.getType().equals(EmployeeEnum.SALES)) {
            percent = SALES_PERCENT;
            max = SALES_MAX;
        }


        int years = date.getYear() - employee.getEmploymentDate().getYear();
        if (years < 0) {
            throw new ArithmeticException("The wage calculation date is shorter than the date of employment");
        }

        BigDecimal employeePercent = BigDecimal.valueOf(years)
                .multiply(percent);

        if (employeePercent.compareTo(max) > 0) {
            employeePercent = max;
        }

        BigDecimal surcharge = employee.BASE_PAYROLL
                .multiply(employeePercent);

        return employee.BASE_PAYROLL.add(surcharge.setScale(2, RoundingMode.HALF_UP));
    }


    private BigDecimal calculateSalaryOneEmployee(AbstractEmployee employee, LocalDate date) {

        BigDecimal salary = employee.getSalary(employee, date);

        return salarySub.add(salary.setScale(2, RoundingMode.HALF_UP));
    }


    @Override
    public BigDecimal calculateSalary(AbstractEmployee employee, LocalDate date) {
        BigDecimal salary = BigDecimal.ZERO;
        if (employee.getType().equals(EmployeeEnum.EMPLOYEE)) {
            salary = calculateSalaryWithPercent(employee, date);
        } else if (employee.getType().equals(EmployeeEnum.MANAGER)) {
            BigDecimal baseSalaryManager = calculateSalaryWithPercent(employee, date);
            for (AbstractEmployee sub : employee.getSubordinates()) {
                salarySub = calculateSalaryOneEmployee(sub, date);
            }
            salarySub = salarySub.multiply(MANAGER_PERCENT_SUBORDINATES);
            salary = baseSalaryManager.add(salarySub).setScale(2, RoundingMode.HALF_UP);
        } else if (employee.getType().equals(EmployeeEnum.SALES)) {
            BigDecimal baseSalaryManager = calculateSalaryWithPercent(employee, date);

            salarySub = calculateSalaryAllSubordinates(employee.getSubordinates(), date);
            salarySub = salarySub.multiply(SALES_PERCENT_SUBORDINATES);

            salary = baseSalaryManager.add(salarySub).setScale(2, RoundingMode.HALF_UP);
        }

        return salary;
    }

    private BigDecimal calculateSalaryAllSubordinates(List<AbstractEmployee> list, LocalDate date) {
        if (list.size() > 0) {
            AbstractEmployee employee = list.get(list.size() - 1);

            salarySub = calculateSalaryOneEmployee(employee, date);

            List<AbstractEmployee> subSubordinates = employee.getSubordinates();
            if (subSubordinates != null && subSubordinates.size() > 0) {
                calculateSalaryAllSubordinates(subSubordinates, date);
            }
            list.remove(employee);
            if (list.size() > 0) {
                calculateSalaryAllSubordinates(list, date);
            }

            return salarySub.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}






package ru.savimar.test.payroll.service;

import ru.savimar.test.payroll.model.AbstractEmployee;
import ru.savimar.test.payroll.model.Manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class ManagerService extends AbstractEmployeeService implements IAbstractEmployeeService<Manager> {

    private final BigDecimal PERCENT = new BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal MAX = new BigDecimal(0.4).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal PERCENT_SUBORDINATES = new BigDecimal(0.005).setScale(3, RoundingMode.HALF_EVEN);


    @Override
    public BigDecimal calculateSalary(Manager employee, LocalDate date) {

        BigDecimal baseSalaryManager = calculateSalaryWithPercent(employee, PERCENT, MAX, date);
        for (AbstractEmployee sub : employee.getSubordinates()) {
         salarySub =  calculateSalaryOneEmployee(sub, date);
        }
        salarySub = salarySub.multiply(PERCENT_SUBORDINATES);
        return baseSalaryManager.add(salarySub).setScale(2, RoundingMode.HALF_UP);
    }


}

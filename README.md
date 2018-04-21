# PayrollCalculation


This application consists of 2 layers: model and service.
In the layer of model, there are enum for types of employees and Employee, Manager, Sales classes, which are extended from abstract class of AbstractEmployee. In it there are following fields:
private Long id;
private String name;
private LocalDate employmentDate;
private EmployeeEnum type;
private List<AbstractEmployee> subordinates;

 Subordinates field is overridden for Employee so as empty immutable list to be written and null to return, because an object of this class cannot have subordinates by its order.
Also in this class there is a final field public final BigDecimal BASE_PAYROLL, in which we have base salary. This is a simplification, although in fact  it should be taken out of configs.
In abstract class there is abstract method 
public abstract BigDecimal getSalary(AbstractEmployee employee, LocalDate date),
which is overridden in class-heirs. It recalls the interface of the layer of service for each class. This method is used for payroll calculation.
In service layer there is EmployeeService, in which payroll calculation is realized for each type of employees. It implement generic interface IAbstractEmployeeService.
Parameters for payroll calculation are put into constants for each class. EmployeeService class calculates payrolls for ordinary employees without their subordinates. ManagerService class calculates basic payrolls only for manager and in the cycle calculates general payrolls for his subordinates of the first level. SalesService class also consists of the sum of basic payroll and payroll for subordinates of all the levels, in this method of calculation recursive technique is used.
In this case problems with scalability may occur. 

Есть компания, у компании могут быть сотрудники. Сотрудник характеризуется именем, датой поступления на работу, базовой ставкой (для простоты, это значение по умолчанию одинаково для всех видов сотрудников).
Сотрудники бывают 3 видов: Employee, Manager, Sales. У каждого сотрудника может быть начальник. У каждого сотрудника, кроме Employee, могут быть подчинённые.
Зарплата сотрудника Employee - это базовая ставка плюс 3% за каждый год работы в компании, но не больше 30% от базовой ставки.
Зарплата сотрудника Manager - это базовая ставка плюс 5% за каждый год работы в компании (но не больше 40% от базовой ставки) плюс 0,5% зарплаты всех подчинённых первого уровня
Зарплата сотрудника Sales - это базовая ставка плюс 1% за каждый год работы в компании (но не больше 35% от базовой ставки) плюс 0,3% зарплаты всех подчинённых  всех уровней
У сотрудников (кроме Employee) может быть любое количество подчинённых любого вида.
Требуется: составить библиотеку классов, описывающих данную модель, а также реализовать алгоритм расчета зарплаты каждого сотрудника на произвольную дату (а также подсчёт суммарной зарплаты всех сотрудников фирмы в целом).
Система должна быть проверена unit-testами (не обязательно полное покрытие, но должны быть показательные тесты для проверки бизнес-логики).
Кроме того, требуется написать (на английском) краткий обзор своего решения тестовой задачи, описав архитектуру, ее плюсы и минусы (что можно улучшить или поменять или еще какие-то соображения для использования решения в реальных целях).
Из сторонних библиотек можно использовать только JUnit. СУБД не нужны.



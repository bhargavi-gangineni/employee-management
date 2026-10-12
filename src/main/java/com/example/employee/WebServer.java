package com.example.employee;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class WebServer {

    private static final EmployeeManager manager = new EmployeeManager();

    public static void main(String[] args) throws IOException {

        loadSampleEmployees();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8081),
                0
        );

        server.createContext("/", WebServer::handleRequest);

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));
        server.start();

        System.out.println(
                "Employee Management Web Server started on port 8081"
        );
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void loadSampleEmployees() {

        manager.addEmployee(new Employee(
                101,
                "Ravi",
                "ravi@example.com",
                "IT",
                50000
        ));

        manager.addEmployee(new Employee(
                102,
                "Ramya",
                "ramya@example.com",
                "HR",
                45000
        ));

        manager.addEmployee(new Employee(
                103,
                "Priya",
                "priya@example.com",
                "Finance",
                55000
        ));

        manager.addEmployee(new Employee(
                104,
                "Arjun",
                "arjun@example.com",
                "IT",
                60000
        ));
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        try {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equalsIgnoreCase(method) && "/".equals(path)) {
            showHomePage(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && "/add".equals(path)) {
            showAddForm(exchange);
            return;
        }

        if ("POST".equalsIgnoreCase(method) && "/add".equals(path)) {
            addEmployee(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && "/edit".equals(path)) {
            showEditForm(exchange);
            return;
        }

        if ("POST".equalsIgnoreCase(method) && "/edit".equals(path)) {
            updateEmployee(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && "/delete".equals(path)) {
            deleteEmployee(exchange);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && "/view".equals(path)) {
            viewEmployee(exchange);
            return;
        }

        sendResponse(
                exchange,
                "<h2>404 - Page Not Found</h2>",
                404
        );

        } catch (Exception e) {
        e.printStackTrace();

        sendResponse(
                exchange,
                "<h2>Internal Server Error</h2><p>Check server console.</p>",
                500
        );
    } }

    private static void showHomePage(HttpExchange exchange)
            throws IOException {

        String search = getQueryParameter(
                exchange.getRequestURI().getRawQuery(),
                "search"
        );

        List<Employee> employees = manager.searchEmployees(search);

        StringBuilder employeeRows = new StringBuilder();

        for (Employee employee : employees) {

            employeeRows.append("""
                    <tr>
                        <td>%d</td>
                        <td>
                            <div class="employee-name">%s</div>
                            <div class="employee-email">%s</div>
                        </td>
                        <td><span class="department">%s</span></td>
                        <td>₹%.2f</td>
                        <td>
                            <div class="actions">
                                <a class="btn view"
                                   href="/view?id=%d">View</a>

                                <a class="btn edit"
                                   href="/edit?id=%d">Edit</a>

                                <a class="btn delete"
                                   href="/delete?id=%d"
                                   onclick="return confirm('Delete this employee?');">
                                   Delete
                                </a>
                            </div>
                        </td>
                    </tr>
                    """.formatted(
                    employee.getId(),
                    escapeHtml(employee.getName()),
                    escapeHtml(employee.getEmail()),
                    escapeHtml(employee.getDepartment()),
                    employee.getSalary(),
                    employee.getId(),
                    employee.getId(),
                    employee.getId()
            ));
        }

        if (employeeRows.length() == 0) {
            employeeRows.append("""
                    <tr>
                        <td colspan="5" class="empty">
                            No employees found.
                        </td>
                    </tr>
                    """);
        }

        String response = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Employee Management System</title>

                    <style>

                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;
                            background: #f4f5f7;
                            color: #222;
                        }

                        .header {
                            background: #111;
                            color: white;
                            padding: 18px 40px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                        }

                        .logo {
                            font-size: 22px;
                            font-weight: bold;
                        }

                        .header-subtitle {
                            color: #bbb;
                            font-size: 13px;
                        }

                        .container {
                            max-width: 1200px;
                            margin: 35px auto;
                            padding: 0 20px;
                        }

                        .welcome {
                            margin-bottom: 25px;
                        }

                        .welcome h1 {
                            margin-bottom: 6px;
                            font-size: 30px;
                        }

                        .welcome p {
                            color: #1f2020;
                            margin-top: 0;
                        }

                        .stats {
                            display: grid;
                            grid-template-columns:
                                repeat(3, 1fr);
                            gap: 20px;
                            margin-bottom: 30px;
                        }

                        .stat-card {
                            background: white;
                            padding: 22px;
                            border-radius: 10px;
                            border: 1px solid #ddd;
                        }

                        .stat-title {
                            color: #777;
                            font-size: 14px;
                            margin-bottom: 8px;
                        }

                        .stat-value {
                            font-size: 28px;
                            font-weight: bold;
                        }

                        .main-card {
                            background: white;
                            border-radius: 10px;
                            border: 1px solid #ddd;
                            overflow: hidden;
                        }

                        .card-header {
                            padding: 22px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            border-bottom: 1px solid #eee;
                        }

                        .card-header h2 {
                            margin: 0;
                        }

                        .search-area {
                            display: flex;
                            gap: 10px;
                        }

                        .search-input {
                            padding: 10px 14px;
                            border: 1px solid #ccc;
                            border-radius: 6px;
                            min-width: 260px;
                        }

                        .btn {
                            text-decoration: none;
                            padding: 8px 12px;
                            border-radius: 5px;
                            font-size: 13px;
                            display: inline-block;
                            border: none;
                            cursor: pointer;
                        }

                        .primary {
                            background: #111;
                            color: white;
                        }

                        .view {
                            background: #eee;
                            color: #111;
                        }

                        .edit {
                            background: #ddd;
                            color: #111;
                        }

                        .delete {
                            background: #111;
                            color: white;
                        }

                        .actions {
                            display: flex;
                            gap: 6px;
                        }

                        table {
                            width: 100%%;
                            border-collapse: collapse;
                        }

                        th {
                            background: #f7f7f7;
                            text-align: left;
                            padding: 14px;
                            font-size: 13px;
                            color: #666;
                        }

                        td {
                            padding: 15px 14px;
                            border-top: 1px solid #eee;
                            font-size: 14px;
                        }

                        .employee-name {
                            font-weight: bold;
                            margin-bottom: 4px;
                        }

                        .employee-email {
                            font-size: 12px;
                            color: #888;
                        }

                        .department {
                            background: #f0f0f0;
                            padding: 5px 9px;
                            border-radius: 15px;
                            font-size: 12px;
                        }

                        .empty {
                            text-align: center;
                            padding: 40px;
                            color: #777;
                        }

                        .footer {
                            text-align: center;
                            color: #888;
                            padding: 25px;
                            font-size: 12px;
                        }

                        @media (max-width: 800px) {

                            .header {
                                padding: 18px 20px;
                            }

                            .stats {
                                grid-template-columns: 1fr;
                            }

                            .card-header {
                                flex-direction: column;
                                align-items: stretch;
                                gap: 15px;
                            }

                            .search-area {
                                width: 100%%;
                            }

                            .search-input {
                                min-width: 0;
                                width: 100%%;
                            }

                            .main-card {
                                overflow-x: auto;
                            }

                            table {
                                min-width: 800px;
                            }
                        }

                    </style>
                </head>

                <body>

                    <header class="header">
                        <div class="logo">
                            Employee Management
                        </div>

                        <div class="header-subtitle">
                            Management Dashboard
                        </div>
                    </header>

                    <main class="container">

                        <section class="welcome">
                            <h1>Employee Dashboard</h1>
                            <p>
                                Manage employee information from one place.
                            </p>
                        </section>

                        <section class="stats">

                            <div class="stat-card">
                                <div class="stat-title">
                                    Total Employees
                                </div>

                                <div class="stat-value">
                                    %d
                                </div>
                            </div>

                            <div class="stat-card">
                                <div class="stat-title">
                                    Departments
                                </div>

                                <div class="stat-value">
                                    %d
                                </div>
                            </div>

                            <div class="stat-card">
                                <div class="stat-title">
                                    Application Status
                                </div>

                                <div class="stat-value">
                                    Active
                                </div>
                            </div>

                        </section>

                        <section class="main-card">

                            <div class="card-header">

                                <h2>Employees</h2>

                                <div class="search-area">

                                    <form method="get" action="/" onsubmit="return false;">
                                        <input
                                            class="search-input"
                                            type="text"
                                            id="employeeSearch"
                                            name="search"
                                            placeholder="Search employee..."
                                            value="%s"
                                            autocomplete="off">

                                        <button class="btn primary"
                                                type="button"
                                                onclick="filterEmployees()">
                                            Search
                                        </button>
                                    </form>

                                    <a class="btn primary"
                                       href="/add">
                                        + Add Employee
                                    </a>

                                </div>

                            </div>

                            <table>

                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Employee</th>
                                        <th>Department</th>
                                        <th>Salary</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    %s
                                </tbody>

                            </table>

                        </section>

                    </main>

                    <footer class="footer">
                        Employee Management System | DevOps Demo Application
                    </footer>

                <script>
                    function filterEmployees() {
                        const searchInput = document.getElementById("employeeSearch");
                        const searchText = searchInput.value.toLowerCase().trim();
                        const rows = document.querySelectorAll("tbody tr");

                        rows.forEach(row => {
                            const rowText = row.textContent.toLowerCase();

                            if (rowText.includes(searchText)) {
                                row.style.display = "";
                            } else {
                                row.style.display = "none";
                            }
                        });
                    }

                    document.getElementById("employeeSearch").addEventListener("input", filterEmployees);
                </script>

                </body>
                </html>
                """.formatted(
                manager.getEmployeeCount(),
                getDepartmentCount(),
                escapeHtml(search == null ? "" : search),
                employeeRows
        );

        sendResponse(exchange, response, 200);
    }

    private static void showAddForm(HttpExchange exchange)
            throws IOException {

        String response = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Add Employee</title>

                    <style>
                        body {
                            margin: 0;
                            font-family: Arial, sans-serif;
                            background: #f4f5f7;
                            color: #222;
                        }

                        .header {
                            background: #111;
                            color: white;
                            padding: 20px 40px;
                            font-size: 22px;
                            font-weight: bold;
                        }

                        .form-container {
                            max-width: 650px;
                            margin: 45px auto;
                            background: white;
                            padding: 35px;
                            border-radius: 10px;
                            border: 1px solid #ddd;
                        }

                        h1 {
                            margin-top: 0;
                        }

                        .form-group {
                            margin-bottom: 18px;
                        }

                        label {
                            display: block;
                            margin-bottom: 7px;
                            font-weight: bold;
                            font-size: 14px;
                        }

                        input {
                            width: 100%%;
                            padding: 11px;
                            border: 1px solid #ccc;
                            border-radius: 6px;
                            font-size: 14px;
                        }

                        .buttons {
                            margin-top: 25px;
                            display: flex;
                            gap: 10px;
                        }

                        button,
                        a {
                            padding: 11px 18px;
                            border-radius: 6px;
                            text-decoration: none;
                            cursor: pointer;
                            border: none;
                            font-size: 14px;
                        }

                        button {
                            background: #111;
                            color: white;
                        }

                        .cancel {
                            background: #eee;
                            color: #111;
                        }
                    </style>

                </head>

                <body>

                    <div class="header">
                        Employee Management
                    </div>

                    <div class="form-container">

                        <h1>Add Employee</h1>

                        <form method="post" action="/add">

                            <div class="form-group">
                                <label>Employee ID</label>
                                <input type="number"
                                       name="id"
                                       required>
                            </div>

                            <div class="form-group">
                                <label>Name</label>
                                <input type="text"
                                       name="name"
                                       required>
                            </div>

                            <div class="form-group">
                                <label>Email</label>
                                <input type="email"
                                       name="email"
                                       required>
                            </div>

                            <div class="form-group">
                                <label>Department</label>
                                <input type="text"
                                       name="department"
                                       required>
                            </div>

                            <div class="form-group">
                                <label>Salary</label>
                                <input type="number"
                                       name="salary"
                                       step="0.01"
                                       required>
                            </div>

                            <div class="buttons">

                                <button type="submit">
                                    Add Employee
                                </button>

                                <a class="cancel" href="/">
                                    Cancel
                                </a>

                            </div>

                        </form>

                    </div>

                </body>
                </html>
                """;

        sendResponse(exchange, response, 200);
    }

    private static void addEmployee(HttpExchange exchange)
            throws IOException {

        Map<String, String> formData =
                parseFormData(exchange);

        try {

            int id = Integer.parseInt(formData.get("id"));

            if (manager.findEmployeeById(id) != null) {
                sendResponse(
                        exchange,
                        "<h2>Employee ID already exists.</h2>" +
                        "<a href='/'>Back</a>",
                        400
                );
                return;
            }

            String name = formData.get("name");
            String email = formData.get("email");
            String department = formData.get("department");
            double salary =
                    Double.parseDouble(formData.get("salary"));

            manager.addEmployee(
                    new Employee(
                            id,
                            name,
                            email,
                            department,
                            salary
                    )
            );

            redirect(exchange, "/");

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "<h2>Invalid employee information.</h2>" +
                    "<a href='/add'>Try Again</a>",
                    400
            );
        }
    }

    private static void showEditForm(HttpExchange exchange)
            throws IOException {

        String idValue = getQueryParameter(
                exchange.getRequestURI().getRawQuery(),
                "id"
        );

        try {

            int id = Integer.parseInt(idValue);
            Employee employee =
                    manager.findEmployeeById(id);

            if (employee == null) {
                sendResponse(
                        exchange,
                        "<h2>Employee not found.</h2>" +
                        "<a href='/'>Back</a>",
                        404
                );
                return;
            }

            String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width,
                              initial-scale=1.0">

                        <title>Edit Employee</title>

                        <style>
                            body {
                                margin: 0;
                                font-family: Arial, sans-serif;
                                background: #f4f5f7;
                                color: #222;
                            }

                            .header {
                                background: #111;
                                color: white;
                                padding: 20px 40px;
                                font-size: 22px;
                                font-weight: bold;
                            }

                            .form-container {
                                max-width: 650px;
                                margin: 45px auto;
                                background: white;
                                padding: 35px;
                                border-radius: 10px;
                                border: 1px solid #ddd;
                            }

                            .form-group {
                                margin-bottom: 18px;
                            }

                            label {
                                display: block;
                                margin-bottom: 7px;
                                font-weight: bold;
                                font-size: 14px;
                            }

                            input {
                                width: 100%%;
                                padding: 11px;
                                border: 1px solid #ccc;
                                border-radius: 6px;
                                font-size: 14px;
                            }

                            .buttons {
                                margin-top: 25px;
                                display: flex;
                                gap: 10px;
                            }

                            button,
                            a {
                                padding: 11px 18px;
                                border-radius: 6px;
                                text-decoration: none;
                                cursor: pointer;
                                border: none;
                                font-size: 14px;
                            }

                            button {
                                background: #111;
                                color: white;
                            }

                            .cancel {
                                background: #eee;
                                color: #111;
                            }
                        </style>

                    </head>

                    <body>

                        <div class="header">
                            Employee Management
                        </div>

                        <div class="form-container">

                            <h1>Edit Employee</h1>

                            <form method="post" action="/edit">

                                <input type="hidden"
                                       name="id"
                                       value="%d">

                                <div class="form-group">
                                    <label>Name</label>
                                    <input type="text"
                                           name="name"
                                           value="%s"
                                           required>
                                </div>

                                <div class="form-group">
                                    <label>Email</label>
                                    <input type="email"
                                           name="email"
                                           value="%s"
                                           required>
                                </div>

                                <div class="form-group">
                                    <label>Department</label>
                                    <input type="text"
                                           name="department"
                                           value="%s"
                                           required>
                                </div>

                                <div class="form-group">
                                    <label>Salary</label>
                                    <input type="number"
                                           name="salary"
                                           value="%.2f"
                                           step="0.01"
                                           required>
                                </div>

                                <div class="buttons">

                                    <button type="submit">
                                        Save Changes
                                    </button>

                                    <a class="cancel" href="/">
                                        Cancel
                                    </a>

                                </div>

                            </form>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                    employee.getId(),
                    escapeHtml(employee.getName()),
                    escapeHtml(employee.getEmail()),
                    escapeHtml(employee.getDepartment()),
                    employee.getSalary()
            );

            sendResponse(exchange, response, 200);

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "<h2>Invalid employee ID.</h2>" +
                    "<a href='/'>Back</a>",
                    400
            );
        }
    }

    private static void updateEmployee(HttpExchange exchange)
            throws IOException {

        Map<String, String> formData =
                parseFormData(exchange);

        try {

            int id = Integer.parseInt(formData.get("id"));

            String name = formData.get("name");
            String email = formData.get("email");
            String department = formData.get("department");
            double salary =
                    Double.parseDouble(formData.get("salary"));

            boolean updated =
                    manager.updateEmployee(
                            id,
                            name,
                            email,
                            department,
                            salary
                    );

            if (!updated) {
                sendResponse(
                        exchange,
                        "<h2>Employee not found.</h2>" +
                        "<a href='/'>Back</a>",
                        404
                );
                return;
            }

            redirect(exchange, "/");

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "<h2>Invalid employee information.</h2>" +
                    "<a href='/'>Back</a>",
                    400
            );
        }
    }

    private static void deleteEmployee(HttpExchange exchange)
            throws IOException {

        String idValue = getQueryParameter(
                exchange.getRequestURI().getRawQuery(),
                "id"
        );

        try {

            int id = Integer.parseInt(idValue);

            manager.deleteEmployee(id);

            redirect(exchange, "/");

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "<h2>Invalid employee ID.</h2>" +
                    "<a href='/'>Back</a>",
                    400
            );
        }
    }

    private static void viewEmployee(HttpExchange exchange)
            throws IOException {

        String idValue = getQueryParameter(
                exchange.getRequestURI().getRawQuery(),
                "id"
        );

        try {

            int id = Integer.parseInt(idValue);

            Employee employee =
                    manager.findEmployeeById(id);

            if (employee == null) {
                sendResponse(
                        exchange,
                        "<h2>Employee not found.</h2>" +
                        "<a href='/'>Back</a>",
                        404
                );
                return;
            }

            String response = """
                    <!DOCTYPE html>
                    <html>
                    <head>

                        <meta charset="UTF-8">

                        <meta name="viewport"
                              content="width=device-width,
                              initial-scale=1.0">

                        <title>Employee Details</title>

                        <style>

                            body {
                                margin: 0;
                                font-family: Arial, sans-serif;
                                background: #f4f5f7;
                            }

                            .header {
                                background: #111;
                                color: white;
                                padding: 20px 40px;
                                font-size: 22px;
                                font-weight: bold;
                            }

                            .card {
                                max-width: 650px;
                                margin: 50px auto;
                                background: white;
                                padding: 35px;
                                border-radius: 10px;
                                border: 1px solid #ddd;
                            }

                            .detail {
                                padding: 15px 0;
                                border-bottom: 1px solid #eee;
                            }

                            .label {
                                color: #777;
                                font-size: 13px;
                                margin-bottom: 5px;
                            }

                            .value {
                                font-size: 17px;
                                font-weight: bold;
                            }

                            .back {
                                display: inline-block;
                                margin-top: 25px;
                                background: #111;
                                color: white;
                                padding: 11px 18px;
                                border-radius: 6px;
                                text-decoration: none;
                            }

                        </style>

                    </head>

                    <body>

                        <div class="header">
                            Employee Management
                        </div>

                        <div class="card">

                            <h1>Employee Details</h1>

                            <div class="detail">
                                <div class="label">Employee ID</div>
                                <div class="value">%d</div>
                            </div>

                            <div class="detail">
                                <div class="label">Name</div>
                                <div class="value">%s</div>
                            </div>

                            <div class="detail">
                                <div class="label">Email</div>
                                <div class="value">%s</div>
                            </div>

                            <div class="detail">
                                <div class="label">Department</div>
                                <div class="value">%s</div>
                            </div>

                            <div class="detail">
                                <div class="label">Salary</div>
                                <div class="value">₹%.2f</div>
                            </div>

                            <a class="back" href="/">
                                Back to Employees
                            </a>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                    employee.getId(),
                    escapeHtml(employee.getName()),
                    escapeHtml(employee.getEmail()),
                    escapeHtml(employee.getDepartment()),
                    employee.getSalary()
            );

            sendResponse(exchange, response, 200);

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "<h2>Invalid employee ID.</h2>" +
                    "<a href='/'>Back</a>",
                    400
            );
        }
    }

    private static int getDepartmentCount() {

        return (int) manager.getAllEmployees()
                .stream()
                .map(Employee::getDepartment)
                .distinct()
                .count();
    }

    private static Map<String, String> parseFormData(
            HttpExchange exchange) throws IOException {

        String body =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> data = new HashMap<>();

        if (body.isEmpty()) {
            return data;
        }

        for (String pair : body.split("&")) {

            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );

                data.put(key, value);
            }
        }

        return data;
    }

    private static String getQueryParameter(
            String query,
            String parameter) {

        if (query == null || query.isEmpty()) {
            return null;
        }

        for (String pair : query.split("&")) {

            String[] parts = pair.split("=", 2);

            if (parts.length == 2 &&
                    parts[0].equals(parameter)) {

                return URLDecoder.decode(
                        parts[1],
                        StandardCharsets.UTF_8
                );
            }
        }

        return null;
    }

    private static String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void redirect(
            HttpExchange exchange,
            String location) throws IOException {

        exchange.getResponseHeaders()
                .set("Location", location);

        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int statusCode) throws IOException {

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set("Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (OutputStream outputStream =
                     exchange.getResponseBody()) {

            outputStream.write(bytes);
        }
    }
}

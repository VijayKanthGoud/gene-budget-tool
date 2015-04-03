<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Delete from report</title>
</head>
<body>
	<form action="/delete">
  <select name="name">
    <option value="budgetsummary">BudgetSummary</option>
    <option value="gtfreport">GTFReport</option>
    <option value="costcenterbrand">CostCenter_Brand</option>
    <option value="userroleinfo">UserRoleInfo</option>
  </select>
  <input type="submit" value="Delete">
</form>
</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/5/10 0010
  Time: 15:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>echats</title>
    <script src="<%=request.getContextPath()%>/assets/js/echarts.js"></script>
</head>
<body>


<table id="sample-table-1" class="table table-striped table-bordered table-hover">
    <thead>
    <tr>
        <th width="15%">学生姓名</th>
        <th width="15%">学生成绩</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <c:forEach items="${listName}" var="name">
            <%--<td><a href="#">${mame}</a></td>--%>
            <td><a href="#">${name}</a></td>
        </c:forEach>
    </tr>
    <tr>
        <c:forEach items="${listMark}" var="mark">
            <td><a href="#">${mark}</a></td>
        </c:forEach>
    </tr>
    </tbody>
</table>


<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 600px;height:400px;"></div>
<script>
    var myChart = echarts.init(document.getElementById('main'));
    // 显示标题，图例和空的坐标轴
    myChart.setOption({
        title: {
            text: '成绩展示'
        },
        tooltip: {},
        legend: {
            data:['分数']
        },
        xAxis: {
            data: []
        },
        yAxis: {},
        series: [{
            name: '分数',
            type: 'bar',
            data: []
        }]
    });

    // 异步加载数据
    var name="${listName}";
//    alert(name.split(",")+":"+typeof name.split(","));
    var mark="${listMark}";
    myChart.setOption({
        xAxis:{data:name.slice(1,name.length-1).split(",")},
        series:[{
            name:'分数',
            data:mark.slice(1,mark.length-1).split(",")
        }]
    })
</script>
</body>
</html>

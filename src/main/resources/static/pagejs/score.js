let layerIndex;
let $;
let table;

// 1. 弹出成绩新增/更新对话框
function showScoreDlg(id) {
    let title = "录入成绩";

    // 重置表单
    $("#scoreForm")[0].reset();
    $("#id").val("");
    $("#btnOK").prop("disabled", false).removeClass("layui-btn-disabled");

    if (id) {
        title = "修改成绩";
        $.ajax({
        });
    }

    layerIndex = layer.open({
        type: 1,
        title: title,
        area: ['450px', 'auto'],
        content: $('#scoreForm')
    });
}

// 获取搜索条件
function getSearchCondition() {
    let keyword = $('input[name="keyword"]').val();
    return {
        keyword: keyword // 对应 QueryObj 中的 keyword 字段
    };
}

layui.use(function () {
    $ = layui.$;
    table = layui.table;
    let form = layui.form;

    // (1) 监听表单提交
    form.on('submit(score-dlg)', function (data) {
        commitScoreDlg();
        return false;
    });

    // (2) 表格初始化
    let queryParams = getSearchCondition();

    table.render({
        elem: '#tbScore',
        url: '/api/score', // 对应 ScoreApiController.list
        method: 'GET',     // 注意：之前 Controller 写的是 @GetMapping
        request: {
            pageName: 'page', // 页码的参数名称，默认：page
            limitName: 'limit' // 每页数据量的参数名，默认：limit
        },
        // 这里的 where 会被 Spring 自动映射到 QueryObj 对象中
        where: queryParams,

        page: true,
        cols: [[
            {type: 'checkbox', fixed: 'left'},
            {field: 'id', title: 'ID', width: 80, sort: true},

            // 显示关联对象的信息
            {field: 'studentName', title: '学生姓名',sort: true, templet: function(d){
                    // 增加空值保护，防止 null 报错
                    return (d.student && d.student.name) ? d.student.name : '<span style="color:#ccc">未知</span>';
                }},
            {field: 'courseName', title: '课程名称', sort: true,templet: function(d){
                    return (d.course && d.course.name) ? d.course.name : '<span style="color:#ccc">未知</span>';
                }},

            {field: 'score', title: '分数', sort: true, templet: function(d){
                    if(d.score < 60) return '<span style="color:red;font-weight:bold">'+d.score+'</span>';
                    if(d.score >= 90) return '<span style="color:green;font-weight:bold">'+d.score+'</span>';
                    return d.score;
                }},

            {fixed: 'right', title: '操作', width: 150, templet: '#editTemplate'}
        ]]
    });

    // (3) 工具条事件
    table.on('sort(tbScore)', function(obj){
        // obj.field: 当前排序的字段名 (如 id, score, studentName)
        // obj.type: 当前排序类型：desc（降序）、asc（升序）、null（空对象，默认排序）

        let sortField = obj.field;
        let sortOrder = obj.type;

        // 获取当前的搜索关键词，确保排序时不丢失搜索条件
        let params = getSearchCondition();

        // 重新加载表格，携带排序参数
        table.reload('tbScore', {
            initSort: obj, // 记录初始排序，让表头显示正确的箭头状态
            where: {
                keyword: params.keyword, // 保持搜索关键词
                sortField: sortField,    // 告诉后端按哪个字段排
                sortOrder: sortOrder     // 告诉后端是升序还是降序
            }
        });
    });

    table.on('tool(tbScore)', function(obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            // 编辑逻辑：直接用弹窗回显
            showScoreDlg(data.id);
            // 手动回填数据 (因为 student.id 是嵌套的)
            form.val('scoreFormFilter', {
                "id": data.id,
                "score": data.score,
                "student.id": data.student ? data.student.id : "",
                "course.id": data.course ? data.course.id : ""
            });
        } else if (obj.event === 'del') {
            deleteById(data.id);
        }
    });
});

// 查询按钮
function search() {
    let params = getSearchCondition();
    table.reloadData("tbScore", {
        where: params,
        page: { curr: 1 }
    });
}

// 提交数据
function commitScoreDlg(){
    let id = $("#id").val();
    let formData = $("#scoreForm").serialize(); // 序列化表单数据

    $("#btnOK").prop("disabled", true).addClass("layui-btn-disabled");

    let url = "/api/score";
    let method = (id) ? "PUT" : "POST"; // 根据是否有ID判断是新增还是修改

    // 注意：后端 @RequestBody Score score 接收的是 JSON
    // 但 jQuery .serialize() 生成的是 k=v&k=v 格式
    // 所以我们需要把表单转成 JSON 对象，或者修改后端接收方式。
    // 为了方便，这里我们在前端把 form 转 JSON
    let dataObj = {};
    let formArray = $("#scoreForm").serializeArray();

    // 构建嵌套对象 student: {id: 1}, course: {id: 2}
    let studentId = null;
    let courseId = null;

    $.each(formArray, function() {
        if(this.name === "student.id") studentId = this.value;
        else if (this.name === "course.id") courseId = this.value;
        else dataObj[this.name] = this.value;
    });

    // 手动构造符合后端结构的 JSON
    let payload = {
        id: dataObj.id,
        score: dataObj.score,
        student: { id: studentId },
        course: { id: courseId }
    };

    $.ajax({
        url: url,
        method: method,
        contentType: "application/json",
        data: JSON.stringify(payload)
    }).done(function(result) {
        if (result.code === 1) { // REnum.SUCCESS = 1
            layer.msg("操作成功", {icon: 1});
            if(layerIndex) layer.close(layerIndex);
            search();
        } else {
            layer.msg("操作失败: " + result.msg, {icon: 2});
        }
    }).always(function() {
        $("#btnOK").prop("disabled", false).removeClass("layui-btn-disabled");
    });
}

// 单个删除
function deleteById(id){
    layer.confirm('确认删除该成绩记录？', {icon: 3}, function(index){
        $.ajax({
            url: "/api/score/" + id,
            method: "DELETE"
        }).done(function(result){
            if(result.code === 1){
                layer.msg("删除成功");
                search();
            } else {
                layer.msg("删除失败: " + result.msg);
            }
        });
        layer.close(index);
    });
}

// 批量删除
function deleteConfirm() {
    var checkStatus = table.checkStatus('tbScore');
    var data = checkStatus.data;

    if (data.length === 0) {
        layer.msg('请先选择要删除的数据');
        return;
    }

    layer.confirm('确定要删除选中的 ' + data.length + ' 条数据吗？', {icon: 3}, function(index){
        let ids = data.map(item => item.id);

        $.ajax({
            url: "/api/score/batch",
            method: "DELETE",
            contentType: "application/json",
            data: JSON.stringify(ids)
        }).done(function(result) {
            if(result.code === 1){
                layer.msg("删除成功");
                search();
            } else {
                layer.msg("删除失败");
            }
        });
        layer.close(index);
    });
}
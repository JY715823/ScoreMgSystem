let layerIndex;
let $;
let table;

// 弹出学生新增/更新对话框
function showStudentDlg(id){
    let title = "新增学生";

    // 重置表单
    $("#studForm")[0].reset();
    $("#id").val("");
    $("#btnOK").prop("disabled", false).removeClass("layui-btn-disabled");

    if(id){
        title = "编辑学生";
        $.ajax({
            url: "/api/student/" + id,
            method: "GET"
        }).done(function(result) {
            // 【修正1】result.code 必须匹配 RUtil 中的 0
            if (result.code === 0 && result.data) {
                // 自动回显数据
                layui.form.val('studFormFilter', result.data);
            }
        });
    }

    layerIndex = layer.open({
        type: 1,
        title: title,
        area: ['520px','auto'],
        content: $('#studForm')
    });
}

function getSearchCondition() {
    let formData = {};
    $('#queryForm').find('input, select').each(function () {
        let name = $(this).attr('name');
        let value = $(this).val();
        if (name && value) {
            formData[name] = value;
        }
    });
    return formData;
}

layui.use(function () {
    $ = layui.$;
    table = layui.table;

    // (1) 提交表单监听
    layui.form.on('submit(stud-dlg)', function (data) {
        commitStuDlg();
        return false;
    });

    // (2) 表格初始化
    let student = getSearchCondition();
    table.render({
        elem: '#tbStudent',
        url: '/api/student/getbypage',
        method: 'POST',
        contentType: 'application/json',
        where: {"data": student},
        page: true,
        autoSort: false, // 禁用前端排序，走后端排序
        cols: [[
            {type: 'checkbox', fixed: 'left'},
            {field: 'id', fixed: 'left', width: 80, title: 'ID', sort: true},
            {field: 'sNo', title: '学号', width: 150, sort: true},
            {field: 'name', title: '姓名'},
            {field: 'sex', width: 80, title: '性别', sort: true,
                templet: d => d.sex === 1 ? '男' : (d.sex === 2 ? '女' : '未知')
            },
            {field: 'age', width: 100, title: '年龄', sort: true},
            {fixed: 'right', title: '操作', width: 150, templet: '#editTemplate'}
        ]]
    });

    // (3) 监听排序事件（后端全局排序）
    table.on('sort(tbStudent)', function(obj){
        let sortField = obj.field;
        let sortOrder = obj.type;

        let searchData = getSearchCondition();

        table.reload('tbStudent', {
            initSort: obj,
            where: {
                data: searchData,
                sortField: sortField,
                sortOrder: sortOrder
            }
        });
    });

    // (4) 工具条事件
    table.on('tool(tbStudent)', function(obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            showStudentDlg(data.id);
        } else if (obj.event === 'del') {
            deleteById(data.id);
        }
    });
});

function search() {
    let student = getSearchCondition();
    table.reloadData("tbStudent", {
        where: {
            data: student,
            sortField: null,
            sortOrder: null
        },
        page: { curr: 1 }
    });
}

function deleteConfirm() {
    var checkStatus = table.checkStatus('tbStudent');
    var data = checkStatus.data;

    if (data.length === 0) {
        layer.msg('请先选择要删除的数据');
        return;
    }

    layer.confirm('确定要删除选中的 ' + data.length + ' 条数据吗？', {icon: 3, title:'提示'}, function(index){
        let ids = data.map(item => item.id);

        $.ajax({
            url: "/api/student/deletebatch",
            method: "DELETE",
            contentType: "application/json",
            data: JSON.stringify(ids)
        }).done(function(result) {
            // 【修正2】批量删除成功判断 code === 0
            if(result.code === 0){
                layer.msg("删除成功");
                search();
            } else {
                layer.msg("删除失败: " + result.msg);
            }
        });

        layer.close(index);
    });
}

function commitStuDlg(){
    let id = $("#id").val();
    let formData = $("#studForm").serialize();

    $("#btnOK").prop("disabled", true).addClass("layui-btn-disabled");

    let url = (id) ? "/api/student/update" : "/api/student/add";
    let method = (id) ? "PUT" : "POST";

    $.ajax({
        url: url,
        method: method,
        data: formData
    }).done(function(result) {
        // 【修正3】新增/修改成功判断 code === 0
        if (result.code === 0) {
            layer.msg("操作成功", {icon: 1});
            if(layerIndex) layer.close(layerIndex);
            search(); // 刷新表格
        } else {
            layer.msg("操作失败: " + result.msg, {icon: 2});
        }
    }).fail(function(xhr) {
        layer.msg("请求出错: " + xhr.status);
    }).always(function() {
        // 恢复按钮状态
        $("#btnOK").prop("disabled", false).removeClass("layui-btn-disabled");
    });
}

function deleteById(id){
    layer.confirm('真的要删除该学生吗？', {icon: 3}, function(index){
        $.ajax({
            url: "/api/student/delete/" + id,
            method: "DELETE"
        }).done(function(result){
            // 【修正4】删除成功判断 code === 0
            if(result.code === 0){
                layer.msg("删除成功");
                search();
            } else {
                layer.msg("删除失败");
            }
        });
        layer.close(index);
    });
}
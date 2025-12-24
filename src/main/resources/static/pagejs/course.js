let layerIndex;
let $;
let table;

// 弹出对话框
function showCourseDlg(id){
    let title = "新增课程";

    // 重置
    $("#courseForm")[0].reset();
    $("#id").val("");
    $("#btnOK").prop("disabled", false).removeClass("layui-btn-disabled");

    if(id){
        title = "编辑课程";
        $.ajax({
            url: "/api/course/" + id,
            method: "GET"
        }).done(function(result) {
            if (result.code === 0 && result.data) {
                // 自动填充
                layui.form.val('courseFormFilter', result.data);
            }
        });
    }

    layerIndex = layer.open({
        type: 1,
        title: title,
        area: ['500px','auto'],
        content: $('#courseForm')
    });
}

function getSearchCondition() {
    let formData = {};
    $('#queryForm').find('input').each(function () {
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

    // (1) 提交表单
    layui.form.on('submit(course-dlg)', function (data) {
        commitCourseDlg();
        return false;
    });

    // (2) 表格初始化
    let searchData = getSearchCondition();
    table.render({
        elem: '#tbCourse',
        url: '/api/course/getbypage',
        method: 'POST',
        contentType: 'application/json',
        where: {"data": searchData},
        page: true,
        autoSort: false,
        cols: [[
            {type: 'checkbox', fixed: 'left'},
            {field: 'id', fixed: 'left', width: 80, title: 'ID', sort: true},
            {field: 'name', title: '课程名称', sort: true},
            {field: 'teacher', title: '任课老师', width: 150, sort: true},
            {field: 'credit', title: '学分', width: 100, sort: true},
            {fixed: 'right', title: '操作', width: 150, templet: '#editTemplate'}
        ]]
    });

    // (3) 排序监听
    table.on('sort(tbCourse)', function(obj){
        table.reload('tbCourse', {
            initSort: obj,
            where: {
                data: getSearchCondition(),
                sortField: obj.field,
                sortOrder: obj.type
            }
        });
    });

    // (4) 工具条
    table.on('tool(tbCourse)', function(obj) {
        var data = obj.data;
        if (obj.event === 'edit') {
            showCourseDlg(data.id);
        } else if (obj.event === 'del') {
            deleteById(data.id);
        }
    });
});

function search() {
    table.reloadData("tbCourse", {
        where: {
            data: getSearchCondition(),
            sortField: null,
            sortOrder: null
        },
        page: { curr: 1 }
    });
}

function commitCourseDlg(){
    let id = $("#id").val();
    let formData = $("#courseForm").serialize();

    $("#btnOK").prop("disabled", true).addClass("layui-btn-disabled");

    let url = (id) ? "/api/course/update" : "/api/course/add";
    let method = (id) ? "PUT" : "POST";

    $.ajax({
        url: url,
        method: method,
        data: formData
    }).done(function(result) {
        if (result.code === 0) {
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

function deleteById(id){
    layer.confirm('确定要删除该课程吗？', {icon: 3}, function(index){
        $.ajax({
            url: "/api/course/delete/" + id,
            method: "DELETE"
        }).done(function(result){
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

function deleteConfirm() {
    var checkStatus = table.checkStatus('tbCourse');
    var data = checkStatus.data;

    if (data.length === 0) {
        layer.msg('请先选择要删除的数据');
        return;
    }

    layer.confirm('确定要删除选中的 ' + data.length + ' 条课程吗？', {icon: 3}, function(index){
        let ids = data.map(item => item.id);

        $.ajax({
            url: "/api/course/deletebatch",
            method: "DELETE",
            contentType: "application/json",
            data: JSON.stringify(ids)
        }).done(function(result) {
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
layui.use(['table', 'jquery', 'layer'], function () {
    let $ = layui.$;
    let table = layui.table;
    let layer = layui.layer;

    // 1. 渲染表格
    table.render({
        elem: '#tbMine',
        url: '/api/score/mine', // 学生查成绩专用接口
        method: 'GET',
        // 这里的 parseData 用于拦截数据做统计
        parseData: function(res){
            if(res.code === 0 && res.data){
                calculateSummary(res.data); // 调用统计函数
            }
            return {
                "code": res.code,
                "msg": res.msg,
                "count": res.count,
                "data": res.data
            };
        },
        page: false, // 个人成绩通常不多，直接展示所有，不需要分页
        cols: [[
            {type: 'numbers', title: '序号', width: 60},

            // 课程信息
            {field: 'courseName', title: '课程名称', templet: function(d){
                    return (d.course && d.course.name) ? d.course.name : '未知课程';
                }},

            // 成绩显示 (不及格标红)
            {field: 'score', title: '成绩', sort: true, templet: function(d){
                    let s = d.score;
                    if(s < 60) return '<span class="fail-red" style="font-weight:bold">' + s + '</span>';
                    if(s >= 90) return '<span class="pass-green" style="font-weight:bold">' + s + '</span>';
                    return s;
                }},

            // 绩点计算 (模拟算法：(分数-50)/10)
            {title: '绩点', width: 100, templet: function(d){
                    if(d.score < 60) return '0.0';
                    let gpa = (d.score - 50) / 10;
                    return gpa.toFixed(1);
                }},

            // 等级
            {title: '等级', width: 100, templet: function(d){
                    if(d.score >= 90) return '优';
                    if(d.score >= 80) return '良';
                    if(d.score >= 60) return '中';
                    return '<span class="fail-red">差</span>';
                }}
        ]],
        done: function(res, curr, count){
            // 表格加载完成后的回调
            if(count === 0){
                layer.msg("暂无成绩数据");
            }
        }
    });

    // 2. 计算统计数据 (升级版：支持 GPA 计算)
    function calculateSummary(data) {
        if (!data || data.length === 0) return;

        let totalScore = 0;
        let maxScore = 0;
        let failCount = 0;

        // GPA 相关变量
        let totalPointXCredit = 0; // 绩点 * 学分 的总和
        let totalCredit = 0;       // 总学分

        data.forEach(item => {
            let s = item.score;

            // 1. 基础统计
            totalScore += s;
            if (s > maxScore) maxScore = s;
            if (s < 60) failCount++;

            // 2. GPA 计算
            // 获取学分 (如果课程信息缺失或未设置学分，默认按 1 学分计算，防止除以0)
            let credit = (item.course && item.course.credit) ? item.course.credit : 1;

            // 计算单科绩点 (算法：(分数-50)/10，不及格为0)
            let point = 0;
            if (s >= 60) {
                point = (s - 50) / 10;
            }

            totalPointXCredit += (point * credit);
            totalCredit += credit;
        });

        // 计算平均值
        let avg = (totalScore / data.length).toFixed(1);

        // 计算平均绩点 (保留2位小数)
        let avgGpa = (totalCredit > 0) ? (totalPointXCredit / totalCredit).toFixed(2) : "0.00";

        // 更新界面
        $('#total-course').text(data.length);
        $('#avg-score').text(avg);
        $('#avg-gpa').text(avgGpa); // 更新绩点
        $('#max-score').text(maxScore);

        let failObj = $('#fail-count');
        failObj.text(failCount);
        // 挂科标红，没挂科恢复默认色
        if(failCount > 0) {
            failObj.addClass('fail-red');
        } else {
            failObj.removeClass('fail-red');
        }
    }
});
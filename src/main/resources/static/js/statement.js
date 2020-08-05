layui.use('table', function () {
    var table = layui.table;

    table.render({
        elem: '#st',
        url: '/dev/list',
        page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
            groups: 1,//只显示 1 个连续页码
            first: true,//不显示首页
            last: true //不显示尾页
        },
        cols: [[
            {field: 'id', title: 'ID'},
            {field: 'callPhone', title: '电话号码'},
            {field: 'satisResults', title: '满意度'},
            {field: 'createTime', title: '创建时间'}
        ]]

    });
});
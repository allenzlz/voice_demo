layui.use('table', function () {
    var table = layui.table;

    table.render({
        elem: '#st',
        url: '/dev/list',
        height: 'full-15',
        cellMinWidth: 80,
        page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
            groups: 1,//只显示 1 个连续页码
            first: false,//不显示首页
            last: false //不显示尾页
        },
        cols: [[
            {field: 'id', title: 'ID'},
            {field: 'callPhone', title: '呼叫号码'},
            {
                field: 'satisResults', title: '满意度结果', templet: function (data) {
                    var d = data.satisResults;
                    if (d == 1) {
                        return '非常满意';
                    } else if (d == 2) {
                        return '满意';
                    } else if (d == 3) {
                        return '基本满意';
                    } else if (d == 4) {
                        return '不满意';
                    } else {
                        return '其他';
                    }
                }
            },
            {field: 'createTime', title: '创建时间'}
        ]]

    });
});
layui.use('table', function () {
    var table = layui.table;
    table.render({
        elem: '#st2',
        url: '/dev/tjList',
        height: 'full-15',
        cellMinWidth: 80,
        align: 'center',
        page: false,
        limit: Number.MAX_VALUE,
        cols: [[
            {field: 'statKey', title: '日期'},
            {
                field: 'my1', title: '非常满意', templet: function (data) {
                    var d = data.my1;
                    if (d == null) {
                        return 0;
                    } else {
                        return d;
                    }
                }
            },
            {
                field: 'my2', title: '满意', templet: function (data) {
                    var d = data.my2;
                    if (d == null) {
                        return 0;
                    } else {
                        return d;
                    }
                }
            },
            {
                field: 'my3', title: '基本满意', templet: function (data) {
                    var d = data.my3;
                    if (d == null) {
                        return 0;
                    } else {
                        return d;
                    }
                }
            },
            {
                field: 'my4', title: '不满意', templet: function (data) {
                    var d = data.my4;
                    if (d == null) {
                        return 0;
                    } else {
                        return d;
                    }
                }
            },
            {
                field: 'my5', title: '未调查完成', templet: function (data) {
                    var d = data.my5;
                    if (d == null) {
                        return 0;
                    } else {
                        return d;
                    }
                }
            },
            {field: 'count', title: '总数'},
            {field: 'fcount', title: '完成调查总数'},
            {field: 'dczb', title: '调查占比'},
            {field: 'mydzb', title: '满意度占比'}
        ]]
    });
});
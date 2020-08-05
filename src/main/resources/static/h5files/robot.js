String.prototype.disPx = function () {
    if (this.indexOf('px', 0) > 0) {
        return parseInt(this.replace(/px/g, ''));
    }
    return parseInt(this);
}
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "H+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function UUID() {
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
}

var datetime = '';
var lastDatetime = '';
var $ibot = {
    userId: UUID(),
    ZRG:false,
    platform: 'web',
    welcomeMsg: '你好，中国银行智能客服欢迎您',
    urlPattern1: [/\[link\s+url=[\'\"]+([^\[\]\'\"]+)[\'\"]+\s*[^\[\]]*\]([^\[\]]+)\[\/link\]/gi, '<a href="$1" target="_blank">$2</a>'],
    urlPattern2: [/\[link\s+url=([^\s\[\]\'\"]+)\s*[^\[\]]*\]([^\[\]]+)\[\/link\]/gi, '<a href="$1" target="_blank">$2</a>'],
    submitPattern1: [/\[link\s+submit=[\'\"]+([^\[\]\'\"]+)[\'\"]+\s*[^\[\]]*\]([^\[\]]+)\[\/link\]/gi, '<a id="submitLink" href="#" rel="$1" onclick="getRel(this.rel);return false;" >$2</a>'],
    submitPattern2: [/\[link\s+submit=([^\s\[\]\'\"]+)\s*[^\[\]]*\]([^\[\]]+)\[\/link\]/gi, '<a id="submitLink" href="#" rel="$1" onclick="getRel(this.rel);return false;" >$2</a>'],
    defalutPattern: [/\[link\](.*?)\[\/link\]/gi, '<a id="submitLink" href="#"  rel="$1" onclick="getRel(this.rel);return false;" >$1</a>'],
    // 外部调用run方法即可启动机器人
    run: function (platform, welcomeMsg, _cfg) {
        this.platform = platform;
        this.welcomeMsg = welcomeMsg;
        this.init(_cfg);
    },
    els: {
        'sendBtn': null,
        'outputDiv': null,
        'inputBox': null
    },
    cfg: {
        'robotAskUrl': 'ask.action?t=' + new Date().getTime()
    },
    init: function (_cfg) {
        this.els.sendBtn = $('#sendBtn');
        this.els.outputDiv = $('#h5-robot-outputDiv');
        var outputHeight = this.els.outputDiv.height() - 70;
        this.els.outputDiv.css('height', outputHeight + 'px');
        this.els.inputBox = $('#inputBox');
        for (var item in _cfg) {
            this.cfg[item] = _cfg[item];
        }
        var _this = this;
        this.els.sendBtn.click(function () {
            _this.sendMsg();
            //新增点击完发送让输入框获取焦点
            _this.els.inputBox.focus();
        });
        this.threadDate();
        //展示欢迎语
        _this.showMsg(_this.welcomeMsg, 'left');
    },
    threadDate: function () {
        // 每隔一分钟更新一下日期,如果下一次显示问日期时间有变化则显示
        datetime = new Date().Format('HH:mm');
        window.setInterval(function () {
            datetime = new Date().Format('HH:mm');
        }, 1000 * 60);
    },
    getObjById: function (eleId) {
        var divObj = $('#' + eleId);
        if (divObj && divObj.attr('id')) {
            return divObj;
        }
        return false;
    },
    promptMsg:function(msg){
        var dateDiv = $('<div class="onlineDiv radius-box"></div>');
        this.els.outputDiv.append(dateDiv);
        dateDiv.html(msg);
    },
    showMsg: function (msg, pos) {
        msg = $.trim(msg);
        //自定义标签转换
        msg = replaceLink(msg);
        var div = '';
        if (!pos && pos != 'left' && pos != 'right') {
            alert(msg);
            return;
        }

        if (pos == 'left') {
            if(msg.indexOf('#')>=0){
                var msgArr=msg.split("#");
                msg=msgArr[1];
                div = $('<div class="robot-head"><img class="robot-head-img"' +
                    ' src="h5files/images/admin.png"></div><div class="small-robot-show-div small-robot-left-div"></div><div class="line-x"></div>');
            }else{
                div = $('<div class="robot-head"><img class="robot-head-img" src="h5files/images/robot-head.png"></div><div class="small-robot-show-div small-robot-left-div"></div><div class="line-x"></div>');
            }

        } else if (pos == 'right') {
            if (datetime != lastDatetime) {
                var dateStr = new Date().Format('HH:mm');
                lastDatetime = dateStr;
                var dateDiv = $('<div class="dateDiv radius-box"></div>');
                this.els.outputDiv.append(dateDiv);
                dateDiv.html(dateStr);
            }
            div = $('<div class="user-head"><img class="user-head-img" src="h5files/images/user-head.png"></div><div class="small-robot-show-div small-robot-right-div"></div><div class="line-x"></div>');

            // 增加xss过滤
            msg = xssFilter(msg);
        }
        div.siblings('.small-robot-show-div').html(msg);
        //div.attr('title', new Date().Format('yyyy-MM-dd HH:mm:ss'));
        this.els.outputDiv.append(div);

        //加上淡入淡出的效果
        div.hide();
        div.fadeIn('slow');

        var htmlDiv = $(this.els.outputDiv)[0];
        htmlDiv.scrollTop = htmlDiv.scrollHeight + 30;

    },
    sendMsg: function (str) {
        var msg = '';
        if (str && str != '') {
            msg = str;
        } else {
            msg = this.els.inputBox.val();
        }
        msg = $.trim(msg);
        if (msg && msg != '') {
            this.showMsg(msg, 'right');
            this.els.inputBox.val('');
            if(this.ZRG){
                websocket.send(msg+"&admin");
            }else{
                this.postMsg(msg);
            }

        }
    },
    sendCommand: function () {

    },
    clearAll: function () {
        this.els.inputBox.val('');
        this.els.outputDiv.html('');
    },
    postMsg: function (msg) {
        var userId = this.userId;
        var platform = this.platform;
        var question = msg;
        var jsonpUrl = this.cfg.robotAskUrl;
        var askUrl = this.cfg.robotAskUrl;
        if (jsonpUrl.indexOf("?", 0) < 0) {
            jsonpUrl += '?_ttt=1'
        }
        jsonpUrl += '&userId=' + userId;
        jsonpUrl += '&platform=' + platform;
        jsonpUrl += '&question=' + encodeURI(question);
        jsonpUrl += '&format=json';
        var _this = this;
        $.ajax({
            type: "get",
            async: false,
            jsonp:'jsonp',
            url: jsonpUrl,
            success: function (data) {
                console.log(data.content);
                var html = data.content;
                if(html=='ZRG'){
                    openWebSocket();
                    _this.promptMsg('接入人工系统成功');
                    _this.ZRG=true;
                }else{
                    _this.showMsg(html, 'left');
                }

            },
            error: function (data) {
                _this.showMsg('与后端通讯失败，请重试', 'left');
            }
        });
    }
}


//增加可以接收用户传入userId值
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return unescape(r[2]);
    return null; //返回参数值
}

//增加把link submit标签的submit后面的内容放到a标签的rel字段中，用于提交
function getRel(msg) {
    $ibot.postMsg(msg);
}

//增加 xss过滤
function xssFilter(msg) {
    msg = msg.replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return msg;
}

function replaceLink(html) {
    var urlPattern1 = $ibot.urlPattern1;
    var urlPattern2 = $ibot.urlPattern2;
    var submitPattern1 = $ibot.submitPattern1;
    var submitPattern2 = $ibot.submitPattern2;
    var defalutPattern = $ibot.defalutPattern;

    html = html.replace(urlPattern1[0], urlPattern1[1]);
    html = html.replace(urlPattern2[0], urlPattern2[1]);
    html = html.replace(submitPattern1[0], submitPattern1[1]);
    html = html.replace(submitPattern2[0], submitPattern2[1]);
    html = html.replace(defalutPattern[0], defalutPattern[1]);

    html = html.replace(/[\n]/gi, "<br/>");

    return html;
}
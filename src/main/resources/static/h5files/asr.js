function beforeUpload() {
    var voice = $('input:radio:checked').val();
    if (voice == undefined) {
        alert("请选择语音格式");
        return;
    }
    var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
    if (ie) {
        document.getElementById("fileUpload").click();
    } else {
        var a = document.createEvent("MouseEvents");
        a.initEvent("click", true, true);
        document.getElementById("fileUpload").dispatchEvent(a);
    }
}


$(function () {
    $("#fileUpload").change(function () {
        var files = $("#fileUpload")[0].files[0];
        console.log(files)
        if (files == "") {
            alert("请上传图片");
            return false;
        }
        if (!/\.(wav|mp3|WAV|MP3)$/.test($("#fileUpload").val())) {
            alert("支持格式为.wav、.mp3");
            $("#fileUpload").val('');
            return false;
        }
        if (files.size > 5 * 1024 * 1024) {
            alert("文件不能大于5M");
            $("#fileUpload").val('')
            return false;
        }
        var formData = new FormData();
        formData.append("file", files);
        var voice = $('input:radio:checked').val();
        formData.append("fileType", voice);
        $.portal.ajax({
            url: '/api/asr',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            isShowLoader: true
        }, function (resp) {
            console.log(resp);
            if (resp.code == 0 && resp.data.result == 'success') {
                console.log(resp.data.info)
                $('#asr-result').val(resp.data.info);
            }
        });

        $("#fileUpload").val('');
    });

});

<!-- 【3】实现录音逻辑 -->
var rec, wave, recBlob;
/**调用open打开录音请求好录音权限**/
var recOpen = function () {//一般在显示出录音按钮或相关的录音界面时进行此方法调用，后面用户点击开始录音时就能畅通无阻了
    console.log("当前浏览器是否支持录音：" + Recorder.Support())
    if (!Recorder.Support()) {
        alert('当前浏览器不支持录音功能！', {icon: 5});
        return;
    }
    rec = null;
    wave = null;
    recBlob = null;
    var newRec = Recorder({
        type: "wav", sampleRate: 16000, bitRate: 16 //mp3格式，指定采样率hz、比特率kbps，其他参数使用默认配置；注意：是数字的参数必须提供数字，不要用字符串；需要使用的type类型，需提前把格式支持文件加载进来，比如使用wav格式需要提前加载wav.js编码引擎
        , onProcess: function (buffers, powerLevel, bufferDuration, bufferSampleRate, newBufferIdx, asyncEnd) {
            //录音实时回调，大约1秒调用12次本回调
            // document.querySelector(".recpowerx").style.width = powerLevel + "%";
            // document.querySelector(".recpowert").innerText = bufferDuration + " / " + powerLevel;
            //可视化图形绘制
            // wave.input(buffers[buffers.length - 1], powerLevel, bufferSampleRate);
            // console.log(buffers)
        }
    });

    createDelayDialog(); //我们可以选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调，此处demo省略了弹窗的代码
    newRec.open(function () {//打开麦克风授权获得相关资源
        dialogCancel(); //如果开启了弹框，此处需要取消
        rec = newRec;
        //此处创建这些音频可视化图形绘制浏览器支持妥妥的
        // wave = Recorder.FrequencyHistogramView({elem: ".recwave"});
        console.log("已打开录音，可以点击录制开始录音了");
        recStart();
    }, function (msg, isUserNotAllow) {//用户拒绝未授权或不支持
        dialogCancel(); //如果开启了弹框，此处需要取消
        console.log(isUserNotAllow);
        if (isUserNotAllow) {
            alert('打开录音失败,请打开录音权限', {icon: 5});
        } else {
            alert('打开录音失败,需要HTTPS才能支持', {icon: 5});
        }
        console.log((isUserNotAllow ? "UserNotAllow，" : "") + "打开录音失败：" + msg);

    });

    window.waitDialogClick = function () {
        dialogCancel();
        console.log("打开失败：权限请求被忽略，<span style='color:#f00'>用户主动点击的弹窗</span>", 1);
    };
};


/**关闭录音，释放资源**/
function recClose() {
    if (rec) {
        rec.close();
        console.log("已关闭");
    } else {
        console.log("未打开录音", 1);
    }
    ;
};

var recorderTask = null;
var recorderTime = 60;

/**开始录音**/
function recStart() {//打开了录音后才能进行start、stop调用
    if (rec && Recorder.IsOpen()) {
        recBlob = null;
        rec.start();
        console.log("已开始录音...");
        var temp = 0;
        recorderTask = setInterval(function () {
            // console.log(temp);
            if (temp > recorderTime - 1) {
                recStop();
                clearInterval(recorderTask);
            } else {
                temp += 1;
                if (temp < 10) {
                    $('.countdown-text').html("0:0" + temp);
                } else {
                    $('.countdown-text').html("0:" + temp);
                }
            }
        }, 1000)
    } else {
        console.log("未打开录音", 1);
    }
    ;
};

/**暂停录音**/
function recPause() {
    if (rec && Recorder.IsOpen()) {
        rec.pause();
    } else {
        console.log("未打开录音", 1);
    }
    ;
};

/**恢复录音**/
function recResume() {
    if (rec && Recorder.IsOpen()) {
        rec.resume();
    } else {
        console.log("未打开录音", 1);
    }
    ;
};

/**结束录音，得到音频文件**/
function recStop() {
    if (!(rec && Recorder.IsOpen())) {
        console.log("未打开录音", 1);
        return;
    }
    $('.start-record').show();
    $('.recording').hide();

    rec.stop(function (blob, duration) {
        console.log(blob, (window.URL || webkitURL).createObjectURL(blob), "时长:" + duration + "ms");
        recBlob = blob;
        console.log("已录制wav：" + duration + "ms " + blob.size + "字节，可以点击播放、上传了", 2);
        recUpload();
    }, function (msg) {
        console.log("录音失败:" + msg, 1);
        recClose();
        LayerHide();
    });
};


/**播放**/
function recPlay() {
    if (!recBlob) {
        console.log("请先录音，然后停止后再播放", 1);
        return;
    }
    ;
    var cls = ("a" + Math.random()).replace(".", "");
    console.log('播放中: <span class="' + cls + '"></span>');
    var audio = document.createElement("audio");
    audio.controls = true;
    document.querySelector("." + cls).appendChild(audio);
    //简单利用URL生成播放地址，注意不用了时需要revokeObjectURL，否则霸占内存
    audio.src = (window.URL || webkitURL).createObjectURL(recBlob);
    audio.play();

    setTimeout(function () {
        (window.URL || webkitURL).revokeObjectURL(audio.src);
    }, 5000);
};

/**上传**/
function recUpload() {
    var blob = recBlob;
    if (!blob) {
        console.log("请先录音，然后停止后再上传", 1);
        return;
    }

    var formData = new FormData();
    formData.append("file", blob);
    formData.append("userid", $ibot.userId);
    $.ajax({
        url: 'getAsr',
        type: 'POST',
        data: formData,
        contentType: false,
        processData: false,
        isShowLoader: true,
        success: function (resp) {
            console.log(resp);
            if (resp.result && resp.result == 'success') {
                LayerHide();
                $ibot.showMsg(resp.question, "right");
                $ibot.showMsg(resp.answer, "left");
                document.querySelector('.audio-node').src = resp.ttsUrl + encodeURIComponent(resp.answer);
            }
        }
    });
    return;
    //本例子假设使用原始XMLHttpRequest请求方式，实际使用中自行调整为自己的请求方式
    //录音结束时拿到了blob文件对象，可以用FileReader读取出内容，或者用FormData上传
    var api = "http://localhost:8000/api/asr";
    var onreadystatechange = function (title) {
        return function () {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    console.log(title + "上传成功", 2);
                } else {
                    console.log(title + "没有完成上传，演示上传地址无需关注上传结果，只要浏览器控制台内Network面板内看到的请求数据结构是预期的就ok了。", "#d8c1a0");
                    console.error(title + "上传失败", xhr.status, xhr.responseText);
                }
                ;
            }
            ;
        };
    };
    console.log("开始上传到" + api + "，请求稍后...");

    /***方式二：使用FormData用multipart/form-data表单上传文件***/
    var form = new FormData();
    form.append("file", blob, "recorder.mp3"); //和普通form表单并无二致，后端接收到upfile参数的文件，文件名为recorder.mp3
    form.append("fileType", "8k-cn");
    //...其他表单参数
    var xhr = new XMLHttpRequest();
    xhr.open("POST", api);
    xhr.onreadystatechange = onreadystatechange("上传方式二【FormData】");
    xhr.send(form);
};


//recOpen我们可以选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调
var showDialog = function () {
    if (!/mobile/i.test(navigator.userAgent)) {
        return;//只在移动端开启没有权限请求的检测
    }
    ;
    dialogCancel();

    //显示弹框，应该使用自己的弹框方式
    var div = document.createElement("div");
    document.body.appendChild(div);
    div.innerHTML = (''
        + '<div class="waitDialog" style="z-index:99999;width:100%;height:100%;top:0;left:0;position:fixed;background:rgba(0,0,0,0.3);">'
        + '<div style="display:flex;height:100%;align-items:center;">'
        + '<div style="flex:1;"></div>'
        + '<div style="width:240px;background:#fff;padding:15px 20px;border-radius: 10px;">'
        + '<div style="padding-bottom:10px;">录音功能需要麦克风权限，请允许；如果未看到任何请求，请点击忽略~</div>'
        + '<div style="text-align:center;"><a onclick="waitDialogClick()" style="color:#0B1">忽略</a></div>'
        + '</div>'
        + '<div style="flex:1;"></div>'
        + '</div>'
        + '</div>');
};
var createDelayDialog = function () {
    dialogInt = setTimeout(function () {//定时8秒后打开弹窗，用于监测浏览器没有发起权限请求的情况，在open前放置定时器利于收到了回调能及时取消（不管open是同步还是异步回调的）
        showDialog();
    }, 8000);
};
var dialogInt;
var dialogCancel = function () {
    clearTimeout(dialogInt);

    //关闭弹框，应该使用自己的弹框方式
    var elems = document.querySelectorAll(".waitDialog");
    for (var i = 0; i < elems.length; i++) {
        elems[i].parentNode.removeChild(elems[i]);
    }
    ;
};
//recOpen弹框End

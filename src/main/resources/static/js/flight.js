/**
 * 航班管理
 */
require([
    'jquery',
    'axios',
    'module/functions',
    'bootstrap'], function ($, axios, functions, _) {

    const TEMPLATE_LIST = "flight_list";

    let $list = $('#plane-list');

    let processData = (data) => {
        for (const flight of data) {
            flight.flightTimeHour = (flight.flightTime / (60 * 60)).toFixed(1);
        }
        return data;
    };

    // 渲染全部区块内容
    let render = () => {
        axios.get("/flight/")
            .then((response) => {
                let data = response.data;
                let flights = processData(data.data);
                functions.renderHbs($list, TEMPLATE_LIST, {
                    flights: flights
                });
            })
            .catch((e) => {
                console.error("获取航班数据失败：", from, date, e);
                functions.modal("错误", "无法获取航班数据，请检查网络连接！");
            });
    };

    // 获取区块内容
    render();

    // 事件绑定
    let $filePath = $('#filePath');

    $('#btn-block-add').click(() => {
        $('#addModal').modal('show');
    });

    $('#block-add').click(() => {
        let flightNo = $('#flightNo').val(),
            airlineName = $('#airlineName').val(),
            departureTime = $('#departureTime').val(),
            landingTime = $('#landingTime').val(),
            from = $('#from').val(),
            to = $('#to').val(),
            middle = $('#middle').val(),
            middleTime = $('#middleTime').val(),
            totalCabin = $('#totalCabin').val(),
            restCabin = $('#restCabin').val(),
            ticketPrice = $('#ticketPrice').val();
        // TODO: 补充11条非空判断
        axios.post('/flight/', JSON.stringify({
            flightNo: flightNo,
            airlineName: airlineName,
            departureTime: departureTime,
            landingTime: landingTime,
            from: from,
            to: to,
            middle: middle,
            middleTime: middleTime,
            totalCabin: totalCabin,
            restCabin: restCabin,
            ticketPrice: ticketPrice
        }), {
            headers: {'Content-Type': 'application/json'}
        })
            .then((response) => {
                let data = response.data;
                if (data.status !== 200) {
                    console.error("增加航班错误：", flightNo, data);
                    functions.modal("错误", data.message);
                    return null;
                }
                functions.modal("信息", "添加航班成功！新航班ID为：" + data.data.id);
                render();
            });
    });

    $('#btn-batch-add').click(() => {
        $('#batchModal').modal('show');
    });

    $('#batch-add').click(() => {
        let path = $filePath.val();
        if (path.length <= 0) {
            functions.modal("错误", "路径不能为空！");
            return;
        }
        $filePath.val("");
        axios.get(`/flight/import/?filepath=${path}`)
            .then((response) => {
                let data = response.data;
                if (data.status !== 200) {
                    console.error("批量增加航班错误：", path, data);
                    functions.modal("错误", data.message);
                    return null;
                }
                functions.modal("信息", "添加成功！");
                render();
            });
    });

    // 搜索侦听器
    $('[type=text]').keydown(function (e) {
        let curKey = e.which;
        if (curKey === 13) { // 回车
            let flightNo = $(this).val();
            if (flightNo.length <= 0) {
                functions.modal("提示", "航班号不能为空！");
                return false;
            }
            axios.get(`/flight/${flightNo}/`)
                .then((response) => {
                    let data = response.data;
                    functions.jumpTo(`/detail.html?id=${data.data.id}`, 0);
                })
                .catch((e) => {
                    console.error("获取航班数据失败：", start, end, sort, e);
                    functions.modal("错误", "无法获取航班数据，请检查网络连接！");
                });
        }
    });
});
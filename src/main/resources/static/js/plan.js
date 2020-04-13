/**
 * 搜索
 */
require([
        'jquery',
        'axios',
        'module/functions',
        'module/constants',
        'bootstrap'],
    function ($, axios, functions, constants, _) {

        const TEMPLATE_LIST = "plan_list";

        let processData = (data) => {
            for (const plan of data) {
                plan.flightCount = plan.flights.length;
                plan.totalTimeHour = (plan.totalTime / (60 * 60)).toFixed(1);
                for (const flight of plan.flights) {
                    flight.flightTimeHour = (flight.flightTime / (60 * 60)).toFixed(1);
                }
            }
            return data;
        };

        let $list = $('#plan-list'),
            $from = $('#from'),
            $to = $('#to'),
            $date = $('#date');

        // 搜索侦听器
        $('[type=text]').keydown(function (e) {
            let curKey = e.which;
            if (curKey === 13) { // 回车
                let from = $from.val(),
                    to = $to.val(),
                    date = $date.val();
                if (from.length <= 0) {
                    functions.modal("提示", "出发城市不能为空！");
                    return false;
                }
                if (to.length <= 0) {
                    functions.modal("提示", "目的城市不能为空！");
                    return false;
                }
                if (date.length <= 0) {
                    functions.modal("提示", "旅行日期不能为空！");
                    return false;
                }
                $('h1').text('正在规划...');
                axios.get("/plan/", {
                    params: {
                        from: from,
                        to: to,
                        date: date
                    }
                })
                    .then((response) => {
                        $('h1').text(`规划结果：${from} → ${to}`);
                        let data = response.data;
                        let plans = processData(data.data);
                        functions.renderHbs($list, TEMPLATE_LIST, {
                            plans: plans
                        });
                    })
                    .catch((e) => {
                        console.error("获取规划数据失败：", keyword, e);
                        functions.modal("错误", "无法获取规划数据，请检查网络连接！");
                    });
            }
        });
    });
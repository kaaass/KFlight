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

        const TEMPLATE_LIST = "flight_list";

        let processData = (data) => {
            for (const flight of data) {
                flight.flightTimeHour = (flight.flightTime / (60 * 60)).toFixed(1);
            }
            return data;
        };

        let $list = $('#plane-list'),
            $to = $('#to'),
            $date = $('#date'),
            $sort = $('#sort');

        // 搜索侦听器
        $('[type=text]').keydown(function (e) {
            let curKey = e.which;
            if (curKey === 13) { // 回车
                let to = $to.val(),
                    date = $date.val(),
                    sort = $sort.val();
                if (to.length <= 0) {
                    functions.modal("提示", "目的城市不能为空！");
                    return false;
                }
                if (date.length <= 0) {
                    functions.modal("提示", "旅行日期不能为空！");
                    return false;
                }
                if (sort.length <= 0) {
                    functions.modal("提示", "排序方法不能为空！");
                    return false;
                }
                axios.get("/search/to-date/", {
                    params: {
                        to: to,
                        date: date,
                        sort: sort
                    }
                })
                    .then((response) => {
                        let data = response.data;
                        let flights = processData(data.data);
                        functions.renderHbs($list, TEMPLATE_LIST, {
                            flights: flights
                        });
                    })
                    .catch((e) => {
                        console.error("获取航班数据失败：", to, date, sort, e);
                        functions.modal("错误", "无法获取航班数据，请检查网络连接！");
                    });
            }
        });

        $('#about-sort').click(() => {
            functions.modal("提示", "排序方法由一系列字母组成，其中小写为升序、大写为降序，从前至后为主要程度。" +
                "允许的取值如下：f - 起飞地点；t - 降落地点；d - 起飞时间；l - 降落时间；w - 航行时间；p - 当前票价；r - 剩余票数");
        });
    });
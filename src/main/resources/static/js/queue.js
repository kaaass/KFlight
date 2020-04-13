/**
 * 售票队列
 */
require([
        'jquery',
        'axios',
        'module/functions',
        'module/constants',
        'bootstrap'],
    function ($, axios, functions, constants, _) {

        const TEMPLATE_LIST = "queue_list";

        let $list = $('#queue-list');

        axios.get("/ticket/queue/")
            .then((response) => {
                let data = response.data;
                functions.renderHbs($list, TEMPLATE_LIST, {
                    queue: data.data
                });
            })
            .catch((e) => {
                console.error("获取航班数据失败：", from, date, e);
                functions.modal("错误", "无法获取航班数据，请检查网络连接！");
            });
    });
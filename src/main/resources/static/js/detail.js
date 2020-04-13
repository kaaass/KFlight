/**
 * 区块详细信息
 */
require([
        'jquery',
        'axios',
        'module/functions',
        'module/constants',
        'bootstrap'],
    function ($, axios, functions, constants, _) {

        const TEMPLATE_DETAIL = "detail";

        let $detail = $('main');
        let curId, curData;

        // 解析Url
        if (functions.requestParams.has(constants.PARAM_ID)) {
            curId = functions.requestParams.get(constants.PARAM_ID);
        } else {
            functions.modal("错误", "缺少航班id！");
            return;
        }

        // 渲染数据
        let $content = $('#content');

        let bind = () => {
            // 编辑
            $('#edit').click(() => {
                $content.val(JSON.stringify(curData));
                $('#addModal').modal('show');
            });
            $('#block-add').click(() => {
                let content = $content.val();
                if (content.length <= 0) {
                    functions.modal("错误", "航班内容不能为空！");
                    return;
                }
                axios.post(`/flight/id/${curId}/`, content, {
                    headers: {'Content-Type': 'application/json'}
                })
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("编辑航班错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", "编辑航班成功！");
                        render();
                    });
            });
            // 删除
            $('#remove').click(() => {
                axios.delete(`/flight/id/${curId}/`)
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("删除航班错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", "删除航班成功！");
                        render();
                    });
            });
            // 发布延误
            $('#delay').click(() => {
                let delayTo = prompt("请输入延迟后的起飞时间");
                if (delayTo === null || delayTo.length <= 0) {
                    functions.modal("错误", "起飞时间不能为空！");
                    return;
                }
                axios.post(`/flight/id/${curId}/delay/?delayTo=${delayTo}`)
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("发布延误错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", `发布延误成功！已经短信通知旅客推荐就近航班 ${data.data.flightNo}。`);
                        render();
                    });
            });
            // 发布取消
            $('#cancel').click(() => {
                axios.post(`/flight/id/${curId}/cancel/`)
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("发布取消错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", `发布取消成功！已经短信通知旅客推荐就近航班 ${data.data.flightNo}。`);
                        render();
                    });
            });
            // 购票申请
            $('#order').click(() => {
                let phone = prompt("请输入客户电话号码");
                if (phone === undefined || phone.length <= 0) {
                    functions.modal("错误", "电话号码不能为空！");
                    return;
                }
                axios.post(`/ticket/order/id/${curId}/?phone=${phone}`)
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("订票错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", "订票成功！");
                        render();
                    });
            });
            // 退票申请
            $('#withdraw').click(() => {
                let phone = prompt("请输入客户电话号码");
                if (phone === undefined || phone.length <= 0) {
                    functions.modal("错误", "电话号码不能为空！");
                    return;
                }
                axios.post(`/ticket/withdraw/id/${curId}/?phone=${phone}`)
                    .then((response) => {
                        let data = response.data;
                        if (data.status !== 200) {
                            console.error("退票错误：", curId, data);
                            functions.modal("错误", data.message);
                            return null;
                        }
                        functions.modal("信息", "退票成功！");
                        render();
                    });
            });
        };

        let processData = (flight) => {
            flight.flightTimeHour = (flight.flightTime / (60 * 60)).toFixed(1);
            return flight;
        };

        let render = () => {
            axios.get(`/flight/id/${curId}/`)
                .then((response) => {
                    let data = response.data;
                    let flights = processData(data.data);
                    curData = flights;
                    functions.renderHbs($detail, TEMPLATE_DETAIL, flights).then(() => {
                        bind();
                    });
                })
                .catch((e) => {
                    console.error("获取航班数据失败：", curId, e);
                    functions.modal("错误", "无法获取航班数据，请检查网络连接！");
                });
        };

        render();
    });
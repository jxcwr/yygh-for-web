import request from '@/utils/request'

const API = '/api/order/orderInfo'

export default {
  getOrderId(scheduleId, patientId) {
    return request({
      url: `${API}/auth/submitOrder/${scheduleId}/${patientId}`,
      method: 'post'
    })
  },
  //订单列表
  getPageList(page, limit, searchObj) {
    return request({
      url: `${API}/auth/${page}/${limit}`,
      method: `get`,
      params: searchObj
    })
  },

//订单状态
  getStatusList() {
    return request({
      url: `${API}/auth/getStatusList`,
      method: 'get'
    })
  },
  //订单详情
  getOrders(orderId) {
    return request({
      url: `${API}/auth/getOrders/${orderId}`,
      method: `get`
    })
  },

}

import request from '@/utils/request'

const API = '/api/order/weixin'

export default {
  createNative(orderId) {
    return request({
      url: `${API}/createNative/${orderId}`,
      method: 'get'
    })
  },
  queryPayStatus(orderId) {
    return request({
      url: `${API}/queryPayStatus/${orderId}`,
      method: 'get'
    })
  },
}

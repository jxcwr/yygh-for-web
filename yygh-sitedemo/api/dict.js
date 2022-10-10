import request from '@/utils/request'

const API = '/admin/cmn/dict'

export default {
  getChildList(pid) {
    return request({
      url: `${API}/findChildList/${pid}`,
      method: 'get'
    })
  },
  findByDictCode(pid = 20000) {
    return request({
      url: `${API}/findChildList/${pid}`,
      method: 'get'
    })
  }

}

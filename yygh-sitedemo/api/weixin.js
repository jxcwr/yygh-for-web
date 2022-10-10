import request from '@/utils/request'

const api_name = `/user/userinfo/wechat`

export default {
  getWeixinParam() {
    return request({
      url: `${api_name}/login`,
      method: `get`,
    })
  }
}

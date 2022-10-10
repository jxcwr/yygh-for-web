import request from '@/utils/request'


const API = "/admin/hosp/schedule";

export default {

  getScheduleRule(page, limit, hoscode, depcode) {
    return request({
      url: `${API}/getScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
      method: 'get'
    })
  },
  //查询排班详情
  getScheduleDetail(hoscode, depcode, workDate) {
    return request({
      url: `${API}/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
      method: 'get'
    })
  }
}

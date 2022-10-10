import request from '@/utils/request'

const API = '/user/hosp'

export default {
  getHospitalList(searchObj) {
    return request({
      url: `${API}/hospital/list`,
      method: 'get',
      params: searchObj
    })
  },

  findByName(name) {
    return request({
      url: `${API}/hospital/search/${name}`,
      method: 'get'
    })
  },

  show(hoscode) {
    return request({
      url: `${API}/hospital/detail/${hoscode}`,
      method: 'get'
    })
  },
  findDepartment(hoscode) {
    return request({
      url: `${API}/department/findAll/${hoscode}`,
      method: 'get'
    })
  },
  getBookingScheduleRuleDetail(page, limit, hoscode, depcode) {
    return request({
      url: `${API}/schedule/auth/getBookingScheduleRule/${page}/${limit}/${hoscode}/${depcode}`,
      method: 'get'
    })
  },
  findScheduleList(hoscode, depcode, workDate) {
    return request({
      url: `${API}/schedule/auth/getScheduleDetail/${hoscode}/${depcode}/${workDate}`,
      method: 'get'
    })
  },
  getSchedule(id) {
    return request({
      url: `${API}/schedule/getSchedule/${id}`,
      method: 'get'
    })
  }

}

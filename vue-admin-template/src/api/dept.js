import request from '@/utils/request'


const API = "/admin/hosp/department";

export default {
  getDeptByHoscode(id) {
    return request({
      url: `${API}/getDepartmentList/${id}`,
      method: 'get'
    })
  }

}
